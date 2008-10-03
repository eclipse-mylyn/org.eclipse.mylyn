/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import java.text.DateFormat;
import java.util.Calendar;

import org.eclipse.core.runtime.Assert;

/**
 * @author Rob Elves
 * @since 3.0
 */
public class DateRange implements Comparable<DateRange> {
	protected static final long DAY = 1000 * 60 * 60 * 24;

	private final Calendar startDate;

	private final Calendar endDate;

	/**
	 * create an instance of a date range that represents a finite point in time
	 */
	public DateRange(Calendar time) {
		startDate = time;
		endDate = time;
	}

	public DateRange(Calendar startDate, Calendar endDate) {
		Assert.isNotNull(startDate);
		Assert.isNotNull(endDate);
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public boolean includes(DateRange range) {
		return (startDate.getTimeInMillis() <= range.getStartDate().getTimeInMillis())
				&& (endDate.getTimeInMillis() >= range.getEndDate().getTimeInMillis());
	}

	public boolean includes(Calendar cal) {
		return (startDate.getTimeInMillis() <= cal.getTimeInMillis())
				&& (endDate.getTimeInMillis() >= cal.getTimeInMillis());
	}

	public Calendar getStartDate() {
		return startDate;
	}

	public Calendar getEndDate() {
		return endDate;
	}

	/**
	 * TODO: Move into label provider
	 */
	@Override
	public String toString() {
		return toString(true);
	}

	public String toString(boolean useDayOfWeekForNextWeek) {
		return DateFormat.getDateInstance(DateFormat.MEDIUM).format(startDate.getTime());
		/* + " to "+ DateFormat.getDateInstance(DateFormat.MEDIUM).format(endDate.getTime());*/
	}

//	protected DateRange create(int field, int multiplier) {
//		Calendar previousStart = (Calendar) getStartDate().clone();
//		Calendar previousEnd = (Calendar) getEndDate().clone();
//		previousStart.add(field, 1 * multiplier);
//		previousEnd.add(field, 1 * multiplier);
//		return new DateRange(previousStart, previousEnd);
//	}

//	public boolean isDay() {
//		return ((getEndDate().getTimeInMillis() - getStartDate().getTimeInMillis()) == DAY - 1);
//	}
//
//	public boolean isWeek() {
//		return ((getEndDate().getTimeInMillis() - getStartDate().getTimeInMillis()) == (DAY * 7) - 1);
//	}

	public boolean isPresent() {
		return this.getStartDate().before(Calendar.getInstance()) && this.getEndDate().after(Calendar.getInstance());
	}

	public boolean isPast() {
		return getEndDate().compareTo(Calendar.getInstance()) < 0;
	}

	public boolean isFuture() {
		return !isPresent() && this.getStartDate().after(Calendar.getInstance());
	}

	public boolean isBefore(DateRange scheduledDate) {
		return this.getEndDate().compareTo(scheduledDate.getStartDate()) < 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DateRange)) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
//		if (getClass() != obj.getClass()) {
//			return false;
//		}
		DateRange other = (DateRange) obj;
		if (endDate == null) {
			if (other.endDate != null) {
				return false;
			}
		} else if (!endDate.equals(other.endDate)) {
			return false;
		}
		if (startDate == null) {
			if (other.startDate != null) {
				return false;
			}
		} else if (!startDate.equals(other.startDate)) {
			return false;
		}
		return true;
	}

	public boolean before(Calendar cal) {
		return getEndDate().before(cal);
	}

	public boolean after(Calendar cal) {
		return cal.before(getEndDate());
	}

	public int compareTo(DateRange range) {
		if (range.getStartDate().equals(startDate) && range.getEndDate().equals(endDate)) {
			return 0;
		} else if (includes(range)) {
			return 1;
		} else if (before(range.getStartDate())) {
			return -1;
		} else if (after(range.getEndDate())) {
			return 1;
		}
		return -1;
	}

}
