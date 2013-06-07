/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.remote;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.mylyn.gerrit.tests.support.GerritFixture;
import org.eclipse.mylyn.gerrit.tests.support.GerritHarness;
import org.eclipse.mylyn.gerrit.tests.support.GerritProject.CommitResult;
import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritChange;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.internal.gerrit.core.client.PatchSetContent;
import org.eclipse.mylyn.reviews.core.model.IApprovalType;
import org.eclipse.mylyn.reviews.core.model.IChange;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IFileVersion;
import org.eclipse.mylyn.reviews.core.model.IRepository;
import org.eclipse.mylyn.reviews.core.model.IRequirementEntry;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.core.model.IReviewerEntry;
import org.eclipse.mylyn.reviews.core.model.IReviewsFactory;
import org.eclipse.mylyn.reviews.core.model.IUser;
import org.eclipse.mylyn.reviews.core.model.RequirementStatus;
import org.eclipse.mylyn.reviews.core.model.ReviewStatus;
import org.eclipse.mylyn.reviews.core.spi.remote.JobRemoteService;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfConsumer;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gerrit.common.data.PatchSetDetail;
import com.google.gerrit.reviewdb.ApprovalCategory;
import com.google.gerrit.reviewdb.ApprovalCategoryValue;
import com.google.gerrit.reviewdb.Patch;

/**
 * @author Miles Parker
 */
public class GerritRemoteFactoryTest extends TestCase {

	//The maximum difference between two dates to account for clock skew between test machines
	static final long CREATION_TIME_DELTA = 30 * 60 * 1000; //30 Minutes

	private GerritClient client;

	private GerritRemoteFactoryProvider factoryProvider;

	private GerritHarness harness;

	private GerritConnector connector;

	private TaskRepository repository;

	private final IRepository group = IReviewsFactory.INSTANCE.createRepository();

	private ReviewHarness reviewHarness;

	class ReviewHarness {

		TestRemoteObserver<IRepository, IReview> listener;

		RemoteEmfConsumer<IRepository, IReview, GerritChange, String, String> consumer;

		String shortId;

		String commitId;

		String changeId;

		String testIdent;

		private final Git git;

		ReviewHarness(String testIdent) throws Exception {
			this.testIdent = testIdent;
			git = harness.project().getGitProject();
			changeId = "I" + StringUtils.rightPad(testIdent, 40, "a");
			CommitCommand command = git.commit()
					.setAll(true)
					.setMessage("Test Change " + testIdent + "\n\nChange-Id: " + changeId);
			harness.project().addFile("testFile1.txt");
			CommitResult result = harness.project().commitAndPush(command);
			shortId = StringUtils.trimToEmpty(StringUtils.substringAfterLast(result.push.getMessages(), "/"));
			commitId = result.commit.getId().toString();
			assertThat("Bad Push: " + result.push.getMessages(), shortId.length(), greaterThan(0));
			listener = new TestRemoteObserver<IRepository, IReview>(factoryProvider.getReviewFactory());
			consumer = factoryProvider.getReviewFactory().getConsumerForRemoteKey(group, shortId);
			consumer.addObserver(listener);
		}

		public void init() {
			consumer.retrieve(false);
			listener.waitForResponse(1, 1);
			assertThat(group.getReviews().size(), is(1));
			IReview review = group.getReviews().get(0);
			assertThat(review, notNullValue());
			assertThat(review.getId(), is(shortId));
			assertThat(review.getKey(), is(changeId));
			assertThat(review.getSubject(), is("Test Change " + testIdent));
			assertThat(review.getMessage(), allOf(startsWith("Test Change"), endsWith("aaa")));
			assertThat(review.getOwner().getDisplayName(), is("tests"));
			long timeDelta = System.currentTimeMillis() - review.getCreationDate().getTime();
			assertThat("Creation delta out of range : " + timeDelta + " ms", timeDelta > -CREATION_TIME_DELTA
					&& timeDelta < CREATION_TIME_DELTA, is(true));
		}
	}

	public IReview getReview() {
		return reviewHarness.consumer.getModelObject();
	}

	@Override
	@Before
	public void setUp() throws Exception {
		harness = GerritFixture.current().harness();
		connector = new GerritConnector();
		repository = GerritFixture.current().singleRepository();
		client = harness.client();

		factoryProvider = new GerritRemoteFactoryProvider(client);
		factoryProvider.setService(new JobRemoteService());

		reviewHarness = new ReviewHarness(System.currentTimeMillis() + "");
		reviewHarness.init();
	}

	@Override
	@After
	public void tearDown() throws Exception {
		harness.dispose();
	}

