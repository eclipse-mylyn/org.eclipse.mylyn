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

import java.util.Arrays;

import org.eclipse.mylyn.commons.repositories.core.auth.ICredentialsStore;
import org.junit.Test;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public abstract class AbstractCredentialsStoreTest {

	@Test
	public void testGetBooleanDefault() {
		ICredentialsStore store = createCredentialsStore();
		assertEquals(true, store.getBoolean("key2", true));
		assertEquals(false, store.getBoolean("key2", false));
	}

	@Test
	public void testGetStringDefault() {
		ICredentialsStore store = createCredentialsStore();
		assertEquals("default", store.get("key2", "default"));
		assertEquals("otherValue", store.get("key2", "otherValue"));
		assertEquals(null, store.get("key2", null));
	}

	@Test
	public void testKeys() {
		ICredentialsStore store = createCredentialsStore();
		store.put("a", "value", false);
		store.put("b", "value", true);
		String[] keys = store.keys();
		Arrays.sort(keys);
		assertEquals("[a, b]", Arrays.toString(keys));
	}

	@Test
	public void testPutNull() {
		ICredentialsStore store = createCredentialsStore();
		store.put("key", "value", false);
		store.put("key", null, false);
		// putting null is different from removing a key
		assertEquals(null, store.get("key", "default"));
		store.remove("key");
		assertEquals("default", store.get("key", "default"));
	}

	@Test
	public void testPutGetBooleanEncrypted() {
		ICredentialsStore store = createCredentialsStore();
		store.putBoolean("key", true, true);
		assertEquals(true, store.getBoolean("key", false));
	}

	@Test
	public void testPutGetBooleanNotEncrypted() {
		ICredentialsStore store = createCredentialsStore();
		store.putBoolean("key", true, false);
		assertEquals(true, store.getBoolean("key", false));
	}

	@Test
	public void testPutGetByteArrayDefault() {
		ICredentialsStore store = createCredentialsStore();
		assertEquals(null, store.getByteArray("key2", null));
		assertEquals(Arrays.toString(new byte[] { 0x00, 0x32 }),
				Arrays.toString(store.getByteArray("key2", new byte[] { 0x00, 0x32 })));
	}

	@Test
	public void testPutGetByteArrayEncrypted() {
		ICredentialsStore store = createCredentialsStore();
		store.putByteArray("key", new byte[] { 0x00, 0x32 }, true);
		assertEquals(Arrays.toString(new byte[] { 0x00, 0x32 }),
				Arrays.toString(store.getByteArray("key", new byte[] { 0x00, 0x32 })));
	}

	@Test
	public void testPutGetByteArrayNotEncrypted() {
		ICredentialsStore store = createCredentialsStore();
		store.putByteArray("key", new byte[] { 0x00, 0x32 }, false);
		assertEquals(Arrays.toString(new byte[] { 0x00, 0x32 }),
				Arrays.toString(store.getByteArray("key", new byte[] { 0x00, 0x32 })));
	}

	@Test
	public void testPutGetStringEncrypted() {
		ICredentialsStore store = createCredentialsStore();
		store.put("key", "valueGet", true);
		assertEquals("valueGet", store.get("key", "default"));
	}

	@Test
	public void testPutGetStringEncryptedNotPersisted() {
		ICredentialsStore store = createCredentialsStore();
		store.put("key", "valueGet", true, false);
		assertEquals("valueGet", store.get("key", "default"));
		store.put("key", "newValue", true, false);
		assertEquals("newValue", store.get("key", "default"));
	}

	@Test
	public void testPutGetStringNotEncryptedNotPersisted() {
		ICredentialsStore store = createCredentialsStore();
		store.put("key", "valueGet", false, false);
		assertEquals("valueGet", store.get("key", "default"));
		store.put("key", "newValue", false, false);
		assertEquals("newValue", store.get("key", "default"));
	}

	@Test
	public void testPutGetStringNotEncrypted() {
		ICredentialsStore store = createCredentialsStore();
		store.put("key", "valueGet", false);
		assertEquals("valueGet", store.get("key", "default"));
	}

	@Test
	public void testRemoveEncrypted() {
		ICredentialsStore store = createCredentialsStore();
		store.put("key", "value", true);
		store.remove("key");
		assertEquals("default", store.get("key", "default"));
	}

	@Test
	public void testRemoveNonExistantEncrypted() {
		ICredentialsStore store = createCredentialsStore();
		store.remove("key2");
		assertEquals("default", store.get("key2", "default"));
	}

	@Test
	public void testRemoveNotEncrypted() {
		ICredentialsStore store = createCredentialsStore();
		store.put("key", "value", false);
		store.remove("key");
		assertEquals("default", store.get("key", "default"));
	}

	@Test
	public void testRemoveKeys() {
		ICredentialsStore store = createCredentialsStore();
		store.put("key", "value", false);
		store.remove("key");
		assertEquals("[]", Arrays.toString(store.keys()));
	}

	protected abstract ICredentialsStore createCredentialsStore();

}
