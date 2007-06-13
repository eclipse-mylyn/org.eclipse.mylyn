/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.search;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskKeyComparator;
import org.eclipse.mylyn.tasks.core.AbstractTask;

/**
 * Sorts search results by summary.
 * 
 * @author Rob Elves
 */
public class SearchResultSorterDescription extends ViewerSorter {

	private TaskKeyComparator taskKeyComparator = new TaskKeyComparator();

	/**
	 * Returns a negative, zero, or positive number depending on whether the
	 * first bug's summary goes before, is the same as, or goes after the
	 * second element's summary.
	 * <p>
	 * 
	 * @see org.eclipse.jface.viewers.ViewerSorter#compare(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		try {

			AbstractTask entry1 = (AbstractTask) e1;
			AbstractTask entry2 = (AbstractTask) e2;
			// NOTE we just comparing ids here, once summary and taskId separated
			// they should have their own column/sorter.
			return taskKeyComparator.compare(entry1.getTaskId(), entry2.getTaskId());
			// return taskKeyComparator.compare(entry1.getDescription(),
			// entry2.getDescription());
		} catch (Exception ignored) {
			// do nothing
		}

		// if that didn't work, use the default compare method
		return super.compare(viewer, e1, e2);
	}

	/**
	 * Returns the category of the given element. The category is a number used
	 * to allocate elements to bins; the bins are arranged in ascending numeric
	 * order. The elements within a bin are arranged via a second level sort
	 * criterion.
	 * <p>
	 * 
	 * @see org.eclipse.jface.viewers.ViewerSorter#category(Object)
	 */
	@Override
	public int category(Object element) {
		try {
			AbstractTask hit = (AbstractTask) element;
			return Integer.parseInt(hit.getTaskId());
		} catch (Exception ignored) {
			// ignore if
		}
		// if that didn't work, use the default category method
		return super.category(element);
	}
}
