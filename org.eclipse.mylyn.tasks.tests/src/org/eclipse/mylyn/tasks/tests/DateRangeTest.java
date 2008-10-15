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

package org.eclipse.mylyn.tasks.tests;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.core.DateRange;
import org.eclipse.mylyn.internal.tasks.core.DayDateRange;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.internal.tasks.core.WeekDateRange;
import org.eclipse.mylyn.tasks.core.ITask;

/**
 * @author Rob Elves
 */
public class DateRangeTest extends TestCase {

	private static final int HOUR = 60 * 60 * 1000;

	public void testCompareInstant() {
		DateRange range1 = new DateRange(TaskActivityUtil.getCurrentWeek().getToday().previous().getStartDate());
		DateRange range2 = new DateRange(TaskActivityUtil.getCurrentWeek().getToday().getStartDate());
		assertEquals(-1, range1.compareTo(range2));
		assertEquals(1, range2.compareTo(range1));
	}

	public void testCompareRanges() {
		DateRange range1 = TaskActivityUtil.getCurrentWeek().getToday().previous();
		DateRange range2 = TaskActivityUtil.getCurrentWeek().getToday();
		assertEquals(-1, range1.compareTo(range2));
		assertEquals(1, range2.compareTo(range1));
	}

	public void testQueryDateRange() {
		SortedMap<DateRange, Set<ITask>> scheduledTasks = Collections.synchronizedSortedMap(new TreeMap<DateRange, Set<ITask>>());
		DateRange range1 = TaskActivityUtil.getCurrentWeek().getToday().previous();
		Set<ITask> tasks = new HashSet<ITask>();
		tasks.add(new LocalTask("1", "summaryForLocalTask"));
		scheduledTasks.put(range1, tasks);
		assertFalse(scheduledTasks.isEmpty());
		assertNotNull(scheduledTasks.get(range1));

		DateRange rangeTest = TaskActivityUtil.getCurrentWeek().getToday().previous();
		assertNotNull(scheduledTasks.get(rangeTest));

		DateRange range2 = TaskActivityUtil.getCurrentWeek().getToday();
		tasks = new HashSet<ITask>();
		tasks.add(new LocalTask("2", "summaryForLocalTask2"));
		scheduledTasks.put(range2, tasks);

		SortedMap<DateRange, Set<ITask>> result = scheduledTasks.subMap(range1, range2);
		assertEquals(1, result.size());

		DateRange range0 = TaskActivityUtil.getCurrentWeek().getToday().previous().previous();
		DateRange range3 = TaskActivityUtil.getCurrentWeek().getToday().next();
		result = scheduledTasks.subMap(range0, range3);
		assertEquals(2, result.size());
	}

	public void testIsWeekRange() {
		TimeZone defaultTimeZone = TimeZone.getDefault();
		try {
			TimeZone.setDefault(TimeZone.getTimeZone("PST"));
			Calendar time = TaskActivityUtil.getCalendar();

			time.set(2008, 9, 1);
			DateRange range = TaskActivityUtil.getWeekOf(time.getTime());
			assertTrue(WeekDateRange.isWeekRange(range.getStartDate(), range.getEndDate()));
			range.getStartDate().setTimeInMillis(range.getStartDate().getTimeInMillis() + 1);
			assertTrue("1 ms longer than a week, expected to be within legal interval", WeekDateRange.isWeekRange(
					range.getStartDate(), range.getEndDate()));
			range = TaskActivityUtil.getDayOf(time.getTime());
			assertFalse("only a day", WeekDateRange.isWeekRange(range.getStartDate(), range.getEndDate()));

			// PST changes to PDT on Mar 9th 2008
			time.set(2008, 2, 9);
			range = TaskActivityUtil.getWeekOf(time.getTime());
			assertEquals(0, range.getStartDate().get(Calendar.DST_OFFSET));
			assertEquals(HOUR, range.getEndDate().get(Calendar.DST_OFFSET));
			assertTrue(WeekDateRange.isWeekRange(range.getStartDate(), range.getEndDate()));
			range.getStartDate().setTimeInMillis(range.getStartDate().getTimeInMillis() + 1);
			assertFalse("1 ms too long", WeekDateRange.isWeekRange(range.getStartDate(), range.getEndDate()));
			range = TaskActivityUtil.getDayOf(time.getTime());
			assertFalse(WeekDateRange.isWeekRange(range.getStartDate(), range.getEndDate()));

			// PDT changes to PST on Nov 2nd 2008
			time.set(2008, 10, 2);
			range = TaskActivityUtil.getWeekOf(time.getTime());
			assertEquals(HOUR, range.getStartDate().get(Calendar.DST_OFFSET));
			assertEquals(0, range.getEndDate().get(Calendar.DST_OFFSET));
			assertTrue(WeekDateRange.isWeekRange(range.getStartDate(), range.getEndDate()));
			range.getStartDate().setTimeInMillis(range.getStartDate().getTimeInMillis() - 1);
			assertFalse("1 ms too short", WeekDateRange.isWeekRange(range.getStartDate(), range.getEndDate()));
		} finally {
			TimeZone.setDefault(defaultTimeZone);
		}
	}

