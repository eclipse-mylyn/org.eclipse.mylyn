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

package org.eclipse.mylyn.bugzilla.tests;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;

/**
 * @author Frank Becker
 */
public class BugzillaDateTimeTests extends AbstractBugzillaTest {

	public void testTimezones218() throws Exception {
		// could not test BugzillaAttribute.DEADLINE because not supported in 2.18.6
		TimeZone defaultTimeZone = TimeZone.getDefault();
		try {
			TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
			init218();
			ITask task = generateLocalTaskAndDownload("10");
			assertNotNull(task);
			TaskDataModel model = createModel(task);
			TaskData taskData = model.getTaskData();
			assertNotNull(taskData);

			TaskAttribute attribute = taskData.getRoot().getAttribute(BugzillaAttribute.CREATION_TS.getKey());
			attribute.setValue(attribute.getValue() + " PST");
			assertEquals("2006-05-08 15:04 PST", attribute.getValue());
			Date date = taskData.getAttributeMapper().getDateValue(attribute);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			assertEquals(2006, calendar.get(Calendar.YEAR));
			assertEquals(4, calendar.get(Calendar.MONTH));
			assertEquals(8, calendar.get(Calendar.DAY_OF_MONTH));
			assertEquals(23, calendar.get(Calendar.HOUR_OF_DAY));
			assertEquals(4, calendar.get(Calendar.MINUTE));
			assertEquals(0, calendar.get(Calendar.SECOND));

			attribute = taskData.getRoot().getAttribute(BugzillaAttribute.DELTA_TS.getKey());
			attribute.setValue(attribute.getValue() + " PST");
			assertEquals("2006-06-02 14:45:37 PST", attribute.getValue());
			date = taskData.getAttributeMapper().getDateValue(attribute);
			calendar.setTime(date);
			assertEquals(2006, calendar.get(Calendar.YEAR));
			assertEquals(5, calendar.get(Calendar.MONTH));
			assertEquals(2, calendar.get(Calendar.DAY_OF_MONTH));
			assertEquals(22, calendar.get(Calendar.HOUR_OF_DAY));
			assertEquals(45, calendar.get(Calendar.MINUTE));
			assertEquals(37, calendar.get(Calendar.SECOND));
			TaskAttribute attachment = taskData.getAttributeMapper().getAttributesByType(taskData,
					TaskAttribute.TYPE_ATTACHMENT).get(0);
			assertNotNull(attachment);

			attribute = attachment.getAttribute(BugzillaAttribute.DATE.getKey());
			attribute.setValue(attribute.getValue() + " PST");
			assertEquals("2006-05-26 19:38 PST", attribute.getValue());
			date = taskData.getAttributeMapper().getDateValue(attribute);
			calendar.setTime(date);
			assertEquals(2006, calendar.get(Calendar.YEAR));
			assertEquals(4, calendar.get(Calendar.MONTH));
			assertEquals(27, calendar.get(Calendar.DAY_OF_MONTH));
			assertEquals(3, calendar.get(Calendar.HOUR_OF_DAY));
			assertEquals(38, calendar.get(Calendar.MINUTE));
			assertEquals(0, calendar.get(Calendar.SECOND));

			TaskAttribute comment = taskData.getAttributeMapper().getAttributesByType(taskData,
					TaskAttribute.TYPE_COMMENT).get(0);
			assertNotNull(comment);
			attribute = comment.getAttribute(BugzillaAttribute.BUG_WHEN.getKey());
			attribute.setValue(attribute.getValue() + " PST");
			assertEquals("2006-05-08 15:05 PST", attribute.getValue());
			date = taskData.getAttributeMapper().getDateValue(attribute);
			calendar.setTime(date);
			assertEquals(2006, calendar.get(Calendar.YEAR));
			assertEquals(4, calendar.get(Calendar.MONTH));
			assertEquals(8, calendar.get(Calendar.DAY_OF_MONTH));
			assertEquals(23, calendar.get(Calendar.HOUR_OF_DAY));
			assertEquals(5, calendar.get(Calendar.MINUTE));
			assertEquals(0, calendar.get(Calendar.SECOND));
		} finally {
			TimeZone.setDefault(defaultTimeZone);
		}
	}

