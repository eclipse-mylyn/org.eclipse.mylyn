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
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;

/**
 * @author Mik Kersten
 * @author Eugene Kuleshov
 */
public class NewQueryWizard extends MultiRepositoryAwareWizard {

	public NewQueryWizard() {
		super(new SelectRepositoryPageForNewQuery(), Messages.NewQueryWizard_New_Repository_Query);
	}

	private static final class SelectRepositoryPageForNewQuery extends SelectRepositoryPage {
		public SelectRepositoryPageForNewQuery() {
			super(ITaskRepositoryFilter.CAN_QUERY);
		}

		@Override
		protected IWizard createWizard(TaskRepository taskRepository) {
			AbstractRepositoryConnectorUi repositoryUi = TasksUiPlugin.getConnectorUi(taskRepository.getConnectorKind());
			return repositoryUi.getQueryWizard(taskRepository, null);
		}
	}

}
