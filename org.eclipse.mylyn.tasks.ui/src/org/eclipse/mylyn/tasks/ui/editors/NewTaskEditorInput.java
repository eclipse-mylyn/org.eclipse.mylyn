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
 * @author Rob Elves
 */
public class NewTaskEditorInput extends RepositoryTaskEditorInput {

	public NewTaskEditorInput(TaskRepository repository, RepositoryTaskData taskData) {
		// TODO: should not have taskData.getId() twice
		super(repository, taskData.getId(), "", taskData.getId());
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
