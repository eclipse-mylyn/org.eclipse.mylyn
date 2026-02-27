/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;

import org.eclipse.mylyn.internal.tasks.core.DateRange;
import org.eclipse.mylyn.internal.tasks.core.DayDateRange;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.internal.tasks.core.WeekDateRange;
import org.eclipse.mylyn.tasks.core.ITask;
import org.junit.jupiter.api.Test;

/**
 * @author Rob Elves
 */
@SuppressWarnings("nls")
public class DateRangeTest {

	private static final int HOUR = 60 * 60 * 1000;

	@Test
	public void testCompareInstant() {
		DateRange range1 = new DateRange(TaskActivityUtil.getCurrentWeek().getToday().previous().getStartDate());
		DateRange range2 = new DateRange(TaskActivityUtil.getCurrentWeek().getToday().getStartDate());
		assertEquals(-1, range1.compareTo(range2));
		assertEquals(1, range2.compareTo(range1));
	}

	@Test
	public void testCompareRanges() {
		DateRange range1 = TaskActivityUtil.getCurrentWeek().getToday().previous();
		DateRange range2 = TaskActivityUtil.getCurrentWeek().getToday();
		assertEquals(-1, range1.compareTo(range2));
		assertEquals(1, range2.compareTo(range1));
	}

	@Test
	public void testQueryDateRange() {
		SortedMap<DateRange, Set<ITask>> scheduledTasks = Collections
				.synchronizedSortedMap(new TreeMap<DateRange, Set<ITask>>());
		DateRange range1 = TaskActivityUtil.getCurrentWeek().getToday().previous();
		Set<ITask> tasks = new HashSet<>();
		tasks.add(new LocalTask("1", "summaryForLocalTask"));
		scheduledTasks.put(range1, tasks);
		assertFalse(scheduledTasks.isEmpty());
		assertNotNull(scheduledTasks.get(range1));

		DateRange rangeTest = TaskActivityUtil.getCurrentWeek().getToday().previous();
		assertNotNull(scheduledTasks.get(rangeTest));

		DateRange range2 = TaskActivityUtil.getCurrentWeek().getToday();
		tasks = new HashSet<>();
		tasks.add(new LocalTask("2", "summaryForLocalTask2"));
		scheduledTasks.put(range2, tasks);

		SortedMap<DateRange, Set<ITask>> result = scheduledTasks.subMap(range1, range2);
		assertEquals(1, result.size());

		DateRange range0 = TaskActivityUtil.getCurrentWeek().getToday().previous().previous();
		DateRange range3 = TaskActivityUtil.getCurrentWeek().getToday().next();
		result = scheduledTasks.subMap(range0, range3);
		assertEquals(2, result.size());
	}

	@Test
	public void testOverScheduled() {
		SortedMap<DateRange, Set<ITask>> scheduledTasks = Collections
				.synchronizedSortedMap(new TreeMap<DateRange, Set<ITask>>());
		DateRange range1 = TaskActivityUtil.getDayOf(new Date(0));
		Set<ITask> tasks = new HashSet<>();
		tasks.add(new LocalTask("1", "summaryForLocalTask"));
		scheduledTasks.put(range1, tasks);
		assertFalse(scheduledTasks.isEmpty());
		assertNotNull(scheduledTasks.get(range1));

		Calendar start = TaskActivityUtil.getCalendar();
		start.setTimeInMillis(0);
		Calendar end = TaskActivityUtil.getCalendar();
		TaskActivityUtil.snapStartOfDay(end);

		DateRange startRange = new DateRange(start);
		Calendar endExclusive = TaskActivityUtil.getCalendar();
		endExclusive.setTimeInMillis(end.getTimeInMillis() + 1);
		DateRange endRange = new DateRange(endExclusive);

		SortedMap<DateRange, Set<ITask>> result = scheduledTasks.subMap(startRange, endRange);

		assertEquals(1, result.size());
	}

