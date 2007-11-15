/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylyn.internal.tasks.core.ITaskRepositoryFilter;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskSelection;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * @author Mik Kersten
 * @author Eugene Kuleshov
 */
public class NewTaskPage extends SelectRepositoryPage {

	private final TaskSelection taskSelection;

	// API-3.0: remove legacy support
	private boolean supportsTaskSelection;

	public NewTaskPage(ITaskRepositoryFilter taskRepositoryFilter, TaskSelection taskSelection) {
		super(taskRepositoryFilter);
		this.taskSelection = taskSelection;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected IWizard createWizard(TaskRepository taskRepository) {
		AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(taskRepository.getConnectorKind());
		IWizard wizard = connectorUi.getNewTaskWizard(taskRepository, taskSelection);
		if (wizard == null) {
			// API-3.0: remove legacy support
			wizard = connectorUi.getNewTaskWizard(taskRepository);
			supportsTaskSelection = false;
		} else {
			supportsTaskSelection = true;
		}
		return wizard;
	}

	// API-3.0: remove legacy support
	public boolean supportsTaskSelection() {
		return supportsTaskSelection;
	}

}
