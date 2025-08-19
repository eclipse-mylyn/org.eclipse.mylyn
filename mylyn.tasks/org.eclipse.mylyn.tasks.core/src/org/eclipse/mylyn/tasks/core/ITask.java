/*******************************************************************************
 * Copyright (c) 2004, 2015 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.core;

import java.util.Date;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.internal.tasks.core.Messages;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 * @since 3.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
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
			return switch (this) {
				case INCOMING, INCOMING_NEW, CONFLICT -> true;
				default -> false;
			};
		}

		/**
		 * @since 3.0
		 */
		public boolean isOutgoing() {
			return switch (this) {
				case OUTGOING, OUTGOING_NEW, CONFLICT -> true;
				default -> false;
			};
		}

		/**
		 * @since 3.0
		 */
		public boolean isSynchronized() {
			return switch (this) {
				case SYNCHRONIZED -> true;
				default -> false;
			};
		}
	}

	/**
	 * Defines an interface for priorities that have an associated integer value.
	 *
	 * @author Steffen Pingel
	 * @since 3.7
	 * @see PriorityLevel#fromValue(IPriorityValue[], IPriorityValue)
	 */
	public interface IPriorityValue {

		/**
		 * Returns the integer value of this priority.
		 */
		int getPriorityValue();

	}

	/**
	 * @since 3.0
	 */
	public enum PriorityLevel {
		P1, P2, P3, P4, P5;

		private static final int LEVEL_COUNT = PriorityLevel.values().length;

		@Override
		public String toString() {
			return switch (this) {
				case P1 -> "P1"; //$NON-NLS-1$
				case P2 -> "P2"; //$NON-NLS-1$
				case P3 -> "P3"; //$NON-NLS-1$
				case P4 -> "P4"; //$NON-NLS-1$
				case P5 -> "P5"; //$NON-NLS-1$
				default -> "P3"; //$NON-NLS-1$
			};
		}

		/**
		 * @since 3.0
		 */
		public String getDescription() {
			return switch (this) {
				case P1 -> Messages.PriorityLevel_Very_High;
				case P2 -> Messages.PriorityLevel_High;
				case P3 -> Messages.PriorityLevel_Normal;
				case P4 -> Messages.PriorityLevel_Low;
				case P5 -> Messages.PriorityLevel_Very_Low;
				default -> ""; //$NON-NLS-1$
			};
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
			if ("P1".equals(string)) { //$NON-NLS-1$
				return P1;
			}
			if ("P2".equals(string)) { //$NON-NLS-1$
				return P2;
			}
			if ("P3".equals(string)) { //$NON-NLS-1$
				return P3;
			}
			if ("P4".equals(string)) { //$NON-NLS-1$
				return P4;
			}
			if ("P5".equals(string)) { //$NON-NLS-1$
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
			if (string.equals(Messages.PriorityLevel_Very_High)) {
				return P1;
			}
			if (string.equals(Messages.PriorityLevel_High)) {
				return P2;
			}
			if (string.equals(Messages.PriorityLevel_Normal)) {
				return P3;
			}
			if (string.equals(Messages.PriorityLevel_Low)) {
				return P4;
			}
			if (string.equals(Messages.PriorityLevel_Very_Low)) {
				return P5;
			}
			return getDefault();
		}

		/**
		 * @since 3.20
		 */
		public static boolean isValidPriority(String string) {
			try {
				PriorityLevel.valueOf(string);
				return true;
			} catch (IllegalArgumentException e) {
				return false;
			}
		}

		/**
		 * Maps a priority value to a {@link PriorityLevel}. The value needs to be present in <code>priorities</code>, otherwise
		 * {@link PriorityLevel#getDefault()} is returned.
		 * <p>
		 * NOTE: <code>priorities</code> needs to be sorted in ascending order.
		 *
		 * @param priorities
		 *            a sorted array of priority levels
		 * @param value
		 *            the value to map
		 * @since 3.7
		 */
		public static PriorityLevel fromValue(IPriorityValue[] priorities, IPriorityValue value) {
			Assert.isNotNull(priorities);
			if (value != null) {
				int minValue = priorities[0].getPriorityValue();
				int range = priorities[priorities.length - 1].getPriorityValue() - minValue;
				for (IPriorityValue priority : priorities) {
					if (value.equals(priority)) {
						float relativeValue = (float) (priority.getPriorityValue() - minValue) / range;
						int level = (int) (relativeValue * LEVEL_COUNT) + 1;
						return PriorityLevel.fromLevel(level);
					}
				}
			}
			return PriorityLevel.getDefault();
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
	Date getCompletionDate();

	/**
	 * Returns the identifier that uniquely distinguishes the repository connector associated with this task.
	 *
	 * @since 3.0
	 */
	String getConnectorKind();

	/**
	 * Returns the date that this task was created.
	 *
	 * @since 3.0
	 */
	Date getCreationDate();

	/**
	 * Returns the date after which this task will become overdue.
	 *
	 * @since 3.0
	 */
	Date getDueDate();

	/**
	 * @since 3.0
	 */
	@Override
	String getHandleIdentifier();

	/**
	 * Returns the date that the repository contents of this task were last modified.
	 *
	 * @since 3.0
	 */
	Date getModificationDate();

	/**
	 * Returns the label of the owner, that is, the <i>option label</i> corresponding to the value of the
	 * {@link TaskAttribute#USER_ASSIGNED} attribute in the TaskData. If the connector does not provide option labels for this attribute,
	 * the {@link #getOwnerId() ID} is returned instead.
	 *
	 * @since 3.0
	 */
	String getOwner();

	/**
	 * Returns the ID of the owner, that is, the <i>value</i> of the {@link TaskAttribute#USER_ASSIGNED} attribute in the TaskData.
	 *
	 * @since 3.15
	 */
	String getOwnerId();

	/**
	 * @since 3.0
	 */
	String getPriority();

	/**
	 * @since 3.0
	 */
	String getRepositoryUrl();

	/**
	 * @since 3.0
	 */
	@Override
	String getSummary();

	/**
	 * @since 3.0
	 */
	SynchronizationState getSynchronizationState();

	/**
	 * @since 3.0
	 */
	String getTaskId();

	/**
	 * User identifiable key for the task to be used in UI facilities such as label displays and hyperlinked references. Can return the same
	 * as the ID (e.g. in the case of Bugzilla). Can return null if no such label exists.
	 *
	 * @since 3.0
	 */
	String getTaskKey();

	/**
	 * @since 3.0
	 */
	String getTaskKind();

	/**
	 * @since 3.0
	 */
	boolean isActive();

	/**
	 * @since 3.0
	 */
	boolean isCompleted();

	/**
	 * @since 3.0
	 */
	void setCompletionDate(Date completionDate);

	/**
	 * @since 3.0
	 */
	void setCreationDate(Date date);

	/**
	 * @since 3.0
	 */
	void setDueDate(Date date);

	/**
	 * @since 3.0
	 */
	void setModificationDate(Date modificationDate);

	/**
	 * @since 3.0
	 */
	void setOwner(String owner);

	/**
	 * @since 3.15
	 */
	void setOwnerId(String ownerId);

	/**
	 * @since 3.0
	 */
	void setPriority(String priority);

	/**
	 * @since 3.0
	 */
	void setSummary(String summary);

	/**
	 * @since 3.0
	 */
	void setTaskKind(String kind);

	/**
	 * @since 3.0
	 */
	void setUrl(String taskUrl);

	/**
	 * @since 3.0
	 */
	void setTaskKey(String taskKey);

}
