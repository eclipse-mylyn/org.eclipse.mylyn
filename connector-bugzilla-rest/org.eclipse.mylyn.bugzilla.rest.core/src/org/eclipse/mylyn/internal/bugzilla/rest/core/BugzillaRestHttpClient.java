/*******************************************************************************
 * Copyright (c) 2013 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.rest.core;

import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpClient;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.LoginToken;

public class BugzillaRestHttpClient extends CommonHttpClient {
	private LoginToken loginToken = null;

	public BugzillaRestHttpClient(RepositoryLocation location) {
		super(location);
	}

	public LoginToken getLoginToken() {
		return loginToken;
	}

	public void setLoginToken(LoginToken loginToken) {
		this.loginToken = loginToken;
	}

	@Override
	public boolean needsAuthentication() {
		return ((loginToken == null) || super.needsAuthentication());
	}
}
