/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.trac.core;

import java.net.URL;

/**
 * @author Steffen Pingel
 */
public abstract class AbstractTracRepository implements ITracRepository {

	protected String username;

	protected String password;

	protected URL repositoryUrl;

	protected Version version;

	public AbstractTracRepository(URL repositoryUrl, Version version, String username, String password) {
		this.repositoryUrl = repositoryUrl;
		this.version = version;
		this.username = username;
		this.password = password;
	}

	public Version getVersion() {
		return version;
	}

	protected boolean hasAuthenticationCredentials() {
		return username.length() > 0;
	}

}
