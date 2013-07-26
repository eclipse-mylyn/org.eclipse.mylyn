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
import org.eclipse.mylyn.internal.gerrit.core.client.rest.CommentInfo;
import org.junit.Test;

public class CommentInfoTest extends TestCase {

	@Test
	public void testFromEmptyJson() throws Exception {
		CommentInfo commentInfo = parseFile("testdata/EmptyWithMagic.json");

		assertNotNull(commentInfo);
		assertNull(commentInfo.getId());
		assertEquals("gerritcodereview#comment", commentInfo.getKind());
		assertNull(commentInfo.getMessage());
		assertNull(commentInfo.getPath());
		assertEquals(0, commentInfo.getLine());
	}

	@Test
	public void testFromInvalid() throws Exception {
		CommentInfo commentInfo = parseFile("testdata/InvalidWithMagic.json");

		assertNotNull(commentInfo);
		assertNull(commentInfo.getId());
		assertEquals("gerritcodereview#comment", commentInfo.getKind());
		assertNull(commentInfo.getMessage());
		assertNull(commentInfo.getPath());
		assertEquals(0, commentInfo.getLine());
	}

	@Test
	public void testFromCodeReviewMinusOne() throws Exception {
		CommentInfo commentInfo = parseFile("testdata/CommentInfo_draft.json");

		assertNotNull(commentInfo);
		assertEquals("gerritcodereview#comment", commentInfo.getKind());
		assertEquals("daeb3561_e122c600", commentInfo.getId());
		assertEquals("Line 2 Comment", commentInfo.getMessage());
		assertEquals(2, commentInfo.getLine());
	}

	private CommentInfo parseFile(String path) throws IOException {
		File file = CommonTestUtil.getFile(this, path);
		String content = CommonTestUtil.read(file);
		return new JSonSupport().parseResponse(content, CommentInfo.class);
	}

}
