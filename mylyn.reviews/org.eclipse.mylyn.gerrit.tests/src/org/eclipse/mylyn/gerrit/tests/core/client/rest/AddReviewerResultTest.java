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
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.internal.gerrit.core.client.JSonSupport;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.AddReviewerResult;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.ReviewerInfo;
import org.junit.Test;

public class AddReviewerResultTest extends TestCase {
	@Test
	public void testFromEmptyJson() throws Exception {
		AddReviewerResult addReviewerResult = parseFile("testdata/EmptyWithMagic.json");

		assertNotNull(addReviewerResult);
		assertNull(addReviewerResult.getError());
		assertNull(addReviewerResult.getReviewers());
	}

	@Test
	public void testFromInvalid() throws Exception {
		AddReviewerResult addReviewerResult = parseFile("testdata/InvalidWithMagic.json");

		assertNotNull(addReviewerResult);
		assertNull(addReviewerResult.getError());
		assertNull(addReviewerResult.getReviewers());
	}

	@Test
	public void testFromValid() throws Exception {
		AddReviewerResult addReviewerResult = parseFile("testdata/AddReviewerResult_reviewers.json");

		assertNotNull(addReviewerResult);
		assertNull(addReviewerResult.getError());
		List<ReviewerInfo> reviewers = addReviewerResult.getReviewers();
		assertNotNull(reviewers);
		assertEquals(2, reviewers.size());
		ReviewerInfo johnDoe = reviewers.get(0);
		ReviewerInfo janeRoe = reviewers.get(1);
		assertAccountInfo(johnDoe, 1000096, "John Doe", "john.doe@example.com");
		assertAccountInfo(janeRoe, 1000097, "Jane Roe", "jane.roe@example.com");
		assertApprovals(johnDoe, "+1", "+2");
		assertApprovals(janeRoe, " 0", "-1");
	}

	private static void assertAccountInfo(ReviewerInfo reviewerInfo, int id, String name, String email) {
		assertEquals(id, reviewerInfo.getId());
		assertEquals(name, reviewerInfo.getName());
		assertEquals(email, reviewerInfo.getEmail());
		assertNull(reviewerInfo.getUsername());
	}

	private static void assertApprovals(ReviewerInfo reviewerInfo, String verified, String codeReview) {
		assertNotNull(reviewerInfo.getApprovals());
		assertEquals(2, reviewerInfo.getApprovals().size());
		assertEquals(verified, reviewerInfo.getApprovals().get("Verified"));
		assertEquals(codeReview, reviewerInfo.getApprovals().get("Code-Review"));
	}

	private AddReviewerResult parseFile(String path) throws IOException {
		File file = CommonTestUtil.getFile(this, path);
		String content = CommonTestUtil.read(file);
		return new JSonSupport().parseResponse(content, AddReviewerResult.class);
	}
}
