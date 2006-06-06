/*******************************************************************************
 * Copyright (c) 2003 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.internal.bugzilla.ui.search;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaReportElement;
import org.eclipse.mylar.internal.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaQueryHit;
import org.eclipse.mylar.internal.tasklist.RepositoryTaskData;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.Match;

/**
 * Collects results of a Bugzilla search and inserts them into the search
 * results view.
 */
public class BugzillaSearchResultCollector implements IBugzillaSearchResultCollector {
	/** The bugzilla search operation */
	private IBugzillaSearchOperation operation;

	/** The collection of all the bugzilla matches */
	private BugzillaSearchResult searchResult;

	/** The progress monitor for the search operation */
	private IProgressMonitor monitor;

	/** The number of matches found */
	private int matchCount;

	/** The string to display to the user while querying */
	private static final String STARTING = "querying the server";

	/** The string to display to the user when we have 1 match */
	private static final String MATCH = "1 match";

	/** The string to display to the user when we have multiple or no matches */
	private static final String MATCHES = "{0} matches";

	/** The string to display to the user when the query is done */
	private static final String DONE = "done";

	// /** Resource used to create markers */
	// private static final IResource resource =
	// ResourcesPlugin.getWorkspace().getRoot();

	// TODO Find a better way to get the states and severity

	/** Array of severities for a bug */
	private static final String[] severity = { "blo", "cri", "maj", "nor", "min", "tri", "enh" };

	/** Array of priorities for a bug */
	private static final String[] priority = { "P1", "P2", "P3", "P4", "P5", "--" };

	/** Array of possible states of a bug */
	private static final String[] state = { "UNCO", "NEW", "ASSI", "REOP", "RESO", "VERI", "CLOS" };

	/** Array of the possible resolutions of the bug */
	private static final String[] result = { "", "FIXE", "INVA", "WONT", "LATE", "REMI", "DUPL", "WORK" };

	/**
	 * @see org.eclipse.mylar.internal.bugzilla.ui.search.IBugzillaSearchResultCollector#aboutToStart(int)
	 */
	public void aboutToStart(int startMatchCount) throws CoreException {
		NewSearchUI.activateSearchResultView();
		matchCount = startMatchCount;
		searchResult = (BugzillaSearchResult) getOperation().getQuery().getSearchResult();

		// set the progress monitor to say that we are querying the server
		monitor.setTaskName(STARTING);
	}

	/**
	 * @see org.eclipse.mylar.internal.bugzilla.ui.search.IBugzillaSearchResultCollector#accept(org.eclipse.mylar.internal.bugzilla.ui.search.BugzillaSearchHit)
	 */
	public void accept(BugzillaSearchHit hit) throws CoreException {
		String description = hit.getId() + ": " + hit.getDescription();

		BugzillaQueryHit queryHit = new BugzillaQueryHit(description, hit.getPriority(), hit.getRepositoryUrl(), hit
				.getId(), null, hit.getState());

		ITask correspondingTask = MylarTaskListPlugin.getTaskListManager().getTaskList().getTask(
				queryHit.getHandleIdentifier());
		if (correspondingTask instanceof AbstractRepositoryTask) {
			queryHit.setCorrespondingTask((AbstractRepositoryTask) correspondingTask);
		}

		searchResult.addMatch(new Match(queryHit, 0, 0));

		// increment the match count
		matchCount++;

		if (!getProgressMonitor().isCanceled()) {
			// if the operation is cancelled finish with whatever data was
			// already found
			getProgressMonitor().subTask(getFormattedMatchesString(matchCount));
			getProgressMonitor().worked(1);
		}
	}

