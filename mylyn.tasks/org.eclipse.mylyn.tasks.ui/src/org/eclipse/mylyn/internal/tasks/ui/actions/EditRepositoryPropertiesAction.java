/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class EditRepositoryPropertiesAction extends AbstractTaskRepositoryAction implements IViewActionDelegate {

	private static final String ID = "org.eclipse.mylyn.tasklist.repositories.properties"; //$NON-NLS-1$

	public EditRepositoryPropertiesAction() {
		super(Messages.EditRepositoryPropertiesAction_Properties);
		setId(ID);
		setEnabled(false);
	}

	@Override
	public void run() {
		TaskRepository taskRepository = getTaskRepository(getStructuredSelection());
		if (taskRepository != null) {
			TasksUiUtil.openEditRepositoryWizard(taskRepository);
		}
	}

	@Override
	public void init(IViewPart view) {
	}

	@Override
	public void run(IAction action) {
		run();
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			selectionChanged((IStructuredSelection) selection);
			action.setEnabled(isEnabled());
		} else {
			clearCache();
			action.setEnabled(false);
		}
	}

}
