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
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;

/**
 * @author Mik Kersten
 * @author Brock Janiczak
 * @author Eugene Kuleshov
 * @author Steffen Pingel
 */
public class AddExistingTaskWizard extends MultiRepositoryAwareWizard {

	public static final String TITLE = "Add Existing Repository Task";

	public AddExistingTaskWizard() {
		super(new SelectRepositoryPageForAddExistingTask(ITaskRepositoryFilter.CAN_CREATE_TASK_FROM_KEY), TITLE);
	}

	private static final class SelectRepositoryPageForAddExistingTask extends SelectRepositoryPage {

		public SelectRepositoryPageForAddExistingTask(ITaskRepositoryFilter filter) {
			super(filter);
		}

		@Override
		protected IWizard createWizard(TaskRepository taskRepository) {
			AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(taskRepository.getConnectorKind());
			return connectorUi.getAddExistingTaskWizard(taskRepository);
		}
	}

}
