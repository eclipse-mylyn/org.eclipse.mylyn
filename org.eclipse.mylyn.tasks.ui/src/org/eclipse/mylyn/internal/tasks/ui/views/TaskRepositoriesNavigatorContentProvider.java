/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.commons.repositories.RepositoryCategory;
import org.eclipse.mylyn.internal.tasks.core.IRepositoryConstants;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryAdapter;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * @author Robert Elves
 * @author Steffen Pingel
 */
public class TaskRepositoriesNavigatorContentProvider implements ITreeContentProvider {

	private static final Object[] EMPTY_ARRAY = new Object[0];

	private final TaskRepositoryManager manager;

	private Viewer viewer;

	private Listener listener;

	private class Listener extends TaskRepositoryAdapter {
		@Override
		public void repositoryAdded(TaskRepository repository) {
			refresh(repository);
		}

		protected void refresh(TaskRepository repository) {
			if (viewer != null) {
				viewer.refresh();
			}
		}

		@Override
		public void repositoryRemoved(TaskRepository repository) {
			refresh(repository);
		}

		@Override
		public void repositorySettingsChanged(TaskRepository repository) {
			refresh(repository);
		}

		@Override
		public void repositoryUrlChanged(TaskRepository repository, String oldUrl) {
			refresh(repository);
		}
	}

	public TaskRepositoriesNavigatorContentProvider() {
		manager = ((TaskRepositoryManager) TasksUi.getRepositoryManager());
	}

	public void dispose() {
		if (listener != null) {
			manager.removeListener(listener);
			listener = null;
		}
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof RepositoryCategory) {
			List<TaskRepository> repositories = TasksUi.getRepositoryManager().getAllRepositories();
			RepositoryCategory category = (RepositoryCategory) parentElement;
			if (RepositoryCategory.ID_CATEGORY_ALL.equals(category.getId())) {
				return repositories.toArray();
			} else if (RepositoryCategory.ID_CATEGORY_OTHER.equals(category.getId())) {
				Set<Object> items = new HashSet<Object>();
				for (TaskRepository repository : repositories) {
					if (repository.getProperty(IRepositoryConstants.PROPERTY_CATEGORY) == null) {
						items.add(repository);
					}
				}
				return items.toArray();
			} else {
				Set<Object> items = new HashSet<Object>();
				for (TaskRepository repository : repositories) {
					if (category.getId().equals(repository.getProperty(IRepositoryConstants.PROPERTY_CATEGORY))) {
						items.add(repository);
					}
				}
				return items.toArray();
			}
		}
		return EMPTY_ARRAY;
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (listener != null) {
			manager.removeListener(listener);
			listener = null;
		}
		this.viewer = viewer;
		if (newInput != null) {
			listener = new Listener();
			manager.addListener(listener);
		}
	}

}
