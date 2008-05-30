/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * Extend to provide task duplicate detection facilities to the task editor (e.g. Java stack trace matching).
 * 
 * @author Gail Murphy
 * @author Robert Elves
 * @author Steffen Pingel
 * @since 3.0
 */
public abstract class AbstractDuplicateDetector {

	private String name;

	private String connectorKind;

	public abstract IRepositoryQuery getDuplicatesQuery(TaskRepository repository, TaskData taskData)
			throws CoreException;

	public void setName(String name) {
		this.name = name;
	}

	public void setConnectorKind(String kind) {
		this.connectorKind = kind;
	}

	public String getName() {
		return this.name;
	}

	public String getConnectorKind() {
		return this.connectorKind;
	}

	public boolean canQuery(TaskData taskData) {
		return true;
	}

}
