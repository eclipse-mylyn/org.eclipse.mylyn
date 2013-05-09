/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.sdk.util;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Provides URLs for testing connections.
 * 
 * @author Steffen Pingel
 */
public class TestUrl {

	public static final TestUrl DEFAULT = new TestUrl();

	private final String URL_HTTP_404_NOT_FOUND = "http://mylyn.org/notfound";

	private final String URL_HTTP_CONNECTION_REFUSED = "http://mylyn.org:9999/";

	private final String URL_HTTP_CONNECTION_TIMEOUT = "http://google.com:9999/";

	private final String URL_HTTP_OK = "http://mylyn.org/";

	private final String URL_HTTP_UNKNOWN_HOST = "http://nonexistant.mylyn.org";

	private final String URL_HTTPS_OK = "https://mylyn.org/";

	public URL getConnectionRefused() {
		return createUrl(URL_HTTP_CONNECTION_REFUSED);
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
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	private TestUrl() {
		// not intended to be instantiated
	}

}
