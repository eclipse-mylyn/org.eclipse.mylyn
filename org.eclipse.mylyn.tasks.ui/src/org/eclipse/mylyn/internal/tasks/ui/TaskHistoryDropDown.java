/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.commons.ui.CommonUiUtil;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.TaskActivationHistory;
import org.eclipse.mylyn.internal.tasks.ui.actions.ActivateTaskDialogAction;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.workingsets.TaskWorkingSetUpdater;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
import org.eclipse.mylyn.tasks.ui.TaskElementLabelProvider;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;

/**
 * @author Wesley Coelho
 * @author Mik Kersten
 * @author Leo Dos Santos
 * @author Steffen Pingel
 * @author Sam Davis
 */
public class TaskHistoryDropDown extends CompoundContributionItem {

	private final static int MAX_ITEMS_TO_DISPLAY = 16;

	private class ActivateDialogAction extends Action {

		private final ActivateTaskDialogAction dialogAction;

		public ActivateDialogAction(ActivateTaskDialogAction action) {
			dialogAction = action;
			dialogAction.init(PlatformUI.getWorkbench().getActiveWorkbenchWindow());

			setText(Messages.TaskHistoryDropDown_Activate_Task_);
			setToolTipText(Messages.TaskHistoryDropDown_Activate_Task_);
			setEnabled(true);
			setChecked(false);
			setImageDescriptor(null);
			//TasksUiImages.TASK_ACTIVE);
		}

		@Override
		public void run() {
			dialogAction.run(null);
		}
	}

	private class DeactivateTaskAction extends Action {

		public DeactivateTaskAction() {
			setText(Messages.TaskHistoryDropDown_Deactivate_Task);
			setToolTipText(Messages.TaskHistoryDropDown_Deactivate_Task);
			setEnabled(true);
			setChecked(false);
			setImageDescriptor(null);
			//TasksUiImages.TASK_INACTIVE);
		}

		@Override
		public void run() {
			ITask active = TasksUi.getTaskActivityManager().getActiveTask();
			if (active != null) {
				TasksUi.getTaskActivityManager().deactivateTask(active);
			}
		}

	}

	/**
	 * Action for navigating to a specified task. This class should be protected but has been made public for testing
	 * only
	 */
	private class ActivateTaskAction extends Action {

		private static final int MAX_LABEL_LENGTH = 40;

		private final AbstractTask targetTask;

		public ActivateTaskAction(AbstractTask task) {
			targetTask = task;
			String taskDescription = task.getSummary();
			if (taskDescription.length() > MAX_LABEL_LENGTH) {
				taskDescription = taskDescription.subSequence(0, MAX_LABEL_LENGTH - 3) + "..."; //$NON-NLS-1$
			}
			taskDescription = CommonUiUtil.toMenuLabel(taskDescription);
			setText(taskDescription);
			setEnabled(true);
			setToolTipText(task.getSummary());
			Image image = labelProvider.getImage(task);
			setImageDescriptor(ImageDescriptor.createFromImage(image));
		}

		@Override
		public void run() {
			if (targetTask.isActive()) {
				return;
			}
			TasksUiInternal.activateTaskThroughCommand(targetTask);
		}

		@Override
		public void runWithEvent(Event event) {
			run();
			if ((event.stateMask & SWT.SHIFT) != 0) {
				TasksUiUtil.openTask(targetTask);
			}
		}

	}

	private final TaskElementLabelProvider labelProvider = new TaskElementLabelProvider(false);

	private boolean scopedToWorkingSet;

	private final TaskActivationHistory taskHistory;

	public TaskHistoryDropDown() {
		this(null);
	}

	public TaskHistoryDropDown(String id) {
		this(id, TasksUiPlugin.getTaskActivityManager().getTaskActivationHistory());
	}

	public TaskHistoryDropDown(String id, TaskActivationHistory taskHistory) {
		super(id);
		this.taskHistory = taskHistory;
	}

	@Override
	protected IContributionItem[] getContributionItems() {
		List<AbstractTask> tasks = new ArrayList<AbstractTask>(taskHistory.getPreviousTasks());
		Set<IWorkingSet> sets = TaskWorkingSetUpdater.getActiveWorkingSets(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow());
		if (scopedToWorkingSet && !sets.isEmpty()) {
			Set<ITask> allWorkingSetTasks = new HashSet<ITask>();
			for (IWorkingSet workingSet : sets) {
				IAdaptable[] elements = workingSet.getElements();
				for (IAdaptable adaptable : elements) {
					if (adaptable instanceof ITaskContainer) {
						allWorkingSetTasks.addAll(((ITaskContainer) adaptable).getChildren());
					}
				}
			}
			List<AbstractTask> allScopedTasks = new ArrayList<AbstractTask>(tasks);
			for (ITask task : tasks) {
				if (!allWorkingSetTasks.contains(task)) {
					allScopedTasks.remove(task);
				}
			}
			tasks = allScopedTasks;
		}

		if (tasks.size() > MAX_ITEMS_TO_DISPLAY) {
			tasks = tasks.subList(tasks.size() - MAX_ITEMS_TO_DISPLAY, tasks.size());
		}

		List<IContributionItem> items = new ArrayList<IContributionItem>();
		for (int i = tasks.size() - 1; i >= 0; i--) {
			AbstractTask currTask = tasks.get(i);
			Action taskNavAction = new ActivateTaskAction(currTask);
			ActionContributionItem item = new ActionContributionItem(taskNavAction);
			if (currTask.isActive()) {
				taskNavAction.setChecked(true);
			}
			items.add(item);
		}

		if (items.size() > 0) {
			Separator separator = new Separator();
			items.add(separator);
		}

		final ITask active = TasksUi.getTaskActivityManager().getActiveTask();
		if (active != null) {
			Action deactivateAction = new DeactivateTaskAction();
			ActionContributionItem item = new ActionContributionItem(deactivateAction);
			items.add(item);
			items.add(new ActionContributionItem(new Action(Messages.TaskHistoryDropDown_Open_Active_Task) {
				@Override
				public void run() {
					TasksUiInternal.openTask(active, active.getTaskId());
				}
			}));
		} else {
			Action activateDialogAction = new ActivateDialogAction(new ActivateTaskDialogAction());
			ActionContributionItem item = new ActionContributionItem(activateDialogAction);
			items.add(item);
		}

		return items.toArray(new IContributionItem[items.size()]);
	}

	public boolean isScopedToWorkingSet() {
		return scopedToWorkingSet;
	}

	/**
	 * If <code>scopedToWorkingSet</code> is set to true only tasks from the current working set are contributed.
	 */
	public void setScopedToWorkingSet(boolean scopedToWorkingSet) {
		this.scopedToWorkingSet = scopedToWorkingSet;
	}

}
