/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.tests.ui.editor;

import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.core.DateRange;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.internal.tasks.ui.editors.Messages;
import org.eclipse.mylyn.internal.tasks.ui.editors.PlanningPart;

/**
 * @author Steffen Pingel
 */
public class PlanningPartTest extends TestCase {

	private static final int DAY = 24 * 60 * 60 * 1000;

	private static final long WEEK = 7 * DAY;

	public void testGetLabelWeek() {
		DateRange range = TaskActivityUtil.getCurrentWeek();
		assertEquals(Messages.PlanningPart_This_Week, PlanningPart.getLabel(range));
		range = TaskActivityUtil.getWeekOf(new Date(0));
		assertEquals(Messages.PlanningPart_This_Week, PlanningPart.getLabel(range));
		range = TaskActivityUtil.getNextWeek();
		assertEquals(Messages.PlanningPart_Next_Week, PlanningPart.getLabel(range));
		range = TaskActivityUtil.getWeekOf(new Date(System.currentTimeMillis() + WEEK));
		assertEquals(Messages.PlanningPart_Next_Week, PlanningPart.getLabel(range));
		range = TaskActivityUtil.getWeekOf(new Date(System.currentTimeMillis() + 2 * WEEK));
		assertEquals(Messages.PlanningPart_Later, PlanningPart.getLabel(range));
		range = TaskActivityUtil.getWeekOf(new Date(System.currentTimeMillis() + 3 * WEEK));
		assertEquals(Messages.PlanningPart_Later, PlanningPart.getLabel(range));
		range = TaskActivityUtil.getWeekOf(new Date(System.currentTimeMillis() - WEEK));
		assertEquals(Messages.PlanningPart_This_Week, PlanningPart.getLabel(range));
		range = TaskActivityUtil.getWeekOf(new Date(System.currentTimeMillis() - 2 * WEEK));
		assertEquals(Messages.PlanningPart_This_Week, PlanningPart.getLabel(range));
	}

	public void testGetLabelDay() {
		DateRange range = TaskActivityUtil.getDayOf(new Date());
		assertEquals(Messages.PlanningPart_Today, PlanningPart.getLabel(range));
		range = TaskActivityUtil.getDayOf(new Date(System.currentTimeMillis() - DAY));
		assertEquals(Messages.PlanningPart_Today, PlanningPart.getLabel(range));
		range = TaskActivityUtil.getDayOf(TaskActivityUtil.getCurrentWeek().getStartDate().getTime());
		assertEquals(Messages.PlanningPart_Today, PlanningPart.getLabel(range));
		range = TaskActivityUtil.getDayOf(new Date(0));
		assertEquals(Messages.PlanningPart_Today, PlanningPart.getLabel(range));

		Calendar cal = TaskActivityUtil.getCalendar();
		if (cal.get(Calendar.DAY_OF_WEEK) != TaskActivityUtil.getLastDayOfWeek(cal)) {
			range = TaskActivityUtil.getDayOf(new Date(System.currentTimeMillis() + DAY));
			assertEquals(Messages.PlanningPart_This_Week, PlanningPart.getLabel(range));
		}
		range = TaskActivityUtil.getDayOf(new Date(System.currentTimeMillis() + 7 * DAY));
		assertEquals(Messages.PlanningPart_Next_Week, PlanningPart.getLabel(range));
		cal = TaskActivityUtil.getNextWeek().getEndDate();
		cal.add(Calendar.DAY_OF_YEAR, 1);
		range = TaskActivityUtil.getDayOf(cal.getTime());
		assertEquals(Messages.PlanningPart_Later, PlanningPart.getLabel(range));
		range = TaskActivityUtil.getDayOf(new Date(System.currentTimeMillis() + 14 * DAY));
		assertEquals(Messages.PlanningPart_Later, PlanningPart.getLabel(range));
	}

}
