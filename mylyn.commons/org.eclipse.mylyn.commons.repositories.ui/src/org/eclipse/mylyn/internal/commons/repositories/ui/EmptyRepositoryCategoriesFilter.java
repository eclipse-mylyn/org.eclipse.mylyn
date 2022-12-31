/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.repositories.ui;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylyn.commons.repositories.core.RepositoryCategory;

/**
 * @author Robert Elves
 * @author Steffen Pingel
 */
public class EmptyRepositoryCategoriesFilter extends ViewerFilter {

	public EmptyRepositoryCategoriesFilter() {
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof RepositoryCategory) {
			return ((ITreeContentProvider) ((StructuredViewer) viewer).getContentProvider())
					.getChildren(element).length > 0;
		}
		return true;
	}
}
