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
package org.eclipse.mylar.internal.trac.ui.search;

import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;

/**
 * Collects results of a search.
 */
public abstract class AbstractQueryHitCollector implements IQueryHitCollector {

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

	public void aboutToStart(int startMatchCount) throws CoreException {
		matchCount = startMatchCount;

		// set the progress monitor to say that we are querying the server
		monitor.setTaskName(STARTING);
	}

	public void accept(AbstractQueryHit hit) throws CoreException {
		ITask correspondingTask = TasksUiPlugin.getTaskListManager().getTaskList().getTask(
				hit.getHandleIdentifier());
		if (correspondingTask instanceof AbstractRepositoryTask) {
			hit.setCorrespondingTask((AbstractRepositoryTask) correspondingTask);
		}

		addMatch(hit);
		
		// increment the match count
		matchCount++;

		if (!getProgressMonitor().isCanceled()) {
			// if the operation is cancelled finish with whatever data was
			// already found
			getProgressMonitor().subTask(getFormattedMatchesString(matchCount));
			getProgressMonitor().worked(1);
		}
	}

	public abstract void addMatch(AbstractQueryHit hit);

	public void done() {
		if (!monitor.isCanceled()) {
			// if the operation is cancelled, finish with the data that we
			// already have
			String matchesString = getFormattedMatchesString(matchCount);
			monitor.setTaskName(MessageFormat.format(DONE, new Object[] { matchesString }));
		}

		// Cut no longer used references because the collector might be re-used
		monitor = null;
	}

	/**
	 * Get the string specifying the number of matches found
	 * 
	 * @param count
	 *            The number of matches found
	 * @return The <code>String</code> specifying the number of matches found
	 */
	protected String getFormattedMatchesString(int count) {
		// if only 1 match, return the singular match string
		if (count == 1)
			return MATCH;

		// format the matches string and return it
		Object[] messageFormatArgs = { new Integer(count) };
		return MessageFormat.format(MATCHES, messageFormatArgs);
	}

	public IProgressMonitor getProgressMonitor() {
		return monitor;
	}

	public void setProgressMonitor(IProgressMonitor monitor) {
		this.monitor = monitor;
	}

}
