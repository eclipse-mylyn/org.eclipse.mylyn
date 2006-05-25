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

package org.eclipse.mylar.internal.bugzilla.ui.search;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaQueryHit;

/**
 * Sorts results of Bugzilla search by bug priority.
 */
public class BugzillaPrioritySearchSorter extends ViewerSorter {

	/**
	 * Returns a negative, zero, or positive number depending on whether the
	 * first bug's priority goes before, is the same as, or goes after the
	 * second element's priority.
	 * <p>
	 * 
	 * @see org.eclipse.jface.viewers.ViewerSorter#compare(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		try {

			BugzillaQueryHit entry1 = (BugzillaQueryHit) e1;
			String[] priorityLevels = BugzillaUiPlugin.getQueryOptions(IBugzillaConstants.VALUES_PRIORITY, null, entry1
					.getRepositoryUrl());
			if (priorityLevels != null && priorityLevels.length > 0) {
				List<String> levels = Arrays.asList(priorityLevels);
				Integer pr1 = new Integer(levels.indexOf(entry1.getPriority()));

				BugzillaQueryHit entry2 = (BugzillaQueryHit) e2;
				Integer pr2 = new Integer(levels.indexOf(entry2.getPriority()));
				if (pr1 != null && pr2 != null) {
					return pr1.compareTo(pr2);
				}
			}
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
			BugzillaQueryHit hit = (BugzillaQueryHit) element;
			return hit.getId();

		} catch (Exception ignored) {
			// ignore if there is a problem
		}
		// if that didn't work, use the default category method
		return super.category(element);
	}
}
