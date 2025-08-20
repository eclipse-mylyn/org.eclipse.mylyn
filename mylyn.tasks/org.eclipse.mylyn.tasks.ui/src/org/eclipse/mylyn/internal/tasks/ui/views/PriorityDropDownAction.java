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

package org.eclipse.mylyn.internal.tasks.ui.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

/**
 * @author Mik Kersten
 */
class PriorityDropDownAction extends Action implements IMenuCreator {

	/**
	 *
	 */
	private final TaskListView taskListView;

	private Action priority1;

	private Action priority2;

	private Action priority3;

	private Action priority4;

	private Action priority5;

	private Menu dropDownMenu = null;

	public PriorityDropDownAction(TaskListView taskListView) {
		this.taskListView = taskListView;
		setText(Messages.PriorityDropDownAction_Filter_Priority_Lower_Than);
		setToolTipText(Messages.PriorityDropDownAction_Filter_Priority_Lower_Than);
		setImageDescriptor(CommonImages.FILTER_PRIORITY);
		setMenuCreator(this);
	}

	@Override
	public void dispose() {
		if (dropDownMenu != null) {
			dropDownMenu.dispose();
			dropDownMenu = null;
		}
	}

	@Override
	public Menu getMenu(Control parent) {
		if (dropDownMenu != null) {
			dropDownMenu.dispose();
		}
		dropDownMenu = new Menu(parent);
		addActionsToMenu();
		return dropDownMenu;
	}

	@Override
	public Menu getMenu(Menu parent) {
		if (dropDownMenu != null) {
			dropDownMenu.dispose();
		}
		dropDownMenu = new Menu(parent);
		addActionsToMenu();
		return dropDownMenu;
	}

	public void addActionsToMenu() {
		priority1 = new Action("", AS_CHECK_BOX) { //$NON-NLS-1$
			@Override
			public void run() {
				TasksUiPlugin.getDefault()
						.getPreferenceStore()
						.setValue(ITasksUiPreferenceConstants.FILTER_PRIORITY, PriorityLevel.P1.toString());
				taskListView.displayPrioritiesAbove(TaskListView.PRIORITY_LEVELS[0]);
			}
		};
		priority1.setEnabled(true);
		priority1.setText(PriorityLevel.P1.getDescription());
		priority1.setImageDescriptor(CommonImages.PRIORITY_1);
		ActionContributionItem item = new ActionContributionItem(priority1);
		item.fill(dropDownMenu, -1);

		priority2 = new Action("", AS_CHECK_BOX) { //$NON-NLS-1$
			@Override
			public void run() {
				TasksUiPlugin.getDefault()
						.getPreferenceStore()
						.setValue(ITasksUiPreferenceConstants.FILTER_PRIORITY, PriorityLevel.P2.toString());
				taskListView.displayPrioritiesAbove(TaskListView.PRIORITY_LEVELS[1]);
			}
		};
		priority2.setEnabled(true);
		priority2.setText(PriorityLevel.P2.getDescription());
		priority2.setImageDescriptor(CommonImages.PRIORITY_2);
		item = new ActionContributionItem(priority2);
		item.fill(dropDownMenu, -1);

		priority3 = new Action("", AS_CHECK_BOX) { //$NON-NLS-1$
			@Override
			public void run() {
				TasksUiPlugin.getDefault()
						.getPreferenceStore()
						.setValue(ITasksUiPreferenceConstants.FILTER_PRIORITY, PriorityLevel.P3.toString());
				taskListView.displayPrioritiesAbove(TaskListView.PRIORITY_LEVELS[2]);
			}
		};
		priority3.setEnabled(true);
		priority3.setText(PriorityLevel.P3.getDescription());
		priority3.setImageDescriptor(CommonImages.PRIORITY_3);
		item = new ActionContributionItem(priority3);
		item.fill(dropDownMenu, -1);

		priority4 = new Action("", AS_CHECK_BOX) { //$NON-NLS-1$
			@Override
			public void run() {
				TasksUiPlugin.getDefault()
						.getPreferenceStore()
						.setValue(ITasksUiPreferenceConstants.FILTER_PRIORITY, PriorityLevel.P4.toString());
				taskListView.displayPrioritiesAbove(TaskListView.PRIORITY_LEVELS[3]);
			}
		};
		priority4.setEnabled(true);
		priority4.setText(PriorityLevel.P4.getDescription());
		priority4.setImageDescriptor(CommonImages.PRIORITY_4);
		item = new ActionContributionItem(priority4);
		item.fill(dropDownMenu, -1);

		priority5 = new Action("", AS_CHECK_BOX) { //$NON-NLS-1$
			@Override
			public void run() {
				TasksUiPlugin.getDefault()
						.getPreferenceStore()
						.setValue(ITasksUiPreferenceConstants.FILTER_PRIORITY, PriorityLevel.P5.toString());
				taskListView.displayPrioritiesAbove(TaskListView.PRIORITY_LEVELS[4]);
			}
		};
		priority5.setEnabled(true);
		priority5.setImageDescriptor(CommonImages.PRIORITY_5);
		priority5.setText(PriorityLevel.P5.getDescription());
		item = new ActionContributionItem(priority5);
		item.fill(dropDownMenu, -1);

		updateCheckedState();
//		updateCheckedState(priority1, priority2, priority3, priority4, priority5);
	}

	void updateCheckedState() {
		if (priority1 == null) {
			return;
		}
		String priority = TaskListView.getCurrentPriorityLevel();

		priority1.setChecked(false);
		priority2.setChecked(false);
		priority3.setChecked(false);
		priority4.setChecked(false);
		priority5.setChecked(false);

		if (priority.equals(TaskListView.PRIORITY_LEVELS[0])) {
			priority1.setChecked(true);
		} else if (priority.equals(TaskListView.PRIORITY_LEVELS[1])) {
			priority1.setChecked(true);
			priority2.setChecked(true);
		} else if (priority.equals(TaskListView.PRIORITY_LEVELS[2])) {
			priority1.setChecked(true);
			priority2.setChecked(true);
			priority3.setChecked(true);
		} else if (priority.equals(TaskListView.PRIORITY_LEVELS[3])) {
			priority1.setChecked(true);
			priority2.setChecked(true);
			priority3.setChecked(true);
			priority4.setChecked(true);
		} else if (priority.equals(TaskListView.PRIORITY_LEVELS[4])) {
			priority1.setChecked(true);
			priority2.setChecked(true);
			priority3.setChecked(true);
			priority4.setChecked(true);
			priority5.setChecked(true);
		}
	}

	@Override
	public void run() {
		setChecked(isChecked());
	}
}
