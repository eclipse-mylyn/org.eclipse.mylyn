/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Shawn Minto
 * @author Steffen Pingel
 */
public abstract class AbstractTaskRepositoryAction extends BaseSelectionListenerAction {

	public AbstractTaskRepositoryAction(String text) {
		super(text);
	}

	@Override
	protected boolean updateSelection(IStructuredSelection selection) {
		if (selection != null && !selection.isEmpty()) {
			return getTaskRepository(selection.getFirstElement()) != null;
		}
		return false;
	}

	protected TaskRepository getTaskRepository(IStructuredSelection selection) {
		if (selection != null && !selection.isEmpty()) {
			return getTaskRepository(selection.getFirstElement());
		}
		return null;
	}

	protected TaskRepository getTaskRepository(Object selectedObject) {
		TaskRepository taskRepository = null;
		if (selectedObject instanceof TaskRepository) {
			taskRepository = (TaskRepository) selectedObject;
		} else if (selectedObject instanceof AbstractRepositoryQuery) {
			AbstractRepositoryQuery query = (AbstractRepositoryQuery) selectedObject;
			taskRepository = TasksUiPlugin.getRepositoryManager().getRepository(query.getRepositoryKind(),
					query.getRepositoryUrl());
		}

		if (taskRepository != null && isUserManaged(taskRepository)) {
			return taskRepository;
		}
		return null;
	}

	protected boolean isUserManaged(TaskRepository taskRepository) {
		AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
				taskRepository.getConnectorKind());
		return connector != null && connector.isUserManaged();
	}

}
