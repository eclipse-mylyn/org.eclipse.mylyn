/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.mylyn.internal.monitor.core.util.StatusManager;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskRepositoryManager;
import org.osgi.service.prefs.BackingStoreException;

/**
 * Default Task Repository link provider
 * 
 * @author Eugene Kuleshov
 */
public class ProjectPreferencesLinkProvider extends AbstractTaskRepositoryLinkProvider {

	private static final String PROPERTY_PREFIX = "project.repository";

	private static final String PROJECT_REPOSITORY_KIND = PROPERTY_PREFIX + ".kind";

	private static final String PROJECT_REPOSITORY_URL = PROPERTY_PREFIX + ".url";

	public TaskRepository getTaskRepository(IResource resource, TaskRepositoryManager repositoryManager) {
		IProject project = resource.getProject();
		if (project == null || !project.isAccessible()) {
			return null;
		}

		IScopeContext projectScope = new ProjectScope(project);
		IEclipsePreferences projectNode = projectScope.getNode(TasksUiPlugin.PLUGIN_ID);
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
		IEclipsePreferences projectNode = projectScope.getNode(TasksUiPlugin.PLUGIN_ID);
		if (projectNode != null) {
			projectNode.put(PROJECT_REPOSITORY_KIND, repository.getKind());
			projectNode.put(PROJECT_REPOSITORY_URL, repository.getUrl());
			try {
				projectNode.flush();
				return true;
			} catch (BackingStoreException e) {
				StatusManager.fail(e, "Failed to save task repository to project association preference", false);
			}
		}
		return false;
	}

}
