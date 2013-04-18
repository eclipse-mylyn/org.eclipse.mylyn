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

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.gerrit.tests.support.GerritFixture;
import org.eclipse.mylyn.gerrit.tests.support.GerritHarness;
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
import org.eclipse.mylyn.reviews.core.model.IRequirementReviewState;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.core.model.IReviewerEntry;
import org.eclipse.mylyn.reviews.core.model.IReviewsFactory;
import org.eclipse.mylyn.reviews.core.model.ITopic;
import org.eclipse.mylyn.reviews.core.model.IUser;
import org.eclipse.mylyn.reviews.core.model.RequirementStatus;
import org.eclipse.mylyn.reviews.core.spi.remote.JobRemoteService;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfConsumer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gerrit.common.data.PatchSetDetail;
import com.google.gerrit.reviewdb.PatchSet;

/**
 * @author Miles Parker
 */
public class GerritRemoteFactoryTest extends TestCase {

	private GerritClient client;

	private GerritRemoteFactoryProvider factoryProvider;

	private GerritHarness harness;

	@Override
	@Before
	public void setUp() throws Exception {
		harness = GerritFixture.current().harness();
		client = harness.client();
		factoryProvider = new GerritRemoteFactoryProvider(client);
		factoryProvider.setService(new JobRemoteService());
	}

	@Override
	@After
	public void tearDown() throws Exception {
		harness.dispose();
	}

