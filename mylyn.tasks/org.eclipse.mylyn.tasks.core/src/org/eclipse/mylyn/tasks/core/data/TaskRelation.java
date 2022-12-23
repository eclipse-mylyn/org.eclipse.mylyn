/*******************************************************************************
 * Copyright (c) 2004, 2014 Tasktop Technologies and others.
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

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.internal.tasks.core.Messages;

/**
 * @author Steffen Pingel
 * @since 3.0
 */
public class TaskRelation {

	private final String taskId;

	private final Kind kind;

	private final Direction direction;

	/**
	 * @since 3.0
	 */
	public enum Direction {
		INWARD, OUTWARD
	};

	/**
	 * @since 3.0
	 */
	public enum Kind {
		CONTAINMENT, DEPENDENCY, DUPLICATE
	}

	private TaskRelation(Kind kind, Direction direction, String taskId) {
		Assert.isNotNull(kind);
		Assert.isNotNull(direction);
		Assert.isNotNull(taskId);
		this.kind = kind;
		this.direction = direction;
		this.taskId = taskId;
	}

	/**
	 * @since 3.0
	 */
	public String getTaskId() {
		return taskId;
	}

	/**
	 * @since 3.0
	 */
	public Kind getKind() {
		return kind;
	}

	/**
	 * @since 3.0
	 */
	public Direction getDirection() {
		return direction;
	}

	/**
	 * @since 3.0
	 */
	public static TaskRelation parentTask(String taskId) {
		return new TaskRelation(Kind.CONTAINMENT, Direction.INWARD, taskId);
	}

	/**
	 * @since 3.0
	 */
	public static TaskRelation subtask(String taskId) {
		return new TaskRelation(Kind.CONTAINMENT, Direction.OUTWARD, taskId);
	}

	/**
	 * @since 3.0
	 */
	public static TaskRelation dependency(String taskId, Direction direction) {
		return new TaskRelation(Kind.DEPENDENCY, direction, taskId);
	}

	/**
	 * @since 3.12
	 */
	public boolean isParentRelation() {
		return kind == Kind.CONTAINMENT && direction == Direction.INWARD;
	}

	/**
	 * @since 3.12
	 */
	public boolean isChildRelation() {
		return kind == Kind.CONTAINMENT && direction == Direction.OUTWARD;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + direction.hashCode();
		result = prime * result + kind.hashCode();
		result = prime * result + taskId.hashCode();
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
		TaskRelation other = (TaskRelation) obj;
		return direction.equals(other.direction) && kind.equals(other.kind) && taskId.equals(other.taskId);
	}

	@Override
	public String toString() {
		if (isParentRelation()) {
			return Messages.TaskRelation_Parent;
		} else if (isChildRelation()) {
			return Messages.TaskRelation_Subtask;
		} else {
			return Messages.TaskRelation_Dependency;
		}
	}

}
