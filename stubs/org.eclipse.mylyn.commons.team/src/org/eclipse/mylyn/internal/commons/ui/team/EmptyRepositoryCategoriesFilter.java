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

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylyn.commons.repositories.RepositoryCategory;

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
			return ((ITreeContentProvider) ((StructuredViewer) viewer).getContentProvider()).getChildren(element).length > 0;
		}
		return true;
	}
}
