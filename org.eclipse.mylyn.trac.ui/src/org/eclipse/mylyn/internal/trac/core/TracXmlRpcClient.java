package org.eclipse.mylar.internal.trac.core;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.internal.trac.core.TracHttpClientTransportFactory.TracHttpException;
import org.eclipse.mylar.internal.trac.model.TracAttachment;
import org.eclipse.mylar.internal.trac.model.TracComment;
import org.eclipse.mylar.internal.trac.model.TracComponent;
import org.eclipse.mylar.internal.trac.model.TracMilestone;
import org.eclipse.mylar.internal.trac.model.TracPriority;
import org.eclipse.mylar.internal.trac.model.TracSearch;
import org.eclipse.mylar.internal.trac.model.TracSeverity;
import org.eclipse.mylar.internal.trac.model.TracTicket;
import org.eclipse.mylar.internal.trac.model.TracTicketResolution;
import org.eclipse.mylar.internal.trac.model.TracTicketStatus;
import org.eclipse.mylar.internal.trac.model.TracTicketType;
import org.eclipse.mylar.internal.trac.model.TracVersion;
import org.eclipse.mylar.internal.trac.model.TracTicket.Key;

/**
 * Represents a Trac repository that is accessed through the Trac XmlRpcPlugin.
 * 
 * @author Steffen Pingel
 */
public class TracXmlRpcClient extends AbstractTracClient {

	public static final String XMLRPC_URL = "/xmlrpc";

	private XmlRpcClient xmlrpc;

	public TracXmlRpcClient(URL url, Version version, String username, String password) {
		super(url, version, username, password);
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

		xmlrpc = new XmlRpcClient();
		xmlrpc.setConfig(config);

		TracHttpClientTransportFactory factory = new TracHttpClientTransportFactory(xmlrpc);
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
			if (e.code == HttpURLConnection.HTTP_FORBIDDEN || e.code == HttpURLConnection.HTTP_UNAUTHORIZED) {
				throw new TracLoginException();
			} else {
				throw new TracException(e);
			}
		} catch (XmlRpcException e) {
			throw new TracRemoteException(e);
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
			Map exceptionData = (Map) result;
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
		Object[] result = (Object[]) call("system.listMethods");
		boolean hasGetTicket = false, hasQuery = false;
		for (Object methodName : result) {
			if ("ticket.get".equals(methodName)) {
				hasGetTicket = true;
			}
			if ("ticket.query".equals(methodName)) {
				hasQuery = true;
			}

			if (hasGetTicket && hasQuery) {
				return;
			}
		}

		throw new TracException("XML-RPC is missing required API calls");
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

		try {
			String[] actions = getActions(id);
			ticket.setActions(actions);
		} catch (TracException e) {
			// remove this if getActions() has been implemented in XmlRpcPlugin
			String status = ticket.getValue(Key.STATUS);
			ticket.setActions(getDefaultTicketActions(status));
		}
		
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
		Map attributes = (Map) ticketResult[3];
		for (Object key : attributes.keySet()) {
			ticket.putValue(key.toString(), attributes.get(key).toString());
		}
		return ticket;
	}

	public synchronized void updateAttributes(IProgressMonitor monitor) throws TracException {
		monitor.beginTask("Updating attributes", 8);

		Object[] result = getAttributes("ticket.component");
		data.components = new ArrayList<TracComponent>(result.length);
		for (Object item : result) {
			data.components.add(parseComponent((Map) getMultiCallResult(item)));
		}
		monitor.worked(1);
		if (monitor.isCanceled())
			throw new OperationCanceledException();

		result = getAttributes("ticket.milestone");
		data.milestones = new ArrayList<TracMilestone>(result.length);
		for (Object item : result) {
			data.milestones.add(parseMilestone((Map) getMultiCallResult(item)));
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
			data.versions.add(parseVersion((Map) getMultiCallResult(item)));
		}
		monitor.worked(1);
		if (monitor.isCanceled())
			throw new OperationCanceledException();
	}

	private TracComponent parseComponent(Map result) {
		TracComponent component = new TracComponent((String) result.get("name"));
		component.setOwner((String) result.get("owner"));
		component.setDescription((String) result.get("description"));
		return component;
	}

	private TracMilestone parseMilestone(Map result) {
		TracMilestone milestone = new TracMilestone((String) result.get("name"));
		milestone.setCompleted(TracUtils.parseDate((Integer) result.get("completed")));
		milestone.setDue(TracUtils.parseDate((Integer) result.get("due")));
		milestone.setDescription((String) result.get("description"));
		return milestone;
	}

	private TracVersion parseVersion(Map result) {
		TracVersion version = new TracVersion((String) result.get("name"));
		version.setTime(TracUtils.parseDate((Integer) result.get("time")));
		version.setDescription((String) result.get("description"));
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
		Object[] ids = (Object[]) call(attributeType + ".getAll");
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
				attribute.value = Integer.parseInt((String) getMultiCallResult(result[i]));
				attributes.add(attribute);
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

	public void createTicket(TracTicket ticket) throws TracException {
		Map<String, String> attributes = ticket.getValues();
		String summary = attributes.remove(Key.SUMMARY.getKey());
		String description = attributes.remove(Key.DESCRIPTION.getKey());
		if (summary == null || description == null) {
			throw new InvalidTicketException();
		}
		call("ticket.create", summary, description, attributes);
	}

	public void updateTicket(TracTicket ticket, String comment) throws TracException {
		Map<String, String> attributes = ticket.getValues();
		call("ticket.update", ticket.getId(), comment, attributes);
	}

	public Set<Integer> getChangedTickets(Date since) throws TracException {
		Object[] ids;
		try {
			ids = (Object[]) call("ticket.getRecentChanges", since);
		} catch (TracException e) {
			// TODO remove this once getRecentChanges is supported by the XmlRpcPlugin
			return null;
		}
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

}
