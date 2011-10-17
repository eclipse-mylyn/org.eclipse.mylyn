/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *    Christian Trutz             - HttpClient 4.1
 *******************************************************************************/
package org.eclipse.egit.github.core.client;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.apache.http.client.protocol.ClientContext.AUTH_CACHE;
import static org.eclipse.egit.github.core.client.IGitHubConstants.AUTH_TOKEN;
import static org.eclipse.egit.github.core.client.IGitHubConstants.CHARSET_UTF8;
import static org.eclipse.egit.github.core.client.IGitHubConstants.CONTENT_TYPE_JSON;
import static org.eclipse.egit.github.core.client.IGitHubConstants.HOST_API;
import static org.eclipse.egit.github.core.client.IGitHubConstants.PROTOCOL_HTTPS;
import static org.eclipse.egit.github.core.client.IGitHubConstants.SUBDOMAIN_API;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.ProxySelector;
import java.net.URL;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpMessage;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.ProxySelectorRoutePlanner;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.eclipse.egit.github.core.RequestError;

/**
 * Client class for interacting with GitHub HTTP/JSON API.
 */
public class GitHubClient {

	/**
	 * Create API client from URL.
	 *
	 * This creates an HTTPS-based client with a host that contains the host
	 * value of the given URL prefixed with 'api'.
	 *
	 * @param url
	 * @return client
	 */
	public static GitHubClient createClient(String url) {
		try {
			String host = new URL(url).getHost();
			host = SUBDOMAIN_API + "." + host; //$NON-NLS-1$
			return new GitHubClient(host);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private static final Header USER_AGENT = new BasicHeader(HTTP.USER_AGENT,
			"GitHubJava/1.2.0"); //$NON-NLS-1$

	private final HttpHost httpHost;

	private final HttpContext httpContext;

	private final DefaultHttpClient client = new DefaultHttpClient();

	private Header userAgent = USER_AGENT;

	private Gson gson = GsonUtils.getGson();

	/**
	 * Create default client
	 */
	public GitHubClient() {
		this(HOST_API);
	}

	/**
	 * Create client for host name
	 *
	 * @param hostname
	 */
	public GitHubClient(String hostname) {
		this(hostname, -1, PROTOCOL_HTTPS);
	}

	/**
	 * Create client for host, port, and scheme
	 *
	 * @param hostname
	 * @param port
	 * @param scheme
	 */
	public GitHubClient(String hostname, int port, String scheme) {
		this(new HttpHost(hostname, port, scheme));
	}

	/**
	 * Create client for host configuration
	 *
	 * @param httpHost
	 */
	public GitHubClient(HttpHost httpHost) {
		if (httpHost == null)
			throw new IllegalArgumentException("Http host cannot be null"); //$NON-NLS-1$

		this.httpHost = httpHost;

		// Support JVM configured proxy servers
		client.setRoutePlanner(new ProxySelectorRoutePlanner(client
				.getConnectionManager().getSchemeRegistry(), ProxySelector
				.getDefault()));

		// Preemptive authentication
		httpContext = new BasicHttpContext();
		AuthCache authCache = new BasicAuthCache();
		httpContext.setAttribute(AUTH_CACHE, authCache);
		client.addRequestInterceptor(new AuthInterceptor(), 0);
	}

	/**
	 * Set whether or not serialized data should include fields that are null.
	 *
	 * @param serializeNulls
	 * @return this client
	 */
	public GitHubClient setSerializeNulls(boolean serializeNulls) {
		gson = GsonUtils.getGson(serializeNulls);
		return this;
	}

	/**
	 * Set the value to set as the user agent header on every request created.
	 * Specifying a null or empty agent parameter will reset this client to use
	 * the default user agent header value.
	 *
	 * @param agent
	 * @return this client
	 */
	public GitHubClient setUserAgent(String agent) {
		if (agent != null && agent.length() > 0)
			userAgent = new BasicHeader(HTTP.USER_AGENT, agent);
		else
			userAgent = USER_AGENT;
		return this;
	}

	/**
	 * Configure request with standard headers
	 *
	 * @param request
	 * @return configured request
	 */
	protected <V extends HttpMessage> V configureRequest(V request) {
		request.addHeader(userAgent);
		return request;
	}

	/**
	 * Create standard post method
	 *
	 * @param uri
	 * @return post
	 */
	protected HttpPost createPost(String uri) {
		return configureRequest(new HttpPost(uri));
	}

	/**
	 * Create standard post method
	 *
	 * @param uri
	 * @return post
	 */
	protected HttpPut createPut(String uri) {
		return configureRequest(new HttpPut(uri));
	}

	/**
	 * Create get method
	 *
	 * @param uri
	 * @return get method
	 */
	protected HttpGet createGet(String uri) {
		return configureRequest(new HttpGet(uri));
	}

	/**
	 * Create delete method
	 *
	 * @param uri
	 * @return get method
	 */
	protected HttpDelete createDelete(String uri) {
		return configureRequest(new HttpDelete(uri));
	}

	/**
	 * Update credential on HTTP client credentials provider
	 *
	 * @param user
	 * @param password
	 * @return this client
	 */
	protected GitHubClient updateCredentials(String user, String password) {
		if (user != null && password != null)
			client.getCredentialsProvider().setCredentials(
					new AuthScope(httpHost.getHostName(), httpHost.getPort()),
					new UsernamePasswordCredentials(user, password));
		else
			client.getCredentialsProvider().clear();
		return this;
	}

	/**
	 * Set credentials
	 *
	 * @param user
	 * @param password
	 * @return this client
	 */
	public GitHubClient setCredentials(String user, String password) {
		updateCredentials(user, password);
		AuthCache authCache = (AuthCache) httpContext.getAttribute(AUTH_CACHE);
		authCache.put(httpHost, new BasicScheme());
		return this;
	}

	/**
	 * Set OAuth2 token
	 *
	 * @param token
	 * @return this client
	 */
	public GitHubClient setOAuth2Token(String token) {
		updateCredentials(AUTH_TOKEN, token);
		AuthCache authCache = (AuthCache) httpContext.getAttribute(AUTH_CACHE);
		authCache.put(httpHost, new OAuth2Scheme());
		return this;
	}

	/**
	 * Get the user that this client is currently authenticating as
	 *
	 * @return user or null if not authentication
	 */
	public String getUser() {
		Credentials credentials = client.getCredentialsProvider()
				.getCredentials(
						new AuthScope(httpHost.getHostName(), httpHost
								.getPort()));
		return credentials != null ? credentials.getUserPrincipal().getName()
				: null;
	}

	/**
	 * Parse JSON to specified type
	 *
	 * @param <V>
	 * @param response
	 * @param type
	 * @return type
	 * @throws IOException
	 */
	protected <V> V parseJson(HttpResponse response, Type type)
			throws IOException {
		InputStreamReader reader = new InputStreamReader(getStream(response),
				CHARSET_UTF8);
		try {
			return gson.fromJson(reader, type);
		} catch (JsonParseException jpe) {
			throw new IOException(jpe.getMessage());
		} finally {
			try {
				reader.close();
			} catch (IOException ignored) {
				// Ignored
			}
		}
	}

	/**
	 * Convert object to a JSON string
	 *
	 * @param object
	 * @return JSON string
	 * @throws IOException
	 */
	private String toJson(Object object) throws IOException {
		try {
			return gson.toJson(object);
		} catch (JsonParseException jpe) {
			throw new IOException(jpe.getMessage());
		}
	}

	/**
	 * Get {@link HttpEntity} from response
	 *
	 * @param response
	 * @return non-null entity
	 * @throws IOException
	 */
	protected HttpEntity getEntity(HttpResponse response) throws IOException {
		HttpEntity entity = response.getEntity();
		if (entity == null)
			throw new IOException("Response has no entity"); //$NON-NLS-1$
		return entity;
	}

	/**
	 * Get {@link InputStream} from response
	 *
	 * @param response
	 * @return non-null input stream
	 * @throws IOException
	 */
	protected InputStream getStream(HttpResponse response) throws IOException {
		InputStream stream = getEntity(response).getContent();
		if (stream == null)
			throw new IOException("Empty body"); //$NON-NLS-1$
		return stream;
	}

	/**
	 * Parse error from response
	 *
	 * @param response
	 * @return request error
	 * @throws IOException
	 */
	protected RequestError parseError(HttpResponse response) throws IOException {
		return parseJson(response, RequestError.class);
	}

	/**
	 * Create error exception from response and throw it
	 *
	 * @param response
	 * @param status
	 * @return non-null newly created {@link IOException}
	 */
	protected IOException createException(HttpResponse response,
			StatusLine status) {
		final int code = status.getStatusCode();
		switch (code) {
		case SC_BAD_REQUEST:
		case SC_UNAUTHORIZED:
		case SC_FORBIDDEN:
		case SC_NOT_FOUND:
		case SC_UNPROCESSABLE_ENTITY:
		case SC_INTERNAL_SERVER_ERROR:
			RequestError error;
			try {
				error = parseError(response);
			} catch (IOException e) {
				return e;
			}
			return new RequestException(error, code);
		default:
			return new IOException(status.getReasonPhrase());
		}
	}

	/**
	 * Is the response successful?
	 *
	 * @param response
	 * @param status
	 * @return true if okay, false otherwise
	 */
	protected boolean isOk(HttpResponse response, StatusLine status) {
		switch (status.getStatusCode()) {
		case SC_OK:
		case SC_CREATED:
			return true;
		default:
			return false;
		}
	}

	/**
	 * Is the response empty?
	 *
	 * @param response
	 * @param status
	 * @return true if empty, false otherwise
	 */
	protected boolean isEmpty(HttpResponse response, StatusLine status) {
		return SC_NO_CONTENT == status.getStatusCode();
	}

	/**
	 * Get status line from response
	 *
	 * @param response
	 * @return Non-null status line
	 * @throws IOException
	 */
	protected StatusLine getStatus(HttpResponse response) throws IOException {
		StatusLine statusLine = response.getStatusLine();
		if (statusLine == null)
			throw new IOException("Empty HTTP response status line"); //$NON-NLS-1$
		return statusLine;
	}

	/**
	 * Get response stream from URI. It is the responsibility of the calling
	 * method to close the returned stream.
	 *
	 * @param request
	 * @return stream
	 * @throws IOException
	 */
	public InputStream getStream(GitHubRequest request) throws IOException {
		HttpGet method = createGet(request.generateUri());
		HttpResponse response = client.execute(httpHost, method, httpContext);
		StatusLine status = getStatus(response);
		if (isOk(response, status))
			return getStream(response);
		throw createException(response, status);
	}

	/**
	 * Get response from URI and bind to specified type
	 *
	 * @param request
	 * @return response
	 * @throws IOException
	 */
	public GitHubResponse get(GitHubRequest request) throws IOException {
		HttpGet method = createGet(request.generateUri());
		HttpResponse response = client.execute(httpHost, method, httpContext);
		StatusLine status = getStatus(response);
		if (isOk(response, status)) {
			Object body = null;
			Type type = request.getType();
			if (type != null)
				body = parseJson(response, type);
			else
				EntityUtils.consume(response.getEntity());
			return new GitHubResponse(response, body);
		}
		if (isEmpty(response, status))
			return new GitHubResponse(response, null);
		throw createException(response, status);
	}

	/**
	 * Send JSON using specified method
	 *
	 * @param <V>
	 * @param method
	 * @param params
	 * @param type
	 * @return resource
	 * @throws IOException
	 */
	protected <V> V sendJson(HttpEntityEnclosingRequestBase method,
			Object params, Type type) throws IOException {
		if (params != null)
			method.setEntity(new StringEntity(toJson(params),
					CONTENT_TYPE_JSON, CHARSET_UTF8));
		HttpResponse response = client.execute(httpHost, method, httpContext);
		StatusLine status = getStatus(response);
		if (isOk(response, status)) {
			if (type != null)
				return parseJson(response, type);
			EntityUtils.consume(response.getEntity());
			return null;
		}
		if (isEmpty(response, status))
			return null;
		throw createException(response, status);
	}

	/**
	 * Post data to URI
	 *
	 * @param <V>
	 * @param uri
	 * @param params
	 * @param type
	 * @return response
	 * @throws IOException
	 */
	public <V> V post(String uri, Object params, Type type) throws IOException {
		return sendJson(createPost(uri), params, type);
	}

	/**
	 * Post to URI
	 *
	 * @param uri
	 * @throws IOException
	 */
	public void post(String uri) throws IOException {
		post(uri, null, null);
	}

	/**
	 * Put data to URI
	 *
	 * @param <V>
	 * @param uri
	 * @param params
	 * @param type
	 * @return response
	 * @throws IOException
	 */
	public <V> V put(String uri, Object params, Type type) throws IOException {
		return sendJson(createPut(uri), params, type);
	}

	/**
	 * Put to URI
	 *
	 * @param uri
	 * @throws IOException
	 */
	public void put(String uri) throws IOException {
		put(uri, null, null);
	}

	/**
	 * Delete resource at URI. This method will throw an {@link IOException}
	 * when the response status is not a 204 (No Content).
	 *
	 * @param uri
	 * @param params
	 * @throws IOException
	 */
	public void delete(String uri, Object params) throws IOException {
		HttpRequest method;
		if (params == null)
			method = createDelete(uri);
		else {
			EntityDeleteMethod delete = configureRequest(new EntityDeleteMethod(
					uri));
			delete.setEntity(new StringEntity(toJson(params),
					CONTENT_TYPE_JSON, CHARSET_UTF8));
			method = delete;
		}

		HttpResponse response = client.execute(httpHost, method, httpContext);
		StatusLine status = getStatus(response);
		if (!isEmpty(response, status))
			throw new RequestException(parseError(response),
					status.getStatusCode());
	}

	/**
	 * Delete resource at URI. This method will throw an {@link IOException}
	 * when the response status is not a 204 (No Content).
	 *
	 * @param uri
	 * @throws IOException
	 */
	public void delete(String uri) throws IOException {
		delete(uri, null);
	}
}
