/*******************************************************************************
 * Copyright (c) 2012, 2013 Tasktop Technologies and others.
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

import java.io.IOException;

import org.eclipse.core.runtime.Assert;

/**
 * @author Steffen Pingel
 */
public class AuthenticationException extends IOException {

	private static final long serialVersionUID = 1L;

	private final AuthenticationRequest<?> request;

	private final boolean shouldRetry;

	public AuthenticationException(String message, AuthenticationRequest<?> request, boolean shouldRetry) {
		super(message);
		Assert.isNotNull(request);
		this.request = request;
		this.shouldRetry = shouldRetry;
	}

	public AuthenticationException(String message, AuthenticationRequest<?> request) {
		this(message, request, false);
	}

	public AuthenticationException(AuthenticationRequest<?> request) {
		this(null, request, false);
	}

	public AuthenticationRequest<?> getRequest() {
		return request;
	}

	public boolean shouldRetry() {
		return shouldRetry;
	}

}
