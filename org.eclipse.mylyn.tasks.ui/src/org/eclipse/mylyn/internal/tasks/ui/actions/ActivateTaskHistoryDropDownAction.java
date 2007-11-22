/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskActivationHistory;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.ITaskActivityListener;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;

/**
 * @author Wesley Coelho
 * @author Mik Kersten
 * @author Leo Dos Santos
 */
public class ActivateTaskHistoryDropDownAction extends TaskNavigateDropDownAction implements
		IWorkbenchWindowPulldownDelegate, ITaskActivityListener {

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.navigate.previous";

	private boolean scopeToWorkingSet = false;

	private static final String LABEL = "Activate Previous Task";

	public ActivateTaskHistoryDropDownAction() {
		this(TasksUiPlugin.getTaskListManager().getTaskActivationHistory(), false);
	}

	public ActivateTaskHistoryDropDownAction(TaskActivationHistory history, boolean scopeToWorkingSet) {
		super(history);
		setText(LABEL);
		setToolTipText(LABEL);
		setId(ID);
		setEnabled(true);
		setImageDescriptor(TasksUiImages.NAVIGATE_PREVIOUS);
		this.scopeToWorkingSet = scopeToWorkingSet;
		TasksUiPlugin.getTaskListManager().addActivityListener(this);
	}

	@Override
	public void dispose() {
		super.dispose();
		TasksUiPlugin.getTaskListManager().addActivityListener(this);
	}

	@Override
	protected void addActionsToMenu() {
		List<AbstractTask> tasks = new ArrayList<AbstractTask>(taskHistory.getPreviousTasks());
		Set<IWorkingSet> sets = TaskListView.getActiveWorkingSets();
		if (scopeToWorkingSet && !sets.isEmpty()) {
			Set<AbstractTask> allWorkingSetTasks = new HashSet<AbstractTask>();
			for (IWorkingSet workingSet : sets) {
				IAdaptable[] elements = workingSet.getElements();
				for (IAdaptable adaptable : elements) {
					if (adaptable instanceof AbstractTaskContainer) {
						allWorkingSetTasks.addAll(((AbstractTaskContainer) adaptable).getChildren());
					}
				}
			}
			List<AbstractTask> allScopedTasks = new ArrayList<AbstractTask>(tasks);
			for (AbstractTask task : tasks) {
				if (!allWorkingSetTasks.contains(task)) {
					allScopedTasks.remove(task);
				}
			}
			tasks = allScopedTasks;
		}

		if (tasks.size() > MAX_ITEMS_TO_DISPLAY) {
			tasks = tasks.subList(tasks.size() - MAX_ITEMS_TO_DISPLAY, tasks.size());
		}

		for (int i = tasks.size() - 1; i >= 0; i--) {
			AbstractTask currTask = tasks.get(i);
			Action taskNavAction = new TaskNavigateAction(currTask);
			ActionContributionItem item = new ActionContributionItem(taskNavAction);
			if (currTask.isActive()) {
				taskNavAction.setChecked(true);
			}
			item.fill(dropDownMenu, -1);
		}

		Separator separator = new Separator();
		separator.fill(dropDownMenu, -1);

		AbstractTask active = TasksUiPlugin.getTaskListManager().getTaskList().getActiveTask();
		if (active != null) {
			Action deactivateAction = new DeactivateTaskAction();
			ActionContributionItem item = new ActionContributionItem(deactivateAction);
			item.fill(dropDownMenu, -1);
		} else {
			Action activateDialogAction = new ActivateDialogAction(new ActivateTaskDialogAction());
			ActionContributionItem item = new ActionContributionItem(activateDialogAction);
			item.fill(dropDownMenu, -1);
		}
	}

	@Override
	public void run() {
		if (taskHistory.hasPrevious()) {
			AbstractTask previousTask = taskHistory.getPreviousTask();
			if (previousTask != null && !previousTask.isActive()) {
				TasksUiPlugin.getTaskListManager().activateTask(previousTask, false);
				if (TaskListView.getFromActivePerspective() != null) {
					TaskListView.getFromActivePerspective().refresh();
				}
			}
			
			setButtonStatus();
			// view.refreshAndFocus(false);
			// TasksUiUtil.refreshAndOpenTaskListElement(previousTask);
		}
	}

	public class DeactivateTaskAction extends Action {

		public DeactivateTaskAction() {
			setText("Deactivate Task");
			setToolTipText("Deactivate Task");
			setEnabled(true);
			setChecked(false);
			setImageDescriptor(null);
			//TasksUiImages.TASK_INACTIVE);
		}

		@Override
		public void run() {
			AbstractTask active = TasksUiPlugin.getTaskListManager().getTaskList().getActiveTask();
			if (active != null) {
				TasksUiPlugin.getTaskListManager().deactivateTask(active);
			}
		}

	}

	public class ActivateDialogAction extends Action {

		private ActivateTaskDialogAction dialogAction;

		public ActivateDialogAction(ActivateTaskDialogAction action) {
			dialogAction = action;
			dialogAction.init(PlatformUI.getWorkbench().getActiveWorkbenchWindow());

			setText("Activate Task...");
			setToolTipText("Activate Task...");
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

	public void taskActivated(AbstractTask task) {
		// TODO: update label
	}

	public void taskDeactivated(AbstractTask task) {
		// TODO: update label
	}

	public void init(IWorkbenchWindow window) {
	}

	public void run(IAction action) {
		run();
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void activityChanged(ScheduledTaskContainer week) {
	}

	public void calendarChanged() {
	}

	public void taskListRead() {
	}

}
