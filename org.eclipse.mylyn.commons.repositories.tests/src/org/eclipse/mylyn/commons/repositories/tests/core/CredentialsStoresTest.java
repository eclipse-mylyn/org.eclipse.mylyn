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

package org.eclipse.mylyn.commons.repositories.tests.core;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.eclipse.mylyn.commons.repositories.core.auth.CredentialsStores;
import org.eclipse.mylyn.commons.repositories.core.auth.ICredentialsStore;
import org.junit.Test;

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
