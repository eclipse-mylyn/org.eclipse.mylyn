/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core.data;

/**
 * @author Steffen Pingel
 * @since 3.0
 */
public class TaskDataModelEvent {

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
