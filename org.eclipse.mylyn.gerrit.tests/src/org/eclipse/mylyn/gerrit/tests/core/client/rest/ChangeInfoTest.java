/*******************************************************************************
 * Copyright (c) 2013, 2014 Tasktop Technologies and others.
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

import static org.eclipse.mylyn.gerrit.tests.core.client.rest.IsEmpty.empty;
import static org.eclipse.mylyn.internal.gerrit.core.client.rest.ApprovalUtil.CRVW;
import static org.eclipse.mylyn.internal.gerrit.core.client.rest.ApprovalUtil.toNameWithDash;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.internal.gerrit.core.client.JSonSupport;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.PermissionLabel;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.AccountInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.ApprovalInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.ApprovalUtil;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.ChangeInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.LabelInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.RevisionInfo;
import org.hamcrest.Matchers;
import org.junit.Test;

import com.google.gerrit.common.data.ApprovalDetail;
import com.google.gerrit.common.data.ApprovalType;
import com.google.gerrit.reviewdb.Account;
import com.google.gerrit.reviewdb.ApprovalCategory.Id;
import com.google.gerrit.reviewdb.ApprovalCategoryValue;
import com.google.gerrit.reviewdb.Change;
import com.google.gerrit.reviewdb.PatchSet;
import com.google.gerrit.reviewdb.PatchSetApproval;

public class ChangeInfoTest extends TestCase {
	@Test
	public void testFromEmptyJson() throws Exception {
		ChangeInfo changeInfo = parseFile("testdata/EmptyWithMagic.json");

		assertNotNull(changeInfo);
		assertNull(changeInfo.getKind());
		assertNull(changeInfo.getId());
		assertNull(changeInfo.getProject());
		assertNull(changeInfo.getBranch());
		assertNull(changeInfo.getTopic());
		assertNull(changeInfo.getChangeId());
		assertNull(changeInfo.getSubject());
		assertNull(changeInfo.getStatus());
		assertNull(changeInfo.getCreated());
		assertNull(changeInfo.getUpdated());
		assertEquals(false, changeInfo.isReviewed());
		assertEquals(false, changeInfo.isMergeable());
		assertNull(changeInfo.getOwner());
	}

	@Test
	public void testFromInvalid() throws Exception {
		ChangeInfo changeInfo = parseFile("testdata/InvalidWithMagic.json");

		assertNotNull(changeInfo);
		assertNotNull(changeInfo);
		assertNull(changeInfo.getKind());
		assertNull(changeInfo.getId());
		assertNull(changeInfo.getProject());
		assertNull(changeInfo.getBranch());
		assertNull(changeInfo.getTopic());
		assertNull(changeInfo.getChangeId());
		assertNull(changeInfo.getSubject());
		assertNull(changeInfo.getStatus());
		assertNull(changeInfo.getCreated());
		assertNull(changeInfo.getUpdated());
		assertEquals(false, changeInfo.isReviewed());
		assertEquals(false, changeInfo.isMergeable());
		assertNull(changeInfo.getOwner());
	}

	@Test
	public void testFromAbandoned() throws Exception {
		ChangeInfo changeInfo = parseFile("testdata/ChangeInfo_abandoned.json");

		assertNotNull(changeInfo);
		assertEquals(changeInfo.getKind(), "gerritcodereview#change");
		assertEquals(changeInfo.getId(), "myProject~master~I8473b95934b5732ac55d26311a706c9c2bde9940");
		assertEquals(changeInfo.getProject(), "myProject");
		assertEquals(changeInfo.getBranch(), "master");
		assertEquals(changeInfo.getTopic(), "My Topic");
		assertEquals("I8473b95934b5732ac55d26311a706c9c2bde9940", changeInfo.getChangeId());
		assertEquals("Implementing Feature X", changeInfo.getSubject());
		assertEquals(Change.Status.ABANDONED, changeInfo.getStatus());
		assertEquals(timestamp("2013-02-01 09:59:32.126"), changeInfo.getCreated());
		assertEquals(timestamp("2013-02-21 11:16:36.775"), changeInfo.getUpdated());
		assertEquals(true, changeInfo.isReviewed());
		assertEquals(true, changeInfo.isMergeable());
		AccountInfo changeOwner = changeInfo.getOwner();
		assertNotNull(changeOwner);
		assertEquals("John Doe", changeOwner.getName());
		assertNull(changeOwner.getEmail());
		assertNull(changeOwner.getUsername());
		assertEquals(-1, changeOwner.getId());
	}

	@Test
	public void testNewChangeInfo() {
		ChangeInfo changeInfo = new ChangeInfo();

		assertThat(changeInfo.getLabels(), nullValue());
		// reviews, no labels = no reviews
		assertThat(changeInfo.convertToApprovalDetails(), empty());
		assertThat(changeInfo.convertToApprovalTypes(), nullValue());
		assertThat(changeInfo.getRevisions(), nullValue());
	}

	@Test
	public void testNoReviews() throws Exception {
		ChangeInfo changeInfo = parseFile("testdata/ChangeInfo_NoReviews.json");

		assertHasCodeReviewLabels(changeInfo);
		assertThat(changeInfo.getLabels().get("Code-Review").getAll(), nullValue());
		assertThat(changeInfo.convertToApprovalDetails(), empty());
		assertHasCodeReviewApprovalType(changeInfo.convertToApprovalTypes());
		// no permitted labels
		assertHasRevisions(changeInfo, 1);
		// no approvals given
		Account account = new Account(new Account.Id(1000001));
		PatchSet.Id patchSetId = createCurrentPatchSetId(2, 1);
		assertThat(changeInfo.convertToPatchSetApprovals(patchSetId, account), empty());
	}

	@Test
	public void testCodeReviewMinusOne() throws IOException {
		ChangeInfo changeInfo = parseFile("testdata/ChangeInfo_CodeReviewMinusOne.json");

		assertHasCodeReviewLabels(changeInfo);
		assertHasApprovalInfo(changeInfo.getLabels().get("Code-Review").getAll(), -1);
		assertHasApprovalDetail(changeInfo.convertToApprovalDetails(), -1);
		assertHasCodeReviewApprovalType(changeInfo.convertToApprovalTypes());
		assertHasCodeReviewPermissionLabels(changeInfo);
		assertHasRevisions(changeInfo, 1);

		assertGiven(changeInfo, createCurrentPatchSetId(2, 1), 1000001, -1);
	}

	@Test
	public void testTwoRevisions() throws IOException {
		ChangeInfo changeInfo = parseFile("testdata/ChangeInfo_TwoRevisions.json");

		assertHasCodeReviewLabels(changeInfo);
		assertHasApprovalInfo(changeInfo.getLabels().get("Code-Review").getAll(), 0);
		assertHasApprovalDetail(changeInfo.convertToApprovalDetails(), 0);
		assertHasCodeReviewApprovalType(changeInfo.convertToApprovalTypes());
		// no permission labels
		assertHasRevisions(changeInfo, 2);

		assertGiven(changeInfo, createCurrentPatchSetId(1, 2), 1000001, 0);
	}

	@Test
	public void testThreeApprovalTypes() throws IOException {
		ChangeInfo changeInfo = parseFile("testdata/ChangeInfo_ThreeApprovalTypes.json");

		// assert approval types ordering is not changed
		Map<String, LabelInfo> labels = changeInfo.getLabels();
		assertNotNull(labels);
		assertEquals(3, labels.size());
		Set<ApprovalType> approvalTypes = changeInfo.convertToApprovalTypes();
		assertNotNull(approvalTypes);
		assertEquals(3, approvalTypes.size());
		Iterator<ApprovalType> it = approvalTypes.iterator();
		assertCategoriesEqual(ApprovalUtil.VRIF, it.next());
		assertCategoriesEqual(ApprovalUtil.CRVW, it.next());
		assertCategoriesEqual(ApprovalUtil.IPCL, it.next());

		PatchSet.Id patchSetId = createCurrentPatchSetId(12850, -1 /*unspecified*/);
		assertGiven(changeInfo, patchSetId, 4, -1, 0, -1);
		assertGiven(changeInfo, patchSetId, 118, 0, 0, 0);
		assertGiven(changeInfo, patchSetId, 442, 0, 1, 0);
	}

	@Test
	public void testCustomApprovalType() throws IOException {
		ChangeInfo changeInfo = parseFile("testdata/ChangeInfo_CustomApprovalType.json");

		Map<String, LabelInfo> labels = changeInfo.getLabels();
		assertNotNull(labels);
		assertEquals(2, labels.size());
		Set<ApprovalType> approvalTypes = changeInfo.convertToApprovalTypes();
		assertNotNull(approvalTypes);
		assertEquals(2, approvalTypes.size());
		Iterator<ApprovalType> it = approvalTypes.iterator();
		assertCategoriesEqual(ApprovalUtil.CRVW, it.next());
		ApprovalType custom = it.next();
		assertEquals("Non-Author-Code-Review", custom.getCategory().getName());
		// that's all we know about the approval, the rest is void
		assertNull(custom.getCategory().getAbbreviatedName());
		assertTrue(custom.getValues().isEmpty());
		// no approvals given
		Account account = new Account(new Account.Id(1000001));
		PatchSet.Id patchSetId = createCurrentPatchSetId(1, -1 /*unspecified*/);
		assertThat(changeInfo.convertToPatchSetApprovals(patchSetId, account), empty());
	}

	// Utility methods

	public static void assertHasCodeReviewLabels(ChangeInfo changeInfo) {
		assertHasCodeReviewLabels(changeInfo, false);
	}

	public static void assertHasCodeReviewLabels(ChangeInfo changeInfo, boolean version29) {
		assertThat(changeInfo, notNullValue());
		Map<String, LabelInfo> labels = changeInfo.getLabels();
		assertThat(labels, not(empty()));
		assertThat(labels.size(), is(1));
		assertThat(labels, Matchers.<String, LabelInfo> hasKey("Code-Review"));
		Map<String, String> values = labels.get("Code-Review").getValues();
		assertThat(values, not(empty()));
		assertThat(values.size(), is(5));
		assertThat(values, Matchers.<String, String> hasKey("-2"));
		assertThat(values, Matchers.<String, String> hasKey("-1"));
		assertThat(values, Matchers.<String, String> hasKey(" 0"));
		assertThat(values, Matchers.<String, String> hasKey("+1"));
		assertThat(values, Matchers.<String, String> hasKey("+2"));
		if (version29) {
			//Text for Gerrit 2.9 has changed for the "-2" values
			assertThat(values.get("-2"), equalTo("This shall not be merged"));
			assertThat(values.get("-1"), equalTo("I would prefer this is not merged as is"));
		} else {
			assertThat(values.get("-2"), equalTo(CRVW.getValue((short) -2).getName()));
			assertThat(values.get("-1"), equalTo(CRVW.getValue((short) -1).getName()));
		}
		assertThat(values.get(" 0"), equalTo(CRVW.getValue((short) 0).getName()));
		assertThat(values.get("+1"), equalTo(CRVW.getValue((short) 1).getName()));
		assertThat(values.get("+2"), equalTo(CRVW.getValue((short) 2).getName()));
	}

	private static Timestamp timestamp(String date) throws ParseException {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		cal.setTime(sdf.parse(date));
		cal.add(Calendar.MILLISECOND, TimeZone.getDefault().getRawOffset());
		return new Timestamp(cal.getTimeInMillis());
	}

	private ChangeInfo parseFile(String path) throws IOException {
		File file = CommonTestUtil.getFile(this, path);
		String content = CommonTestUtil.read(file);
		return new JSonSupport().parseResponse(content, ChangeInfo.class);
	}

	private static void assertHasCodeReviewApprovalType(Set<ApprovalType> approvalTypes) {
		assertThat(approvalTypes, not(empty()));
		assertThat(approvalTypes.size(), is(1));
		ApprovalType approvalType = approvalTypes.iterator().next();
		assertThat(approvalType.getCategory().getId(), equalTo(CRVW.getCategory().getId()));
		assertThat(approvalType.getCategory().getName(), is("Code Review"));
		assertThat(approvalType.getValues().size(), is(5));
		for (ApprovalCategoryValue approvalCategoryValue : CRVW.getValues()) {
			assertHasItem(approvalType.getValues(), ApprovalCategoryValueComparator.INSTANCE, approvalCategoryValue);
		}
	}

	private static void assertHasApprovalInfo(List<ApprovalInfo> all, int value) {
		assertThat(all, not(empty()));
		assertThat(all.size(), is(1));
		ApprovalInfo approvalInfo = all.get(0);
		assertThat(approvalInfo, notNullValue());
		assertThat(approvalInfo.getValue(), is((short) value));
		assertThat(approvalInfo.getEmail(), equalTo("tests@mylyn.eclipse.org"));
	}

	private static void assertHasApprovalDetail(Set<ApprovalDetail> approvalDetails, int value) {
		assertThat(approvalDetails, not(empty()));
		assertThat(approvalDetails.size(), is(1));
		ApprovalDetail approvalDetail = approvalDetails.iterator().next();
		assertThat(approvalDetail, notNullValue());
		Map<Id, PatchSetApproval> approvalMap = approvalDetail.getApprovalMap();
		assertThat(approvalMap, notNullValue());
		assertThat(approvalMap, Matchers.<Id, PatchSetApproval> hasKey(CRVW.getCategory().getId()));
		PatchSetApproval patchSetApproval = approvalMap.get(CRVW.getCategory().getId());
		assertThat(patchSetApproval.getValue(), is((short) value));
	}

	public static void assertHasCodeReviewPermissionLabels(ChangeInfo changeInfo) {
		Map<String, String[]> permittedLabels = changeInfo.getPermittedLabels();

		assertThat(permittedLabels, notNullValue());
		assertThat(permittedLabels, not(empty()));
		assertThat(permittedLabels.size(), is(1));
		String[] crvwPermittedLabels = permittedLabels.get(toNameWithDash(CRVW.getCategory().getName()));
		assertThat(crvwPermittedLabels, not(empty()));
		assertThat(crvwPermittedLabels.length, is(3));
		assertThat(crvwPermittedLabels[0], is(CRVW.getValue((short) -1).formatValue()));
		assertThat(crvwPermittedLabels[1], is(CRVW.getValue((short) 0).formatValue()));
		assertThat(crvwPermittedLabels[2], is(CRVW.getValue((short) 1).formatValue()));

		List<PermissionLabel> permissionLabels = changeInfo.convertToPermissionLabels();
		assertThat(permissionLabels, notNullValue());
		assertThat(permissionLabels, not(empty()));
		assertThat(permissionLabels.size(), is(1));
		PermissionLabel crvwAllowed = permissionLabels.get(0);
		assertThat(crvwAllowed.getName(),
				is(PermissionLabel.toLabelName(toNameWithDash(CRVW.getCategory().getName()))));
		assertThat(crvwAllowed.getMin(), is(-1));
		assertThat(crvwAllowed.getMax(), is(1));
	}

	private static void assertHasRevisions(ChangeInfo changeInfo, int patchSetNr) {
		assertThat(changeInfo, notNullValue());
		String currentRevision = changeInfo.getCurrentRevision();
		assertThat(currentRevision, notNullValue());
		Map<String, RevisionInfo> revisions = changeInfo.getRevisions();
		assertThat(revisions, not(empty()));
		assertThat(revisions.size(), is(1));
		RevisionInfo currentRevisionInfo = revisions.get(currentRevision);
		assertThat(currentRevisionInfo, notNullValue());
		assertThat(currentRevisionInfo.isDraft(), is(false));
		assertThat(currentRevisionInfo.getNumber(), is(patchSetNr));
	}

	private static <T> void assertHasItem(Collection<T> collection, Comparator<T> comparator, T itemToFind) {
		for (T item : collection) {
			if (comparator.compare(item, itemToFind) == 0) {
				return;
			}
		}
		fail("Item " + itemToFind + " not found in " + collection);
	}

	private static class ApprovalCategoryValueComparator implements Comparator<ApprovalCategoryValue> {
		private final static Comparator<ApprovalCategoryValue> INSTANCE = new ApprovalCategoryValueComparator();

		public int compare(ApprovalCategoryValue acv1, ApprovalCategoryValue acv2) {
			return acv1.format().compareTo(acv2.format());
		}
	}

	private static void assertCategoriesEqual(ApprovalType expected, ApprovalType actual) {
		assertEquals(expected.getCategory().getId().get(), actual.getCategory().getId().get());
	}

	private static void assertGiven(ChangeInfo changeInfo, PatchSet.Id patchSetId, int accountId, int... approvals) {
		Account account = new Account(new Account.Id(accountId));
		Map<Id, PatchSetApproval> given = changeInfo.convertToPatchSetApprovals(patchSetId, account);
		assertNotNull(given);
		assertEquals(approvals.length, given.size());
		if (approvals.length > 0) {
			assertEquals((short) approvals[0], given.get(ApprovalUtil.CRVW.getCategory().getId()).getValue());
		}
		if (approvals.length > 1) {
			assertEquals((short) approvals[1], given.get(ApprovalUtil.VRIF.getCategory().getId()).getValue());
		}
		if (approvals.length > 2) {
			assertEquals((short) approvals[2], given.get(ApprovalUtil.IPCL.getCategory().getId()).getValue());
		}
	}

	private static PatchSet.Id createCurrentPatchSetId(int changeNumber, int patchSetNumber) {
		Change.Id changeId = new Change.Id(changeNumber);
		return new PatchSet.Id(changeId, patchSetNumber);
	}
}
