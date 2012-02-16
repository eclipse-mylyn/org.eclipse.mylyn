/*******************************************************************************
 * Copyright (c) 2012 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Sascha Scholz (SAP) - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.egit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.internal.gerrit.core.GerritCorePlugin;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritConfiguration;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;

import com.google.gerrit.reviewdb.Project;

/**
 * @author Sascha Scholz
 */
public class GerritRepositorySearchPageContentProvider implements ITreeContentProvider {

	public GerritRepositorySearchPageContentProvider() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof TaskRepository) {
			return !getProjects((TaskRepository) element).isEmpty();
		}
		return false;
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getGerritTaskRepositories().toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof TaskRepository) {
			return getProjects((TaskRepository) parentElement).toArray();
		}
		return null;
	}

	private static List<TaskRepository> getGerritTaskRepositories() {
		List<TaskRepository> repositories = new ArrayList<TaskRepository>();
		TaskRepositoryManager repositoryManager = TasksUiPlugin.getRepositoryManager();
		for (AbstractRepositoryConnector connector : repositoryManager.getRepositoryConnectors()) {
			Set<TaskRepository> connectorRepositories = repositoryManager.getRepositories(connector.getConnectorKind());
			for (TaskRepository repository : connectorRepositories) {
				if (repository.getConnectorKind().equals(
						GerritCorePlugin.getDefault().getConnector().getConnectorKind())) {
					repositories.add(repository);
				}
			}
		}
		return repositories;
	}

	private List<Project> getProjects(TaskRepository repository) {
		GerritConfiguration config = GerritCorePlugin.getGerritClient(repository).getConfiguration();
		if (config != null) {
			return config.getProjects();
		}
		return Collections.emptyList();
	}
}