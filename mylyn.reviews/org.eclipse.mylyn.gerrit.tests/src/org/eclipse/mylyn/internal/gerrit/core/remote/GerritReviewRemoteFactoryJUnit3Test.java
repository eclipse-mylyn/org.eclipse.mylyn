/*******************************************************************************
 * Copyright (c) 2012, 2016 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.remote;

import static org.eclipse.mylyn.gerrit.tests.core.client.rest.IsEmpty.empty;
import static org.eclipse.mylyn.internal.gerrit.core.client.rest.ApprovalUtil.CRVW;
import static org.eclipse.mylyn.internal.gerrit.core.client.rest.ApprovalUtil.toNameWithDash;
import static org.eclipse.mylyn.internal.gerrit.core.remote.TestRemoteObserverConsumer.retrieveForLocalKey;
import static org.eclipse.mylyn.internal.gerrit.core.remote.TestRemoteObserverConsumer.retrieveForRemoteKey;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.core.IsNot.not;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.PrivilegeLevel;
import org.eclipse.mylyn.gerrit.tests.core.client.rest.ChangeInfoTest;
import org.eclipse.mylyn.gerrit.tests.support.GerritProject;
import org.eclipse.mylyn.gerrit.tests.support.GerritProject.CommitResult;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritChange;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritException;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.ChangeDetailX;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.GerritSystemAccount;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.PatchSetPublishDetailX;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.PermissionLabel;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.BranchInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.ChangeInfo;
import org.eclipse.mylyn.reviews.core.model.IApprovalType;
import org.eclipse.mylyn.reviews.core.model.IChange;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.IRepository;
import org.eclipse.mylyn.reviews.core.model.IRequirementEntry;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.core.model.IReviewerEntry;
import org.eclipse.mylyn.reviews.core.model.IUser;
import org.eclipse.mylyn.reviews.core.model.RequirementStatus;
import org.eclipse.mylyn.reviews.core.model.ReviewStatus;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfConsumer;
import org.junit.Test;

import com.google.gerrit.common.data.ApprovalDetail;
import com.google.gerrit.common.data.ChangeDetail;
import com.google.gerrit.common.data.PatchSetDetail;
import com.google.gerrit.common.data.ReviewerResult;
import com.google.gerrit.reviewdb.ApprovalCategory;
import com.google.gerrit.reviewdb.ApprovalCategoryValue;
import com.google.gerrit.reviewdb.ApprovalCategoryValue.Id;
import com.google.gerrit.reviewdb.Change;
import com.google.gerrit.reviewdb.Change.Status;
import com.google.gerrit.reviewdb.ChangeMessage;
import com.google.gerrit.reviewdb.PatchSet;
import com.google.gerrit.reviewdb.PatchSetApproval;
import com.google.gerrit.reviewdb.Project.NameKey;

/**
 * @author Miles Parker
 */
public class GerritReviewRemoteFactoryJUnit3Test extends GerritRemoteTest {

	public void testGlobalComments() throws Exception {
		String message1 = "new comment, time: " + System.currentTimeMillis(); //$NON-NLS-1$
		reviewHarness.getClient()
		.publishComments(reviewHarness.getShortId(), 1, message1,
				Collections.<ApprovalCategoryValue.Id> emptySet(), null);
		String message2 = "new comment, time: " + System.currentTimeMillis(); //$NON-NLS-1$
		reviewHarness.getClient()
		.publishComments(reviewHarness.getShortId(), 1, message2,
				Collections.<ApprovalCategoryValue.Id> emptySet(), null);
		reviewHarness.retrieve();
		List<IComment> comments = getReview().getComments();
		assertThat(comments.size(), is(3));
		IComment uploadComment = comments.get(0);
		assertThat(uploadComment.getAuthor().getDisplayName(), is("tests"));
		assertThat(uploadComment.getDescription(), is("Uploaded patch set 1."));
		IComment comment1 = comments.get(1);
		assertThat(comment1.getAuthor().getDisplayName(), is("tests"));
		assertThat(comment1.getDescription(), is("Patch Set 1:\n\n" + message1));
		IComment comment2 = comments.get(2);
		assertThat(comment2.getAuthor().getDisplayName(), is("tests"));
		assertThat(comment2.getDescription(), is("Patch Set 1:\n\n" + message2));
	}

	@Test
	public void testReviewStatus() throws Exception {
		assertThat(GerritReviewRemoteFactory.getReviewStatus(Status.ABANDONED), is(ReviewStatus.ABANDONED));
		assertThat(GerritReviewRemoteFactory.getReviewStatus(Status.MERGED), is(ReviewStatus.MERGED));
		assertThat(GerritReviewRemoteFactory.getReviewStatus(Status.NEW), is(ReviewStatus.NEW));
		assertThat(GerritReviewRemoteFactory.getReviewStatus(Status.SUBMITTED), is(ReviewStatus.SUBMITTED));
		//Test for drafts hack
		assertThat(GerritReviewRemoteFactory.getReviewStatus(null), is(ReviewStatus.DRAFT));
	}

