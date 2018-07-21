/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.tests;

import junit.framework.TestCase;

import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.mylyn.internal.tasks.core.IRepositoryConstants;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskRepositoriesSorter;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Mik Kersten
 */
public class TaskRepositorySorterTest extends TestCase {

	private final static ViewerComparator sorter = new TaskRepositoriesSorter();

	public void testConnectorKindSorting() {
		TaskRepository t1 = new TaskRepository("t1", "http://a");
		TaskRepository t2 = new TaskRepository("t2", "http://a");
		TaskRepository t3 = new TaskRepository("t3", "http://a");

		// connector kind no longer affects sorting order
		assertTrue(sorter.compare(null, t1, t2) == 0);
		assertTrue(sorter.compare(null, t2, t3) == 0);
		assertTrue(sorter.compare(null, t3, t1) == 0);
	}

	public void testCategorySorting() {
		TaskRepository t1 = new TaskRepository("t1", "http://a");
		t1.setCategory(IRepositoryConstants.CATEGORY_OTHER);
		TaskRepository t2 = new TaskRepository("t2", "http://a");
		t2.setCategory(IRepositoryConstants.CATEGORY_OTHER);
		TaskRepository t3 = new TaskRepository("t3", "http://a");
		t3.setCategory(TaskRepository.CATEGORY_TASKS);

		// TaskRepositoriesSorter is not aware of categories, see TaskRepositoryViewSorterTest
		assertTrue(sorter.compare(null, t1, t2) == 0);
		assertTrue(sorter.compare(null, t2, t3) == 0);
		assertTrue(sorter.compare(null, t3, t1) == 0);
	}

	public void testUrlSorting() {
		TaskRepository t1 = new TaskRepository("t", "http://a");
		TaskRepository t2 = new TaskRepository("t", "http://b");
		TaskRepository t3 = new TaskRepository("t", "http://c");

		assertTrue(sorter.compare(null, t1, t2) < 0);
		assertTrue(sorter.compare(null, t2, t3) < 0);
		assertTrue(sorter.compare(null, t3, t1) > 0);
	}

	public void testUrlSortingWithEmptyLabels() {
		TaskRepository t1 = new TaskRepository("t", "http://a");
		t1.setProperty(IRepositoryConstants.PROPERTY_LABEL, "");
		TaskRepository t2 = new TaskRepository("t", "http://b");
		t2.setProperty(IRepositoryConstants.PROPERTY_LABEL, "");
		TaskRepository t3 = new TaskRepository("t", "http://c");
		t3.setProperty(IRepositoryConstants.PROPERTY_LABEL, "");

		assertTrue(sorter.compare(null, t1, t2) < 0);
		assertTrue(sorter.compare(null, t2, t3) < 0);
		assertTrue(sorter.compare(null, t3, t1) > 0);
	}

	public void testLabelSorting() {
		TaskRepository t1 = new TaskRepository("kind", "http://a");
		t1.setProperty(IRepositoryConstants.PROPERTY_LABEL, "a");
		TaskRepository t2 = new TaskRepository("kind", "http://a");
		t2.setProperty(IRepositoryConstants.PROPERTY_LABEL, "b");
		TaskRepository t3 = new TaskRepository("kind", "http://a");
		t3.setProperty(IRepositoryConstants.PROPERTY_LABEL, "c");

		assertTrue(sorter.compare(null, t1, t2) < 0);
		assertTrue(sorter.compare(null, t3, t1) > 0);
	}

	public void testLabelVsNoLabel() {
		TaskRepository t1 = new TaskRepository("kind", "http://a");
		t1.setProperty(IRepositoryConstants.PROPERTY_LABEL, "a");
		TaskRepository t2 = new TaskRepository("kind", "http://a");

		assertTrue(sorter.compare(null, t1, t2) < 0);
	}

	public void testLocalAlwaysFirst() {
		TaskRepository t1 = new TaskRepository("kind", "http://a");
		TaskRepository t2 = new TaskRepository("kind", "http://a");
		t2.setProperty(IRepositoryConstants.PROPERTY_LABEL, LocalRepositoryConnector.REPOSITORY_LABEL);

		assertTrue(sorter.compare(null, t1, t2) > 0);
	}

	public void testCaseInsensitiveOrdering() {
		TaskRepository t1 = new TaskRepository("kind", "http://a");
		t1.setProperty(IRepositoryConstants.PROPERTY_LABEL, "B");
		TaskRepository t2 = new TaskRepository("kind", "http://a");
		t2.setProperty(IRepositoryConstants.PROPERTY_LABEL, "a");

		assertTrue(sorter.compare(null, t1, t2) > 0);
	}

	public void testNullRepositoryUrl() {
		TaskRepository t1 = new TaskRepository("kind", "http://a");
		t1.setProperty(IRepositoryConstants.PROPERTY_URL, null);
		TaskRepository t2 = new TaskRepository("kind", "http://a");

		assertTrue(sorter.compare(null, t1, t2) > 0);
	}

}
