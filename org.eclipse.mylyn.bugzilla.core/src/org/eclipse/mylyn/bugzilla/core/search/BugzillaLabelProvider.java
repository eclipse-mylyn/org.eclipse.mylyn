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

package org.eclipse.mylar.bugzilla.core.search;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.mylar.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.bugzilla.core.internal.HtmlStreamTokenizer;

/**
 * Label provider for Bugzilla search items.
 */
public class BugzillaLabelProvider extends LabelProvider {
	/** A list of the default severity labels */
	private static final String[] severityLabel = { "blocker", "critical", "major", "normal", "minor", "trivial",
			"enhancement" };

	/** A list of the default priority labels */
	private static final String[] priorityLabel = { "P1", "P2", "P3", "P4", "P5", "--" };

	/** A list of the default state labels */
	private static final String[] stateLabel = { "Unconfirmed", "New", "Assigned", "Reopened", "Resolved", "Verified",
			"Closed" };

	/** A list of the default result labels */
	private static final String[] resultLabel = { "", "fixed", "invalid", "wont fix", "later", "remind", "duplicate",
			"works for me" };

	/**
	 * Returns the text for the label of the given element.
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof IMarker) {

			try {
				IMarker marker = (IMarker) element;

				// get the severity of the bug
				String severity = severityLabel[((Integer) marker
						.getAttribute(IBugzillaConstants.HIT_MARKER_ATTR_SEVERITY)).intValue()];

				// get the priority of the bug
				String priority = priorityLabel[((Integer) marker
						.getAttribute(IBugzillaConstants.HIT_MARKER_ATTR_PRIORITY)).intValue()];

				// get the state of the bug
				String state = stateLabel[((Integer) marker.getAttribute(IBugzillaConstants.HIT_MARKER_ATTR_STATE))
						.intValue()];

				// get the resolution of the bug
				String result = resultLabel[((Integer) marker.getAttribute(IBugzillaConstants.HIT_MARKER_ATTR_RESULT))
						.intValue()];

				// return a string containing the information about the bug to
				// be displayed
				// in the searh window
				String assignedTo = HtmlStreamTokenizer.unescape(marker.getAttribute(
						IBugzillaConstants.HIT_MARKER_ATTR_OWNER).toString());
				String description = HtmlStreamTokenizer.unescape(marker.getAttribute(
						IBugzillaConstants.HIT_MARKER_ATTR_DESC).toString());
				return "Bug " + marker.getAttribute(IBugzillaConstants.HIT_MARKER_ATTR_ID) + " (" + severity + " - "
						+ priority + " - " + state + (result.length() > 0 ? " " + result : "") + ") " + " - "
						+ description + " (" + assignedTo + ") ";
			} catch (Exception ignored) {
				// ignore if there is a problem
			}
		}

		// return an empty string if there is a problem
		return "";
	}
}
