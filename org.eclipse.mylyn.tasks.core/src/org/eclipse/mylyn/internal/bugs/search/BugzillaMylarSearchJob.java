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

package org.eclipse.mylar.internal.bugs.search;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.internal.bugs.BugzillaSearchManager;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.ui.PlatformUI;

/**
 * The bugzilla search job used to search a bugzilla site
 * 
 * @author Shawn Minto
 * @author Mik Kersten
 */
public class BugzillaMylarSearchJob extends Job {

	/** The search operation used to perform the query */
	private BugzillaMylarSearchOperation operation;

	/**
	 * Constructor
	 * 
	 * @param name
	 *            Job name
	 * @param operation
	 *            The operation to perform the search query
	 */
	public BugzillaMylarSearchJob(String name, BugzillaMylarSearchOperation operation) {
		super(name);
		this.operation = operation;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		final IStatus[] status = new IStatus[1];

		try {
			// execute the search operation
			operation.execute(monitor);

			// get the status of the search operation
			status[0] = operation.getStatus();

			// determine if there was an error, if it was cancelled, or if it is
			// ok
			if (status[0] == null) {

			} else if (status[0].getCode() == IStatus.CANCEL) {
				// it was cancelled, so just return
				status[0] = Status.OK_STATUS;

				// make sure that we know this job is not running anymore
				BugzillaSearchManager.removeSearchJob(operation.getSearchMember().getHandleIdentifier() + " "
						+ operation.getScope());// runningJobs.remove(operation.getSearchMember());
				return status[0];
			} else if (!status[0].isOK()) {
				// there was an error, so display an error message
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						ErrorDialog.openError(null, "Bugzilla Search Error", null, status[0]);
					}
				});
				status[0] = Status.OK_STATUS;

				// make sure we know that this job is not running anymore
				BugzillaSearchManager.removeSearchJob(operation.getSearchMember().getHandleIdentifier() + " "
						+ operation.getScope());// runningJobs.remove(operation.getSearchMember());
				return status[0];
			}
		} catch (LoginException e) {
			// we had a problem while searching that seems like a login info
			// problem
			// thrown in BugzillaSearchOperation
			MessageDialog
					.openError(
							null,
							"Login Error",
							"Bugzilla could not log you in to get the information you requested since login name or password is incorrect.\nPlease check your settings in the bugzilla preferences. ");
			BugzillaPlugin.log(new Status(IStatus.ERROR, BugzillaUiPlugin.PLUGIN_ID, IStatus.OK, "", e));
		} finally {
			// make sure that we know that this job is not running anymore
			BugzillaSearchManager.removeSearchJob(operation.getSearchMember().getHandleIdentifier() + " "
					+ operation.getScope());// .runningJobs.remove(operation.getSearchMember());
		}

		return status[0];
	}
}