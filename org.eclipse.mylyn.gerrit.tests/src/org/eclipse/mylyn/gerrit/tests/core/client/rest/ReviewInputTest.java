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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.internal.gerrit.core.client.JSonSupport;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.ApprovalUtil;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.ReviewInput;
import org.junit.Test;

import com.google.gerrit.reviewdb.ApprovalCategoryValue;

public class ReviewInputTest extends TestCase {

	@Test
	public void testFromNull() throws Exception {
		try {
			new ReviewInput(null);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// expected
		}
	}

	@Test
	public void testFromEmpty() throws Exception {
		ReviewInput reviewInput = new ReviewInput("");

		String json = new JSonSupport().getGson().toJson(reviewInput);

		assertNotNull(json);
		assertFalse(json.isEmpty());
		assertEquals(readFile("testdata/ReviewInput_emptyMessage.json"), json);
	}

	@Test
	public void testFromValid() throws Exception {
		ReviewInput reviewInput = new ReviewInput("Looking good!");

		String json = new JSonSupport().getGson().toJson(reviewInput);

		assertNotNull(json);
		assertFalse(json.isEmpty());
		assertEquals(readFile("testdata/ReviewInput_message.json"), json);
	}

	@Test
	public void testSetNullApprovals() throws Exception {
		ReviewInput reviewInput = new ReviewInput("");
		reviewInput.setApprovals(null);

		String json = new JSonSupport().getGson().toJson(reviewInput);

		assertNotNull(json);
		assertFalse(json.isEmpty());
		assertEquals(readFile("testdata/ReviewInput_emptyMessage.json"), json);
	}

	@Test
	public void testSetEmptyApprovals() throws Exception {
		ReviewInput reviewInput = new ReviewInput("");
		reviewInput.setApprovals(Collections.<ApprovalCategoryValue.Id> emptySet());

		String json = new JSonSupport().getGson().toJson(reviewInput);

		assertNotNull(json);
		assertFalse(json.isEmpty());
		assertEquals(readFile("testdata/ReviewInput_emptyMessage.json"), json);
	}

	@Test
	public void testSetApprovals() throws Exception {
		ReviewInput reviewInput = new ReviewInput("");
		Set<ApprovalCategoryValue.Id> approvals = new HashSet<ApprovalCategoryValue.Id>(1);
		approvals.add(ApprovalUtil.CRVW.getValue((short) 1).getId());
		reviewInput.setApprovals(approvals);

		String json = new JSonSupport().getGson().toJson(reviewInput);

		assertNotNull(json);
		assertFalse(json.isEmpty());
		assertEquals(readFile("testdata/ReviewInput_emptyMessageCodeReviewPlusOne.json"), json);
	}

	private String readFile(String path) throws IOException {
		File file = CommonTestUtil.getFile(this, path);
		return CommonTestUtil.read(file);
	}
}
