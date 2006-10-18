/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.tasks.ui;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.mylar.internal.tasks.ui.actions.NewCategoryAction;
import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractTaskContainer;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.ITaskListElement;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;

/**
 * @author Mik Kersten
 * @author Raphael Ackermann (bug 160315)
 */
public class MoveToCategoryMenuContributor implements IDynamicSubMenuContributor {

	private static final String LABEL = "Move to";

	public MenuManager getSubMenuManager(final List<ITaskListElement> selectedElements) {
		final MenuManager subMenuManager = new MenuManager(LABEL);

		subMenuManager.setVisible(selectedElements.size() > 0 && !(selectedElements.get(0) instanceof AbstractTaskContainer || selectedElements.get(0) instanceof AbstractRepositoryQuery));
		
		for (final AbstractTaskContainer category : TasksUiPlugin.getTaskListManager().getTaskList()
				.getCategories()) {
			if (!category.equals(TasksUiPlugin.getTaskListManager().getTaskList().getArchiveContainer())) {
				Action action = new Action() {
					@Override
					public void run() {
						moveToCategory(selectedElements, category);
					}
				};
				String text = handleAcceleratorKeys(category.getDescription());
				action.setText(text);
				action.setImageDescriptor(TaskListImages.CATEGORY);
				if (selectedElements.size() == 1 && selectedElements.get(0) instanceof AbstractQueryHit) {
					AbstractQueryHit hit = (AbstractQueryHit) selectedElements.get(0);
					if (hit.getCorrespondingTask() == null) {
						action.setEnabled(false);
					}
				}
				subMenuManager.add(action);
			}
		}
		// add New Category action at the end of the Move to Category Submenu
		// and move selected actions to this newly created category
		Action action = new NewCategoryAction() {
			@Override
			public void run() {
				super.run();
				if (super.cat != null) {
					moveToCategory(selectedElements, super.cat);
				}
			}
		};
		subMenuManager.add(new Separator());
		subMenuManager.add(action);
		return subMenuManager;
	}
	
	/**
	 * public for testing
	 * 
	 * Deals with text where user has entered a '@' or tab character but which are not meant to be accelerators.
	 * from: Action#setText: 
	 * Note that if you want to insert a '@' character into the text (but no accelerator,
	 * you can simply insert a '@' or a tab at the end of the text.
	 * see Action#setText
	 */
	public String handleAcceleratorKeys(String text) {
		if (text == null) {
			return null;
		}

		int index = text.lastIndexOf('\t');
		if (index == -1) {
			index = text.lastIndexOf('@');
		}
		if (index >= 0) {
			return text.concat("@");
		}
		return text;
	}

	/** 
	 * @param selectedElements
	 * @param category 
	 */
	private void moveToCategory(final List<ITaskListElement> selectedElements, AbstractTaskContainer category) {
		for (ITaskListElement element : selectedElements) {
			if (element instanceof ITask) {
				TasksUiPlugin.getTaskListManager().getTaskList().moveToContainer(category,
						(ITask) element);
			} else if (element instanceof AbstractQueryHit) {
				ITask task = ((AbstractQueryHit) element).getCorrespondingTask();
				if (task != null) {
					TasksUiPlugin.getTaskListManager().getTaskList().moveToContainer(category,
							task);
				}
			}
		}
	}
	
	
	

}
