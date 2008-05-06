/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.ui;

import java.util.Arrays;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskElement;
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
		if (parent instanceof ITask) {
			return true;
		}

		if (parent == null && element instanceof ScheduledTaskContainer) {
			return true;
		}

		if (parent == null && element instanceof ITaskElement) {
			return isContainedInWorkingSet((ITaskElement) element);
		}
		if (parent instanceof ITaskElement && !(parent instanceof ScheduledTaskContainer)) {
			return isContainedInWorkingSet((ITaskElement) parent);
		}
		if (element instanceof LocalTask) {
			for (ITaskElement container : ((LocalTask) element).getParentContainers()) {
				return isContainedInWorkingSet(container);
			}
		}
		if (parent instanceof ScheduledTaskContainer && element instanceof ITask) {
			for (ITaskElement query : ((AbstractTask) element).getParentContainers()) {
				if (isContainedInWorkingSet(query)) {
					return true;
				}
			}
			return false;
		}
		return true;
	}

	private boolean isContainedInWorkingSet(ITaskElement container) {
		if (elements == null) {
			return true;
		}

		boolean seenTaskWorkingSets = false;
		String handleIdentifier = container.getHandleIdentifier();
		for (IAdaptable adaptable : elements) {
			if (adaptable instanceof ITaskElement) {
				seenTaskWorkingSets = true;
				if (handleIdentifier.equals(((ITaskElement) adaptable).getHandleIdentifier())) {
					return true;
				}
			}
		}
		return !seenTaskWorkingSets;
	}

	public boolean updateWorkingSet(IWorkingSet currentWorkingSet) {
		IAdaptable[] newElements = currentWorkingSet.getElements();
		if (!Arrays.equals(this.elements, newElements)) {
			this.elements = newElements;
			return true;
		}
		return false;
	}

	public IAdaptable[] getElements() {
		return elements;
	}

}
