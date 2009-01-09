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

package org.eclipse.mylyn.internal.tasks.ui.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.Separator;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskComparator;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

/**
 * @author Mik Kersten
 */
class SortyByDropDownAction extends Action implements IMenuCreator {

	private final TaskListView taskListView;

	private static final String LABEL = Messages.SortyByDropDownAction_Sort_by;

	private Action byPriority;

	private Action bySummary;

	private Action byDateCreated;

	private Menu dropDownMenu = null;

	public SortyByDropDownAction(TaskListView taskListView) {
		super();
		this.taskListView = taskListView;
		setText(LABEL);
		setToolTipText(LABEL);
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
		byPriority = new Action("", AS_CHECK_BOX) { //$NON-NLS-1$
			@Override
			public void run() {
				taskListView.getSorter().setSortByIndex(TaskComparator.SortByIndex.PRIORITY);
				byPriority.setChecked(true);
				bySummary.setChecked(false);
				byDateCreated.setChecked(false);
			}
		};
		byPriority.setEnabled(true);
		byPriority.setText(Messages.SortyByDropDownAction_Priority);
		byPriority.setImageDescriptor(CommonImages.PRIORITY_1);
		new ActionContributionItem(byPriority).fill(dropDownMenu, -1);

		bySummary = new Action("", AS_CHECK_BOX) { //$NON-NLS-1$
			@Override
			public void run() {
				taskListView.getSorter().setSortByIndex(TaskComparator.SortByIndex.SUMMARY);
				byPriority.setChecked(false);
				bySummary.setChecked(true);
				byDateCreated.setChecked(false);
			}
		};
		bySummary.setEnabled(true);
		bySummary.setText(Messages.SortyByDropDownAction_Summary);
		new ActionContributionItem(bySummary).fill(dropDownMenu, -1);

		byDateCreated = new Action("", AS_CHECK_BOX) { //$NON-NLS-1$
			@Override
			public void run() {
				taskListView.getSorter().setSortByIndex(TaskComparator.SortByIndex.DATE_CREATED);
				byPriority.setChecked(false);
				bySummary.setChecked(false);
				byDateCreated.setChecked(true);
			}
		};
		byDateCreated.setEnabled(true);
		byDateCreated.setText(Messages.SortyByDropDownAction_Date_Created);
		byDateCreated.setImageDescriptor(CommonImages.CALENDAR_SMALL);
		new ActionContributionItem(byDateCreated).fill(dropDownMenu, -1);

		new Separator().fill(dropDownMenu, -1);

		Action reverse = new Action("", AS_CHECK_BOX) { //$NON-NLS-1$
			@Override
			public void run() {
				taskListView.getSorter().setSortDirection(taskListView.getSorter().getSortDirection() * -1);
				setChecked(taskListView.getSorter().getSortDirection() < 0);
			}
		};
		reverse.setEnabled(true);
		reverse.setText(Messages.SortyByDropDownAction_Descending);
		reverse.setChecked(taskListView.getSorter().getSortDirection() < 0);
		new ActionContributionItem(reverse).fill(dropDownMenu, -1);

		switch (taskListView.getSorter().getSortByIndex()) {
		case PRIORITY:
			byPriority.setChecked(true);
			break;
		case SUMMARY:
			bySummary.setChecked(true);
			break;
		case DATE_CREATED:
			byDateCreated.setChecked(true);
			break;
		}
	}

	@Override
	public void run() {
		this.setChecked(isChecked());
	}
}
