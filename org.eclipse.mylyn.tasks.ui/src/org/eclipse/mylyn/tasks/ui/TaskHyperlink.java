/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.tasks.ui;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * Immutable. Encapsulates information for linking to tasks from text.
 * 
 * @author Eugene Kuleshov
 * @author Steffen Pingel
 */
public final class TaskHyperlink implements IHyperlink {

	private final IRegion region;

	private final TaskRepository repository;

	private final String taskId;

	public TaskHyperlink(IRegion region, TaskRepository repository, String taskId) {
		this.region = region;
		this.repository = repository;
		this.taskId = taskId;
	}

	public IRegion getHyperlinkRegion() {
		return region;
	}

	public String getTaskId() {
		return taskId;
	}

	public String getTypeLabel() {
		return null;
	}

	/**
	 * @Since 2.1
	 * @return
	 */
	public TaskRepository getRepository() {
		return repository;
	}

	public String getHyperlinkText() {
		return "Open Task " + taskId;
	}

	public void open() {
		if (repository != null) {
			TasksUiUtil.openRepositoryTask(repository, taskId);
		} else {
			MessageDialog.openError(null, "Mylyn", "Could not determine repository for report");
		}
	}

}
