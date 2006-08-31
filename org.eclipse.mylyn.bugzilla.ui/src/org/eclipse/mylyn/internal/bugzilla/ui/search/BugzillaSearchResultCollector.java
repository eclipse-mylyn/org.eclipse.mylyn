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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaResultCollector;
import org.eclipse.mylar.internal.tasks.ui.search.RepositorySearchResult;
import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.TaskList;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.Match;

/**
 * Used for returning results from Eclipse Search view. Collects results of a
 * Bugzilla search and inserts them into the search results view.
 * 
 * TODO: unify BugzillaSearchResultCollector and BugzillaResultCollector
 * 
 * @author Rob Elves (modifications)
 */
public class BugzillaSearchResultCollector extends BugzillaResultCollector {

	private IBugzillaSearchOperation operation;
	
	public BugzillaSearchResultCollector(TaskList tasklist) {
		super(tasklist);
	}

	private RepositorySearchResult searchResult;

	public void aboutToStart(int startMatchCount) throws CoreException {
		super.aboutToStart(startMatchCount);
		NewSearchUI.activateSearchResultView();
		searchResult = (RepositorySearchResult) getOperation().getQuery().getSearchResult();		
	}

	public void done() {		
		super.done();
		searchResult = null;
	}

	@Override
	public void addMatch(AbstractQueryHit hit) {
		super.addMatch(hit);
		searchResult.addMatch(new Match(hit, 0, 0));		
	}
	
	public void setOperation(IBugzillaSearchOperation operation) {
		this.operation = operation;
	}

	public IBugzillaSearchOperation getOperation() {
		return operation;
	}

}

// /** Array of severities for a bug */
// private static final String[] severity = { "blo", "cri", "maj", "nor", "min",
// "tri", "enh" };
//
// /** Array of priorities for a bug */
// private static final String[] priority = { "P1", "P2", "P3", "P4", "P5", "--"
// };
//
// /** Array of possible states of a bug */
// private static final String[] state = { "UNCO", "NEW", "ASSI", "REOP",
// "RESO", "VERI", "CLOS" };
//
// /** Array of the possible resolutions of the bug */
// private static final String[] result = { "", "FIXE", "INVA", "WONT", "LATE",
// "REMI", "DUPL", "WORK" };

// /**
// * Returns a map where BugReport's attributes are entered into a Map using
// * the same key/value pairs as those created on a search hit marker.
// */
// public static Map<String, Object> getAttributeMap(RepositoryTaskData bug) {
// HashMap<String, Object> map = new HashMap<String, Object>();
// map.put(BugzillaUiPlugin.HIT_MARKER_ATTR_ID, new Integer(bug.getId()));
// map.put(BugzillaUiPlugin.HIT_MARKER_ATTR_REPOSITORY, bug.getRepositoryUrl());
// map.put(BugzillaUiPlugin.HIT_MARKER_ATTR_DESC, bug.getDescription());
// map.put(BugzillaUiPlugin.HIT_MARKER_ATTR_SEVERITY, mapValue(bug.getAttribute(
// BugzillaReportElement.BUG_SEVERITY.getKeyString()).getValue(), severity));
// map.put(BugzillaUiPlugin.HIT_MARKER_ATTR_PRIORITY,
// mapValue(bug.getAttribute(BugzillaReportElement.PRIORITY.getKeyString())
// .getValue(), priority));
// map.put(BugzillaUiPlugin.HIT_MARKER_ATTR_PLATFORM,
// bug.getAttribute("Hardware").getValue());
// map.put(BugzillaUiPlugin.HIT_MARKER_ATTR_STATE, mapValue(bug.getStatus(),
// state));
// map.put(BugzillaUiPlugin.HIT_MARKER_ATTR_RESULT,
// mapValue(bug.getResolution(), result));
// map.put(BugzillaUiPlugin.HIT_MARKER_ATTR_OWNER, bug.getAssignedTo());
// map.put(BugzillaUiPlugin.HIT_MARKER_ATTR_QUERY, "");
// return map;
// }
//
// /**
// * Get the map value for the given <code>String</code> value
// *
// * @param value
// * The value that we are trying to map
// * @param map
// * The map that we are using
// * @return The map value
// */
// private static Integer mapValue(String value, String[] map) {
// // go through each element in the map
// for (int i = 0; i < map.length; i++) {
// // if we found the value, return the position in the map
// if (map[i].equals(value)) {
// return new Integer(i);
// }
// }
//
// // return null if we didn't find anything
// return null;
// }

// /**
// * @see
// org.eclipse.mylar.internal.bugzilla.ui.search.IBugzillaSearchResultCollector#accept(org.eclipse.mylar.internal.bugzilla.ui.search.BugzillaSearchHit)
// */
// public void accept(BugzillaQueryHit queryHit) throws CoreException {
// // String description = hit.getId() + ": " + hit.getDescription();
// //
// // BugzillaQueryHit queryHit = new BugzillaQueryHit(description,
// hit.getPriority(), hit.getRepositoryUrl(), ""+hit
// // .getId(), null, hit.getState());
//
// ITask correspondingTask =
// TasksUiPlugin.getTaskListManager().getTaskList().getTask(
// queryHit.getHandleIdentifier());
// if (correspondingTask instanceof AbstractRepositoryTask) {
// queryHit.setCorrespondingTask((AbstractRepositoryTask) correspondingTask);
// }
//
// // searchResult.addMatch(new Match(queryHit, 0, 0));
//
// // increment the match count
// // matchCount++;
// //
// // if (!getProgressMonitor().isCanceled()) {
// // // if the operation is cancelled finish with whatever data was
// // // already found
// // getProgressMonitor().subTask(getFormattedMatchesString(matchCount));
// // getProgressMonitor().worked(1);
// // }
// }
