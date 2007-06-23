/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui.editors;

import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * Provides input to editors with unsubmitted tasks (i.e., those that do not yet exist on the repository).
 * 
 * @author Rob Elves
 * @since 2.0
 */
public class NewTaskEditorInput extends RepositoryTaskEditorInput {

	public NewTaskEditorInput(TaskRepository repository, RepositoryTaskData taskData) {
		super(repository, taskData.getId(), "");
		super.setOldTaskData(taskData);
		super.setEditableTaskData(taskData);
	}

	public String getName() {
		return this.toolTipText;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof NewTaskEditorInput) {
			NewTaskEditorInput input = (NewTaskEditorInput) o;
			return input.getTaskData().equals(this.getTaskData());
		}
		return false;
	}

}