	public void testGlobalComments() throws Exception {
		String message1 = "new comment, time: " + System.currentTimeMillis(); //$NON-NLS-1$
		client.publishComments(reviewHarness.shortId, 1, message1, Collections.<ApprovalCategoryValue.Id> emptySet(),
				null);
		String message2 = "new comment, time: " + System.currentTimeMillis(); //$NON-NLS-1$
		client.publishComments(reviewHarness.shortId, 1, message2, Collections.<ApprovalCategoryValue.Id> emptySet(),
				null);
		reviewHarness.consumer.retrieve(false);
		reviewHarness.listener.waitForResponse(2, 2);
		List<IComment> comments = getReview().getComments();
		assertThat(comments.size(), is(2));
		IComment comment = comments.get(0);
		assertThat(comment.getAuthor().getDisplayName(), is("tests"));
		assertThat(comment.getDescription(), is("Patch Set 1:\n\n" + message1));
		assertThat(comment.getAuthor().getDisplayName(), is("tests"));
		assertThat(comment.getDescription(), is("Patch Set 1:\n\n" + message1));
	}

	@Test
	public void testNewChange() throws Exception {
		CommitCommand command2 = reviewHarness.git.commit()
				.setAmend(true)
				.setAll(true)
				.setMessage("Test Change " + reviewHarness.testIdent + " [2]\n\nChange-Id: " + reviewHarness.changeId);
		harness.project().addFile("testFile2.txt");
		harness.project().commitAndPush(command2);
		reviewHarness.consumer.retrieve(false);
		reviewHarness.listener.waitForResponse(2, 2);
		List<IReviewItemSet> items = getReview().getSets();
		assertThat(items.size(), is(2));
		IReviewItemSet patchSet2 = items.get(1);
		assertThat(patchSet2.getReference(), endsWith("/2"));
		long timeDelta = System.currentTimeMillis() - patchSet2.getCreationDate().getTime();
		assertThat("Creation delta out of range : " + timeDelta + " ms", timeDelta > -CREATION_TIME_DELTA
				&& timeDelta < CREATION_TIME_DELTA, is(true));
	}

	@Test
	public void testUsers() throws Exception {
		//Users
		assertThat(group.getUsers().size(), is(1));
		assertThat(group.getUsers().get(0).getDisplayName(), is("tests"));
	}

	@Test
	public void testApprovals() throws Exception {
		//Approvals
		assertThat(group.getApprovalTypes().size(), is(2));
		IApprovalType verifyApproval = group.getApprovalTypes().get(0);
		assertThat(verifyApproval.getKey(), is("VRIF"));
		assertThat(verifyApproval.getName(), is("Verified"));
		IApprovalType codeReviewApproval = group.getApprovalTypes().get(1);
		assertThat(codeReviewApproval.getKey(), is("CRVW"));
		assertThat(codeReviewApproval.getName(), is("Code Review"));

		String approvalMessage = "approval, time: " + System.currentTimeMillis(); //$NON-NLS-1$
		client.publishComments(reviewHarness.shortId, 1, approvalMessage, new HashSet<ApprovalCategoryValue.Id>(
				Collections.singleton(new ApprovalCategoryValue.Id(new ApprovalCategory.Id("CRVW"), (short) 1))), null);
		reviewHarness.consumer.retrieve(false);
		reviewHarness.listener.waitForResponse(2, 2);
		assertThat(getReview().getReviewerApprovals().size(), is(1));
		Entry<IUser, IReviewerEntry> reviewerEntry = getReview().getReviewerApprovals().entrySet().iterator().next();
		Map<IApprovalType, Integer> reviewerApprovals = reviewerEntry.getValue().getApprovals();
		assertThat(reviewerApprovals.size(), is(1));
		Entry<IApprovalType, Integer> next = reviewerApprovals.entrySet().iterator().next();
		assertThat(next.getKey(), sameInstance(codeReviewApproval));
		assertThat(next.getValue(), is(1));

		Set<Entry<IApprovalType, IRequirementEntry>> reviewApprovals = getReview().getRequirements().entrySet();
		assertThat(reviewApprovals.size(), is(2));
		IRequirementEntry codeReviewEntry = getReview().getRequirements().get(codeReviewApproval);
		assertThat(codeReviewEntry, notNullValue());
		assertThat(codeReviewEntry.getBy(), nullValue());
		assertThat(codeReviewEntry.getStatus(), is(RequirementStatus.NOT_SATISFIED));
		IRequirementEntry verifyEntry = getReview().getRequirements().get(verifyApproval);
		assertThat(verifyEntry, notNullValue());
		assertThat(verifyEntry.getBy(), nullValue());
		assertThat(verifyEntry.getStatus(), is(RequirementStatus.NOT_SATISFIED));

		assertThat(getReview().getState(), is(ReviewStatus.NEW));
	}