	@Test
	public void testNewChange() throws Exception {
		if (!canMakeMultipleCommits()) {
			return;
		}
		CommitCommand command2 = reviewHarness.createCommitCommand();
		reviewHarness.addFile("testFile2.txt");
		reviewHarness.commitAndPush(command2);
		reviewHarness.retrieve();
		List<IReviewItemSet> items = getReview().getSets();
		assertThat(items.size(), is(2));
		IReviewItemSet patchSet2 = items.get(1);
		assertThat(patchSet2.getReference(), endsWith("/2"));
		reviewHarness.assertIsRecent(patchSet2.getCreationDate());
	}

	@Test
	public void testAccount() throws Exception {
		assertThat(reviewHarness.getRepository().getAccount(), notNullValue());
		assertThat(reviewHarness.getRepository().getAccount().getDisplayName(), is("tests"));
		assertThat(reviewHarness.getRepository().getAccount().getEmail(), is("tests@mylyn.eclipse.org"));
		assertThat(reviewHarness.getRepository().getUsers().get(0), is(reviewHarness.getRepository().getAccount()));
	}

	@Test
	public void testApprovals() throws Exception {
		int approvals = 1;
		assertThat(reviewHarness.getRepository().getApprovalTypes().size(), is(approvals));
		IApprovalType codeReviewApproval = reviewHarness.getRepository().getApprovalTypes().get(approvals - 1);
		assertThat(codeReviewApproval.getKey(), is(CRVW.getCategory().getId().get()));
		assertThat(codeReviewApproval.getName(), is(CRVW.getCategory().getName()));

		String approvalMessage = "approval, time: " + System.currentTimeMillis(); //$NON-NLS-1$
		reviewHarness.getClient()
		.publishComments(reviewHarness.getShortId(), 1, approvalMessage,
				new HashSet<>(Collections.singleton(CRVW.getValue((short) 1).getId())),
				new NullProgressMonitor());
		reviewHarness.retrieve();
		assertThat(getReview().getReviewerApprovals().size(), is(1));
		Entry<IUser, IReviewerEntry> reviewerEntry = getReview().getReviewerApprovals().entrySet().iterator().next();
		Map<IApprovalType, Integer> reviewerApprovals = reviewerEntry.getValue().getApprovals();
		assertThat(reviewerApprovals.size(), is(1));
		Entry<IApprovalType, Integer> next = reviewerApprovals.entrySet().iterator().next();
		assertThat(next.getKey(), sameInstance(codeReviewApproval));
		assertThat(next.getValue(), is(1));

		Set<Entry<IApprovalType, IRequirementEntry>> reviewApprovals = getReview().getRequirements().entrySet();
		assertThat(reviewApprovals.size(), is(approvals));
		IRequirementEntry codeReviewEntry = getReview().getRequirements().get(codeReviewApproval);
		assertThat(codeReviewEntry, notNullValue());
		assertThat(codeReviewEntry.getBy(), nullValue());
		assertThat(codeReviewEntry.getStatus(), is(RequirementStatus.NOT_SATISFIED));
		assertThat(getReview().getState(), is(ReviewStatus.NEW));
	}

	@Test
	public void testDependencies() throws Exception {
		String changeIdDep1 = "I" + StringUtils.rightPad(System.currentTimeMillis() + "", 40, "a");
		CommitCommand commandDep1 = reviewHarness.createCommitCommand(changeIdDep1);
		reviewHarness.addFile("testFile1.txt", "test 2");
		CommitResult resultDep1 = reviewHarness.commitAndPush(commandDep1);
		String resultIdDep1 = ReviewHarness.parseShortId(resultDep1.push.getMessages());
		assertThat("Bad Push: " + resultDep1.push.getMessages(), resultIdDep1.length(), greaterThan(0));

		TestRemoteObserverConsumer<IRepository, IReview, String, GerritChange, String, Date> consumerDep1 = retrieveForRemoteKey(
				reviewHarness.getProvider().getReviewFactory(), reviewHarness.getRepository(), resultIdDep1, true);
		IReview reviewDep1 = consumerDep1.getModelObject();

		assertThat(reviewDep1.getParents().size(), is(1));
		IChange parentChange = reviewDep1.getParents().get(0);
		//Not expected to be same instance
		assertThat(parentChange.getId(), is(getReview().getId()));
		assertThat(parentChange.getSubject(), is(getReview().getSubject()));
		//There s an offset ~ 1 sec, so no test for now
//		assertThat(parentChange.getModificationDate().getTime(), is(getReview().getModificationDate().getTime()));

		reviewHarness.retrieve();
		assertThat(getReview().getChildren().size(), is(1));
		IChange childChange = getReview().getChildren().get(0);
		//Not expected to be same instance
		assertThat(childChange.getId(), is(reviewDep1.getId()));
		assertThat(childChange.getSubject(), is(reviewDep1.getSubject()));
		//There s an offset ~ 1 sec, so no test for now
//		assertThat(childChange.getModificationDate().getTime(), is(reviewDep1.getModificationDate().getTime()));
	}

