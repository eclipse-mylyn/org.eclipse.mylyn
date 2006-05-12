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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Collector for the bugzilla search results
 * 
 * @author Shawn Minto
 */
public class BugzillaResultCollector implements IBugzillaSearchResultCollector {
	/** A list of all of the search results found */
	private List<BugzillaSearchHit> results = new ArrayList<BugzillaSearchHit>();

	/** The progress monitor for the search operation */
	private IProgressMonitor monitor;

	/** The number of matches found */
	private int matchCount;

	/** The bugzilla search operation */
	private IBugzillaSearchOperation operation;

	/** The string to display to the user while querying */
	private static final String STARTING = "querying the server";

	/** The string to display to the user when the query is done */
	private static final String DONE = "done";

	/** The string to display when there is one match from the search */
	private static final String MATCH = "Bugzilla Mylar search - 1 match";

	/** The string to display when there is more than one match from the search */
	private static final String MATCHES = "Bugzilla Mylar search - {0} matches";

	/**
	 * @see org.eclipse.mylar.internal.bugzilla.ui.search.IBugzillaSearchResultCollector#aboutToStart()
	 */
	public void aboutToStart(int startMatchCount) throws CoreException {
		// initiailize the number of matches
		matchCount = startMatchCount;

		// set the progress monitor to say that we are querying the server
		monitor.setTaskName(STARTING);
	}

	/**
	 * @see org.eclipse.mylar.internal.bugzilla.ui.search.IBugzillaSearchResultCollector#accept(org.eclipse.mylar.internal.bugzilla.ui.search.BugzillaSearchHit)
	 */
	public void accept(BugzillaSearchHit hit) throws CoreException {
		// add the result to the list of results
		results.add(hit);

		// increment the match count
		matchCount++;

		if (getProgressMonitor() != null) {
			if (!getProgressMonitor().isCanceled()) {
				// if the operation is cancelled finish with whatever data was
				// already found
				getProgressMonitor().subTask(getFormattedMatchesString(matchCount));
				getProgressMonitor().worked(1);
			}
		}
	}

	/**
	 * @see org.eclipse.mylar.internal.bugzilla.ui.search.IBugzillaSearchResultCollector#done()
	 */
	public void done() {
		if (getProgressMonitor() != null) {
			if (!monitor.isCanceled()) {
				// if the operation is cancelled, finish with the data that we
				// already have
				String matchesString = getFormattedMatchesString(matchCount);
				monitor.setTaskName(MessageFormat.format(DONE, new Object[] { matchesString }));
			}
		}

		monitor = null;
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
	 * @see org.eclipse.mylar.internal.bugzilla.ui.search.IBugzillaSearchResultCollector#setOperation(org.eclipse.mylar.internal.bugzilla.core.search.BugzillaSearchOperation)
	 */
	public void setOperation(IBugzillaSearchOperation operation) {
		this.operation = operation;
	}

	/**
	 * @see org.eclipse.mylar.internal.bugzilla.ui.search.IBugzillaSearchResultCollector#getOperation()
	 */
	public IBugzillaSearchOperation getOperation() {
		return operation;
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
		String name = "";
		if (operation.getName() != null)
			name = " - " + operation.getName();
		if (count == 1)
			return MATCH + name;

		// format the matches string and return it
		Object[] messageFormatArgs = { new Integer(count) };
		return MessageFormat.format(MATCHES + name, messageFormatArgs);
	}

	/**
	 * Get the list of results
	 * 
	 * @return A List of BugzillaSearchHit
	 */
	public List<BugzillaSearchHit> getResults() {
		return results;
	}

	/**
	 * Get the number of matches from the operation
	 * 
	 * @return Returns the matchCount.
	 */
	public int getMatchCount() {
		return matchCount;
	}

	/**
	 * Set the starting number of matches for the search operation
	 * 
	 * @param matchCount
	 *            The matchCount to set.
	 */
	public void setMatchCount(int matchCount) {
		this.matchCount = matchCount;
	}
}