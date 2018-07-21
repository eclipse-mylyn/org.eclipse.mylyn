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
 *     Eugene Kuleshov - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylyn.internal.tasks.core.ITaskRepositoryFilter;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;

/**
 * @author Mik Kersten
 * @author Eugene Kuleshov
 */
public class NewTaskPage extends SelectRepositoryPage {

	private final ITaskMapping taskSelection;

	public NewTaskPage(ITaskRepositoryFilter taskRepositoryFilter, ITaskMapping taskSelection) {
		super(taskRepositoryFilter);
		this.taskSelection = taskSelection;
	}

	@Override
	protected IWizard createWizard(TaskRepository taskRepository) {
		AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(taskRepository.getConnectorKind());
		return connectorUi.getNewTaskWizard(taskRepository, taskSelection);
	}

}
