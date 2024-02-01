/*******************************************************************************
 * Copyright (c) 2011, 2013 Tasktop Technologies.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.context.tasks.tests;

import java.util.Calendar;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.ui.RefactorRepositoryUrlOperation;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.tests.TaskTestUtil;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryQuery;
import org.eclipse.mylyn.tasks.tests.connector.MockTask;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * @author Robert Elves
 * @author Steffen Pingel
 */
public class RefactorRepositoryUrlOperationTest extends TestCase {

	private TaskList taskList;

	@Override
	@Before
	public void setUp() throws Exception {
		taskList = TasksUiPlugin.getTaskList();
		TaskTestUtil.resetTaskList();
	}

	@Test
	public void testMigrateQueryUrlHandles() throws Exception {
		RepositoryQuery query = new MockRepositoryQuery("mquery");
		query.setRepositoryUrl("http://foo.bar");
		query.setUrl("http://foo.bar/b");
		taskList.addQuery(query);
		assertTrue(taskList.getRepositoryQueries("http://foo.bar").size() > 0);
		new RefactorRepositoryUrlOperation("http://foo.bar", "http://bar.baz").run(new NullProgressMonitor());
		assertTrue(taskList.getRepositoryQueries("http://foo.bar").size() == 0);
		assertTrue(taskList.getRepositoryQueries("http://bar.baz").size() > 0);
		IRepositoryQuery changedQuery = taskList.getRepositoryQueries("http://bar.baz").iterator().next();
		assertEquals("http://bar.baz/b", changedQuery.getUrl());
	}

	@Test
	public void testRefactorMetaContextHandles() throws Exception {
		Calendar now = Calendar.getInstance();
		String firstUrl = "http://repository1.com/bugs";
		String secondUrl = "http://repository2.com/bugs";
		AbstractTask task1 = new MockTask(firstUrl, "1");
		AbstractTask task2 = new MockTask(firstUrl, "2");
		taskList.addTask(task1);
		taskList.addTask(task2);
		Calendar startDate = Calendar.getInstance();
		startDate.setTimeInMillis(now.getTimeInMillis());
		Calendar endDate = Calendar.getInstance();
		endDate.setTimeInMillis(now.getTimeInMillis());
		endDate.add(Calendar.MINUTE, 5);

		Calendar startDate2 = Calendar.getInstance();
		startDate2.setTimeInMillis(now.getTimeInMillis());
		startDate2.add(Calendar.MINUTE, 15);
		Calendar endDate2 = Calendar.getInstance();
		endDate2.setTimeInMillis(now.getTimeInMillis());
		endDate2.add(Calendar.MINUTE, 25);

		ContextCorePlugin.getContextManager().resetActivityMetaContext();
		InteractionContext metaContext = ContextCorePlugin.getContextManager().getActivityMetaContext();
		assertEquals(0, metaContext.getInteractionHistory().size());

		ContextCorePlugin.getContextManager()
				.processActivityMetaContextEvent(new InteractionEvent(InteractionEvent.Kind.ATTENTION,
						InteractionContextManager.ACTIVITY_STRUCTUREKIND_TIMING, task1.getHandleIdentifier(), "origin",
						null, InteractionContextManager.ACTIVITY_DELTA_ADDED, 1f, startDate.getTime(),
						endDate.getTime()));

		ContextCorePlugin.getContextManager()
				.processActivityMetaContextEvent(new InteractionEvent(InteractionEvent.Kind.ATTENTION,
						InteractionContextManager.ACTIVITY_STRUCTUREKIND_TIMING, task2.getHandleIdentifier(), "origin",
						null, InteractionContextManager.ACTIVITY_DELTA_ADDED, 1f, startDate2.getTime(),
						endDate2.getTime()));

		assertEquals(2, metaContext.getInteractionHistory().size());
		assertEquals(5 * 60 * 1000, TasksUiPlugin.getTaskActivityManager().getElapsedTime(task1));
		assertEquals(10 * 60 * 1000, TasksUiPlugin.getTaskActivityManager().getElapsedTime(task2));
		new RefactorRepositoryUrlOperation(firstUrl, secondUrl).run(new NullProgressMonitor());
		metaContext = ContextCorePlugin.getContextManager().getActivityMetaContext();
		assertEquals(2, metaContext.getInteractionHistory().size());
		assertEquals(5 * 60 * 1000,
				TasksUiPlugin.getTaskActivityManager().getElapsedTime(new MockTask(secondUrl, "1")));
		assertEquals(10 * 60 * 1000,
				TasksUiPlugin.getTaskActivityManager().getElapsedTime(new MockTask(secondUrl, "2")));
		assertEquals(secondUrl + "-1", metaContext.getInteractionHistory().get(0).getStructureHandle());
	}

}
