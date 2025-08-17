/*******************************************************************************
 * Copyright (c) 2012, 2013 SAP and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *      Sascha Scholz (SAP) - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.egit;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.internal.gerrit.core.GerritCorePlugin;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritConfiguration;
import org.eclipse.mylyn.internal.tasks.ui.TaskRepositoryComparator;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
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
		return getGerritTaskRepositories();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof TaskRepository) {
			return getProjects((TaskRepository) parentElement).toArray();
		}
		return null;
	}

	private static TaskRepository[] getGerritTaskRepositories() {
		Set<TaskRepository> repositories = TasksUiPlugin.getRepositoryManager()
				.getRepositories(GerritConnector.CONNECTOR_KIND);
		for (Iterator<TaskRepository> it = repositories.iterator(); it.hasNext();) {
			TaskRepository repository = it.next();
			if (repository.isOffline()) {
				it.remove();
			}
		}
		TaskRepository[] result = repositories.toArray(new TaskRepository[repositories.size()]);
		Arrays.sort(result, new TaskRepositoryComparator());
		return result;
	}

	private List<Project> getProjects(TaskRepository repository) {
		GerritConfiguration config = GerritCorePlugin.getGerritClient(repository).getConfiguration();
		if (config != null) {
			return config.getProjects();
		}
		return Collections.emptyList();
	}
}