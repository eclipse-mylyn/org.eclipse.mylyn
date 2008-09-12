/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.deprecated;

import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @deprecated Do not use. This class is pending for removal: see bug 237552.
 */
@Deprecated
public class RepositoryTaskEditorInput extends AbstractRepositoryTaskEditorInput {

	protected String taskId;

	protected String url;

	protected AbstractTask repositoryTask = null;

	public RepositoryTaskEditorInput(TaskRepository repository, String taskId, String taskUrl) {
		super(repository, taskId);
		this.taskId = taskId;
		this.url = taskUrl;
		AbstractTask task = (AbstractTask) TasksUiInternal.getTaskList().getTask(repository.getRepositoryUrl(), taskId);
		if (task != null) {
			this.repositoryTask = task;
		}
	}

	public AbstractTask getRepositoryTask() {
		return repositoryTask;
	}

	public String getName() {
		if (repositoryTask != null) {
			String idLabel = repositoryTask.getTaskKey();

			String label = "";
			if (idLabel != null) {
				label += idLabel + ": ";
			}
			label += repositoryTask.getSummary();
			return label;
		} else if (getTaskData() != null && getTaskData().getLabel() != null) {
			return getTaskData().getTaskKey() + ": " + getTaskData().getLabel();
		} else if (taskId != null) {
			return taskId;
		} else {
			return "<unknown>";
		}
	}

	/**
	 * @return The taskId of the bug for this editor input.
	 */
	public String getId() {
		return taskId;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((repositoryTask == null) ? 0 : repositoryTask.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final RepositoryTaskEditorInput other = (RepositoryTaskEditorInput) obj;
		if (repositoryTask == null) {
			if (other.repositoryTask != null) {
				return false;
			} else if (!other.getId().equals(this.getId())) {
				return false;
			}
		} else if (!repositoryTask.equals(other.repositoryTask)) {
			return false;
		}
		return true;
	}

	/**
	 * @return url for the repositoryTask/hit. Used by TaskEditor when opening browser
	 */
	public String getUrl() {
		return url;
	}

}
