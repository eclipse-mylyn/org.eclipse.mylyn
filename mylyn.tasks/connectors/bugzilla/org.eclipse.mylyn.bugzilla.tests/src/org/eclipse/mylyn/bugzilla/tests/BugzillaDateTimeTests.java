/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
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

package org.eclipse.mylyn.bugzilla.tests;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttributeMapper;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * @author Frank Becker
 * @author Robert Elves
 * @author Steffen Pingel
 */
public class BugzillaDateTimeTests extends TestCase {

	private TaskRepository repository;

	private BugzillaRepositoryConnector connector;

	@Override
	public void setUp() throws Exception {
		repository = new TaskRepository(BugzillaCorePlugin.CONNECTOR_KIND, "http://mylyn.org");
		connector = new BugzillaRepositoryConnector();
	}

	public void testDateFormatParsing() {
		TimeZone defaultTimeZone = TimeZone.getDefault();
		try {
			TimeZone.setDefault(TimeZone.getTimeZone("GMT"));

			TaskData taskData = new TaskData(new BugzillaAttributeMapper(repository, connector), "bugzilla", "repourl",
					"1");
			TaskAttribute attribute = taskData.getRoot().createAttribute(BugzillaAttribute.CREATION_TS.getKey());
			attribute.setValue("2006-05-08 15:04 PST");
			Date date = taskData.getAttributeMapper().getDateValue(attribute);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			assertEquals(2006, calendar.get(Calendar.YEAR));
			assertEquals(4, calendar.get(Calendar.MONTH));
			assertEquals(8, calendar.get(Calendar.DAY_OF_MONTH));
			assertEquals(23, calendar.get(Calendar.HOUR_OF_DAY));
			assertEquals(4, calendar.get(Calendar.MINUTE));
			assertEquals(0, calendar.get(Calendar.SECOND));

			attribute.setValue("2006-05-08 15:04:11 PST");
			date = taskData.getAttributeMapper().getDateValue(attribute);
			calendar = Calendar.getInstance();
			calendar.setTime(date);
			assertEquals(2006, calendar.get(Calendar.YEAR));
			assertEquals(4, calendar.get(Calendar.MONTH));
			assertEquals(8, calendar.get(Calendar.DAY_OF_MONTH));
			assertEquals(23, calendar.get(Calendar.HOUR_OF_DAY));
			assertEquals(4, calendar.get(Calendar.MINUTE));
			assertEquals(11, calendar.get(Calendar.SECOND));

			attribute.setValue("2006-05-08 15:04:11 -0800");
			date = taskData.getAttributeMapper().getDateValue(attribute);
			calendar = Calendar.getInstance();
			calendar.setTime(date);
			assertEquals(2006, calendar.get(Calendar.YEAR));
			assertEquals(4, calendar.get(Calendar.MONTH));
			assertEquals(8, calendar.get(Calendar.DAY_OF_MONTH));
			assertEquals(23, calendar.get(Calendar.HOUR_OF_DAY));
			assertEquals(4, calendar.get(Calendar.MINUTE));
			assertEquals(11, calendar.get(Calendar.SECOND));

			attribute.setValue("2006-05-08 15:04 -0800");
			date = taskData.getAttributeMapper().getDateValue(attribute);
			calendar = Calendar.getInstance();
			calendar.setTime(date);
			assertEquals(2006, calendar.get(Calendar.YEAR));
			assertEquals(4, calendar.get(Calendar.MONTH));
			assertEquals(8, calendar.get(Calendar.DAY_OF_MONTH));
			assertEquals(23, calendar.get(Calendar.HOUR_OF_DAY));
			assertEquals(4, calendar.get(Calendar.MINUTE));
			assertEquals(0, calendar.get(Calendar.SECOND));

			attribute.setValue("2006-05-08 15:04:11");
			date = taskData.getAttributeMapper().getDateValue(attribute);
			calendar = Calendar.getInstance();
			calendar.setTime(date);
			assertEquals(2006, calendar.get(Calendar.YEAR));
			assertEquals(4, calendar.get(Calendar.MONTH));
			assertEquals(8, calendar.get(Calendar.DAY_OF_MONTH));
			assertEquals(15, calendar.get(Calendar.HOUR_OF_DAY));
			assertEquals(4, calendar.get(Calendar.MINUTE));
			assertEquals(11, calendar.get(Calendar.SECOND));

			attribute.setValue("2006-05-08 15:04");
			date = taskData.getAttributeMapper().getDateValue(attribute);
			calendar = Calendar.getInstance();
			calendar.setTime(date);
			assertEquals(2006, calendar.get(Calendar.YEAR));
			assertEquals(4, calendar.get(Calendar.MONTH));
			assertEquals(8, calendar.get(Calendar.DAY_OF_MONTH));
			assertEquals(15, calendar.get(Calendar.HOUR_OF_DAY));
			assertEquals(4, calendar.get(Calendar.MINUTE));
			assertEquals(0, calendar.get(Calendar.SECOND));

			attribute.setValue("2006-05-08");
			date = taskData.getAttributeMapper().getDateValue(attribute);
			calendar = Calendar.getInstance();
			calendar.setTime(date);
			assertEquals(2006, calendar.get(Calendar.YEAR));
			assertEquals(4, calendar.get(Calendar.MONTH));
			assertEquals(8, calendar.get(Calendar.DAY_OF_MONTH));
			assertEquals(0, calendar.get(Calendar.HOUR_OF_DAY));
			assertEquals(0, calendar.get(Calendar.MINUTE));
			assertEquals(0, calendar.get(Calendar.SECOND));
		} finally {
			TimeZone.setDefault(defaultTimeZone);
		}
	}

}
