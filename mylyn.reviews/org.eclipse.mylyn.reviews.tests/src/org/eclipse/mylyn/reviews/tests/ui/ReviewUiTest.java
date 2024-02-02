/*******************************************************************************
 * Copyright (c) 2012, 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Guy Perron 423242: Add ability to edit comment from compare navigator popup

 *******************************************************************************/

package org.eclipse.mylyn.reviews.tests.ui;

import org.eclipse.mylyn.reviews.tests.util.MockReviewBehavior;
import org.eclipse.mylyn.reviews.ui.ReviewBehavior;
import org.eclipse.mylyn.reviews.ui.ReviewUi;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * @author Steffen Pingel
 * @author Guy Perron
 */
public class ReviewUiTest extends TestCase {

	@Test
	public void testGetActiveReview() {
		ReviewBehavior activeReview = new MockReviewBehavior();
		ReviewUi.setActiveReview(activeReview);
		assertEquals(activeReview, ReviewUi.getActiveReview());
	}
}
