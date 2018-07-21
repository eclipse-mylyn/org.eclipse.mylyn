/*******************************************************************************
 * Copyright (c) 2010, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
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
import org.eclipse.mylyn.commons.repositories.core.RepositoryCategory;
import org.eclipse.mylyn.internal.tasks.core.IRepositoryChangeListener;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryAdapter;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryChangeEvent;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryDelta;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryDelta.Type;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.swt.widgets.Display;

/**
 * @author Robert Elves
 * @author Steffen Pingel
 */
public class TaskRepositoriesNavigatorContentProvider implements ITreeContentProvider {

	private static final Object[] EMPTY_ARRAY = new Object[0];

	private final TaskRepositoryManager manager;

	private Viewer viewer;

	private Listener listener;

	private class Listener extends TaskRepositoryAdapter implements IRepositoryChangeListener {
		@Override
		public void repositoryAdded(TaskRepository repository) {
			refresh(repository);
		}

		protected void refresh(TaskRepository repository) {
			if (viewer != null) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (viewer != null && viewer.getControl() != null && !viewer.getControl().isDisposed()) {
							viewer.refresh();
						}
					}
				});
			}
		}

		@Override
		public void repositoryRemoved(TaskRepository repository) {
			refresh(repository);
		}

		@Override
		public void repositoryUrlChanged(TaskRepository repository, String oldUrl) {
			refresh(repository);
		}

		public void repositoryChanged(TaskRepositoryChangeEvent event) {
			Type type = event.getDelta().getType();
			if (type == TaskRepositoryDelta.Type.ALL || type == TaskRepositoryDelta.Type.PROPERTY
					|| type == TaskRepositoryDelta.Type.OFFLINE) {
				refresh(event.getRepository());
			}
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
					if (repository.getCategory() == null) {
						items.add(repository);
					}
				}
				return items.toArray();
			} else {
				Set<Object> items = new HashSet<Object>();
				for (TaskRepository repository : repositories) {
					if (category.getId().equals(repository.getCategory())) {
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
