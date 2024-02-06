/*******************************************************************************
 * Copyright (c) 2014, 2016 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Guy Perron 423242: Add ability to edit comment from compare navigator popup
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.reviews.tests.ui;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.mylyn.internal.reviews.ui.annotations.ReviewAnnotationModel;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.ILineLocation;
import org.eclipse.mylyn.reviews.core.model.ILineRange;
import org.eclipse.mylyn.reviews.core.model.ILocation;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.mylyn.reviews.core.model.IReviewsFactory;
import org.eclipse.mylyn.reviews.core.model.IUser;
import org.eclipse.mylyn.reviews.tests.util.MockReviewBehavior;

import junit.framework.TestCase;

/**
 * @author Leo Dos Santos
 * @author Guy Perron
 */

@SuppressWarnings("nls")
public class ReviewAnnotationModelTest extends TestCase {

	private final static String DEFAULT_TEXT = "This change looks good.";

	private final static long DEFAULT_TIMESTAMP = 1388577600000L;

	private IDocument doc;

	private IReviewItem review;

	private ReviewAnnotationModel model;

	@Override
	protected void setUp() throws Exception {
		doc = new Document("A test document, nothing special.");
		review = IReviewsFactory.INSTANCE.createFileItem();
		generateComment(DEFAULT_TEXT, new Date(DEFAULT_TIMESTAMP));

		model = new ReviewAnnotationModel();
		model.setItem(review, new MockReviewBehavior());
		model.connect(doc);
	}

	@Override
	protected void tearDown() throws Exception {
		model.disconnect(doc);
	}

	public void testConnect() {
		Iterator<Annotation> iter = model.getAnnotationIterator();
		assertEquals(1, getCount(iter));

		model.disconnect(doc);
		iter = model.getAnnotationIterator();
		assertEquals(0, getCount(iter));

		model.connect(doc);
		iter = model.getAnnotationIterator();
		assertEquals(1, getCount(iter));
	}

	public void testNotifyChanged() {
		Iterator<Annotation> iter = model.getAnnotationIterator();
		assertEquals(1, getCount(iter));

		// Comments sometimes come in with Dates and sometimes with Timestamps,
		// so we need to be able to handle both.
		IComment clone = generateComment(DEFAULT_TEXT, new Timestamp(DEFAULT_TIMESTAMP));
		Notification notification = new NotificationImpl(Notification.ADD, null, clone);
		model.getItem().eNotify(notification);

		iter = model.getAnnotationIterator();
		assertEquals(1, getCount(iter));

		IComment newComment = generateComment("Actually, maybe it needs more work.", new Date());
		notification = new NotificationImpl(Notification.ADD, null, newComment);
		model.getItem().eNotify(notification);

		iter = model.getAnnotationIterator();
		assertEquals(2, getCount(iter));

		notification = new NotificationImpl(Notification.REMOVE, newComment, null);
		model.getItem().eNotify(notification);

		iter = model.getAnnotationIterator();
		assertEquals(1, getCount(iter));

	}

	private int getCount(Iterator<Annotation> iter) {
		int count = 0;
		while (iter.hasNext()) {
			count++;
			iter.next();
		}
		return count;
	}

	private IUser generateUser() {
		IUser user = IReviewsFactory.INSTANCE.createUser();
		user.setId("1");
		user.setDisplayName("Leo Dos Santos");
		user.setEmail("leo@testkop.com");
		return user;
	}

	private IComment generateComment(String text, Date date) {
		IComment comment = IReviewsFactory.INSTANCE.createComment();
		comment.getLocations().add(generateLocation());
		comment.setDescription(text);
		comment.setCreationDate(date);
		comment.setDraft(false);
		comment.setId("12345");
		comment.setAuthor(generateUser());
		comment.setItem(review);
		return comment;
	}

	private ILocation generateLocation() {
		ILineRange range = IReviewsFactory.INSTANCE.createLineRange();
		range.setStart(0);
		range.setEnd(10);
		ILineLocation location = IReviewsFactory.INSTANCE.createLineLocation();
		location.getRanges().add(range);
		return location;
	}

}
