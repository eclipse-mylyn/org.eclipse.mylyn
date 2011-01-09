/*********************************************************************
 * Copyright (c) 2010 Sony Ericsson/ST Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *********************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.commons.net.WebUtil;

/**
 * Abstract class that handles the http communications with the Gerrit server.
 * 
 * @author Daniel Olsson, ST Ericsson
 * @author Tomas Westling, Sony Ericsson - thomas.westling@sonyericsson.com
 */
public class GerritHttpClient {

	private String host; // server adress

	private HttpClient httpClient;

	private int id = 1;

	private final String password;

	private final String path;

	private int port;

	private String schema; // http, https

	private final String user;

	private Cookie xsrfKey;

	/**
	 * Constructor.
	 * 
	 * @param schema
	 *            The schema to use i.e http or https
	 * @param host
	 *            The server address to the Gerrit host
	 * @param path
	 * @param port
	 *            The port that the communication should be relayed over
	 */
	public GerritHttpClient(String schema, String host, String path, int port, String user, String password) {
		this.schema = schema;
		this.host = host;
		this.path = path;
		this.port = port;
		this.user = user;
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	/**
	 * @throws GerritException
	 *             if something goes wrong with the connection.
	 */
	public HttpClient getHttpClient() throws GerritException {
		if (httpClient == null) {
			httpClient = new HttpClient();
			PostMethod method = new PostMethod(getURL() + "/gerrit/rpc/UserPassAuthService");
			method.setRequestBody("{\"jsonrpc\":\"2.0\",\"method\":\"authenticate\",\"params\":[\"" + user + "\",\""
					+ password + "\"],\"id\":3}");
			method.addRequestHeader("content-type", "	application/json; charset=utf-8");
			method.setRequestHeader("Accept", "application/json,application/json,application/jsonrequest");
			try {
				HttpClientParams params = new HttpClientParams();
				params.setCookiePolicy(org.apache.commons.httpclient.cookie.CookiePolicy.BROWSER_COMPATIBILITY);
				httpClient.setParams(params);
				
				HostConfiguration hostConfiguration = getHostConfiguration();
				WebUtil.execute(httpClient, hostConfiguration, method, new NullProgressMonitor());
				
				httpClient.setParams(params);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}

		return httpClient;
	}

	public int getId() {
		return id++;
	}

	public int getPort() {
		return port;
	}

	public String getSchema() {
		return schema;
	}

	public String getURL() {
		return schema + "://" + host + ":" + port + path;
	}

	/**
	 * Fetch the xsrfKey which is a required parameter for most requests.
	 * 
	 * @return the XsrfKey
	 * @throws GerritException
	 */
	public synchronized String getXsrfKey() throws GerritException {
		if (user == null || password == null) 
			return null;
		if (xsrfKey == null || xsrfKey.isExpired()) {
			updateXsrfKey();
		}
		return xsrfKey.getValue();
	}

	/**
	 * Send a JSON request to the Gerrit server.
	 * 
	 * @return The JSON response
	 * @throws GerritException
	 */
	public String postJsonRequest(String serviceUri, String message) throws GerritException {

		// Create a method instance
		PostMethod postMethod = new PostMethod(getURL() + serviceUri);
		postMethod.setRequestHeader("Content-Type", "application/json; charset=utf-8");
		postMethod.setRequestHeader("Accept", "application/json");

		try {
			RequestEntity requestEntity = new StringRequestEntity(message.toString(), "application/json", null);
			postMethod.setRequestEntity(requestEntity);

			// Execute the method.
			HostConfiguration hostConfiguration = getHostConfiguration();
			int statusCode = WebUtil.execute(httpClient, hostConfiguration, postMethod, new NullProgressMonitor());

			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + postMethod.getStatusLine() + "\n"
						+ postMethod.getResponseBodyAsString());
				throw new GerritException();
			}

			// Release the connection.
			String retString = postMethod.getResponseBodyAsString();
			return retString;

		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			throw new GerritException();
		} catch (HttpException e) {
			System.err.println("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
			throw new GerritException();
		} catch (IOException e) {
			System.err.println("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
			throw new GerritException();
		} finally {
			postMethod.releaseConnection();
		}
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	/**
	 * Updates the Xsrf key which is needed in all methods where login to the Gerrit server is needed.
	 * 
	 * @throws GerritException
	 *             if either the connection fails or an error message from the server is received.
	 */
	private void updateXsrfKey() throws GerritException {
		HttpClient client = getHttpClient();
		GetMethod getMethod = new GetMethod(getURL() + "/#mine");
		try {
			// Execute the method.
			// The code below where we first connect to /#mine, release it
			// and then connect to /login/mine
			// is needed for the connection to our internal Gerrit server
			// and will probably not work
			// towards review.source.android.com.
			
			HostConfiguration hostConfiguration = getHostConfiguration();
			int statusCode = WebUtil.execute(httpClient, hostConfiguration, getMethod, new NullProgressMonitor());
			getMethod.releaseConnection();
			
			getMethod = new GetMethod(getURL() + "/login/mine");
			statusCode = WebUtil.execute(httpClient, hostConfiguration, getMethod, new NullProgressMonitor());
			Cookie[] cookies = client.getState().getCookies();
			for (Cookie c : cookies) {
				if (c.getName().equals("GerritAccount")) {
					xsrfKey = c;
					break;
				}
			}
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + getMethod.getStatusLine() + "\n"
						+ getMethod.getResponseBodyAsString());
				throw new GerritException();
			}

		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			throw new GerritException();
		} catch (HttpException e) {
			System.err.println("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
			throw new GerritException();
		} catch (IOException e) {
			System.err.println("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
			throw new GerritException();
		} finally {
			// Release the connection.
			getMethod.releaseConnection();
		}
	}

	private HostConfiguration getHostConfiguration() throws GerritException {
		WebLocation location = new WebLocation(getURL());
		if (user != null && password != null)
			location.setCredentials(AuthenticationType.HTTP, user, password);
		HostConfiguration hostConfiguration = WebUtil.createHostConfiguration(getHttpClient(), location, new NullProgressMonitor());
		return hostConfiguration;
	}

}
