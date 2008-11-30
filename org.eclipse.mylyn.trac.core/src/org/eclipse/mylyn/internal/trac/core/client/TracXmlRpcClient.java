/*******************************************************************************
 * Copyright (c) 2006, 2008 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Xiaoyang Guan - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScheme;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.auth.BasicScheme;
import org.apache.commons.httpclient.auth.DigestScheme;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.commons.net.UnsupportedRequestException;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.eclipse.mylyn.internal.trac.core.TracCorePlugin;
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
import org.eclipse.mylyn.internal.trac.core.util.HttpMethodInterceptor;
import org.eclipse.mylyn.internal.trac.core.util.TracHttpClientTransportFactory;
import org.eclipse.mylyn.internal.trac.core.util.TracUtil;
import org.eclipse.mylyn.internal.trac.core.util.TracXmlRpcClientRequest;
import org.eclipse.mylyn.internal.trac.core.util.TracHttpClientTransportFactory.TracHttpException;

/**
 * Represents a Trac repository that is accessed through the Trac XmlRpcPlugin.
 * 
 * @author Steffen Pingel
 * @author Xiaoyang Guan
 */
public class TracXmlRpcClient extends AbstractTracClient implements ITracWikiClient {

	private class XmlRpcRequest {

		private final String method;

		private final Object[] parameters;

		public XmlRpcRequest(String method, Object[] parameters) {
			this.method = method;
			this.parameters = parameters;
		}

		public Object execute(IProgressMonitor monitor) throws TracException {
			try {
				// first attempt
				return executeCallInternal(monitor);
			} catch (TracPermissionDeniedException e) {
				if (accountMangerAuthenticationFailed) {
					// do not try again if this has failed in the past since it
					// is more likely that XML_RPC permissions have not been set
					throw e;
				}

				AuthenticationCredentials credentials = location.getCredentials(AuthenticationType.REPOSITORY);
				if (!credentialsValid(credentials)) {
					throw e;
				}

				// try form-based authentication via AccountManagerPlugin as a
				// fall-back
				HostConfiguration hostConfiguration = WebUtil.createHostConfiguration(httpClient, location, monitor);
				try {
					authenticateAccountManager(httpClient, hostConfiguration, credentials, monitor);
				} catch (TracLoginException loginException) {
					// caused by wrong user name or password
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

				// the authentication information is available through the shared state in httpClient
			}

			// second attempt
			return executeCallInternal(monitor);
		}

		private Object executeCallInternal(IProgressMonitor monitor) throws TracException {
			try {
				if (isTracd && digestScheme != null) {
					probeAuthenticationScheme(monitor);
				}

				TracXmlRpcClientRequest request = new TracXmlRpcClientRequest(xmlrpc.getClientConfig(), method,
						parameters, monitor);
				return xmlrpc.execute(request);
			} catch (TracHttpException e) {
				if (e.code == HttpStatus.SC_UNAUTHORIZED) {
					digestScheme = null;
					throw new TracLoginException();
				} else if (e.code == HttpStatus.SC_FORBIDDEN) {
					digestScheme = null;
					throw new TracPermissionDeniedException();
				} else if (e.code == HttpStatus.SC_PROXY_AUTHENTICATION_REQUIRED) {
					throw new TracProxyAuthenticationException();
				} else {
					throw new TracException(e);
				}
			} catch (XmlRpcException e) {
				if (e.code == NO_SUCH_METHOD_ERROR) {
					throw new TracNoSuchMethodException(e);
				} else {
					throw new TracRemoteException(e);
				}
			} catch (OperationCanceledException e) {
				throw e;
			} catch (Exception e) {
				throw new TracException(e);
			}
		}

	}

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

	private XmlRpcClientConfigImpl config;

	private final HttpClient httpClient;

	private boolean probed;

	private volatile DigestScheme digestScheme;

	private final AuthScope authScope;

	private boolean isTracd;

