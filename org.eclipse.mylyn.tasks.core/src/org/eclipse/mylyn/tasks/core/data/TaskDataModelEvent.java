/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
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

/**
 * @author Steffen Pingel
 * @since 3.0
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
