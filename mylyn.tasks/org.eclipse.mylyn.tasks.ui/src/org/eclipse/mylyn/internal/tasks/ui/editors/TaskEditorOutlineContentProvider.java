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

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class TaskEditorOutlineContentProvider implements ITreeContentProvider {

	public void dispose() {
		// ignore
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof TaskEditorOutlineNode) {
			Object[] children = ((TaskEditorOutlineNode) parentElement).getChildren();
			return children;
		}
		return new Object[0];
	}

	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof TaskEditorOutlineModel) {
			return new Object[] { ((TaskEditorOutlineModel) inputElement).getRoot() };
		}
		return new Object[0];
	}

	public Object getParent(Object element) {
		if (element instanceof TaskEditorOutlineNode) {
			return ((TaskEditorOutlineNode) element).getParent();
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof TaskEditorOutlineNode) {
			return ((TaskEditorOutlineNode) element).getChildren().length > 0;
		}
		return false;
	}

	public void inputChanged(Viewer viewerChanged, Object oldInput, Object newInput) {
		// ignore
	}

}