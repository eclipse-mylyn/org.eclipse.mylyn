/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import java.util.Date;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 * @since 3.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ITask extends IRepositoryElement, IAttributeContainer {

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

		/**
		 * @since 3.0
		 */
		public boolean isSynchronized() {
			switch (this) {
			case SYNCHRONIZED:
				return true;
			default:
				return false;
			}
		}
	}

	/**
	 * @since 3.0
	 */
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

		/**
		 * @since 3.0
		 */
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
		 * @since 3.0
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

		/**
		 * @since 3.0
		 */
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

		/**
		 * @since 3.0
		 */
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

		/**
		 * @since 3.0
		 */
		public static PriorityLevel getDefault() {
			return P3;
		}
	}

	/**
	 * Returns the date that the task was completed.
	 * 
	 * @since 3.0
	 */
	public abstract Date getCompletionDate();

	/**
	 * Returns the identifier that uniquely distinguishes the repository connector associated with this task.
	 * 
	 * @since 3.0
	 */
	public abstract String getConnectorKind();

	/**
	 * Returns the date that this task was created.
	 * 
	 * @since 3.0
	 */
	public abstract Date getCreationDate();

	/**
	 * Returns the date after which this task will become overdue.
	 * 
	 * @since 3.0
	 */
	public abstract Date getDueDate();

	/**
	 * @since 3.0
	 */
	public abstract String getHandleIdentifier();

	/**
	 * Returns the date that the repository contents of this task were last modified.
	 * 
	 * @since 3.0
	 */
	public abstract Date getModificationDate();

	/**
	 * @since 3.0
	 */
	public abstract String getOwner();

	/**
	 * @since 3.0
	 */
	public abstract String getPriority();

	/**
	 * @since 3.0
	 */
	public abstract String getRepositoryUrl();

	/**
	 * @since 3.0
	 */
	public abstract String getSummary();

	/**
	 * @since 3.0
	 */
	public abstract SynchronizationState getSynchronizationState();

	/**
	 * @since 3.0
	 */
	public abstract String getTaskId();

	/**
	 * User identifiable key for the task to be used in UI facilities such as label displays and hyperlinked references.
	 * Can return the same as the ID (e.g. in the case of Bugzilla). Can return null if no such label exists.
	 * 
	 * @since 3.0
	 */
	public abstract String getTaskKey();

	/**
	 * @since 3.0
	 */
	public abstract String getTaskKind();

	/**
	 * @since 3.0
	 */
	public abstract boolean isActive();

	/**
	 * @since 3.0
	 */
	public abstract boolean isCompleted();

	/**
	 * @since 3.0
	 */
	public abstract void setCompletionDate(Date completionDate);

	/**
	 * @since 3.0
	 */
	public abstract void setCreationDate(Date date);

	/**
	 * @since 3.0
	 */
	public abstract void setDueDate(Date date);

	/**
	 * @since 3.0
	 */
	public abstract void setModificationDate(Date modificationDate);

	/**
	 * @since 3.0
	 */
	public abstract void setOwner(String owner);

	/**
	 * @since 3.0
	 */
	public abstract void setPriority(String priority);

	/**
	 * @since 3.0
	 */
	public abstract void setSummary(String summary);

	/**
	 * @since 3.0
	 */
	public abstract void setTaskKind(String kind);

	/**
	 * @since 3.0
	 */
	public abstract void setUrl(String taskUrl);

	/**
	 * @since 3.0
	 */
	public abstract void setTaskKey(String taskKey);

}
