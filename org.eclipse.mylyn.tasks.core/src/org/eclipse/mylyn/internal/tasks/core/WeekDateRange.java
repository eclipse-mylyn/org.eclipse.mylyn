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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author Rob Elves
 */
public class WeekDateRange extends DateRange {

	private static final String DESCRIPTION_WEEK_AFTER_NEXT = "Two Weeks";

	private static final String DESCRIPTION_PREVIOUS_WEEK = "Previous Week";

	private static final String DESCRIPTION_THIS_WEEK = "This Week";

	private static final String DESCRIPTION_NEXT_WEEK = "Next Week";

	private final List<DayDateRange> days = new ArrayList<DayDateRange>();

	public WeekDateRange(Calendar startDate, Calendar endDate) {
		super(startDate, endDate);
	}

	public List<DateRange> getRemainingDays() {
		List<DateRange> remainingDays = new ArrayList<DateRange>();
		for (DateRange dayDateRange : getDaysOfWeek()) {
			if (!dayDateRange.isPast()) {
				remainingDays.add(dayDateRange);
			}
		}
		return remainingDays;
	}

	public List<DayDateRange> getDaysOfWeek() {
		if (days.isEmpty()) {
			for (int x = TaskActivityUtil.getStartDay(); x < (TaskActivityUtil.getStartDay() + 7); x++) {
				Calendar dayStart = TaskActivityUtil.getCalendar();
				dayStart.setTime(getStartDate().getTime());
				TaskActivityUtil.snapStartOfDay(dayStart);

				Calendar dayEnd = TaskActivityUtil.getCalendar();
				dayEnd.setTime(getStartDate().getTime());
				TaskActivityUtil.snapEndOfDay(dayEnd);

				if (x > 7) {
					dayStart.set(Calendar.DAY_OF_WEEK, x % 7);
					dayEnd.set(Calendar.DAY_OF_WEEK, x % 7);
				} else {
					dayStart.set(Calendar.DAY_OF_WEEK, x);
					dayEnd.set(Calendar.DAY_OF_WEEK, x);
				}

				days.add(new DayDateRange(dayStart, dayEnd));
			}
		}
		return days;
	}

	/**
	 * @return today's DayDateRange, null if does not exist (now > endDate)
	 */
	public DayDateRange getToday() {
		DayDateRange today = null;
		Calendar now = TaskActivityUtil.getCalendar();
		for (DayDateRange range : getDaysOfWeek()) {
			if (range.includes(now)) {
				today = range;
				break;
			}
		}
		if (today == null) {
			Calendar todayStart = TaskActivityUtil.getCalendar();
			TaskActivityUtil.snapStartOfDay(todayStart);
			Calendar todayEnd = TaskActivityUtil.getCalendar();
			TaskActivityUtil.snapEndOfDay(todayEnd);
			today = new DayDateRange(todayStart, todayEnd);
		}
		return today;
	}

	public boolean isCurrentWeekDay(DateRange range) {
		if (range == null) {
			return false;
		}
		return getDaysOfWeek().contains(range);
	}

	private boolean isNextWeek() {
		return TaskActivityUtil.getNextWeek().compareTo(this) == 0;
	}

	public boolean isThisWeek() {
		//if (isWeek()) {
		return this.includes(Calendar.getInstance());
		//}
		//return false;
	}

	private boolean isPreviousWeek() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.WEEK_OF_YEAR, -1);
		return this.includes(cal);
	}

	private boolean isWeekAfterNext() {
		return TaskActivityUtil.getNextWeek().next().compareTo(this) == 0;
	}

	public WeekDateRange next() {
		return create(Calendar.WEEK_OF_YEAR, 1);
	}

	public WeekDateRange previous() {
		return create(Calendar.WEEK_OF_YEAR, -1);
	}

	protected WeekDateRange create(int field, int multiplier) {
		Calendar previousStart = (Calendar) getStartDate().clone();
		Calendar previousEnd = (Calendar) getEndDate().clone();
		previousStart.add(field, 1 * multiplier);
		previousEnd.add(field, 1 * multiplier);
		return new WeekDateRange(previousStart, previousEnd);
	}

	@Override
	public String toString(boolean useDayOfWeekForNextWeek) {
		if (isWeekAfterNext()) {
			return DESCRIPTION_WEEK_AFTER_NEXT;
		} else if (isThisWeek()) {
			return DESCRIPTION_THIS_WEEK;
		} else if (isNextWeek()) {
			return DESCRIPTION_NEXT_WEEK;
		} else if (isPreviousWeek()) {
			return DESCRIPTION_PREVIOUS_WEEK;
		}
		return super.toString(useDayOfWeekForNextWeek);
	}

	public DateRange getDayOfWeek(int dayNum) {
		if (dayNum > 0 && dayNum <= 7) {
			for (DateRange day : getDaysOfWeek()) {
				if (day.getStartDate().get(Calendar.DAY_OF_WEEK) == dayNum) {
					return day;
				}
			}
		}
		throw new IllegalArgumentException("Valid day values are 1 - 7");
	}

	public static boolean isWeekRange(Calendar calStart, Calendar calEnd) {
		// bug 248683
		long diff = (calEnd.getTimeInMillis() - calStart.getTimeInMillis()) - (DAY * 7 - 1);
		return Math.abs(diff) <= 60 * 60 * 1000;
	}
}
