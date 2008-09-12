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

package org.eclipse.mylyn.tasks.core.data;

/**
 * @author Steffen Pingel
 * @since 3.0
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public final class TaskDataModelEvent {

	public enum EventKind {
		CHANGED
	}

	private final EventKind kind;

	private final TaskDataModel model;

	private final TaskAttribute taskAttribute;;

	public TaskDataModelEvent(TaskDataModel model, EventKind kind, TaskAttribute taskAttribute) {
		this.model = model;
		this.kind = kind;
		this.taskAttribute = taskAttribute;
	}

	public EventKind getKind() {
		return kind;
	}

	public TaskDataModel getModel() {
		return model;
	}

	public TaskAttribute getTaskAttribute() {
		return taskAttribute;
	}

}
