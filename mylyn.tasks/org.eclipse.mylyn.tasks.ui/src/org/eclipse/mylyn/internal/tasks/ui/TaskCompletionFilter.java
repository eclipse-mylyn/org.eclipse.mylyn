/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
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

import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.ui.views.PresentationFilter;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskScheduleContentProvider.Completed;
import org.eclipse.mylyn.tasks.core.ITask;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 */
public class TaskCompletionFilter extends AbstractTaskListFilter {

	@Override
	public boolean select(Object parent, Object element) {
		if (element instanceof AbstractTask) {
			AbstractTask task = (AbstractTask) element;
			if (task.isCompleted()) {
				Collection<ITask> children = task.getChildren();
				for (ITask child : children) {
					if (PresentationFilter.getInstance().select(element, child) && select(element, child)) {
						return true;
					}
				}
				// hide completed task
				return false;
			}
		}
		// hide the completed category when not showing completed tasks
		if (element instanceof Completed) {
			return false;
		}
		return true;
	}

}
