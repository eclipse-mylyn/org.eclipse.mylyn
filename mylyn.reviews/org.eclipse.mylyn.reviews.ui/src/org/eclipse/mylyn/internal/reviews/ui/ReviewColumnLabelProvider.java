/*******************************************************************************
 * Copyright (c) 2015, 2016 Landon Butterworth and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Landon Butterworth - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.reviews.ui.editors.parts.TaskReview;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.swt.graphics.Image;

/**
 * @author Landon Butterworth
 */
public class ReviewColumnLabelProvider extends ColumnLabelProvider {

	private final int VERIFIED = 1;

	private final int NOT_VERIFIED = -1;

	private static final int DESCRIPTION_COLUMN = 0;

	private static final int BRANCH_COLUMN = 1;

	private static final int CODE_REVIEW_COLUMN = 2;

	private static final int VERIFIED_COLUMN = 3;

	private static final int STATUS_COLUMN = 4;

	private static final int MINUS_TWO = -2;

	private static final int MINUS_ONE = -1;

	private static final int PLUS_ONE = 1;

	private static final int PLUS_TWO = 2;

	public ReviewColumnLabelProvider() {
	}

	public Image getColumnImage(Object element, int columnIndex) {
		TaskReview reviewContainer = (TaskReview) element;

		switch (columnIndex) {
			case DESCRIPTION_COLUMN:
				return getIncomingChangesImage(reviewContainer);
			case CODE_REVIEW_COLUMN:
				return getReviewStateImage(reviewContainer);
			case VERIFIED_COLUMN:
				return getVerifiedStateImage(reviewContainer);
		}

		return null;
	}

	private Image getIncomingChangesImage(TaskReview reviewContainer) {
		if (reviewContainer.getSyncState() == SynchronizationState.INCOMING) {
			return CommonImages.getImage(CommonImages.OVERLAY_SYNC_INCOMMING);
		} else if (reviewContainer.getSyncState() == SynchronizationState.INCOMING_NEW) {
			return CommonImages.getImage(CommonImages.OVERLAY_SYNC_INCOMMING_NEW);
		}

		return CommonImages.getImage(CommonImages.OVERLAY_CLEAR);
	}

	private Image getReviewStateImage(TaskReview reviewContainer) {
		int reviewScore = reviewContainer.getCodeReviewScore();

		switch (reviewScore) {
			case MINUS_TWO:
				return CommonImages.getImage(ReviewsImages.RED_NOT);
			case MINUS_ONE:
				return CommonImages.getImage(ReviewsImages.MINUS_ONE);
			case PLUS_ONE:
				return CommonImages.getImage(ReviewsImages.PLUS_ONE);
			case PLUS_TWO:
				return CommonImages.getImage(ReviewsImages.GREEN_CHECK);
		}

		return CommonImages.getImage(CommonImages.OVERLAY_CLEAR);
	}

	private Image getVerifiedStateImage(TaskReview reviewContainer) {
		int verifiedState = reviewContainer.getVerifiedScore();

		if (verifiedState >= VERIFIED) {
			return CommonImages.getImage(ReviewsImages.GREEN_CHECK);
		} else if (verifiedState <= NOT_VERIFIED) {
			return CommonImages.getImage(ReviewsImages.RED_NOT);
		}

		return CommonImages.getImage(CommonImages.OVERLAY_CLEAR);
	}

	public String getColumnText(Object element, int columnIndex) {
		TaskReview reviewContainer = (TaskReview) element;

		return switch (columnIndex) {
			case DESCRIPTION_COLUMN -> reviewContainer.getSummary();
			case BRANCH_COLUMN -> reviewContainer.getBranch();
			case STATUS_COLUMN -> reviewContainer.getStatus();
			default -> ""; //$NON-NLS-1$
		};
	}

	public String getSortString(Object element, int columnIndex) {
		TaskReview reviewContainer = (TaskReview) element;

		return switch (columnIndex) {
			case CODE_REVIEW_COLUMN -> Integer.toString(reviewContainer.getCodeReviewScore());
			case VERIFIED_COLUMN -> Integer.toString(reviewContainer.getVerifiedScore());
			default -> getColumnText(element, columnIndex);
		};
	}

	@Override
	public void update(ViewerCell cell) {
		Object element = cell.getElement();

		int columnNumber = cell.getColumnIndex();

		cell.setImage(getColumnImage(element, columnNumber));
		cell.setText(getColumnText(element, columnNumber));
	}
}