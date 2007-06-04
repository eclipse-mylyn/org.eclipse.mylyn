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
package org.eclipse.mylar.internal.tasks.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylar.internal.tasks.ui.views.TaskActivationHistory;
import org.eclipse.mylar.internal.tasks.ui.views.TaskElementLabelProvider;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

/**
 * This abstract class contains some common code used by NextTaskDropDownAction
 * and PreviousTaskDropDownAction
 * 
 * @author Wesley Coelho
 * @author Mik Kersten
 */
public abstract class TaskNavigateDropDownAction extends Action implements IMenuCreator {
	
	protected TaskActivationHistory taskHistory;

	protected Menu dropDownMenu = null;

	protected TaskElementLabelProvider labelProvider = new TaskElementLabelProvider(true);

	/** Maximum number of items to appear in the drop-down menu */
	protected final static int MAX_ITEMS_TO_DISPLAY = 12;

	public TaskNavigateDropDownAction(TaskActivationHistory history) {
		super();
		taskHistory = history;
		setMenuCreator(this);
	}

	/**
	 * Action for navigating to a specified task. This class should be protected
	 * but has been made public for testing only
	 */
	public class TaskNavigateAction extends Action {

		private ITask targetTask;

		private static final int MAX_LABEL_LENGTH = 40;

		public TaskNavigateAction(ITask task) {
			targetTask = task;
			String taskDescription = task.getSummary();
			if (taskDescription.length() > MAX_LABEL_LENGTH) {
				taskDescription = taskDescription.subSequence(0, MAX_LABEL_LENGTH - 3) + "...";
			}
			setText(taskDescription);
			setEnabled(true);
			setToolTipText(task.getSummary());
			if (task != null) {
				Image image = labelProvider.getImage(task);
				setImageDescriptor(ImageDescriptor.createFromImage(image));
			}
		}

		@Override
		public void run() {
			if (targetTask.isActive()) {
				return;
			}
			new TaskActivateAction().run(targetTask);
			// taskHistory.navigatedToTask(targetTask);
			taskHistory.addTask(targetTask);
			setButtonStatus();
//			view.refreshAndFocus(false);
//			TasksUiUtil.refreshAndOpenTaskListElement(targetTask);
		}
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

	public void setButtonStatus() {
		setEnabled(taskHistory.getPreviousTasks() != null && taskHistory.getPreviousTasks().size() > 0);
		// view.getNextTaskAction().setEnabled(taskHistory.hasNext());
	}

	protected abstract void addActionsToMenu();

}
