/*******************************************************************************
 * Copyright (c) 2013, 2015 Ericsson AB and others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Description:
 *
 * This class implements the search to pre-filled the list of Gerrit
 * project locations.
 *
 * Contributors:
 *   Jacques Bouthillier - Created for Mylyn Review Gerrit Dashboard project
 *
 ******************************************************************************/
package org.eclipse.mylyn.gerrit.dashboard.ui.internal.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.gerrit.dashboard.GerritPlugin;
import org.eclipse.mylyn.gerrit.dashboard.ui.GerritUi;
import org.eclipse.mylyn.gerrit.dashboard.ui.internal.utils.GerritServerUtility;
import org.eclipse.mylyn.gerrit.dashboard.ui.internal.utils.UIConstants;
import org.eclipse.mylyn.gerrit.dashboard.ui.views.GerritTableView;
import org.eclipse.mylyn.internal.gerrit.core.GerritQuery;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.handlers.IHandlerService;

/**
 * @author Jacques Bouthillier
 */
public class SelectReviewSiteHandler extends AbstractHandler {

	// ------------------------------------------------------------------------
	// Variables
	// ------------------------------------------------------------------------

	private GerritServerUtility fServerUtil = null;

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * Method execute.
	 *
	 * @param aEvent
	 *            ExecutionEvent
	 * @return Object
	 * @see org.eclipse.core.commands.IHandler#execute(ExecutionEvent)
	 */
	public Object execute(final ExecutionEvent aEvent) {

		GerritPlugin.Ftracer.traceInfo("Collecting the gerrit review locations"); //$NON-NLS-1$

		// Open the review table first;
		final GerritTableView reviewTableView = GerritTableView.getActiveView(true);

		reviewTableView.openView();

		final Job job = new Job(Messages.SelectReviewSiteHandler_searchCommand) {

			@Override
			public boolean belongsTo(Object aFamily) {
				return Messages.SelectReviewSiteHandler_dashboardUiJob.equals(aFamily);

			}

			@Override
			public IStatus run(IProgressMonitor aMonitor) {
				aMonitor.beginTask(Messages.SelectReviewSiteHandler_searchCommand, IProgressMonitor.UNKNOWN);

				//Map the Gerrit server
				fServerUtil = GerritServerUtility.getInstance();

				String serverToUsed = fServerUtil.getLastSavedGerritServer();
				if (serverToUsed != null) {
					//Initiate the request for the list of reviews with a default query
					reviewTableView.processCommands(GerritQuery.MY_WATCHED_CHANGES);

				} else {
					IWorkbench workbench = GerritUi.getDefault().getWorkbench();
					IHandlerService handlerService = (IHandlerService) workbench.getService(IHandlerService.class);
					try {
						handlerService.executeCommand(UIConstants.ADD_GERRIT_SITE_COMMAND_ID, null);
					} catch (Exception ex) {
						GerritPlugin.Ftracer
								.traceError(NLS.bind(Messages.SelectReviewSiteHandler_exception, ex.toString()));
//					      GerritUi.getDefault().logError("Exception: ", ex);
						//  throw new RuntimeException("org.eclipse.mylyn.gerrit.dashboard.ui.internal.commands.AddGerritSite not found");

					}
				}

				aMonitor.done();
				return Status.OK_STATUS;
			}

		};
		job.setUser(true);
		job.schedule();
		return null;
	}
}
