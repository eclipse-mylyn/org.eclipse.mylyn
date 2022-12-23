/*******************************************************************************
 * Copyright (c) 2009, 2013 Tasktop Technologies and others.
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

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.PresentationFilter;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;
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

	private final HideQueryAction hideQueryAction;

	private final IAction undoAction;

	private final IAction redoAction;

	public TaskListViewActionGroup(TaskListView view, DrillDownAdapter drillDownAdapter) {
		this.view = view;
		this.drillDownAdapter = drillDownAdapter;

		goIntoAction = new GoIntoAction();
		goUpAction = new GoUpAction(drillDownAdapter);
		renameAction = add(new RenameAction(view));
		hideQueryAction = add(new HideQueryAction());

		IUndoContext undoContext = TasksUiInternal.getUndoContext();
		undoAction = new UndoActionHandler(view.getSite(), undoContext);
		redoAction = new RedoActionHandler(view.getSite(), undoContext);

		setSelectionProvider(view.getViewer());
	}

	public void dispose() {
		setSelectionProvider(null);
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

		if (hideQueryAction.isEnabled() && !PresentationFilter.getInstance().isFilterHiddenQueries()) {
			manager.appendToGroup(ID_SEPARATOR_REPOSITORY, new Separator());
			manager.appendToGroup(ID_SEPARATOR_REPOSITORY, hideQueryAction);
		}

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
			manager.appendToGroup(ID_SEPARATOR_NAVIGATE, goIntoAction);
		}
		if (goUpAction.isEnabled()) {
			manager.appendToGroup(ID_SEPARATOR_NAVIGATE, goUpAction);
		}
		if (!(element instanceof ITask) && renameAction.isEnabled() && element != null) {
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

	public IAction getUndoAction() {
		return undoAction;
	}

	public IAction getRedoAction() {
		return redoAction;
	}

}
