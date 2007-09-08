/*******************************************************************************
 * Copyright (c) 2005, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.trac.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.mylyn.internal.trac.core.model.TracAttachment;
import org.eclipse.mylyn.internal.trac.core.model.TracComment;
import org.eclipse.mylyn.internal.trac.core.model.TracComponent;
import org.eclipse.mylyn.internal.trac.core.model.TracMilestone;
import org.eclipse.mylyn.internal.trac.core.model.TracPriority;
import org.eclipse.mylyn.internal.trac.core.model.TracSearch;
import org.eclipse.mylyn.internal.trac.core.model.TracSeverity;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket;
import org.eclipse.mylyn.internal.trac.core.model.TracTicketField;
import org.eclipse.mylyn.internal.trac.core.model.TracTicketResolution;
import org.eclipse.mylyn.internal.trac.core.model.TracTicketStatus;
import org.eclipse.mylyn.internal.trac.core.model.TracTicketType;
import org.eclipse.mylyn.internal.trac.core.model.TracVersion;
import org.eclipse.mylyn.internal.trac.core.model.TracWikiPage;
import org.eclipse.mylyn.internal.trac.core.model.TracWikiPageInfo;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket.Key;
import org.eclipse.mylyn.internal.trac.core.util.TracHttpClientTransportFactory;
import org.eclipse.mylyn.internal.trac.core.util.TracUtils;
import org.eclipse.mylyn.internal.trac.core.util.TracHttpClientTransportFactory.TracHttpException;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.web.core.WebClientUtil;

/**
 * Represents a Trac repository that is accessed through the Trac XmlRpcPlugin.
 * 
 * @author Steffen Pingel
 * @author Xiaoyang Guan
 */
public class TracXmlRpcClient extends AbstractTracClient implements ITracWikiClient {

	public static final String XMLRPC_URL = "/xmlrpc";

	public static final String REQUIRED_REVISION = "1950";

	public static final int REQUIRED_EPOCH = 0;

	public static final int REQUIRED_MAJOR = 0;

	public static final int REQUIRED_MINOR = 1;

	private static final int NO_SUCH_METHOD_ERROR = 1;

	private static final int LATEST_VERSION = -1;

	public static final int REQUIRED_WIKI_RPC_VERSION = 2;

	private XmlRpcClient xmlrpc;

	private TracHttpClientTransportFactory factory;

	private int majorAPIVersion = -1;

	private int minorAPIVersion = -1;

	private int epochAPIVersion = -1;

	private boolean accountMangerAuthenticationFailed;

	public TracXmlRpcClient(URL url, Version version, String username, String password, Proxy proxy) {
		super(url, version, username, password, proxy);
	}

	public synchronized XmlRpcClient getClient() throws TracException {
		if (xmlrpc != null) {
			return xmlrpc;
		}

		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		config.setEncoding(ITracClient.CHARSET);
		config.setBasicUserName(username);
		config.setBasicPassword(password);
		config.setServerURL(getXmlRpcUrl());
		config.setTimeZone(TimeZone.getTimeZone(ITracClient.TIME_ZONE));
		config.setContentLengthOptional(false);
		config.setConnectionTimeout(WebClientUtil.CONNNECT_TIMEOUT);
		config.setReplyTimeout(WebClientUtil.SOCKET_TIMEOUT);

		xmlrpc = new XmlRpcClient();
		xmlrpc.setConfig(config);

		factory = new TracHttpClientTransportFactory(xmlrpc);
		factory.setProxy(proxy);
		xmlrpc.setTransportFactory(factory);

		return xmlrpc;
	}

	private URL getXmlRpcUrl() throws TracException {
		try {
			String location = repositoryUrl.toString();
			if (hasAuthenticationCredentials()) {
				location += LOGIN_URL;
			}
			location += XMLRPC_URL;

			return new URL(location);
		} catch (Exception e) {
			throw new TracException(e);
		}
	}