	@Test
	public void testReviewFactory() throws CoreException {
		IRepository group = IReviewsFactory.INSTANCE.createRepository();
		TestRemoteObserver<IRepository, IReview> reviewListener = new TestRemoteObserver<IRepository, IReview>(
				factoryProvider.getReviewFactory());
		RemoteEmfConsumer<IRepository, IReview, GerritChange, String, String> consumer = factoryProvider.getReviewFactory()
				.getConsumerForRemoteKey(group, "2");
		consumer.addObserver(reviewListener);
		consumer.retrieve(false);
		reviewListener.waitForResponse(1, 1);

		assertThat(group.getReviews().size(), is(1));
		IReview review = group.getReviews().get(0);
		assertThat(review, notNullValue());
		assertThat(review.getId(), is("2"));
		assertThat(review.getKey(), is("I2fcca48e6ae767e779cc428540cbe3fac6df1eb3"));
		assertThat(review.getSubject(), is("Create Remote Test Review"));
		assertThat(review.getMessage(), startsWith("Create Remote Test Review"));
		assertThat(review.getMessage(), endsWith("<tests@mylyn.eclipse.org>"));
		assertThat(review.getOwner().getDisplayName(), is("Mylyn Test User"));
		Calendar reviewCreated = Calendar.getInstance();
		reviewCreated.setTime(review.getCreationDate());
		assertThat(reviewCreated.get(Calendar.MONTH), is(Calendar.FEBRUARY));
		assertThat(reviewCreated.get(Calendar.DAY_OF_MONTH), is(19));
		assertThat(reviewCreated.get(Calendar.YEAR), is(2013));
		assertThat(reviewCreated.get(Calendar.MINUTE), is(9));
//		assertThat(review.getReviewTask().getRepositoryURL(), is("http://mylyn.org/gerrit-2.4"));
//		assertThat(review.getReviewTask().getTaskId(), is("1"));
		List<ITopic> topics = review.getTopics();
		assertThat(topics.size(), greaterThan(2));
		ITopic topic = topics.get(0);
		IComment comment = topic.getComments().get(0);
		assertThat(comment.getAuthor().getDisplayName(), is("Mylyn Test User"));
		assertThat(comment.getDescription(), is("Uploaded patch set 2."));
		assertThat(topic.getAuthor().getDisplayName(), is("Mylyn Test User"));
		assertThat(topic.getDescription(), is("Uploaded patch set 2."));
		assertThat(topic.getComments().size(), is(1));

		List<IReviewItemSet> items = review.getSets();
		assertThat(items.size(), greaterThan(1));

		IReviewItemSet patchSet1 = items.get(0);
		assertThat(patchSet1.getAddedBy().getDisplayName(), is("Mylyn Test User"));
		assertThat(patchSet1.getCommittedBy().getDisplayName(), is("Mylyn Test User"));
		Calendar patchCreated = Calendar.getInstance();
		patchCreated.setTime(patchSet1.getCreationDate());
		assertThat(patchCreated.get(Calendar.MONTH), is(Calendar.FEBRUARY));
		assertThat(patchCreated.get(Calendar.DAY_OF_MONTH), is(19));
		assertThat(patchCreated.get(Calendar.YEAR), is(2013));
		assertThat(patchCreated.get(Calendar.MINUTE), is(9));
		assertThat(patchSet1.getId(), is("1"));
		assertThat(patchSet1.getName(), is("Patch Set 1"));
		assertThat(patchSet1.getReference(), is("refs/changes/02/2/1"));

		IReviewItemSet patchSet2 = items.get(1);
		Calendar patch2Created = Calendar.getInstance();
		patch2Created.setTime(patchSet2.getModificationDate());
		//TODO -- we need to get update time here, not creation time. Not clear where gerrit API provides this
		assertThat(patch2Created.get(Calendar.MINUTE), is(9));
		assertThat(patchSet2.getReference(), is("refs/changes/02/2/2"));

		assertThat(group.getUsers().size(), is(1));
		assertThat(group.getUsers().get(0).getDisplayName(), is("Mylyn Test User"));

		//Approvals
		assertThat(group.getApprovalTypes().size(), is(2));
		IApprovalType verifyApproval = group.getApprovalTypes().get(0);
		assertThat(verifyApproval.getKey(), is("VRIF"));
		assertThat(verifyApproval.getName(), is("Verified"));
		IApprovalType codeReviewApproval = group.getApprovalTypes().get(1);
		assertThat(codeReviewApproval.getKey(), is("CRVW"));
		assertThat(codeReviewApproval.getName(), is("Code Review"));

		assertThat(review.getReviewerApprovals().size(), is(1));
		Entry<IUser, IReviewerEntry> reviewerEntry = review.getReviewerApprovals().entrySet().iterator().next();
		Map<IApprovalType, Integer> reviewerApprovals = reviewerEntry.getValue().getApprovals();
		assertThat(reviewerApprovals.size(), is(1));
		Entry<IApprovalType, Integer> next = reviewerApprovals.entrySet().iterator().next();
		assertThat(next.getKey(), sameInstance(codeReviewApproval));
		assertThat(next.getValue(), is(1));

		Set<Entry<IApprovalType, IRequirementEntry>> reviewApprovals = review.getRequirements().entrySet();
		assertThat(reviewApprovals.size(), is(2));
		IRequirementEntry codeReviewEntry = review.getRequirements().get(codeReviewApproval);
		assertThat(codeReviewEntry, notNullValue());
		assertThat(codeReviewEntry.getBy(), nullValue());
		assertThat(codeReviewEntry.getStatus(), is(RequirementStatus.NOT_SATISFIED));
		IRequirementEntry verifyEntry = review.getRequirements().get(verifyApproval);
		assertThat(verifyEntry, notNullValue());
		assertThat(verifyEntry.getBy(), nullValue());
		assertThat(verifyEntry.getStatus(), is(RequirementStatus.NOT_SATISFIED));

		assertThat(review.getState(), instanceOf(IRequirementReviewState.class));
		assertThat(((IRequirementReviewState) review.getState()).getStatus(), is(RequirementStatus.NOT_SATISFIED));
		assertThat(((IRequirementReviewState) review.getState()).getDescriptor(), is("NotSatisfied"));

		//Dependencies
		assertThat(review.getParents().size(), is(1));
		IChange parentChange = review.getParents().get(0);
		assertThat(parentChange.getId(), is("1"));
		assertThat(parentChange.getKey(), is("I4c72e71a1bce68eff290c55b52b066b15a95a7b9"));
		assertThat(parentChange.getSubject(), is("Test Review Commit"));
		assertThat(parentChange.getMessage(), nullValue());
		assertThat(parentChange.getOwner().getDisplayName(), is("Mylyn Test User"));

		assertThat(review.getChildren().size(), is(1));
		IChange childChange = review.getChildren().get(0);
		assertThat(childChange.getId(), is("3"));
		assertThat(childChange.getKey(), is("Id6475a8d1546943ed9d98139d5b553961bceb42b"));
		assertThat(childChange.getSubject(), is("New Manual Test"));
		assertThat(childChange.getMessage(), nullValue());
		assertThat(childChange.getOwner().getDisplayName(), is("Mylyn Test User"));
	}

