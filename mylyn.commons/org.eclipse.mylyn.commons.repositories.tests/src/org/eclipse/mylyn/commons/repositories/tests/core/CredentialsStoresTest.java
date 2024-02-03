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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.eclipse.mylyn.commons.repositories.core.auth.CredentialsStores;
import org.eclipse.mylyn.commons.repositories.core.auth.ICredentialsStore;
import org.junit.Test;

@SuppressWarnings("nls")
public class CredentialsStoresTest {

	@Test
	public void testCreateInMemoryStore() {
		ICredentialsStore store = CredentialsStores.createInMemoryStore();
		assertNotNull(store);
	}

	@Test
	public void testGetDefaultCredentialsStore() {
		ICredentialsStore store = CredentialsStores.getDefaultCredentialsStore("test");
		assertNotNull(store);
	}

	@Test
	public void testGetDefaultCredentialsStoreNullID() {
		try {
			CredentialsStores.getDefaultCredentialsStore(null);
			fail();
		} catch (RuntimeException e) {
			// expected
		}
	}
}
