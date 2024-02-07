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

import java.io.File;
import java.io.IOException;

import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.internal.gerrit.core.client.JSonSupport;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.SubmitInfo;
import org.junit.Test;

import junit.framework.TestCase;

@SuppressWarnings("nls")
public class SubmitInfoTest extends TestCase {

	@Test
	public void testFromValid() throws IOException {
		SubmitInfo submitInfo = parseFile("testdata/SubmitInfo_merged.json");

		assertEquals(SubmitInfo.Status.MERGED, submitInfo.getStatus());
	}

	private SubmitInfo parseFile(String path) throws IOException {
		File file = CommonTestUtil.getFile(this, path);
		String content = CommonTestUtil.read(file);
		return new JSonSupport().parseResponse(content, SubmitInfo.class);
	}
}
