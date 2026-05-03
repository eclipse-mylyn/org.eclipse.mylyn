/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.gerrit.tests.core.client.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;

import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.internal.gerrit.core.client.JSonSupport;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.RestoreInput;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
public class RestoreInputTest {

	@Test
	public void testFromNull() throws Exception {
		assertThrows(IllegalArgumentException.class, () -> new RestoreInput(null));
	}

	@Test
	public void testFromEmpty() throws Exception {
		RestoreInput restoreInput = new RestoreInput("");

		String json = new JSonSupport().toJson(restoreInput);

		assertNotNull(json);
		assertFalse(json.isEmpty());
		assertEquals(readFile("testdata/EmptyMessage.json"), json);
	}

	@Test
	public void testFromValid() throws Exception {
		RestoreInput restoreInput = new RestoreInput("Whatever");

		String json = new JSonSupport().toJson(restoreInput);

		assertNotNull(json);
		assertFalse(json.isEmpty());
		assertEquals(readFile("testdata/Message.json"), json);
	}

	private String readFile(String path) throws IOException {
		File file = CommonTestUtil.getFile(this, path);
		return CommonTestUtil.read(file);
	}
}
