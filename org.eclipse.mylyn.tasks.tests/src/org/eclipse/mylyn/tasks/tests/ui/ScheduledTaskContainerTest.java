/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.tests.ui;

import java.util.Calendar;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.DateRange;
import org.eclipse.mylyn.internal.tasks.core.DayDateRange;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityManager;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.internal.tasks.core.WeekDateRange;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.tests.TaskTestUtil;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;

/**
 * @author Sam Davis
 */
public class ScheduledTaskContainerTest extends TestCase {
	private TaskActivityManager taskActivityManager;

	private long taskID;

	private TaskRepository repository;

	private final long instant = 1334355143000L;// arbitrary fixed point in time

	@Override
	protected void setUp() throws Exception {
		taskActivityManager = TasksUiPlugin.getTaskActivityManager();
		TaskTestUtil.resetTaskListAndRepositories();
		repository = new TaskRepository(MockRepositoryConnector.CONNECTOR_KIND, MockRepositoryConnector.REPOSITORY_URL);
		TasksUiPlugin.getRepositoryManager().addRepository(repository);
	}

	@Override
	protected void tearDown() throws Exception {
		taskActivityManager.clear();
	}

	public void testUSHawaiiDay() {
		runDayTest("US/Hawaii");
	}

	public void testCanadaPacificDay() {
		runDayTest("Canada/Pacific");
	}

	public void testBrazilWestDay() {
		runDayTest("Brazil/West");
	}

	public void testEuropeDublinDay() {
		runDayTest("Europe/Dublin");
	}

	public void testGreenwichDay() {
		runDayTest("Greenwich");
	}

	public void testPortugalDay() {
		runDayTest("Portugal");
	}

	public void testUniversalDay() {
		runDayTest("Universal");
	}

	public void testEuropeViennaDay() {
		runDayTest("Europe/Vienna");
	}

	public void testAsiaIstanbulDay() {
		runDayTest("Asia/Istanbul");
	}

	public void testAsiaDubaiDay() {
		runDayTest("Asia/Dubai");
	}

	public void testIndianComoroDay() {
		runDayTest("Indian/Comoro");
	}

	public void testAsiaMacauDay() {
		runDayTest("Asia/Macau");
	}

	public void testAustraliaSouthDay() {
		runDayTest("Australia/South");
	}

	public void testAsiaTokyoDay() {
		runDayTest("Asia/Tokyo");
	}

//	public void testPacificFijiDay() {
//		// Fails because we cannot distinguish UTC+12 to UTC+14 from UTC-12 to -10
//		runDayTest("Pacific/Fiji");
//	}

	public void testUSHawaiiWeek() {
		runWeekTest("US/Hawaii");
	}

	public void testCanadaPacificWeek() {
		runWeekTest("Canada/Pacific");
	}

	public void testBrazilWestWeek() {
		runWeekTest("Brazil/West");
	}

	public void testEuropeDublinWeek() {
		runWeekTest("Europe/Dublin");
	}

	public void testGreenwichWeek() {
		runWeekTest("Greenwich");
	}

	public void testPortugalWeek() {
		runWeekTest("Portugal");
	}

	public void testUniversalWeek() {
		runWeekTest("Universal");
	}

	public void testEuropeViennaWeek() {
		runWeekTest("Europe/Vienna");
	}

	public void testAsiaIstanbulWeek() {
		runWeekTest("Asia/Istanbul");
	}

	public void testAsiaDubaiWeek() {
		runWeekTest("Asia/Dubai");
	}

	public void testIndianComoroWeek() {
		runWeekTest("Indian/Comoro");
	}

	public void testAsiaMacauWeek() {
		runWeekTest("Asia/Macau");
	}

	public void testAustraliaSouthWeek() {
		runWeekTest("Australia/South");
	}

	public void testAsiaTokyoWeek() {
		runWeekTest("Asia/Tokyo");
	}

//	public void testPacificFijiWeek() {
//		// Fails because we cannot distinguish UTC+12 to UTC+14 from UTC-12 to -10
//		runWeekTest("Pacific/Fiji");
//	}

