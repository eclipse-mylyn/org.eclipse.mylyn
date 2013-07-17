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

package org.eclipse.mylyn.gerrit.tests.core.client.rest;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.internal.gerrit.core.client.JSonSupport;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.AbandonInput;
import org.junit.Test;

public class AbandonInputTest extends TestCase {

	@Test(expected = IllegalArgumentException.class)
	public void testFromNull() throws Exception {
		try {
			new AbandonInput(null);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// expected
		}
	}

	@Test
	public void testFromEmpty() throws Exception {
		AbandonInput abandonInput = new AbandonInput("");

		String json = new JSonSupport().getGson().toJson(abandonInput);

		assertNotNull(json);
		assertFalse(json.isEmpty());
		assertEquals(readFile("testdata/AbandonInput_emptyMessage.json"), json);
	}

	@Test
	public void testFromValid() throws Exception {
		AbandonInput abandonInput = new AbandonInput("No go!");

		String json = new JSonSupport().getGson().toJson(abandonInput);

		assertNotNull(json);
		assertFalse(json.isEmpty());
		assertEquals(readFile("testdata/AbandonInput_message.json"), json);
	}

	private String readFile(String path) throws IOException {
		File file = CommonTestUtil.getFile(this, path);
		return CommonTestUtil.read(file);
	}
}
