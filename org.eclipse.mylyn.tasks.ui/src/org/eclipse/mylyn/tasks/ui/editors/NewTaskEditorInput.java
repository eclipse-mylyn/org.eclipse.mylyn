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

package org.eclipse.mylar.tasks.ui.editors;

import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskRepository;

/**
 * @author Rob Elves
 */
public class NewTaskEditorInput extends AbstractTaskEditorInput {

	public NewTaskEditorInput(TaskRepository repository, RepositoryTaskData taskData) {
		super(repository, null);
		super.setNewTaskData(taskData);
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
