/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author Rob Elves
 */
public class WeekDateRange extends DateRange {

	private final List<DateRange> days = new ArrayList<DateRange>();

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

	public List<DateRange> getDaysOfWeek() {
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

				days.add(new DateRange(dayStart, dayEnd));
			}
		}
		return days;
	}

	/**
	 * @return today's DayDateRange, null if does not exist (now > endDate)
	 */
	public DateRange getToday() {
		DateRange today = null;
		Calendar now = TaskActivityUtil.getCalendar();
		for (DateRange range : getDaysOfWeek()) {
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
			today = new DateRange(todayStart, todayEnd);
		}
		return today;
	}

	public boolean isCurrentWeekDay(DateRange range) {
		if (range == null) {
			return false;
		}
		return getDaysOfWeek().contains(range);
	}

	public DateRange getDayOfWeek(int dayNum) {
		if (dayNum > 0 && dayNum <= 7) {
			for (DateRange day : getDaysOfWeek()) {
				if (day.getStartDate().get(Calendar.DAY_OF_WEEK) == dayNum) {
					return day;
				}
			}
		}
		return null;
	}
}