	@Test
	public void testAbandonChange() throws Exception {
		String message1 = "abandon, time: " + System.currentTimeMillis(); //$NON-NLS-1$

		ChangeDetail changeDetail = reviewHarness.getClient()
				.abandon(reviewHarness.getShortId(), 1, message1, new NullProgressMonitor());
		reviewHarness.retrieve();
		assertThat(changeDetail, notNullValue());
		assertThat(changeDetail.getChange().getStatus(), is(Status.ABANDONED));
		List<ChangeMessage> messages = changeDetail.getMessages();
		assertThat(messages.size(), is(2));
		ChangeMessage lastMessage = messages.get(1);
		assertThat(lastMessage.getAuthor().get(), is(1000001));
		assertThat(lastMessage.getMessage(), endsWith("Abandoned\n\n" + message1));

		assertThat(getReview().getState(), is(ReviewStatus.ABANDONED));
		List<IComment> comments = getReview().getComments();
		assertThat(comments.size(), is(2));
		IComment lastComment = comments.get(1);
		assertThat(lastComment.getAuthor().getDisplayName(), is("tests"));
		assertThat(lastComment.getAuthor().getId(), is("1000001"));
		assertThat(lastComment.getDescription(), endsWith("Abandoned\n\n" + message1));
	}

	@Test
	public void testRestoreChange() throws Exception {
		String message1 = "abandon, time: " + System.currentTimeMillis();
		reviewHarness.getClient().abandon(reviewHarness.getShortId(), 1, message1, new NullProgressMonitor());
		reviewHarness.retrieve();
		String message2 = "restore, time: " + System.currentTimeMillis();

		reviewHarness.getClient().restore(reviewHarness.getShortId(), 1, message2, new NullProgressMonitor());
		reviewHarness.retrieve();
		assertThat(getReview().getState(), is(ReviewStatus.NEW));
		List<IComment> comments = getReview().getComments();
		assertThat(comments.size(), is(3)); // abandon + restore
		IComment lastComment = comments.get(2);
		assertThat(lastComment.getAuthor().getDisplayName(), is("tests"));
		assertThat(lastComment.getDescription(), endsWith("Restored\n\n" + message2));
	}

	@Test
	public void testRestoreNewChange() throws Exception {
		assertThat(getReview().getState(), is(ReviewStatus.NEW));
		String message1 = "restore, time: " + System.currentTimeMillis();
		try {
			reviewHarness.getClient().restore(reviewHarness.getShortId(), 1, message1, new NullProgressMonitor());
			fail("Expected to fail when restoring a new change");
		} catch (GerritException e) {
			assertThat(e.getMessage(), is("Not Found"));
		}
	}

	public void testCannotSubmitChange() throws Exception {
		try {
			reviewHarness.getClient().submit(reviewHarness.getShortId(), 1, new NullProgressMonitor());
			fail("Expected to fail when submitting a change without approvals");
		} catch (GerritException e) {
			assertThat(e.getMessage(), startsWith("Cannot submit change"));
		}
	}

