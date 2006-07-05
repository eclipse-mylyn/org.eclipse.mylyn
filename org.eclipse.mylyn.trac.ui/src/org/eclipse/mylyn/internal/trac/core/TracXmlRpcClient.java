package org.eclipse.mylar.internal.trac.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpc;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcClientException;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcTransport;
import org.apache.xmlrpc.XmlRpcTransportFactory;
import org.eclipse.mylar.internal.trac.MylarTracPlugin;
import org.eclipse.mylar.internal.trac.model.TracSearch;
import org.eclipse.mylar.internal.trac.model.TracTicket;

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

	public XmlRpcClient getClient() throws TracException {
		if (xmlrpc != null) {
			return xmlrpc;
		}

		// initialize XML-RPC library
		XmlRpc.setDefaultInputEncoding(ITracClient.CHARSET);

		try {
			String location = repositoryUrl.toString();
			if (hasAuthenticationCredentials()) {
				location += LOGIN_URL;
			}
			location += XMLRPC_URL;

			URL url = new URL(location);
			TransportFactory transport = new TransportFactory(url);
			xmlrpc = new XmlRpcClient(url, transport);
		} catch (Exception e) {
			throw new TracException(e);
		}

		return xmlrpc;
	}

	private Object call(String method, Object... parameters) throws TracException {
		getClient();

		Vector<Object> params = new Vector<Object>();
		for (Object parameter : parameters) {
			params.add(parameter);
		}

		try {
			return xmlrpc.execute(method, params);
		} catch (HttpException e) {
			if (e.responseCode == HttpURLConnection.HTTP_FORBIDDEN
					|| e.responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
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

	private Vector multicall(Hashtable<String, Object>... calls) throws TracException {
		Vector result = (Vector) call("system.multicall", new Object[] { calls });
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
		if (result instanceof Hashtable) {
			Hashtable exceptionData = (Hashtable) result;
			if (exceptionData.containsKey("faultCode") && exceptionData.containsKey("faultString")) {
				throw new XmlRpcException(Integer.parseInt(exceptionData.get("faultCode").toString()),
						(String) exceptionData.get("faultString"));
			}
		}
	}

	private Hashtable<String, Object> createMultiCall(String methodName, Object... parameters) throws TracException {
		Hashtable<String, Object> table = new Hashtable<String, Object>();
		table.put("methodName", methodName);
		table.put("params", parameters);
		return table;
	}

	private Object getMultiCallResult(Object item) {
		return ((Vector) item).get(0);
	}

	public void validate() throws TracException {
		Vector result = (Vector) call("system.listMethods");
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
		Vector result = (Vector) call("ticket.get", id);
		return parseTicket(result);
	}

	@SuppressWarnings("unchecked")
	public List<TracTicket> getTickets(int[] ids) throws TracException {
		Hashtable<String, Object>[] calls = new Hashtable[ids.length];
		for (int i = 0; i < calls.length; i++) {
			calls[i] = createMultiCall("ticket.get", ids[i]);
		}

		Vector result = multicall(calls);
		assert result.size() == ids.length;

		List<TracTicket> tickets = new ArrayList<TracTicket>(result.size());
		for (Object item : result) {
			Vector ticketResult = (Vector) getMultiCallResult(item);
			tickets.add(parseTicket(ticketResult));
		}

		return tickets;
	}

	@SuppressWarnings("unchecked")
	public void search(TracSearch query, List<TracTicket> tickets) throws TracException {
		// an empty query string is not valid, therefore prepend order
		Vector result = (Vector) call("ticket.query", "order=id" + query.toQuery());

		Hashtable<String, Object>[] calls = new Hashtable[result.size()];
		for (int i = 0; i < calls.length; i++) {
			calls[i] = createMultiCall("ticket.get", result.get(i));
		}
		result = multicall(calls);

		for (Object item : result) {
			Vector ticketResult = (Vector) getMultiCallResult(item);
			tickets.add(parseTicket(ticketResult));
		}
	}

	private TracTicket parseTicket(Vector result) throws InvalidTicketException {
		TracTicket ticket = new TracTicket((Integer) result.get(0));
		ticket.setCreated((Integer) result.get(1));
		ticket.setLastChanged((Integer) result.get(2));
		Hashtable attributes = (Hashtable) result.get(3);
		for (Object key : attributes.keySet()) {
			ticket.putTracValue(key.toString(), attributes.get(key).toString());
		}
		return ticket;
	}

	/**
	 * A custom transport factory used to establish XML-RPC connections. Uses
	 * the Eclipse proxy settings.
	 * 
	 * @author Steffen Pingel
	 */
	private class TransportFactory implements XmlRpcTransportFactory {

		private URL url;

		public TransportFactory(URL url) {
			assert url != null;

			this.url = url;
		}

		public XmlRpcTransport createTransport() throws XmlRpcClientException {
			return new XmlRpcTransport() {

				private HttpURLConnection connection;

				public void endClientRequest() throws XmlRpcClientException {
					assert connection != null;

					try {
						connection.getInputStream().close();
					} catch (Exception e) {
						throw new XmlRpcClientException("Exception closing connection", e);
					}
				}

				public InputStream sendXmlRpc(byte[] request) throws IOException, XmlRpcClientException {
					try {
						connection = MylarTracPlugin.getHttpConnection(url);
					} catch (Exception e) {
						throw new IOException(e.toString());
					}

					connection.setDoInput(true);
					connection.setDoOutput(true);
					connection.setUseCaches(false);
					connection.setAllowUserInteraction(false);

					connection.setRequestProperty("Content-Length", request.length + "");
					connection.setRequestProperty("Content-Type", "text/xml");
					if (hasAuthenticationCredentials()) {
						MylarTracPlugin.setAuthCredentials(connection, username, password);
					}

					OutputStream out = connection.getOutputStream();
					out.write(request);
					out.flush();
					out.close();

					connection.connect();
					int responseCode = connection.getResponseCode();
					if (responseCode != HttpURLConnection.HTTP_OK) {
						throw new HttpException(responseCode);
					}

					return connection.getInputStream();
				}

			};
		}

		public void setProperty(String key, Object value) {
			// ignore, this is never called by the library
		}

	}

	private class HttpException extends IOException {

		private static final long serialVersionUID = 7083228933121822248L;

		final int responseCode;

		public HttpException(int responseCode) {
			super("HTTP Error " + responseCode);

			this.responseCode = responseCode;
		}

	}

}
