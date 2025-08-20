/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
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

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Shawn Minto
 * @author Steffen Pingel
 */
public abstract class AbstractTaskRepositoryAction extends BaseSelectionListenerAction {

	private boolean singleSelect;

	public AbstractTaskRepositoryAction(String text) {
		super(text);
	}

	@Override
	protected boolean updateSelection(IStructuredSelection selection) {
		if (selection != null && !selection.isEmpty()) {
			if (singleSelect) {
				Object[] array = selection.toArray();
				if (array.length != 1) {
					return false;
				}
			}
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
		} else if (selectedObject instanceof IRepositoryQuery query) {
			taskRepository = TasksUi.getRepositoryManager()
					.getRepository(query.getConnectorKind(), query.getRepositoryUrl());
		}

		if (taskRepository != null && isUserManaged(taskRepository)) {
			return taskRepository;
		}
		return null;
	}

	protected boolean isUserManaged(TaskRepository taskRepository) {
		AbstractRepositoryConnector connector = TasksUi.getRepositoryManager()
				.getRepositoryConnector(taskRepository.getConnectorKind());
		return connector != null && connector.isUserManaged();
	}

	protected boolean isSingleSelect() {
		return singleSelect;
	}

	protected void setSingleSelect(boolean singleSelect) {
		this.singleSelect = singleSelect;
	}

}
