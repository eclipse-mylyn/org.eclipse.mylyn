/*******************************************************************************
 * Copyright (c) 2011, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.repositories.core.auth;

import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;

/**
 * @author Steffen Pingel
 */
public class OpenIdAuthenticationRequest extends AuthenticationRequest<AuthenticationType<OpenIdCredentials>> {

	private String alternateUrl;

	private final Map<String, String> providerArgs;

	private final String requestUrl;

	private final String returnUrl;

	private String cookie;

	private String cookieUrl;

	public OpenIdAuthenticationRequest(RepositoryLocation location,
			AuthenticationType<OpenIdCredentials> authenticationType, String requestUrl,
			Map<String, String> providerArgs, String returnUrl) {
		super(location, authenticationType);
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
