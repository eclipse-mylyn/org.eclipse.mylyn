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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;

import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.mylyn.commons.repositories.tests.support.DelegatingSecurePreferences;
import org.eclipse.mylyn.internal.commons.repositories.core.InMemoryCredentialsStore;
import org.eclipse.mylyn.internal.commons.repositories.core.SecureCredentialsStore;
import org.eclipse.mylyn.internal.commons.repositories.ui.UiLocationService;
import org.eclipse.mylyn.internal.commons.repositories.ui.UiSecureCredentialsStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.junit.Test;

/**
 * @author Steffen Pingel
 */
public class SecureCredentialsStoreTest extends AbstractCredentialsStoreTest {

	private class StubSecureCredentialsStore extends UiSecureCredentialsStore {

		DelegatingSecurePreferences delegate;

		private boolean openSecurePreferencesCalled;

		private Display display;

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

		@Override
		protected ISecurePreferences openSecurePreferences() {
			openSecurePreferencesCalled = true;
			display = Display.getCurrent();
			return super.openSecurePreferences();
		}

		protected boolean wasOpenSecurePreferencesCalled() {
			return openSecurePreferencesCalled;
		}

		protected Display getDisplay() {
			return display;
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

	@Test
	public void testOpenSecurePreferencesThrowsExceptionOnUiThread() throws Exception {
		runOnUiThread(new Runnable() {
			public void run() {
				assertNotNull(Display.getCurrent());
				StubSecureCredentialsStore store = new StubSecureCredentialsStore();
				try {
					store.openSecurePreferences();
				} catch (AssertionFailedException e) {// expected
					return;
				}
				assertTrue(false);
			}
		});
	}

	@Test
	public void testAccessSecureStoreOnUiThread() throws Exception {
		runOnUiThread(new Runnable() {
			public void run() {
				assertNotNull(Display.getCurrent());
				StubSecureCredentialsStore store = new StubSecureCredentialsStore();
				assertFalse(store.wasOpenSecurePreferencesCalled());
				store.put("key", "value", false);
				assertEquals("value", store.get("key", null));
				// check that openSecurePreferences was called but not from the UI thread
				assertTrue(store.wasOpenSecurePreferencesCalled());
				assertNull(store.getDisplay());
			}
		});
	}

	protected void runOnUiThread(final Runnable runnable) throws AssertionError {
		final AssertionError assertionError[] = new AssertionError[1];
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {
				try {
					runnable.run();
				} catch (AssertionError e) {
					assertionError[0] = e;
				}

			}
		});
		if (assertionError[0] != null) {
			throw assertionError[0];
		}
	}

	@Test
	public void testRunOnUiThread() throws Exception {
		try {
			runOnUiThread(new Runnable() {
				public void run() {
					assertTrue(false);
				}
			});
		} catch (AssertionError e) {// expected
			return;
		}
		assertTrue(false);
	}

	@Test
	public void testUiLocationService() throws Exception {
		assertTrue(new UiLocationService().getCredentialsStore("test") instanceof UiSecureCredentialsStore);
	}

	@Test
	public void testSpecialCharactersInId() {
		StubSecureCredentialsStore store = new StubSecureCredentialsStore("http://ci.mylyn.org/test 1");
		assertEquals("http://ci.mylyn.org/test 1", store.getId());
		assertEquals("http:\\2f\\2fci.mylyn.org\\2ftest 1", store.getSecurePreferences().name());

		store = new StubSecureCredentialsStore("http://ci.mylyn.org/\u00E7\u00F1\u00FC");
		assertEquals("http://ci.mylyn.org/\u00E7\u00F1\u00FC", store.getId());
		assertEquals("http%3A%2F%2Fci.mylyn.org%2F%C3%A7%C3%B1%C3%BC", store.getSecurePreferences().name());

		store = new StubSecureCredentialsStore("\uABCD  \u1F00");
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
		StubSecureCredentialsStore store = new StubSecureCredentialsStore(key);
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
		StubSecureCredentialsStore store = new StubSecureCredentialsStore(key);
		assertEquals(key, store.getId());
		assertEquals("key" + URLEncoder.encode(Character.toString(c), "UTF-8"), store.getSecurePreferences().name());
	}

	@Test
	public void testSpecialCharactersInIdRetrieveValue() {
		StubSecureCredentialsStore store = new StubSecureCredentialsStore("http://ci.mylyn.org/\u00E7\u00F1\u00FC");
		store.put("key", "value", false);
		assertEquals("[key]", Arrays.toString(store.getSecurePreferences().keys()));
		assertEquals("value", store.get("key", null));
	}

	@Test
	public void testSpecialCharactersInIdRetrieveValueNoPersist() {
		StubSecureCredentialsStore store = new StubSecureCredentialsStore("http://ci.mylyn.org/\u00E7\u00F1\u00FC");
		store.put("key", "value", false, false);
		assertEquals("[key]", Arrays.toString(store.getInMemoryStore().keys()));
		assertEquals("value", store.get("key", null));
	}

	@Test
	public void testSpecialCharactersInIdRetrieveValueEncrypt() {
		StubSecureCredentialsStore store = new StubSecureCredentialsStore("http://ci.mylyn.org/\u00E7\u00F1\u00FC");
		store.put("key", "value", true);
		assertEquals("[key]", Arrays.toString(store.getSecurePreferences().keys()));
		assertEquals("value", store.get("key", null));
	}

	@Test
	public void testSpecialCharactersInIdRetrieveValueEncryptNoPersist() {
		StubSecureCredentialsStore store = new StubSecureCredentialsStore("http://ci.mylyn.org/\u00E7\u00F1\u00FC");
		store.put("key", "value", true, false);
		assertEquals("[key]", Arrays.toString(store.getInMemoryStore().keys()));
		assertEquals("value", store.get("key", null));
	}
}
