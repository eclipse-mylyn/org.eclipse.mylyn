/*******************************************************************************
 * Copyright (c) 2009, 2012 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.ui.util;

import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.ui.IEditorPart;

/**
 * @author Steffen Pingel
 */
public class TaskOpenEvent {

	private final TaskRepository repository;

	private final ITask task;

	private final String taskId;

	private final boolean inBrowser;

	private final IEditorPart editor;

	public TaskOpenEvent(TaskRepository repository, ITask task, String taskId, IEditorPart editor, boolean inBrowser) {
		this.repository = repository;
		this.task = task;
		this.taskId = taskId;
		this.editor = editor;
		this.inBrowser = inBrowser;
	}

	public boolean isInBrowser() {
		return inBrowser;
	}

	public TaskRepository getRepository() {
		return repository;
	}

	public ITask getTask() {
		return task;
	}

	public String getTaskId() {
		return taskId;
	}

	public IEditorPart getEditor() {
		return editor;
	}

}
