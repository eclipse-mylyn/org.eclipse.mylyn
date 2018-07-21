/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.core.data;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * A model that describes the history of changes to a task.
 * 
 * @author Steffen Pingel
 * @since 3.6
 */
public class TaskHistory {

	private final TaskRepository repository;

	private final List<TaskRevision> revisions;

	private final ITask task;

	public TaskHistory(TaskRepository repository, ITask task) {
		Assert.isNotNull(repository);
		Assert.isNotNull(task);
		this.repository = repository;
		this.task = task;
		this.revisions = new ArrayList<TaskRevision>();
	}

	public void add(TaskRevision entry) {
		revisions.add(entry);
	}

	public TaskRepository getRepository() {
		return repository;
	}

	public List<TaskRevision> getRevisions() {
		return new ArrayList<TaskRevision>(revisions);
	}

	/**
	 * Returns a specific revision for this task history.
	 * 
	 * @param id
	 *            the id of the revision
	 * @return the revision matching <code>id</code> or null, if no matching revision was found
	 * @see TaskRevision#getId()
	 */
	public TaskRevision getRevision(String id) {
		for (TaskRevision revision : revisions) {
			if (revision.getId().equals(id)) {
				return revision;
			}
		}
		return null;
	}

	public ITask getTask() {
		return task;
	}

	public void remove(TaskRevision entry) {
		revisions.remove(entry);
	}

}
