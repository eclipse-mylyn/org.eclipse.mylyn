/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.tasklist.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;
import org.eclipse.mylar.provisional.tasklist.AbstractTaskContainer;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.ITaskListElement;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;

/**
 * @author Mik Kersten
 */
public class MoveToCategoryMenuContributor implements IDynamicSubMenuContributor {

	private static final String LABEL = "Move to Category";

	public MenuManager getSubMenuManager(TaskListView view, ITaskListElement selectedElement) {
		ITask task = null;
		final MenuManager subMenuManager = new MenuManager(LABEL);
		if (selectedElement instanceof ITask) {
			task = (ITask) selectedElement;
		}
		final ITask taskToMove = task;
		if (taskToMove != null) {
			for (final AbstractTaskContainer category : MylarTaskListPlugin.getTaskListManager().getTaskList()
					.getCategories()) {
				if (!category.equals(task.getContainer()) && !category.equals(MylarTaskListPlugin.getTaskListManager().getTaskList().getArchiveContainer())) {
					Action action = new Action() {
						@Override
						public void run() {
							MylarTaskListPlugin.getTaskListManager().getTaskList()
									.moveToContainer(category, taskToMove);
						}
					};
					action.setText(category.getDescription());
					action.setImageDescriptor(TaskListImages.CATEGORY);
					subMenuManager.add(action);
				}
			}
		}
		return subMenuManager;
	}

}
