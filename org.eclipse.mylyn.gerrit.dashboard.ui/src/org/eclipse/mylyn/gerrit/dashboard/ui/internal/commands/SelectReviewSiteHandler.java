/*******************************************************************************
 * Copyright (c) 2013 Ericsson AB and others.
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

import java.util.Map;
import java.util.Set;

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
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.handlers.IHandlerService;

/**
 * @author Jacques Bouthillier
 * @version $Revision: 1.0 $
 */
public class SelectReviewSiteHandler extends AbstractHandler {

	// ------------------------------------------------------------------------
	// Constants
	// ------------------------------------------------------------------------

	/**
	 * Field COMMAND_MESSAGE. (value is ""Opening Element..."")
	 */
	private static final String COMMAND_MESSAGE = "Search Gerrit locations ...";
	
	
	
	// ------------------------------------------------------------------------
	// Variables
	// ------------------------------------------------------------------------

	private GerritServerUtility fServerUtil = null;
	
	private Map<TaskRepository, String> fMapRepoServer = null;


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
		final GerritTableView reviewTableView = GerritTableView
				.getActiveView();
		
		reviewTableView.openView(); 

		final Job job = new Job(COMMAND_MESSAGE) {

			public String familyName = UIConstants.DASHBOARD_UI_JOB_FAMILY;

			@Override
			public boolean belongsTo(Object aFamily) {
				return familyName.equals(aFamily);
			}

			@Override
			public IStatus run(IProgressMonitor aMonitor) {
				aMonitor.beginTask(COMMAND_MESSAGE, IProgressMonitor.UNKNOWN);


				//Map the Gerrit server
				fServerUtil = GerritServerUtility.getInstance();
				
				//Debug purpose, see which project have a gerrit server
				fMapRepoServer = fServerUtil.getGerritMapping();
				if (!fMapRepoServer.isEmpty()) {
					Set<TaskRepository> mapSet = fMapRepoServer.keySet();
					GerritPlugin.Ftracer.traceInfo("-------------------");
					for (TaskRepository key: mapSet) {
					    GerritPlugin.Ftracer.traceInfo("Map Key repo name : " 
								+ key.getRepositoryLabel() 
								+ "\t URL: " 
								+ fMapRepoServer.get(key));
					}
				}
				//End Debug
				
				String serverToUsed = fServerUtil.getLastSavedGerritServer();
				if (serverToUsed!= null) {
					//Initiate the request for the list of reviews with a default query
					reviewTableView.processCommands(GerritQuery.MY_WATCHED_CHANGES);

				} else {
					//Need to open the Dialogue to fill a Gerrit server
				    GerritPlugin.Ftracer.traceInfo("Need to open the Dialogue to fill a gerrit server " );
					//Get the service
					IWorkbench workbench = GerritUi.getDefault().getWorkbench();
					IHandlerService handlerService = (IHandlerService) workbench.getService(IHandlerService.class);
					try {
						
						  handlerService.executeCommand(UIConstants.ADD_GERRIT_SITE_COMMAND_ID, null);
					  } catch (Exception ex) {
					      GerritPlugin.Ftracer.traceError("Exception: " + ex.toString());
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
