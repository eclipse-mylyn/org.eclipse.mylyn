/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client;

import java.util.Map;

import org.eclipse.core.runtime.Assert;

/**
 * @author Steffen Pingel
 */
public class OpenIdAuthenticationRequest {

	private String alternateUrl;

	private final Map<String, String> providerArgs;

	private final String requestUrl;

	private final String returnUrl;

	private String cookie;

	private String cookieUrl;

	public OpenIdAuthenticationRequest(String requestUrl, Map<String, String> providerArgs, String returnUrl) {
		Assert.isNotNull(requestUrl);
		Assert.isNotNull(providerArgs);
		Assert.isNotNull(returnUrl);
		this.requestUrl = requestUrl;
		this.providerArgs = providerArgs;
		this.returnUrl = returnUrl;
	}

	public String getAlternateUrl() {
		return alternateUrl;
	}

	public Map<String, String> getProviderArgs() {
		return providerArgs;
	}

	public String getRequestUrl() {
		return requestUrl;
	}

	/**
	 * Alternate URL in case the browser does not support POST requests.
	 */
	public void setAlternateUrl(String alternateUrl) {
		this.alternateUrl = alternateUrl;
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	public void setCookieUrl(String cookieUrl) {
		this.cookieUrl = cookieUrl;
	}

	public String getCookie() {
		return cookie;
	}

	public String getCookieUrl() {
		return cookieUrl;
	}

}
