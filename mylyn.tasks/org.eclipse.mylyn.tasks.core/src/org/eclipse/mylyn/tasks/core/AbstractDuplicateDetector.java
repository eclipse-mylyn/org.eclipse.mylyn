/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
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
		connectorKind = kind;
	}

	public String getName() {
		return name;
	}

	public String getConnectorKind() {
		return connectorKind;
	}

	public boolean canQuery(TaskData taskData) {
		return true;
	}

}
