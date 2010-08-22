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

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylyn.internal.tasks.core.Category;

/**
 * @author Robert Elves
 */
public class EmptyCategoriesFilter extends ViewerFilter {

	private final TaskRepositoriesContentProvider provider;

	public EmptyCategoriesFilter(TaskRepositoriesContentProvider provider) {
		this.provider = provider;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof Category) {
			return provider.getChildren(element).length > 0;
		}
		return true;
	}
}