	@Test
	public void testAddNullReviewers() throws Exception {
		try {
			reviewHarness.getClient().addReviewers(reviewHarness.getShortId(), null, new NullProgressMonitor());
			fail("Expected to fail when trying to add null reviewers");
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), is("reviewers cannot be null"));
		}
	}

	@Test
	public void testAddEmptyReviewers() throws Exception {
		ReviewerResult reviewerResult = reviewHarness.getClient()
				.addReviewers(reviewHarness.getShortId(), Collections.<String> emptyList(), new NullProgressMonitor());
		reviewHarness.retrieve();
		assertThat(reviewerResult, notNullValue());
		assertThat(reviewerResult.getErrors().isEmpty(), is(true));
		assertThat(reviewerResult.getChange().getApprovals().isEmpty(), is(true));
		assertThat(getReview().getReviewerApprovals().isEmpty(), is(true));
	}

	@Test
	public void testAddInvalidReviewers() throws Exception {
		List<String> reviewers = Arrays.asList("foo");

		ReviewerResult reviewerResult = reviewHarness.getClient()
				.addReviewers(reviewHarness.getShortId(), reviewers, new NullProgressMonitor());
		reviewHarness.retrieve();
		assertThat(reviewerResult, notNullValue());
		assertThat(reviewerResult.getErrors().size(), is(1));
		assertThat(reviewerResult.getErrors().get(0).getName(), is("foo"));
		assertThat(reviewerResult.getErrors().get(0).getType(), nullValue());
		assertThat(reviewerResult.getChange().getApprovals().isEmpty(), is(true));
		assertThat(getReview().getReviewerApprovals().isEmpty(), is(true));
	}

	@Test
	public void testAddSomeInvalidReviewers() throws Exception {

		//use "admin " since this is a valid user in 2.9
		List<String> reviewers = Arrays.asList("admin", "foo");
		List<Integer> userid = Arrays.asList(1000000); //user id for tests

		ReviewerResult reviewerResult = reviewHarness.getClient()
				.addReviewers(reviewHarness.getShortId(), reviewers, new NullProgressMonitor());
		reviewHarness.retrieve();
		assertReviewerResult(reviewerResult, "foo", userid);
	}

	@Test
	public void testAddReviewers() throws Exception {
		assertThat(getReview().getReviewerApprovals().isEmpty(), is(true));

		addAdminToReviewAndVerify();
	}

	@Test
	public void testAddReviewerThenRemoveReviewer() throws Exception {
		assertThat(getReview().getReviewerApprovals().isEmpty(), is(true)); //Make sure theres no reviewers

		addAdminToReviewAndVerify();

		String reviewer = "admin";
		ReviewerResult removeReviewerResult = reviewHarness.getClient()
				.removeReviewer(reviewHarness.getShortId(), reviewer, new NullProgressMonitor());
		reviewHarness.retrieve();

		//I went through the checks after we add a reviewer and reversed them to look for empty rather than for a new reviewer
		assertThat(getReview().getReviewerApprovals().isEmpty(), is(true));
		assertThat(removeReviewerResult, notNullValue());
		assertTrue(removeReviewerResult.getErrors().isEmpty());

		List<ApprovalDetail> approvals = removeReviewerResult.getChange().getApprovals();
		assertThat(approvals.isEmpty(), is(true));
		assertThat(approvals.size(), is(0));

	}

	@Test
	public void testAddReviewerAndRemoveInvalidReviewer() throws Exception {
		addAdminToReviewAndVerify();

		//Foo isnt a valid user
		String reviewer = "foo";

		ReviewerResult removeReviewerResult = reviewHarness.getClient()
				.removeReviewer(reviewHarness.getShortId(), reviewer, new NullProgressMonitor());

		reviewHarness.retrieve();

		assertThat(removeReviewerResult, notNullValue());
		assertThat(removeReviewerResult.getErrors().size(), is(1));
		assertThat(removeReviewerResult.getErrors().get(0).getType(), nullValue());
		assertThat(removeReviewerResult.getErrors().get(0).getName(), is(reviewer));

		List<ApprovalDetail> approvals = removeReviewerResult.getChange().getApprovals();
		assertThat(approvals.isEmpty(), is(false));
		assertThat(approvals.size(), is(1));

	}

	@Test
	public void testAddReviewerAndRemoveValidReviewerNotOnReview() throws Exception {
		addAdminToReviewAndVerify();

		String reviewer = "tests";

		ReviewerResult removeReviewerResult = reviewHarness.getClient()
				.removeReviewer(reviewHarness.getShortId(), reviewer, new NullProgressMonitor());

		reviewHarness.retrieve();

		assertThat(removeReviewerResult, notNullValue());
		assertThat(removeReviewerResult.getErrors().size(), is(1));
		assertThat(removeReviewerResult.getErrors().get(0).getType(), nullValue());
		assertThat(removeReviewerResult.getErrors().get(0).getName(), is(reviewer));

		List<ApprovalDetail> approvals = removeReviewerResult.getChange().getApprovals();
		assertThat(approvals.isEmpty(), is(false));
		assertThat(approvals.size(), is(1));

	}

	private void addAdminToReviewAndVerify() throws Exception {
		List<String> reviewers = Arrays.asList("admin");
		List<Integer> userid = Arrays.asList(1000000);
		ReviewerResult addReviewerResult = reviewHarness.getClient()
				.addReviewers(reviewHarness.getShortId(), reviewers, new NullProgressMonitor());
		reviewHarness.retrieve();
		assertReviewerResult(addReviewerResult, null, userid);
	}

	@Test
	public void testAddReviewersByEmail() throws Exception {
		List<String> reviewers = Arrays.asList("admin@mylyn.eclipse.org");
		List<Integer> userid = Arrays.asList(1000000); //user id for tests

		ReviewerResult reviewerResult = reviewHarness.getClient()
				.addReviewers(reviewHarness.getShortId(), reviewers, new NullProgressMonitor());
		reviewHarness.retrieve();
		assertReviewerResult(reviewerResult, null, userid);
	}

	private void assertReviewerResult(ReviewerResult reviewerResult, String nameInErrors, List<Integer> userIds) {
		assertThat(reviewerResult, notNullValue());

		int numReviewers = userIds.size();

		assertThat(reviewerResult.getErrors().isEmpty(), is(nameInErrors == null));
		if (nameInErrors != null) {
			assertThat(reviewerResult.getErrors().size(), is(1));
			assertThat(reviewerResult.getErrors().get(0).getName(), is(nameInErrors));
			assertThat(reviewerResult.getErrors().get(0).getType(), nullValue());
		}

		List<ApprovalDetail> approvals = reviewerResult.getChange().getApprovals();
		assertThat(approvals.isEmpty(), is(false));
		assertThat(approvals.size(), is(numReviewers));

		for (int i = 0; i < numReviewers; i++) {
			assertThat(approvals.get(i).getAccount().get(), is(userIds.get(i)));

			Map<ApprovalCategory.Id, PatchSetApproval> approvalMap = approvals.get(i).getApprovalMap();
			assertThat(approvalMap, notNullValue());
			assertThat(approvalMap.isEmpty(), is(false));
			assertThat(approvalMap.size(), is(1));

			PatchSetApproval crvw = approvalMap.get(CRVW.getCategory().getId());
			assertThat(crvw, notNullValue());
			assertThat(crvw.getAccountId().get(), is(userIds.get(i)));
			assertThat(crvw.getValue(), is((short) 0));
			assertThat(crvw.getGranted(), notNullValue());
			assertThat(crvw.getPatchSetId(), notNullValue());
			assertThat(crvw.getPatchSetId().get(), is(1));
			assertThat(crvw.getPatchSetId().getParentKey().get(), is(Integer.parseInt(getReview().getId())));

			assertThat(getReview().getReviewerApprovals().get(i), nullValue());
		}

		assertThat(getReview().getReviewerApprovals().isEmpty(), is(false));
		assertThat(getReview().getReviewerApprovals().size(), is(numReviewers));

	}

	@Test
	public void testCannotRebaseChangeAlreadyUpToDate() throws Exception {
		try {
			reviewHarness.getClient().rebase(reviewHarness.getShortId(), 1, new NullProgressMonitor());
			fail("Expected to fail when rebasing a change that is already up to date");
		} catch (GerritException e) {
			String message = e.getMessage().replaceAll("\\p{Cntrl}", "");
			assertThat(message, is("Change is already up to date."));
		}
	}

	@Test
	public void testGetChangeDetailWithNoApprovals() throws Exception {
		int reviewId = Integer.parseInt(reviewHarness.getShortId());

		ChangeDetailX changeDetail = reviewHarness.getClient().getChangeDetail(reviewId, new NullProgressMonitor());

		assertThat(changeDetail, notNullValue());
		assertThat(changeDetail.getApprovals(), empty());
	}

	@Test
	public void testGetChangeInfo() throws Exception {
		int reviewId = Integer.parseInt(reviewHarness.getShortId());

		ChangeInfo changeInfo = reviewHarness.getClient().getChangeInfo(reviewId, new NullProgressMonitor());
		ChangeInfoTest.assertHasCodeReviewLabels(changeInfo, true);
	}

	@Test
	public void testUnpermittedApproval() throws Exception {
		String approvalMessage = "approval, time: " + System.currentTimeMillis();
		try {
			reviewHarness.getClient()
			.publishComments(reviewHarness.getShortId(), 1, approvalMessage,
					new HashSet<>(
							Collections.singleton(CRVW.getValue((short) 2).getId())),
					new NullProgressMonitor());
			fail("Expected to fail when trying to vote +2 when it's not permitted");
		} catch (GerritException e) {
			assertEquals("Applying label \"Code-Review\": 2 is restricted", e.getMessage());
		}
	}

	@Test
	public void testGetPatchSetPublishDetail() throws Exception {
		int reviewId = Integer.parseInt(reviewHarness.getShortId());
		PatchSet.Id id = new PatchSet.Id(new Change.Id(reviewId), 1);

		PatchSetPublishDetailX patchSetDetail = reviewHarness.getClient()
				.getPatchSetPublishDetail(id, new NullProgressMonitor());

		assertThat(patchSetDetail, notNullValue());
		List<PermissionLabel> allowed = patchSetDetail.getLabels();
		assertThat(allowed, notNullValue());
		assertThat(allowed, not(empty()));
		assertThat(allowed.size(), is(1));
		PermissionLabel crvwAllowed = allowed.get(0);
		assertThat(crvwAllowed.matches(CRVW.getCategory()), is(true));
		assertThat(crvwAllowed.getName(),
				is(PermissionLabel.toLabelName(toNameWithDash(CRVW.getCategory().getName()))));
		assertThat(crvwAllowed.getMin(), is(-1));
		assertThat(crvwAllowed.getMax(), is(1));
	}

	@Test
	public void testSetStarred() throws Exception {

		int reviewId = Integer.parseInt(reviewHarness.getShortId());
		//Set the Starred to a review
		reviewHarness.getClient().setStarred(reviewHarness.getShortId(), true, new NullProgressMonitor());

		ChangeDetailX changeDetail = reviewHarness.getClient().getChangeDetail(reviewId, new NullProgressMonitor());
		assertEquals(true, changeDetail.isStarred());

		//Test if already the Starred was set, should react the same way
		reviewHarness.getClient().setStarred(reviewHarness.getShortId(), true, new NullProgressMonitor());
		changeDetail = reviewHarness.getClient().getChangeDetail(reviewId, new NullProgressMonitor());
		assertEquals(true, changeDetail.isStarred());

		reviewHarness.getClient().setStarred(reviewHarness.getShortId(), false, new NullProgressMonitor());
		changeDetail = reviewHarness.getClient().getChangeDetail(reviewId, new NullProgressMonitor());
		assertEquals(false, changeDetail.isStarred());

	}

	@Test
	public void testReviewsWithSameChangeId() throws Exception {
		String branchName = "test_side_branch";
		createBranchIfNonExistent(branchName);
		ReviewHarness reviewHarness2 = reviewHarness.duplicate(); //same ChangeId
		reviewHarness2.init("HEAD:refs/for/" + branchName, PrivilegeLevel.USER, "otherTestFile.txt", true);

		reviewHarness.retrieve();
		reviewHarness2.retrieve();

		assertEquals(reviewHarness.getReview().getKey(), reviewHarness2.getReview().getKey()); // same changeId
		assertThat(reviewHarness.getReview().getId(), is(not(reviewHarness2.getReview().getId()))); // different reviewId

		assertThat(reviewHarness.getReview().getSets().size(), is(1));
		assertThat(reviewHarness.getReview().getSets().get(0).getId(), is("1"));
		PatchSetDetail detail = retrievePatchSetDetail(reviewHarness, "1");
		assertThat(detail.getPatches().size(), is(2));
		assertThat(detail.getPatches().get(0).getFileName(), is("/COMMIT_MSG"));
		assertThat(detail.getPatches().get(1).getFileName(), is("testFile1.txt"));

		assertThat(reviewHarness2.getReview().getSets().size(), is(1));
		assertThat(reviewHarness2.getReview().getSets().get(0).getId(), is("1"));
		PatchSetDetail detail2 = retrievePatchSetDetail(reviewHarness2, "1");
		assertThat(detail2.getPatches().size(), is(2));
		assertThat(detail2.getPatches().get(0).getFileName(), is("/COMMIT_MSG"));
		assertThat(detail2.getPatches().get(1).getFileName(), is("otherTestFile.txt"));
	}

	@Test
	public void testCherryPick() throws Exception {
		String testMessage = "Test Cherry Pick";
		String branchName = "test_side_branch";
		createBranchIfNonExistent(branchName);
		String refSpec = "refs/heads/" + branchName;

		ChangeDetail changeDetail = reviewHarness.getClient()
				.cherryPick(reviewHarness.getShortId(), 1, testMessage, refSpec, new NullProgressMonitor());
		Change change = changeDetail.getChange();

		IReview review = reviewHarness.getReview();

		assertThat(change.getChangeId(), is(not(Integer.parseInt(review.getId())))); //changeId deprecated, yet still used.
		assertThat(change.getKey().get(), is(not(review.getKey())));
		assertThat(change.getSubject(), is(testMessage));
		assertThat(change.getDest().get(), is(branchName));
	}

	@Test
	public void testCannotCherryPick() throws Exception {
		String testMessage = "Test Cherry Pick";
		String testDest = "refs/heads/test_side_branch";

		badRequestCherryPick(null, testDest, "message must be non-empty");
		badRequestCherryPick("", testDest, "message must be non-empty");

		badRequestCherryPick(testMessage, null, "destination must be non-empty");
		badRequestCherryPick(testMessage, "", "destination must be non-empty");

		badRequestCherryPick(testMessage, "no_such_branch", "Branch no_such_branch does not exist.");
	}

	private void badRequestCherryPick(String message, String dest, String errMsg) {
		failedCherryPick(message, dest, "Bad Request: " + errMsg);
	}

	@Test
	public void unsupportedVersionCherryPick(String message, String dest, String errMsg) throws GerritException {
		failedCherryPick("Test Cherry Pick", "refs/heads/test_side_branch",
				"Cherry Picking not supported before version 2.8");
	}

	private void failedCherryPick(String message, String dest, String errMsg) {
		try {
			reviewHarness.getClient()
			.cherryPick(reviewHarness.getShortId(), 1, message, dest, new NullProgressMonitor());
			fail("Expected to get an exception when cherry picking");
		} catch (GerritException e) {
			String receivedMessage = e.getMessage().replaceAll("\\p{Cntrl}", "");
			assertThat(receivedMessage, is(errMsg));
		}
	}

	@Test
	public void testGlobalCommentByGerrit() throws Exception {
		//create a new commit and Review that depends on Patch Set 1 of the existing Review
		String changeIdNewChange = ReviewHarness.generateChangeId();
		CommitCommand commandNewChange = reviewHarness.createCommitCommand(changeIdNewChange);
		reviewHarness.addFile("testFileNewChange.txt");
		CommitResult result = reviewHarness.commitAndPush(commandNewChange);
		String newReviewShortId = ReviewHarness.parseShortId(result.push.getMessages());

		TestRemoteObserver<IRepository, IReview, String, Date> newReviewListener = new TestRemoteObserver<>(
				reviewHarness.getProvider().getReviewFactory());

		RemoteEmfConsumer<IRepository, IReview, String, GerritChange, String, Date> newReviewConsumer = reviewHarness
				.getProvider()
				.getReviewFactory()
				.getConsumerForRemoteKey(reviewHarness.getRepository(), newReviewShortId);
		newReviewConsumer.addObserver(newReviewListener);
		newReviewConsumer.retrieve(false);
		newReviewListener.waitForResponse();

		reviewHarness.retrieve();
		IReview newReview = reviewHarness.getProvider().open(newReviewShortId);
		assertThat(newReview.getId(), is(newReviewShortId));

		assertThat(getReview().getChildren().size(), is(1));
		assertThat(getReview().getSets().size(), is(1));

		reviewHarness.checkoutPatchSet(1);

		//create Patch Set 2 for Review 1
		CommitCommand command2 = reviewHarness.createCommitCommand();
		reviewHarness.addFile("testFile3.txt");
		reviewHarness.commitAndPush(command2);
		reviewHarness.retrieve();
		List<IReviewItemSet> items = getReview().getSets();
		assertThat(items.size(), is(2));
		IReviewItemSet patchSet2 = items.get(1);
		assertThat(patchSet2.getReference(), endsWith("/2"));
		reviewHarness.assertIsRecent(patchSet2.getCreationDate());

		//now approve, publish and submit Review 2 - this should create a comment authored by Gerrit
		String approvalMessage = "approval, time: " + System.currentTimeMillis();
		HashSet<Id> approvals = new HashSet<>(
				Collections.singleton(CRVW.getValue((short) 2).getId()));
		reviewHarness.getAdminClient()
		.publishComments(newReviewShortId, 1, approvalMessage, approvals, new NullProgressMonitor());
		reviewHarness.getAdminClient().submit(newReviewShortId, 1, new NullProgressMonitor());

		newReviewConsumer.retrieve(false);
		newReviewListener.waitForResponse();

		assertThat(newReview.getState(), is(ReviewStatus.SUBMITTED));

		List<IComment> comments = newReview.getComments();

		assertThat(comments.size(), is(3));

		IComment commentByGerrit = comments.get(2);

		assertNotNull(commentByGerrit.getAuthor());
		assertThat(commentByGerrit.getAuthor().getId(), is(String.valueOf(GerritSystemAccount.GERRIT_SYSTEM.getId())));
		assertThat(commentByGerrit.getAuthor().getDisplayName(), is(GerritSystemAccount.GERRIT_SYSTEM_NAME));

		assertThat(commentByGerrit.getDescription().substring(0, 58),
				is("Change cannot be merged due to unsatisfiable dependencies."));

		// After running this test, we should have at least the following users
		assertThat(findUser("tests").getEmail(), is("tests@mylyn.eclipse.org"));
		assertThat(findUser("admin").getEmail(), is("admin@mylyn.eclipse.org"));
		IUser systemUser = findUser("Gerrit Code Review");
		assertThat(systemUser.getEmail(), nullValue());
		assertThat(systemUser.getId(), is("-2"));
	}

	private IUser findUser(final String displayName) {
		return reviewHarness.getRepository()
				.getUsers()
				.stream()
				.filter(user -> user.getDisplayName().equals(displayName))
				.findAny()
				.orElseThrow();
	}

	@Test
	public void testParentCommit() throws Exception {
		String changeIdNewChange = ReviewHarness.generateChangeId();
		CommitCommand commandNewChange = reviewHarness.createCommitCommand(changeIdNewChange);
		reviewHarness.addFile("testFileNewChange.txt");
		CommitResult result = reviewHarness.commitAndPush(commandNewChange);
		String newReviewShortId = ReviewHarness.parseShortId(result.push.getMessages());

		TestRemoteObserver<IRepository, IReview, String, Date> newReviewListener = new TestRemoteObserver<>(
				reviewHarness.getProvider().getReviewFactory());

		RemoteEmfConsumer<IRepository, IReview, String, GerritChange, String, Date> newReviewConsumer = reviewHarness
				.getProvider()
				.getReviewFactory()
				.getConsumerForRemoteKey(reviewHarness.getRepository(), newReviewShortId);
		newReviewConsumer.addObserver(newReviewListener);
		newReviewConsumer.retrieve(false);
		newReviewListener.waitForResponse();

		reviewHarness.retrieve();
		IReview parentReview = getReview();
		IReview childReview = reviewHarness.getProvider().open(newReviewShortId);
		assertThat(childReview.getId(), is(newReviewShortId));

		assertThat(parentReview.getChildren().size(), is(1));
		assertThat(parentReview.getSets().size(), is(1));
		assertThat(childReview.getSets().size(), is(1));

		IReviewItemSet childPatchSet = childReview.getSets().get(0);
		IReviewItemSet parentPatchSet = parentReview.getSets().get(0);

		assertThat(childPatchSet.getParentCommits().size(), is(1));
		String parentCommitId = childPatchSet.getParentCommits().get(0).getId();
		assertThat(parentCommitId, is(parentPatchSet.getRevision()));
	}

	@Test
	public void testGetCachedBranches() throws GerritException {
		GerritClient client = reviewHarness.getClient();
		NullProgressMonitor monitor = new NullProgressMonitor();
		NameKey project = client.getChange(reviewHarness.getShortId(), monitor)
				.getChangeDetail()
				.getChange()
				.getProject();
		assertNull(client.getCachedBranches(project));

		client.refreshConfigOnce(project, monitor);

		Set<String> branches = client.getCachedBranches(project);
		assertNotNull(branches);
		assertTrue(branches.contains("refs/heads/master"));

		String newBranch = "branch-" + System.currentTimeMillis();
		String newBranchRef = "refs/heads/" + newBranch;
		try {
			createBranchIfNonExistent(newBranch);

			assertFalse(client.getCachedBranches(project).contains(newBranchRef));

			client.refreshConfigOnce(project, monitor);
			assertFalse(client.getCachedBranches(project).contains(newBranchRef));

			client.refreshConfig(monitor);
			assertTrue(client.getCachedBranches(project).contains(newBranchRef));
		} finally {
			deleteBranch(newBranch);
		}
	}

	private void createBranchIfNonExistent(String branchName) throws GerritException {
		if (!branchExists(branchName)) {
			reviewHarness.getAdminClient()
			.createRemoteBranch(GerritProject.PROJECT, branchName, null, new NullProgressMonitor());
		}
	}

	private void deleteBranch(String branchName) throws GerritException {
		reviewHarness.getAdminClient()
		.deleteRemoteBranch(GerritProject.PROJECT, branchName, null, new NullProgressMonitor());
	}

	private boolean branchExists(String branchName) throws GerritException {
		BranchInfo[] branches = reviewHarness.getAdminClient()
				.getRemoteProjectBranches(GerritProject.PROJECT, new NullProgressMonitor());
		for (BranchInfo branch : branches) {
			String branchRef = StringUtils.trimToEmpty(StringUtils.substringAfterLast(branch.getRef(), "/"));
			if (branchRef.equals(branchName)) {
				return true;
			}
		}
		return false;
	}

	private PatchSetDetail retrievePatchSetDetail(ReviewHarness reviewHarness, String patchSetId) {
		TestRemoteObserverConsumer<IReview, IReviewItemSet, String, PatchSetDetail, PatchSetDetail, String> itemSetObserver = retrieveForLocalKey(
				reviewHarness.getProvider().getReviewItemSetFactory(), reviewHarness.getReview(), patchSetId, false);
		PatchSetDetail detail = itemSetObserver.getRemoteObject();
		return detail;
	}

	@Test
	public void testNoLabels() throws Exception {
		//create a commit w/ -2, resulting in no labels
		HashSet<Id> approvals = new HashSet<>(
				Collections.singleton(CRVW.getValue((short) -2).getId()));
		reviewHarness.getAdminClient()
		.publishComments(reviewHarness.getShortId(), 1, "", approvals, new NullProgressMonitor());
		reviewHarness.retrieve();
	}
}
