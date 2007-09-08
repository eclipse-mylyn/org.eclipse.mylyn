/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.Separator;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.views.AbstractTaskListPresentation;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

/**
 * @author Rob Elves
 */
public class PresentationDropDownSelectionAction extends Action implements IMenuCreator {

	private static final String LABEL_NAME = "Task Presentation";

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.presentationselection";

	private TaskListView view;

	protected Menu dropDownMenu = null;

	public PresentationDropDownSelectionAction(TaskListView view) {
		super();
		this.view = view;
		setMenuCreator(this);
		setText(LABEL_NAME);
		setToolTipText(LABEL_NAME);
		setId(ID);
		setEnabled(true);
		setImageDescriptor(TasksUiImages.TASKLIST_MODE);
	}

	protected void addActionsToMenu() {
		for (AbstractTaskListPresentation presentation : TaskListView.getPresentations()) {
			if (presentation.isPrimary()) {
				PresentationSelectionAction action = new PresentationSelectionAction(presentation);
				ActionContributionItem item = new ActionContributionItem(action);
				action.setText(presentation.getName());
				action.setImageDescriptor(presentation.getImageDescriptor());
				action.setChecked(view.getCurrentPresentation().getId().equals(presentation.getId()));
				item.fill(dropDownMenu, -1);
			}
		}
		boolean separatorAdded = false;
		
		for (AbstractTaskListPresentation presentation : TaskListView.getPresentations()) {
			if (!presentation.isPrimary()) {
				if (!separatorAdded) {
					new Separator().fill(dropDownMenu, -1);
					separatorAdded = true;
				}
				
				PresentationSelectionAction action = new PresentationSelectionAction(presentation);
				ActionContributionItem item = new ActionContributionItem(action);
				action.setText(presentation.getName());
				action.setImageDescriptor(presentation.getImageDescriptor());
				action.setChecked(view.getCurrentPresentation().getId().equals(presentation.getId()));
				item.fill(dropDownMenu, -1);
			}
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

	private class PresentationSelectionAction extends Action {

		private AbstractTaskListPresentation presentation;

		public PresentationSelectionAction(AbstractTaskListPresentation presentation) {
			this.presentation = presentation;
			setText(presentation.getName());
		}

		@Override
		public void run() {
			view.applyPresentation(presentation);
		}
	}

}