	@Test
	public void testDependencies() throws Exception {
		String changeIdDep1 = "I" + StringUtils.rightPad(System.currentTimeMillis() + "", 40, "a");
		CommitCommand commandDep1 = reviewHarness.git.commit()
				.setAll(true)
				.setMessage("Test Change Dependent 1 " + reviewHarness.testIdent + "\n\nChange-Id: " + changeIdDep1);
		harness.project().addFile("testFile1.txt", "test 2");
		CommitResult resultDep1 = harness.project().commitAndPush(commandDep1);
		String resultIdDep1 = StringUtils.trimToEmpty(StringUtils.substringAfterLast(resultDep1.push.getMessages(), "/"));
		assertThat("Bad Push: " + resultDep1.push.getMessages(), resultIdDep1.length(), greaterThan(0));

		TestRemoteObserver<IRepository, IReview> reviewListenerDep1 = new TestRemoteObserver<IRepository, IReview>(
				factoryProvider.getReviewFactory());
		RemoteEmfConsumer<IRepository, IReview, GerritChange, String, String> consumerDep1 = factoryProvider.getReviewFactory()
				.getConsumerForRemoteKey(group, resultIdDep1);
		consumerDep1.addObserver(reviewListenerDep1);
		consumerDep1.retrieve(false);
		reviewListenerDep1.waitForResponse(1, 1);
		IReview reviewDep1 = consumerDep1.getModelObject();

		assertThat(reviewDep1.getParents().size(), is(1));
		IChange parentChange = reviewDep1.getParents().get(0);
		//Not expected to be same instance
		assertThat(parentChange.getId(), is(getReview().getId()));
		assertThat(parentChange.getSubject(), is(getReview().getSubject()));
		assertThat(parentChange.getModificationDate(), is(getReview().getModificationDate()));

		reviewHarness.consumer.retrieve(false);
		reviewHarness.listener.waitForResponse(2, 2);
		assertThat(getReview().getChildren().size(), is(1));
		IChange childChange = getReview().getChildren().get(0);
		//Not expected to be same instance
		assertThat(childChange.getId(), is(reviewDep1.getId()));
		assertThat(childChange.getSubject(), is(reviewDep1.getSubject()));
		assertThat(childChange.getModificationDate(), is(reviewDep1.getModificationDate()));
	}

	@Test
	public void testPatchSetFiles() throws Exception {
		CommitCommand command2 = reviewHarness.git.commit()
				.setAmend(true)
				.setAll(true)
				.setMessage("Test Change " + reviewHarness.testIdent + " [2]\n\nChange-Id: " + reviewHarness.changeId);
		harness.project().addFile("testFile2.txt");
		harness.project().addFile("testFile3.txt");
		harness.project().commitAndPush(command2);
		CommitCommand command3 = reviewHarness.git.commit()
				.setAmend(true)
				.setAll(true)
				.setMessage("Test Change " + reviewHarness.testIdent + " [2]\n\nChange-Id: " + reviewHarness.changeId);
		harness.project().addFile("testFile2.txt", "testmod");
		harness.project().addFile("testFile4.txt");
		harness.project().addFile("testFile5.txt");
		harness.project().commitAndPush(command3);
		reviewHarness.consumer.retrieve(false);
		reviewHarness.listener.waitForResponse(2, 2);

		assertThat(getReview().getSets().size(), is(3));
		IReviewItemSet testPatchSet = getReview().getSets().get(2);
		RemoteEmfConsumer<IReview, IReviewItemSet, PatchSetDetail, PatchSetDetail, String> itemSetConsumer = factoryProvider.getReviewItemSetFactory()
				.getConsumerForLocalKey(getReview(), "3");
		itemSetConsumer.retrieve(false);
		PatchSetDetail detail = itemSetConsumer.getRemoteObject();
		assertThat(detail.getInfo().getKey().get(), is(3));

		PatchSetContentIdRemoteFactory patchFactory = factoryProvider.getReviewItemSetContentFactory();
		List<IFileItem> fileItems = testPatchSet.getItems();
		assertThat(fileItems.size(), is(0));
		TestRemoteObserver<IReviewItemSet, List<IFileItem>> patchSetListener = new TestRemoteObserver<IReviewItemSet, List<IFileItem>>(
				patchFactory);
		RemoteEmfConsumer<IReviewItemSet, List<IFileItem>, PatchSetContent, String, String> patchSetConsumer = patchFactory.getConsumerForRemoteKey(
				testPatchSet, "3");
		patchSetConsumer.addObserver(patchSetListener);
		patchSetConsumer.retrieve(false);
		patchSetListener.waitForResponse(1, 1);

		assertThat(fileItems.size(), is(6));
		for (IReviewItem fileItem : fileItems) {
			assertThat(fileItem, instanceOf(IFileItem.class));
			assertThat(fileItem.getAddedBy().getDisplayName(), is("tests"));
			assertThat(fileItem.getCommittedBy().getDisplayName(), is("tests"));
		}
		IFileItem fileItem0 = fileItems.get(0);
		assertThat(fileItem0.getName(), is("/COMMIT_MSG"));

		IFileItem fileItem1 = fileItems.get(1);
		assertThat(fileItem1.getName(), is("testFile1.txt"));

		IFileItem fileItem2 = fileItems.get(2);
		assertThat(fileItem2.getName(), is("testFile2.txt"));

		IFileVersion base2 = fileItem2.getBase();
		assertThat(base2.getAddedBy(), nullValue());
		assertThat(base2.getCommittedBy(), nullValue());
		assertThat(base2.getContent(), is(""));
		assertThat(base2.getId(), is("base-" + reviewHarness.shortId + ",3,testFile2.txt"));
		assertThat(base2.getName(), is("testFile2.txt"));
		assertThat(base2.getPath(), nullValue());
		assertThat(base2.getReference(), nullValue());
		assertThat(base2.getDescription(), is("Base"));

		IFileVersion target2 = fileItem2.getTarget();
		assertThat(target2.getAddedBy().getDisplayName(), is("tests"));
		assertThat(target2.getCommittedBy().getDisplayName(), is("tests"));
		assertThat(target2.getContent(), is("testmod"));
		assertThat(target2.getId(), is(reviewHarness.shortId + ",3,testFile2.txt"));
		assertThat(target2.getName(), is("testFile2.txt"));
		assertThat(target2.getPath(), is("testFile2.txt"));
		assertThat(target2.getReference(), nullValue());
		assertThat(target2.getDescription(), is("Patch Set 3"));
	}

