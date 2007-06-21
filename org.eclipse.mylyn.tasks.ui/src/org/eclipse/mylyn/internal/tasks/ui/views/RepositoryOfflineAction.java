/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * @author Steffen Pingel
 */
public class RepositoryOfflineAction extends Action implements ISelectionChangedListener {

	private static final String ID = "org.eclipse.mylyn.tasklist.repositories.offline";
	
	private TaskRepository repository;

	public RepositoryOfflineAction() {
		super("Offline", Action.AS_CHECK_BOX);
		setId(ID);
		setEnabled(false);
	}

	@Override
	public void run() {
		repository.setOffline(isChecked());
		TasksUiPlugin.getRepositoryManager().notifyRepositorySettingsChagned(repository);
		TasksUiPlugin.getRepositoryManager().saveRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void selectionChanged(SelectionChangedEvent event) {
		ISelection selection = event.getSelection();
		if (selection instanceof IStructuredSelection) {
			Object selectedObject = ((IStructuredSelection)selection).getFirstElement();
			if (selectedObject instanceof TaskRepository) {
				AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
						((TaskRepository) selectedObject).getKind());
				if (connector.isUserManaged()) {
					this.repository = (TaskRepository) selectedObject;
					setChecked(this.repository.isOffline());
					setEnabled(true);
					return;
				}
			}
		}
		this.repository = null;
		setChecked(false);
		setEnabled(false);
	}

}
