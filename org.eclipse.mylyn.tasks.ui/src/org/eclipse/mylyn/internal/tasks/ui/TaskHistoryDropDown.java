/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.TaskActivationHistory;
import org.eclipse.mylyn.internal.tasks.ui.actions.ActivateTaskDialogAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskActivateAction;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskElement;
import org.eclipse.mylyn.tasks.ui.TaskElementLabelProvider;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;

/**
 * @author Wesley Coelho
 * @author Mik Kersten
 * @author Leo Dos Santos
 * @author Steffen Pingel
 */
public class TaskHistoryDropDown extends CompoundContributionItem {

	private class ActivateDialogAction extends Action {

		private final ActivateTaskDialogAction dialogAction;

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

	private class DeactivateTaskAction extends Action {

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
				taskDescription = taskDescription.subSequence(0, MAX_LABEL_LENGTH - 3) + "...";
			}
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
			new TaskActivateAction().run(targetTask);
		}
	}

	private final static int MAX_ITEMS_TO_DISPLAY = 12;

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
	@SuppressWarnings("deprecation")
	protected IContributionItem[] getContributionItems() {
		List<AbstractTask> tasks = new ArrayList<AbstractTask>(taskHistory.getPreviousTasks());
		Set<IWorkingSet> sets = TaskListView.getActiveWorkingSets();
		if (scopedToWorkingSet && !sets.isEmpty()) {
			Set<ITask> allWorkingSetTasks = new HashSet<ITask>();
			for (IWorkingSet workingSet : sets) {
				IAdaptable[] elements = workingSet.getElements();
				for (IAdaptable adaptable : elements) {
					if (adaptable instanceof ITaskElement) {
						allWorkingSetTasks.addAll(((ITaskElement) adaptable).getChildren());
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

		Separator separator = new Separator();
		items.add(separator);

		ITask active = TasksUi.getTaskActivityManager().getActiveTask();
		if (active != null) {
			IContributionItem pauseContributionItem = new CommandContributionItem(PlatformUI.getWorkbench(),
					"org.eclipse.mylyn.ui.context.capture.pause", // id 
					"org.eclipse.mylyn.ui.context.capture.pause.command", // commandId 
					Collections.EMPTY_MAP, // params 
					TasksUiImages.CONTEXT_CAPTURE_PAUSE, // icon 
					null, null, "Pause Capturing Context", // label 
					null, // mnemonic 
					"Pause Capturing Context", // tooltip
					CommandContributionItem.STYLE_CHECK);
			items.add(pauseContributionItem);

			Action deactivateAction = new DeactivateTaskAction();
			ActionContributionItem item = new ActionContributionItem(deactivateAction);
			items.add(item);
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
