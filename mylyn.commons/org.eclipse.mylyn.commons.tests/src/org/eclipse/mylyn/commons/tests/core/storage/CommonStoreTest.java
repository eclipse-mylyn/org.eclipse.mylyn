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
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.commons.tests.core.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.mylyn.commons.core.storage.CommonStore;
import org.eclipse.mylyn.commons.core.storage.ICommonStorable;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings({ "nls", "restriction" })
public class CommonStoreTest {

	private File location;

	private CommonStore store;

	@Test
	public void testDelete() throws Exception {
		ICommonStorable storable = store.get(Path.EMPTY);
		assertFalse(storable.exists("handle"));

		try (OutputStream out = storable.write("handle", null)) {
		}
		assertTrue(storable.exists("handle"));
		assertEquals(Collections.singletonList(new File(location, "handle")), Arrays.asList(location.listFiles()));

		storable.delete("handle");
		assertFalse(storable.exists("handle"));
		assertEquals(Collections.emptyList(), Arrays.asList(location.listFiles()));
	}

	@Test
	public void testDeleteAll() throws Exception {
		ICommonStorable storable = store.get(Path.EMPTY);
		writeHello(storable, "1");
		writeHello(storable, "2");
		storable.deleteAll();
		assertFalse(storable.exists("1"));
		assertFalse(location.exists());
	}

	@Test
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

	@Test
	public void testDeleteAllSubPathException() throws Exception {
		ICommonStorable storable = store.get(new Path("sub"));
		writeHello(storable, "1");
		ICommonStorable storable2 = store.get(new Path("sub/sub2"));
		writeHello(storable2, "1");

		assertThrows(CoreException.class, () -> storable.deleteAll());
		assertTrue(storable.exists("1"));
		assertTrue(storable2.exists("1"));

		storable2.deleteAll();
		storable.deleteAll();
		assertTrue(location.exists());
		assertFalse(new File(location, "sub").exists());
	}

	@Test
	public void testExists() throws Exception {
		ICommonStorable storable = store.get(Path.EMPTY);
		assertFalse(storable.exists("handle"));

		try (OutputStream out = storable.write("handle", null)) {
		}
		assertTrue(storable.exists("handle"));
	}

	@Test
	public void testGet() throws Exception {
		ICommonStorable storable = store.get(Path.EMPTY);
		ICommonStorable storable2 = store.get(Path.EMPTY);
		assertSame(storable, storable2);
	}

	@Test
	public void testGetPath() {
		ICommonStorable storable = store.get(new Path("sub"));
		ICommonStorable storable2 = store.get(Path.EMPTY);
		assertNotSame(storable, storable2);
		storable2 = store.get(new Path("sub"));
		assertSame(storable, storable2);
	}

	@Test
	public void testGetPathLazyCreate() {
		ICommonStorable storable = store.get(new Path("sub"));
		assertEquals(Collections.emptyList(), Arrays.asList(location.listFiles()));
		assertFalse(storable.exists("handle"));
		assertEquals(Collections.emptyList(), Arrays.asList(location.listFiles()));
	}

	@Test
	public void testGetPathWrite() throws Exception {
		ICommonStorable storable = store.get(new Path("sub"));
		writeHello(storable, "handle");
		File subFile = new File(location, "sub");
		assertEquals(Collections.singletonList(subFile), Arrays.asList(location.listFiles()));
		assertEquals(Collections.singletonList(new File(subFile, "handle")), Arrays.asList(subFile.listFiles()));
	}

	@Test
	public void testMove() throws Exception {
		ICommonStorable storable = store.get(new Path("source"));
		writeHello(storable, "handle");
		store.move(new Path("source"), new Path("target"));
		File targetFile = new File(location, "target");
		assertEquals(Collections.singletonList(targetFile), Arrays.asList(location.listFiles()));
		assertEquals(Collections.singletonList(new File(targetFile, "handle")), Arrays.asList(targetFile.listFiles()));
	}

	@Test
	public void testMoveExistant() throws Exception {
		ICommonStorable storable = store.get(new Path("source"));
		writeHello(storable, "handle");
		ICommonStorable storable2 = store.get(new Path("target"));
		writeHello(storable2, "handle2");

		assertThrows(CoreException.class, () -> store.move(new Path("source"), new Path("target")));
		File sourceFile = new File(location, "source");
		File targetFile = new File(location, "target");
		List<File> list = Arrays.asList(location.listFiles());
		Collections.sort(list);
		assertEquals(Arrays.asList(sourceFile, targetFile), list);
		assertEquals(Collections.singletonList(new File(targetFile, "handle2")),
				Arrays.asList(targetFile.listFiles()));
	}

	@Test
	public void testMoveNonExistant() throws Exception {
		store.move(new Path("source"), new Path("target"));
		assertEquals(Collections.emptyList(), Arrays.asList(location.listFiles()));
	}

	@Test
	public void testRelease() throws Exception {
		ICommonStorable storable = store.get(Path.EMPTY);
		storable.release();
		ICommonStorable storable2 = store.get(Path.EMPTY);
		assertNotSame(storable, storable2);
	}

	@Test
	public void testWriteRead() throws Exception {
		ICommonStorable storable = store.get(Path.EMPTY);
		writeHello(storable, "handle");
		assertTrue(storable.exists("handle"));

		try (InputStream in = storable.read("handle", null)) {
			byte[] buffer = new byte[5];
			in.read(buffer);
			assertEquals("hello", new String(buffer));
		}
	}

	private void writeHello(ICommonStorable storable, String handle) throws IOException, CoreException {
		try (OutputStream out = storable.write(handle, null)) {
			out.write("hello".getBytes());
		}
	}

	@BeforeEach
	void setUp() throws Exception {
		location = CommonTestUtil.createTempFolder(CommonStoreTest.class.getName());
		store = new CommonStore(location);
	}

	@AfterEach
	void tearDown() throws Exception {
		CommonTestUtil.deleteFolderRecursively(location);
	}

}
