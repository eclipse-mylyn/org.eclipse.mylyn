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

package org.eclipse.mylar.provisional.tasklist;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.internal.tasklist.ui.TaskListImages;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask.RepositoryTaskSyncState;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressConstants;

/**
 * @author Mik Kersten
 */
class SynchronizeQueryJob extends Job {

	private final AbstractRepositoryConnector connector;

	private static final String JOB_LABEL = "Synchronizing queries";
	
	private Set<AbstractRepositoryQuery> queries;
	
	private List<AbstractQueryHit> hits = new ArrayList<AbstractQueryHit>();

	public SynchronizeQueryJob(AbstractRepositoryConnector connector, Set<AbstractRepositoryQuery> queries) {
		super(JOB_LABEL + ": " + connector.getRepositoryType());
		this.connector = connector;
		this.queries = queries;			
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask(JOB_LABEL, queries.size());
		for (AbstractRepositoryQuery repositoryQuery : queries) {
			monitor.setTaskName("Synchronizing: " + repositoryQuery.getDescription());
			setProperty(IProgressConstants.ICON_PROPERTY, TaskListImages.REPOSITORY_SYNCHRONIZE);
			repositoryQuery.setCurrentlySynchronizing(true);
			TaskRepository repository = MylarTaskListPlugin.getRepositoryManager().getRepository(
					repositoryQuery.getRepositoryKind(), repositoryQuery.getRepositoryUrl());
			if (repository == null) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						MessageDialog
								.openInformation(Display.getDefault().getActiveShell(),
										MylarTaskListPlugin.TITLE_DIALOG,
										"No task repository associated with this query. Open the query to associate it with a repository.");
					}
				});
			}

			MultiStatus queryStatus = new MultiStatus(MylarTaskListPlugin.PLUGIN_ID, IStatus.OK, "Query result",
					null);

			hits = this.connector.performQuery(repositoryQuery, monitor, queryStatus);
			if (queryStatus.getChildren() != null && queryStatus.getChildren().length > 0) {
				if (queryStatus.getChildren()[0].getException() == null) {
					repositoryQuery.clearHits();
					for (AbstractQueryHit newHit : hits) {
						repositoryQuery.addHit(newHit);
						if (newHit.getCorrespondingTask() != null && newHit instanceof AbstractQueryHit && newHit.getCorrespondingTask().getSyncState() == RepositoryTaskSyncState.SYNCHRONIZED) {
							this.connector.requestRefresh(newHit.getCorrespondingTask());
						}
					}
				} else {
					repositoryQuery.setCurrentlySynchronizing(false);
					return queryStatus.getChildren()[0];
				}
			}
			
			repositoryQuery.setCurrentlySynchronizing(false);
			MylarTaskListPlugin.getTaskListManager().getTaskList().notifyQueryUpdated(repositoryQuery);
			monitor.worked(1);
		}
		return Status.OK_STATUS;
	}
}