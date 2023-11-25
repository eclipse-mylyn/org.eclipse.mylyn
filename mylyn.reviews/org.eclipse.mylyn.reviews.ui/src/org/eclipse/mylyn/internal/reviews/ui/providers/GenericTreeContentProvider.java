/*******************************************************************************
 * Copyright (c) 2012, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.providers;

import java.util.Collection;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Provides common features for tree content providers including support for gathering children of collections
 * efficiently.
 * 
 * @author Miles Parker
 */
public abstract class GenericTreeContentProvider implements ITreeContentProvider {

	@Override
	public Object[] getChildren(Object parentElement) {
		return getElements(parentElement);
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	protected Object[] getCollectionChildren(Object element) {
		if (element instanceof Collection) {
			Collection<?> collection = (Collection<?>) element;
			Object[] elements = {};
			for (Object member : collection) {
				elements = ArrayUtils.addAll(elements, getElements(member));
			}
			return elements;
		}
		return new Object[0];
	}

	/**
	 * Optimization over getCollectionChildren() as we don't have to tour each member.
	 */
	protected boolean hasCollectionChildren(Object element) {
		if (element instanceof Collection) {
			Collection<?> collection = (Collection<?>) element;
			for (Object member : collection) {
				if (hasChildren(member)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}
