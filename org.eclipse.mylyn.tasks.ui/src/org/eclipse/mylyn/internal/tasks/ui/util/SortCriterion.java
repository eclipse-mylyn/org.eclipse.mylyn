/*******************************************************************************
 * Copyright (c) 2009 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.util;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ui.IMemento;

/**
 * @author Frank Becker
 * @author Steffen Pingel
 */
public class SortCriterion {

	public enum SortKey {
		NONE, DATE_CREATED, PRIORITY, RANK, SUMMARY, TASK_ID, TASK_TYPE, DUE_DATE, MODIFICATION_DATE;

		public static SortKey valueOfLabel(String label) {
			for (SortKey value : values()) {
				if (value.getLabel().equals(label)) {
					return value;
				}
			}
			return null;
		}

		public String getLabel() {
			switch (this) {
			case NONE:
				return Messages.SortKindEntry_None;
			case PRIORITY:
				return Messages.SortKindEntry_Priority;
			case SUMMARY:
				return Messages.SortKindEntry_Summary;
			case RANK:
				return Messages.SortKindEntry_Rank;
			case DATE_CREATED:
				return Messages.SortKindEntry_Date_Created;
			case TASK_ID:
				return Messages.SortKindEntry_Task_ID;
			case TASK_TYPE:
				return Messages.SortCriterion_Type;
			case DUE_DATE:
				return Messages.SortKindEntry_Due_Date;
			case MODIFICATION_DATE:
				return Messages.SortCriterion_Modification_Date;
			default:
				return null;
			}
		}

	}

	private SortKey key;

	private int direction;

	private static final String MEMENTO_KEY_SORT_KEY = "sortKey"; //$NON-NLS-1$

	private static final String MEMENTO_KEY_SORT_DIRECTION = "sortDirection"; //$NON-NLS-1$

	private static final SortKey DEFAULT_SORT_KIND = SortKey.NONE;

	private static final int DEFAULT_SORT_DIRECTION = 1;

	public static final int kindCount = SortKey.values().length - 1;

	public SortCriterion() {
		key = DEFAULT_SORT_KIND;
		direction = DEFAULT_SORT_DIRECTION;
	}

	public SortCriterion(SortKey kind, int sortDirection) {
		this.key = kind;
		this.direction = sortDirection;
	}

	public SortKey getKey() {
		return key;
	}

	public void setKey(SortKey kind) {
		Assert.isNotNull(kind);
		this.key = kind;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int sortDirection) {
		Assert.isTrue(sortDirection == -1 || sortDirection == 1);
		this.direction = sortDirection;
	}

	private SortKey getSortKey(IMemento memento, String key, SortKey defaultValue) {
		String value = memento.getString(key);
		if (value != null) {
			try {
				return SortKey.valueOf(value);
			} catch (IllegalArgumentException e) {
				// ignore
			}
		}
		return defaultValue;
	}

	private int getSortDirection(IMemento memento, String key, int defaultValue) {
		Integer value = memento.getInteger(key);
		if (value != null) {
			return value >= 0 ? 1 : -1;
		}
		return defaultValue;
	}

	public void restoreState(IMemento memento) {
		setKey(getSortKey(memento, MEMENTO_KEY_SORT_KEY, DEFAULT_SORT_KIND));
		setDirection(getSortDirection(memento, MEMENTO_KEY_SORT_DIRECTION, DEFAULT_SORT_DIRECTION));
	}

	public void saveState(IMemento memento) {
		memento.putString(MEMENTO_KEY_SORT_KEY, getKey().name());
		memento.putInteger(MEMENTO_KEY_SORT_DIRECTION, getDirection());
	}
}
