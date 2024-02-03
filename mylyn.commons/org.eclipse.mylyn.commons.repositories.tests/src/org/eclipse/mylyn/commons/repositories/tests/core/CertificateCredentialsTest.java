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

import org.eclipse.mylyn.commons.repositories.core.auth.CertificateCredentials;
import org.junit.Test;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class CertificateCredentialsTest {

	@Test
	public void testConstructor() {
		CertificateCredentials credentials = new CertificateCredentials("filename", "password", "type");
		assertEquals("filename", credentials.getKeyStoreFileName());
		assertEquals("password", credentials.getPassword());
		assertEquals("type", credentials.getKeyStoreType());
		assertEquals(true, credentials.getSavePassword());
	}

	@Test
	public void testConstructorSavePassword() {
		CertificateCredentials credentials = new CertificateCredentials("filename", "password", "type", false);
		assertEquals("filename", credentials.getKeyStoreFileName());
		assertEquals("password", credentials.getPassword());
		assertEquals("type", credentials.getKeyStoreType());
		assertEquals(false, credentials.getSavePassword());
	}

}
