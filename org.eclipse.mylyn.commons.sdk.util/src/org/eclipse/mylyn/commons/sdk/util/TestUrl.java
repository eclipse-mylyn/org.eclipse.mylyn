/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.sdk.util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

/**
 * Provides URLs for testing connections.
 *
 * @author Steffen Pingel
 */
public class TestUrl {

	public static final TestUrl DEFAULT = probeLocalhost();

	private final String URL_HTTP_404_NOT_FOUND = "http://mylyn.org/notfound";

	private final String URL_HTTP_CONNECTION_REFUSED = "http://mylyn.org:9999/";

	private final String URL_HTTP_CONNECTION_TIMEOUT = "http://google.com:9999/";

	private final String URL_HTTP_OK = "http://mylyn.org/";

	private final String URL_HTTP_UNKNOWN_HOST = "http://nonexistant.mylyn.org";

	private final String URL_HTTPS_OK = "https://mylyn.org/";

	private final String host;

	public URL getConnectionRefused() {
		return createUrl(URL_HTTP_CONNECTION_REFUSED);
	}

	private static TestUrl probeLocalhost() {
		Socket socket = new Socket();
		try {
			socket.connect(new InetSocketAddress("localhost", 2080), 100);
			return new TestUrl("localhost");
		} catch (IOException e) {
			return new TestUrl(null);
		}
	}

	public URL getConnectionTimeout() {
		return createUrl(URL_HTTP_CONNECTION_TIMEOUT);
	}

	public URL getHttpNotFound() {
		return createUrl(URL_HTTP_404_NOT_FOUND);
	}

	public URL getHttpOk() {
		return createUrl(URL_HTTP_OK);
	}

	public URL getHttpsOk() {
		return createUrl(URL_HTTPS_OK);
	}

	public URL getUnknownHost() {
		return createUrl(URL_HTTP_UNKNOWN_HOST);
	}

	private URL createUrl(String url) {
		if (host != null) {
			url = url.replace("mylyn.org", host);
		}
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	private TestUrl(String host) {
		this.host = host;
	}

}
