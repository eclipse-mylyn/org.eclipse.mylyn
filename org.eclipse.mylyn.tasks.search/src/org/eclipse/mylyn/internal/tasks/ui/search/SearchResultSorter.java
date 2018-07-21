/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Frank Becker - fix for bug 216150
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.search;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskComparator;
import org.eclipse.mylyn.tasks.core.ITask;

/**
 * Sorts search results.
 * 
 * @see TaskComparator
 * @author Rob Elves
 * @author Frank Becker
 */
public class SearchResultSorter extends ViewerSorter {

	private final TaskComparator taskComparator;

	public SearchResultSorter() {
		taskComparator = new TaskComparator();
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if (e1 instanceof ITask && e2 instanceof ITask) {
			ITask entry1 = (ITask) e1;
			ITask entry2 = (ITask) e2;
			return taskComparator.compare(entry1, entry2);
		} else {
			return super.compare(viewer, e1, e2);
		}
	}

	public TaskComparator getTaskComparator() {
		return taskComparator;
	}

}
