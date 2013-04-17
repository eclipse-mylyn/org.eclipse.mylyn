/**
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 */
package org.eclipse.mylyn.reviews.core.model;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.eclipse.mylyn.reviews.internal.core.model.ReviewsFactory;
import org.junit.Test;

/**
 * <!-- begin-user-doc --> A test case for the model object '<em><b>Review Item</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following operations are tested:
 * <ul>
 * <li>
 * {@link org.eclipse.mylyn.reviews.core.model.IReviewItem#createTopicComment(org.eclipse.mylyn.reviews.core.model.ILocation, java.lang.String)
 * <em>Create Topic Comment</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class TopicContainerTest {

	@Test
	public void testGetAllCommentsFileItems() {
		IFileItem f1 = ReviewsFactory.eINSTANCE.createFileItem();
		ITopic topic0 = ReviewsFactory.eINSTANCE.createTopic();
		f1.getTopics().add(topic0);
		ITopic topic1 = ReviewsFactory.eINSTANCE.createTopic();
		f1.getTopics().add(topic1);
		f1.setBase(ReviewsFactory.eINSTANCE.createFileVersion());
		f1.setTarget(ReviewsFactory.eINSTANCE.createFileVersion());
		ITopic topic2 = ReviewsFactory.eINSTANCE.createTopic();
		f1.getBase().getTopics().add(topic2);
		ITopic topic3 = ReviewsFactory.eINSTANCE.createTopic();
		f1.getTarget().getTopics().add(topic3);
		assertThat(f1.getAllComments().size(), is(4));
		assertThat((ITopic) f1.getAllComments().get(0), sameInstance(topic0));
		assertThat((ITopic) f1.getAllComments().get(1), sameInstance(topic1));
		assertThat((ITopic) f1.getAllComments().get(2), sameInstance(topic2));
		assertThat((ITopic) f1.getAllComments().get(3), sameInstance(topic3));
	}

	@Test
	public void testGetAllCommentsReviewItems() {
		IReviewItem i1 = ReviewsFactory.eINSTANCE.createReviewItem();
		i1.getTopics().add(ReviewsFactory.eINSTANCE.createTopic());
		i1.getTopics().add(ReviewsFactory.eINSTANCE.createTopic());
		assertThat(i1.getAllComments().size(), is(2));
	}

	@Test
	public void testGetAllCommentsReviewItemSet() {
		IReviewItemSet itemSet = ReviewsFactory.eINSTANCE.createReviewItemSet();
		IFileItem i1 = IReviewsFactory.INSTANCE.createFileItem();
		ITopic t0 = ReviewsFactory.eINSTANCE.createTopic();
		i1.getTopics().add(t0);
		ITopic t1 = ReviewsFactory.eINSTANCE.createTopic();
		i1.getTopics().add(t1);
		itemSet.getItems().add(i1);
		assertThat(itemSet.getAllComments().size(), is(2));

		IFileItem i2 = IReviewsFactory.INSTANCE.createFileItem();
		ITopic t2 = ReviewsFactory.eINSTANCE.createTopic();
		i2.getTopics().add(t2);
		ITopic t3 = ReviewsFactory.eINSTANCE.createTopic();
		i2.getTopics().add(t3);
		itemSet.getItems().add(i2);
		assertThat(itemSet.getAllComments().size(), is(4));
	}

	@Test
	public void testGetAllCommentsReview() {
		IReview review = ReviewsFactory.eINSTANCE.createReview();
		ITopic t0 = ReviewsFactory.eINSTANCE.createTopic();
		review.getTopics().add(t0);
		assertThat(review.getAllComments().size(), is(1));

		IReviewItemSet itemSet = ReviewsFactory.eINSTANCE.createReviewItemSet();
		review.getSets().add(itemSet);
		IFileItem i1 = IReviewsFactory.INSTANCE.createFileItem();
		ITopic t1 = ReviewsFactory.eINSTANCE.createTopic();
		i1.getTopics().add(t1);
		ITopic t2 = ReviewsFactory.eINSTANCE.createTopic();
		i1.getTopics().add(t2);
		itemSet.getItems().add(i1);
		assertThat(review.getAllComments().size(), is(3));

		IFileItem i2 = IReviewsFactory.INSTANCE.createFileItem();
		ITopic t3 = ReviewsFactory.eINSTANCE.createTopic();
		i2.getTopics().add(t3);
		ITopic t4 = ReviewsFactory.eINSTANCE.createTopic();
		i2.getTopics().add(t4);
		itemSet.getItems().add(i2);
		assertThat(review.getAllComments().size(), is(5));

		IReviewItemSet reviewSubSet = ReviewsFactory.eINSTANCE.createReviewItemSet();
		IFileItem i4 = IReviewsFactory.INSTANCE.createFileItem();
		ITopic t5 = ReviewsFactory.eINSTANCE.createTopic();
		i4.getTopics().add(t5);
		t5.setId("5");
		reviewSubSet.getItems().add(i4);
		review.getSets().add(reviewSubSet);
		assertThat(review.getAllComments().size(), is(6));
		assertThat((ITopic) review.getAllComments().get(0), is(t0));
		assertThat((ITopic) review.getAllComments().get(1), is(t1));
		assertThat((ITopic) review.getAllComments().get(2), is(t2));
		assertThat((ITopic) review.getAllComments().get(3), is(t3));
		assertThat((ITopic) review.getAllComments().get(4), is(t4));
		assertThat((ITopic) review.getAllComments().get(5), is(t5));

	}

	@Test
	public void testGetAllCommentReplies() {
		IReview review = ReviewsFactory.eINSTANCE.createReview();
		ITopic mainTopic = ReviewsFactory.eINSTANCE.createTopic();
		review.getTopics().add(mainTopic);
		List<IComment> allComments = review.getAllComments();
		assertThat(allComments.size(), is(1));

		IComment comment0 = ReviewsFactory.eINSTANCE.createComment();
		mainTopic.getReplies().add(comment0);

		allComments = review.getAllComments();
		assertThat(allComments.size(), is(2));
		assertThat((ITopic) allComments.get(0), is(mainTopic));
		assertThat(allComments.get(1), is(comment0));
	}

	@Test
	public void testCreateReviewItemTopicComment() {
		IReviewItem item = ReviewsFactory.eINSTANCE.createReviewItem();
		ILineLocation location = ReviewsFactory.eINSTANCE.createLineLocation();
		IUser definedUser = ReviewsFactory.eINSTANCE.createUser();
		definedUser.setDisplayName("Some User");
		item.setAddedBy(definedUser);
		ITopic topic = item.createTopicComment(location, "My Comment");
		assertTrue(item.getTopics().contains(topic));
		assertSame(topic.getItem(), item);
		//TODO Is this really what we want here - do topic comments stay in sync w/ Comment comments? Why do we have this redundancy in model?
		assertThat(topic.getDescription(), is("My Comment"));
		assertThat(topic.getComments().size(), is(1));
		IComment comment = topic.getComments().get(0);
		assertThat(comment.getAuthor().getDisplayName(), is("Some User"));
		assertThat(topic.getAuthor().getDisplayName(), is("Some User"));
		assertTrue(new Date().getTime() - 10000 < topic.getCreationDate().getTime());
		assertThat(comment.getDescription(), is("My Comment"));
	}

	@Test
	public void testCreateReviewItemTopicCommentNoUser() {
		IReviewItem item = ReviewsFactory.eINSTANCE.createReviewItem();
		ILineLocation location = ReviewsFactory.eINSTANCE.createLineLocation();
		ITopic topic = item.createTopicComment(location, "My Comment");
		assertThat(item.getTopics().size(), is(1));
		assertThat(topic.getComments().size(), is(1));
		IComment comment = topic.getComments().get(0);
		assertThat(comment.getAuthor().getDisplayName(), is("<Undefined>"));
		assertThat(topic.getAuthor().getDisplayName(), is("<Undefined>"));
	}

	@Test
	public void testCreateReviewTopicCommentNoUser() {
		IReview review = ReviewsFactory.eINSTANCE.createReview();
		ILineLocation location = ReviewsFactory.eINSTANCE.createLineLocation();
		ITopic topic = review.createTopicComment(location, "My Comment");
		assertThat(review.getTopics().size(), is(1));
		assertThat(topic.getComments().size(), is(1));
		IComment comment = topic.getComments().get(0);
		assertNull(comment.getAuthor());
	}

	@Test
	public void testDerivedTopicContainer() {
		final ITopic topic = ReviewsFactory.eINSTANCE.createTopic();
		IReview review = ReviewsFactory.eINSTANCE.createReview();
		review.getDirectTopics().add(topic);
		assertThat(review.getTopics().size(), is(1));
		assertThat(review.getTopics().get(0), is(topic));
	}
} //ReviewItemTest
