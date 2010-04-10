/*******************************************************************************
 * Copyright (c) 2010 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core.service;

import java.io.BufferedOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.auth.AuthScheme;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientException;
import org.apache.xmlrpc.client.XmlRpcHttpClientConfig;
import org.apache.xmlrpc.client.XmlRpcHttpTransport;
import org.apache.xmlrpc.client.XmlRpcTransport;
import org.apache.xmlrpc.client.XmlRpcTransportFactory;
import org.apache.xmlrpc.common.XmlRpcStreamRequestConfig;
import org.apache.xmlrpc.util.HttpUtil;
import org.apache.xmlrpc.util.XmlRpcIOException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.xml.sax.SAXException;

/**
 * @author Frank Becker
 * @author Steffen Pingel
 */
public class BaseXMLRPCTransportFactory implements XmlRpcTransportFactory {
	/**
	 * A transport that uses the Apache HttpClient library.
	 */
	public static class BaseHttpClientTransport extends XmlRpcHttpTransport {

		private final HttpClient httpClient;

		private final AbstractWebLocation location;

		private PostMethod method;

		private int contentLength = -1;

		private XmlRpcHttpClientConfig config;

		private IProgressMonitor monitor;

		private HostConfiguration hostConfiguration;

		private final BaseHttpMethodInterceptor interceptor;

		public BaseHttpClientTransport(XmlRpcClient client, HttpClient httpClient, AbstractWebLocation location,
				BaseHttpMethodInterceptor interceptor) {
			super(client, ""); //$NON-NLS-1$
			this.httpClient = httpClient;
			this.location = location;
			this.interceptor = interceptor;
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
			if (responseCode != java.net.HttpURLConnection.HTTP_OK) {
				BaseHttpException e = new BaseHttpException(responseCode);
				if (responseCode == HttpStatus.SC_UNAUTHORIZED) {
					e.setAuthScheme(method.getHostAuthState().getAuthScheme());
				}
				throw e;
			}

			try {
				return method.getResponseBodyAsStream();
			} catch (HttpException e) {
				throw new XmlRpcClientException("Error in HTTP transport: " + e.getMessage(), e); //$NON-NLS-1$
			} catch (IOException e) {
				throw new XmlRpcClientException("I/O error in server communication: " + e.getMessage(), e); //$NON-NLS-1$
			}
		}

		@Override
		protected String getUserAgent() {
			return WebUtil.getUserAgent(""); //$NON-NLS-1$
		}

		@Override
		protected void initHttpHeaders(XmlRpcRequest request) throws XmlRpcClientException {
			config = (XmlRpcHttpClientConfig) request.getConfig();

			if (request instanceof BaseXmlRpcClientRequestImpl) {
				BaseXmlRpcClientRequestImpl repositoryRequest = (BaseXmlRpcClientRequestImpl) request;
				monitor = repositoryRequest.getProgressMonitor();
			} else {
				monitor = null;
			}

			String url = config.getServerURL().toString();
			hostConfiguration = WebUtil.createHostConfiguration(httpClient, location, monitor);
			method = new PostMethod(WebUtil.getRequestPath(url));

			super.initHttpHeaders(request);

			if (config.getConnectionTimeout() != 0) {
				httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(config.getConnectionTimeout());
			}

			if (config.getReplyTimeout() != 0) {
				httpClient.getHttpConnectionManager().getParams().setSoTimeout(config.getConnectionTimeout());
			}

			method.getParams().setVersion(HttpVersion.HTTP_1_1);

			if (interceptor != null) {
				interceptor.processRequest(method);
			}
		}

		@Override
		protected boolean isResponseGzipCompressed(XmlRpcStreamRequestConfig config) {
			Header header = method.getResponseHeader("Content-Encoding"); //$NON-NLS-1$
			return header != null && HttpUtil.isUsingGzipEncoding(header.getValue());
		}

		@Override
		protected void setContentLength(int contentLength) {
			this.contentLength = contentLength;
		}

		@Override
		protected void setCredentials(XmlRpcHttpClientConfig config) throws XmlRpcClientException {
			// handled by TracXmlRpcClient
		}

		@Override
		protected void setRequestHeader(String header, String value) {
			method.setRequestHeader(new Header(header, value));
		}

		@Override
		protected void writeRequest(final ReqWriter writer) throws XmlRpcException {
			method.setRequestEntity(new RequestEntity() {
				public long getContentLength() {
					return BaseHttpClientTransport.this.getContentLength();
				}

				public String getContentType() {
					return "text/xml"; //$NON-NLS-1$
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
				if (interceptor != null) {
					interceptor.processResponse(method);
				}
			} catch (XmlRpcIOException e) {
				Throwable t = e.getLinkedException();
				if (t instanceof XmlRpcException) {
					throw (XmlRpcException) t;
				} else {
					throw new XmlRpcException("Unexpected exception: " + t.getMessage(), t); //$NON-NLS-1$
				}
			} catch (IOException e) {
				throw new XmlRpcException("I/O error while communicating with HTTP server: " + e.getMessage(), e); //$NON-NLS-1$
			}
		}

	}

	public static class BaseHttpException extends XmlRpcException {

		private static final long serialVersionUID = -6088919818534852787L;

		private AuthScheme authScheme;

		public BaseHttpException(int responseCode) {
			super(responseCode, "HTTP Error " + responseCode); //$NON-NLS-1$
		}

		public AuthScheme getAuthScheme() {
			return authScheme;
		}

		public void setAuthScheme(AuthScheme authScheme) {
			this.authScheme = authScheme;
		}

	}

	private final XmlRpcClient xmlRpcClient;

	private AbstractWebLocation location;

	private final HttpClient httpClient;

	private BaseHttpMethodInterceptor interceptor;

	public BaseXMLRPCTransportFactory(XmlRpcClient xmlRpcClient, HttpClient httpClient) {
		this.xmlRpcClient = xmlRpcClient;
		this.httpClient = httpClient;
	}

	public AbstractWebLocation getLocation() {
		return location;
	}

	public XmlRpcTransport getTransport() {
		return new BaseHttpClientTransport(xmlRpcClient, httpClient, location, interceptor);
	}

	public void setLocation(AbstractWebLocation location) {
		this.location = location;
	}

	public BaseHttpMethodInterceptor getInterceptor() {
		return interceptor;
	}

	public void setInterceptor(BaseHttpMethodInterceptor interceptor) {
		this.interceptor = interceptor;
	}

}
