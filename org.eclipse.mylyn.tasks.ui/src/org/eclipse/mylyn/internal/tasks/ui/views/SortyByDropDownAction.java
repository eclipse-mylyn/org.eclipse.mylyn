/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.tasks.ui.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.mylar.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylar.internal.tasks.ui.TaskListPreferenceConstants;
import org.eclipse.mylar.tasks.core.Task;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

/**
 * @author Mik Kersten
 */
class SortyByDropDownAction extends Action implements IMenuCreator {

	/**
	 * 
	 */
	private final TaskListView taskListView;

	private static final String LABEL = "Sort by";

	private Action byPriority;
	
	private Action bySummary;
	
	private Menu dropDownMenu = null;

	public SortyByDropDownAction(TaskListView taskListView) {
		super();
		this.taskListView = taskListView;
		setText(LABEL);
		setToolTipText(LABEL);
//		setImageDescriptor(TasksUiImages.FILTER_PRIORITY);
		setMenuCreator(this);
	}

	public void dispose() {
		if (dropDownMenu != null) {
			dropDownMenu.dispose();
			dropDownMenu = null;
		}
	}

	public Menu getMenu(Control parent) {
		if (dropDownMenu != null) {
			dropDownMenu.dispose();
		}
		dropDownMenu = new Menu(parent);
		addActionsToMenu();
		return dropDownMenu;
	}

	public Menu getMenu(Menu parent) {
		if (dropDownMenu != null) {
			dropDownMenu.dispose();
		}
		dropDownMenu = new Menu(parent);
		addActionsToMenu();
		return dropDownMenu;
	}
	
	public void addActionsToMenu() {
		byPriority = new Action("", AS_CHECK_BOX) {
			@Override
			public void run() {
				TasksUiPlugin.getDefault().getPreferenceStore().setValue(
						TaskListPreferenceConstants.SELECTED_PRIORITY, Task.PriorityLevel.P1.toString());
				// MylarTaskListPlugin.setCurrentPriorityLevel(Task.PriorityLevel.P1);
				TaskListView.FILTER_PRIORITY.displayPrioritiesAbove(TaskListView.PRIORITY_LEVELS[0]);
				SortyByDropDownAction.this.taskListView.getViewer().refresh();
			}
		};
		byPriority.setEnabled(true);
		byPriority.setText(Task.PriorityLevel.P1.getDescription());
		byPriority.setImageDescriptor(TasksUiImages.PRIORITY_1);
		ActionContributionItem item = new ActionContributionItem(byPriority);
		item.fill(dropDownMenu, -1);

		bySummary = new Action("", AS_CHECK_BOX) {
			@Override
			public void run() {
				TasksUiPlugin.getDefault().getPreferenceStore().setValue(
						TaskListPreferenceConstants.SELECTED_PRIORITY, Task.PriorityLevel.P2.toString());
				// MylarTaskListPlugin.setCurrentPriorityLevel(Task.PriorityLevel.P2);
				TaskListView.FILTER_PRIORITY.displayPrioritiesAbove(TaskListView.PRIORITY_LEVELS[1]);
//				updateCheckedState(priority1, priority2, priority3, priority4, priority5);
				SortyByDropDownAction.this.taskListView.getViewer().refresh();
			}
		};
		bySummary.setEnabled(true);
		bySummary.setText(Task.PriorityLevel.P2.getDescription());
		bySummary.setImageDescriptor(TasksUiImages.PRIORITY_2);
		item = new ActionContributionItem(bySummary);
		item.fill(dropDownMenu, -1);
//		updateCheckedState(priority1, priority2, priority3, priority4, priority5);
	}

	@Override
	public void run() {
		this.setChecked(isChecked());
	}
}