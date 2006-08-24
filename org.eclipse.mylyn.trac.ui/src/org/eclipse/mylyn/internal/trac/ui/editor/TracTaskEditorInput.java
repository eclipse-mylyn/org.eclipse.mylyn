/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.trac.ui.editor;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.eclipse.mylar.internal.tasks.ui.editors.ExistingBugEditorInput;
import org.eclipse.mylar.internal.trac.TracTask;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.TaskRepository;

/**
 * @author Steffen Pingel
 */
public class TracTaskEditorInput extends ExistingBugEditorInput {

	public TracTaskEditorInput(TaskRepository repository, TracTask task) throws IOException, GeneralSecurityException {
		super(repository, task.getTaskData(), AbstractRepositoryTask.getTaskId(task.getHandleIdentifier()));
		// TODO Auto-generated constructor stub
	}

	public String getName() {
		// TODO fix super implementation
		return repositoryTask.getDescription();
	}

}
