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

package org.eclipse.mylar.internal.tasklist.ui.actions;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.mylar.internal.tasklist.AbstractRepositoryClient;
import org.eclipse.mylar.internal.tasklist.IQueryHit;
import org.eclipse.mylar.internal.tasklist.IRepositoryQuery;
import org.eclipse.mylar.internal.tasklist.ITask;
import org.eclipse.mylar.internal.tasklist.ITaskCategory;
import org.eclipse.mylar.internal.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.internal.tasklist.ui.TaskListUiUtil;

/**
 * @author Mik Kersten
 */
public class OpenTaskListElementAction extends Action {

	public static final String ID = "org.eclipse.mylar.tasklist.actions.open";

	private final StructuredViewer viewer;

	/**
	 * @param view
	 */
	public OpenTaskListElementAction(StructuredViewer view) {
		this.viewer = view;
		setText("Open");
		setToolTipText("Open Task List Element");
		setId(ID);
	}

	@Override
	public void run() {
		ISelection selection = viewer.getSelection();
		Object element = ((IStructuredSelection) selection).getFirstElement();
		if (element instanceof ITask || element instanceof IQueryHit) {
			final ITask task;
			if (element instanceof IQueryHit) {
				task = ((IQueryHit) element).getOrCreateCorrespondingTask();
			} else {
				task = (ITask) element;
			}
			
			boolean forceUpdate = false;//element instanceof IQueryHit;

			final AbstractRepositoryClient client = MylarTaskListPlugin.getRepositoryManager().getRepositoryClient(
					task.getRepositoryKind());
			if (!task.isLocal() && client != null) {
				Job refreshJob = client.synchronize(task, forceUpdate, new IJobChangeListener() {

					public void done(IJobChangeEvent event) {
						TaskListUiUtil.openEditor(task);
					}

					public void aboutToRun(IJobChangeEvent event) {
						// ignore
					}

					public void awake(IJobChangeEvent event) {
						// ignore
					}

					public void running(IJobChangeEvent event) {
						// ignore
					}

					public void scheduled(IJobChangeEvent event) {
						// ignore
					}

					public void sleeping(IJobChangeEvent event) {
						// ignore
					}
				}); 
				if (refreshJob == null) {
					TaskListUiUtil.openEditor(task);
				} 
			} else {
				TaskListUiUtil.openEditor(task);
			}
		} else if (element instanceof ITaskCategory) {
			TaskListUiUtil.openEditor((ITaskCategory) element);
		} else if (element instanceof IRepositoryQuery) {
			IRepositoryQuery query = (IRepositoryQuery) element;
			AbstractRepositoryClient client = MylarTaskListPlugin.getRepositoryManager().getRepositoryClient(
					query.getRepositoryKind());
			client.openEditQueryDialog(query);
		}
	}

	// /**
	// * Consider refactoring to make generic
	// */
	// private static class SynchronizeTaskWithRepositoryJob extends Job {
	//
	// private static final String JOB_LABEL = "Synchronizing task with
	// repository";
	//
	// private AbstractRepositoryClient client;
	//
	// private ITask task;
	//
	// public SynchronizeTaskWithRepositoryJob(AbstractRepositoryClient client,
	// ITask task) {
	// super(JOB_LABEL);
	// this.client = client;
	// this.task = task;
	// }
	//
	// public IStatus run(IProgressMonitor monitor) {
	// try {
	// monitor.beginTask(JOB_LABEL, 10);
	// monitor.worked(1);
	// client.synchronize(task);
	// monitor.done();
	// return new Status(IStatus.OK, MylarPlugin.PLUGIN_ID, IStatus.OK, "",
	// null);
	// } catch (Exception e) {
	// MylarStatusHandler.fail(e, "Could not open task editor", true);
	// }
	// return Status.CANCEL_STATUS;
	// }
	// }

}