	public TracXmlRpcClient(AbstractWebLocation location, Version version) {
		super(location, version);
		this.httpClient = createHttpClient();
		this.authScope = new AuthScope(WebUtil.getHost(repositoryUrl), WebUtil.getPort(repositoryUrl), null,
				AuthScope.ANY_SCHEME);
	}

	public synchronized XmlRpcClient getClient() throws TracException {
		if (xmlrpc == null) {
			config = new XmlRpcClientConfigImpl();
			config.setEncoding(ITracClient.CHARSET);
			config.setTimeZone(TimeZone.getTimeZone(ITracClient.TIME_ZONE));
			config.setContentLengthOptional(false);
			config.setConnectionTimeout(WebUtil.getConnectionTimeout());
			config.setReplyTimeout(WebUtil.getSocketTimeout());

			xmlrpc = new XmlRpcClient();
			xmlrpc.setConfig(config);

			factory = new TracHttpClientTransportFactory(xmlrpc, httpClient);
			factory.setLocation(location);
			factory.setInterceptor(new HttpMethodInterceptor() {
				public void processRequest(HttpMethod method) {
					DigestScheme scheme = digestScheme;
					if (scheme != null) {
						Credentials creds = httpClient.getState().getCredentials(authScope);
						if (creds != null) {
							method.getHostAuthState().setAuthScheme(digestScheme);
							method.getHostAuthState().setAuthRequested(true);
						}
					}
				}

				public void processResponse(HttpMethod method) {
					AuthScheme authScheme = method.getHostAuthState().getAuthScheme();
					if (authScheme instanceof DigestScheme) {
						digestScheme = (DigestScheme) authScheme;
					}
				}
			});
			xmlrpc.setTransportFactory(factory);
		}

		// update configuration with latest values
		AuthenticationCredentials credentials = location.getCredentials(AuthenticationType.REPOSITORY);
		config.setServerURL(getXmlRpcUrl(credentials));
		if (credentialsValid(credentials)) {
			Credentials creds = new UsernamePasswordCredentials(credentials.getUserName(), credentials.getPassword());
			httpClient.getState().setCredentials(authScope, creds);
		} else {
			httpClient.getState().clearCredentials();
		}

		return xmlrpc;
	}

	private URL getXmlRpcUrl(AuthenticationCredentials credentials) throws TracException {
		try {
			String location = repositoryUrl.toString();
			if (credentialsValid(credentials)) {
				location += LOGIN_URL;
			}
			location += XMLRPC_URL;

			return new URL(location);
		} catch (Exception e) {
			throw new TracException(e);
		}
	}

	private void probeAuthenticationScheme(IProgressMonitor monitor) throws TracException {
		AuthenticationCredentials credentials = location.getCredentials(AuthenticationType.REPOSITORY);
		if (!credentialsValid(credentials)) {
			return;
		}

		HostConfiguration hostConfiguration = WebUtil.createHostConfiguration(httpClient, location, monitor);
		HeadMethod method = new HeadMethod(getXmlRpcUrl(credentials).toString());
		try {
			// execute without any credentials set
			int result = WebUtil.execute(httpClient, hostConfiguration, method, new HttpState(), monitor);
			if (result == HttpStatus.SC_UNAUTHORIZED || result == HttpStatus.SC_FORBIDDEN) {
				AuthScheme authScheme = method.getHostAuthState().getAuthScheme();
				if (authScheme instanceof DigestScheme) {
					this.digestScheme = (DigestScheme) authScheme;
				} else if (authScheme instanceof BasicScheme) {
					httpClient.getParams().setAuthenticationPreemptive(true);
				}

				Header header = method.getResponseHeader("Server");
				isTracd = (header != null && header.getValue().startsWith("tracd"));

//					Header header = method.getResponseHeader("WWW-Authenticate");
//					if (header != null) {
//						if (header.getValue().startsWith("Basic")) {
//							httpClient.getParams().setAuthenticationPreemptive(true);
//						} else if (header.getValue().startsWith("Digest")) {
//							DigestScheme scheme = new DigestScheme();
//							try {
//								scheme.processChallenge(header.getValue());
//								this.digestScheme = scheme;
//							} catch (MalformedChallengeException e) {
//								// ignore
//							}
//						}
//					}
			}
		} catch (IOException e) {
			// ignore
		} finally {
			method.releaseConnection();
		}
	}