	private Object call(String method, Object... parameters) throws TracException {
		try {
			// first attempt
			return callInternal(method, parameters);
		} catch (TracPermissionDeniedException e) {
			if (accountMangerAuthenticationFailed) {
				// do not try again if this has failed in the past since it
				// is more likely that XML_RPC permissions have not been set
				throw e;
			}

			// try form-based authentication via AccountManagerPlugin as a
			// fall-back
			HttpClient httpClient = new HttpClient();
			httpClient.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
			WebClientUtil.setupHttpClient(httpClient, proxy, repositoryUrl.toString(), null, null);
			try {
				authenticateAccountManager(httpClient);
			} catch (TracLoginException loginException) {
				// caused by wrong username or password
				throw loginException;
			} catch (IOException ignore) {
				accountMangerAuthenticationFailed = true;
				throw e;
			}

			try {
				validateAuthenticationState(httpClient);
			} catch (TracLoginException ignore) {
				// most likely form based authentication is not supported by
				// repository
				accountMangerAuthenticationFailed = true;
				throw e;
			}

			factory.setCookies(httpClient.getState().getCookies());
		}

		// second attempt
		return callInternal(method, parameters);
	}

	private Object callInternal(String method, Object... parameters) throws TracException {
		getClient();

		try {
			return xmlrpc.execute(method, parameters);
		} catch (TracHttpException e) {
			if (e.code == HttpURLConnection.HTTP_UNAUTHORIZED) {
				throw new TracLoginException();
			} else if (e.code == HttpURLConnection.HTTP_FORBIDDEN) {
				throw new TracPermissionDeniedException();
			} else {
				throw new TracException(e);
			}
		} catch (XmlRpcException e) {
			if (e.code == NO_SUCH_METHOD_ERROR) {
				throw new TracNoSuchMethodException(e);
			} else {
				throw new TracRemoteException(e);
			}
		} catch (Exception e) {
			throw new TracException(e);
		}
	}

	private Object[] multicall(Map<String, Object>... calls) throws TracException {
		Object[] result = (Object[]) call("system.multicall", new Object[] { calls });
		for (Object item : result) {
			try {
				checkForException(item);
			} catch (XmlRpcException e) {
				throw new TracRemoteException(e);
			} catch (Exception e) {
				throw new TracException(e);
			}
		}
		return result;
	}

	private void checkForException(Object result) throws NumberFormatException, XmlRpcException {
		if (result instanceof Map) {
			Map<?, ?> exceptionData = (Map<?, ?>) result;
			if (exceptionData.containsKey("faultCode") && exceptionData.containsKey("faultString")) {
				throw new XmlRpcException(Integer.parseInt(exceptionData.get("faultCode").toString()),
						(String) exceptionData.get("faultString"));
			}
		}
	}

	private Map<String, Object> createMultiCall(String methodName, Object... parameters) throws TracException {
		Map<String, Object> table = new HashMap<String, Object>();
		table.put("methodName", methodName);
		table.put("params", parameters);
		return table;
	}

	private Object getMultiCallResult(Object item) {
		return ((Object[]) item)[0];
	}

	public void validate() throws TracException {
		try {
			Object[] result = (Object[]) call("system.getAPIVersion");
			if (result.length >= 3) {
				epochAPIVersion = (Integer) result[0];
				majorAPIVersion = (Integer) result[1];
				minorAPIVersion = (Integer) result[2];
			} else if (result.length >= 2) {
				epochAPIVersion = 0;
				majorAPIVersion = (Integer) result[0];
				minorAPIVersion = (Integer) result[1];				
			} else {
				throw new TracException("The API version is unsupported, please update your Trac XML-RPC Plugin to revision " + REQUIRED_REVISION
						+ " or later");
			}
		} catch (TracNoSuchMethodException e) {
			throw new TracException(
					"Required API calls are missing, please update your Trac XML-RPC Plugin to revision "
							+ REQUIRED_REVISION + " or later");
		}

		if (!isAPIVersionOrHigher(REQUIRED_EPOCH, REQUIRED_MAJOR, REQUIRED_MINOR)) {
			throw new TracException("The API version " + majorAPIVersion + "." + minorAPIVersion
					+ " is unsupported, please update your Trac XML-RPC Plugin to revision " + REQUIRED_REVISION
					+ " or later");
		}
	}

	private void updateAPIVersion() throws TracException {
		if (epochAPIVersion == -1 || majorAPIVersion == -1 || minorAPIVersion == -1) {
			validate();
		}
	}

	private boolean isAPIVersionOrHigher(int epoch, int major, int minor) throws TracException {
		updateAPIVersion();
		return (epochAPIVersion > epoch || (epochAPIVersion == epoch && majorAPIVersion > major || (majorAPIVersion == major && minorAPIVersion >= minor)));
	}

