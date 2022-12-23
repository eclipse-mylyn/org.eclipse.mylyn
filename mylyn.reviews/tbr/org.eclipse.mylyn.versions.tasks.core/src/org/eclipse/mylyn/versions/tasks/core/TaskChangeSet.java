/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Research Group for Industrial Software (INSO), Vienna University of Technology - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.versions.tasks.core;

import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.versions.core.ChangeSet;

/**
 * 
 * @author Kilian Matt
 *
 */
public class TaskChangeSet {
	private ChangeSet changeset;
	private ITask task;

	public TaskChangeSet(ITask task, ChangeSet cs) {
		this.task = task;
		this.changeset = cs;
	}

	public ChangeSet getChangeset() {
		return changeset;
	}

	public ITask getTask() {
		return task;
	}
}
