/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
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

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;

/**
 * @author Steffen Pingel
 */
public class AuthenticationRequest<T extends AuthenticationType<?>> {

	private final T authenticationType;

	private final RepositoryLocation location;

	private final String message;

	public AuthenticationRequest(RepositoryLocation location, T authenticationType, String message) {
		Assert.isNotNull(location);
		Assert.isNotNull(authenticationType);
		this.location = location;
		this.authenticationType = authenticationType;
		this.message = message;
	}

	public AuthenticationRequest(RepositoryLocation location, T authenticationType) {
		this(location, authenticationType, null);
	}

	public T getAuthenticationType() {
		return authenticationType;
	}

	public RepositoryLocation getLocation() {
		return location;
	}

	public String getMessage() {
		return message;
	}

}
