/*******************************************************************************
 * Copyright (c) 2012, 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.repositories.tests.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.eclipse.mylyn.internal.commons.repositories.core.InMemoryCredentialsStore;
import org.junit.Test;

/**
 * @author Steffen Pingel
 */
public class InMemoryCredentialsStoreTest extends AbstractCredentialsStoreTest {

	@Test
	public void testCopyToNullValue() {
		InMemoryCredentialsStore source = new InMemoryCredentialsStore();
		InMemoryCredentialsStore target = new InMemoryCredentialsStore();
		target.put("key", "value", false);
		source.put("key", null, false);
		source.copyTo(target);
		assertEquals(null, target.get("key", null));
	}

	@Test
	public void testCopyTo() {
		InMemoryCredentialsStore source = new InMemoryCredentialsStore();
		InMemoryCredentialsStore target = new InMemoryCredentialsStore();
		source.put("key1", "value", true);
		source.copyTo(target);
		assertEquals("value", target.get("key1", null));
	}

	@Test
	public void testCopyToRemove() {
		InMemoryCredentialsStore source = new InMemoryCredentialsStore();
		InMemoryCredentialsStore target = new InMemoryCredentialsStore();
		target.put("key", "value", true);
		source.remove("key");
		source.copyTo(target);
		assertEquals(null, target.get("key", null));
	}

	@Test
	public void testGetStore() {
		InMemoryCredentialsStore store = InMemoryCredentialsStore.getStore("test-store");
		assertNotNull(store);
		assertSame(store, InMemoryCredentialsStore.getStore("test-store"));
		assertNotSame(store, InMemoryCredentialsStore.getStore("test-store2"));
	}

	@Test
	public void testParentPutGetString() {
		InMemoryCredentialsStore parent = new InMemoryCredentialsStore();
		InMemoryCredentialsStore store = new InMemoryCredentialsStore(parent);
		parent.put("key", "parentValue", false);
		assertEquals("parentValue", store.get("key", null));
		store.put("key", "value", false);
		assertEquals("value", store.get("key", null));
	}

	@Test
	public void testParentCopyToRemove() {
		InMemoryCredentialsStore parent = new InMemoryCredentialsStore();
		InMemoryCredentialsStore store = new InMemoryCredentialsStore(parent);
		parent.put("key", "parentValue", false);
		store.remove("key");
		assertEquals("parentValue", store.get("key", null));
		parent.remove("key");
		assertEquals(null, store.get("key", null));
	}

	@Test
	public void testTestAvailability() throws Exception {
		InMemoryCredentialsStore store = createCredentialsStore();
		store.testAvailability();
	}

	@Test
	public void testGetId() {
		assertEquals("id", InMemoryCredentialsStore.getStore("id").getId());
	}

	@Override
	protected InMemoryCredentialsStore createCredentialsStore() {
		return new InMemoryCredentialsStore();
	}

}
