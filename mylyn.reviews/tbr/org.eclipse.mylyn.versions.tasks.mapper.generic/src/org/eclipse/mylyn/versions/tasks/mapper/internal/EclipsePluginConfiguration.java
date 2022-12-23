/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Research Group for Industrial Software (INSO), Vienna University of Technology - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.versions.tasks.mapper.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.versions.core.ScmCore;
import org.eclipse.mylyn.versions.core.ScmRepository;
import org.eclipse.mylyn.versions.core.spi.ScmConnector;
import org.eclipse.mylyn.versions.tasks.mapper.generic.IConfiguration;

/**
 *
 * @author Kilian Matt
 *
 */
public class EclipsePluginConfiguration implements IConfiguration {

	public List<ScmRepository> getRepositoriesFor(ITask task)
			throws CoreException {
		Set<ScmRepository> repos = new HashSet<ScmRepository>();

		List<IProject> projects = getProjectsForTaskRepository(
				task.getConnectorKind(), task.getRepositoryUrl());
		for (IProject p : projects) {
			ScmRepository repository = getRepositoryForProject(p);
			if(repository!=null) {
				repos.add(repository);
			}
		}
		return new ArrayList<ScmRepository>(repos);
	}

	private List<IProject> getProjectsForTaskRepository(String connectorKind,
			String repositoryUrl) {
		List<IProject> projects = new ArrayList<IProject>();
		for (IProject project : ResourcesPlugin.getWorkspace().getRoot()
				.getProjects()) {
			TaskRepository repo = TasksUiPlugin.getDefault()
					.getRepositoryForResource(project);

			if (repo!=null && connectorKind.equals(repo.getConnectorKind())
					&& repositoryUrl.equals(repo.getRepositoryUrl())) {
				projects.add(project);
			}
		}
		return projects;
	}

	private ScmRepository getRepositoryForProject(IProject p)
			throws CoreException {
		ScmConnector connector = ScmCore.getConnector(p);
		if(connector==null) {
			return null;
		}
		ScmRepository repository = connector.getRepository(p,
				new NullProgressMonitor());
		return repository;
	}
}
