/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.mylyn.internal.github.ui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

/**
 * Base handler for working with a {@link TaskData} selection
 */
public abstract class TaskDataHandler extends AbstractHandler {

	/**
	 * Get task data from event
	 * 
	 * @param event
	 * @return task data
	 */
	protected TaskData getTaskData(ExecutionEvent event) {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection == null || selection.isEmpty())
			selection = HandlerUtil.getActiveMenuSelection(event);

		if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
			Object first = ((IStructuredSelection) selection).getFirstElement();
			if (first instanceof TaskData)
				return (TaskData) first;
			else if (first instanceof ITask)
				try {
					return TasksUi.getTaskDataManager().getTaskData(
							(ITask) first);
				} catch (CoreException e) {
					return null;
				}
		}
		return null;
	}

	/**
	 * Should this handler be enabled for the given task data?
	 * 
	 * Always returns true by default, sub-classes should override
	 * 
	 * @param data
	 * @return true is enabled, false otherwise
	 */
	protected boolean isEnabled(TaskData data) {
		return true;
	}

	/**
	 * Schedule job
	 * 
	 * @param job
	 * @param event
	 */
	protected void schedule(Job job, ExecutionEvent event) {
		IWorkbenchSite activeSite = HandlerUtil.getActiveSite(event);
		IWorkbenchSiteProgressService service = (IWorkbenchSiteProgressService) activeSite
				.getService(IWorkbenchSiteProgressService.class);
		if (service != null)
			service.schedule(job);
		else
			job.schedule();
	}

}
