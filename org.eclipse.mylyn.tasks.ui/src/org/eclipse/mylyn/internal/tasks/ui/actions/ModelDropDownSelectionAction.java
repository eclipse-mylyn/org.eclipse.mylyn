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
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.mylar.internal.tasks.ui.TaskListImages;
import org.eclipse.mylar.internal.tasks.ui.views.TaskListContentProvider;
import org.eclipse.mylar.internal.tasks.ui.views.TaskListView;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

/**
 * @author Rob Elves
 */
public class ModelDropDownSelectionAction extends Action implements IMenuCreator {

	private static final String LABEL_NAME = "Task Presentation";

	public static final String ID = "org.eclipse.mylar.tasklist.actions.modelselection";

	private TaskListView view;

	protected Menu dropDownMenu = null;

	private TaskListContentProvider[] contentProviders;

	public ModelDropDownSelectionAction(TaskListView view, TaskListContentProvider[] contentProviders) {
		super();
		this.view = view;
		setMenuCreator(this);
		setText(LABEL_NAME);
		setToolTipText(LABEL_NAME);
		setId(ID);
		setEnabled(true);
		setImageDescriptor(TaskListImages.TASKLIST_MODE);
		this.contentProviders = contentProviders;
	}

	protected void addActionsToMenu() {
		for (TaskListContentProvider provider : contentProviders) {
			ModelSelectionAction action = new ModelSelectionAction(provider);
			ActionContributionItem item = new ActionContributionItem(action);
			action.setText(provider.getLabel());
			action.setChecked(view.getViewer().getContentProvider().equals(provider));
			item.fill(dropDownMenu, -1);
		}
	}

	@Override
	public void run() {
		// ignore
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

	private class ModelSelectionAction extends Action {

		private TaskListContentProvider provider;

		public ModelSelectionAction(TaskListContentProvider provider) {
			this.provider = provider;
			setText(provider.getLabel());
		}

		@Override
		public void run() {
			try {
				view.getViewer().getControl().setRedraw(false);
				view.getViewer().setContentProvider(provider);
				view.refreshAndFocus(view.isFocusedMode());
			} finally {
				view.getViewer().getControl().setRedraw(true);
			}
		}
	}

}