	/**
	 * Test day bins
	 */
	protected void runDayTest(String localTimezone) {
		// create scheduled tasks
		ITask taskUSHawaii = createTaskScheduledForDay(instant, "US/Hawaii");
		ITask taskCanadaPacific = createTaskScheduledForDay(instant, "Canada/Pacific");
		ITask taskCanadaEastern = createTaskScheduledForDay(instant, "Canada/Eastern");
		ITask taskEuropeWarsaw = createTaskScheduledForDay(instant, "Europe/Warsaw");
		ITask taskIndianComoro = createTaskScheduledForDay(instant, "Indian/Comoro");
		ITask taskAsiaTokyo = createTaskScheduledForDay(instant, "Asia/Tokyo");
		ITask taskAustraliaSydney = createTaskScheduledForDay(instant, "Australia/Sydney");
		// create due tasks
		ITask taskUSHawaiiDue = createTaskDueForDay(instant, "US/Hawaii");
		ITask taskCanadaPacificDue = createTaskDueForDay(instant, "Canada/Pacific");
		ITask taskCanadaEasternDue = createTaskDueForDay(instant, "Canada/Eastern");
		ITask taskEuropeWarsawDue = createTaskDueForDay(instant, "Europe/Warsaw");
		ITask taskIndianComoroDue = createTaskDueForDay(instant, "Indian/Comoro");
		ITask taskAsiaTokyoDue = createTaskDueForDay(instant, "Asia/Tokyo");
		ITask taskAustraliaSydneyDue = createTaskDueForDay(instant, "Australia/Sydney");
		ScheduledTaskContainer dayContainer = new ScheduledTaskContainer(taskActivityManager, getDayOf(instant,
				localTimezone));
		// check scheduled tasks are contained
		assertTrue(dayContainer.getChildren().contains(taskUSHawaii));
		assertTrue(dayContainer.getChildren().contains(taskCanadaPacific));
		assertTrue(dayContainer.getChildren().contains(taskCanadaEastern));
		assertTrue(dayContainer.getChildren().contains(taskEuropeWarsaw));
		assertTrue(dayContainer.getChildren().contains(taskIndianComoro));
		assertTrue(dayContainer.getChildren().contains(taskAsiaTokyo));
		assertTrue(dayContainer.getChildren().contains(taskAustraliaSydney));
		// check due tasks are contained
		assertTrue(dayContainer.getChildren().contains(taskUSHawaiiDue));
		assertTrue(dayContainer.getChildren().contains(taskCanadaPacificDue));
		assertTrue(dayContainer.getChildren().contains(taskCanadaEasternDue));
		assertTrue(dayContainer.getChildren().contains(taskEuropeWarsawDue));
		assertTrue(dayContainer.getChildren().contains(taskIndianComoroDue));
		assertTrue(dayContainer.getChildren().contains(taskAsiaTokyoDue));
		assertTrue(dayContainer.getChildren().contains(taskAustraliaSydneyDue));
		// surrounding days should be empty
		DayDateRange previousDay = getDayOf(instant, localTimezone);
		snapForwardNumDays(previousDay, -1);
		ScheduledTaskContainer previousDayContainer = new ScheduledTaskContainer(taskActivityManager, previousDay);
		assertTrue(previousDayContainer.getChildren().isEmpty());
		DayDateRange nextDay = getDayOf(instant, localTimezone);
		snapForwardNumDays(nextDay, 1);
		ScheduledTaskContainer nextDayContainer = new ScheduledTaskContainer(taskActivityManager, nextDay);
		assertTrue(nextDayContainer.getChildren().isEmpty());
	}

