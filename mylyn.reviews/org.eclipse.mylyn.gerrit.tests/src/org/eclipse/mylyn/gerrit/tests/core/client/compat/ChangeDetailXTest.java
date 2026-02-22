/*******************************************************************************
 * Copyright (c) 2013, 2015 Tasktop Technologies and others.
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

package org.eclipse.mylyn.gerrit.tests.core.client.compat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.internal.gerrit.core.client.JSonSupport;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.ChangeDetailX;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.GerritConfigX;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.SubmitRecord;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.SubmitRecord.Label;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.ApprovalUtil;
import org.junit.Test;

import com.google.gerrit.common.data.ApprovalType;
import com.google.gerrit.common.data.ApprovalTypes;
import com.google.gerrit.reviewdb.ChangeMessage;

import junit.framework.TestCase;

@SuppressWarnings("nls")
public class ChangeDetailXTest extends TestCase {
	@Test
	public void testCustomLabel() throws Exception {
		ChangeDetailXAsResult result = parseFile("testdata/ChangeDetailX.json");

		assertEquals("2.0", result.jsonrpc);
		assertEquals(1, result.id);
		assertNotNull(result.result);

		ChangeDetailX changeDetailX = result.result;

		assertNull(changeDetailX.getApprovalTypes());
		assertEquals(1, changeDetailX.getSubmitRecords().size());
		SubmitRecord submitRecord = changeDetailX.getSubmitRecords().get(0);
		assertEquals("NOT_READY", submitRecord.getStatus());
		assertEquals(3, submitRecord.getLabels().size());

		Iterator<Label> labels = submitRecord.getLabels().iterator();
		assertLabelEqual("Non-Author-Code-Review", labels.next());
		assertLabelEqual("Verified", labels.next());
		assertLabelEqual("Code-Review", labels.next());

		changeDetailX.convertSubmitRecordsToApprovalTypes(getTestConfig().getApprovalTypes());
		assertNotNull(changeDetailX.getApprovalTypes());
		assertEquals(3, changeDetailX.getApprovalTypes().size());
		Iterator<ApprovalType> approvalTypes = changeDetailX.getApprovalTypes().iterator();
		ApprovalType custom = approvalTypes.next();
		assertEquals("Non-Author-Code-Review", custom.getCategory().getName());
		// that's all we know about the approval, the rest is void
		assertNull(custom.getCategory().getAbbreviatedName());
		assertTrue(custom.getValues().isEmpty());
		assertCategoriesEqual(ApprovalUtil.VRIF, approvalTypes.next());
		assertCategoriesEqual(ApprovalUtil.CRVW, approvalTypes.next());
	}

	@Test
	public void testConvertWhenApprovalTypesNotNull() throws Exception {
		ChangeDetailX changeDetailX = new ChangeDetailX();
		changeDetailX.setApprovalTypes(Collections.singleton(ApprovalUtil.CRVW));

		try {
			changeDetailX.convertSubmitRecordsToApprovalTypes(getTestConfig().getApprovalTypes());
			fail();
		} catch (IllegalStateException e) {
			// expected
		}
	}

	@Test
	public void testAbandoned() throws Exception {
		ChangeDetailXAsResult result = parseFile("testdata/ChangeDetailX_abandoned.json");

		assertEquals("2.0", result.jsonrpc);
		assertEquals(3, result.id);
		assertNotNull(result.result);

		ChangeDetailX changeDetailX = result.result;

		assertNull(changeDetailX.getApprovalTypes());
		assertNull(changeDetailX.getSubmitRecords());

		changeDetailX.convertSubmitRecordsToApprovalTypes(getTestConfig().getApprovalTypes());
		assertNull(changeDetailX.getApprovalTypes()); // nothing has changed
	}

	@Test
	public void testContinousIntegrationCommentsAreRemoved() throws Exception {
		ChangeDetailXAsResult result = parseFile("testdata/ChangeDetailX_hudson.json");
		ChangeDetailX changeDetailX = result.result;

		assertEquals(1, changeDetailX.getMessages().size());

		// Make sure the message with the Hudson message was removed
		ChangeMessage lastMessageLeft = changeDetailX.getMessages().get(0);
		assertFalse(lastMessageLeft.getMessage().contains("hudson.eclipse.org"));
	}

	private void assertLabelEqual(String expectedLabel, Label label) {
		assertEquals(expectedLabel, label.getLabel());
		assertEquals("NEED", label.getStatus());
		assertNull(label.getAppliedBy());
	}

	private static void assertCategoriesEqual(ApprovalType expected, ApprovalType actual) {
		assertEquals(expected.getCategory().getId().get(), actual.getCategory().getId().get());
	}

	private static GerritConfigX getTestConfig() {
		GerritConfigX result = new GerritConfigX();
		List<ApprovalType> approvals = new ArrayList<>(3);
		approvals.add(ApprovalUtil.CRVW);
		approvals.add(ApprovalUtil.VRIF);
		approvals.add(ApprovalUtil.IPCL);
		result.setApprovalTypes(new ApprovalTypes(approvals, null));
		return result;
	}

	private ChangeDetailXAsResult parseFile(String path) throws IOException {
		File file = CommonTestUtil.getFile(this, path);
		String content = CommonTestUtil.read(file);
		return new JSonSupport().parseResponse(content, ChangeDetailXAsResult.class);
	}

	private static class ChangeDetailXAsResult {
		private String jsonrpc;

		private int id;

		private ChangeDetailX result;
	}
}
