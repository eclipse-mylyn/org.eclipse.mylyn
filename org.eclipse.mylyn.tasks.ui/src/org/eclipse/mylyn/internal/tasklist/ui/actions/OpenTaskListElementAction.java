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
import org.eclipse.mylar.internal.tasklist.ui.TaskListUiUtil;
import org.eclipse.mylar.provisional.tasklist.AbstractQueryHit;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryConnector;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryQuery;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.ITaskContainer;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskCategory;

/**
 * @author Mik Kersten
 */
public class OpenTaskListElementAction extends Action {

	public static final String ID = "org.eclipse.mylar.tasklist.actions.open";

	private final StructuredViewer viewer;

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
		if (element instanceof ITask || element instanceof AbstractQueryHit) {
			final ITask task;
			if (element instanceof AbstractQueryHit) {
				task = ((AbstractQueryHit) element).getOrCreateCorrespondingTask();
			} else {
				task = (ITask) element;
			}
			
			//element instanceof IQueryHit;
			boolean forceUpdate = false;

			final AbstractRepositoryConnector connector = MylarTaskListPlugin.getRepositoryManager().getRepositoryClient(
					task.getRepositoryKind());
			if (!task.isLocal() && connector != null) {
				Job refreshJob = connector.synchronize(task, forceUpdate, new IJobChangeListener() {

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
		} else if (element instanceof TaskCategory) {
			TaskListUiUtil.openEditor((ITaskContainer) element);
		} else if (element instanceof AbstractRepositoryQuery) {
			AbstractRepositoryQuery query = (AbstractRepositoryQuery) element;
			AbstractRepositoryConnector client = MylarTaskListPlugin.getRepositoryManager().getRepositoryClient(
					query.getRepositoryKind());
			client.openEditQueryDialog(query);
		}
	}

}
