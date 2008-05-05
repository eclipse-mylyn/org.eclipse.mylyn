/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.search;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylyn.tasks.core.ITask;

/**
 * Sorts results of Bugzilla search by bug priority.
 * 
 * @author Rob Elves (modifications)
 */
public class SearchResultSorterPriority extends ViewerSorter {

	/**
	 * Returns a negative, zero, or positive number depending on whether the first bug's priority goes before, is the
	 * same as, or goes after the second element's priority.
	 * <p>
	 * 
	 * @see org.eclipse.jface.viewers.ViewerSorter#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		try {
			ITask hit1 = (ITask) e1;
			ITask hit2 = (ITask) e2;
			return hit1.getPriority().compareTo(hit2.getPriority());
		} catch (Exception ignored) {
			// do nothing
		}
		// if that didn't work, use the default compare method
		return super.compare(viewer, e1, e2);
	}

	/**
	 * Returns the category of the given element. The category is a number used to allocate elements to bins; the bins
	 * are arranged in ascending numeric order. The elements within a bin are arranged via a second level sort
	 * criterion.
	 * <p>
	 * 
	 * @see org.eclipse.jface.viewers.ViewerSorter#category(Object)
	 */
	@Override
	public int category(Object element) {
		try {
			ITask hit = (ITask) element;
			return Integer.parseInt(hit.getTaskId());
		} catch (Exception ignored) {
			// ignore if there is a problem
		}
		// if that didn't work, use the default category method
		return super.category(element);
	}
}
