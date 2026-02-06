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
import java.util.Map;

import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.internal.gerrit.core.client.JSonSupport;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.ApprovalUtil;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.ReviewerInfo;
import org.junit.Test;

import com.google.gerrit.common.data.ApprovalDetail;
import com.google.gerrit.reviewdb.Account;
import com.google.gerrit.reviewdb.ApprovalCategory;
import com.google.gerrit.reviewdb.Change;
import com.google.gerrit.reviewdb.PatchSet;
import com.google.gerrit.reviewdb.PatchSetApproval;

import junit.framework.TestCase;

@SuppressWarnings("nls")
public class ReviewerInfoTest extends TestCase {
	@Test
	public void testFromEmptyJson() throws Exception {
		ReviewerInfo reviewerInfo = parseFile("testdata/EmptyWithMagic.json");

		assertNotNull(reviewerInfo);
		// always available
		assertEquals("gerritcodereview#reviewer", reviewerInfo.getKind());
		assertNull(reviewerInfo.getApprovals());
		// from AccountInfo
		assertEquals(-1, reviewerInfo.getId());
		assertNull(reviewerInfo.getName());
		assertNull(reviewerInfo.getEmail());
		assertNull(reviewerInfo.getUsername());
	}

	@Test
	public void testFromInvalid() throws Exception {
		ReviewerInfo reviewerInfo = parseFile("testdata/InvalidWithMagic.json");

		assertNotNull(reviewerInfo);
		// always available
		assertEquals("gerritcodereview#reviewer", reviewerInfo.getKind());
		assertNull(reviewerInfo.getApprovals());
		// from AccountInfo
		assertEquals(-1, reviewerInfo.getId());
		assertNull(reviewerInfo.getName());
		assertNull(reviewerInfo.getEmail());
		assertNull(reviewerInfo.getUsername());
	}

	@Test
	public void testFromValid() throws IOException {
		ReviewerInfo reviewerInfo = parseFile("testdata/ReviewerInfo_JohnDoePlusTwo.json");

		assertEquals("gerritcodereview#reviewer", reviewerInfo.getKind());
		Map<String, String> approvals = reviewerInfo.getApprovals();
		assertNotNull(approvals);
		assertFalse(approvals.isEmpty());
		assertEquals(2, approvals.size());
		assertEquals("+1", approvals.get("Verified"));
		assertEquals("+2", approvals.get("Code-Review"));
		// from AccountInfo
		assertEquals(1000096, reviewerInfo.getId());
		assertEquals("John Doe", reviewerInfo.getName());
		assertEquals("john.doe@example.com", reviewerInfo.getEmail());
		assertNull(reviewerInfo.getUsername());

		// toApprovalDetail
		PatchSet dummyPatchSet = new PatchSet(new PatchSet.Id(new Change.Id(1), 1));
		ApprovalDetail approvalDetail = reviewerInfo.toApprovalDetail(dummyPatchSet);
		assertNotNull(approvalDetail);
		assertEquals(new Account.Id(1000096), approvalDetail.getAccount());
		Map<ApprovalCategory.Id, PatchSetApproval> approvalMap = approvalDetail.getApprovalMap();
		assertNotNull(approvalMap);
		assertEquals(2, approvalMap.size());
		PatchSetApproval crvw = approvalMap.get(ApprovalUtil.CRVW.getCategory().getId());
		assertNotNull(crvw);
		assertEquals(1000096, crvw.getAccountId().get());
		assertEquals(2, crvw.getValue());
		PatchSetApproval vrif = approvalMap.get(ApprovalUtil.VRIF.getCategory().getId());
		assertNotNull(vrif);
		assertEquals(1000096, vrif.getAccountId().get());
		assertEquals(1, vrif.getValue());

		// toAccountInfo
		com.google.gerrit.common.data.AccountInfo accountInfo = reviewerInfo.toAccountInfo();
		assertNotNull(accountInfo);
		assertEquals(1000096, accountInfo.getId().get());
		assertEquals("john.doe@example.com", accountInfo.getPreferredEmail());
		assertEquals("John Doe", accountInfo.getFullName());
	}

	private ReviewerInfo parseFile(String path) throws IOException {
		File file = CommonTestUtil.getFile(this, path);
		String content = CommonTestUtil.read(file);
		return new JSonSupport().parseResponse(content, ReviewerInfo.class);
	}
}
