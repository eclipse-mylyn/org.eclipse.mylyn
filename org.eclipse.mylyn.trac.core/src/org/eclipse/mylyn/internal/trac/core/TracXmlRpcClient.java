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
 */
public class TracXmlRpcClient extends AbstractTracClient {

	public static final String XMLRPC_URL = "/xmlrpc";

	public static final String REQUIRED_REVISION = "1950";

	public static final int REQUIRED_MAJOR = 0;

	public static final int REQUIRED_MINOR = 1;

	private static final int NO_SUCH_METHOD_ERROR = 1;

	private XmlRpcClient xmlrpc;

	private TracHttpClientTransportFactory factory;

	private int majorAPIVersion = -1;

	private int minorAPIVersion = -1;

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
			majorAPIVersion = (Integer) result[0];
			minorAPIVersion = (Integer) result[1];
		} catch (TracNoSuchMethodException e) {
			throw new TracException(
					"Required API calls are missing, please update your Trac XML-RPC Plugin to revision "
							+ REQUIRED_REVISION + " or later");
		}

		if (!isAPIVersionOrHigher(REQUIRED_MAJOR, REQUIRED_MINOR)) {
			throw new TracException("The API version " + majorAPIVersion + "." + minorAPIVersion
					+ " is unsupported, please update your Trac XML-RPC Plugin to revision " + REQUIRED_REVISION
					+ " or later");
		}
	}

	private void updateAPIVersion() throws TracException {
		if (majorAPIVersion == -1 || minorAPIVersion == -1) {
			validate();
		}
	}

	private boolean isAPIVersionOrHigher(int major, int minor) throws TracException {
		updateAPIVersion();
		return (majorAPIVersion > major || (majorAPIVersion == major && minorAPIVersion >= minor));
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

		attributes = getTicketAttributes("ticket.status");
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

	@SuppressWarnings("unchecked")
	private List<TicketAttributeResult> getTicketAttributes(String attributeType) throws TracException {
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
				attribute.value = (value instanceof Integer) ? (Integer) value : Integer.parseInt((String) value);
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

	public void putAttachmentData(int ticketId, String filename, String description, InputStream in) throws TracException {
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
		if (isAPIVersionOrHigher(0, 2)) {
			return (Integer) call("ticket.create", summary, description, attributes, true);
		} else {
			return (Integer) call("ticket.create", summary, description, attributes);
		}
	}

	public void updateTicket(TracTicket ticket, String comment) throws TracException {
		updateAPIVersion();

		Map<String, String> attributes = ticket.getValues();
		if (isAPIVersionOrHigher(0, 2)) {
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

}