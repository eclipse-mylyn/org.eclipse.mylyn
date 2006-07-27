package org.eclipse.mylar.internal.trac.core;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.internal.trac.core.TracHttpClientTransportFactory.TracHttpException;
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
			if (e.code == HttpURLConnection.HTTP_FORBIDDEN
					|| e.code == HttpURLConnection.HTTP_UNAUTHORIZED) {
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
		return parseTicket(result);
	}

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
			ticket.putTracValue(key.toString(), attributes.get(key).toString());
		}
		return ticket;
	}

	public synchronized void updateAttributes(IProgressMonitor monitor) throws TracException {
		monitor.beginTask("Updating attributes", 8);

		Object[] result = getAttributes("ticket.component");
		components = new ArrayList<TracComponent>(result.length);
		for (Object item : result) {
			components.add(parseComponent((Map) getMultiCallResult(item)));
		}
		monitor.worked(1);
		if (monitor.isCanceled())
			throw new OperationCanceledException();

		result = getAttributes("ticket.milestone");
		milestones = new ArrayList<TracMilestone>(result.length);
		for (Object item : result) {
			milestones.add(parseMilestone((Map) getMultiCallResult(item)));
		}
		monitor.worked(1);
		if (monitor.isCanceled())
			throw new OperationCanceledException();

		List<TicketAttributeResult> attributes = getTicketAttributes("ticket.priority");
		priorities = new ArrayList<TracPriority>(result.length);
		for (TicketAttributeResult attribute : attributes) {
			priorities.add(new TracPriority(attribute.name, attribute.value));
		}
		Collections.sort(priorities);
		monitor.worked(1);
		if (monitor.isCanceled())
			throw new OperationCanceledException();

		attributes = getTicketAttributes("ticket.resolution");
		ticketResolutions = new ArrayList<TracTicketResolution>(result.length);
		for (TicketAttributeResult attribute : attributes) {
			ticketResolutions.add(new TracTicketResolution(attribute.name, attribute.value));
		}
		Collections.sort(ticketResolutions);
		monitor.worked(1);
		if (monitor.isCanceled())
			throw new OperationCanceledException();

		attributes = getTicketAttributes("ticket.severity");
		severities = new ArrayList<TracSeverity>(result.length);
		for (TicketAttributeResult attribute : attributes) {
			severities.add(new TracSeverity(attribute.name, attribute.value));
		}
		Collections.sort(severities);
		monitor.worked(1);
		if (monitor.isCanceled())
			throw new OperationCanceledException();

		attributes = getTicketAttributes("ticket.status");
		ticketStatus = new ArrayList<TracTicketStatus>(result.length);
		for (TicketAttributeResult attribute : attributes) {
			ticketStatus.add(new TracTicketStatus(attribute.name, attribute.value));
		}
		Collections.sort(ticketStatus);
		monitor.worked(1);
		if (monitor.isCanceled())
			throw new OperationCanceledException();

		attributes = getTicketAttributes("ticket.type");
		ticketTypes = new ArrayList<TracTicketType>(result.length);
		for (TicketAttributeResult attribute : attributes) {
			ticketTypes.add(new TracTicketType(attribute.name, attribute.value));
		}
		Collections.sort(ticketTypes);
		monitor.worked(1);
		if (monitor.isCanceled())
			throw new OperationCanceledException();

		result = getAttributes("ticket.version");
		versions = new ArrayList<TracVersion>(result.length);
		for (Object item : result) {
			versions.add(parseVersion((Map) getMultiCallResult(item)));
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

	private class TicketAttributeResult {

		String name;

		int value;

	}

}
