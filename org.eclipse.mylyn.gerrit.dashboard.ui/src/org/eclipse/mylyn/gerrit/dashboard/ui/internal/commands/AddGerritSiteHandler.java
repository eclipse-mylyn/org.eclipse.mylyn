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
 * This class implements the "Add ..." a new Gerrit 
 * project locations.
 * 
 * Contributors:
 *   Jacques Bouthillier - Created for Mylyn Review Dashboard-Gerrit project
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
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.gerrit.dashboard.GerritPlugin;
import org.eclipse.mylyn.gerrit.dashboard.ui.internal.utils.GerritServerUtility;
import org.eclipse.mylyn.gerrit.dashboard.ui.internal.utils.UIConstants;
import org.eclipse.mylyn.gerrit.dashboard.ui.internal.utils.UIUtils;
import org.eclipse.mylyn.gerrit.dashboard.ui.views.GerritTableView;
import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.internal.gerrit.core.GerritQuery;
import org.eclipse.mylyn.internal.tasks.ui.wizards.EditRepositoryWizard;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.TaskRepositoryWizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Widget;


/**
 * @author Jacques Bouthillier
 * @version $Revision: 1.0 $
 */
public class AddGerritSiteHandler extends AbstractHandler {

	// ------------------------------------------------------------------------
	// Constants
	// ------------------------------------------------------------------------

	/**
	 * Field COMMAND_MESSAGE. (value is ""Add a Gerrit location ..."")
	 */
	private static final String COMMAND_MESSAGE = "Add a Gerrit location ...";

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

	    GerritPlugin.Ftracer.traceInfo("Create the Add button to search the Gerrit location " ); //$NON-NLS-1$
		String menuItemText = "";
		fServerUtil = GerritServerUtility.getInstance();
		Object obj = aEvent.getTrigger();
		Map<String, String> param = aEvent.getParameters();
		GerritTableView reviewTableView = GerritTableView
				.getActiveView();
		
		if (obj instanceof Event) {
			Event ev = (Event) obj;
			Widget objWidget = ev.widget;
			if (objWidget instanceof MenuItem) {
				MenuItem menuItem = (MenuItem) objWidget;
				menuItemText = menuItem.getText();
				GerritPlugin.Ftracer.traceInfo("MenuItem: " + menuItemText + "\t value: " +
						param.get(menuItemText) + " VS saved: "  + fServerUtil.getLastSavedGerritServer());
				fMapRepoServer = fServerUtil.getGerritMapping();
				String stURL = fServerUtil.getMenuSelectionURL(menuItemText);
				GerritPlugin.Ftracer.traceInfo("URL for the menuItemText: " + stURL);
				if (!fMapRepoServer.isEmpty()) {
					Set<TaskRepository> mapSet = fMapRepoServer.keySet();
					GerritPlugin.Ftracer.traceInfo("-------------------");
					for (TaskRepository key: mapSet) {
					    GerritPlugin.Ftracer.traceInfo("Map Key name: " + key.getRepositoryLabel() + "\t URL: " + fMapRepoServer.get(key));
					}
				}
				// Open the review table first;
				reviewTableView.openView(); 
				
				//Verify if we selected the "Add.." button or a pre=defined Gerrit
				if (stURL != null) {
					if (stURL.equals(fServerUtil.getLastSavedGerritServer())) {
					    GerritPlugin.Ftracer.traceInfo("LAST SAVED server is the SAME ");
						fServerUtil.getReviewListFromServer ();
						
						//Initiate the request for the list of reviews with a default query
						reviewTableView.processCommands(GerritQuery.MY_WATCHED_CHANGES);

						return Status.OK_STATUS; //For now , do not process the dialogue
					} else {
						//Store the new Gerrit server into a file
						fServerUtil.saveLastGerritServer(stURL);
						fServerUtil.getReviewListFromServer ();
						
						//Initiate the request for the list of reviews with a default query
						reviewTableView.processCommands(GerritQuery.MY_WATCHED_CHANGES);

						return Status.OK_STATUS; //For now , do not process the dialogue
					}
				}
			}
		}

		//Open the Dialogue to enter a new Gerrit URL
//		Object dialogObj = openDialogue ();
//		return dialogObj;
		