	public void testTimezones323() throws Exception {
		// could not test BugzillaAttribute.DEADLINE and CUSTOM_FIELDS because not supported in 3.2.3
		TimeZone defaultTimeZone = TimeZone.getDefault();
		try {
			TimeZone.setDefault(TimeZone.getTimeZone("GMT"));

			init323();
			ITask task = generateLocalTaskAndDownload("2");
			assertNotNull(task);
			TaskDataModel model = createModel(task);
			TaskData taskData = model.getTaskData();
			assertNotNull(taskData);
			TaskAttribute attribute = taskData.getRoot().getAttribute(BugzillaAttribute.CREATION_TS.getKey());
			attribute.setValue(attribute.getValue() + " PST");
			assertEquals("2009-02-12 13:40 PST", attribute.getValue());
			Date date = taskData.getAttributeMapper().getDateValue(attribute);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			assertEquals(2009, calendar.get(Calendar.YEAR));
			assertEquals(1, calendar.get(Calendar.MONTH));
			assertEquals(12, calendar.get(Calendar.DAY_OF_MONTH));
			assertEquals(21, calendar.get(Calendar.HOUR_OF_DAY));
			assertEquals(40, calendar.get(Calendar.MINUTE));
			assertEquals(0, calendar.get(Calendar.SECOND));

			attribute = taskData.getRoot().getAttribute(BugzillaAttribute.DELTA_TS.getKey());
			attribute.setValue(attribute.getValue() + " PST");
//			String val = attribute.getValue();
			assertEquals("2009-06-03 23:31:42 PST", attribute.getValue());
			date = taskData.getAttributeMapper().getDateValue(attribute);
			calendar.setTime(date);
			assertEquals(2009, calendar.get(Calendar.YEAR));
			assertEquals(5, calendar.get(Calendar.MONTH));
			assertEquals(4, calendar.get(Calendar.DAY_OF_MONTH));
			assertEquals(7, calendar.get(Calendar.HOUR_OF_DAY));
			assertEquals(31, calendar.get(Calendar.MINUTE));
			assertEquals(42, calendar.get(Calendar.SECOND));
			TaskAttribute attachment = taskData.getAttributeMapper().getAttributesByType(taskData,
					TaskAttribute.TYPE_ATTACHMENT).get(0);
			assertNotNull(attachment);

			attribute = attachment.getAttribute(BugzillaAttribute.DATE.getKey());
			attribute.setValue(attribute.getValue() + " PST");
			assertEquals("2009-05-07 17:07:56 PST", attribute.getValue());
			date = taskData.getAttributeMapper().getDateValue(attribute);
			calendar.setTime(date);
			assertEquals(2009, calendar.get(Calendar.YEAR));
			assertEquals(4, calendar.get(Calendar.MONTH));
			assertEquals(8, calendar.get(Calendar.DAY_OF_MONTH));
			assertEquals(1, calendar.get(Calendar.HOUR_OF_DAY));
			assertEquals(7, calendar.get(Calendar.MINUTE));
			assertEquals(56, calendar.get(Calendar.SECOND));

			TaskAttribute comment = taskData.getAttributeMapper().getAttributesByType(taskData,
					TaskAttribute.TYPE_COMMENT).get(0);
			assertNotNull(comment);
			attribute = comment.getAttribute(BugzillaAttribute.BUG_WHEN.getKey());
			attribute.setValue(attribute.getValue() + " PST");
			assertEquals("2009-02-17 17:29:35 PST", attribute.getValue());
			date = taskData.getAttributeMapper().getDateValue(attribute);
			calendar.setTime(date);
			assertEquals(2009, calendar.get(Calendar.YEAR));
			assertEquals(1, calendar.get(Calendar.MONTH));
			assertEquals(18, calendar.get(Calendar.DAY_OF_MONTH));
			assertEquals(1, calendar.get(Calendar.HOUR_OF_DAY));
			assertEquals(29, calendar.get(Calendar.MINUTE));
			assertEquals(35, calendar.get(Calendar.SECOND));

			int of = defaultTimeZone.getRawOffset();
			of = TimeZone.getDefault().getRawOffset();
			attribute = taskData.getRoot().getAttribute(BugzillaAttribute.DEADLINE.getKey());
			String val = attribute.getValue();
			assertEquals("2009-06-24", attribute.getValue());
			date = taskData.getAttributeMapper().getDateValue(attribute);
			calendar.setTime(date);
			assertEquals(2009, calendar.get(Calendar.YEAR));
			assertEquals(5, calendar.get(Calendar.MONTH));
			assertEquals(24, calendar.get(Calendar.DAY_OF_MONTH));
			assertEquals(0, calendar.get(Calendar.HOUR_OF_DAY));
			assertEquals(0, calendar.get(Calendar.MINUTE));
			assertEquals(0, calendar.get(Calendar.SECOND));
		} finally {
			TimeZone.setDefault(defaultTimeZone);
		}
	}
}
