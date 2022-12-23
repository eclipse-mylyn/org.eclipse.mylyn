/*******************************************************************************
 * Copyright (c) 2012 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Research Group for Industrial Software (INSO), Vienna University of Technology - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.versions.tasks.ui.spi;

import java.util.List;

import org.eclipse.mylyn.versions.tasks.core.TaskChangeSet;

/**
 * Model of a list of Task Changesets. Can optionally include sub-tasks
 *
 * @author Kilian Matt
 */
public interface ITaskVersionsModel {

	/**
	 * Switch, whether sub-tasks of the task should be include in the task
	 * changeset mapping.
	 *
	 * @param includeSubTasks
	 */
	public void setIncludeSubTasks(boolean includeSubTasks);

	/**
	 * Returns a list of TaskChangeSet objects, for each changeset of the task
	 * (or a subtask)
	 *
	 * @return a non-null List
	 */
	public List<TaskChangeSet> getInput();
}
