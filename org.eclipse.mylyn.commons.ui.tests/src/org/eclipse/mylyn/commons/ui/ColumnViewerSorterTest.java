/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.ui;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.mylyn.commons.ui.AbstractColumnViewerSorter;
import org.eclipse.swt.widgets.Item;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author Steffen Pingel
 */
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

}
