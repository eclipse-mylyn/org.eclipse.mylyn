/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.ui.editor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.eclipse.jface.text.Region;
import org.eclipse.mylyn.internal.tasks.ui.editors.AbstractHyperlinkTextPresentationManager.RegionComparator;
import org.junit.jupiter.api.Test;

/**
 * @author Steffen Pingel
 */
public class RegionComparatorTest {

	RegionComparator comparator = new RegionComparator();

	@Test
	public void testCompareToEquals() {
		Region r1 = new Region(0, 10);
		Region r2 = new Region(0, 10);
		assertEquals(r1, r2);
		assertEquals(0, comparator.compare(r1, r2));
	}

	@Test
	public void testCompareToSameLength() {
		Region r1 = new Region(0, 10);
		Region r2 = new Region(1, 10);
		assertFalse(r1.equals(r2));
		assertEquals(-1, comparator.compare(r1, r2));
		assertEquals(1, comparator.compare(r2, r1));
	}

	@Test
	public void testCompareToNested() {
		Region r1 = new Region(0, 10);
		Region r2 = new Region(1, 8);
		assertEquals(-1, comparator.compare(r1, r2));
		assertEquals(1, comparator.compare(r2, r1));
	}

	@Test
	public void testCompareToOverlapping() {
		Region r1 = new Region(0, 10);
		Region r2 = new Region(1, 12);
		assertEquals(-1, comparator.compare(r1, r2));
		assertEquals(1, comparator.compare(r2, r1));

		r2 = new Region(1, 9);
		assertEquals(-1, comparator.compare(r1, r2));
		assertEquals(1, comparator.compare(r2, r1));
	}

	@Test
	public void testCompareToSameOffset() {
		Region r1 = new Region(5, 8);
		Region r2 = new Region(5, 10);
		assertFalse(r1.equals(r2));
		assertEquals(-1, comparator.compare(r1, r2));
		assertEquals(1, comparator.compare(r2, r1));
	}

}
