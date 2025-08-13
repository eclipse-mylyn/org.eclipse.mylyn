/*******************************************************************************
 * Copyright (c) 2011, 2013 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.ui;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Item;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class ColumnViewerSorterTest {

	class StubColumnViewerSorter extends AbstractColumnViewerSorter<ColumnViewer, Item> {

		Item sortColumn;

		int sortDirection;

		int columnIndex;

		@Override
		Item getSortColumn(ColumnViewer viewer) {
			return sortColumn;
		}

		@Override
		int getSortDirection(ColumnViewer viewer) {
			return sortDirection;
		}

		@Override
		int getColumnIndex(ColumnViewer viewer, Item column) {
			return columnIndex;
		}

	}

	private StubColumnViewerSorter sorter;

	@Before
	public void setUp() {
		sorter = new StubColumnViewerSorter();
	}

	@Test
	public void testCompare() {
		ColumnViewer viewer = mock(ColumnViewer.class);
		assertEquals(-1, sorter.compare(viewer, "a", "b"));
		assertEquals(0, sorter.compare(viewer, "a", "a"));
		assertEquals(1, sorter.compare(viewer, 2, 1));
	}

	@Test
	public void testCompareSortColumn() {
		ColumnViewer viewer = mock(ColumnViewer.class);
		sorter.sortColumn = mock(Item.class);
		assertEquals(-1, sorter.compare(viewer, "a", "b"));
		assertEquals(0, sorter.compare(viewer, "a", "a"));
		assertEquals(1, sorter.compare(viewer, 2, 1));
	}

	@Test
	public void testCompareSortColumnLabelProvider() {
		ColumnViewer viewer = Mockito.mock(ColumnViewer.class);
		sorter.sortColumn = mock(Item.class);
		assertEquals(-1, sorter.compare(viewer, "a", "b"));
		assertEquals(0, sorter.compare(viewer, "a", "a"));
		assertEquals(1, sorter.compare(viewer, 2, 1));
	}

	@Test
	public void testCompareColumn() {
		assertEquals(-1, sorter.compare(null, "a", "b", 0));
	}

	@Test
	public void testCompareDefault() {
		ColumnViewer viewer = mock(ColumnViewer.class);
		assertEquals(-1, sorter.compareDefault(viewer, "a", "b"));
	}

	@Test
	public void testCompareDirection() {
		ColumnViewer viewer = mock(ColumnViewer.class);
		sorter.sortColumn = mock(Item.class);
		sorter.sortDirection = SWT.UP;
		assertEquals(-1, sorter.compare(viewer, "a", "b"));
		sorter.sortDirection = SWT.DOWN;
		assertEquals(1, sorter.compare(viewer, "a", "b"));
	}

}
