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

import java.lang.reflect.InvocationTargetException;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.ui.PlatformUI;

/**
 * This class performs a search query on Bugzilla bug reports.
 * 
 * @see org.eclipse.search.ui.ISearchQuery
 */
public class BugzillaSearchQuery implements ISearchQuery {

	private static final String MESSAGE_LOGIN_FAILURE = "Bugzilla could not log you in to get the information you requested since login name or password is incorrect.\nPlease check your settings in the bugzilla preferences. ";

	/** The collection of all the bugzilla matches. */
	private BugzillaSearchResult bugResult;

	/** The operation that performs the Bugzilla search query. */
	private IBugzillaSearchOperation operation;

	/**
	 * Constructor
	 * 
	 * @param operation
	 *            The operation that performs the Bugzilla search query.
	 */
	public BugzillaSearchQuery(IBugzillaSearchOperation operation) {
		this.operation = operation;
		operation.setQuery(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.search.ui.ISearchQuery#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus run(IProgressMonitor monitor) throws OperationCanceledException {
		final IStatus[] status = new IStatus[1];
		final AbstractTextSearchResult textResult = (AbstractTextSearchResult) getSearchResult();
		textResult.removeAll(); // Remove any existing search results from the
		// view.

		// try {
		try {
			operation.run(monitor);

			status[0] = operation.getStatus();

			if (status[0].getCode() == IStatus.CANCEL) {
				status[0] = Status.OK_STATUS;
			} else if (!status[0].isOK()) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						ErrorDialog.openError(null, "Bugzilla Search Error", null, status[0]);
					}
				});
				status[0] = Status.OK_STATUS;
			}
		} catch (InvocationTargetException e) {
			MessageDialog
					.openInformation(
							null,
							"Bugzilla Login Error",
							MESSAGE_LOGIN_FAILURE);
			BugzillaPlugin.log(new Status(IStatus.ERROR, BugzillaUiPlugin.PLUGIN_ID, IStatus.OK, "", e));
		} catch (InterruptedException e) {
			// ignore
		} catch (LoginException e) {
			MessageDialog
					.openInformation(
							null,
							"Bugzilla Login Error",
							MESSAGE_LOGIN_FAILURE);
			BugzillaPlugin.log(new Status(IStatus.ERROR, BugzillaUiPlugin.PLUGIN_ID, IStatus.OK, "", e));
		}
		// } catch (LoginException e) {
		// // we had a problem while searching that seems like a login info
		// // problem thrown in BugzillaSearchOperation
		//			
		// }
		return status[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.search.ui.ISearchQuery#getLabel()
	 */
	public String getLabel() {
		return BugzillaSearchEngine.QUERYING_SERVER;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.search.ui.ISearchQuery#canRerun()
	 */
	public boolean canRerun() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.search.ui.ISearchQuery#canRunInBackground()
	 */
	public boolean canRunInBackground() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.search.ui.ISearchQuery#getSearchResult()
	 */
	public ISearchResult getSearchResult() {
		if (bugResult == null) {
			bugResult = new BugzillaSearchResult(this);
		}
		return bugResult;
	}

}