		//JB Dec 19, 2013
		//The previous line are blocked for now until we can add 
		//a new Gerrit Server from the "Add Gerrit Repository.." button
		UIUtils.showErrorDialog("Use Button [Task Repositories...] to define a new Gerrit Server", "Button [Add Gerrit Repository...] is not ready");
		return obj;
	}

	/**
	 * Initiate a JOB to open the Gerrit definition dialogue
	 * @return Object
	 */
	private Object openDialogue () {
		final Job job = new Job(COMMAND_MESSAGE) {

			public String familyName = UIConstants.DASHBOARD_UI_JOB_FAMILY;

			@Override
			public boolean belongsTo(Object aFamily) {
				return familyName.equals(aFamily);
			}

			@Override
			public IStatus run(final IProgressMonitor aMonitor) {
				aMonitor.beginTask(COMMAND_MESSAGE, IProgressMonitor.UNKNOWN);
						
				TaskRepository taskRepository = getTaskRepository(""); 
				
				GerritPlugin.Ftracer.traceInfo("repository:   " + taskRepository.getUrl()); //$NON-NLS-1$
				
				final EditRepositoryWizard wizard = new EditRepositoryWizard(taskRepository);
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
							WizardDialog dialog = new TaskRepositoryWizardDialog(wizard.getShell(), wizard);
							dialog.create();
							dialog.setBlockOnOpen(true);
							dialog.open();
					}
				});
				
				//When the wizard is closed
				taskRepository = wizard.getRepository();//Possibility the taskRepository has changed
				if (taskRepository.getUrl().isEmpty() || 
						taskRepository.getUrl().endsWith(UIConstants.DEFAULT_REPOSITORY)) {
					//User selected the Cancel button
				    GerritPlugin.Ftracer.traceInfo("AFTER: repository: CANCEL "  ); //$NON-NLS-1$
				} else {
				    GerritPlugin.Ftracer.traceInfo("AFTER: repository: :  FINISH " ); //$NON-NLS-1$		
					fServerUtil.saveLastGerritServer(taskRepository.getUrl());
					//Test if we already have the Gerrit server in our internal map
					TaskRepository taskRepositoryTmp = fServerUtil.getTaskRepo (taskRepository.getUrl());
					if (taskRepositoryTmp == null) {
						//Need to re-map our internal Gerrit Repo
						fServerUtil.getGerritMapping();
					}
					/*****************************************************/
					/*                                                   */
					/*    Now, we need to get the Gerrit repo data       */
					/*    and populate the list of Reviews               */
					/*                                                   */
					/*                                                   */
					/*****************************************************/
					fServerUtil.getReviewListFromServer ();
					GerritTableView reviewTableView = GerritTableView
							.getActiveView();

					//Set the table view with the last TaskRepo and the default query
					reviewTableView.processCommands(GerritQuery.MY_WATCHED_CHANGES);

				}
				
				GerritPlugin.Ftracer.traceInfo("AFTER: repository: :  " + taskRepository.getUrl() + 
						"\n\t repo: " + taskRepository.getRepositoryUrl() ); //$NON-NLS-1$
				

				aMonitor.done();
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();
		return null;	
	}
	
	
	/**
	 * Look at the current Gerrit repository and return a default value 
	 * 
	 * @param String default URL
	 * @return TaskRepository
	 */
	private TaskRepository getTaskRepository (String aUrl) {
		TaskRepository taskRepo = null;
		//Search for the current Gerrit connector
		taskRepo =  GerritServerUtility.getInstance().getTaskRepo(aUrl);
		
		if (taskRepo == null) {
			if (aUrl != null) {
				taskRepo = new TaskRepository (GerritConnector.CONNECTOR_KIND, aUrl);
			} else {
				//Create a default Task repo
				taskRepo = new TaskRepository (GerritConnector.CONNECTOR_KIND, UIConstants.DEFAULT_REPOSITORY);				
			}
			
		} else {
		    GerritPlugin.Ftracer.traceInfo("Repo already in list:  " + taskRepo.getRepositoryLabel()); 
			
		}
		return taskRepo;
	}
	
}
