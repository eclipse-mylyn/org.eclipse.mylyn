package org.eclipse.mylar.internal.trac.core;

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

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.mylar.core.MylarStatusHandler;
import org.eclipse.mylar.internal.trac.core.model.TracAttachment;
import org.eclipse.mylar.internal.trac.core.model.TracComment;
import org.eclipse.mylar.internal.trac.core.model.TracComponent;
import org.eclipse.mylar.internal.trac.core.model.TracMilestone;
import org.eclipse.mylar.internal.trac.core.model.TracPriority;
import org.eclipse.mylar.internal.trac.core.model.TracSearch;
import org.eclipse.mylar.internal.trac.core.model.TracSeverity;
import org.eclipse.mylar.internal.trac.core.model.TracTicket;
import org.eclipse.mylar.internal.trac.core.model.TracTicketResolution;
import org.eclipse.mylar.internal.trac.core.model.TracTicketStatus;
import org.eclipse.mylar.internal.trac.core.model.TracTicketType;
import org.eclipse.mylar.internal.trac.core.model.TracVersion;
import org.eclipse.mylar.internal.trac.core.model.TracTicket.Key;
import org.eclipse.mylar.internal.trac.core.util.TracHttpClientTransportFactory;
import org.eclipse.mylar.internal.trac.core.util.TracUtils;
import org.eclipse.mylar.internal.trac.core.util.TracHttpClientTransportFactory.TracHttpException;

/**
 * Represents a Trac repository that is accessed through the Trac XmlRpcPlugin.
 * 
 * @author Steffen Pingel
 */
public class TracXmlRpcClient extends AbstractTracClient {

	public static final String XMLRPC_URL = "/xmlrpc";

	public static final String REQUIRED_REVISION = "1735";

	public static final int REQUIRED_MAJOR = 0;

	public static final int REQUIRED_MINOR = 1;

	private static final int NO_SUCH_METHOD_ERROR = 1;

	private XmlRpcClient xmlrpc;

	private TracHttpClientTransportFactory factory;

	private int majorAPIVersion = -1;

	private int minorAPIVersion = -1;

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

		ticket.setResolutions(getDefaultTicketResolutions());

