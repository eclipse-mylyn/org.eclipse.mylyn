/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import java.util.Date;

import org.eclipse.core.runtime.IStatus;

/**
 * @author Mik Kersten
 * @since 3.0
 */
public interface ITask extends ITaskElement {

	/**
	 * @since 3.0
	 */
	public enum SynchronizationState {
		CONFLICT, INCOMING, INCOMING_NEW, OUTGOING, OUTGOING_NEW, SYNCHRONIZED;

		/**
		 * @since 3.0
		 */
		public boolean isIncoming() {
			switch (this) {
			case INCOMING:
			case INCOMING_NEW:
			case CONFLICT:
				return true;
			default:
				return false;
			}
		}

		/**
		 * @since 3.0
		 */
		public boolean isOutgoing() {
			switch (this) {
			case OUTGOING:
			case OUTGOING_NEW:
			case CONFLICT:
				return true;
			default:
				return false;
			}
		}

		public boolean isSynchronized() {
			switch (this) {
			case SYNCHRONIZED:
				return true;
			default:
				return false;
			}
		}
	}

	public enum PriorityLevel {
		P1, P2, P3, P4, P5;

		@Override
		public String toString() {
			switch (this) {
			case P1:
				return "P1";
			case P2:
				return "P2";
			case P3:
				return "P3";
			case P4:
				return "P4";
			case P5:
				return "P5";
			default:
				return "P3";
			}
		}

		public String getDescription() {
			switch (this) {
			case P1:
				return "Very High";
			case P2:
				return "High";
			case P3:
				return "Normal";
			case P4:
				return "Low";
			case P5:
				return "Very Low";
			default:
				return "";
			}
		}

		/**
		 * @since 2.3
		 */
		public static PriorityLevel fromLevel(int level) {
			if (level <= 1) {
				return P1;
			}
			if (level == 2) {
				return P2;
			}
			if (level == 3) {
				return P3;
			}
			if (level == 4) {
				return P4;
			}
			if (level >= 5) {
				return P5;
			}
			return getDefault();
		}

		public static PriorityLevel fromString(String string) {
			if (string.equals("P1")) {
				return P1;
			}
			if (string.equals("P2")) {
				return P2;
			}
			if (string.equals("P3")) {
				return P3;
			}
			if (string.equals("P4")) {
				return P4;
			}
			if (string.equals("P5")) {
				return P5;
			}
			return getDefault();
		}

		public static PriorityLevel fromDescription(String string) {
			if (string == null) {
				return null;
			}
			if (string.equals("Very High")) {
				return P1;
			}
			if (string.equals("High")) {
				return P2;
			}
			if (string.equals("Normal")) {
				return P3;
			}
			if (string.equals("Low")) {
				return P4;
			}
			if (string.equals("Very Low")) {
				return P5;
			}
			return getDefault();
		}

		public static PriorityLevel getDefault() {
			return P3;
		}
	}

	public abstract Date getCompletionDate();

	public abstract String getConnectorKind();

	public abstract Date getCreationDate();

	public abstract Date getDueDate();

	/**
	 * @since 3.0
	 */
	public abstract int getEstimatedTimeHours();

	/**
	 * Final to preserve the handle identifier format required by the framework.
	 */
	public abstract String getHandleIdentifier();

	@Deprecated
	public abstract String getLastReadTimeStamp();

	/**
	 * @since 3.0
	 */
	public abstract Date getModificationDate();

	public abstract String getNotes();

	public abstract String getOwner();

	public abstract String getPriority();

	public abstract String getRepositoryUrl();

	//public abstract DateRange getScheduledForDate();

	public abstract String getSummary();

	/**
	 * @since 3.0
	 */
	public abstract SynchronizationState getSynchronizationState();

	public abstract IStatus getSynchronizationStatus();

	public abstract String getTaskId();

	/**
	 * User identifiable key for the task to be used in UI facilities such as label displays and hyperlinked references.
	 * Can return the same as the ID (e.g. in the case of Bugzilla). Can return null if no such label exists.
	 */
	public abstract String getTaskKey();

	public abstract String getTaskKind();

	public abstract boolean hasValidUrl();

	@Deprecated
	public abstract boolean isActive();

	public abstract boolean isCompleted();

	/**
	 * True for tasks that can be modified without a round-trip to a server. For example, such a task can be marked
	 * completed via the Task List.
	 */
	public abstract boolean isLocal();

	public abstract boolean isPastReminder();

	public abstract boolean isStale();

	public abstract boolean isSubmitting();

	public abstract boolean isSynchronizing();

	@Deprecated
	public abstract void setActive(boolean b);

	/**
	 * @deprecated use {@link #setCompletionDate(Date)} instead
	 */
	@Deprecated
	public abstract void setCompleted(boolean completed);

	public abstract void setCompletionDate(Date completionDate);

	public abstract void setCreationDate(Date date);

	public abstract void setDueDate(Date date);

	public abstract void setEstimatedTimeHours(int estimated);

	/**
	 * @since 3.0
	 */
	public abstract void setModificationDate(Date modificationDate);

	public abstract void setNotes(String notes);

	public abstract void setOwner(String owner);

	public abstract void setPriority(String priority);

	public abstract void setStale(boolean stale);

	public abstract void setSummary(String summary);

	public abstract void setTaskKind(String kind);

	public abstract void setUrl(String taskUrl);

}
