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
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

/**
 * An operation to perform Bugzilla search query.
 */
public class BugzillaSearchOperation extends WorkspaceModifyOperation implements IBugzillaSearchOperation 
{
	/** The url of the bugzilla server */
	private String url;
	
	/** The bugzilla collector for the search */
	private IBugzillaSearchResultCollector collector;
	
	/** The bugzilla search query */
	private BugzillaSearchQuery query;
	
	/** The status of the search operation */
	private IStatus status;
	
	/** The LoginException that was thrown when trying to do the search */
	private LoginException loginException = null;
	
	/**
	 * Constructor
	 * @param url The url of the bugzilla server
	 * @param collector The bugzilla search collector to use for this search
	 */
	public BugzillaSearchOperation(String url, IBugzillaSearchResultCollector collector) 
	{
		this.url = url;
		this.collector = collector;
		collector.setOperation(this);
	}

	@Override
	public void execute(IProgressMonitor monitor) {
		// set the progress monitor for the search collector and start the search
		collector.setProgressMonitor(monitor);
		BugzillaSearchEngine engine = new BugzillaSearchEngine(url);
		try
		{
			status = engine.search(collector);
		}
		catch(LoginException e) {
			//save this exception to throw later
			this.loginException = e;
		}
	}
	
	/**
	 * @see org.eclipse.mylar.bugzilla.core.search.IBugzillaSearchOperation#getImageDescriptor()
	 */
    public ImageDescriptor getImageDescriptor() {
		return null;
	}
	
	/**
	 * @see org.eclipse.mylar.bugzilla.core.search.IBugzillaSearchOperation#getStatus()
	 */
    public IStatus getStatus() throws LoginException {
		// if a LoginException was thrown while trying to search, throw this
		if (loginException == null)
			return status;
		else
			throw loginException;
	}
	
	/**
	 * @see org.eclipse.mylar.bugzilla.core.search.IBugzillaSearchOperation#getQuery()
	 */
	public BugzillaSearchQuery getQuery() {
		return query;
	}

	/**
	 * @see org.eclipse.mylar.bugzilla.core.search.IBugzillaSearchOperation#setQuery(org.eclipse.mylar.bugzilla.core.search.BugzillaSearchQuery)
	 */
	public void setQuery(BugzillaSearchQuery newQuery) {
		this.query = newQuery;
	}

	public String getName() {
		return null;
	}

}