	public TracTicket getTicket(int id) throws TracException {
		Object[] result = (Object[]) call("ticket.get", id);
		TracTicket ticket = parseTicket(result);

		result = (Object[]) call("ticket.changeLog", id, 0);
		for (Object item : result) {
			ticket.addComment(parseChangeLogEntry((Object[]) item));
		}

		result = (Object[]) call("ticket.listAttachments", id);
		for (Object item : result) {
			ticket.addAttachment(parseAttachment((Object[]) item));
		}

		String[] actions = getActions(id);
		ticket.setActions(actions);

		updateAttributes(new NullProgressMonitor(), false);
		TracTicketResolution[] resolutions = getTicketResolutions();
		if (resolutions != null) {
			String[] resolutionStrings = new String[resolutions.length];
			for (int i = 0; i < resolutions.length; i++) {
				resolutionStrings[i] = resolutions[i].getName();
			}
			ticket.setResolutions(resolutionStrings);
		} else {
			ticket.setResolutions(getDefaultTicketResolutions());
		}

		return ticket;
	}

	private TracAttachment parseAttachment(Object[] entry) {
		TracAttachment attachment = new TracAttachment((String) entry[0]);
		attachment.setDescription((String) entry[1]);
		attachment.setSize((Integer) entry[2]);
		attachment.setCreated(parseDate(entry[3]));
		attachment.setAuthor((String) entry[4]);
		return attachment;
	}

	private TracComment parseChangeLogEntry(Object[] entry) {
		TracComment comment = new TracComment();
		comment.setCreated(parseDate(entry[0]));
		comment.setAuthor((String) entry[1]);
		comment.setField((String) entry[2]);
		comment.setOldValue((String) entry[3]);
		comment.setNewValue((String) entry[4]);
		return comment;
	}

	/* public for testing */
	@SuppressWarnings("unchecked")
	public List<TracTicket> getTickets(int[] ids) throws TracException {
		Map<String, Object>[] calls = new Map[ids.length];
		for (int i = 0; i < calls.length; i++) {
			calls[i] = createMultiCall("ticket.get", ids[i]);
		}

		Object[] result = multicall(calls);
		assert result.length == ids.length;

		List<TracTicket> tickets = new ArrayList<TracTicket>(result.length);
		for (Object item : result) {
			Object[] ticketResult = (Object[]) getMultiCallResult(item);
			tickets.add(parseTicket(ticketResult));
		}

		return tickets;
	}

	@SuppressWarnings("unchecked")
	public void search(TracSearch query, List<TracTicket> tickets) throws TracException {
		// an empty query string is not valid, therefore prepend order
		Object[] result = (Object[]) call("ticket.query", "order=id" + query.toQuery());

		Map<String, Object>[] calls = new Map[result.length];
		for (int i = 0; i < calls.length; i++) {
			calls[i] = createMultiCall("ticket.get", result[i]);
		}
		result = multicall(calls);

		for (Object item : result) {
			Object[] ticketResult = (Object[]) getMultiCallResult(item);
			tickets.add(parseTicket(ticketResult));
		}
	}

	private TracTicket parseTicket(Object[] ticketResult) throws InvalidTicketException {
		TracTicket ticket = new TracTicket((Integer) ticketResult[0]);
		ticket.setCreated(parseDate(ticketResult[1]));
		ticket.setLastChanged(parseDate(ticketResult[2]));
		Map<?, ?> attributes = (Map<?, ?>) ticketResult[3];
		for (Object key : attributes.keySet()) {
			ticket.putValue(key.toString(), attributes.get(key).toString());
		}
		return ticket;
	}

	private Date parseDate(Object object) {
		if (object instanceof Date) {
			return (Date) object;
		} else if (object instanceof Integer) {
			return TracUtils.parseDate((Integer) object);
		}
		throw new ClassCastException("Unexpected object type for date: " + object.getClass());
	}

