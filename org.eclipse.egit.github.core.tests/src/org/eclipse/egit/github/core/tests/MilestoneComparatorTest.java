/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.Assert.assertEquals;

import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.util.MilestoneComparator;
import org.junit.Test;

/**
 * Unit tests of {@link MilestoneComparator}
 */
public class MilestoneComparatorTest {

	/**
	 * Compare milestones
	 */
	@Test
	public void compareMilestones() {
		MilestoneComparator cmp = new MilestoneComparator();
		Milestone m1 = new Milestone().setTitle("a");
		Milestone m2 = new Milestone().setTitle("b");
		assertEquals(-1, cmp.compare(m1, m2));
		assertEquals(0, cmp.compare(m1, m1));
		assertEquals(1, cmp.compare(m2, m1));
	}
}
