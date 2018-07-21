/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.ui.search;

import java.text.MessageFormat;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylyn.internal.tasks.core.Person;
import org.eclipse.mylyn.internal.tasks.core.TaskGroup;
import org.eclipse.mylyn.tasks.ui.TaskElementLabelProvider;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class SearchResultsLabelProvider extends TaskElementLabelProvider {

	private final SearchResultContentProvider contentProvider;

	private final TreeViewer viewer;

	public SearchResultsLabelProvider(SearchResultContentProvider contentProvider, TreeViewer viewer) {
		super(true);
		this.contentProvider = contentProvider;
		this.viewer = viewer;
	}

	@Override
	public String getText(Object object) {
		if (object instanceof TaskGroup || object instanceof Person) {
			Object[] children = contentProvider.getChildren(object);
			ViewerFilter[] filters = viewer.getFilters();
			int filtered = 0;
			if (filters.length > 0) {
				for (Object child : children) {
					for (ViewerFilter filter : filters) {
						if (!filter.select(viewer, object, child)) {
							filtered++;
							break; //don't count a child more the once
						}
					}
				}
			}
			if (filtered > 0) {
				return super.getText(object)
						+ " (" //$NON-NLS-1$ 
						+ MessageFormat.format(Messages.SearchResultsLabelProvider_OF, (children.length - filtered),
								children.length) + ")"; //$NON-NLS-1$ 
			} else {
				return super.getText(object) + " (" + children.length + ")"; //$NON-NLS-1$ //$NON-NLS-2$
			}
		} else {
			return super.getText(object);
		}
	}

}
