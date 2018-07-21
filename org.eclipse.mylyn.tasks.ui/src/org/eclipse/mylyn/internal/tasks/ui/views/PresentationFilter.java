/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.ui.views;

import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.internal.tasks.ui.AbstractTaskListFilter;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
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

	/**
	 * @noreference This method is not intended to be referenced by clients.
	 */
	PresentationFilter() {
		updateSettings();
	}

	public boolean isFilterHiddenQueries() {
		return filterHiddenQueries;
	}

	public boolean isFilterNonMatching() {
		return filterNonMatching;
	}

	@Override
	public boolean select(Object parent, Object element) {
		if (element instanceof IRepositoryQuery) {
			return selectQuery((IRepositoryQuery) element);
		}
		// only filter repository tasks
		if (element instanceof TaskTask) {
			return selectTask(parent, (TaskTask) element);
		}
		return true;
	}

	private boolean selectQuery(IRepositoryQuery query) {
		if (!filterHiddenQueries) {
			return true;
		}
		return !Boolean.parseBoolean(query.getAttribute(ITasksCoreConstants.ATTRIBUTE_HIDDEN));
	}

	private boolean selectTask(Object parent, TaskTask task) {
		if (!filterNonMatching) {
			return true;
		}
		// tasks matching a query or category should be included
		if (isInVisibleQuery(task)) {
			return true;
		}
		// explicitly scheduled subtasks should be shown in those containers
		if (parent != null && parent.getClass().equals(ScheduledTaskContainer.class)) {
			return true;
		}

		return false;
	}

	public void setFilterHiddenQueries(boolean enabled) {
		this.filterHiddenQueries = enabled;
	}

	public void setFilterNonMatching(boolean filterSubtasks) {
		this.filterNonMatching = filterSubtasks;
	}

	public void updateSettings() {
		setFilterHiddenQueries(
				TasksUiPlugin.getDefault().getPreferenceStore().getBoolean(ITasksUiPreferenceConstants.FILTER_HIDDEN));
		setFilterNonMatching(TasksUiPlugin.getDefault()
				.getPreferenceStore()
				.getBoolean(ITasksUiPreferenceConstants.FILTER_NON_MATCHING));
	}

	public boolean isInVisibleQuery(ITask task) {
		for (AbstractTaskContainer container : ((AbstractTask) task).getParentContainers()) {
			// categories and local subtasks are always visible
			if (container instanceof AbstractTaskCategory) {
				return true;
			}
			// show task if is contained in a query
			if (container instanceof IRepositoryQuery && selectQuery((IRepositoryQuery) container)) {
				return true;
			}
		}
		return false;
	}
}
