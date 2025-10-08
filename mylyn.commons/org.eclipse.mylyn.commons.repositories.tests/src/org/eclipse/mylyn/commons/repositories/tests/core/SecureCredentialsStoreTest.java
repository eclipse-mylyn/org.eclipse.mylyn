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
import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.mylyn.commons.repositories.core.auth.UnavailableException;
import org.eclipse.mylyn.commons.repositories.tests.support.DelegatingSecurePreferences;
import org.eclipse.mylyn.internal.commons.repositories.core.InMemoryCredentialsStore;
import org.eclipse.mylyn.internal.commons.repositories.core.SecureCredentialsStore;
import org.junit.Test;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class SecureCredentialsStoreTest extends AbstractCredentialsStoreTest {

	private class StubSecureCredentialsStore extends SecureCredentialsStore {
		private boolean unavailable;

		DelegatingSecurePreferences delegate;

		public StubSecureCredentialsStore() {
			super(SecureCredentialsStore.class.getName());
		}

		public StubSecureCredentialsStore(String id) {
			super(id);
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

					@Override
					public String get(String key, String def) throws StorageException {
						if (unavailable) {
							throw new StorageException(0, "Unavailable");
						}
						return super.get(key, def);
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

		public void setUnavailable(boolean unavailable) {
			this.unavailable = unavailable;
		}
	}

	@Test
	public void testClear() {
		StubSecureCredentialsStore store = createStubSecureCredentialsStore();
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

	private StubSecureCredentialsStore createStubSecureCredentialsStore() {
		StubSecureCredentialsStore store = new StubSecureCredentialsStore();
		store.clear();
		return store;
	}

	private StubSecureCredentialsStore createStubSecureCredentialsStore(String id) {
		StubSecureCredentialsStore store = new StubSecureCredentialsStore(id);
		store.clear();
		return store;
	}

	@Test
	public void testKeysInSecurePreferences() {
		StubSecureCredentialsStore store = createStubSecureCredentialsStore();
		store.put("key", "value", false);
		assertEquals("[key]", Arrays.toString(store.getSecurePreferences().keys()));
		assertEquals("[]", Arrays.toString(store.getInMemoryStore().keys()));
	}

	@Test
	public void testKeysInSecurePreferencesNoPersist() {
		StubSecureCredentialsStore store = createStubSecureCredentialsStore();
		store.put("key", "value", false, false);
		assertEquals("[]", Arrays.toString(store.getSecurePreferences().keys()));
		assertEquals("[key]", Arrays.toString(store.getInMemoryStore().keys()));
	}

	@Test
	public void testKeysInSecurePreferencesEncryptNoPersist() {
		StubSecureCredentialsStore store = createStubSecureCredentialsStore();
		store.put("key", "value", true, false);
		assertEquals("[]", Arrays.toString(store.getSecurePreferences().keys()));
		assertEquals("[key]", Arrays.toString(store.getInMemoryStore().keys()));
	}

	@Test
	public void testKeysInSecurePreferencesNoPersistClear() {
		StubSecureCredentialsStore store = createStubSecureCredentialsStore();
		store.put("key", "value", false, false);
		store.clear();
		assertEquals("[]", Arrays.toString(store.getSecurePreferences().keys()));
		assertEquals("[]", Arrays.toString(store.getInMemoryStore().keys()));
	}

	@Test
	public void testPutException() {
		StubSecureCredentialsStore store = createStubSecureCredentialsStore();
		store.getSecurePreferences().setException(new StorageException(0, ""));
		store.put("key", "value", true);
		assertEquals("[]", Arrays.toString(store.getSecurePreferences().keys()));
		assertEquals("value", store.getInMemoryStore().get("key", null));
		assertEquals("value", store.get("key", null));
	}

	@Test
	public void testPutExceptionNoException() {
		StubSecureCredentialsStore store = createStubSecureCredentialsStore();
		store.getSecurePreferences().setException(new StorageException(0, ""));
		store.put("key", "value", true);
		store.getSecurePreferences().setException(null);
		store.put("key", "value", true);
		assertEquals("[key]", Arrays.toString(store.getSecurePreferences().keys()));
		assertEquals(null, store.getInMemoryStore().get("key", null));
		assertEquals("value", store.get("key", null));
	}

	@Test
	public void testSpecialCharactersInId() {
		StubSecureCredentialsStore store = createStubSecureCredentialsStore("http://ci.mylyn.org/test 1");
		assertEquals("http://ci.mylyn.org/test 1", store.getId());
		assertEquals("http:\\2f\\2fci.mylyn.org\\2ftest 1", store.getSecurePreferences().name());

		store = createStubSecureCredentialsStore("http://ci.mylyn.org/\u00E7\u00F1\u00FC");
		assertEquals("http://ci.mylyn.org/\u00E7\u00F1\u00FC", store.getId());
		assertEquals("http%3A%2F%2Fci.mylyn.org%2F%C3%A7%C3%B1%C3%BC", store.getSecurePreferences().name());

		store = createStubSecureCredentialsStore("\uABCD  \u1F00");
		assertEquals("\uABCD  \u1F00", store.getId());
		assertEquals("%EA%AF%8D++%E1%BC%80", store.getSecurePreferences().name());
	}

	@Test
	public void testValidCharactersNotEncoded() {
		// create a key containing all valid characters
		StringBuilder sb = new StringBuilder();
		for (char c = 32; c <= 126; c++) {
			if (c != '\\' && c != '/') {
				sb.append(c);
			}
		}
		String key = sb.toString();
		StubSecureCredentialsStore store = createStubSecureCredentialsStore(key);
		assertEquals(key, store.getId());
		assertEquals(key, store.getSecurePreferences().name());
	}

	@Test
	public void testInvalidCharactersEncoded() throws UnsupportedEncodingException {
		for (char c = 0; c < 32; c++) {
			assertInvalidCharacter(c);
		}
		for (char c = 127; c < 256; c++) {
			assertInvalidCharacter(c);
		}
	}

	private void assertInvalidCharacter(char c) throws UnsupportedEncodingException {
		String key = "key" + Character.toString(c);
		StubSecureCredentialsStore store = createStubSecureCredentialsStore(key);
		assertEquals(key, store.getId());
		assertEquals("key" + URLEncoder.encode(Character.toString(c), "UTF-8"), store.getSecurePreferences().name());
	}

	@Test
	public void testSpecialCharactersInIdRetrieveValue() {
		StubSecureCredentialsStore store = createStubSecureCredentialsStore("http://ci.mylyn.org/\u00E7\u00F1\u00FC");
		store.put("key", "value", false);
		assertEquals("[key]", Arrays.toString(store.getSecurePreferences().keys()));
		assertEquals("value", store.get("key", null));
	}

	@Test
	public void testSpecialCharactersInIdRetrieveValueNoPersist() {
		StubSecureCredentialsStore store = createStubSecureCredentialsStore("http://ci.mylyn.org/\u00E7\u00F1\u00FC");
		store.put("key", "value", false, false);
		assertEquals("[key]", Arrays.toString(store.getInMemoryStore().keys()));
		assertEquals("value", store.get("key", null));
	}

	@Test
	public void testSpecialCharactersInIdRetrieveValueEncrypt() {
		StubSecureCredentialsStore store = createStubSecureCredentialsStore("http://ci.mylyn.org/\u00E7\u00F1\u00FC");
		store.put("key", "value", true);
		assertEquals("[key]", Arrays.toString(store.getSecurePreferences().keys()));
		assertEquals("value", store.get("key", null));
	}

	@Test
	public void testSpecialCharactersInIdRetrieveValueEncryptNoPersist() {
		StubSecureCredentialsStore store = createStubSecureCredentialsStore("http://ci.mylyn.org/\u00E7\u00F1\u00FC");
		store.put("key", "value", true, false);
		assertEquals("[key]", Arrays.toString(store.getInMemoryStore().keys()));
		assertEquals("value", store.get("key", null));
	}

	@Test
	public void testTestAvailability() throws Exception {
		StubSecureCredentialsStore store = createStubSecureCredentialsStore();
		assertNull(store.get("org.eclipse.mylyn.commons.repositories.core.SecureCredentialsStore", null));
		store.testAvailability();
		assertNotNull(store.get("org.eclipse.mylyn.commons.repositories.core.SecureCredentialsStore", null));
		store.setUnavailable(true);
		try {
			store.testAvailability();
			fail("Expected UnavailableException");
		} catch (UnavailableException e) {// expected
		}
	}
}
