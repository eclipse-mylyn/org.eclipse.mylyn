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
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.reviews.tests.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.reviews.ui.ReviewColumnLabelProvider;
import org.eclipse.mylyn.internal.reviews.ui.editors.parts.TaskReview;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.tasks.core.ITask;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("nls")
public class ReviewColumnLabelProviderTest {

	private final String summaryString = "new changes";

	private final String incomingString = "incoming changes";

	ITask mockTask1, mockTask2;

	TaskReview myTaskReview1, myTaskReview2;

	ReviewColumnLabelProvider myLabelProvider;

	@Before
	public void setUp() {
		mockTask1 = new TaskTask("mock", "http://mock", "taskID");

		mockTask1.setAttribute("CODE_REVIEW", "0");
		mockTask1.setAttribute("VERIFIED", "0");
		mockTask1.setAttribute("INCOMING_REVIEW", "true");

		myTaskReview1 = new TaskReview(mockTask1);

		mockTask2 = new TaskTask("mock", "http://mock", "taskID");
		mockTask2.setSummary(summaryString);

		mockTask2.setAttribute("CODE_REVIEW", "2");
		mockTask2.setAttribute("VERIFIED", "2");
		mockTask2.setAttribute("INCOMING_REVIEW", incomingString);

		myTaskReview2 = new TaskReview(mockTask2);

		myLabelProvider = new ReviewColumnLabelProvider();
	}

	@Test
	public void imagePresentTest() {

		//check that all images are not null when there should be an image present
		assertNotNull(myLabelProvider.getColumnImage(myTaskReview1, 0));
		assertNotNull(myLabelProvider.getColumnImage(myTaskReview1, 1));
		assertNotNull(myLabelProvider.getColumnImage(myTaskReview1, 2));
		assertNotNull(myLabelProvider.getColumnImage(myTaskReview1, 3));
	}

	@Test
	public void columnTextTests() {

		assertEquals(summaryString, myTaskReview2.getSummary());

		assertEquals(summaryString, myLabelProvider.getColumnText(myTaskReview2, 0));
		assertEquals(incomingString, myLabelProvider.getColumnText(myTaskReview2, 3));

		//columns that should be blank
		assertEquals("", myLabelProvider.getColumnText(myTaskReview2, 1));
		assertEquals("", myLabelProvider.getColumnText(myTaskReview2, 2));

		//columns that do not exist
		assertEquals("", myLabelProvider.getColumnText(myTaskReview2, 5));
		assertEquals("", myLabelProvider.getColumnText(myTaskReview2, 4));
	}

	@Test
	public void imageNotPresentTests() {

		//check that all images are null when they should be
		assertNotNull(myLabelProvider.getColumnImage(myTaskReview1, 0));
		assertNotNull(myLabelProvider.getColumnImage(myTaskReview1, 1));
		assertNotNull(myLabelProvider.getColumnImage(myTaskReview1, 2));
		assertNotNull(myLabelProvider.getColumnImage(myTaskReview1, 3));

		assertEquals(CommonImages.getImage(CommonImages.OVERLAY_CLEAR),
				myLabelProvider.getColumnImage(myTaskReview1, 0));
		assertEquals(CommonImages.getImage(CommonImages.OVERLAY_CLEAR),
				myLabelProvider.getColumnImage(myTaskReview1, 0));
		assertEquals(CommonImages.getImage(CommonImages.OVERLAY_CLEAR),
				myLabelProvider.getColumnImage(myTaskReview1, 0));
		assertEquals(CommonImages.getImage(CommonImages.OVERLAY_CLEAR),
				myLabelProvider.getColumnImage(myTaskReview1, 0));
	}

	@Test
	public void noImageTests() {

		//check that all images are null when they should be
		assertNotNull(myLabelProvider.getColumnImage(myTaskReview1, 0));
		assertNotNull(myLabelProvider.getColumnImage(myTaskReview1, 1));
		assertNotNull(myLabelProvider.getColumnImage(myTaskReview1, 2));
		assertNotNull(myLabelProvider.getColumnImage(myTaskReview1, 3));

		assertEquals(CommonImages.getImage(CommonImages.OVERLAY_CLEAR),
				myLabelProvider.getColumnImage(myTaskReview1, 0));
		assertEquals(CommonImages.getImage(CommonImages.OVERLAY_CLEAR),
				myLabelProvider.getColumnImage(myTaskReview1, 0));
		assertEquals(CommonImages.getImage(CommonImages.OVERLAY_CLEAR),
				myLabelProvider.getColumnImage(myTaskReview1, 0));
		assertEquals(CommonImages.getImage(CommonImages.OVERLAY_CLEAR),
				myLabelProvider.getColumnImage(myTaskReview1, 0));
	}
}