	@Override
	public synchronized void updateAttributes(IProgressMonitor monitor) throws TracException {
		monitor.beginTask("Updating attributes", 9);

		Object[] result = getAttributes("ticket.component");
		data.components = new ArrayList<TracComponent>(result.length);
		for (Object item : result) {
			data.components.add(parseComponent((Map<?, ?>) getMultiCallResult(item)));
		}
		advance(monitor, 1);

		result = getAttributes("ticket.milestone");
		data.milestones = new ArrayList<TracMilestone>(result.length);
		for (Object item : result) {
			data.milestones.add(parseMilestone((Map<?, ?>) getMultiCallResult(item)));
		}
		advance(monitor, 1);

		List<TicketAttributeResult> attributes = getTicketAttributes("ticket.priority");
		data.priorities = new ArrayList<TracPriority>(result.length);
		for (TicketAttributeResult attribute : attributes) {
			data.priorities.add(new TracPriority(attribute.name, attribute.value));
		}
		Collections.sort(data.priorities);
		advance(monitor, 1);

		attributes = getTicketAttributes("ticket.resolution");
		data.ticketResolutions = new ArrayList<TracTicketResolution>(result.length);
		for (TicketAttributeResult attribute : attributes) {
			data.ticketResolutions.add(new TracTicketResolution(attribute.name, attribute.value));
		}
		Collections.sort(data.ticketResolutions);
		advance(monitor, 1);

		attributes = getTicketAttributes("ticket.severity");
		data.severities = new ArrayList<TracSeverity>(result.length);
		for (TicketAttributeResult attribute : attributes) {
			data.severities.add(new TracSeverity(attribute.name, attribute.value));
		}
		Collections.sort(data.severities);
		advance(monitor, 1);

		boolean trac011 = isAPIVersionOrHigher(1, 0, 0);
		attributes = getTicketAttributes("ticket.status", trac011);
		data.ticketStatus = new ArrayList<TracTicketStatus>(result.length);
		for (TicketAttributeResult attribute : attributes) {
			data.ticketStatus.add(new TracTicketStatus(attribute.name, attribute.value));
		}
		Collections.sort(data.ticketStatus);
		advance(monitor, 1);

		attributes = getTicketAttributes("ticket.type");
		data.ticketTypes = new ArrayList<TracTicketType>(result.length);
		for (TicketAttributeResult attribute : attributes) {
			data.ticketTypes.add(new TracTicketType(attribute.name, attribute.value));
		}
		Collections.sort(data.ticketTypes);
		advance(monitor, 1);

		result = getAttributes("ticket.version");
		data.versions = new ArrayList<TracVersion>(result.length);
		for (Object item : result) {
			data.versions.add(parseVersion((Map<?, ?>) getMultiCallResult(item)));
		}
		advance(monitor, 1);

		result = (Object[]) call("ticket.getTicketFields");
		data.ticketFields = new ArrayList<TracTicketField>(result.length);
		for (Object item : result) {
			data.ticketFields.add(parseTicketField((Map<?, ?>) item));
		}
		advance(monitor, 1);
	}

	private void advance(IProgressMonitor monitor, int worked) {
		monitor.worked(worked);
		if (monitor.isCanceled())
			throw new OperationCanceledException();

	}

	private TracComponent parseComponent(Map<?, ?> result) {
		TracComponent component = new TracComponent((String) result.get("name"));
		component.setOwner((String) result.get("owner"));
		component.setDescription((String) result.get("description"));
		return component;
	}

	private TracMilestone parseMilestone(Map<?, ?> result) {
		TracMilestone milestone = new TracMilestone((String) result.get("name"));
		milestone.setCompleted(parseDate(result.get("completed")));
		milestone.setDue(parseDate(result.get("due")));
		milestone.setDescription((String) result.get("description"));
		return milestone;
	}

	private TracVersion parseVersion(Map<?, ?> result) {
		TracVersion version = new TracVersion((String) result.get("name"));
		version.setTime(parseDate(result.get("time")));
		version.setDescription((String) result.get("description"));
		return version;
	}

	private TracTicketField parseTicketField(Map<?, ?> result) {
		TracTicketField field = new TracTicketField((String) result.get("name"));
		field.setType(TracTicketField.Type.fromString((String) result.get("type")));
		field.setLabel((String) result.get("label"));
		field.setDefaultValue((String) result.get("value"));
		Object[] items = (Object[]) result.get("options");
		if (items != null) {
			String[] options = new String[items.length];
			for (int i = 0; i < items.length; i++) {
				options[i] = (String) items[i];
			}
			field.setOptions(options);
		}
		if (result.get("custom") != null) {
			field.setCustom((Boolean) result.get("custom"));
		}
		if (result.get("order") != null) {
			field.setOrder((Integer) result.get("order"));
		}
		if (result.get("optional") != null) {
			field.setOptional((Boolean) result.get("optional"));
		}
		if (result.get("width") != null) {
			field.setOrder((Integer) result.get("width"));
		}
		if (result.get("height") != null) {
			field.setOrder((Integer) result.get("height"));
		}
		return field;
	}

