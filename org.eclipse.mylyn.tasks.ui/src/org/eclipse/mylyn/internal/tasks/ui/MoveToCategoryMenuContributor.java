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
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.mylyn.internal.tasks.core.UnmatchedTaskContainer;
import org.eclipse.mylyn.internal.tasks.ui.actions.NewCategoryAction;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;

/**
 * @author Mik Kersten
 * @author Raphael Ackermann (bug 160315)
 */
public class MoveToCategoryMenuContributor implements IDynamicSubMenuContributor {

	private static final String LABEL = "Move to";

	public MenuManager getSubMenuManager(final List<AbstractTaskContainer> selectedElements) {
		final MenuManager subMenuManager = new MenuManager(LABEL);

		// Compute selected tasks
		List<AbstractTask> selectedTasks = new ArrayList<AbstractTask>(selectedElements.size());
		for (AbstractTaskContainer elemement : selectedElements) {
			if (elemement instanceof AbstractTask) {
				selectedTasks.add((AbstractTask) elemement);
			}
		}
		subMenuManager.setVisible(!selectedTasks.isEmpty());

		List<AbstractTaskCategory> categories = new ArrayList<AbstractTaskCategory>(TasksUiPlugin.getTaskListManager()
				.getTaskList()
				.getCategories());
		Collections.sort(categories);
		for (final AbstractTaskCategory category : categories) {
			if (!(category instanceof UnmatchedTaskContainer)) {
				String text = handleAcceleratorKeys(category.getSummary());
				Action action = new Action(text, IAction.AS_RADIO_BUTTON) {
					@Override
					public void run() {
						moveToCategory(selectedElements, category);
					}
				};
				action.setImageDescriptor(TasksUiImages.CATEGORY);
				if (selectedTasks.size() == 1) {
					if (category.contains(selectedTasks.get(0).getHandleIdentifier())) {
						action.setChecked(true);
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
	 * Deals with text where user has entered a '@' or tab character but which are not meant to be accelerators. from:
	 * Action#setText: Note that if you want to insert a '@' character into the text (but no accelerator, you can simply
	 * insert a '@' or a tab at the end of the text. see Action#setText
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
	private void moveToCategory(final List<AbstractTaskContainer> selectedElements, AbstractTaskCategory category) {
		for (AbstractTaskContainer element : selectedElements) {
			if (element instanceof AbstractTask) {
				TasksUiPlugin.getTaskListManager().getTaskList().moveTask((AbstractTask) element, category);
			}
		}
	}

}
