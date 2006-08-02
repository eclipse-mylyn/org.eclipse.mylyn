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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.AbstractRepositoryConnector;
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
		super(new SelectRepositoryPageForAddExistingTask(getTaskRepositories()).setSelection(selection), TITLE);
	}

	private static final class SelectRepositoryPageForAddExistingTask extends SelectRepositoryPage {

		public SelectRepositoryPageForAddExistingTask(TaskRepository[] repositories) {
			super(repositories);
		}

		@Override
		protected IWizard createWizard(TaskRepository taskRepository) {
			AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
					taskRepository.getKind());
			return connector.getAddExistingTaskWizard(taskRepository);
		}
	}

	private static TaskRepository[] getTaskRepositories() {
		List<TaskRepository> repositories = new ArrayList<TaskRepository>();
		for (AbstractRepositoryConnector client : TasksUiPlugin.getRepositoryManager().getRepositoryConnectors()) {
			Set<TaskRepository> clientRepositories = TasksUiPlugin.getRepositoryManager().getRepositories(
					client.getRepositoryType());
			for (TaskRepository repository : clientRepositories) {
				if (client.canCreateTaskFromKey(repository)) {
					repositories.add(repository);
				}
			}
		}
		return repositories.toArray(new TaskRepository[0]);
	}

}
