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

package org.eclipse.mylyn.internal.trac.core.util;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.Proxy;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientException;
import org.apache.xmlrpc.client.XmlRpcCommonsTransport;
import org.apache.xmlrpc.client.XmlRpcHttpClientConfig;
import org.apache.xmlrpc.client.XmlRpcTransport;
import org.apache.xmlrpc.client.XmlRpcTransportFactory;
import org.eclipse.mylyn.web.core.WebClientUtil;

/**
 * A custom transport factory used to establish XML-RPC connections. Uses the Mylar proxy settings.
 * 
 * @author Steffen Pingel
 */
public class TracHttpClientTransportFactory implements XmlRpcTransportFactory {

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

		private Proxy proxy;

		private Cookie[] cookies;

		public TracHttpClientTransport(XmlRpcClient client, Proxy proxy, Cookie[] cookies) {
			super(client);

			this.proxy = proxy;
			this.cookies = cookies;

			XmlRpcHttpClientConfig config = (XmlRpcHttpClientConfig) client.getConfig();
			// this needs to be set to avoid exceptions
			getHttpClient().getParams().setAuthenticationPreemptive(config.getBasicUserName() != null);
		}

		@Override
		protected String getUserAgent() {
			return WebClientUtil.USER_AGENT;
		}

		public HttpClient getHttpClient() {
			return (HttpClient) getValue("client");
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
				throw new RuntimeException("Internal error accessing field: " + name, t);
			}
		}

		private void setValue(String name, Object value) {
			try {
				Field field = XmlRpcCommonsTransport.class.getDeclaredField(name);
				field.setAccessible(true);
				field.set(this, value);
			} catch (Throwable t) {
				throw new RuntimeException("Internal error accessing field: " + name, t);
			}
		}

		@Override
		protected InputStream getInputStream() throws XmlRpcException {
			int responseCode = getMethod().getStatusCode();
			if (responseCode != HttpURLConnection.HTTP_OK) {
				throw new TracHttpException(responseCode);
			}

			return super.getInputStream();
		}

		@Override
		protected void initHttpHeaders(XmlRpcRequest request) throws XmlRpcClientException {
			// super call needed to initialize private fields of XmlRpcCommonsTransport
			super.initHttpHeaders(request);

			// The super method sets a private field that contains the
			// HttpClient Method object which is initialized using the wrong url.			
			// Since the URL can not be modified once the Method object has been
			// constructed a new object is constructed here, initialized and
			// assigned to the private field

			XmlRpcHttpClientConfig config = (XmlRpcHttpClientConfig) request.getConfig();

			String url = config.getServerURL().toString();
			WebClientUtil.setupHttpClient(getHttpClient(), proxy, url, null, null);
			if (cookies != null) {
				getHttpClient().getState().addCookies(cookies);
			}

			PostMethod method = new PostMethod(WebClientUtil.getRequestPath(url));
			setMethod(method);

			setRequestHeader("Content-Type", "text/xml");
			setRequestHeader("User-Agent", getUserAgent());
			setCredentials(config);
			setCompressionHeaders(config);

			if (config.getConnectionTimeout() != 0)
				getHttpClient().getHttpConnectionManager().getParams().setConnectionTimeout(
						config.getConnectionTimeout());

			if (config.getReplyTimeout() != 0)
				getHttpClient().getHttpConnectionManager().getParams().setSoTimeout(config.getConnectionTimeout());

			method.getParams().setVersion(HttpVersion.HTTP_1_1);
		}

	}

	private XmlRpcClient client;

	private Proxy proxy;

	private Cookie[] cookies;

	public TracHttpClientTransportFactory(XmlRpcClient client) {
		this.client = client;
	}

	public XmlRpcTransport getTransport() {
		return new TracHttpClientTransport(client, proxy, cookies);
	}

	public Proxy getProxy() {
		return proxy;
	}

	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
	}

	public Cookie[] getCookies() {
		return cookies;
	}

	public void setCookies(Cookie[] cookies) {
		this.cookies = cookies;
	}

}
