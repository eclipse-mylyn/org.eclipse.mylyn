/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui.search;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylyn.internal.bugzilla.ui.BugzillaUiPlugin;

/**
 * Sorts results of Bugzilla search by bug severity.
 */
public class BugzillaSeveritySearchSorter extends ViewerSorter {

	/**
	 * Returns a negative, zero, or positive number depending on whether the first bug's severity goes before, is the
	 * same as, or goes after the second element's severity.
	 * <p>
	 * 
	 * @see org.eclipse.jface.viewers.ViewerSorter#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		try {
			// cast the object and get its severity
			IMarker entry1 = (IMarker) e1;
			Integer severity1 = (Integer) entry1.getAttribute(BugzillaUiPlugin.HIT_MARKER_ATTR_SEVERITY);

			// cast the other object and get its severity
			IMarker entry2 = (IMarker) e2;
			Integer severity2 = (Integer) entry2.getAttribute(BugzillaUiPlugin.HIT_MARKER_ATTR_SEVERITY);

			// if neither is null, compare the bugs severities
			if (severity1 != null && severity2 != null) {
				return severity1.compareTo(severity2);
			}
		} catch (Exception ignored) {
			// ignore if there is a problem
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
			IMarker marker = (IMarker) element;

			// return the bugs id
			if (marker.getType().equals(BugzillaUiPlugin.HIT_MARKER_ID)) {
				return ((Integer) marker.getAttribute(BugzillaUiPlugin.HIT_MARKER_ATTR_ID)).intValue();
			}
		} catch (Exception ignored) {
			// ignore if there is a problem
		}

		// if that didn't work, use the default category method
		return super.category(element);
	}
}
