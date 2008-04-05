/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core.util;

import java.io.BufferedOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientException;
import org.apache.xmlrpc.client.XmlRpcHttpClientConfig;
import org.apache.xmlrpc.client.XmlRpcHttpTransport;
import org.apache.xmlrpc.client.XmlRpcTransport;
import org.apache.xmlrpc.client.XmlRpcTransportFactory;
import org.apache.xmlrpc.common.XmlRpcStreamConfig;
import org.apache.xmlrpc.common.XmlRpcStreamRequestConfig;
import org.apache.xmlrpc.util.HttpUtil;
import org.apache.xmlrpc.util.XmlRpcIOException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.web.core.AbstractWebLocation;
import org.eclipse.mylyn.web.core.WebUtil;
import org.xml.sax.SAXException;

/**
 * A custom transport factory used to establish XML-RPC connections. Uses the Mylyn proxy settings.
 * 
 * @author Steffen Pingel
 */
public class TracHttpClientTransportFactory implements XmlRpcTransportFactory {

	/**
	 * A transport that uses the Apache HttpClient library.
	 */
	public static class TracHttpClientTransport extends XmlRpcHttpTransport {

		private final HttpClient httpClient;

		private final AbstractWebLocation location;

		private final Cookie[] cookies;

		private PostMethod method;

		private int contentLength = -1;

		private XmlRpcHttpClientConfig config;

		private IProgressMonitor monitor;

		private HostConfiguration hostConfiguration;

		public TracHttpClientTransport(XmlRpcClient client, HttpClient httpClient, AbstractWebLocation location,
				Cookie[] cookies) {
			super(client, "");

			this.httpClient = httpClient;
			this.location = location;
			this.cookies = cookies;
		}

		@Override
		protected void close() throws XmlRpcClientException {
			method.releaseConnection();
		}

		public int getContentLength() {
			return contentLength;
		}

		@Override
		protected InputStream getInputStream() throws XmlRpcException {
			int responseCode = method.getStatusCode();
			if (responseCode != HttpURLConnection.HTTP_OK) {
				throw new TracHttpException(responseCode);
			}

			try {
				return method.getResponseBodyAsStream();
			} catch (HttpException e) {
				throw new XmlRpcClientException("Error in HTTP transport: " + e.getMessage(), e);
			} catch (IOException e) {
				throw new XmlRpcClientException("I/O error in server communication: " + e.getMessage(), e);
			}
		}

		@Override
		protected String getUserAgent() {
			return WebUtil.getUserAgent("");
		}

		@Override
		protected void initHttpHeaders(XmlRpcRequest request) throws XmlRpcClientException {
			config = (XmlRpcHttpClientConfig) request.getConfig();

			if (request instanceof TracXmlRpcClientRequest) {
				TracXmlRpcClientRequest tracRequest = (TracXmlRpcClientRequest) request;
				monitor = tracRequest.getProgressMonitor();
			} else {
				monitor = null;
			}

			String url = config.getServerURL().toString();
			hostConfiguration = WebUtil.createHostConfiguration(httpClient, location, monitor);
			if (cookies != null) {
				httpClient.getState().addCookies(cookies);
			}
			method = new PostMethod(WebUtil.getRequestPath(url));

			super.initHttpHeaders(request);

			if (config.getConnectionTimeout() != 0) {
				httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(config.getConnectionTimeout());
			}

			if (config.getReplyTimeout() != 0) {
				httpClient.getHttpConnectionManager().getParams().setSoTimeout(config.getConnectionTimeout());
			}

			method.getParams().setVersion(HttpVersion.HTTP_1_1);
		}

		@Override
		protected boolean isResponseGzipCompressed(XmlRpcStreamRequestConfig config) {
			Header header = method.getResponseHeader("Content-Encoding");
			return header != null && HttpUtil.isUsingGzipEncoding(header.getValue());
		}

		@Override
		protected void setContentLength(int contentLength) {
			this.contentLength = contentLength;
		}

		@Override
		protected void setCredentials(XmlRpcHttpClientConfig config) throws XmlRpcClientException {
			String userName = config.getBasicUserName();
			if (userName != null) {
				String encoding = config.getBasicEncoding();
				if (encoding == null) {
					encoding = XmlRpcStreamConfig.UTF8_ENCODING;
				}
				httpClient.getParams().setParameter(HttpMethodParams.CREDENTIAL_CHARSET, encoding);
				Credentials creds = new UsernamePasswordCredentials(userName, config.getBasicPassword());
				AuthScope scope = new AuthScope(null, AuthScope.ANY_PORT, null, AuthScope.ANY_SCHEME);
				httpClient.getState().setCredentials(scope, creds);
				httpClient.getParams().setAuthenticationPreemptive(true);
			}
		}

		@Override
		protected void setRequestHeader(String header, String value) {
			method.setRequestHeader(new Header(header, value));
		}

		@Override
		protected void writeRequest(final ReqWriter writer) throws XmlRpcException {
			method.setRequestEntity(new RequestEntity() {
				public long getContentLength() {
					return TracHttpClientTransport.this.getContentLength();
				}

				public String getContentType() {
					return "text/xml";
				}

				public boolean isRepeatable() {
					return getContentLength() != -1;
				}

				public void writeRequest(OutputStream pOut) throws IOException {
					try {
						/* Make sure, that the socket is not closed by replacing it with our
						 * own BufferedOutputStream.
						 */
						OutputStream ostream;
						if (isUsingByteArrayOutput(config)) {
							// No need to buffer the output.
							ostream = new FilterOutputStream(pOut) {
								@Override
								public void close() throws IOException {
									flush();
								}
							};
						} else {
							ostream = new BufferedOutputStream(pOut) {
								@Override
								public void close() throws IOException {
									flush();
								}
							};
						}
						writer.write(ostream);
					} catch (XmlRpcException e) {
						throw new XmlRpcIOException(e);
					} catch (SAXException e) {
						throw new XmlRpcIOException(e);
					}
				}
			});

			try {
				WebUtil.execute(httpClient, hostConfiguration, method, monitor);
			} catch (XmlRpcIOException e) {
				Throwable t = e.getLinkedException();
				if (t instanceof XmlRpcException) {
					throw (XmlRpcException) t;
				} else {
					throw new XmlRpcException("Unexpected exception: " + t.getMessage(), t);
				}
			} catch (IOException e) {
				throw new XmlRpcException("I/O error while communicating with HTTP server: " + e.getMessage(), e);
			}
		}

	}

	public static class TracHttpException extends XmlRpcException {

		private static final long serialVersionUID = 9032521978140685830L;

		public TracHttpException(int responseCode) {
			super(responseCode, "HTTP Error " + responseCode);
		}

	}

	protected static final String USER_AGENT = "TracConnector Apache XML-RPC/3.0";

	private final XmlRpcClient xmlRpcClient;

	private Cookie[] cookies;

	private AbstractWebLocation location;

	private final HttpClient httpClient;

	public TracHttpClientTransportFactory(XmlRpcClient xmlRpcClient, HttpClient httpClient) {
		this.xmlRpcClient = xmlRpcClient;
		this.httpClient = httpClient;
	}

	public Cookie[] getCookies() {
		return cookies;
	}

	public AbstractWebLocation getLocation() {
		return location;
	}

	public XmlRpcTransport getTransport() {
		return new TracHttpClientTransport(xmlRpcClient, httpClient, location, cookies);
	}

	public void setCookies(Cookie[] cookies) {
		this.cookies = cookies;
	}

	public void setLocation(AbstractWebLocation location) {
		this.location = location;
	}

}