	/**
	 * test week bins
	 */
	protected void runWeekTest(String localTimezone) {
		ITask taskUSHawaii = createTaskScheduledForWeek(instant, "US/Hawaii");
		ITask taskCanadaPacific = createTaskScheduledForWeek(instant, "Canada/Pacific");
		ITask taskCanadaEastern = createTaskScheduledForWeek(instant, "Canada/Eastern");
		ITask taskEuropeWarsaw = createTaskScheduledForWeek(instant, "Europe/Warsaw");
		ITask taskIndianComoro = createTaskScheduledForWeek(instant, "Indian/Comoro");
		ITask taskAsiaTokyo = createTaskScheduledForWeek(instant, "Asia/Tokyo");
		ITask taskAustraliaSydney = createTaskScheduledForWeek(instant, "Australia/Sydney");
		ScheduledTaskContainer weekContainer = new ScheduledTaskContainer(taskActivityManager, getWeekOf(instant,
				localTimezone));
		assertTrue(weekContainer.getChildren().contains(taskUSHawaii));
		assertTrue(weekContainer.getChildren().contains(taskCanadaPacific));
		assertTrue(weekContainer.getChildren().contains(taskCanadaEastern));
		assertTrue(weekContainer.getChildren().contains(taskEuropeWarsaw));
		assertTrue(weekContainer.getChildren().contains(taskIndianComoro));
		assertTrue(weekContainer.getChildren().contains(taskAsiaTokyo));
		assertTrue(weekContainer.getChildren().contains(taskAustraliaSydney));
		// surrounding days should be empty
		DayDateRange previousDay = TaskActivityUtil.getDayOf(weekContainer.getDateRange().getStartDate().getTime());
		snapForwardNumDays(previousDay, -1);
		ScheduledTaskContainer previousDayContainer = new ScheduledTaskContainer(taskActivityManager, previousDay);
		assertTrue(previousDayContainer.getChildren().isEmpty());
		DayDateRange nextDay = TaskActivityUtil.getDayOf(weekContainer.getDateRange().getEndDate().getTime());
		snapForwardNumDays(nextDay, 1);
		ScheduledTaskContainer nextDayContainer = new ScheduledTaskContainer(taskActivityManager, nextDay);
		assertTrue(nextDayContainer.getChildren().isEmpty());
		// surrounding weeks should be empty
		WeekDateRange previousWeek = TaskActivityUtil.getWeekOf(previousDay.getStartDate().getTime());
		ScheduledTaskContainer previousWeekContainer = new ScheduledTaskContainer(taskActivityManager, previousWeek);
		assertTrue(previousWeekContainer.getChildren().isEmpty());
		WeekDateRange nextWeek = TaskActivityUtil.getWeekOf(nextDay.getStartDate().getTime());
		ScheduledTaskContainer nextWeekContainer = new ScheduledTaskContainer(taskActivityManager, nextWeek);
		assertTrue(nextWeekContainer.getChildren().isEmpty());
	}

	protected void snapForwardNumDays(DayDateRange previousDay, int num) {
		previousDay.getStartDate().add(Calendar.DAY_OF_MONTH, num);
		previousDay.getEndDate().add(Calendar.DAY_OF_MONTH, num);
	}

	protected AbstractTask createTaskScheduledForDay(long date, String timezone) {
		AbstractTask task = new LocalTask(taskID++ + "", "task " + taskID);
		taskActivityManager.setScheduledFor(task, getDayOf(date, timezone));
		return task;
	}

	protected AbstractTask createTaskDueForDay(long date, String timezone) {
		AbstractTask task = new LocalTask(taskID++ + "", "task " + taskID);
		taskActivityManager.setDueDate(task, getDayOf(date, timezone).getStartDate().getTime());
		return task;
	}

	protected AbstractTask createTaskScheduledForWeek(long date, String timezone) {
		AbstractTask task = new LocalTask(taskID++ + "", "task " + taskID);
		taskActivityManager.setScheduledFor(task, getWeekOf(date, timezone));
		return task;
	}

	protected DateRange getWeekOf(long date, String timezoneString) {
		TimeZone timeZone = TimeZone.getTimeZone(timezoneString);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(date);
		Calendar start = getSameDayInTimeZone(cal, timeZone);
		TaskActivityUtil.snapStartOfWorkWeek(start);
		Calendar end = getSameDayInTimeZone(cal, timeZone);
		TaskActivityUtil.snapEndOfWeek(end);
		WeekDateRange weekInTimezone = new WeekDateRange(start, end);
		return weekInTimezone;
	}

	protected DayDateRange getDayOf(long date, String timezoneString) {
		TimeZone timeZone = TimeZone.getTimeZone(timezoneString);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(date);
		Calendar start = getSameDayInTimeZone(cal, timeZone);
		Calendar end = getSameDayInTimeZone(cal, timeZone);
		TaskActivityUtil.snapEndOfDay(end);
		DayDateRange dayInTimezone = new DayDateRange(start, end);
		return dayInTimezone;
	}

	/**
	 * use the same y/m/d values but ignore the time
	 */
	protected Calendar getSameDayInTimeZone(Calendar cal, TimeZone timeZone) {
		Calendar day = Calendar.getInstance(timeZone);
		day.set(Calendar.YEAR, cal.get(Calendar.YEAR));
		day.set(Calendar.MONTH, cal.get(Calendar.MONTH));
		day.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH));
		TaskActivityUtil.snapStartOfDay(day);
		return day;
	}

}