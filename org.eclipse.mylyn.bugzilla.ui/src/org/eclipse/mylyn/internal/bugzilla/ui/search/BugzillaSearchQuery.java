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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylar.internal.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylar.internal.tasks.ui.search.AbstractRepositorySearchQuery;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.ui.PlatformUI;

/**
 * This class performs a search query on Bugzilla bug reports.
 * 
 * @author Rob Elves (modifications)
 */
public class BugzillaSearchQuery extends AbstractRepositorySearchQuery {

	private static final String MESSAGE_LOGIN_FAILURE = "Could not log you in to get the information you requested since login name or password is incorrect.\nPlease check settings.";

	/** The operation that performs the Bugzilla search query. */
	private IBugzillaSearchOperation operation;

	public BugzillaSearchQuery(IBugzillaSearchOperation operation) {
		this.operation = operation;
		operation.setQuery(this);
	}

	public IStatus run(IProgressMonitor monitor) throws OperationCanceledException {
		final IStatus[] status = new IStatus[1];
		final AbstractTextSearchResult textResult = (AbstractTextSearchResult) getSearchResult();
		textResult.removeAll();

		try {
			operation.run(monitor);

			status[0] = operation.getStatus();

			if (status[0].getCode() == IStatus.CANCEL) {
				status[0] = Status.OK_STATUS;
			} else if (!status[0].isOK()) {
				PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
					public void run() {
						MessageDialog.openError(null, "Repository Search Error", status[0].getMessage());
					}
				});
				status[0] = Status.OK_STATUS;
			}
		} catch (InvocationTargetException e) {
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
				public void run() {
					MessageDialog.openInformation(null, "Bugzilla Login Error", MESSAGE_LOGIN_FAILURE);
				}
			});
		} catch (InterruptedException e) {
			// ignore
		} catch (final LoginException e) {
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
				public void run() {
					MessageDialog.openInformation(null, "Bugzilla Login Error", MESSAGE_LOGIN_FAILURE);
					BugzillaCorePlugin.log(new Status(IStatus.ERROR, BugzillaUiPlugin.PLUGIN_ID, IStatus.OK, "", e));
				}
			});
		}

		return status[0];
	}
}
