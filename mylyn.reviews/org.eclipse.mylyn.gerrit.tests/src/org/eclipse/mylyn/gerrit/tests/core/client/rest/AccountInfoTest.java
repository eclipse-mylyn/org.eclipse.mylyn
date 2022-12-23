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
 *******************************************************************************/

package org.eclipse.mylyn.gerrit.tests.core.client.rest;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.internal.gerrit.core.client.JSonSupport;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.AccountInfo;
import org.junit.Test;

public class AccountInfoTest extends TestCase {

	@Test
	public void testFromEmptyJson() throws Exception {
		AccountInfo accountInfo = parseFile("testdata/EmptyWithMagic.json");

		assertNotNull(accountInfo);
		assertEquals(-1, accountInfo.getId());
		assertNull(accountInfo.getName());
		assertNull(accountInfo.getEmail());
		assertNull(accountInfo.getUsername());
	}

	@Test
	public void testFromInvalid() throws Exception {
		AccountInfo accountInfo = parseFile("testdata/InvalidWithMagic.json");

		assertNotNull(accountInfo);
		assertEquals(-1, accountInfo.getId());
		assertNull(accountInfo.getName());
		assertNull(accountInfo.getEmail());
		assertNull(accountInfo.getUsername());
	}

	@Test
	public void testFromValid() throws IOException {
		AccountInfo accountInfo = parseFile("testdata/AccountInfo_johnDoe.json");

		assertEquals(1000195, accountInfo.getId());
		assertEquals("John Doe", accountInfo.getName());
		assertEquals("john.doe@example.com", accountInfo.getEmail());
		assertNull(accountInfo.getUsername());
	}

	private AccountInfo parseFile(String path) throws IOException {
		File file = CommonTestUtil.getFile(this, path);
		String content = CommonTestUtil.read(file);
		return new JSonSupport().parseResponse(content, AccountInfo.class);
	}
}
