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

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;

/**
 * @author Steffen Pingel
 */
public class AuthenticationRequest<T extends AuthenticationType<?>> {

	private final T authenticationType;

	private final RepositoryLocation location;

	public AuthenticationRequest(RepositoryLocation location, T authenticationType) {
		Assert.isNotNull(location);
		Assert.isNotNull(authenticationType);
		this.location = location;
		this.authenticationType = authenticationType;
	}

	public T getAuthenticationType() {
		return authenticationType;
	}

	public RepositoryLocation getLocation() {
		return location;
	}

	public String getMessage() {
		return null;
	}

}
