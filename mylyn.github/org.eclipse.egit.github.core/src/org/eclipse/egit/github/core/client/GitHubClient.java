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

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
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
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.eclipse.egit.github.core.Assert;
import org.eclipse.egit.github.core.RequestError;

/**
 * Client class for interacting with GitHub HTTP/JSON API.
 */
public class GitHubClient {

	private final HttpHost httpHost;

	private final HttpContext httpContext;

	private final DefaultHttpClient client = new DefaultHttpClient();

	private final Gson gson = new GsonBuilder()
			.registerTypeAdapter(Date.class, new DateFormatter())
			.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
			.serializeNulls().create();

	/**
	 * Create default client
	 */
	public GitHubClient() {
		this(IGitHubConstants.HOST_API, -1, IGitHubConstants.PROTOCOL_HTTPS);
	}

	/**
	 * Create client for host configuration
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
		Assert.notNull("Http host cannot be null", httpHost); //$NON-NLS-1$
		this.httpHost = httpHost;

		// Preemptive authentication
		httpContext = new BasicHttpContext();
		AuthCache authCache = new BasicAuthCache();
		authCache.put(this.httpHost, new BasicScheme());
		httpContext.setAttribute(ClientContext.AUTH_CACHE, authCache);
	}

	/**
	 * Create standard post method
	 * 
	 * @param uri
	 * @return post
	 */
	protected HttpPost createPost(String uri) {
		return new HttpPost(uri);
	}

	/**
	 * Create standard post method
	 * 
	 * @param uri
	 * @return post
	 */
	protected HttpPut createPut(String uri) {
		return new HttpPut(uri);
	}

	/**
	 * Create get method
	 * 
	 * @param uri
	 * @return get method
	 */
	protected HttpGet createGet(String uri) {
		return new HttpGet(uri);
	}

	/**
	 * Create delete method
	 * 
	 * @param uri
	 * @return get method
	 */
	protected HttpDelete createDelete(String uri) {
		return new HttpDelete(uri);
	}

	/**
	 * Set credentials
	 * 
	 * @param user
	 * @param password
	 * @return this client
	 */
	public GitHubClient setCredentials(String user, String password) {
		if (user != null && password != null)
			this.client.getCredentialsProvider().setCredentials(
					new AuthScope(httpHost.getHostName(), httpHost.getPort()),
					new UsernamePasswordCredentials(user, password));
		else
			this.client.getCredentialsProvider().clear();
		return this;
	}

	/**
	 * Get the user that this client is currently authenticating as
	 * 
	 * @return user or null if not authentication
	 */
	public String getUser() {
		Credentials credentials = this.client.getCredentialsProvider()
				.getCredentials(
						new AuthScope(httpHost.getHostName(), httpHost
								.getPort()));
		return credentials != null ? credentials.getUserPrincipal().getName()
				: null;
	}

