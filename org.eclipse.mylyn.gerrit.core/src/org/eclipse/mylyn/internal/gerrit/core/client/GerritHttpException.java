/*******************************************************************************
 * Copyright (c) 2011, 2012 Steffen Pingel and others.
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

import org.apache.commons.httpclient.auth.AuthScheme;

/**
 * @author Steffen Pingel
 */
public class GerritHttpException extends GerritException {

	private static final long serialVersionUID = 9032521978140685830L;

	private AuthScheme authScheme;

	private final int responseCode;

	public GerritHttpException(int responseCode) {
		super("HTTP Error " + responseCode); //$NON-NLS-1$
		this.responseCode = responseCode;
	}

	public AuthScheme getAuthScheme() {
		return authScheme;
	}

	public void setAuthScheme(AuthScheme authScheme) {
		this.authScheme = authScheme;
	}

	public int getResponseCode() {
		return responseCode;
	}

}
