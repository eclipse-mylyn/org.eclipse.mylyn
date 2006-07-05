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

package org.eclipse.mylar.trac.tests.support;

import java.net.Authenticator;

import org.eclipse.mylar.internal.trac.core.ITracClient;
import org.eclipse.mylar.trac.tests.Constants;

/**
 * @author Steffen Pingel
 * 
 */
public abstract class AbstractTracRepositoryFactory {

	public String repositoryUrl;

	public ITracClient repository;

	public String username;

	public String password;

	public ITracClient connectRepository1() throws Exception {
		return connect(Constants.TEST_REPOSITORY1_URL, Constants.TEST_REPOSITORY1_ADMIN_USERNAME,
				Constants.TEST_REPOSITORY1_ADMIN_PASSWORD);
	}

	public ITracClient connect(String url, String username, String password) throws Exception {
		this.repositoryUrl = url;
		this.username = username;
		this.password = password;
		this.repository = createRepository(url, username, password);

		// make sure no dialog pops up to prompt for a password
		Authenticator.setDefault(null);

		return this.repository;
	}

	protected abstract ITracClient createRepository(String url, String username, String password) throws Exception;

}
