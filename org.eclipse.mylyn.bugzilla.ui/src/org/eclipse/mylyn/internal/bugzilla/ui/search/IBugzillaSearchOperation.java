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

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * Interface for the bugzilla search operation
 * 
 * @author Shawn Minto
 */
public interface IBugzillaSearchOperation extends IRunnableWithProgress {

	/**
	 * Get the status of the search operation
	 * 
	 * @return The status of the search operation
	 * @throws LoginException
	 */
	public IStatus getStatus() throws LoginException;

	/**
	 * Get the bugzilla search query
	 * 
	 * @return The bugzilla search query
	 */
	public BugzillaSearchQuery getQuery();

	/**
	 * Sets the bugzilla search query
	 * 
	 * @param newQuery
	 *            The bugzilla search query to be set
	 */
	public void setQuery(BugzillaSearchQuery newQuery);

	public String getName();
}
