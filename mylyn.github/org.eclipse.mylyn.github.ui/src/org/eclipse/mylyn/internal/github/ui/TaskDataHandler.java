/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.mylyn.internal.github.ui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

/**
 * Base handler for working with a {@link TaskData} selection
 */
public abstract class TaskDataHandler extends AbstractHandler {

	/**
	 * POST_HANDLER_CALLBACK - variable for post handler callback that is a
	 * {@link Runnable}
	 */
	public static final String POST_HANDLER_CALLBACK = "postHandlerCallback"; //$NON-NLS-1$

	/**
	 * Create context with given selection
	 *
	 * @param selection
	 * @param handlerService
	 * @return context
	 */
	public static IEvaluationContext createContext(
			IStructuredSelection selection, IHandlerService handlerService) {
		IEvaluationContext context = new EvaluationContext(
				handlerService.createContextSnapshot(false), selection.toList());
		context.addVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME, selection);
		context.removeVariable(ISources.ACTIVE_MENU_SELECTION_NAME);
		return context;
	}

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
		IWorkbenchSite site = HandlerUtil.getActiveSite(event);
		if (site == null) {
			IWorkbenchPart part = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage().getActivePart();
			if (part != null)
				site = part.getSite();
		}
		IWorkbenchSiteProgressService progress = site != null ? (IWorkbenchSiteProgressService) site
				.getService(IWorkbenchSiteProgressService.class) : null;
		if (progress != null)
			progress.schedule(job);
		else
			job.schedule();
	}

	/**
	 * Execute callback on trigger if configured
	 *
	 * @param event
	 */
	protected void executeCallback(ExecutionEvent event) {
		Object callback = HandlerUtil.getVariable(event, POST_HANDLER_CALLBACK);
		if (callback instanceof Runnable)
			((Runnable) callback).run();
	}
}
