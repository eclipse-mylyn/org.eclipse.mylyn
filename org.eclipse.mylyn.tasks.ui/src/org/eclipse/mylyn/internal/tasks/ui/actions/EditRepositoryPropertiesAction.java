/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;

/**
 * @author Mik Kersten
 */
public class EditRepositoryPropertiesAction extends AbstractTaskRepositoryAction {

	private static final String ID = "org.eclipse.mylyn.tasklist.repositories.properties";

	public EditRepositoryPropertiesAction() {
		super("Properties");
		setId(ID);
		setEnabled(false);
	}

	@Override
	protected boolean updateSelection(IStructuredSelection selection) {
		if (selection != null && !selection.isEmpty()) {
			Object selectedObject = selection.getFirstElement();
			if (selectedObject instanceof TaskRepository) {
				TaskRepository taskRepository = (TaskRepository) selectedObject;
				AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
						taskRepository.getConnectorKind());
				if (connector.isUserManaged()) {
					return true;
				}
				return false;
			}
		}
		return false;
	}

	@Override
	public void run() {
		IStructuredSelection selection = getStructuredSelection();
		if (selection.getFirstElement() instanceof TaskRepository) {
			TasksUiUtil.openEditRepositoryWizard((TaskRepository) selection.getFirstElement());
		}
	}
}
