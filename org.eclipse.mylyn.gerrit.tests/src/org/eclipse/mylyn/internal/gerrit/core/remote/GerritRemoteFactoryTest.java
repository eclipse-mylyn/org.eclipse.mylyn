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

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Calendar;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.gerrit.tests.support.GerritFixture;
import org.eclipse.mylyn.gerrit.tests.support.GerritHarness;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritChange;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.internal.gerrit.core.client.PatchSetContent;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IFileRevision;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewGroup;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.core.model.IReviewsFactory;
import org.eclipse.mylyn.reviews.core.model.ITopic;
import org.eclipse.mylyn.reviews.core.spi.remote.JobRemoteService;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfConsumer;
import org.eclipse.mylyn.tasks.core.TaskRepository;
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

	private GerritRemoteFactoryProvider service;

	private GerritHarness harness;

	@Override
	@Before
	public void setUp() throws Exception {
		//TODO -- we shouldn't be using eclipse as test host, but I can't find a test gerrit instance that is working
		harness = GerritFixture.current().harness();
		client = harness.client();
		TaskRepository repository = GerritFixture.current().singleRepository();
		service = new GerritRemoteFactoryProvider(new JobRemoteService(), client);
	}

	@Override
	@After
	public void tearDown() throws Exception {
		harness.dispose();
	}

	private final class TestListener<T> implements RemoteEmfConsumer.IObserver<T> {

		T createdObject;

		int updated;

		IStatus failure;

		@Override
		public void created(T object) {
			createdObject = object;
		}

		@Override
		public void responded(boolean modified) {
			updated++;
		}

		@Override
		public void failed(org.eclipse.core.runtime.IStatus status) {
			failure = status;
		}

		protected void waitForUpdate() {
			long delay;
			delay = 0;
			while (delay < 10000) {
				if (updated < 1) {
					try {
						Thread.sleep(10);
						delay += 10;
					} catch (InterruptedException e) {
					}
				} else {
					break;
				}
			}
			assertThat(updated, greaterThan(0));
		}

		protected void waitForFailure() {
			long delay = 0;
			while (delay < 10000) {
				if (failure == null) {
					try {
						Thread.sleep(10);
						delay += 10;
					} catch (InterruptedException e) {
					}
				} else {
					break;
				}
			}
		}

		void clear() {
			createdObject = null;
			updated = 0;
			failure = null;
		}
	}

	@Test
	public void testReviewFactory() throws CoreException {
		IReviewGroup group = IReviewsFactory.INSTANCE.createReviewGroup();
		TestListener<IReview> reviewListener = new TestListener<IReview>();
		RemoteEmfConsumer<IReviewGroup, IReview, GerritChange, String, String> consumer = service.getReviewFactory()
				.consume("Test Get Review", group, "2", "2", reviewListener);
		consumer.request();
		reviewListener.waitForUpdate();
		assertThat(group.getReviews().size(), is(1));
		IReview review = group.getReviews().get(0);
		assertThat(review, notNullValue());
		assertThat(review.getId(), is("2"));
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
		assertThat(topic.getAuthor().getDisplayName(), is("Mylyn Test User"));
		assertThat(topic.getDescription(), is("Uploaded patch set 2."));
		assertThat(topic.getComments().size(), is(1));
		IComment comment = topic.getComments().get(0);
		assertThat(comment.getAuthor().getDisplayName(), is("Mylyn Test User"));
		assertThat(comment.getDescription(), is("Uploaded patch set 2."));

		List<IReviewItem> items = review.getItems();
		assertThat(items.size(), greaterThan(1));

		IReviewItemSet patchSet1 = (IReviewItemSet) items.get(0);
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

		IReviewItemSet patchSet2 = (IReviewItemSet) items.get(1);
		Calendar patch2Created = Calendar.getInstance();
		patch2Created.setTime(patchSet2.getModificationDate());
		//TODO -- we need to get update time here, not creation time. Not clear where gerrit API provides this
		assertThat(patch2Created.get(Calendar.MINUTE), is(9));
		assertThat(patchSet2.getReference(), is("refs/changes/02/2/2"));
	}

	@Test
	public void testPatchSetContentFactory() throws CoreException {
		IReviewGroup group = IReviewsFactory.INSTANCE.createReviewGroup();
		TestListener<IReview> reviewListener = new TestListener<IReview>();
		RemoteEmfConsumer<IReviewGroup, IReview, GerritChange, String, String> consumer = service.getReviewFactory()
				.consume("Test Get Review", group, "2", "2", reviewListener);
		consumer.request();
		reviewListener.waitForUpdate();
		assertThat(group.getReviews().size(), is(1));
		IReview review = group.getReviews().get(0);

		PatchSetContentRemoteFactory patchFactory = service.getReviewItemSetContentFactory();
		assertThat(review.getItems().size(), greaterThan(2));
		IReviewItemSet patchSet4 = (IReviewItemSet) review.getItems().get(3);
		PatchSetDetail detail = service.getReviewItemSetFactory().getRemoteObject(patchSet4);
		PatchSetContent content = new PatchSetContent((PatchSet) null, detail);
		TestListener<List<IReviewItem>> reviewItemListener = new TestListener<List<IReviewItem>>();
		RemoteEmfConsumer<IReviewItemSet, List<IReviewItem>, PatchSetContent, PatchSetContent, String> patchSetConsumer = patchFactory.consume(
				"CompareItems", patchSet4, content, "", reviewItemListener);
		patchSetConsumer.request();
		reviewItemListener.waitForUpdate();
		List<IReviewItem> fileItems = patchSet4.getItems();
		assertThat(fileItems.size(), is(3));
		for (IReviewItem fileItem : fileItems) {
			assertThat(fileItem, instanceOf(IFileItem.class));
		}
		IFileItem fileItem = (IFileItem) fileItems.get(2);
		assertThat(fileItem.getAddedBy().getDisplayName(), is("Mylyn Test User"));
		assertThat(fileItem.getCommittedBy().getDisplayName(), is("Mylyn Test User"));
		//TODO Shouldn't name be last segment only?
		assertThat(fileItem.getName(), is("mylyn.test.files/item_remote_test_2.txt"));

		IFileRevision base = fileItem.getBase();
		assertThat(base.getAddedBy(), nullValue());
		assertThat(base.getCommittedBy(), nullValue());
		assertThat(base.getContent(), is(""));
		assertThat(base.getId(), is("base-2,4,mylyn.test.files/item_remote_test_2.txt"));
		assertThat(base.getName(), is("mylyn.test.files/item_remote_test_2.txt"));
		assertThat(base.getPath(), nullValue());
		assertThat(base.getReference(), nullValue());
		assertThat(base.getRevision(), is("Base"));

		IFileRevision target = fileItem.getTarget();
		assertThat(target.getAddedBy().getDisplayName(), is("Mylyn Test User"));
		assertThat(target.getCommittedBy().getDisplayName(), is("Mylyn Test User"));
		assertThat(target.getContent(), is("(Added for comment test review. V2)"));
		assertThat(target.getId(), is("2,4,mylyn.test.files/item_remote_test_2.txt"));
		assertThat(target.getName(), is("mylyn.test.files/item_remote_test_2.txt"));
		assertThat(target.getPath(), is("mylyn.test.files/item_remote_test_2.txt"));
		assertThat(target.getReference(), nullValue());
		assertThat(target.getRevision(), is("Patch Set 4"));

		List<IComment> allComments = target.getAllComments();
		assertThat(allComments.size(), is(1));
		IComment fileComment = allComments.get(0);
		assertThat(fileComment.getAuthor().getDisplayName(), is("Mylyn Test User"));
		assertThat(fileComment.getDescription(), is("Changed the version."));
	}
}
