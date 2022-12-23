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

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.internal.tasks.core.Category;
import org.eclipse.mylyn.internal.tasks.core.IRepositoryConstants;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * @author Robert Elves
 */
public class TaskRepositoriesViewSorter extends TaskRepositoriesSorter {
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		if (e1 instanceof Category && e2 instanceof Category) {
			return ((Category) e1).compareTo(e2);
		}
		if (e1 instanceof Category && e2 instanceof TaskRepository) {

			Category cat1 = ((Category) e1);

			String categoryId = ((TaskRepository) e2).getProperty(IRepositoryConstants.PROPERTY_CATEGORY);
			Category cat2 = ((TaskRepositoryManager) TasksUi.getRepositoryManager()).getCategory(categoryId);

			return cat1.compareTo(cat2);

		} else if (e1 instanceof TaskRepository && e2 instanceof Category) {
			Category cat1 = ((Category) e2);

			String categoryId = ((TaskRepository) e1).getProperty(IRepositoryConstants.PROPERTY_CATEGORY);
			Category cat2 = ((TaskRepositoryManager) TasksUi.getRepositoryManager()).getCategory(categoryId);
			int result = cat2.compareTo(cat1);
			if (result == 0) {
				result = 1;
			}
			return result;

		} else if (e1 instanceof TaskRepository && e2 instanceof TaskRepository) {
			String categoryId = ((TaskRepository) e1).getProperty(IRepositoryConstants.PROPERTY_CATEGORY);
			Category cat1 = ((TaskRepositoryManager) TasksUi.getRepositoryManager()).getCategory(categoryId);

			String categoryId2 = ((TaskRepository) e2).getProperty(IRepositoryConstants.PROPERTY_CATEGORY);
			Category cat2 = ((TaskRepositoryManager) TasksUi.getRepositoryManager()).getCategory(categoryId2);

			int result = cat1.compareTo(cat2);
			if (result == 0) {
				return super.compare(viewer, e1, e2);
			} else {
				return result;
			}
		}
		return super.compare(viewer, e1, e2);
	}
}
