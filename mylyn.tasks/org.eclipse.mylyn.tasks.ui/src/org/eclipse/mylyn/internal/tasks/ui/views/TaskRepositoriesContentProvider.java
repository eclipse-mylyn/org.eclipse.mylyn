/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
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
import java.util.Set;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.internal.tasks.core.Category;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * @author Robert Elves
 */
public class TaskRepositoriesContentProvider implements ITreeContentProvider {

	private final TaskRepositoryManager manager;

	public TaskRepositoriesContentProvider() {
		manager = ((TaskRepositoryManager) TasksUi.getRepositoryManager());
	}

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}

	public void dispose() {
	}

	public Object[] getElements(Object parent) {

		Set<Object> objects = new HashSet<Object>();
		objects.addAll(manager.getCategories());
		return objects.toArray();
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof Category) {
			Set<Object> objects = new HashSet<Object>();
			for (TaskRepository repository : TasksUi.getRepositoryManager().getAllRepositories()) {
				Category cat = manager.getCategory(repository);
				if (cat.equals(parentElement)) {
					objects.add(repository);
				}
//				String categoryId = repository.getProperty(IRepositoryConstants.PROPERTY_CATEGORY);
//				if (categoryId != null && ((Category) parentElement).getId().equals(categoryId)) {
//					objects.add(repository);
//				}
			}
			return objects.toArray();
		}
		return new Object[0];
	}

	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
		return element instanceof Category;
	}
}