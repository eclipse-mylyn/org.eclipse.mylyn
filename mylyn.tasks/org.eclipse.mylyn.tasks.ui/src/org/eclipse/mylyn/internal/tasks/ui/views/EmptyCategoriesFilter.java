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