	/**
	 * Parse json to specified type
	 * 
	 * @param <V>
	 * @param response
	 * @param type
	 * @return type
	 * @throws IOException
	 */
	protected <V> V parseJson(HttpResponse response, Type type)
			throws IOException {
		InputStreamReader reader = new InputStreamReader(getStream(response));
		try {
			return this.gson.fromJson(reader, type);
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
	 * Get {@link InputStream} from response
	 * 
	 * @param response
	 * @return non-null input stream
	 * @throws IOException
	 */
	protected InputStream getStream(HttpResponse response) throws IOException {
		HttpEntity entity = response.getEntity();
		if (entity == null)
			throw new IOException("Response has no entity"); //$NON-NLS-1$
		InputStream stream = entity.getContent();
		if (stream == null)
			throw new IOException("Empty body"); //$NON-NLS-1$
		return stream;
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
	 * Get response stream from uri. It is the responsibility of the calling
	 * method to close the returned stream.
	 * 
	 * @param request
	 * @return stream
	 * @throws IOException
	 */
	public InputStream getStream(GitHubRequest request) throws IOException {
		HttpGet method = createGet(request.generateUri());
		try {
			HttpResponse response = client.execute(httpHost, method,
					httpContext);
			StatusLine status = getStatus(response);
			switch (status.getStatusCode()) {
			case HttpStatus.SC_OK:
				return getStream(response);
			case HttpStatus.SC_BAD_REQUEST:
			case HttpStatus.SC_UNAUTHORIZED:
			case HttpStatus.SC_FORBIDDEN:
			case HttpStatus.SC_NOT_FOUND:
			case HttpStatus.SC_UNPROCESSABLE_ENTITY:
			case HttpStatus.SC_INTERNAL_SERVER_ERROR:
				RequestError error = parseJson(response, RequestError.class);
				throw new RequestException(error, status.getStatusCode());
			default:
				throw new IOException(status.getReasonPhrase());
			}
		} catch (JsonParseException jpe) {
			throw new IOException(jpe.getMessage());
		}
	}

	/**
	 * Get response from uri and bind to specified type
	 * 
	 * @param request
	 * @return response
	 * @throws IOException
	 */
	public GitHubResponse get(GitHubRequest request) throws IOException {
		HttpGet method = createGet(request.generateUri());
		try {
			HttpResponse response = client.execute(httpHost, method,
					httpContext);
			StatusLine status = getStatus(response);
			switch (status.getStatusCode()) {
			case HttpStatus.SC_OK:
				return new GitHubResponse(response, parseJson(response,
						request.getType()));
			case HttpStatus.SC_NO_CONTENT:
				return new GitHubResponse(response, null);
			case HttpStatus.SC_BAD_REQUEST:
			case HttpStatus.SC_UNAUTHORIZED:
			case HttpStatus.SC_FORBIDDEN:
			case HttpStatus.SC_NOT_FOUND:
			case HttpStatus.SC_UNPROCESSABLE_ENTITY:
			case HttpStatus.SC_INTERNAL_SERVER_ERROR:
				RequestError error = parseJson(response, RequestError.class);
				throw new RequestException(error, status.getStatusCode());
			default:
				throw new IOException(status.getReasonPhrase());
			}
		} catch (JsonParseException jpe) {
			throw new IOException(jpe.getMessage());
		}
	}

	/**
	 * Send json using specified method
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
		if (params != null) {
			StringBuilder payload = new StringBuilder();
			gson.toJson(params, payload);
			method.setEntity(new StringEntity(payload.toString(),
					IGitHubConstants.CONTENT_TYPE_JSON,
					IGitHubConstants.CHARSET_UTF8));
		}
		HttpResponse response = client.execute(httpHost, method, httpContext);
		StatusLine status = getStatus(response);
		switch (status.getStatusCode()) {
		case HttpStatus.SC_OK:
		case HttpStatus.SC_CREATED:
			if (type != null)
				return parseJson(response, type);
		case HttpStatus.SC_NO_CONTENT:
			break;
		case HttpStatus.SC_BAD_REQUEST:
		case HttpStatus.SC_UNAUTHORIZED:
		case HttpStatus.SC_FORBIDDEN:
		case HttpStatus.SC_NOT_FOUND:
		case HttpStatus.SC_UNPROCESSABLE_ENTITY:
		case HttpStatus.SC_INTERNAL_SERVER_ERROR:
			RequestError error = parseJson(response, RequestError.class);
			throw new RequestException(error, status.getStatusCode());
		default:
			throw new IOException(status.getReasonPhrase());
		}
		return null;
	}

	/**
	 * Post data to uri
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
	 * Put data to uri
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
	 * Delete resource at uri. This method will throw an {@link IOException}
	 * when the response in not a 204 (No Content) status.
	 * 
	 * @param uri
	 * @throws IOException
	 */
	public void delete(String uri) throws IOException {
		HttpResponse response = client.execute(httpHost, createDelete(uri),
				httpContext);
		int status = getStatus(response).getStatusCode();
		if (status != HttpStatus.SC_NO_CONTENT) {
			RequestError error = parseJson(response, RequestError.class);
			throw new RequestException(error, status);
		}
	}
}
