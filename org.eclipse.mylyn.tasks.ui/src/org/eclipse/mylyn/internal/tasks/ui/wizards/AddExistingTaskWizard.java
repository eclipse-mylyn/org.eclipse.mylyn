/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.tasks.ui.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;

/**
 * @author Mik Kersten
 * @author Brock Janiczak
 * @author Eugene Kuleshov
 * @author Steffen Pingel
 */
public class AddExistingTaskWizard extends MultiRepositoryAwareWizard {

	public static final String TITLE = "Add Existing Repository Task";

	public AddExistingTaskWizard(IStructuredSelection selection) {
		super(new SelectRepositoryPageForAddExistingTask(TaskRepositoryFilter.CAN_CREATE_TASK_FROM_KEY).setSelection(selection), TITLE);
	}

	private static final class SelectRepositoryPageForAddExistingTask extends SelectRepositoryPage {

		public SelectRepositoryPageForAddExistingTask(TaskRepositoryFilter filter) {
			super(filter);
		}

		@Override
		protected IWizard createWizard(TaskRepository taskRepository) {
			AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getRepositoryUi(
					taskRepository.getKind());
			return connectorUi.getAddExistingTaskWizard(taskRepository);
		}
	}

}
