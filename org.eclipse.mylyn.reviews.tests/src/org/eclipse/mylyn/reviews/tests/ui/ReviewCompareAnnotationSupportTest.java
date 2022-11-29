/*******************************************************************************
 * Copyright (c) 2014 Ericsson.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Guy Perron - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.tests.ui;

import static org.eclipse.mylyn.internal.reviews.ui.compare.ReviewCompareAnnotationSupport.Side.LEFT_SIDE;
import static org.eclipse.mylyn.internal.reviews.ui.compare.ReviewCompareAnnotationSupport.Side.RIGHT_SIDE;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import junit.framework.TestCase;

import org.eclipse.compare.internal.MergeSourceViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.mylyn.internal.reviews.ui.compare.Direction;
import org.eclipse.mylyn.internal.reviews.ui.compare.ReviewCompareAnnotationSupport;
import org.eclipse.mylyn.internal.reviews.ui.compare.ReviewCompareAnnotationSupport.Side;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

/**
 * @author Guy Perron
 */
public class ReviewCompareAnnotationSupportTest extends TestCase {

	@Spy
	private final ReviewCompareAnnotationSupport rcaSupportspy = new ReviewCompareAnnotationSupport(
			mock(ListViewer.class));

	@Override
	public void setUp() throws Exception {
		super.setUp();
		MockitoAnnotations.initMocks(this);
		doNothing().when(rcaSupportspy)
				.moveToAnnotation((MergeSourceViewer) Matchers.any(), (MergeSourceViewer) Matchers.any(),
						(Position) Matchers.any());

	}

	@Test
	public void testNextAnnotFwdLeftBeforeRightOffsetAfterBoth() throws Exception {
		// Position left < right, currentLeftOffset > Left and Right position, moving forward 
		assertSide(Direction.FORWARDS, 5, 8, 10, LEFT_SIDE);
	}

	@Test
	public void testNextAnnotFwdLeftAfterRightOffsetAfterBoth() throws Exception {
		// Position left > right, currentLeftOffset >Left and Right position, moving forward 
		assertSide(Direction.FORWARDS, 5, 3, 10, RIGHT_SIDE);
	}

	@Test
	public void testNextAnnotFwdLeftBeforeRightOffsetBeforeBoth() throws Exception {
		// Position left < right, currentLeftOffset < Left and Right position, moving forward 
		assertSide(Direction.FORWARDS, 15, 16, 10, LEFT_SIDE);
	}

	@Test
	public void testNextAnnotFwdLeftAfterRightOffsetBeforeBoth() throws Exception {
		// Position left > right, currentLeftOffset < Left and Right position, moving forward 
		assertSide(Direction.FORWARDS, 20, 15, 10, RIGHT_SIDE);
	}

	@Test
	public void testNextAnnotFwdLeftBeforeRightOffsetAfterLeftBeforeRightAfter() throws Exception {
		// Position left < right, currentLeftOffset > Left and currentLeftOffset < Right, moving forward 
		assertSide(Direction.FORWARDS, 5, 15, 10, RIGHT_SIDE);
	}

	@Test
	public void testNextAnnotFwdLeftAfterRightOffsetBeforeLeftAfterRight() throws Exception {
		// left > right, currentLeftOffset < left && currentLeftOffset > Right , moving forward 
		assertSide(Direction.FORWARDS, 10, 0, 1, LEFT_SIDE);
	}

	@Test
	public void testNextAnnotFwdLeftAfterRightOffsetEqualLeftAfterRight() throws Exception {
		// Position left > right, currentLeftOffset = next left && currentLeftOffset > next Right, moving forward 
		assertSide(Direction.FORWARDS, 10, 0, 10, RIGHT_SIDE);
	}

	@Test
	public void testNextAnnotFwdLeftAfterRightOffsetBeforeLeftEqualRight() throws Exception {
		// Position left > right, currentLeftOffset < next Left && currentLeftOffset = next right, moving forward 
		assertSide(Direction.FORWARDS, 10, 0, 0, LEFT_SIDE);
	}