	/**
	 * Returns a map where BugReport's attributes are entered into a Map using
	 * the same key/value pairs as those created on a search hit marker.
	 */
	public static Map<String, Object> getAttributeMap(RepositoryTaskData bug) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(BugzillaUiPlugin.HIT_MARKER_ATTR_ID, new Integer(bug.getId()));
		map.put(BugzillaUiPlugin.HIT_MARKER_ATTR_REPOSITORY, bug.getRepositoryUrl());
		map.put(BugzillaUiPlugin.HIT_MARKER_ATTR_DESC, bug.getDescription());
		map.put(BugzillaUiPlugin.HIT_MARKER_ATTR_SEVERITY, mapValue(bug.getAttribute(
				BugzillaReportElement.BUG_SEVERITY.getKeyString()).getValue(), severity));
		map.put(BugzillaUiPlugin.HIT_MARKER_ATTR_PRIORITY, mapValue(bug.getAttribute(BugzillaReportElement.PRIORITY.getKeyString())
				.getValue(), priority));
		map.put(BugzillaUiPlugin.HIT_MARKER_ATTR_PLATFORM, bug.getAttribute("Hardware").getValue());
		map.put(BugzillaUiPlugin.HIT_MARKER_ATTR_STATE, mapValue(bug.getStatus(), state));
		map.put(BugzillaUiPlugin.HIT_MARKER_ATTR_RESULT, mapValue(bug.getResolution(), result));
		map.put(BugzillaUiPlugin.HIT_MARKER_ATTR_OWNER, bug.getAssignedTo());
		map.put(BugzillaUiPlugin.HIT_MARKER_ATTR_QUERY, "");
		return map;
	}

	/**
	 * Get the map value for the given <code>String</code> value
	 * 
	 * @param value
	 *            The value that we are trying to map
	 * @param map
	 *            The map that we are using
	 * @return The map value
	 */
	private static Integer mapValue(String value, String[] map) {
		// go through each element in the map
		for (int i = 0; i < map.length; i++) {
			// if we found the value, return the position in the map
			if (map[i].equals(value)) {
				return new Integer(i);
			}
		}

		// return null if we didn't find anything
		return null;
	}

	/**
	 * @see org.eclipse.mylar.internal.bugzilla.ui.search.IBugzillaSearchResultCollector#done()
	 */
	public void done() {
		if (!monitor.isCanceled()) {
			// if the operation is cancelled, finish with the data that we
			// already have
			String matchesString = getFormattedMatchesString(matchCount);
			monitor.setTaskName(MessageFormat.format(DONE, new Object[] { matchesString }));
		}

		// Cut no longer used references because the collector might be re-used
		monitor = null;
		searchResult = null;
	}

	/**
	 * Get the string specifying the number of matches found
	 * 
	 * @param count
	 *            The number of matches found
	 * @return The <code>String</code> specifying the number of matches found
	 */
	private String getFormattedMatchesString(int count) {
		// if only 1 match, return the singular match string
		if (count == 1)
			return MATCH;

		// format the matches string and return it
		Object[] messageFormatArgs = { new Integer(count) };
		return MessageFormat.format(MATCHES, messageFormatArgs);
	}

	/**
	 * @see org.eclipse.mylar.internal.bugzilla.ui.search.IBugzillaSearchResultCollector#getProgressMonitor()
	 */
	public IProgressMonitor getProgressMonitor() {
		return monitor;
	}

	/**
	 * @see org.eclipse.mylar.internal.bugzilla.ui.search.IBugzillaSearchResultCollector#setProgressMonitor(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void setProgressMonitor(IProgressMonitor monitor) {
		this.monitor = monitor;
	}

	/**
	 * @see org.eclipse.mylar.internal.bugzilla.ui.search.IBugzillaSearchResultCollector#getOperation()
	 */
	public IBugzillaSearchOperation getOperation() {
		return operation;
	}

	/**
	 * @see org.eclipse.mylar.internal.bugzilla.ui.search.IBugzillaSearchResultCollector#setOperation(org.eclipse.mylar.internal.bugzilla.ui.search.IBugzillaSearchOperation)
	 */
	public void setOperation(IBugzillaSearchOperation operation) {
		this.operation = operation;
	}
}

// set the markers to have the bugs attributes
// IMarker marker =
// resource.createMarker(IBugzillaConstants.HIT_MARKER_ID);
//
// marker.setAttribute(IBugzillaConstants.HIT_MARKER_ATTR_REPOSITORY,
// hit.getRepository());
// marker.setAttribute(IBugzillaConstants.HIT_MARKER_ATTR_ID, new
// Integer(hit.getId()));
// marker.setAttribute(IBugzillaConstants.HIT_MARKER_ATTR_DESC,
// hit.getDescription());
// marker.setAttribute(IBugzillaConstants.HIT_MARKER_ATTR_SEVERITY, new
// Integer(IBugzillaConstants.BUGZILLA_REPORT_STATUS.valueOf(hit.getSeverity().trim().toUpperCase()).ordinal()));
// marker.setAttribute(IBugzillaConstants.HIT_MARKER_ATTR_PRIORITY, new
// Integer(BugzillaReportElement.valueOf(hit.getPriority().trim().toUpperCase()).ordinal()));
// marker.setAttribute(IBugzillaConstants.HIT_MARKER_ATTR_PLATFORM, new
// Integer(BugzillaReportElement.valueOf(hit.getPlatform().trim().toUpperCase()).ordinal()));
// marker.setAttribute(IBugzillaConstants.HIT_MARKER_ATTR_STATE, new
// Integer(BugzillaReportElement.valueOf(hit.getState().trim().toUpperCase()).ordinal()));
// marker.setAttribute(IBugzillaConstants.HIT_MARKER_ATTR_RESULT, new
// Integer(BugzillaReportElement.valueOf(hit.getResolution().trim().toUpperCase()).ordinal()));
// // marker.setAttribute(IBugzillaConstants.HIT_MARKER_ATTR_SEVERITY,
// mapValue(hit.getSeverity(), severity));
// // marker.setAttribute(IBugzillaConstants.HIT_MARKER_ATTR_PRIORITY,
// mapValue(hit.getPriority(), priority));
// // marker.setAttribute(IBugzillaConstants.HIT_MARKER_ATTR_PLATFORM,
// hit.getPlatform());
// // marker.setAttribute(IBugzillaConstants.HIT_MARKER_ATTR_STATE,
// mapValue(hit.getState(), state));
// // marker.setAttribute(IBugzillaConstants.HIT_MARKER_ATTR_RESULT,
// mapValue(hit.getResolution(), result));
// marker.setAttribute(IBugzillaConstants.HIT_MARKER_ATTR_OWNER,
// hit.getOwner());
// marker.setAttribute(IBugzillaConstants.HIT_MARKER_ATTR_QUERY,
// hit.getQuery());