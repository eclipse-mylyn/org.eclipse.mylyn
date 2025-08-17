/*******************************************************************************
 * Copyright (c) 2015 Landon Butterworth and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Landon Butterworth - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.editors.parts;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.mylyn.reviews.internal.core.ReviewsCoreConstants;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

public class TaskReview implements IAdaptable {

	private final ITask review;

	public TaskReview(ITask review) {
		this.review = review;
	}

	public String getSummary() {
		return review.getSummary();
	}

	public int getCodeReviewScore() {
		String reviewScore = review.getAttribute(Messages.TaskEditorReviewsPart_CodeReviewAttribute);

		try {
			return Integer.parseInt(reviewScore);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public int getVerifiedScore() {
		String verifiedScore = review.getAttribute(Messages.TaskEditorReviewsPart_VerifiedAttribute);

		try {
			return Integer.parseInt(verifiedScore);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public String getUrl() {
		return review.getUrl();
	}

	public SynchronizationState getSyncState() {
		return review.getSynchronizationState();
	}

	public String getStatus() {
		return review.getAttribute(TaskAttribute.STATUS);
	}

	public String getBranch() {
		return review.getAttribute(ReviewsCoreConstants.BRANCH);
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public <T> T getAdapter(Class<T> adapter) {
		if (adapter.isAssignableFrom(review.getClass())) {
			return (T) review;
		}
		return null;
	}

}