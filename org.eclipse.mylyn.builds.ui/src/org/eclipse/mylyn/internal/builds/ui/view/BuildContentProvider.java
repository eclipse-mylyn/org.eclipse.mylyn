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

package org.eclipse.mylyn.internal.builds.ui.view;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.builds.core.IBuildModel;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildServer;

/**
 * @author Steffen Pingel
 */
public class BuildContentProvider implements ITreeContentProvider {

	private static final Object[] EMPTY_ARRAY = new Object[0];

	public void dispose() {
		// ignore
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// ignore
	}

	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof IBuildModel) {
			return ((IBuildModel) inputElement).getServers().toArray();
		}
		return EMPTY_ARRAY;
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IBuildServer) {
			return ((IBuildServer) parentElement).getPlans().toArray();
		} else if (parentElement instanceof IBuildPlan) {
			return ((IBuildPlan) parentElement).getChildren().toArray();
		}
		return EMPTY_ARRAY;
	}

	public Object getParent(Object element) {
		// ignore
		return null;
	}

	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

}
