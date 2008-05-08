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
public class TaskRelation {

	private final String taskId;

	private final Kind kind;

	private final Direction direction;

	public enum Direction {
		INWARD, OUTWARD
	};

	public enum Kind {
		CONTAINMENT, DEPENDENCY, DUPLICATE
	}

	private TaskRelation(Kind kind, Direction direction, String taskId) {
		this.kind = kind;
		this.direction = direction;
		this.taskId = taskId;
	}

	public String getTaskId() {
		return taskId;
	}

	public Kind getKind() {
		return kind;
	}

	public Direction getDirection() {
		return direction;
	}

	public static TaskRelation parentTask(String taskId) {
		return new TaskRelation(Kind.CONTAINMENT, Direction.INWARD, taskId);
	}

	public static TaskRelation subtask(String taskId) {
		return new TaskRelation(Kind.CONTAINMENT, Direction.OUTWARD, taskId);
	}

	public static TaskRelation dependency(String taskId, Direction direction) {
		return new TaskRelation(Kind.CONTAINMENT, direction, taskId);
	}

}
