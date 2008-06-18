/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.deprecated;

import java.util.Collections;
import java.util.Set;

import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskAttribute;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @deprecated Do not use. This class is pending for removal: see bug 237552.
 */
@Deprecated
public class NewTaskEditorInput extends RepositoryTaskEditorInput {

	public NewTaskEditorInput(TaskRepository repository, RepositoryTaskData taskData) {
		super(repository, taskData.getTaskId(), "");
		setOldTaskData(taskData);
		Set<RepositoryTaskAttribute> edits = Collections.emptySet();
		setOldEdits(edits);
		setEditableTaskData(taskData);
	}

	@Override
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

	@Override
	public void refreshInput() {
		// does nothing
	}

}
