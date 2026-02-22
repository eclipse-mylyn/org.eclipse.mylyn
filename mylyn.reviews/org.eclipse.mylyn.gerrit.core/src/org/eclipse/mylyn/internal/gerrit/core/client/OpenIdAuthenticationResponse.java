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

package org.eclipse.mylyn.internal.gerrit.core.client;

/**
 * @author Steffen Pingel
 */
public class OpenIdAuthenticationResponse {

	private final String responseUrl;

	private final String cookieValue;

	public OpenIdAuthenticationResponse(String responseUrl, String cookieValue) {
		this.responseUrl = responseUrl;
		this.cookieValue = cookieValue;
	}

	public String getCookieValue() {
		return cookieValue;
	}

	public String getResponseUrl() {
		return responseUrl;
	}

}
