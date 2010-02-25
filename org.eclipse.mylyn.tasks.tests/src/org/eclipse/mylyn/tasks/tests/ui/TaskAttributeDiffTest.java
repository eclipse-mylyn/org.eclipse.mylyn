/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.core.RepositoryModel;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.ui.notifications.TaskDataDiff;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * @author Robert Elves
 */
public class TaskAttributeDiffTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testDateDiff() {
		TaskData dataA = new TaskData(new MyTaskAttributeMapper(new TaskRepository("mock", "url")), "mock", "url",
				"1123");
		TaskAttribute attributeA = new TaskAttribute(dataA.getRoot(), "attributeA");
		attributeA.getMetaData().setKind(TaskAttribute.KIND_DEFAULT).setType(TaskAttribute.TYPE_DATE).setLabel(
				"someDate:");
		attributeA.setValue("1275068800000");

		TaskData dataB = new TaskData(new MyTaskAttributeMapper(new TaskRepository("mock", "url")), "mock", "url",
				"1123");
		TaskAttribute attributeB = new TaskAttribute(dataB.getRoot(), "attributeA");
		attributeB.getMetaData()
				.setKind(TaskAttribute.KIND_DEFAULT)
				.setType(TaskAttribute.TYPE_DATE)
				.setLabel("mydate");
		attributeB.setValue("1265068800000");

		TaskDataDiff diff = new TaskDataDiff(new RepositoryModel(new TaskList(), new TaskRepositoryManager()), dataA,
				dataB);
		assertEquals(" someDate: 2010/02/01 -> 2010/05/28", diff.toString());

	}

	static class MyTaskAttributeMapper extends TaskAttributeMapper {

		public MyTaskAttributeMapper(TaskRepository taskRepository) {
			super(taskRepository);
		}

		@Override
		public String getValueLabel(TaskAttribute taskAttribute) {
			if (taskAttribute.getMetaData().getType() == TaskAttribute.TYPE_DATE) {
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(Long.parseLong(taskAttribute.getValue()));
				return new SimpleDateFormat("yyyy/MM/dd").format(cal.getTime());
			}
			return super.getValueLabel(taskAttribute);
		}

		@Override
		public List<String> getValueLabels(TaskAttribute taskAttribute) {
			if (taskAttribute.getMetaData().getType() == TaskAttribute.TYPE_DATE) {
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(Long.parseLong(taskAttribute.getValue()));
				return Collections.singletonList(new SimpleDateFormat("yyyy/MM/dd").format(cal.getTime()));
			}
			return super.getValueLabels(taskAttribute);
		}

	}
}