	private Object call(IProgressMonitor monitor, String method, Object... parameters) throws TracException {
		monitor = Policy.monitorFor(monitor);
		while (true) {
			if (!probed) {
				try {
					probeAuthenticationScheme(monitor);
				} finally {
					probed = true;
				}
			}

			getClient();

			try {
				XmlRpcRequest request = new XmlRpcRequest(method, parameters);
				return request.execute(monitor);
			} catch (TracLoginException e) {
				try {
					location.requestCredentials(AuthenticationType.REPOSITORY, null, monitor);
				} catch (UnsupportedRequestException ignored) {
					throw e;
				}
			} catch (TracPermissionDeniedException e) {
				try {
					location.requestCredentials(AuthenticationType.REPOSITORY, null, monitor);
				} catch (UnsupportedRequestException ignored) {
					throw e;
				}
			} catch (TracProxyAuthenticationException e) {
				try {
					location.requestCredentials(AuthenticationType.PROXY, null, monitor);
				} catch (UnsupportedRequestException ignored) {
					throw e;
				}
			}
		}
	}

	private Object[] multicall(IProgressMonitor monitor, Map<String, Object>... calls) throws TracException {
		Object[] result = (Object[]) call(monitor, "system.multicall", new Object[] { calls });
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

	public void validate(IProgressMonitor monitor) throws TracException {
		try {
			Object[] result = (Object[]) call(monitor, "system.getAPIVersion");
			if (result.length >= 3) {
				epochAPIVersion = (Integer) result[0];
				majorAPIVersion = (Integer) result[1];
				minorAPIVersion = (Integer) result[2];
			} else if (result.length >= 2) {
				epochAPIVersion = 0;
				majorAPIVersion = (Integer) result[0];
				minorAPIVersion = (Integer) result[1];
			} else {
				throw new TracException(
						"The API version is unsupported, please update your Trac XML-RPC Plugin to revision "
								+ REQUIRED_REVISION + " or later");
			}
		} catch (TracNoSuchMethodException e) {
			throw new TracException(
					"Required API calls are missing, please update your Trac XML-RPC Plugin to revision "
							+ REQUIRED_REVISION + " or later");
		}

		if (!isAPIVersionOrHigher(REQUIRED_EPOCH, REQUIRED_MAJOR, REQUIRED_MINOR, monitor)) {
			throw new TracException("The API version " + majorAPIVersion + "." + minorAPIVersion
					+ " is unsupported, please update your Trac XML-RPC Plugin to revision " + REQUIRED_REVISION
					+ " or later");
		}
	}

	private void updateAPIVersion(IProgressMonitor monitor) throws TracException {
		if (epochAPIVersion == -1 || majorAPIVersion == -1 || minorAPIVersion == -1) {
			validate(monitor);
		}
	}

	private boolean isAPIVersionOrHigher(int epoch, int major, int minor, IProgressMonitor monitor)
			throws TracException {
		updateAPIVersion(monitor);
		return (epochAPIVersion > epoch || (epochAPIVersion == epoch && majorAPIVersion > major || (majorAPIVersion == major && minorAPIVersion >= minor)));
	}

	public TracTicket getTicket(int id, IProgressMonitor monitor) throws TracException {
		Object[] result = (Object[]) call(monitor, "ticket.get", id);
		TracTicket ticket = parseTicket(result);

		result = (Object[]) call(monitor, "ticket.changeLog", id, 0);
		for (Object item : result) {
			ticket.addComment(parseChangeLogEntry((Object[]) item));
		}

		result = (Object[]) call(monitor, "ticket.listAttachments", id);
		for (Object item : result) {
			ticket.addAttachment(parseAttachment((Object[]) item));
		}

		String[] actions = getActions(id, monitor);
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
	public List<TracTicket> getTickets(int[] ids, IProgressMonitor monitor) throws TracException {
		Map<String, Object>[] calls = new Map[ids.length];
		for (int i = 0; i < calls.length; i++) {
			calls[i] = createMultiCall("ticket.get", ids[i]);
		}

		Object[] result = multicall(monitor, calls);
		assert result.length == ids.length;

		List<TracTicket> tickets = new ArrayList<TracTicket>(result.length);
		for (Object item : result) {
			Object[] ticketResult = (Object[]) getMultiCallResult(item);
			tickets.add(parseTicket(ticketResult));
		}

		return tickets;
	}

	@SuppressWarnings("unchecked")
	public void search(TracSearch query, List<TracTicket> tickets, IProgressMonitor monitor) throws TracException {
		// an empty query string is not valid, therefore prepend order
		Object[] result = (Object[]) call(monitor, "ticket.query", "order=id" + query.toQuery());

		Map<String, Object>[] calls = new Map[result.length];
		for (int i = 0; i < calls.length; i++) {
			calls[i] = createMultiCall("ticket.get", result[i]);
		}
		result = multicall(monitor, calls);

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
			return TracUtil.parseDate((Integer) object);
		}
		throw new ClassCastException("Unexpected object type for date: " + object.getClass());
	}

	@Override
	public synchronized void updateAttributes(IProgressMonitor monitor) throws TracException {
		monitor.beginTask("Updating attributes", 9);

		Object[] result = getAttributes("ticket.component", monitor);
		data.components = new ArrayList<TracComponent>(result.length);
		for (Object item : result) {
			data.components.add(parseComponent((Map<?, ?>) getMultiCallResult(item)));
		}
		advance(monitor, 1);

		result = getAttributes("ticket.milestone", monitor);
		data.milestones = new ArrayList<TracMilestone>(result.length);
		for (Object item : result) {
			data.milestones.add(parseMilestone((Map<?, ?>) getMultiCallResult(item)));
		}
		advance(monitor, 1);

		List<TicketAttributeResult> attributes = getTicketAttributes("ticket.priority", monitor);
		data.priorities = new ArrayList<TracPriority>(result.length);
		for (TicketAttributeResult attribute : attributes) {
			data.priorities.add(new TracPriority(attribute.name, attribute.value));
		}
		Collections.sort(data.priorities);
		advance(monitor, 1);

		attributes = getTicketAttributes("ticket.resolution", monitor);
		data.ticketResolutions = new ArrayList<TracTicketResolution>(result.length);
		for (TicketAttributeResult attribute : attributes) {
			data.ticketResolutions.add(new TracTicketResolution(attribute.name, attribute.value));
		}
		Collections.sort(data.ticketResolutions);
		advance(monitor, 1);

		attributes = getTicketAttributes("ticket.severity", monitor);
		data.severities = new ArrayList<TracSeverity>(result.length);
		for (TicketAttributeResult attribute : attributes) {
			data.severities.add(new TracSeverity(attribute.name, attribute.value));
		}
		Collections.sort(data.severities);
		advance(monitor, 1);

		boolean trac011 = isAPIVersionOrHigher(1, 0, 0, monitor);
		attributes = getTicketAttributes("ticket.status", trac011, monitor);
		data.ticketStatus = new ArrayList<TracTicketStatus>(result.length);
		for (TicketAttributeResult attribute : attributes) {
			data.ticketStatus.add(new TracTicketStatus(attribute.name, attribute.value));
		}
		Collections.sort(data.ticketStatus);
		advance(monitor, 1);

		attributes = getTicketAttributes("ticket.type", monitor);
		data.ticketTypes = new ArrayList<TracTicketType>(result.length);
		for (TicketAttributeResult attribute : attributes) {
			data.ticketTypes.add(new TracTicketType(attribute.name, attribute.value));
		}
		Collections.sort(data.ticketTypes);
		advance(monitor, 1);

		result = getAttributes("ticket.version", monitor);
		data.versions = new ArrayList<TracVersion>(result.length);
		for (Object item : result) {
			data.versions.add(parseVersion((Map<?, ?>) getMultiCallResult(item)));
		}
		advance(monitor, 1);

		result = (Object[]) call(monitor, "ticket.getTicketFields");
		data.ticketFields = new ArrayList<TracTicketField>(result.length);
		for (Object item : result) {
			data.ticketFields.add(parseTicketField((Map<?, ?>) item));
		}
		advance(monitor, 1);
	}

	private void advance(IProgressMonitor monitor, int worked) {
		monitor.worked(worked);
		if (monitor.isCanceled()) {
			throw new OperationCanceledException();
		}

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
			field.setWidth((Integer) result.get("width"));
		}
		if (result.get("height") != null) {
			field.setHeight((Integer) result.get("height"));
		}
		return field;
	}

