/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.ui.editor;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineItem;

/**
 *
 *
 * @author David Green
 */
class OutlineContentProvider implements ITreeContentProvider {

	private static final Object[] NO_CHILDREN = new Object[0];


	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof OutlineItem) {
			return ((OutlineItem)parentElement).getChildren().toArray();
		}
		return NO_CHILDREN;
	}

	public Object getParent(Object element) {
		if (element instanceof OutlineItem) {
			((OutlineItem)element).getParent();
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof OutlineItem) {
			return !((OutlineItem)element).getChildren().isEmpty();
		}

		return getChildren(element).length > 0;
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

}
