/*******************************************************************************
 * Copyright (c) 2011, 2012 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.ui;

/**
 * @author Steffen Pingel
 */
public class ReviewUi {

	private static final String PROPERTY = "org.eclipse.mylyn.reviews.ui.review.Active"; //$NON-NLS-1$

	private static ReviewBehavior activeReview;

	public static ReviewBehavior getActiveReview() {
		return activeReview;
	}

	public static void setActiveReview(ReviewBehavior activeReview) {
		ReviewUi.activeReview = activeReview;
		if (activeReview != null) {
			System.setProperty(PROPERTY, Boolean.TRUE.toString());
		} else {
			System.setProperty(PROPERTY, null);
		}
	}

}
