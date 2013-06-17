/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import junit.framework.TestCase;

import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylyn.internal.tasks.core.IRepositoryConstants;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskRepositoriesViewSorter;
import org.eclipse.mylyn.tasks.core.TaskRepository;

public class TaskRepositoryViewSorterTest extends TestCase {

	public void testCategorySorting() {
		ViewerSorter sorter = new TaskRepositoriesViewSorter();
		TaskRepository t1 = new TaskRepository("kind", "http://a");
		t1.setCategory(IRepositoryConstants.CATEGORY_OTHER);
		TaskRepository t2 = new TaskRepository("kind", "http://b");
		t2.setCategory(IRepositoryConstants.CATEGORY_OTHER);
		TaskRepository t3 = new TaskRepository("kind", "http://c");
		t3.setCategory(TaskRepository.CATEGORY_TASKS);

		assertTrue(sorter.compare(null, t1, t2) < 0);
		assertTrue(sorter.compare(null, t2, t3) > 0);
		assertTrue(sorter.compare(null, t3, t1) < 0);
	}
}
