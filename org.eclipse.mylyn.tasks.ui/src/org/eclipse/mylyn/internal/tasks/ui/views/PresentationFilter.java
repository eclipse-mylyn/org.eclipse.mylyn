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
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.internal.tasks.ui.AbstractTaskListFilter;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.IAttributeContainer;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;

/**
 * @author Steffen Pingel
 */
public class PresentationFilter extends AbstractTaskListFilter {

	private static PresentationFilter instance = new PresentationFilter();

	public static PresentationFilter getInstance() {
		return instance;
	}

	private boolean filterHiddenQueries;

	private boolean filterNonMatching;

	private PresentationFilter() {
		updateSettings();
	}

	public boolean isFilterHiddenQueries() {
		return filterHiddenQueries;
	}

	public boolean isFilterNonMatching() {
		return filterNonMatching;
	}

	public boolean select(Object element) {
		// filter hidden queries
		if (element instanceof IRepositoryQuery) {
			if (!filterHiddenQueries) {
				return true;
			}
			return isQueryVisible(element);
		}
		// filter repository sub-tasks not connected to queries or categories
		if (element instanceof TaskTask) {
			if (!filterNonMatching) {
				return true;
			}
			for (AbstractTaskContainer container : ((AbstractTask) element).getParentContainers()) {
				// categories and local subtasks are always visible
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

	@Override
	public boolean select(Object parent, Object element) {
		return select(element);
	}

	public void setFilterHiddenQueries(boolean enabled) {
		this.filterHiddenQueries = enabled;
	}

	public void setFilterNonMatching(boolean filterSubtasks) {
		this.filterNonMatching = filterSubtasks;
	}

	public void updateSettings() {
		setFilterHiddenQueries(TasksUiPlugin.getDefault()
				.getPreferenceStore()
				.getBoolean(ITasksUiPreferenceConstants.FILTER_HIDDEN));
		setFilterNonMatching(TasksUiPlugin.getDefault()
				.getPreferenceStore()
				.getBoolean(ITasksUiPreferenceConstants.FILTER_NON_MATCHING));
	}

	private boolean isQueryVisible(Object element) {
		return !Boolean.parseBoolean(((IAttributeContainer) element).getAttribute(ITasksCoreConstants.ATTRIBUTE_HIDDEN));
	}

	public boolean isInVisibleQuery(ITask task) {
		for (AbstractTaskContainer container : ((AbstractTask) task).getParentContainers()) {
			// categories and local subtasks are always visible
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

}
