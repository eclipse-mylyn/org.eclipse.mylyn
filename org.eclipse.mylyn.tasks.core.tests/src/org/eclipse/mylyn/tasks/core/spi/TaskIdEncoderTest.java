/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core.spi;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.mylyn.internal.tasks.core.RepositoryTaskHandleUtil;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TaskIdEncoderTest {
	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	@Test
	public void testEncodeNull() {
		thrown.expect(AssertionFailedException.class);
		TaskIdEncoder.encode(null);
	}

	@Test
	public void testDecodeNull() {
		thrown.expect(AssertionFailedException.class);
		TaskIdEncoder.decode(null);
	}

	@Test
	public void testEncodeSimple() {
		assertEquals("1234 abc", TaskIdEncoder.encode("1234 abc"));
	}

	@Test
	public void testEncodeComplex() {
		assertEquals("1234%25%2Dabc @$", TaskIdEncoder.encode("1234%-abc @$"));
	}

	@Test
	public void testDecodeSimple() {
		assertEquals("1234 abc", TaskIdEncoder.decode("1234 abc"));
	}

	@Test
	public void testDecodeComplex() {
		assertEquals("1234%-abc @$", TaskIdEncoder.decode("1234%25%2Dabc @$"));
	}

	@Test
	public void testUuidRoundTrip() {
		assertRoundTrip(UUID.randomUUID().toString());
	}

	@Test
	public void testHyphenatedRoundTrip() {
		assertRoundTrip("TEST-1234");
	}

	@Test
	public void testJsonRoundTrip() {
		assertRoundTrip("{\"one\":true,\"two\":\"three\"}");
	}

	@Test
	public void testJsonReadable() {
		String json = "{\"one\":true,\"two\":\"three\"}";
		assertEquals(json, TaskIdEncoder.encode(json));
	}

	private void assertRoundTrip(String original) {
		String encoded = TaskIdEncoder.encode(original);

		String handle = RepositoryTaskHandleUtil.getHandle("https://example.com", encoded);

		assertTrue(handle.endsWith(encoded));
		assertEquals(original, TaskIdEncoder.decode(encoded));
	}
}
