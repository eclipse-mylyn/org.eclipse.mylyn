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

package org.eclipse.mylyn.internal.team.ui.history;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.tasks.core.data.TaskHistory;
import org.eclipse.mylyn.tasks.core.data.TaskRevision;

/**
 * @author Steffen Pingel
 */
public class TaskHistoryContentProvider implements ITreeContentProvider {

	private static final Object[] EMPTY_ARRAY = {};

	public TaskHistoryContentProvider() {
	}

	@Override
	public void dispose() {
		// ignore
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof TaskRevision) {
			return ((TaskRevision) parentElement).getChanges().toArray();
		}
		return EMPTY_ARRAY;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof TaskHistory) {
			return ((TaskHistory) inputElement).getRevisions().toArray();
		}
		return EMPTY_ARRAY;
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof TaskRevision) {
			return getChildren(element).length > 1;
		}
		return getChildren(element).length > 0;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}
