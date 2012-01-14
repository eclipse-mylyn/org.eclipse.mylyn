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

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.mylyn.commons.repositories.tests.support.DelegatingSecurePreferences;
import org.eclipse.mylyn.internal.commons.repositories.core.InMemoryCredentialsStore;
import org.eclipse.mylyn.internal.commons.repositories.core.SecureCredentialsStore;
import org.junit.Test;

/**
 * @author Steffen Pingel
 */
public class SecureCredentialsStoreTest extends AbstractCredentialsStoreTest {

	private class StubSecureCredentialsStore extends SecureCredentialsStore {

		DelegatingSecurePreferences delegate;

		public StubSecureCredentialsStore() {
			super(SecureCredentialsStore.class.getName());
		}

		@Override
		protected DelegatingSecurePreferences getSecurePreferences() {
			if (delegate == null) {
				delegate = new DelegatingSecurePreferences(getSecurePreferencesSuper()) {
					@Override
					public void removeNode() {
						super.removeNode();
						// re-initialize
						setDelegate(getSecurePreferencesSuper());
					}
				};
			}
			return delegate;
		}

		ISecurePreferences getSecurePreferencesSuper() {
			return super.getSecurePreferences();
		}

		@Override
		protected synchronized InMemoryCredentialsStore getInMemoryStore() {
			return super.getInMemoryStore();
		}

	}

	@Test
	public void testClear() {
		StubSecureCredentialsStore store = new StubSecureCredentialsStore();
		store.put("key", "value", false);
		assertEquals("[key]", Arrays.toString(store.getSecurePreferences().keys()));
		store.clear();
		assertEquals("[]", Arrays.toString(store.getSecurePreferences().keys()));
	}

	@Test
	public void testGetId() {
		SecureCredentialsStore store = createCredentialsStore();
		assertEquals(SecureCredentialsStoreTest.class.getName(), store.getId());
	}

	@Override
	protected SecureCredentialsStore createCredentialsStore() {
		SecureCredentialsStore store = new SecureCredentialsStore(SecureCredentialsStoreTest.class.getName());
		store.clear();
		return store;
	}

	@Test
	public void testKeysInSecurePreferences() {
		StubSecureCredentialsStore store = new StubSecureCredentialsStore();
		store.put("key", "value", false);
		assertEquals("[key]", Arrays.toString(store.getSecurePreferences().keys()));
		assertEquals("[]", Arrays.toString(store.getInMemoryStore().keys()));
	}

	@Test
	public void testKeysInSecurePreferencesNoPersist() {
		StubSecureCredentialsStore store = new StubSecureCredentialsStore();
		store.put("key", "value", false, false);
		assertEquals("[]", Arrays.toString(store.getSecurePreferences().keys()));
		assertEquals("[key]", Arrays.toString(store.getInMemoryStore().keys()));
	}

	@Test
	public void testKeysInSecurePreferencesEncryptNoPersist() {
		StubSecureCredentialsStore store = new StubSecureCredentialsStore();
		store.put("key", "value", true, false);
		assertEquals("[]", Arrays.toString(store.getSecurePreferences().keys()));
		assertEquals("[key]", Arrays.toString(store.getInMemoryStore().keys()));
	}

	@Test
	public void testKeysInSecurePreferencesNoPersistClear() {
		StubSecureCredentialsStore store = new StubSecureCredentialsStore();
		store.put("key", "value", false, false);
		store.clear();
		assertEquals("[]", Arrays.toString(store.getSecurePreferences().keys()));
		assertEquals("[]", Arrays.toString(store.getInMemoryStore().keys()));
	}

	@Test
	public void testPutException() {
		StubSecureCredentialsStore store = new StubSecureCredentialsStore();
		store.getSecurePreferences().setException(new StorageException(0, ""));
		store.put("key", "value", true);
		assertEquals("[]", Arrays.toString(store.getSecurePreferences().keys()));
		assertEquals("value", store.getInMemoryStore().get("key", null));
		assertEquals("value", store.get("key", null));
	}

	@Test
	public void testPutExceptionNoException() {
		StubSecureCredentialsStore store = new StubSecureCredentialsStore();
		store.getSecurePreferences().setException(new StorageException(0, ""));
		store.put("key", "value", true);
		store.getSecurePreferences().setException(null);
		store.put("key", "value", true);
		assertEquals("[key]", Arrays.toString(store.getSecurePreferences().keys()));
		assertEquals(null, store.getInMemoryStore().get("key", null));
		assertEquals("value", store.get("key", null));
	}

}