	@SuppressWarnings("unchecked")
	private Object[] getAttributes(String attributeType, IProgressMonitor monitor) throws TracException {
		Object[] ids = (Object[]) call(monitor, attributeType + ".getAll");
		Map<String, Object>[] calls = new Map[ids.length];
		for (int i = 0; i < calls.length; i++) {
			calls[i] = createMultiCall(attributeType + ".get", ids[i]);
		}

		Object[] result = multicall(monitor, calls);
		assert result.length == ids.length;

		return result;
	}

	private List<TicketAttributeResult> getTicketAttributes(String attributeType, IProgressMonitor monitor)
			throws TracException {
		return getTicketAttributes(attributeType, false, monitor);
	}

	@SuppressWarnings("unchecked")
	private List<TicketAttributeResult> getTicketAttributes(String attributeType, boolean assignValues,
			IProgressMonitor monitor) throws TracException {
		// get list of attribute ids first
		Object[] ids = (Object[]) call(monitor, attributeType + ".getAll");
		// fetch all attributes in a single call
		Map<String, Object>[] calls = new Map[ids.length];
		for (int i = 0; i < calls.length; i++) {
			calls[i] = createMultiCall(attributeType + ".get", ids[i]);
		}

		Object[] result = multicall(monitor, calls);
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
				StatusHandler.log(new Status(IStatus.WARNING, TracCorePlugin.ID_PLUGIN,
						"Invalid response from Trac repository for attribute type: '" + attributeType + "'", e));
			} catch (NumberFormatException e) {
				StatusHandler.log(new Status(IStatus.WARNING, TracCorePlugin.ID_PLUGIN,
						"Invalid response from Trac repository for attribute type: '" + attributeType + "'", e));
			}
		}

