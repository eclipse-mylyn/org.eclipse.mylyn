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

package org.eclipse.mylar.internal.trac;

import org.eclipse.mylar.internal.trac.core.ITracClient;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;

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

	}

	public enum Status {
		NEW, ASSIGNED, CLOSED;

		@Override
		public String toString() {
			switch (this) {
			case NEW:
				return "New";
			case ASSIGNED:
				return "Assigned";
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
			if (status.equals("closed"))
				return CLOSED;
			return null;
		}

	}

	public TracTask(String handle, String label, boolean newTask) {
		super(handle, label, newTask);
		
		setUrl(AbstractRepositoryTask.getRepositoryUrl(handle) + ITracClient.TICKET_URL + AbstractRepositoryTask.getTaskId(handle));
	}
	
	@Override
	public boolean isCompleted() {
		if (taskData != null) {
			return Status.CLOSED.toString().toLowerCase().equals(taskData.getStatus());
		} else {
			return super.isCompleted();
		}
	}

	@Override
	public String getRepositoryKind() {
		return TracUiPlugin.REPOSITORY_KIND;
	}

}
