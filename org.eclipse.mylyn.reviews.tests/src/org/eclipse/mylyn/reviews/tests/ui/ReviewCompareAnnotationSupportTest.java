/*******************************************************************************
 * Copyright (c) 2014 Ericsson.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Guy Perron - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.tests.ui;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import junit.framework.TestCase;

import org.eclipse.compare.internal.MergeSourceViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.mylyn.internal.reviews.ui.compare.Direction;
import org.eclipse.mylyn.internal.reviews.ui.compare.ReviewCompareAnnotationSupport;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

/**
 * @author Guy Perron
 */
public class ReviewCompareAnnotationSupportTest extends TestCase {

	@Mock
	private ReviewCompareAnnotationSupport rcaSupportspy;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		ListViewer mockListViewer = mock(ListViewer.class);
		ReviewCompareAnnotationSupport rcaSupport = new ReviewCompareAnnotationSupport(mockListViewer);

		rcaSupportspy = spy(rcaSupport);
		doNothing().when(rcaSupportspy).moveToAnnotation((MergeSourceViewer) Matchers.any(),
				(MergeSourceViewer) Matchers.any(), (Position) Matchers.any());

	}

	//Test FORWARDS direction

	@Test
	public void testNextAnnotFwdLeftBeforeRightOffsetAfterBoth() throws Exception {
		int ret;

		// Position left < right, currentLeftOffset > Left and Right position, moving forward 
		int currentLeftOffset = 10;
		Position nextLeftPosition = new Position(5, 0);
		Position nextRightPosition = new Position(8, 0);
		ret = rcaSupportspy.calculateNextAnnotation(Direction.FORWARDS, nextLeftPosition, nextRightPosition,
				currentLeftOffset);

		assertEquals(rcaSupportspy.LEFT_SIDE, ret);
	}

	@Test
	public void testNextAnnotFwdLeftAfterRightOffsetAfterBoth() throws Exception {
		int ret;

		// Position left > right, currentLeftOffset >Left and Right position, moving forward 
		int currentLeftOffset = 10;
		Position nextLeftPosition = new Position(5, 0);
		Position nextRightPosition = new Position(3, 0);
		ret = rcaSupportspy.calculateNextAnnotation(Direction.FORWARDS, nextLeftPosition, nextRightPosition,
				currentLeftOffset);

		assertEquals(rcaSupportspy.RIGHT_SIDE, ret);
	}

	@Test
	public void testNextAnnotFwdLeftBeforeRightOffsetBeforeBoth() throws Exception {
		int ret;

		// Position left < right, currentLeftOffset < Left and Right position, moving forward 
		int currentLeftOffset = 10;
		Position nextLeftPosition = new Position(15, 0);
		Position nextRightPosition = new Position(16, 0);
		ret = rcaSupportspy.calculateNextAnnotation(Direction.FORWARDS, nextLeftPosition, nextRightPosition,
				currentLeftOffset);

		assertEquals(rcaSupportspy.LEFT_SIDE, ret);
	}

	@Test
	public void testNextAnnotFwdLeftAfterRightOffsetBeforeBoth() throws Exception {
		int ret;

		// Position left > right, currentLeftOffset < Left and Right position, moving forward 
		int currentLeftOffset = 10;
		Position nextLeftPosition = new Position(20, 0);
		Position nextRightPosition = new Position(15, 0);
		ret = rcaSupportspy.calculateNextAnnotation(Direction.FORWARDS, nextLeftPosition, nextRightPosition,
				currentLeftOffset);

		assertEquals(rcaSupportspy.RIGHT_SIDE, ret);
	}

	@Test
	public void testNextAnnotFwdLeftBeforeRightOffsetAfterLeftBeforeRightAfter() throws Exception {
		int ret;

		// Position left < right, currentLeftOffset > Left and currentLeftOffset < Right, moving forward 
		int currentLeftOffset = 10;
		Position nextLeftPosition = new Position(5, 0);
		Position nextRightPosition = new Position(15, 0);
		ret = rcaSupportspy.calculateNextAnnotation(Direction.FORWARDS, nextLeftPosition, nextRightPosition,
				currentLeftOffset);

		assertEquals(rcaSupportspy.RIGHT_SIDE, ret);
	}

	@Test
	public void testNextAnnotFwdLeftAfterRightOffsetBeforeLeftAfterRight() throws Exception {
		int ret;

		// left > right, currentLeftOffset < left && currentLeftOffset > Right , moving forward 
		int currentLeftOffset = 1;
		Position nextLeftPosition = new Position(10, 0);
		Position nextRightPosition = new Position(0, 0);
		ret = rcaSupportspy.calculateNextAnnotation(Direction.FORWARDS, nextLeftPosition, nextRightPosition,
				currentLeftOffset);

		assertEquals(rcaSupportspy.LEFT_SIDE, ret);
	}

	@Test
	public void testNextAnnotFwdLeftAfterRightOffsetEqualLeftAfterRight() throws Exception {
		int ret;

		// Position left > right, currentLeftOffset = next left && currentLeftOffset > next Right, moving forward 
		int currentLeftOffset = 10;
		Position nextLeftPosition = new Position(10, 0);
		Position nextRightPosition = new Position(0, 0);
		ret = rcaSupportspy.calculateNextAnnotation(Direction.FORWARDS, nextLeftPosition, nextRightPosition,
				currentLeftOffset);

		assertEquals(rcaSupportspy.RIGHT_SIDE, ret);
	}

	//Test BACKWARDS direction
	@Test
	public void testNextAnnotBwdLeftAfterRightOffsetBeforeBoth() throws Exception {
		int ret;

		// left after right, currentLeftOffset is before both position, moving backwards
		int currentLeftOffset = 10;
		Position nextLeftPosition = new Position(20, 0);
		Position nextRightPosition = new Position(15, 0);
		ret = rcaSupportspy.calculateNextAnnotation(Direction.BACKWARDS, nextLeftPosition, nextRightPosition,
				currentLeftOffset);

		assertEquals(rcaSupportspy.LEFT_SIDE, ret);
	}

	@Test
	public void testNextAnnotBwdLeftBeforeRightOffsetBeforeBoth() throws Exception {
		int ret;

		// left before right, currentLeftOffset is before both position, moving backwards
		int currentLeftOffset = 10;
		Position nextLeftPosition = new Position(20, 0);
		Position nextRightPosition = new Position(25, 0);
		ret = rcaSupportspy.calculateNextAnnotation(Direction.BACKWARDS, nextLeftPosition, nextRightPosition,
				currentLeftOffset);

		assertEquals(rcaSupportspy.RIGHT_SIDE, ret);
	}

	@Test
	public void testNextAnnotBwdLeftAfterRightOffsetAfterBoth() throws Exception {
		int ret;

		// left after right, currentLeftOffset is after both position, moving backwards
		int currentLeftOffset = 15;
		Position nextLeftPosition = new Position(10, 0);
		Position nextRightPosition = new Position(0, 0);
		ret = rcaSupportspy.calculateNextAnnotation(Direction.BACKWARDS, nextLeftPosition, nextRightPosition,
				currentLeftOffset);

		assertEquals(rcaSupportspy.LEFT_SIDE, ret);
	}

	@Test
	public void testNextAnnotBwdLeftBeforeRightOffsetAfterBoth() throws Exception {
		int ret;

		// left after right, currentLeftOffset is after both position, moving backwards
		int currentLeftOffset = 10;
		Position nextLeftPosition = new Position(5, 0);
		Position nextRightPosition = new Position(8, 0);
		ret = rcaSupportspy.calculateNextAnnotation(Direction.BACKWARDS, nextLeftPosition, nextRightPosition,
				currentLeftOffset);

		assertEquals(rcaSupportspy.RIGHT_SIDE, ret);
	}

	@Test
	public void testNextAnnotBwdLeftAfterRightOffsetLeftAfterRightBefore() throws Exception {
		int ret;

		// left after right, currentLeftOffset is before Left and After Right position, moving backwards
		int currentLeftOffset = 1;
		Position nextLeftPosition = new Position(10, 0);
		Position nextRightPosition = new Position(0, 0);
		ret = rcaSupportspy.calculateNextAnnotation(Direction.BACKWARDS, nextLeftPosition, nextRightPosition,
				currentLeftOffset);

		assertEquals(rcaSupportspy.RIGHT_SIDE, ret);
	}

	@Test
	public void testNextAnnotBwdLeftBeforeRightOffsetLeftBeforeRightAfter() throws Exception {
		int ret;

		// left before right, currentLeftOffset is after Left and Before Right, moving backwards
		int currentLeftOffset = 10;
		Position nextLeftPosition = new Position(5, 0);
		Position nextRightPosition = new Position(15, 0);
		ret = rcaSupportspy.calculateNextAnnotation(Direction.BACKWARDS, nextLeftPosition, nextRightPosition,
				currentLeftOffset);

		assertEquals(rcaSupportspy.LEFT_SIDE, ret);
	}

}
