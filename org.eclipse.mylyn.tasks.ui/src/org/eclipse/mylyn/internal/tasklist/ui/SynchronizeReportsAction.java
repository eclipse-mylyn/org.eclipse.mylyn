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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylar.internal.tasklist.AbstractRepositoryClient;
import org.eclipse.mylar.internal.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.internal.tasklist.IQueryHit;
import org.eclipse.mylar.internal.tasklist.AbstractRepositoryQuery;
import org.eclipse.mylar.internal.tasklist.ITask;
import org.eclipse.mylar.internal.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.internal.tasklist.TaskCategory;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * @author Ken Sueda and Mik Kersten
 */
public class SynchronizeReportsAction extends Action implements IViewActionDelegate {

	public static final String ID = "org.eclipse.mylar.tasklist.actions.refresh.bugzilla";

	private AbstractRepositoryQuery query = null;

	public SynchronizeReportsAction() {
		setText("Refresh Refresh");
		setToolTipText("Synchronize Bugzilla");
		setId(ID);
		setImageDescriptor(TaskListImages.REPOSITORY_SYNCHRONIZE);
	}

	public SynchronizeReportsAction(AbstractRepositoryQuery query) {
		this();
		this.query = query;
	}

	@Override
	public void run() {

//		boolean offline = MylarTaskListPlugin.getPrefs().getBoolean(TaskListPreferenceConstants.WORK_OFFLINE);
//		if (offline) {
//			MessageDialog.openInformation(null, IBugzillaConstants.TITLE_MESSAGE_DIALOG,
//					"Unable to refresh the query since you are currently offline");
//			return;
//		}

		if (query != null) {
//			synchronizeCategory(query);
			AbstractRepositoryClient client = MylarTaskListPlugin.getRepositoryManager().getRepositoryClient(query.getRepositoryKind());
			client.synchronize(query);
		} else if (TaskListView.getDefault() != null) {			
			ISelection selection = TaskListView.getDefault().getViewer().getSelection();
			for (Object obj : ((IStructuredSelection) selection).toList()) {
				if (obj instanceof AbstractRepositoryQuery) {
					AbstractRepositoryQuery repositoryQuery = (AbstractRepositoryQuery) obj;
					AbstractRepositoryClient client = MylarTaskListPlugin.getRepositoryManager().getRepositoryClient(repositoryQuery.getRepositoryKind());
					client.synchronize(repositoryQuery);
				} else if (obj instanceof TaskCategory) {
					TaskCategory cat = (TaskCategory) obj;
					for (ITask task : cat.getChildren()) {
						if (task instanceof AbstractRepositoryTask) {
							AbstractRepositoryClient client = MylarTaskListPlugin.getRepositoryManager().getRepositoryClient(task.getRepositoryKind());
							client.requestRefresh((AbstractRepositoryTask)task);
						}
					}
				} else if (obj instanceof AbstractRepositoryTask) {
					AbstractRepositoryTask bugTask = (AbstractRepositoryTask)obj;
					AbstractRepositoryClient client = MylarTaskListPlugin.getRepositoryManager().getRepositoryClient(bugTask.getRepositoryKind());
					client.requestRefresh(bugTask);
				} else if (obj instanceof IQueryHit) {
					IQueryHit hit = (IQueryHit) obj;
					if (hit.getCorrespondingTask() != null) {
						AbstractRepositoryClient client = MylarTaskListPlugin.getRepositoryManager().getRepositoryClient(hit.getCorrespondingTask().getRepositoryKind());
						client.requestRefresh(hit.getCorrespondingTask());
					}
				} 
			}
		}
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (TaskListView.getDefault() != null)
					TaskListView.getDefault().getViewer().refresh();
			}
		});
	}

//	private void synchronizeCategory(final AbstractRepositoryQuery query) {
//		Job job = new Job(LABEL_SYNCHRONIZE_JOB) {
//
//			@Override
//			protected IStatus run(IProgressMonitor monitor) {
//				query.refreshBugs();
//				for (IQueryHit hit : query.getHits()) {
//					if (hit.getCorrespondingTask() != null && hit instanceof IQueryHit) {
//						AbstractRepositoryClient client = MylarTaskListPlugin.getRepositoryManager().getRepositoryClient(hit.getCorrespondingTask().getRepositoryKind());
//						client.requestRefresh(hit.getCorrespondingTask());
//					}
//				}
//				return Status.OK_STATUS;
//			}
//
//		};
//
//		job.schedule();
//	}

	public void init(IViewPart view) {

	}

	public void run(IAction action) {
		run();
	}

	public void selectionChanged(IAction action, ISelection selection) {

	}
}
