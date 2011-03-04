/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.ui.AbstractTaskListFilter;
import org.eclipse.mylyn.tasks.core.IAttributeContainer;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;

/**
 * @author Steffen Pingel
 */
public class PresentationFilter extends AbstractTaskListFilter {

	private static PresentationFilter instance = new PresentationFilter();

	private boolean filterHiddenQueries;

	private boolean filterSubtasks;

	private PresentationFilter() {
		setFilterHiddenQueries(true);
		setFilterSubtasks(true);
	}

	public static PresentationFilter getInstance() {
		return instance;
	}

	public boolean isFilterSubtasks() {
		return filterSubtasks;
	}

	public boolean isFilterHiddenQueries() {
		return filterHiddenQueries;
	}

	public void setFilterHiddenQueries(boolean enabled) {
		this.filterHiddenQueries = enabled;
	}

	public void setFilterSubtasks(boolean filterSubtasks) {
		this.filterSubtasks = filterSubtasks;
	}

	@Override
	public boolean select(Object parent, Object element) {
		// filter hidden queries
		if (element instanceof IRepositoryQuery) {
			if (!filterHiddenQueries) {
				return true;
			}
			return isQueryVisible(element);
		}
		// filter sub-tasks not connected to queries or categories
		if (element instanceof AbstractTask) {
			if (!filterSubtasks) {
				return true;
			}
			for (AbstractTaskContainer container : ((AbstractTask) element).getParentContainers()) {
				// categories are always visible
				if (container instanceof AbstractTaskCategory) {
					return true;
				}
				// show task if is contained in a query
				if (container instanceof IRepositoryQuery && (!filterHiddenQueries || isQueryVisible(container))) {
					return true;
				}
			}
			return false;
		}
		return true;
	}

	private boolean isQueryVisible(Object element) {
		return !Boolean.parseBoolean(((IAttributeContainer) element).getAttribute(ITasksCoreConstants.ATTRIBUTE_HIDDEN));
	}

}