		return attributes;
	}

	public InputStream getAttachmentData(int ticketId, String filename, IProgressMonitor monitor) throws TracException {
		byte[] data = (byte[]) call(monitor, "ticket.getAttachment", ticketId, filename);
		return new ByteArrayInputStream(data);
	}

	public void putAttachmentData(int ticketId, String filename, String description, InputStream in,
			IProgressMonitor monitor) throws TracException {
		byte[] data;
		try {
			data = readData(in, new NullProgressMonitor());
		} catch (IOException e) {
			throw new TracException(e);
		}
		call(monitor, "ticket.putAttachment", ticketId, filename, description, data, false);
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
				StatusHandler.log(new Status(IStatus.ERROR, TracCorePlugin.ID_PLUGIN,
						"Error closing attachment stream", e));
			}
		}
	}

	public void deleteAttachment(int ticketId, String filename, IProgressMonitor monitor) throws TracException {
		call(monitor, "ticket.deleteAttachment", ticketId, filename);
	}

	private class TicketAttributeResult {

		String name;

		int value;

	}

	public int createTicket(TracTicket ticket, IProgressMonitor monitor) throws TracException {
		Map<String, String> attributes = ticket.getValues();
		String summary = attributes.remove(Key.SUMMARY.getKey());
		String description = attributes.remove(Key.DESCRIPTION.getKey());
		if (summary == null || description == null) {
			throw new InvalidTicketException();
		}
		if (isAPIVersionOrHigher(0, 0, 2, monitor)) {
			return (Integer) call(monitor, "ticket.create", summary, description, attributes, true);
		} else {
			return (Integer) call(monitor, "ticket.create", summary, description, attributes);
		}
	}

	public void updateTicket(TracTicket ticket, String comment, IProgressMonitor monitor) throws TracException {
		updateAPIVersion(monitor);

		Map<String, String> attributes = ticket.getValues();
		if (isAPIVersionOrHigher(0, 0, 2, monitor)) {
			call(monitor, "ticket.update", ticket.getId(), comment, attributes, true);
		} else {
			call(monitor, "ticket.update", ticket.getId(), comment, attributes);
		}
	}

	public Set<Integer> getChangedTickets(Date since, IProgressMonitor monitor) throws TracException {
		Object[] ids;
		ids = (Object[]) call(monitor, "ticket.getRecentChanges", since);
		Set<Integer> result = new HashSet<Integer>();
		for (Object id : ids) {
			result.add((Integer) id);
		}
		return result;
	}

	public String[] getActions(int id, IProgressMonitor monitor) throws TracException {
		Object[] actions = (Object[]) call(monitor, "ticket.getAvailableActions", id);
		String[] result = new String[actions.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = (String) actions[i];
		}
		return result;
	}

	public Date getTicketLastChanged(Integer id, IProgressMonitor monitor) throws TracException {
		Object[] result = (Object[]) call(monitor, "ticket.get", id);
		return parseDate(result[2]);
	}

	public void validateWikiRpcApi(IProgressMonitor monitor) throws TracException {
		if (((Integer) call(monitor, "wiki.getRPCVersionSupported")) < 2) {
			validate(monitor);
		}
	}

	public String wikiToHtml(String sourceText, IProgressMonitor monitor) throws TracException {
		return (String) call(monitor, "wiki.wikiToHtml", sourceText);
	}

	public String[] getAllWikiPageNames(IProgressMonitor monitor) throws TracException {
		Object[] result = (Object[]) call(monitor, "wiki.getAllPages");
		String[] wikiPageNames = new String[result.length];
		for (int i = 0; i < wikiPageNames.length; i++) {
			wikiPageNames[i] = (String) result[i];
		}
		return wikiPageNames;
	}

	public TracWikiPageInfo getWikiPageInfo(String pageName, IProgressMonitor monitor) throws TracException {
		return getWikiPageInfo(pageName, LATEST_VERSION, null);
	}

	public TracWikiPageInfo getWikiPageInfo(String pageName, int version, IProgressMonitor monitor)
			throws TracException {
		// Note: if an unexpected null value is passed to XmlRpcPlugin, XmlRpcClient will throw a TracRemoteException. 
		//       So, this null-parameter checking may be omitted if resorting to XmlRpcClient is more appropriate.
		if (pageName == null) {
			throw new IllegalArgumentException("Wiki page name cannot be null");
		}

		Object result = (version == LATEST_VERSION) ? call(monitor, "wiki.getPageInfo", pageName) //
				: call(monitor, "wiki.getPageInfoVersion", pageName, version);
		return parseWikiPageInfo(result);
	}

	@SuppressWarnings("unchecked")
	public TracWikiPageInfo[] getWikiPageInfoAllVersions(String pageName, IProgressMonitor monitor)
			throws TracException {
		TracWikiPageInfo latestVersion = getWikiPageInfo(pageName, null);
		Map<String, Object>[] calls = new Map[latestVersion.getVersion() - 1];
		for (int i = 0; i < calls.length; i++) {
			calls[i] = createMultiCall("wiki.getPageInfoVersion", pageName, i + 1);
		}

		Object[] result = multicall(monitor, calls);

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

	public String getWikiPageContent(String pageName, IProgressMonitor monitor) throws TracException {
		return getWikiPageContent(pageName, LATEST_VERSION, null);
	}

	public String getWikiPageContent(String pageName, int version, IProgressMonitor monitor) throws TracException {
		// Note: if an unexpected null value is passed to XmlRpcPlugin, XmlRpcClient will throw a TracRemoteException. 
		//       So, this null-parameter checking may be omitted if resorting to XmlRpcClient is more appropriate.
		if (pageName == null) {
			throw new IllegalArgumentException("Wiki page name cannot be null");
		}
		if (version == LATEST_VERSION) {
			// XmlRpcClient throws a TracRemoteException if pageName or version doesn't exist
			return (String) call(monitor, "wiki.getPage", pageName);
		} else {
			return (String) call(monitor, "wiki.getPageVersion", pageName, version);
		}
	}

	public String getWikiPageHtml(String pageName, IProgressMonitor monitor) throws TracException {
		return getWikiPageHtml(pageName, LATEST_VERSION, null);
	}

	public String getWikiPageHtml(String pageName, int version, IProgressMonitor monitor) throws TracException {
		if (pageName == null) {
			throw new IllegalArgumentException("Wiki page name cannot be null");
		}

		if (version == LATEST_VERSION) {
			// XmlRpcClient throws a TracRemoteException if pageName or version doesn't exist
			return (String) call(monitor, "wiki.getPageHTML", pageName);
		} else {
			return (String) call(monitor, "wiki.getPageHTMLVersion", pageName, version);
		}
	}

	public TracWikiPageInfo[] getRecentWikiChanges(Date since, IProgressMonitor monitor) throws TracException {
		if (since == null) {
			throw new IllegalArgumentException("Date parameter cannot be null");
		}

		Object[] result = (Object[]) call(monitor, "wiki.getRecentChanges", since);
		TracWikiPageInfo[] changes = new TracWikiPageInfo[result.length];
		for (int i = 0; i < result.length; i++) {
			changes[i] = parseWikiPageInfo(result[i]);
		}
		return changes;
	}

	public TracWikiPage getWikiPage(String pageName, IProgressMonitor monitor) throws TracException {
		return getWikiPage(pageName, LATEST_VERSION, null);
	}

	public TracWikiPage getWikiPage(String pageName, int version, IProgressMonitor monitor) throws TracException {
		TracWikiPage page = new TracWikiPage();
		page.setPageInfo(getWikiPageInfo(pageName, version, null));
		page.setContent(getWikiPageContent(pageName, version, null));
		page.setPageHTML(getWikiPageHtml(pageName, version, null));
		return page;
	}

	public boolean putWikipage(String pageName, String content, Map<String, Object> attributes, IProgressMonitor monitor)
			throws TracException {
		Boolean result = (Boolean) call(monitor, "wiki.putPage", pageName, content, attributes);
		return result.booleanValue();
	}

	public String[] listWikiPageAttachments(String pageName, IProgressMonitor monitor) throws TracException {
		Object[] result = (Object[]) call(monitor, "wiki.listAttachments", pageName);
		String[] attachments = new String[result.length];
		for (int i = 0; i < attachments.length; i++) {
			attachments[i] = (String) result[i];
		}
		return attachments;
	}

	public InputStream getWikiPageAttachmentData(String pageName, String fileName, IProgressMonitor monitor)
			throws TracException {
		String attachmentName = pageName + "/" + fileName;
		byte[] data = (byte[]) call(monitor, "wiki.getAttachment", attachmentName);
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
	 * @return The (possibly transformed) filename of the attachment. If <code>replace</code> is <code>true</code>, the
	 *         returned name is always the same as the argument <code>fileName</code>; if <code>replace</code> is
	 *         <code>false</code> and an attachment with name <code>fileName</code> already exists, a number is appended
	 *         to the file name (before suffix) and the generated filename of the attachment is returned.
	 * @throws TracException
	 */
	public String putWikiPageAttachmentData(String pageName, String fileName, String description, InputStream in,
			boolean replace, IProgressMonitor monitor) throws TracException {
		byte[] data;
		try {
			data = readData(in, new NullProgressMonitor());
		} catch (IOException e) {
			throw new TracException(e);
		}
		return (String) call(monitor, "wiki.putAttachmentEx", pageName, fileName, description, data, replace);
	}

	public void deleteTicket(int ticketId, IProgressMonitor monitor) throws TracException {
		call(monitor, "ticket.delete", ticketId);
	}

}