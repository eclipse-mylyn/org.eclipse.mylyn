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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylar.internal.bugzilla.ui.search.IBugzillaSearchOperation;

/**
 * Interface for the bugzilla search result collector.
 * 
 * @author Shawn Minto
 */
public interface IBugzillaSearchResultCollector {

	/**
	 * Called before the actual search starts
	 * 
	 * @param startCount -
	 *            The starting count for the number of matches
	 * @throws CoreException
	 */
	public void aboutToStart(int startCount) throws CoreException;

	/**
	 * Accept a search hit and add it as a match and set the markers
	 * 
	 * @param hit
	 *            The search hit that was a match
	 * @throws CoreException
	 */
	public void accept(BugzillaSearchHit hit) throws CoreException;

	/**
	 * Called when the search has ended.
	 */
	public void done();

	/**
	 * Get the progress monitor for the search
	 * 
	 * @return The progress monitor
	 */
	public IProgressMonitor getProgressMonitor();

	/**
	 * Set the progress monitor
	 * 
	 * @param monitor
	 *            The progress monitor the search should use
	 */
	public void setProgressMonitor(IProgressMonitor monitor);

	/**
	 * Set the current search operation
	 * 
	 * @param operation
	 *            The operation to set the search to
	 */
	public void setOperation(IBugzillaSearchOperation operation);

	/**
	 * Get the current operation
	 * 
	 * @return The current search operation
	 */
	public IBugzillaSearchOperation getOperation();
}
