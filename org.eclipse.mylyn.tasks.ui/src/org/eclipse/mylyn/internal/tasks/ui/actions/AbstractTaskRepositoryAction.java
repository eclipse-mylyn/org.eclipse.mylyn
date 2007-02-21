/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.tasks.ui.actions;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Shawn Minto
 */
public abstract class AbstractTaskRepositoryAction extends BaseSelectionListenerAction {

	public AbstractTaskRepositoryAction(String text) {
		super(text);
	}

	@Override
	protected boolean updateSelection(IStructuredSelection selection) {
		if (selection != null && !selection.isEmpty()) {
			Object selectedObject = selection.getFirstElement();
			if (selectedObject instanceof TaskRepository) {
				TaskRepository taskRepository = (TaskRepository) selectedObject;
				AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
						taskRepository.getKind());
				if (connector.isUserManaged()) {
					return true;
				}
				return false;
			}
		}
		return false;
	}

}
