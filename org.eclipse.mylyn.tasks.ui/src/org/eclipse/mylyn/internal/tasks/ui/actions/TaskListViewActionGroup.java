/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
import org.eclipse.ui.part.DrillDownAdapter;

/**
 * @author Steffen Pingel
 */
public class TaskListViewActionGroup extends RepositoryElementActionGroup {

	private final RenameAction renameAction;

	private final TaskListView view;

	private final GoIntoAction goIntoAction;

	private final GoUpAction goUpAction;

	private final DrillDownAdapter drillDownAdapter;

	public TaskListViewActionGroup(TaskListView view, DrillDownAdapter drillDownAdapter) {
		this.view = view;
		this.drillDownAdapter = drillDownAdapter;

		goIntoAction = new GoIntoAction();
		goUpAction = new GoUpAction(drillDownAdapter);
		renameAction = new RenameAction(view);
		view.getViewer().addSelectionChangedListener(renameAction);

		setSelectionProvider(view.getViewer());
	}

	public void updateDrillDownActions() {
		if (drillDownAdapter.canGoBack()) {
			goUpAction.setEnabled(true);
		} else {
			goUpAction.setEnabled(false);
		}
	}

	@Override
	public void fillContextMenu(final IMenuManager manager) {
		super.fillContextMenu(manager);

		updateDrillDownActions();

		Object element = ((IStructuredSelection) view.getViewer().getSelection()).getFirstElement();
		if (element instanceof ITaskContainer && !(element instanceof ITask)) {
			ITaskContainer cat = (ITaskContainer) element;
			if (cat.getChildren().size() > 0) {
				goIntoAction.setEnabled(true);
			} else {
				goIntoAction.setEnabled(false);
			}
		} else {
			goIntoAction.setEnabled(false);
		}
		if (goIntoAction.isEnabled()) {
			manager.appendToGroup(ID_SEPARATOR_NEW, goIntoAction);
		}
		if (goUpAction.isEnabled()) {
			manager.appendToGroup(ID_SEPARATOR_NEW, goUpAction);
		}
		if (!(element instanceof ITask)) {
			manager.appendToGroup(ID_SEPARATOR_EDIT, renameAction);
		}
	}

	public GoUpAction getGoUpAction() {
		return goUpAction;
	}

	public GoIntoAction getGoIntoAction() {
		return goIntoAction;
	}

	public RenameAction getRenameAction() {
		return renameAction;
	}

}
