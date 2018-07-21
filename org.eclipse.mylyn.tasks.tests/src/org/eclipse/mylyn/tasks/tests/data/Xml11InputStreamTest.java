/*******************************************************************************
 * Copyright (c) 2011, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.data;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.InputStreamReader;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.core.data.TaskDataExternalizer.Xml11InputStream;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("resource")
public class Xml11InputStreamTest extends TestCase {

	public void testShortStream() throws Exception {
		ByteArrayInputStream source = new ByteArrayInputStream("foo bar".getBytes());
		Xml11InputStream in = new Xml11InputStream(source);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		try {
			assertEquals("<?xml version=\"1.1\" encoding=\"UTF-8\"?>", reader.readLine());
			fail("Expected EOFException");
		} catch (EOFException expected) {
		}
	}

	public void testXml10Stream() throws Exception {
		ByteArrayInputStream source = new ByteArrayInputStream("<?xml version=\"1.0\" encoding=\"UTF-8\"?>".getBytes());
		Xml11InputStream in = new Xml11InputStream(source);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		assertEquals("<?xml version=\"1.1\" encoding=\"UTF-8\"?>", reader.readLine());
		assertEquals(-1, in.read());
	}

	public void testXml10StreamMultiLines() throws Exception {
		ByteArrayInputStream source = new ByteArrayInputStream(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<abc>\n<def>".getBytes());
		Xml11InputStream in = new Xml11InputStream(source);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		assertEquals("<?xml version=\"1.1\" encoding=\"UTF-8\"?>", reader.readLine());
		assertEquals("<abc>", reader.readLine());
		assertEquals("<def>", reader.readLine());
		assertEquals(null, reader.readLine());
	}

	public void testSkipRead() throws Exception {
		ByteArrayInputStream source = new ByteArrayInputStream(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".getBytes());
		Xml11InputStream in = new Xml11InputStream(source);
		assertEquals(0, in.skip(0));
		assertEquals('<', in.read());
		assertEquals(5, in.skip(5));
		assertEquals('v', in.read());
	}

	public void testSkipReadLine() throws Exception {
		ByteArrayInputStream source = new ByteArrayInputStream(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".getBytes());
		Xml11InputStream in = new Xml11InputStream(source);
		assertEquals(3, in.skip(3));
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		assertEquals("ml version=\"1.1\" encoding=\"UTF-8\"?>", reader.readLine());
		assertEquals(null, reader.readLine());
	}

	public void testSkipHeader() throws Exception {
		ByteArrayInputStream source = new ByteArrayInputStream(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>abc".getBytes());
		Xml11InputStream in = new Xml11InputStream(source);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		assertEquals(38, reader.skip(38));
		assertEquals("abc", reader.readLine());
		assertEquals(null, reader.readLine());
	}

}