	@Test
	public void testPatchSetComments() throws Exception {
		CommitCommand command2 = reviewHarness.git.commit()
				.setAmend(true)
				.setAll(true)
				.setMessage("Test Change " + reviewHarness.testIdent + " [2]\n\nChange-Id: " + reviewHarness.changeId);
		harness.project().addFile("testComments.txt", "line1\nline2\nline3\nline4\nline5\nline6\nline7\n");
		harness.project().commitAndPush(command2);
		reviewHarness.consumer.retrieve(false);
		reviewHarness.listener.waitForResponse(2, 2);
		RemoteEmfConsumer<IReview, IReviewItemSet, PatchSetDetail, PatchSetDetail, String> itemSetConsumer = factoryProvider.getReviewItemSetFactory()
				.getConsumerForLocalKey(getReview(), "2");
		itemSetConsumer.retrieve(false);
		PatchSetDetail detail = itemSetConsumer.getRemoteObject();
		assertThat(detail.getInfo().getKey().get(), is(2));

		IReviewItemSet testPatchSet = getReview().getSets().get(1);
		PatchSetContentIdRemoteFactory patchFactory = factoryProvider.getReviewItemSetContentFactory();
		TestRemoteObserver<IReviewItemSet, List<IFileItem>> patchSetListener = new TestRemoteObserver<IReviewItemSet, List<IFileItem>>(
				patchFactory);
		RemoteEmfConsumer<IReviewItemSet, List<IFileItem>, PatchSetContent, String, String> patchSetConsumer = patchFactory.getConsumerForRemoteKey(
				testPatchSet, "2");
		patchSetConsumer.addObserver(patchSetListener);
		patchSetConsumer.retrieve(false);
		patchSetListener.waitForResponse(1, 1);

		IFileItem commentFile = testPatchSet.getItems().get(1);
		assertThat(commentFile.getName(), is("testComments.txt"));
		assertThat(commentFile.getAllComments().size(), is(0));

		String id = commentFile.getReference();
		client.saveDraft(Patch.Key.parse(id), "Line 2 Comment", 2, (short) 1, null, new NullProgressMonitor());
		patchSetConsumer.retrieve(false);
		patchSetListener.waitForResponse(2, 2);

		commentFile = testPatchSet.getItems().get(1);
		List<IComment> allComments = commentFile.getAllComments();
		assertThat(allComments.size(), is(1));
		IComment fileComment = allComments.get(0);
		assertThat(fileComment, notNullValue());
		assertThat(fileComment.isDraft(), is(true));
		assertThat(fileComment.getAuthor().getDisplayName(), is("tests"));
		assertThat(fileComment.getDescription(), is("Line 2 Comment"));

		client.publishComments(reviewHarness.shortId, 2, "Submit Comments",
				Collections.<ApprovalCategoryValue.Id> emptySet(), null);
		patchSetConsumer.retrieve(false);
		patchSetListener.waitForResponse(3, 3);
		allComments = commentFile.getAllComments();
		assertThat(allComments.size(), is(1));
		fileComment = allComments.get(0);
		assertThat(fileComment, notNullValue());
		assertThat(fileComment.isDraft(), is(false));
		assertThat(fileComment.getAuthor().getDisplayName(), is("tests"));
		assertThat(fileComment.getDescription(), is("Line 2 Comment"));

	}
}
