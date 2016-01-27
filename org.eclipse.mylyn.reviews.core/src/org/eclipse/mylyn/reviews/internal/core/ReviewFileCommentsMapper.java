/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.internal.core;

import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;

public class ReviewFileCommentsMapper {

	public static final String FILE_ITEM_COMMENTS = "FILE-COMMENTS"; //$NON-NLS-1$

	private final IReview review;

	public ReviewFileCommentsMapper(IReview review) {
		this.review = review;
	}

	public void applyTo(TaskData taskData) {
		TaskAttribute comments = getOrCreateAttribute(taskData.getRoot(), FILE_ITEM_COMMENTS);
		for (IReviewItemSet set : review.getSets()) {
			for (IFileItem file : set.getItems()) {
				for (IComment comment : file.getAllComments()) {
					TaskAttribute commentAttribute = getOrCreateAttribute(comments, comment.getId());
					commentAttribute.setValue(comment.getDescription());
				}
			}
		}
	}

	private TaskAttribute getOrCreateAttribute(TaskAttribute parent, String id) {
		if (parent.getAttribute(id) == null) {
			return parent.createAttribute(id);
		}
		return parent.getAttribute(id);
	}

}
