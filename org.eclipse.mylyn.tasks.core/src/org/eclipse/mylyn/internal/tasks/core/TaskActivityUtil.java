/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import java.util.Calendar;

/**
 * @author Rob Elves
 */
public class TaskActivityUtil {

	private static int startDay = Calendar.MONDAY;

	private static int endHour = 17;

	public static Calendar snapStartOfDay(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.getTime();
		return cal;
	}

	public static Calendar snapStartOfHour(Calendar cal) {
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.getTime();
		return cal;
	}

	public static Calendar snapEndOfHour(Calendar cal) {
		cal.set(Calendar.MINUTE, cal.getActualMaximum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getActualMaximum(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, cal.getActualMaximum(Calendar.MILLISECOND));
		cal.getTime();
		return cal;
	}

	public static Calendar snapEndOfDay(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND));
		cal.getTime();
		return cal;
	}

	public static Calendar snapNextDay(Calendar cal) {
		cal.add(Calendar.DAY_OF_MONTH, 1);
		snapStartOfDay(cal);
		return cal;
	}

	public static Calendar snapStartOfWorkWeek(Calendar cal) {
		cal.set(Calendar.DAY_OF_WEEK, startDay);
		snapStartOfDay(cal);
		return cal;
	}

	public static Calendar snapEndOfWeek(Calendar cal) {
		if (cal.getFirstDayOfWeek() == Calendar.MONDAY) {
			cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		} else {
			cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		}
		snapEndOfDay(cal);
		return cal;
	}

	public static Calendar snapEndOfNextWeek(Calendar cal) {
		if (cal.getFirstDayOfWeek() == Calendar.MONDAY) {
			cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		} else {
			cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		}
		snapEndOfWeek(cal);
		cal.add(Calendar.WEEK_OF_MONTH, 1);
		return cal;
	}

	public static Calendar snapForwardNumDays(Calendar calendar, int days) {
		calendar.add(Calendar.DAY_OF_MONTH, days);
		calendar.set(Calendar.HOUR_OF_DAY, endHour);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar;
	}

	public static Calendar snapEndOfWorkDay(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, endHour);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar;
	}

	public static Calendar snapNextWorkWeek(Calendar calendar) {
		calendar.add(Calendar.WEEK_OF_MONTH, 1);
		snapStartOfWorkWeek(calendar);
		snapEndOfWorkDay(calendar);
		return calendar;
	}

	public static boolean isAfterCurrentWeek(Calendar time) {
		if (time != null) {
			Calendar cal = getCalendar();
			return time.compareTo(snapNextWorkWeek(cal)) > -1;
		}
		return false;
	}

	/**
	 * @return true if time is in or past Future bin
	 */
	public static boolean isFuture(Calendar time) {
		if (time != null) {
			Calendar cal = getCalendar();
			cal.add(Calendar.WEEK_OF_MONTH, 2);
			snapStartOfWorkWeek(cal);
			return time.compareTo(cal) > -1;
		}
		return false;
	}

	public static boolean isThisWeek(Calendar time) {
		if (time != null) {
			Calendar weekStart = getCalendar();
			snapStartOfWorkWeek(weekStart);
			Calendar weekEnd = getCalendar();
			snapEndOfWeek(weekEnd);
			return (time.compareTo(weekStart) >= 0 && time.compareTo(weekEnd) <= 0);
		}
		return false;
	}

	public static boolean isNextWeek(Calendar time) {
		if (time != null) {
			Calendar weekStart = getCalendar();
			snapNextWorkWeek(weekStart);
			Calendar weekEnd = getCalendar();
			snapNextWorkWeek(weekEnd);
			snapEndOfWeek(weekEnd);
			return (time.compareTo(weekStart) >= 0 && time.compareTo(weekEnd) <= 0);
		}
		return false;
	}

	public static boolean isToday(Calendar time) {
		if (time != null) {
			Calendar dayStart = getCalendar();
			snapStartOfDay(dayStart);
			Calendar midnight = getCalendar();
			snapEndOfDay(midnight);
			return (time.compareTo(dayStart) >= 0 && time.compareTo(midnight) <= 0);
		}
		return false;
	}

	public static Calendar getCalendar() {
		Calendar cal = Calendar.getInstance();
		cal.setFirstDayOfWeek(startDay);
		cal.getTime();
		return cal;
	}

	public static Calendar getStartOfCurrentWeek() {
		Calendar cal = getCalendar();
		return snapStartOfWorkWeek(cal);
	}

	public static Calendar getEndOfCurrentWeek() {
		Calendar cal = getCalendar();
		return snapEndOfWeek(cal);
	}

	public static boolean isBetween(Calendar time, Calendar start, Calendar end) {
		return (time.compareTo(start) >= 0 && time.compareTo(end) <= 0);
	}

	public static void setStartDay(int startDay) {
		TaskActivityUtil.startDay = startDay;
	}

	public static void setEndHour(int endHour) {
		TaskActivityUtil.endHour = endHour;
	}

}