		return ticket;
	}

	private TracAttachment parseAttachment(Object[] entry) {
		TracAttachment attachment = new TracAttachment((String) entry[0]);
		attachment.setDescription((String) entry[1]);
		attachment.setSize((Integer) entry[2]);
		attachment.setCreated(TracUtils.parseDate((Integer) entry[3]));
		attachment.setAuthor((String) entry[4]);
		return attachment;
	}

	private TracComment parseChangeLogEntry(Object[] entry) {
		TracComment comment = new TracComment();
		comment.setCreated(TracUtils.parseDate((Integer) entry[0]));
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
		ticket.setCreated((Integer) ticketResult[1]);
		ticket.setLastChanged((Integer) ticketResult[2]);
		Map<?, ?> attributes = (Map<?, ?>) ticketResult[3];
		for (Object key : attributes.keySet()) {
			ticket.putValue(key.toString(), attributes.get(key).toString());
		}
		return ticket;
	}

	@Override
	public synchronized void updateAttributes(IProgressMonitor monitor) throws TracException {
		monitor.beginTask("Updating attributes", 8);

		Object[] result = getAttributes("ticket.component");
		data.components = new ArrayList<TracComponent>(result.length);
		for (Object item : result) {
			data.components.add(parseComponent((Map<?, ?>) getMultiCallResult(item)));
		}
		monitor.worked(1);
		if (monitor.isCanceled())
			throw new OperationCanceledException();

		result = getAttributes("ticket.milestone");
		data.milestones = new ArrayList<TracMilestone>(result.length);
		for (Object item : result) {
			data.milestones.add(parseMilestone((Map<?, ?>) getMultiCallResult(item)));
		}
		monitor.worked(1);
		if (monitor.isCanceled())
			throw new OperationCanceledException();

		List<TicketAttributeResult> attributes = getTicketAttributes("ticket.priority");
		data.priorities = new ArrayList<TracPriority>(result.length);
		for (TicketAttributeResult attribute : attributes) {
			data.priorities.add(new TracPriority(attribute.name, attribute.value));
		}
		Collections.sort(data.priorities);
		monitor.worked(1);
		if (monitor.isCanceled())
			throw new OperationCanceledException();

		attributes = getTicketAttributes("ticket.resolution");
		data.ticketResolutions = new ArrayList<TracTicketResolution>(result.length);
		for (TicketAttributeResult attribute : attributes) {
			data.ticketResolutions.add(new TracTicketResolution(attribute.name, attribute.value));
		}
		Collections.sort(data.ticketResolutions);
		monitor.worked(1);
		if (monitor.isCanceled())
			throw new OperationCanceledException();

		attributes = getTicketAttributes("ticket.severity");
		data.severities = new ArrayList<TracSeverity>(result.length);
		for (TicketAttributeResult attribute : attributes) {
			data.severities.add(new TracSeverity(attribute.name, attribute.value));
		}
		Collections.sort(data.severities);
		monitor.worked(1);
		if (monitor.isCanceled())
			throw new OperationCanceledException();

		attributes = getTicketAttributes("ticket.status");
		data.ticketStatus = new ArrayList<TracTicketStatus>(result.length);
		for (TicketAttributeResult attribute : attributes) {
			data.ticketStatus.add(new TracTicketStatus(attribute.name, attribute.value));
		}
		Collections.sort(data.ticketStatus);
		monitor.worked(1);
		if (monitor.isCanceled())
			throw new OperationCanceledException();

		attributes = getTicketAttributes("ticket.type");
		data.ticketTypes = new ArrayList<TracTicketType>(result.length);
		for (TicketAttributeResult attribute : attributes) {
			data.ticketTypes.add(new TracTicketType(attribute.name, attribute.value));
		}
		Collections.sort(data.ticketTypes);
		monitor.worked(1);
		if (monitor.isCanceled())
			throw new OperationCanceledException();

		result = getAttributes("ticket.version");
		data.versions = new ArrayList<TracVersion>(result.length);
		for (Object item : result) {
			data.versions.add(parseVersion((Map<?, ?>) getMultiCallResult(item)));
		}
		monitor.worked(1);
		if (monitor.isCanceled())
			throw new OperationCanceledException();
	}

	private TracComponent parseComponent(Map<?, ?> result) {
		TracComponent component = new TracComponent((String) result.get("name"));
		component.setOwner((String) result.get("owner"));
		component.setDescription((String) result.get("summary"));
		return component;
	}

	private TracMilestone parseMilestone(Map<?, ?> result) {
		TracMilestone milestone = new TracMilestone((String) result.get("name"));
		milestone.setCompleted(TracUtils.parseDate((Integer) result.get("completed")));
		milestone.setDue(TracUtils.parseDate((Integer) result.get("due")));
		milestone.setDescription((String) result.get("summary"));
		return milestone;
	}

	private TracVersion parseVersion(Map<?, ?> result) {
		TracVersion version = new TracVersion((String) result.get("name"));
		version.setTime(TracUtils.parseDate((Integer) result.get("time")));
		version.setDescription((String) result.get("summary"));
		return version;
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
				MylarStatusHandler.log(e, "Invalid response from Trac repository for attribute type: '" + attributeType
						+ "'");
			} catch (NumberFormatException e) {
				MylarStatusHandler.log(e, "Invalid response from Trac repository for attribute type: '" + attributeType
						+ "'");
			}
		}

		return attributes;
	}

	public byte[] getAttachmentData(int ticketId, String filename) throws TracException {
		return (byte[]) call("ticket.getAttachment", ticketId, filename);
	}

	public void putAttachmentData(int ticketId, String filename, String description, byte[] data) throws TracException {
		call("ticket.putAttachment", ticketId, filename, description, data, true);
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