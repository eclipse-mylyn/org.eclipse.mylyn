/*******************************************************************************
 * Copyright (c) 2004, 2015 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.ui;

import java.util.Collection;

import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.PresentationFilter;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;

/**
 * Custom filters are used so that the "Find:" filter can 'see through' any filters that may have been applied.
 *
 * @author Mik Kersten
 */
// XXX duplicate implementation in hasDescendantIncoming/hasIncompleteDescendant: consider replacing this by a visitor
public abstract class AbstractTaskListFilter {

	/**
	 * Given an element in the task list to filter against, determines whether or not it should be filtered away or if
	 * it should be kept within view.
	 *
	 * @param parent
	 *            The parent element we are determining filtering rules for.
	 * @param element
	 *            The element we are determining filtering rules for. This is usually some sort of ITask
	 * @return Returns true if the element should not be filtered, otherwise returns false if it should be filtered away
	 *         from the task list.
	 */
	public abstract boolean select(Object parent, Object element);

	/**
	 * @return True if this filter should be applied even with filter text present. Otherwise, returns false.
	 */
	public boolean applyToFilteredText() {
		return false;
	}

	/**
	 * NOTE: performance implication of looking down children TODO: Move to an internal utility class
	 */
	public static boolean hasDescendantIncoming(ITaskContainer container) {
		return hasDescendantIncoming(container, ITasksCoreConstants.MAX_SUBTASK_DEPTH);
	}

	public static boolean hasIncompleteDescendant(ITaskContainer container) {
		return hasIncompleteDescendant(container, ITasksCoreConstants.MAX_SUBTASK_DEPTH);
	}

	/**
	 * Given a container and depth, determines if any of the descendants are incoming. This method will search only up
	 * to the depth provided.
	 *
	 * @param container
	 *            The container of tasks to check against
	 * @param depth
	 *            The maximum amount of the depth to search. For example, 0 is a shallow search, while a 1 would search
	 *            at level one more level deeper.
	 * @return
	 */
	private static boolean hasDescendantIncoming(ITaskContainer container, int depth) {
		Collection<ITask> children = container.getChildren();
		if (children == null || depth <= 0) {
			return false;
		}

		if (!PresentationFilter.getInstance().select(null, container)) {
			return false;
		}

		for (ITask task : children) {
			// FIXME this does not take the patch of container into account and hence may show an incoming although there is none
			if (task != null && PresentationFilter.getInstance().select(container, task)) {
				if (TasksUiInternal.shouldShowIncoming(task)) {
					return true;
				} else if (TasksUiPlugin.getDefault().groupSubtasks(container) && task instanceof ITaskContainer
						&& hasDescendantIncoming((ITaskContainer) task, depth - 1)) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean hasIncompleteDescendant(ITaskContainer container, int depth) {
		Collection<ITask> children = container.getChildren();
		if (children == null || depth <= 0) {
			return false;
		}

		for (ITask task : children) {
			if (task != null) {
				ITask containedRepositoryTask = task;
				if (!containedRepositoryTask.isCompleted()) {
					return true;
				} else
					if (task instanceof ITaskContainer && hasIncompleteDescendant((ITaskContainer) task, depth - 1)) {
					return true;
				}
			}
		}
		return false;
	}

}
