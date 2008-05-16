/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.search;

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
						}
					}
				}
			}
			if (filtered > 0) {
				return super.getText(object) + " (" + (children.length - filtered) + " of " + children.length + ")";
			} else {
				return super.getText(object) + " (" + children.length + ")";
			}
		} else {
			return super.getText(object);
		}
	}

}
