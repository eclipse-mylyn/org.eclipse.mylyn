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
import static org.junit.Assert.assertNotSame;

import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationCredentials;
import org.eclipse.mylyn.commons.repositories.core.auth.CertificateCredentials;
import org.eclipse.mylyn.commons.repositories.core.auth.ICredentialsStore;
import org.eclipse.mylyn.commons.repositories.core.auth.OpenIdCredentials;
import org.eclipse.mylyn.commons.repositories.core.auth.UserCredentials;
import org.eclipse.mylyn.internal.commons.repositories.core.CredentialsFactory;
import org.eclipse.mylyn.internal.commons.repositories.core.InMemoryCredentialsStore;
import org.junit.Test;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class CredentialsFactoryTest {

	@Test(expected = RuntimeException.class)
	public void testCreateInvalidCredentials() {
		ICredentialsStore store = new InMemoryCredentialsStore();
		AuthenticationCredentials credentials = new AuthenticationCredentials() {
			@Override
			public void save(ICredentialsStore store, String prefix) {
				// ignore
			}

			@Override
			public void clear(ICredentialsStore store, String prefix) {
				// ignore
			}
		};
		CredentialsFactory.create(credentials.getClass(), store, "key", false);
	}

	@Test
	public void testCreateUserCredentials() {
		ICredentialsStore store = new InMemoryCredentialsStore();
		UserCredentials credentials = CredentialsFactory.create(UserCredentials.class, store, "key", false);
		assertEquals("", credentials.getUserName());
		assertEquals("", credentials.getPassword());
		assertEquals(null, credentials.getDomain());
		assertEquals(false, credentials.getSavePassword());
	}

	@Test
	public void testSaveUserCredentials() throws StorageException {
		ICredentialsStore store = new InMemoryCredentialsStore();
		UserCredentials oldCredentials = new UserCredentials("user", "password", "domain", false);
		oldCredentials.save(store, "key");
		UserCredentials newCredentials = CredentialsFactory.create(UserCredentials.class, store, "key", true);
		assertNotSame(oldCredentials, newCredentials);
		assertEquals(oldCredentials, newCredentials);
	}

	@Test
	public void testCreateCertificateCredentials() {
		ICredentialsStore store = new InMemoryCredentialsStore();
		CertificateCredentials credentials = CredentialsFactory.create(CertificateCredentials.class, store, "key",
				false);
		assertEquals(null, credentials.getKeyStoreFileName());
		assertEquals("", credentials.getPassword());
	}

	@Test
	public void testSaveCertificateCredentials() throws StorageException {
		ICredentialsStore store = new InMemoryCredentialsStore();
		CertificateCredentials oldCredentials = new CertificateCredentials("keyStore", "password", "type");
		oldCredentials.save(store, "key");
		CertificateCredentials newCredentials = CredentialsFactory.create(CertificateCredentials.class, store, "key",
				true);
		assertNotSame(oldCredentials, newCredentials);
		assertEquals(oldCredentials, newCredentials);
	}

	@Test
	public void testCreateOpenIdCredentials() {
		ICredentialsStore store = new InMemoryCredentialsStore();
		OpenIdCredentials credentials = CredentialsFactory.create(OpenIdCredentials.class, store, "key", false);
		assertEquals(null, credentials.getResponseUrl());
		assertEquals(null, credentials.getToken());
	}

	@Test
	public void testSaveOpenIdCredentials() throws StorageException {
		ICredentialsStore store = new InMemoryCredentialsStore();
		OpenIdCredentials oldCredentials = new OpenIdCredentials("responseUrl", "token");
		oldCredentials.save(store, "key");
		OpenIdCredentials newCredentials = CredentialsFactory.create(OpenIdCredentials.class, store, "key", true);
		assertNotSame(oldCredentials, newCredentials);
		assertEquals(oldCredentials, newCredentials);
	}

}
