/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.ui;

import java.util.Collection;

import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask.SynchronizationState;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskElement;

/**
 * Custom filters are used so that the "Find:" filter can 'see through' any filters that may have been applied.
 * 
 * @author Mik Kersten
 */
//API 3.0 duplicate implementation in hasDescendantIncoming/hasIncompleteDescendant: consider replacing this by a visitor 
public abstract class AbstractTaskListFilter {

	public abstract boolean select(Object parent, Object element);

	/**
	 * NOTE: performance implication of looking down children
	 * 
	 * TODO: Move to an internal utility class
	 */
	public static boolean hasDescendantIncoming(ITaskElement container) {
		return hasDescendantIncoming(container, ITasksCoreConstants.MAX_SUBTASK_DEPTH);
	}

	public static boolean hasIncompleteDescendant(ITaskElement container) {
		return hasIncompleteDescendant(container, ITasksCoreConstants.MAX_SUBTASK_DEPTH);
	}

	private static boolean hasDescendantIncoming(ITaskElement container, int depth) {
		Collection<ITask> children = container.getChildren();
		if (children == null || depth <= 0) {
			return false;
		}

		for (ITask task : children) {
			if (task != null) {
				ITask containedRepositoryTask = task;
				if (containedRepositoryTask.getSynchronizationState() == SynchronizationState.INCOMING) {
					return true;
				} else if (TasksUiPlugin.getDefault().groupSubtasks(container)
						&& hasDescendantIncoming(task, depth - 1)) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean hasIncompleteDescendant(ITaskElement container, int depth) {
		Collection<ITask> children = container.getChildren();
		if (children == null || depth <= 0) {
			return false;
		}

		for (ITask task : children) {
			if (task != null) {
				ITask containedRepositoryTask = task;
				if (!containedRepositoryTask.isCompleted()) {
					return true;
				} else if (hasIncompleteDescendant(task, depth - 1)) {
					return true;
				}
			}
		}
		return false;
	}

}
