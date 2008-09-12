/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eugene Kuleshov - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractTaskRepositoryLinkProvider;
import org.osgi.service.prefs.BackingStoreException;

/**
 * Default Task Repository link provider
 * 
 * @author Eugene Kuleshov
 */
public class ProjectPropertiesLinkProvider extends AbstractTaskRepositoryLinkProvider {

	private static final String PROPERTY_PREFIX = "project.repository";

	private static final String PROJECT_REPOSITORY_KIND = PROPERTY_PREFIX + ".kind";

	private static final String PROJECT_REPOSITORY_URL = PROPERTY_PREFIX + ".url";

	@Override
	public TaskRepository getTaskRepository(IResource resource, IRepositoryManager repositoryManager) {
		IProject project = resource.getProject();
		if (project == null || !project.isAccessible()) {
			return null;
		}

		IScopeContext projectScope = new ProjectScope(project);
		IEclipsePreferences projectNode = projectScope.getNode(TasksUiPlugin.ID_PLUGIN);
		if (projectNode != null) {
			String kind = projectNode.get(PROJECT_REPOSITORY_KIND, "");
			String urlString = projectNode.get(PROJECT_REPOSITORY_URL, "");
			return repositoryManager.getRepository(kind, urlString);
		}
		return null;
	}

	@Override
	public boolean canSetTaskRepository(IResource resource) {
		IProject project = resource.getProject();
		return project != null && project.isAccessible();
	}

	@Override
	public boolean setTaskRepository(IResource resource, TaskRepository repository) {
		IProject project = resource.getProject();
		if (project == null || !project.isAccessible()) {
			return false;
		}

		IScopeContext projectScope = new ProjectScope(project);
		IEclipsePreferences projectNode = projectScope.getNode(TasksUiPlugin.ID_PLUGIN);
		if (projectNode != null) {
			projectNode.put(PROJECT_REPOSITORY_KIND, repository.getConnectorKind());
			projectNode.put(PROJECT_REPOSITORY_URL, repository.getRepositoryUrl());
			try {
				projectNode.flush();
				return true;
			} catch (BackingStoreException e) {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
						"Failed to save task repository to project association preference", e));
			}
		}
		return false;
	}

}
