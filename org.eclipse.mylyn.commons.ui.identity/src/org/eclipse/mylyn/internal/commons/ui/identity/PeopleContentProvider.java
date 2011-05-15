/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.ui.identity;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;

/**
 * @author Steffen Pingel
 */
public class PeopleContentProvider implements ITreeContentProvider {

	public PeopleContentProvider() {
	}

	public void dispose() {
		// ignore

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// ignore

	}

	public Object[] getElements(Object inputElement) {
		return TasksUiPlugin.getDefault().getIdentityModel().getIdentities();
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
