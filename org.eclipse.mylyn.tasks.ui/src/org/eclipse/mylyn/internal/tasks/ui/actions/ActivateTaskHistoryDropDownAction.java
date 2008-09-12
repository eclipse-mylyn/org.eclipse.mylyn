/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.TaskActivationHistory;
import org.eclipse.mylyn.internal.tasks.ui.TaskHistoryDropDown;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;

/**
 * @author Wesley Coelho
 * @author Mik Kersten
 * @author Leo Dos Santos
 * @author Steffen Pingel
 */
public class ActivateTaskHistoryDropDownAction extends Action implements IWorkbenchWindowPulldownDelegate, IMenuCreator {

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.navigate.previous";

	private static final String LABEL = "Activate Previous Task";

	private Menu dropDownMenu;

	private final TaskActivationHistory taskHistory;

	private final TaskHistoryDropDown taskHistoryDropDown;

	public ActivateTaskHistoryDropDownAction() {
		this.taskHistory = TasksUiPlugin.getTaskActivityManager().getTaskActivationHistory();
		this.taskHistoryDropDown = new TaskHistoryDropDown(null, taskHistory);
		setText(LABEL);
		setToolTipText(LABEL);
		setId(ID);
		setEnabled(true);
		setImageDescriptor(TasksUiImages.CONTEXT_HISTORY_PREVIOUS);
	}

	public void dispose() {
		// ignore
	}

	public Menu getMenu(Control parent) {
		if (dropDownMenu != null) {
			dropDownMenu.dispose();
		}
		dropDownMenu = new Menu(parent);
		taskHistoryDropDown.fill(dropDownMenu, -1);
		return dropDownMenu;
	}

	public Menu getMenu(Menu parent) {
		if (dropDownMenu != null) {
			dropDownMenu.dispose();
		}
		dropDownMenu = new Menu(parent);
		taskHistoryDropDown.fill(dropDownMenu, -1);
		return dropDownMenu;
	}

	public void init(IWorkbenchWindow window) {
		// ignore
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
			setEnabled(taskHistory.getPreviousTasks() != null && taskHistory.getPreviousTasks().size() > 0);
		}
	}

	public void run(IAction action) {
		run();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// ignore
	}

}