	@SuppressWarnings("unchecked")
	private Object[] getAttributes(String attributeType) throws TracException {
		Object[] ids = (Object[]) call(attributeType + ".getAll");
		Map<String, Object>[] calls = new Map[ids.length];
		for (int i = 0; i < calls.length; i++) {
			calls[i] = createMultiCall(attributeType + ".get", ids[i]);
		}

		Object[] result = multicall(calls);
		assert result.length == ids.length;

		return result;
	}

	private List<TicketAttributeResult> getTicketAttributes(String attributeType) throws TracException {
		return getTicketAttributes(attributeType, false);
	}

	@SuppressWarnings("unchecked")
	private List<TicketAttributeResult> getTicketAttributes(String attributeType, boolean assignValues)
			throws TracException {
		// get list of attribute ids first
		Object[] ids = (Object[]) call(attributeType + ".getAll");
		// fetch all attributes in a single call
		Map<String, Object>[] calls = new Map[ids.length];
		for (int i = 0; i < calls.length; i++) {
			calls[i] = createMultiCall(attributeType + ".get", ids[i]);
		}

		Object[] result = multicall(calls);
		assert result.length == ids.length;

		List<TicketAttributeResult> attributes = new ArrayList<TicketAttributeResult>(result.length);
		for (int i = 0; i < calls.length; i++) {
			try {
				TicketAttributeResult attribute = new TicketAttributeResult();
				attribute.name = (String) ids[i];
				Object value = getMultiCallResult(result[i]);
				if (assignValues) {
					attribute.value = i;
				} else {
					attribute.value = (value instanceof Integer) ? (Integer) value : Integer.parseInt((String) value);
				}
				attributes.add(attribute);
			} catch (ClassCastException e) {
				StatusHandler.log(e, "Invalid response from Trac repository for attribute type: '" + attributeType
						+ "'");
			} catch (NumberFormatException e) {
				StatusHandler.log(e, "Invalid response from Trac repository for attribute type: '" + attributeType
						+ "'");
			}
		}

		return attributes;
	}

	public InputStream getAttachmentData(int ticketId, String filename) throws TracException {
		byte[] data = (byte[]) call("ticket.getAttachment", ticketId, filename);
		return new ByteArrayInputStream(data);
	}

	public void putAttachmentData(int ticketId, String filename, String description, InputStream in)
			throws TracException {
		byte[] data;
		try {
			data = readData(in, new NullProgressMonitor());
		} catch (IOException e) {
			throw new TracException(e);
		}
		call("ticket.putAttachment", ticketId, filename, description, data, false);
	}

