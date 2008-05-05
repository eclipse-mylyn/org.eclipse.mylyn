/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core;

import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.trac.core.model.TracPriority;

/**
 * @author Steffen Pingel
 */
public class TracTask extends AbstractTask {

	public enum Kind {
		DEFECT, ENHANCEMENT, TASK;

		public static Kind fromString(String type) {
			if (type == null) {
				return null;
			}
			if (type.equals("Defect")) {
				return DEFECT;
			}
			if (type.equals("Enhancement")) {
				return ENHANCEMENT;
			}
			if (type.equals("Task")) {
				return TASK;
			}
			return null;
		}

		public static Kind fromType(String type) {
			if (type == null) {
				return null;
			}
			if (type.equals("defect")) {
				return DEFECT;
			}
			if (type.equals("enhancement")) {
				return ENHANCEMENT;
			}
			if (type.equals("task")) {
				return TASK;
			}
			return null;
		}

		@Override
		public String toString() {
			switch (this) {
			case DEFECT:
				return "Defect";
			case ENHANCEMENT:
				return "Enhancement";
			case TASK:
				return "Task";
			default:
				return "";
			}
		}

	}

	public enum Status {
		ASSIGNED, CLOSED, NEW, REOPENED;

		public static Status fromStatus(String status) {
			if (status == null) {
				return null;
			}
			if (status.equals("new")) {
				return NEW;
			}
			if (status.equals("assigned")) {
				return ASSIGNED;
			}
			if (status.equals("reopened")) {
				return REOPENED;
			}
			if (status.equals("closed")) {
				return CLOSED;
			}
			return null;
		}

		public String toStatusString() {
			switch (this) {
			case NEW:
				return "new";
			case ASSIGNED:
				return "assigned";
			case REOPENED:
				return "reopened";
			case CLOSED:
				return "closed";
			default:
				return "";
			}
		}

		@Override
		public String toString() {
			switch (this) {
			case NEW:
				return "New";
			case ASSIGNED:
				return "Assigned";
			case REOPENED:
				return "Reopened";
			case CLOSED:
				return "Closed";
			default:
				return "";
			}
		}

	}

	public enum TracPriorityLevel {
		BLOCKER, CRITICAL, MAJOR, MINOR, TRIVIAL;

		public static TracPriorityLevel fromPriority(String priority) {
			if (priority == null) {
				return null;
			}
			if (priority.equals("blocker")) {
				return BLOCKER;
			}
			if (priority.equals("critical")) {
				return CRITICAL;
			}
			if (priority.equals("major")) {
				return MAJOR;
			}
			if (priority.equals("minor")) {
				return MINOR;
			}
			if (priority.equals("trivial")) {
				return TRIVIAL;
			}
			return null;
		}

		public PriorityLevel toPriorityLevel() {
			switch (this) {
			case BLOCKER:
				return PriorityLevel.P1;
			case CRITICAL:
				return PriorityLevel.P2;
			case MAJOR:
				return PriorityLevel.P3;
			case MINOR:
				return PriorityLevel.P4;
			case TRIVIAL:
				return PriorityLevel.P5;
			default:
				return null;
			}
		}

		@Override
		public String toString() {
			switch (this) {
			case BLOCKER:
				return "blocker";
			case CRITICAL:
				return "critical";
			case MAJOR:
				return "major";
			case MINOR:
				return "minor";
			case TRIVIAL:
				return "trivial";
			default:
				return null;
			}
		}

	}

	private static int TASK_PRIORITY_LEVELS = 5;

	public static PriorityLevel getTaskPriority(String tracPriority) {
		if (tracPriority != null) {
			TracPriorityLevel priority = TracPriorityLevel.fromPriority(tracPriority);
			if (priority != null) {
				return priority.toPriorityLevel();
			}
		}
		return PriorityLevel.getDefault();
	}

	public static PriorityLevel getTaskPriority(String priority, TracPriority[] tracPriorities) {
		if (priority != null && tracPriorities != null && tracPriorities.length > 0) {
			int minValue = tracPriorities[0].getValue();
			int range = tracPriorities[tracPriorities.length - 1].getValue() - minValue;
			for (TracPriority tracPriority : tracPriorities) {
				if (priority.equals(tracPriority.getName())) {
					float relativeValue = (float) (tracPriority.getValue() - minValue) / range;
					int value = (int) (relativeValue * TASK_PRIORITY_LEVELS) + 1;
					return AbstractTask.PriorityLevel.fromLevel(value);
				}
			}
		}
		return getTaskPriority(priority);
	}

	public static boolean isCompleted(String tracStatus) {
		TracTask.Status status = TracTask.Status.fromStatus(tracStatus);
		return status == TracTask.Status.CLOSED;
	}

	private boolean supportsSubtasks = false;

	public TracTask(String repositoryUrl, String id, String label) {
		super(repositoryUrl, id, label);
		setUrl(repositoryUrl + ITracClient.TICKET_URL + id);
	}

	@Override
	public String getConnectorKind() {
		return TracCorePlugin.REPOSITORY_KIND;
	}

	@Override
	public boolean isLocal() {
		return false;
	}

	public boolean getSupportsSubtasks() {
		return supportsSubtasks;
	}

	public void setSupportsSubtasks(boolean supportsSubtasks) {
		this.supportsSubtasks = supportsSubtasks;
	}

}