	@Test
	public void testPatchSetContentFactory() throws CoreException {
		IRepository group = IReviewsFactory.INSTANCE.createRepository();
		TestRemoteObserver<IRepository, IReview> reviewListener = new TestRemoteObserver<IRepository, IReview>(
				factoryProvider.getReviewFactory());
		RemoteEmfConsumer<IRepository, IReview, GerritChange, String, String> reviewConsumer = factoryProvider.getReviewFactory()
				.getConsumerForRemoteKey(group, "2");
		reviewConsumer.addObserver(reviewListener);
		reviewConsumer.retrieve(false);
		reviewListener.waitForResponse(1, 1);

		assertThat(group.getReviews().size(), is(1));
		IReview review = group.getReviews().get(0);
		assertThat(review.getId(), is("2"));
		assertThat(review.getSubject(), is("Create Remote Test Review"));

		IReviewItemSet testPatchSet = review.getSets().get(3);
		RemoteEmfConsumer<IReview, IReviewItemSet, PatchSetDetail, PatchSetDetail, String> itemSetConsumer = factoryProvider.getReviewItemSetFactory()
				.getConsumerForLocalKey(review, "4");
		itemSetConsumer.retrieve(false);
		PatchSetDetail detail = itemSetConsumer.getRemoteObject();
		assertThat(detail.getInfo().getKey().get(), is(4));

		PatchSetContent content = new PatchSetContent((PatchSet) null, detail);

		PatchSetContentIdRemoteFactory patchFactory = factoryProvider.getReviewItemSetContentFactory();
		assertThat(review.getSets().size(), greaterThan(2));
		List<IFileItem> fileItems = testPatchSet.getItems();
		assertThat(fileItems.size(), is(0));
		TestRemoteObserver<IReviewItemSet, List<IFileItem>> patchSetListener = new TestRemoteObserver<IReviewItemSet, List<IFileItem>>(
				patchFactory);
		RemoteEmfConsumer<IReviewItemSet, List<IFileItem>, PatchSetContent, String, String> patchSetConsumer = patchFactory.getConsumerForRemoteKey(
				testPatchSet, "4");
		patchSetConsumer.addObserver(patchSetListener);
		patchSetConsumer.retrieve(false);
		patchSetListener.waitForResponse(1, 1);

		assertThat(fileItems.size(), is(3));
		for (IReviewItem fileItem : fileItems) {
			assertThat(fileItem, instanceOf(IFileItem.class));
			assertThat(fileItem.getAddedBy().getDisplayName(), is("Mylyn Test User"));
			assertThat(fileItem.getCommittedBy().getDisplayName(), is("Mylyn Test User"));
		}
		IFileItem fileItem0 = fileItems.get(0);
		assertThat(fileItem0.getName(), is("/COMMIT_MSG"));

		IFileItem fileItem1 = fileItems.get(1);
		assertThat(fileItem1.getName(), is("mylyn.test.files/item_remote_test.txt"));

		IFileItem fileItem2 = fileItems.get(2);
		assertThat(fileItem2.getName(), is("mylyn.test.files/item_remote_test_2.txt"));

		IFileVersion base2 = fileItem2.getBase();
		assertThat(base2.getAddedBy(), nullValue());
		assertThat(base2.getCommittedBy(), nullValue());
		assertThat(base2.getContent(), is(""));
		assertThat(base2.getId(), is("base-2,4,mylyn.test.files/item_remote_test_2.txt"));
		assertThat(base2.getName(), is("mylyn.test.files/item_remote_test_2.txt"));
		assertThat(base2.getPath(), nullValue());
		assertThat(base2.getReference(), nullValue());
		assertThat(base2.getDescription(), is("Base"));

		IFileVersion target2 = fileItem2.getTarget();
		assertThat(target2.getAddedBy().getDisplayName(), is("Mylyn Test User"));
		assertThat(target2.getCommittedBy().getDisplayName(), is("Mylyn Test User"));
		assertThat(target2.getContent(), is("(Added for comment test review. V2)"));
		assertThat(target2.getId(), is("2,4,mylyn.test.files/item_remote_test_2.txt"));
		assertThat(target2.getName(), is("mylyn.test.files/item_remote_test_2.txt"));
		assertThat(target2.getPath(), is("mylyn.test.files/item_remote_test_2.txt"));
		assertThat(target2.getReference(), nullValue());
		assertThat(target2.getDescription(), is("Patch Set 4"));

		List<IComment> allComments = target2.getAllComments();
		assertThat(allComments.size(), is(1));
		IComment fileComment = allComments.get(0);
		assertThat(fileComment, notNullValue());
		assertThat(fileComment.getAuthor().getDisplayName(), is("Mylyn Test User"));
		assertThat(fileComment.getDescription(), is("Changed the version."));
	}
}
