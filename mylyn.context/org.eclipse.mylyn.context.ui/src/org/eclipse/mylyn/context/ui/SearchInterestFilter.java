/*******************************************************************************
 * Copyright (c) 2017 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.context.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.search.internal.ui.text.LineElement;

/**
 * @since 3.23
 */
public class SearchInterestFilter extends InterestFilter {

	@SuppressWarnings("restriction")
	@Override
	public boolean select(Viewer viewer, Object parent, Object object) {
		//Only filter out entire projects. All search results
		//within a project that is in the current context will appear.
		IProject project = null;
		if (object instanceof IResource) {
			project = ((IResource) object).getProject();
		} else if (object instanceof IJavaElement) {
			project = ((IJavaElement) object).getJavaProject().getProject();
		} else if (object instanceof LineElement) {
			project = ((LineElement) object).getParent().getProject();
		} else {
			//Include unknown elements in search results
			return true;
		}
		return super.select(viewer, parent, project);
	}

}
