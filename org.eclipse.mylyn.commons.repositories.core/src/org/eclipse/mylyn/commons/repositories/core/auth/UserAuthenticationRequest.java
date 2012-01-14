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

import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;

/**
 * @author Steffen Pingel
 */
public class UserAuthenticationRequest extends AuthenticationRequest<AuthenticationType<UserCredentials>> {

	private final boolean needsDomain;

	public UserAuthenticationRequest(RepositoryLocation location,
			AuthenticationType<UserCredentials> authenticationType, boolean needsDomain) {
		super(location, authenticationType);
		this.needsDomain = needsDomain;
	}

	public boolean needsDomain() {
		return needsDomain;
	}

}