	@Test
	public void testIsWeekRange() {
		TimeZone defaultTimeZone = TimeZone.getDefault();
		try {
			TimeZone.setDefault(TimeZone.getTimeZone("PST"));
			Calendar time = TaskActivityUtil.getCalendar();

			time.set(2008, 9, 1);
			DateRange range = TaskActivityUtil.getWeekOf(time.getTime());
			assertTrue(WeekDateRange.isWeekRange(range.getStartDate(), range.getEndDate()));
			range.getStartDate().setTimeInMillis(range.getStartDate().getTimeInMillis() + 1);
			assertTrue(WeekDateRange.isWeekRange(range.getStartDate(), range.getEndDate()),
					"1 ms longer than a week, expected to be within legal interval");
			range = TaskActivityUtil.getDayOf(time.getTime());
			assertFalse(WeekDateRange.isWeekRange(range.getStartDate(), range.getEndDate()), "only a day");

			// PST changes to PDT on Mar 9th 2008
			time.set(2008, 2, 9);
			range = TaskActivityUtil.getWeekOf(time.getTime());
			assertEquals(0, range.getStartDate().get(Calendar.DST_OFFSET));
			assertEquals(HOUR, range.getEndDate().get(Calendar.DST_OFFSET));
			assertTrue(WeekDateRange.isWeekRange(range.getStartDate(), range.getEndDate()));
			range.getStartDate().setTimeInMillis(range.getStartDate().getTimeInMillis() + 1);
			assertFalse(WeekDateRange.isWeekRange(range.getStartDate(), range.getEndDate()), "1 ms too long");
			range = TaskActivityUtil.getDayOf(time.getTime());
			assertFalse(WeekDateRange.isWeekRange(range.getStartDate(), range.getEndDate()));

			// PDT changes to PST on Nov 2nd 2008
			time.set(2008, 10, 2);
			range = TaskActivityUtil.getWeekOf(time.getTime());
			assertEquals(HOUR, range.getStartDate().get(Calendar.DST_OFFSET));
			assertEquals(0, range.getEndDate().get(Calendar.DST_OFFSET));
			assertTrue(WeekDateRange.isWeekRange(range.getStartDate(), range.getEndDate()));
			range.getStartDate().setTimeInMillis(range.getStartDate().getTimeInMillis() - 1);
			assertFalse(WeekDateRange.isWeekRange(range.getStartDate(), range.getEndDate()), "1 ms too short");
		} finally {
			TimeZone.setDefault(defaultTimeZone);
		}
	}

	@Test
	public void testIsDayRange() {
		TimeZone defaultTimeZone = TimeZone.getDefault();
		try {
			TimeZone.setDefault(TimeZone.getTimeZone("PST"));
			Calendar time = TaskActivityUtil.getCalendar();
			time.set(2008, 9, 1);
			DateRange range = TaskActivityUtil.getDayOf(time.getTime());
			assertTrue(DayDateRange.isDayRange(range.getStartDate(), range.getEndDate()));
			range.getStartDate().setTimeInMillis(range.getStartDate().getTimeInMillis() + 1);
			assertTrue(DayDateRange.isDayRange(range.getStartDate(), range.getEndDate()),
					"1 ms longer than a day, expected to be within legal interval");
			range.getStartDate().setTimeInMillis(range.getStartDate().getTimeInMillis() + HOUR);
			assertFalse(DayDateRange.isDayRange(range.getStartDate(), range.getEndDate()),
					"1 hour + 1 ms longer than a day");
			range.getStartDate().setTimeInMillis(range.getStartDate().getTimeInMillis() - 2 * HOUR - 2);
			assertFalse(DayDateRange.isDayRange(range.getStartDate(), range.getEndDate()),
					"1 hour + 1 ms shorter than a day");
			range = TaskActivityUtil.getDayOf(time.getTime());
			assertTrue(DayDateRange.isDayRange(range.getStartDate(), range.getEndDate()), "a week");

			// PDT changes to PST on Mar 9th 2008
			time.set(2008, 2, 9);
			range = TaskActivityUtil.getDayOf(time.getTime());
			assertEquals(0, range.getStartDate().get(Calendar.DST_OFFSET));
			assertEquals(HOUR, range.getEndDate().get(Calendar.DST_OFFSET));
			assertTrue(DayDateRange.isDayRange(range.getStartDate(), range.getEndDate()));
			range.getStartDate().setTimeInMillis(range.getStartDate().getTimeInMillis() + 1);
			assertFalse(DayDateRange.isDayRange(range.getStartDate(), range.getEndDate()), "1 ms too long");

			// PST changes to PDT on Nov 2nd 2008
			time.set(2008, 10, 2);
			range = TaskActivityUtil.getDayOf(time.getTime());
			assertEquals(HOUR, range.getStartDate().get(Calendar.DST_OFFSET));
			assertEquals(0, range.getEndDate().get(Calendar.DST_OFFSET));
			assertTrue(DayDateRange.isDayRange(range.getStartDate(), range.getEndDate()));
			range.getStartDate().setTimeInMillis(range.getStartDate().getTimeInMillis() - 1);
			assertFalse(DayDateRange.isDayRange(range.getStartDate(), range.getEndDate()), "1 ms too short");
		} finally {
			TimeZone.setDefault(defaultTimeZone);
		}
	}

	@Test
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
