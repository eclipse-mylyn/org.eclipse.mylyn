/*******************************************************************************
 * Copyright (c) 2003 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.bugzilla.core.search;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * Interface for the bugzilla search operation
 * @author Shawn Minto
 */
public interface IBugzillaSearchOperation extends IRunnableWithProgress
{
	/**
	 * Execute the search
	 * @see org.eclipse.ui.actions.WorkspaceModifyOperation#execute(IProgressMonitor)
	 */
	public void execute(IProgressMonitor monitor);

	/**
	 * Get the status of the search operation
	 * @return The status of the search operation
	 * @throws LoginException
	 */
	public IStatus getStatus() throws LoginException;
	
	/**
	 * Get the image descriptor for the operation
	 * @return <code>null</code>
	 */
	public ImageDescriptor getImageDescriptor();
	
	/**
	 * Get the bugzilla search query
	 * @return The bugzilla search query
	 */
	public BugzillaSearchQuery getQuery();
	
	/**
	 * Sets the bugzilla search query
	 * @param newQuery The bugzilla search query to be set
	 */
	public void setQuery(BugzillaSearchQuery newQuery);
}
