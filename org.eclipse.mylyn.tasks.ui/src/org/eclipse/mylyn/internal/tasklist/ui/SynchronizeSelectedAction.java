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

package org.eclipse.mylar.internal.tasklist.ui;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;
import org.eclipse.mylar.provisional.tasklist.AbstractQueryHit;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryConnector;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryQuery;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskCategory;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

/**
 * @author Ken Sueda and Mik Kersten
 */
public class SynchronizeSelectedAction extends Action implements IViewActionDelegate {

	private AbstractRepositoryQuery query = null;

	private void checkSyncResult(final IJobChangeEvent event, final AbstractRepositoryQuery problemQuery) {
		if (event.getResult().getException() != null) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					MessageDialog.openError(Display.getDefault().getActiveShell(), MylarTaskListPlugin.TITLE_DIALOG, event
							.getResult().getMessage());
				}
			});
		}
	}

	public void run(IAction action) {
		if (query != null) {
			AbstractRepositoryConnector connector = MylarTaskListPlugin.getRepositoryManager().getRepositoryConnector(
					query.getRepositoryKind());
			if (connector != null)
				connector.synchronize(query, new JobChangeAdapter() {
					public void done(IJobChangeEvent event) {
						checkSyncResult(event, query);
					}
				});

		} else if (TaskListView.getFromActivePerspective() != null) {
			ISelection selection = TaskListView.getFromActivePerspective().getViewer().getSelection();
			for (Object obj : ((IStructuredSelection) selection).toList()) {
				if (obj instanceof AbstractRepositoryQuery) {
					final AbstractRepositoryQuery repositoryQuery = (AbstractRepositoryQuery) obj;
					AbstractRepositoryConnector client = MylarTaskListPlugin.getRepositoryManager()
							.getRepositoryConnector(repositoryQuery.getRepositoryKind());
					if (client != null)
						client.synchronize(repositoryQuery, new JobChangeAdapter() {
							public void done(IJobChangeEvent event) {
								checkSyncResult(event, repositoryQuery);
							}
						});
				} else if (obj instanceof TaskCategory) {
					TaskCategory cat = (TaskCategory) obj;
					for (ITask task : cat.getChildren()) {
						if (task instanceof AbstractRepositoryTask) {
							AbstractRepositoryConnector client = MylarTaskListPlugin.getRepositoryManager()
									.getRepositoryConnector(((AbstractRepositoryTask) task).getRepositoryKind());
							if (client != null)
								client.forceRefresh((AbstractRepositoryTask) task);
						}
					}
				} else if (obj instanceof AbstractRepositoryTask) {
					AbstractRepositoryTask bugTask = (AbstractRepositoryTask) obj;
					AbstractRepositoryConnector client = MylarTaskListPlugin.getRepositoryManager()
							.getRepositoryConnector(bugTask.getRepositoryKind());
					if (client != null)
						client.forceRefresh(bugTask);
				} else if (obj instanceof AbstractQueryHit) {
					AbstractQueryHit hit = (AbstractQueryHit) obj;
					if (hit.getCorrespondingTask() != null) {
						AbstractRepositoryConnector client = MylarTaskListPlugin.getRepositoryManager()
								.getRepositoryConnector(hit.getCorrespondingTask().getRepositoryKind());
						if (client != null)
							client.forceRefresh(hit.getCorrespondingTask());
					}
				}
			}
		}
		if (TaskListView.getFromActivePerspective() != null) {
			TaskListView.getFromActivePerspective().getViewer().refresh();
		}
	}

	public void init(IViewPart view) {
		// ignore
	}
	
	public void selectionChanged(IAction action, ISelection selection) {
		// ignore
	}
}
