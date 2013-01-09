/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.providers;

import java.util.Collection;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Provides common features for tree content providers including support for gathering children of collections
 * efficiently.
 * 
 * @author Miles Parker
 */
public abstract class GenericTreeContentProvider implements ITreeContentProvider {

	public Object[] getChildren(Object parentElement) {
		return getElements(parentElement);
	}

	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	protected Object[] getCollectionChildren(Object element) {
		if (element instanceof Collection) {
			Collection<?> collection = (Collection<?>) element;
			Object[] elements = new Object[] {};
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

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}
