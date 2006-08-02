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
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;

/**
 * @author Mik Kersten
 * @author Eugene Kuleshov
 * @author Steffen Pingel
 */
public class NewRepositoryTaskWizard extends MultiRepositoryAwareWizard {

	private static final String TITLE = "New Repository Task";

	public NewRepositoryTaskWizard() {
		super(new NewRepositoryTaskPage(getTaskRepositories()), TITLE);
		setNeedsProgressMonitor(true);
	}

	public NewRepositoryTaskWizard(IStructuredSelection selection) {
		super(new NewRepositoryTaskPage(getTaskRepositories()).setSelection(selection), TITLE);
		setNeedsProgressMonitor(true);
	}

	private static TaskRepository[] getTaskRepositories() {
		List<TaskRepository> repositories = new ArrayList<TaskRepository>();
		for (AbstractRepositoryConnector client : TasksUiPlugin.getRepositoryManager().getRepositoryConnectors()) {
			Set<TaskRepository> clientRepositories = TasksUiPlugin.getRepositoryManager().getRepositories(
					client.getRepositoryType());
			for (TaskRepository repository : clientRepositories) {
				if (client.canCreateNewTask(repository)) {
					repositories.add(repository);
				}
			}
		}
		return repositories.toArray(new TaskRepository[0]);
	}

}
