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

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.Separator;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.internal.tasks.ui.views.AbstractTaskListPresentation;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

/**
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class PresentationDropDownSelectionAction extends Action implements IMenuCreator {

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.presentationselection"; //$NON-NLS-1$

	private final TaskListView view;

	private Menu dropDownMenu;

	public PresentationDropDownSelectionAction(TaskListView view) {
		this.view = view;
		setMenuCreator(this);
		setText(Messages.PresentationDropDownSelectionAction_Task_Presentation);
		setToolTipText(Messages.PresentationDropDownSelectionAction_Task_Presentation);
		setId(ID);
		setEnabled(true);
		setImageDescriptor(CommonImages.PRESENTATION);
	}

	private void addActionsToMenu() {
		for (AbstractTaskListPresentation presentation : TaskListView.getPresentations()) {
			if (presentation.isPrimary()) {
				PresentationSelectionAction action = new PresentationSelectionAction(view, presentation);
				ActionContributionItem item = new ActionContributionItem(action);
				item.fill(dropDownMenu, -1);
			}
		}

		boolean separatorAdded = false;

		for (AbstractTaskListPresentation presentation : TaskListView.getPresentations()) {
			if (WorkbenchUtil.allowUseOf(presentation)) {
				if (!presentation.isPrimary()) {
					if (!separatorAdded) {
						new Separator().fill(dropDownMenu, -1);
						separatorAdded = true;
					}

					PresentationSelectionAction action = new PresentationSelectionAction(view, presentation);
					ActionContributionItem item = new ActionContributionItem(action);
					item.fill(dropDownMenu, -1);
				}
			}
		}
	}

	@Override
	public void run() {
		AbstractTaskListPresentation current = view.getCurrentPresentation();
		List<AbstractTaskListPresentation> all = TaskListView.getPresentations();
		int size = all.size();
		if (size == 0) {
			return;
		}

		// cycle between primary presentations
		int index = all.indexOf(current) + 1;
		for (int i = 0; i < size; i++) {
			AbstractTaskListPresentation presentation = all.get(index % size);
			if (presentation.isPrimary()) {
				view.applyPresentation(presentation);
				return;
			}
			index++;
		}

		// fall back to next presentation in list
		index = all.indexOf(current) + 1;
		if (index < size) {
			view.applyPresentation(all.get(index));
		} else {
			view.applyPresentation(all.get(0));
		}
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

	public static class PresentationSelectionAction extends Action {

		private final AbstractTaskListPresentation presentation;

		private final TaskListView view;

		public PresentationSelectionAction(TaskListView view, AbstractTaskListPresentation presentation) {
			this.view = view;
			this.presentation = presentation;
			setText(presentation.getName());
			setImageDescriptor(presentation.getImageDescriptor());
			update();
		}

		public void update() {
			setChecked(view.getCurrentPresentation().getId().equals(presentation.getId()));
		}

		@Override
		public void run() {
			view.applyPresentation(presentation);
		}

	}

}
