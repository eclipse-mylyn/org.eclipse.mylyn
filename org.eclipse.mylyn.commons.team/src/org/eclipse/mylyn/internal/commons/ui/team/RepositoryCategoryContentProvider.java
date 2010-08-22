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

package org.eclipse.mylyn.internal.commons.ui.team;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.commons.repositories.RepositoryCategory;

public class RepositoryCategoryContentProvider implements ITreeContentProvider {

	private static final Map<String, RepositoryCategory> repositoryCategories = new HashMap<String, RepositoryCategory>();

	public RepositoryCategoryContentProvider() {
		RepositoryCategory catTasks = new RepositoryCategory(RepositoryCategory.ID_CATEGORY_TASKS, "Tasks", 0); //$NON-NLS-1$
		repositoryCategories.put(catTasks.getId(), catTasks);
		RepositoryCategory catBugs = new RepositoryCategory(RepositoryCategory.ID_CATEGORY_BUGS, "Bugs", 100); //$NON-NLS-1$
		repositoryCategories.put(catBugs.getId(), catBugs);
		RepositoryCategory catBuild = new RepositoryCategory(RepositoryCategory.ID_CATEGORY_BUILDS, "Builds", 200); //$NON-NLS-1$
		repositoryCategories.put(catBuild.getId(), catBuild);
		RepositoryCategory catReview = new RepositoryCategory(RepositoryCategory.ID_CATEGORY_REVIEWS, "Reviews", 300); //$NON-NLS-1$
		repositoryCategories.put(catReview.getId(), catReview);
		RepositoryCategory catOther = new RepositoryCategory(RepositoryCategory.ID_CATEGORY_OTHER, "Other", 400); //$NON-NLS-1$
		repositoryCategories.put(catOther.getId(), catOther);
	}

	public void dispose() {
		// ignore

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// ignore

	}

	public Object[] getElements(Object inputElement) {
		return repositoryCategories.values().toArray();
	}

	public Object[] getChildren(Object parentElement) {
		// ignore
		return null;
	}

	public Object getParent(Object element) {
		// ignore
		return null;
	}

	public boolean hasChildren(Object element) {
		// ignore
		return false;
	}

}
