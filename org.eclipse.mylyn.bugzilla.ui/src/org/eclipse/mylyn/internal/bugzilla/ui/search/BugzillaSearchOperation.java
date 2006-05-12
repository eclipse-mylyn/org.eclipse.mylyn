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

import java.net.Proxy;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;

/**
 * An operation to perform Bugzilla search query.
 * 
 * @author Mik Kersten (hardening of prototype
 */
public class BugzillaSearchOperation implements IBugzillaSearchOperation {
	private String queryUrl;

	/** The bugzilla collector for the search */
	private IBugzillaSearchResultCollector collector;

	/** The bugzilla search query */
	private BugzillaSearchQuery query;

	/** The status of the search operation */
	private IStatus status;

	/** The LoginException that was thrown when trying to do the search */
	private LoginException loginException = null;

	private int maxHits;

	private TaskRepository repository;

	private Proxy proxySettings;
	
	public BugzillaSearchOperation(TaskRepository repository, String queryUrl, Proxy proxySettings,
			IBugzillaSearchResultCollector collector, String maxHits) {
		this.repository = repository;
		this.queryUrl = queryUrl;
		this.collector = collector;
		this.proxySettings = proxySettings;
		collector.setOperation(this);
		try {
			this.maxHits = Integer.parseInt(maxHits);
		} catch (Exception e) {
			this.maxHits = -1;
		}
	}
	
	public void run(IProgressMonitor monitor) {
		// set the progress monitor for the search collector and start the
		// search
		collector.setProgressMonitor(monitor);
		BugzillaSearchEngine engine = new BugzillaSearchEngine(repository, queryUrl, proxySettings);
		try {
			status = engine.search(collector, 0, maxHits);
		} catch (LoginException e) {
			// save this exception to throw later
			this.loginException = e;
		}
	}

	/**
	 * @see org.eclipse.mylar.internal.bugzilla.ui.search.IBugzillaSearchOperation#getStatus()
	 */
	public IStatus getStatus() throws LoginException {
		// if a LoginException was thrown while trying to search, throw this
		if (loginException == null)
			return status;
		else
			throw loginException;
	}

	/**
	 * @see org.eclipse.mylar.internal.bugzilla.core.search.IBugzillaSearchOperation#getQuery()
	 */
	public BugzillaSearchQuery getQuery() {
		return query;
	}

	/**
	 * @see org.eclipse.mylar.internal.bugzilla.core.search.IBugzillaSearchOperation#setQuery(org.eclipse.mylar.internal.bugzilla.ui.search.BugzillaSearchQuery)
	 */
	public void setQuery(BugzillaSearchQuery newQuery) {
		this.query = newQuery;
	}

	public String getName() {
		return null;
	}
}