	@Test
	public void testNextAnnotFwdLeftBeforeRightOffsetEqualLeftBeforeRight() throws Exception {
		// Position left > right, currentLeftOffset = next left && currentLeftOffset > next Right, moving forward 
		assertSide(Direction.FORWARDS, 0, 10, 0, RIGHT_SIDE);
	}

	@Test
	public void testNextAnnotFwdLeftBeforeRightOffsetAfterLeftEqualRight() throws Exception {
		// Position left > right, currentLeftOffset < next Left && currentLeftOffset = next right, moving forward 
		assertSide(Direction.FORWARDS, 0, 10, 10, LEFT_SIDE);
	}

	@Test
	public void testNextAnnotBwdLeftAfterRightOffsetBeforeBoth() throws Exception {
		// left after right, currentLeftOffset is before both position, moving backwards
		assertSide(Direction.BACKWARDS, 20, 15, 10, LEFT_SIDE);
	}

	@Test
	public void testNextAnnotBwdLeftBeforeRightOffsetBeforeBoth() throws Exception {
		// left before right, currentLeftOffset is before both position, moving backwards
		assertSide(Direction.BACKWARDS, 20, 25, 10, RIGHT_SIDE);
	}

	@Test
	public void testNextAnnotBwdLeftAfterRightOffsetAfterBoth() throws Exception {
		// left after right, currentLeftOffset is after both position, moving backwards
		assertSide(Direction.BACKWARDS, 10, 0, 15, LEFT_SIDE);
	}

	@Test
	public void testNextAnnotBwdLeftBeforeRightOffsetAfterBoth() throws Exception {
		// left after right, currentLeftOffset is after both position, moving backwards
		assertSide(Direction.BACKWARDS, 5, 8, 10, RIGHT_SIDE);
	}

	@Test
	public void testNextAnnotBwdLeftAfterRightOffsetLeftAfterRightBefore() throws Exception {
		// left after right, currentLeftOffset is before Left and After Right position, moving backwards
		assertSide(Direction.BACKWARDS, 10, 0, 1, RIGHT_SIDE);
	}

	@Test
	public void testNextAnnotBwdLeftBeforeRightOffsetLeftBeforeRightAfter() throws Exception {
		// left before right, currentLeftOffset is after Left and Before Right, moving backwards
		assertSide(Direction.BACKWARDS, 5, 15, 10, LEFT_SIDE);
	}

	@Test
	public void testNextAnnotBwdLeftAfterRightOffsetEqualLeftAfterRight() throws Exception {
		// Position left > right, currentLeftOffset = next left && currentLeftOffset > next Right, moving backwards 
		assertSide(Direction.BACKWARDS, 10, 0, 10, RIGHT_SIDE);
	}

	@Test
	public void testNextAnnotBwdLeftAfterRightOffsetBeforeLeftEqualRight() throws Exception {
		// Position left > right, currentLeftOffset = next left && currentLeftOffset > next Right, moving backwards 
		assertSide(Direction.BACKWARDS, 10, 0, 0, LEFT_SIDE);
	}

	@Test
	public void testNextAnnotBwdLeftBeforeRightOffsetEqualLeftBeforeRight() throws Exception {
		// Position left > right, currentLeftOffset = next left && currentLeftOffset > next Right, moving backwards 
		assertSide(Direction.BACKWARDS, 0, 10, 0, RIGHT_SIDE);
	}

	@Test
	public void testNextAnnotBwdLeftBeforeRightOffsetAfterLeftEqualRight() throws Exception {
		// Position left > right, currentLeftOffset = next left && currentLeftOffset > next Right, moving backwards 
		assertSide(Direction.BACKWARDS, 0, 10, 10, LEFT_SIDE);
	}

	private void assertSide(Direction direction, int left, int right, int currentLeftOffset, Side expectedSide) {
		Position nextLeftPosition = new Position(left, 0);
		Position nextRightPosition = new Position(right, 0);
		Side side = rcaSupportspy.calculateNextAnnotation(direction, nextLeftPosition, nextRightPosition,
				currentLeftOffset);

		assertEquals(expectedSide, side);
	}

}
