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

package org.eclipse.mylyn.commons.tests.core.storage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.mylyn.commons.core.storage.CommonStore;
import org.eclipse.mylyn.commons.core.storage.ICommonStorable;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;

/**
 * @author Steffen Pingel
 */
public class CommonStoreTest extends TestCase {

	private File location;

	private CommonStore store;

	public void testDelete() throws Exception {
		ICommonStorable storable = store.get(Path.EMPTY);
		assertFalse(storable.exists("handle"));

		OutputStream out = storable.write("handle", null);
		out.close();
		assertTrue(storable.exists("handle"));
		assertEquals(Collections.singletonList(new File(location, "handle")), Arrays.asList(location.listFiles()));

		storable.delete("handle");
		assertFalse(storable.exists("handle"));
		assertEquals(Collections.emptyList(), Arrays.asList(location.listFiles()));
	}

	public void testDeleteAll() throws Exception {
		ICommonStorable storable = store.get(Path.EMPTY);
		writeHello(storable, "1");
		writeHello(storable, "2");
		storable.deleteAll();
		assertFalse(storable.exists("1"));
		assertFalse(location.exists());
	}

	public void testDeleteAllSubPath() throws Exception {
		ICommonStorable storable2 = store.get(new Path("sub2"));
		writeHello(storable2, "1");
		ICommonStorable storable = store.get(new Path("sub"));
		writeHello(storable, "1");
		writeHello(storable, "2");
		storable.deleteAll();
		assertFalse(storable.exists("1"));
		assertTrue(storable2.exists("1"));
		assertTrue(location.exists());
		assertFalse(new File(location, "sub").exists());
	}

	public void testDeleteAllSubPathException() throws Exception {
		ICommonStorable storable = store.get(new Path("sub"));
		writeHello(storable, "1");
		ICommonStorable storable2 = store.get(new Path("sub/sub2"));
		writeHello(storable2, "1");
		try {
			storable.deleteAll();
			fail("Expected CoreException");
		} catch (CoreException expected) {
			assertTrue(storable.exists("1"));
			assertTrue(storable2.exists("1"));
		}
		storable2.deleteAll();
		storable.deleteAll();
		assertTrue(location.exists());
		assertFalse(new File(location, "sub").exists());
	}

	public void testExists() throws Exception {
		ICommonStorable storable = store.get(Path.EMPTY);
		assertFalse(storable.exists("handle"));

		OutputStream out = storable.write("handle", null);
		out.close();
		assertTrue(storable.exists("handle"));
	}

	public void testGetPathWrite() throws Exception {
		ICommonStorable storable = store.get(new Path("sub"));
		writeHello(storable, "handle");
		File subFile = new File(location, "sub");
		assertEquals(Collections.singletonList(subFile), Arrays.asList(location.listFiles()));
		assertEquals(Collections.singletonList(new File(subFile, "handle")), Arrays.asList(subFile.listFiles()));
	}

	public void testGet() throws Exception {
		ICommonStorable storable = store.get(Path.EMPTY);
		ICommonStorable storable2 = store.get(Path.EMPTY);
		assertSame(storable, storable2);
	}

	public void testGetPath() {
		ICommonStorable storable = store.get(new Path("sub"));
		ICommonStorable storable2 = store.get(Path.EMPTY);
		assertNotSame(storable, storable2);
		storable2 = store.get(new Path("sub"));
		assertSame(storable, storable2);
	}

	public void testGetPathLazyCreate() {
		ICommonStorable storable = store.get(new Path("sub"));
		assertEquals(Collections.emptyList(), Arrays.asList(location.listFiles()));
		assertFalse(storable.exists("handle"));
		assertEquals(Collections.emptyList(), Arrays.asList(location.listFiles()));
	}

	public void testRelease() throws Exception {
		ICommonStorable storable = store.get(Path.EMPTY);
		storable.release();
		ICommonStorable storable2 = store.get(Path.EMPTY);
		assertNotSame(storable, storable2);
	}

	public void testWriteRead() throws Exception {
		ICommonStorable storable = store.get(Path.EMPTY);
		writeHello(storable, "handle");
		assertTrue(storable.exists("handle"));

		InputStream in = storable.read("handle", null);
		try {
			byte[] buffer = new byte[5];
			in.read(buffer);
			assertEquals("hello", new String(buffer));
		} finally {
			in.close();
		}
	}

	private void writeHello(ICommonStorable storable, String handle) throws IOException, CoreException {
		OutputStream out = storable.write(handle, null);
		try {
			out.write("hello".getBytes());
		} finally {
			out.close();
		}
	}

	@Override
	protected void setUp() throws Exception {
		location = CommonTestUtil.createTempFolder(CommonStoreTest.class.getName());
		store = new CommonStore(location);
	}

	@Override
	protected void tearDown() throws Exception {
		CommonTestUtil.deleteFolderRecursively(location);
	}

}
