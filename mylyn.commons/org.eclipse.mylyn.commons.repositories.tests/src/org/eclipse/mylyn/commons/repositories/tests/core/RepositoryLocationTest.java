/*******************************************************************************
 * Copyright (c) 2012, 2024 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.commons.repositories.tests.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationType;
import org.eclipse.mylyn.commons.repositories.core.auth.UserCredentials;
import org.junit.Test;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class RepositoryLocationTest {

	@Test
	public void testGetId() {
		assertNotNull(new RepositoryLocation().getId());
	}

	@Test
	public void testSetCredentialsRepositoryCredentials() {
		RepositoryLocation location = new RepositoryLocation();
		UserCredentials credentials = new UserCredentials("user", "password", "domain", true);
		location.setCredentials(AuthenticationType.REPOSITORY, credentials);
		UserCredentials newCredentials = location.getCredentials(AuthenticationType.REPOSITORY);
		assertEquals(credentials, newCredentials);
	}

	@Test
	public void testSetCredentialsRepositoryHttpCredentials() {
		RepositoryLocation location = new RepositoryLocation();
		UserCredentials credentials = new UserCredentials("httpuser", "httppassword", "httpdomain", true);
		location.setCredentials(AuthenticationType.HTTP, credentials);
		UserCredentials newCredentials = location.getCredentials(AuthenticationType.HTTP);
		assertEquals(credentials, newCredentials);
	}

	@Test
	public void testSetCredentialsRepositoryNull() {
		RepositoryLocation location = new RepositoryLocation();
		UserCredentials credentials = new UserCredentials("httpuser", "httppassword", "httpdomain", true);
		location.setCredentials(AuthenticationType.HTTP, credentials);
		location.setCredentials(AuthenticationType.HTTP, null);
		assertNull(location.getCredentials(AuthenticationType.HTTP));
	}

}
