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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Date;

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
public class ReviewItemTest {

	/**
	 * Tests the '
	 * {@link org.eclipse.mylyn.reviews.core.model.IReviewItem#createTopicComment(org.eclipse.mylyn.reviews.core.model.ILocation, java.lang.String)
	 * <em>Create Topic Comment</em>}' operation. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.reviews.core.model.IReviewItem#createTopicComment(org.eclipse.mylyn.reviews.core.model.ILocation,
	 *      java.lang.String)
	 * @generated NOT
	 */
	@Test
	public void testCreateTopicComment() {
		IReviewItem item = ReviewsFactory.eINSTANCE.createReviewItem();
		ILineLocation location = ReviewsFactory.eINSTANCE.createLineLocation();
		IUser definedUser = ReviewsFactory.eINSTANCE.createUser();
		definedUser.setDisplayName("Some User");
		item.setAddedBy(definedUser);
		ITopic topic = item.createTopicComment(location, "My Comment");
		assertTrue(item.getTopics().contains(topic));
		assertSame(topic.getItem(), item);
		//TODO Is this really what we want here - do topic comments stay in sync w/ Comment comments? Why do we have this redundancy in model?
		assertEquals("My Comment", topic.getDescription());
		assertEquals(1, topic.getComments().size());
		IComment comment = topic.getComments().get(0);
		assertEquals("Some User", comment.getAuthor().getDisplayName());
		assertEquals("Some User", topic.getAuthor().getDisplayName());
		assertTrue(new Date().getTime() - 10000 < topic.getCreationDate().getTime());
		assertEquals("My Comment", comment.getDescription());
	}

	/**
	 * Tests the '
	 * {@link org.eclipse.mylyn.reviews.core.model.IReviewItem#createTopicComment(org.eclipse.mylyn.reviews.core.model.ILocation, java.lang.String)
	 * <em>Create Topic Comment</em>}' operation. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.reviews.core.model.IReviewItem#createTopicComment(org.eclipse.mylyn.reviews.core.model.ILocation,
	 *      java.lang.String)
	 * @generated NOT
	 */
	@Test
	public void testCreateTopicCommentNoUser() {
		IReviewItem item = ReviewsFactory.eINSTANCE.createReviewItem();
		ILineLocation location = ReviewsFactory.eINSTANCE.createLineLocation();
		ITopic topic = item.createTopicComment(location, "My Comment");
		assertEquals(1, topic.getComments().size());
		IComment comment = topic.getComments().get(0);
		assertEquals("<Undefined>", comment.getAuthor().getDisplayName());
		assertEquals("<Undefined>", topic.getAuthor().getDisplayName());
	}

} //ReviewItemTest