	public void testIsDayRange() {
		TimeZone defaultTimeZone = TimeZone.getDefault();
		try {
			TimeZone.setDefault(TimeZone.getTimeZone("PST"));
			Calendar time = TaskActivityUtil.getCalendar();
			time.set(2008, 9, 1);
			DateRange range = TaskActivityUtil.getDayOf(time.getTime());
			assertTrue(DayDateRange.isDayRange(range.getStartDate(), range.getEndDate()));
			range.getStartDate().setTimeInMillis(range.getStartDate().getTimeInMillis() + 1);
			assertTrue("1 ms longer than a day, expected to be within legal interval", DayDateRange.isDayRange(
					range.getStartDate(), range.getEndDate()));
			range.getStartDate().setTimeInMillis(range.getStartDate().getTimeInMillis() + HOUR);
			assertFalse("1 hour + 1 ms longer than a day", DayDateRange.isDayRange(range.getStartDate(),
					range.getEndDate()));
			range.getStartDate().setTimeInMillis(range.getStartDate().getTimeInMillis() - 2 * HOUR - 2);
			assertFalse("1 hour + 1 ms shorter than a day", DayDateRange.isDayRange(range.getStartDate(),
					range.getEndDate()));
			range = TaskActivityUtil.getDayOf(time.getTime());
			assertTrue("a week", DayDateRange.isDayRange(range.getStartDate(), range.getEndDate()));

			// PDT changes to PST on Mar 9th 2008
			time.set(2008, 2, 9);
			range = TaskActivityUtil.getDayOf(time.getTime());
			assertEquals(0, range.getStartDate().get(Calendar.DST_OFFSET));
			assertEquals(HOUR, range.getEndDate().get(Calendar.DST_OFFSET));
			assertTrue(DayDateRange.isDayRange(range.getStartDate(), range.getEndDate()));
			range.getStartDate().setTimeInMillis(range.getStartDate().getTimeInMillis() + 1);
			assertFalse("1 ms too long", DayDateRange.isDayRange(range.getStartDate(), range.getEndDate()));

			// PST changes to PDT on Nov 2nd 2008
			time.set(2008, 10, 2);
			range = TaskActivityUtil.getDayOf(time.getTime());
			assertEquals(HOUR, range.getStartDate().get(Calendar.DST_OFFSET));
			assertEquals(0, range.getEndDate().get(Calendar.DST_OFFSET));
			assertTrue(DayDateRange.isDayRange(range.getStartDate(), range.getEndDate()));
			range.getStartDate().setTimeInMillis(range.getStartDate().getTimeInMillis() - 1);
			assertFalse("1 ms too short", DayDateRange.isDayRange(range.getStartDate(), range.getEndDate()));
		} finally {
			TimeZone.setDefault(defaultTimeZone);
		}
	}

	public void testNext() {
		Calendar time = TaskActivityUtil.getCalendar();
		time.set(2008, 11, 31);
		DayDateRange day = TaskActivityUtil.getDayOf(time.getTime());
		assertEquals(2009, day.next().getStartDate().get(Calendar.YEAR));
		assertEquals(2008, day.getStartDate().get(Calendar.YEAR));
		assertEquals(day, day.next().previous());

		WeekDateRange week = TaskActivityUtil.getWeekOf(time.getTime());
		assertEquals(2009, week.next().getStartDate().get(Calendar.YEAR));
		assertEquals(2008, week.getStartDate().get(Calendar.YEAR));
		assertEquals(week, week.next().previous());
	}

}