	private byte[] readData(InputStream in, IProgressMonitor monitor) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			byte[] buffer = new byte[512];
			while (true) {
				int count = in.read(buffer);
				if (count == -1) {
					return out.toByteArray();
				}
				if (monitor.isCanceled()) {
					throw new OperationCanceledException();
				}
				out.write(buffer, 0, count);
				if (monitor.isCanceled()) {
					throw new OperationCanceledException();
				}
			}
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				StatusHandler.fail(e, "Error closing attachment stream", false);
			}
		}
	}

	public void deleteAttachment(int ticketId, String filename) throws TracException {
		call("ticket.deleteAttachment", ticketId, filename);
	}

	private class TicketAttributeResult {

		String name;

		int value;

	}

	public int createTicket(TracTicket ticket) throws TracException {
		Map<String, String> attributes = ticket.getValues();
		String summary = attributes.remove(Key.SUMMARY.getKey());
		String description = attributes.remove(Key.DESCRIPTION.getKey());
		if (summary == null || description == null) {
			throw new InvalidTicketException();
		}
		if (isAPIVersionOrHigher(0, 0, 2)) {
			return (Integer) call("ticket.create", summary, description, attributes, true);
		} else {
			return (Integer) call("ticket.create", summary, description, attributes);
		}
	}

	public void updateTicket(TracTicket ticket, String comment) throws TracException {
		updateAPIVersion();

		Map<String, String> attributes = ticket.getValues();
		if (isAPIVersionOrHigher(0, 0, 2)) {
			call("ticket.update", ticket.getId(), comment, attributes, true);
		} else {
			call("ticket.update", ticket.getId(), comment, attributes);
		}
	}

	public Set<Integer> getChangedTickets(Date since) throws TracException {
		Object[] ids;
		ids = (Object[]) call("ticket.getRecentChanges", since);
		Set<Integer> result = new HashSet<Integer>();
		for (Object id : ids) {
			result.add((Integer) id);
		}
		return result;
	}

	public String[] getActions(int id) throws TracException {
		Object[] actions = (Object[]) call("ticket.getAvailableActions", id);
		String[] result = new String[actions.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = (String) actions[i];
		}
		return result;
	}

	@Override
	public void setProxy(Proxy proxy) {
		super.setProxy(proxy);

		synchronized (this) {
			if (factory != null) {
				factory.setProxy(proxy);
			}
		}
	}

	public Date getTicketLastChanged(Integer id) throws TracException {
		Object[] result = (Object[]) call("ticket.get", id);
		return parseDate(result[2]);
	}

	public void validateWikiRpcApi() throws TracException {
		if (((Integer) call("wiki.getRPCVersionSupported")) < 2)
			validate();
	}

	public String wikiToHtml(String sourceText) throws TracException {
		return (String) call("wiki.wikiToHtml", sourceText);
	}

	public String[] getAllWikiPageNames() throws TracException {
		Object[] result = (Object[]) call("wiki.getAllPages");
		String[] wikiPageNames = new String[result.length];
		for (int i = 0; i < wikiPageNames.length; i++) {
			wikiPageNames[i] = (String) result[i];
		}
		return wikiPageNames;
	}

	public TracWikiPageInfo getWikiPageInfo(String pageName) throws TracException {
		return getWikiPageInfo(pageName, LATEST_VERSION);
	}

	public TracWikiPageInfo getWikiPageInfo(String pageName, int version) throws TracException {
		// Note: if an unexpected null value is passed to XmlRpcPlugin, XmlRpcClient will throw a TracRemoteException. 
		//       So, this null-parameter checking may be omitted if resorting to XmlRpcClient is more appropriate.
		if (pageName == null)
			throw new IllegalArgumentException("Wiki page name cannot be null");

		Object result = (version == LATEST_VERSION) ? call("wiki.getPageInfo", pageName) //
				: call("wiki.getPageInfoVersion", pageName, version);
		return parseWikiPageInfo(result);
	}

	@SuppressWarnings("unchecked")
	public TracWikiPageInfo[] getWikiPageInfoAllVersions(String pageName) throws TracException {
		TracWikiPageInfo latestVersion = getWikiPageInfo(pageName);
		Map<String, Object>[] calls = new Map[latestVersion.getVersion() - 1];
		for (int i = 0; i < calls.length; i++) {
			calls[i] = createMultiCall("wiki.getPageInfoVersion", pageName, i + 1);
		}

		Object[] result = multicall(calls);

		TracWikiPageInfo[] versions = new TracWikiPageInfo[result.length + 1];
		for (int i = 0; i < result.length; i++) {
			Object pageInfoResult = getMultiCallResult(result[i]);
			versions[i] = parseWikiPageInfo(pageInfoResult);
		}
		versions[result.length] = latestVersion;

		return versions;
	}

	private TracWikiPageInfo parseWikiPageInfo(Object pageInfoResult) throws InvalidWikiPageException {
		// Note: Trac XML-RPC Plugin returns 0 (as Integer) if pageName or version doesn't exist, 
		//       and XmlRpcClient doesn't throw an Exception in this case
		if (pageInfoResult instanceof Map<?, ?>) {
			TracWikiPageInfo pageInfo = new TracWikiPageInfo();
			Map<?, ?> infoMap = (Map<?, ?>) pageInfoResult;
			pageInfo.setPageName((String) infoMap.get("name"));
			pageInfo.setAuthor((String) infoMap.get("author"));
			pageInfo.setLastModified(parseDate(infoMap.get("lastModified")));
			pageInfo.setVersion((Integer) infoMap.get("version"));
			return pageInfo;
		} else {
			throw new InvalidWikiPageException("Wiki page name or version does not exist");
		}
	}

	public String getWikiPageContent(String pageName) throws TracException {
		return getWikiPageContent(pageName, LATEST_VERSION);
	}

	public String getWikiPageContent(String pageName, int version) throws TracException {
		// Note: if an unexpected null value is passed to XmlRpcPlugin, XmlRpcClient will throw a TracRemoteException. 
		//       So, this null-parameter checking may be omitted if resorting to XmlRpcClient is more appropriate.
		if (pageName == null)
			throw new IllegalArgumentException("Wiki page name cannot be null");
		if (version == LATEST_VERSION) {
			// XmlRpcClient throws a TracRemoteException if pageName or version doesn't exist
			return (String) call("wiki.getPage", pageName);
		} else {
			return (String) call("wiki.getPageVersion", pageName, version);
		}
	}

	public String getWikiPageHtml(String pageName) throws TracException {
		return getWikiPageHtml(pageName, LATEST_VERSION);
	}

	public String getWikiPageHtml(String pageName, int version) throws TracException {
		if (pageName == null) {
			throw new IllegalArgumentException("Wiki page name cannot be null");
		}

		if (version == LATEST_VERSION) {
			// XmlRpcClient throws a TracRemoteException if pageName or version doesn't exist
			return (String) call("wiki.getPageHTML", pageName);
		} else {
			return (String) call("wiki.getPageHTMLVersion", pageName, version);
		}
	}

	public TracWikiPageInfo[] getRecentWikiChanges(Date since) throws TracException {
		if (since == null) {
			throw new IllegalArgumentException("Date parameter cannot be null");
		}

		Object[] result = (Object[]) call("wiki.getRecentChanges", since);
		TracWikiPageInfo[] changes = new TracWikiPageInfo[result.length];
		for (int i = 0; i < result.length; i++) {
			changes[i] = parseWikiPageInfo(result[i]);
		}
		return changes;
	}

	public TracWikiPage getWikiPage(String pageName) throws TracException {
		return getWikiPage(pageName, LATEST_VERSION);
	}

	public TracWikiPage getWikiPage(String pageName, int version) throws TracException {
		TracWikiPage page = new TracWikiPage();
		page.setPageInfo(getWikiPageInfo(pageName, version));
		page.setContent(getWikiPageContent(pageName, version));
		page.setPageHTML(getWikiPageHtml(pageName, version));
		return page;
	}

	public boolean putWikipage(String pageName, String content, Map<String, Object> attributes) throws TracException {
		Boolean result = (Boolean) call("wiki.putPage", pageName, content, attributes);
		return result.booleanValue();
	}

	public String[] listWikiPageAttachments(String pageName) throws TracException {
		Object[] result = (Object[]) call("wiki.listAttachments", pageName);
		String[] attachments = new String[result.length];
		for (int i = 0; i < attachments.length; i++) {
			attachments[i] = (String) result[i];
		}
		return attachments;
	}

	public InputStream getWikiPageAttachmentData(String pageName, String fileName) throws TracException {
		String attachmentName = pageName + "/" + fileName;
		byte[] data = (byte[]) call("wiki.getAttachment", attachmentName);
		return new ByteArrayInputStream(data);
	}

	/**
	 * Attach a file to a Wiki page on the repository.
	 * <p>
	 * This implementation uses the wiki.putAttachmentEx() call, which provides a richer functionality specific to Trac.
	 * 
	 * @param pageName
	 *            the name of the Wiki page
	 * @param fileName
	 *            the name of the file to be attached
	 * @param description
	 *            the description of the attachment
	 * @param in
	 *            An InputStream of the content of the attachment
	 * @param replace
	 *            whether to overwrite an existing attachment with the same filename
	 * @return The (possibly transformed) filename of the attachment. If <code>replace</code> is <code>true</code>,
	 *         the returned name is always the same as the argument <code>fileName</code>; if <code>replace</code>
	 *         is <code>false</code> and an attachment with name <code>fileName</code> already exists, a number is
	 *         appended to the file name (before suffix) and the generated filename of the attachment is returned.
	 * @throws TracException
	 */
	public String putWikiPageAttachmentData(String pageName, String fileName, String description, InputStream in,
			boolean replace) throws TracException {
		byte[] data;
		try {
			data = readData(in, new NullProgressMonitor());
		} catch (IOException e) {
			throw new TracException(e);
		}
		return (String) call("wiki.putAttachmentEx", pageName, fileName, description, data, replace);
	}
}