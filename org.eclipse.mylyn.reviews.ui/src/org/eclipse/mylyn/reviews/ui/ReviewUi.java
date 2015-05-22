/*******************************************************************************
 * Copyright (c) 2011, 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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
