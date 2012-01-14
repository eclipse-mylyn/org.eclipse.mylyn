/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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

	public AuthenticationException(String message, AuthenticationRequest<?> request) {
		super(message);
		Assert.isNotNull(request);
		this.request = request;
	}

	public AuthenticationException(AuthenticationRequest<?> request) {
		this(null, request);
	}

	public AuthenticationRequest<?> getRequest() {
		return request;
	}

}
