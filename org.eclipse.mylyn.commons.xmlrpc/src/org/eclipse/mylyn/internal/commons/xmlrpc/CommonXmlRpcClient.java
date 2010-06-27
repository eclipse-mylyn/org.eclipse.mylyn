/*******************************************************************************
 * Copyright (c) 2010 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.xmlrpc;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.TimeZone;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.auth.AuthScheme;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.auth.DigestScheme;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.serializer.CharSetXmlWriterFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.eclipse.osgi.util.NLS;

/**
 * Facilitates connections to repositories accessed through XML-RPC.
 * 
 * @author Steffen Pingel
 */
public class CommonXmlRpcClient {

	static final boolean DEBUG_AUTH = Boolean.valueOf(Platform.getDebugOption("org.eclipse.mylyn.commons.xmlrpc/debug/authentication")); //$NON-NLS-1$

	static final boolean DEBUG_XMLRPC = Boolean.valueOf(Platform.getDebugOption("org.eclipse.mylyn.commons.xmlrpc/debug/xmlrpc")); //$NON-NLS-1$

	private static final String DEFAULT_CHARSET = "UTF-8"; //$NON-NLS-1$

	private static final String DEFAULT_TIME_ZONE = TimeZone.getDefault().getID();

	private static final String DEFAULT_USER_AGENT = "Apache XML-RPC/3.0"; //$NON-NLS-1$

	private static final String DEFAULT_CONTENT_TYPE = "text/xml"; //$NON-NLS-1$

	private static HttpClient createHttpClient(String userAgent) {
		HttpClient httpClient = new HttpClient();
		httpClient.setHttpConnectionManager(WebUtil.getConnectionManager());
		httpClient.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
		WebUtil.configureHttpClient(httpClient, userAgent);
		return httpClient;
	}

	private final AuthScope authScope;

	private XmlRpcClientConfigImpl config;

	volatile DigestScheme digestScheme;

	private HttpClientTransportFactory factory;

	final HttpClient httpClient;

	private final AbstractWebLocation location;

//	private boolean probed;

	private XmlRpcClient xmlrpc;

	private volatile boolean contentTypeCheckingEnabled;

	public CommonXmlRpcClient(AbstractWebLocation location) {
		this(location, createHttpClient(DEFAULT_USER_AGENT));
	}

	public CommonXmlRpcClient(AbstractWebLocation location, HttpClient client) {
		this.location = location;
		this.httpClient = createHttpClient(DEFAULT_USER_AGENT);
		this.authScope = new AuthScope(WebUtil.getHost(location.getUrl()), WebUtil.getPort(location.getUrl()), null,
				AuthScope.ANY_SCHEME);
	}

	public <T> T call(final IProgressMonitor monitor, final String method, final Object... parameters)
			throws XmlRpcException {
		return new XmlRpcOperation<T>(this) {
			@SuppressWarnings("unchecked")
			@Override
			public T execute() throws XmlRpcException {
				return (T) call(monitor, method, parameters);
			}
		}.execute();
	}

	public MulticallResult call(final IProgressMonitor monitor, final Multicall call) throws XmlRpcException {
		return new XmlRpcOperation<MulticallResult>(this) {
			@Override
			public MulticallResult execute() throws XmlRpcException {
				return call(monitor, call);
			}
		}.execute();
	}

	protected void createXmlRpcClient() {
		config = new XmlRpcClientConfigImpl();
		config.setEncoding(DEFAULT_CHARSET);
		config.setTimeZone(TimeZone.getTimeZone(DEFAULT_TIME_ZONE));
		config.setContentLengthOptional(false);
		config.setConnectionTimeout(WebUtil.getConnectionTimeout());
		config.setReplyTimeout(WebUtil.getSocketTimeout());

		xmlrpc = new XmlRpcClient();
		xmlrpc.setConfig(config);
		// bug 307200: force factory that supports proper UTF-8 encoding
		xmlrpc.setXmlWriterFactory(new CharSetXmlWriterFactory());

		factory = new HttpClientTransportFactory(xmlrpc, httpClient);
		factory.setLocation(location);
		factory.setInterceptor(new HttpMethodInterceptor() {
			public void processRequest(HttpMethod method) {
				DigestScheme scheme = digestScheme;
				if (scheme != null) {
					if (DEBUG_AUTH) {
						System.err.println(location.getUrl() + ": Digest scheme is present"); //$NON-NLS-1$ 
					}
					Credentials creds = httpClient.getState().getCredentials(authScope);
					if (creds != null) {
						if (DEBUG_AUTH) {
							System.err.println(location.getUrl() + ": Setting digest scheme for request"); //$NON-NLS-1$ 
						}
						method.getHostAuthState().setAuthScheme(digestScheme);
						method.getHostAuthState().setAuthRequested(true);
					}
				}
			}

			@SuppressWarnings("null")
			public void processResponse(HttpMethod method) throws XmlRpcException {
				if (isContentTypeCheckingEnabled()) {
					Header contentTypeHeader = method.getResponseHeader("Content-Type"); //$NON-NLS-1$
					if (contentTypeHeader == null || !DEFAULT_CONTENT_TYPE.equals(contentTypeHeader.getValue())) {
						throw new XmlRpcIllegalContentTypeException(
								NLS.bind(
										"The server returned an unexpected content type: ''{0}''", contentTypeHeader.getValue()), contentTypeHeader.getValue()); //$NON-NLS-1$
					}
				}
				AuthScheme authScheme = method.getHostAuthState().getAuthScheme();
				if (authScheme instanceof DigestScheme) {
					digestScheme = (DigestScheme) authScheme;
					if (DEBUG_AUTH) {
						System.err.println(location.getUrl() + ": Received digest scheme"); //$NON-NLS-1$ 
					}
				}
			}
		});
		xmlrpc.setTransportFactory(factory);

		try {
			config.setServerURL(new URL(location.getUrl()));
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public synchronized XmlRpcClient getClient() {
		if (xmlrpc == null) {
			createXmlRpcClient();
		}

		return xmlrpc;
	}

	public HttpClient getHttpClient() {
		return httpClient;
	}

	public AbstractWebLocation getLocation() {
		return location;
	}

//	public boolean isProbed() {
//		return probed;
//	}
//
//	public void setProbed(boolean probed) {
//		this.probed = probed;
//	}

	AuthenticationCredentials updateCredentials() {
		// update configuration with latest values
		AuthenticationCredentials credentials = location.getCredentials(AuthenticationType.REPOSITORY);
		if (credentials != null) {
			Credentials httpCredentials = WebUtil.getHttpClientCredentials(credentials,
					WebUtil.getHost(location.getUrl()));
			httpClient.getState().setCredentials(authScope, httpCredentials);
//			if (CoreUtil.TEST_MODE) {
//				System.err.println(" Setting credentials: " + httpCredentials); //$NON-NLS-1$
//			}
			httpClient.getState().setCredentials(authScope, httpCredentials);
		} else {
			httpClient.getState().clearCredentials();
		}
		return credentials;
	}

	public boolean isContentTypeCheckingEnabled() {
		return contentTypeCheckingEnabled;
	}

	public void setContentTypeCheckingEnabled(boolean contentTypeCheckingEnabled) {
		this.contentTypeCheckingEnabled = contentTypeCheckingEnabled;
	}

}