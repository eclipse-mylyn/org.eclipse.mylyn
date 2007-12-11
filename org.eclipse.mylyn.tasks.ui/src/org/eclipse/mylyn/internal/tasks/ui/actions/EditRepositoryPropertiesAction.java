/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class EditRepositoryPropertiesAction extends AbstractTaskRepositoryAction implements IViewActionDelegate {

	private static final String ID = "org.eclipse.mylyn.tasklist.repositories.properties";

	public EditRepositoryPropertiesAction() {
		super("Properties");
		setId(ID);
		setEnabled(false);
	}

	@Override
	protected boolean updateSelection(IStructuredSelection selection) {
		return getTaskRepository(selection) != null;
	}

	@Override
	public void run() {
		TaskRepository taskRepository = getTaskRepository(getStructuredSelection());
		if (taskRepository != null) {
			TasksUiUtil.openEditRepositoryWizard(taskRepository);
		}
	}
	
	private TaskRepository getTaskRepository(IStructuredSelection selection) {
		if (selection != null && !selection.isEmpty()) {
			Object selectedObject = selection.getFirstElement();
			TaskRepository taskRepository = null;
			if (selectedObject instanceof TaskRepository) {
				taskRepository  = (TaskRepository) selectedObject;
			} else if (selectedObject instanceof AbstractRepositoryQuery) {
				AbstractRepositoryQuery query = (AbstractRepositoryQuery) selectedObject;
				taskRepository = TasksUiPlugin.getRepositoryManager().getRepository(query.getRepositoryKind(), query.getRepositoryUrl());
			}
			
			if (taskRepository != null && isUserManaged(taskRepository)) {
				return taskRepository;
			}
		}
		return null;
	}

	private boolean isUserManaged(TaskRepository taskRepository) {
		AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
				taskRepository.getConnectorKind());
		return connector.isUserManaged();
	}

	public void init(IViewPart view) {
	}

	public void run(IAction action) {
		run();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			selectionChanged((IStructuredSelection) selection);
			action.setEnabled(this.isEnabled());
		} else {
			clearCache();
			action.setEnabled(false);
		}
	}
	
}
