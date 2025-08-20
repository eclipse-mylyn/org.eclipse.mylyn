/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
import org.eclipse.ui.IWorkingSet;

/**
 * AbstractTaskListFilter for task working sets
 *
 * @author Eugene Kuleshov
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class TaskWorkingSetFilter extends AbstractTaskListFilter {

	private IAdaptable[] elements;

	@Override
	public boolean select(Object parent, Object element) {
		if ((parent instanceof ITask) || (parent == null && element instanceof ScheduledTaskContainer)) {
			return true;
		}

		if (parent == null && element instanceof IRepositoryElement) {
			return isContainedInWorkingSet((IRepositoryElement) element);
		}
		if (parent instanceof ITaskContainer && !(parent instanceof ScheduledTaskContainer)) {
			return isContainedInWorkingSet((IRepositoryElement) parent);
		}
		if (element instanceof LocalTask) {
			for (IRepositoryElement container : ((LocalTask) element).getParentContainers()) {
				return isContainedInWorkingSet(container);
			}
		}
		if (parent instanceof ScheduledTaskContainer && element instanceof ITask) {
			for (IRepositoryElement query : ((AbstractTask) element).getParentContainers()) {
				if (isContainedInWorkingSet(query)) {
					return true;
				}
			}
			return false;
		}
		return true;
	}

	public boolean select(ITask task) {
		for (IRepositoryElement query : ((AbstractTask) task).getParentContainers()) {
			if (isContainedInWorkingSet(query)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean applyToFilteredText() {
		return true;
	}

	private boolean isContainedInWorkingSet(IRepositoryElement element) {
		return isContainedInWorkingSet(element, new HashSet<>());
	}

	private boolean isContainedInWorkingSet(IRepositoryElement container, Set<IRepositoryElement> visited) {
		if (elements == null) {
			return true;
		}

		if (visited.contains(container)) {
			return false;
		}

		visited.add(container);

		boolean seenTaskWorkingSets = false;
		String handleIdentifier = container.getHandleIdentifier();
		for (IAdaptable adaptable : elements) {
			if (adaptable instanceof IRepositoryElement) {
				seenTaskWorkingSets = true;
				if (handleIdentifier.equals(((IRepositoryElement) adaptable).getHandleIdentifier())) {
					return true;
				}

				// handle case of sub tasks (not directly under a category/query)
				if (container instanceof AbstractTask) {
					for (AbstractTaskContainer parent : ((AbstractTask) container).getParentContainers()) {
						if (visited.contains(parent)) {
							continue;
						}
						if (isContainedInWorkingSet(parent, visited)) {
							return true;
						}
					}
				}
			}
		}
		return !seenTaskWorkingSets;
	}

	public boolean updateWorkingSet(IWorkingSet currentWorkingSet) {
		IAdaptable[] newElements = currentWorkingSet.getElements();
		if (!Arrays.equals(elements, newElements)) {
			elements = newElements;
			return true;
		}
		return false;
	}

	public IAdaptable[] getElements() {
		return elements;
	}

}
