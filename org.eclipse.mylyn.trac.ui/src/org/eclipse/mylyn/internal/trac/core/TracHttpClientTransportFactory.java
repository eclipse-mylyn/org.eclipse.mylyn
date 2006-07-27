/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.trac.core;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.util.zip.GZIPInputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcCommonsTransport;
import org.apache.xmlrpc.client.XmlRpcHttpClientConfig;
import org.apache.xmlrpc.client.XmlRpcTransport;
import org.apache.xmlrpc.client.XmlRpcTransportFactoryImpl;
import org.eclipse.mylar.internal.tasks.core.UrlConnectionUtil;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;

/**
 * A custom transport factory used to establish XML-RPC connections. Uses the
 * Mylar proxy settings.
 * 
 * @author Steffen Pingel
 */
public class TracHttpClientTransportFactory extends XmlRpcTransportFactoryImpl {

	public static class TracHttpException extends XmlRpcException {

		private static final long serialVersionUID = 9032521978140685830L;

		public TracHttpException(int responseCode) {
			super(responseCode, "HTTP Error " + responseCode);
		}

	}

	/**
	 * A transport that uses the Apache HttpClient library.
	 */
	public static class TracHttpClientTransport extends XmlRpcCommonsTransport {

		public TracHttpClientTransport(XmlRpcClient client) {
			super(client);

			XmlRpcHttpClientConfig config = (XmlRpcHttpClientConfig) client.getConfig();
			// this needs to be set to avoid exceptions
			getHttpClient().getParams().setAuthenticationPreemptive(config.getBasicUserName() != null);
		}

		public HttpClient getHttpClient() {
			return (HttpClient) getValue("client");
		}

		@Override
		protected InputStream getInputStream() throws XmlRpcException {
			int responseCode = getMethod().getStatusCode();
			if (responseCode != HttpURLConnection.HTTP_OK) {
				throw new TracHttpException(responseCode);
			}

			return super.getInputStream();
		}

		public PostMethod getMethod() {
			return (PostMethod) getValue("method");
		}

		public void setMethod(PostMethod method) {
			setValue("method", method);
		}

		private Object getValue(String name) {
			try {
				Field field = XmlRpcCommonsTransport.class.getDeclaredField(name);
				field.setAccessible(true);
				return field.get(this);
			} catch (Throwable t) {
				throw new RuntimeException("Internal error accessing HttpClient", t);
			}
		}

		private void setValue(String name, Object value) {
			try {
				Field field = XmlRpcCommonsTransport.class.getDeclaredField(name);
				field.setAccessible(true);
				field.set(this, value);
			} catch (Throwable t) {
				throw new RuntimeException("Internal error accessing HttpClient", t);
			}
		}

		/**
		 * Based on the implementation of XmlRpcCommonsTransport and its super classes.
		 */
		@Override
		public Object sendRequest(XmlRpcRequest pRequest) throws XmlRpcException {
			XmlRpcHttpClientConfig config = (XmlRpcHttpClientConfig) pRequest.getConfig();
			
			String url = config.getServerURL().toString();
			Proxy proxySettings = TasksUiPlugin.getDefault().getProxySettings();
			UrlConnectionUtil.setupHttpClient(getHttpClient(), proxySettings, url);
			
			PostMethod method = new PostMethod(UrlConnectionUtil.getRequestPath(url));
	        
	        if (config.getConnectionTimeout() != 0)
	            getHttpClient().getHttpConnectionManager().getParams().setConnectionTimeout(config.getConnectionTimeout());
	        
	        if (config.getReplyTimeout() != 0)
	        	getHttpClient().getHttpConnectionManager().getParams().setSoTimeout(config.getConnectionTimeout());
	        
			method.getParams().setVersion(HttpVersion.HTTP_1_1);

			setMethod(method);
			
			initHttpHeaders(pRequest);
			
			boolean closed = false;
			try {
				RequestWriter writer = newRequestWriter(pRequest);
				writeRequest(writer);
				InputStream istream = getInputStream();
				if (isResponseGzipCompressed(config)) {
					istream = new GZIPInputStream(istream);
				}
				Object result = readResponse(config, istream);
				closed = true;
				close();
				return result;
			} catch (IOException e) {
				throw new XmlRpcException("Failed to read servers response: "
						+ e.getMessage(), e);
			} finally {
				if (!closed) { try { close(); } catch (Throwable ignore) {} }
			}
		}

	}

	private final TracHttpClientTransport transport;

	public TracHttpClientTransportFactory(XmlRpcClient client) {
		super(client);

		transport = new TracHttpClientTransport(client);
	}

	public XmlRpcTransport getTransport() {
		return transport;
	}
}
