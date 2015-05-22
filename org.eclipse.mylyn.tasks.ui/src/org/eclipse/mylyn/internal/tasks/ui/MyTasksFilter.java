/*******************************************************************************
 * Copyright (c) 2013, 2015 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;

public class MyTasksFilter extends AbstractTaskListFilter {

	public MyTasksFilter() {
		// ignore
	}

	@Override
	public boolean select(Object parent, Object element) {
		if (element instanceof LocalTask) {
			return true;
		}
		if (element instanceof AbstractTask) {
			AbstractTask task = (AbstractTask) element;
			TaskRepository repository = TasksUi.getRepositoryManager().getRepository(task.getConnectorKind(),
					task.getRepositoryUrl());
			if (repository != null) {
				return TasksUi.getRepositoryConnector(task.getConnectorKind()).isOwnedByUser(repository, task);
			}
			return false;
		}
		return true;
	}

}
