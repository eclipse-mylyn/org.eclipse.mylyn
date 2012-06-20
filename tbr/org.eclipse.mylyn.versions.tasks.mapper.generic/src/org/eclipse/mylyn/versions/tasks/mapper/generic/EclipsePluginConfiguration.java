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
package org.eclipse.mylyn.versions.tasks.mapper.generic;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * 
 * @author Kilian Matt
 *
 */
public class EclipsePluginConfiguration implements IConfiguration {

	public List<IProject> getProjectsForTaskRepository(String connectorKind,
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

}
