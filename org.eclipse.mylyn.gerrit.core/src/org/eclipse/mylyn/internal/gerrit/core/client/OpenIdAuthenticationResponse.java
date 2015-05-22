/*******************************************************************************
 * Copyright (c) 2011, 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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
