/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core;

import org.eclipse.mylyn.tasks.core.AbstractRepositoryTask;

/**
 * @author Steffen Pingel
 */
public class TracTask extends AbstractRepositoryTask {

	public enum PriorityLevel {
		BLOCKER, CRITICAL, MAJOR, MINOR, TRIVIAL;

		@Override
		public String toString() {
			switch (this) {
			case BLOCKER:
				return "P1";
			case CRITICAL:
				return "P2";
			case MAJOR:
				return "P3";
			case MINOR:
				return "P4";
			case TRIVIAL:
				return "P5";
			default:
				return "P5";
			}
		}

		public static PriorityLevel fromPriority(String priority) {
			if (priority == null)
				return null;
			if (priority.equals("blocker"))
				return BLOCKER;
			if (priority.equals("critical"))
				return CRITICAL;
			if (priority.equals("major"))
				return MAJOR;
			if (priority.equals("minor"))
				return MINOR;
			if (priority.equals("trivial"))
				return TRIVIAL;
			return null;
		}
	}

	public enum Kind {
		DEFECT, ENHANCEMENT, TASK;

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

		public static Kind fromType(String type) {
			if (type == null)
				return null;
			if (type.equals("defect"))
				return DEFECT;
			if (type.equals("enhancement"))
				return ENHANCEMENT;
			if (type.equals("task"))
				return TASK;
			return null;
		}

		public static Kind fromString(String type) {
			if (type == null)
				return null;
			if (type.equals("Defect"))
				return DEFECT;
			if (type.equals("Enhancement"))
				return ENHANCEMENT;
			if (type.equals("Task"))
				return TASK;
			return null;
		}

	}

	public enum Status {
		NEW, ASSIGNED, REOPENED, CLOSED;

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

		public static Status fromStatus(String status) {
			if (status == null)
				return null;
			if (status.equals("new"))
				return NEW;
			if (status.equals("assigned"))
				return ASSIGNED;
			if (status.equals("reopened"))
				return REOPENED;
			if (status.equals("closed"))
				return CLOSED;
			return null;
		}

	}

	public TracTask(String repositoryUrl, String id, String label) {
		super(repositoryUrl, id, label);
		setTaskUrl(repositoryUrl + ITracClient.TICKET_URL + id);
	}

	@Override
	public String getRepositoryKind() {
		return TracCorePlugin.REPOSITORY_KIND;
	}

	// TODO use priority attributes from repository instead of hard coded enum
	public static String getMylarPriority(String tracPriority) {
		if (tracPriority != null) {
			PriorityLevel priority = PriorityLevel.fromPriority(tracPriority);
			if (priority != null) {
				return priority.toString();
			}
		}
		return AbstractRepositoryTask.PriorityLevel.P3.toString();
	}

	public static boolean isCompleted(String tracStatus) {
		TracTask.Status status = TracTask.Status.fromStatus(tracStatus);
		return status == TracTask.Status.CLOSED;
	}

	@Override
	public boolean isLocal() {
		// TODO Auto-generated method stub
		return false;
	}

}
