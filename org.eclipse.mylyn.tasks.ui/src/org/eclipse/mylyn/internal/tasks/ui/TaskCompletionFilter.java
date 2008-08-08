/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.ui;

import java.util.Collection;

import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.ITask;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 */
public class TaskCompletionFilter extends AbstractTaskListFilter {

	@Override
	public boolean select(Object parent, Object element) {
		if (element instanceof ITask) {
			ITask task = (ITask) element;
			boolean isComplete = task.isCompleted();
			if (!isComplete) {
				return true;
			} else if (element instanceof AbstractTask) {
				AbstractTask abstractTask = (AbstractTask) element;
				Collection<ITask> children = abstractTask.getChildren();
				for (ITask child : children) {
					if (select(abstractTask, child)) {
						return true;
					}
				}
				return false;
			}

		}
		return true;
	}
}
