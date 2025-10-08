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
import org.eclipse.mylyn.internal.commons.repositories.core.InMemoryCredentialsStore;
import org.eclipse.mylyn.internal.commons.repositories.core.SecureCredentialsStore;
import org.junit.Test;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class CredentialsStoreTest {

	@Test
	public void testCopyInMemoryToSecure() {
		InMemoryCredentialsStore source = new InMemoryCredentialsStore();
		SecureCredentialsStore target = new SecureCredentialsStore(CredentialsStoreTest.class.getName());
		target.clear();

		putValues(source);
		source.copyTo(target);
		assertValues(target);
	}

	@Test
	public void testCopyInMemoryToInMemory() {
		InMemoryCredentialsStore source = new InMemoryCredentialsStore();
		InMemoryCredentialsStore target = new InMemoryCredentialsStore();

		putValues(source);
		source.copyTo(target);
		assertValues(target);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCopySecureToInMemory() {
		SecureCredentialsStore source = new SecureCredentialsStore(CredentialsStoreTest.class.getName());
		source.clear();
		InMemoryCredentialsStore target = new InMemoryCredentialsStore();
		source.copyTo(target);
	}

	@Test
	public void testCopySecureToSecure() {
		SecureCredentialsStore source = new SecureCredentialsStore(CredentialsStoreTest.class.getName());
		source.clear();
		SecureCredentialsStore target = new SecureCredentialsStore(CredentialsStoreTest.class.getName() + "2");
		target.clear();

		putValues(source);
		source.copyTo(target);
		assertValues(target);
	}

	private void assertValues(ICredentialsStore target) {
		assertEquals("value", target.get("key1", null));
		assertEquals(true, target.getBoolean("key2", false));
		assertEquals(Arrays.toString(new byte[] { 0x00, 0x05 }), Arrays.toString(target.getByteArray("key3", null)));
		assertEquals("value2", target.get("keyNotEncrypted", null));
	}

	private void putValues(ICredentialsStore source) {
		source.put("key1", "value", true);
		source.putBoolean("key2", true, true);
		source.putByteArray("key3", new byte[] { 0x00, 0x05 }, true);
		source.put("keyNotEncrypted", "value2", false);
	}

}
