/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.activity.tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.eclipse.mylyn.internal.tasks.activity.core.TaskActivityProvider;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.internal.tasks.index.core.TaskListIndex;
import org.eclipse.mylyn.internal.tasks.index.ui.IndexReference;
import org.eclipse.mylyn.tasks.activity.core.ActivityEvent;
import org.eclipse.mylyn.tasks.activity.core.IActivityManager;
import org.eclipse.mylyn.tasks.activity.core.TaskActivityScope;
import org.eclipse.mylyn.tasks.activity.core.spi.IActivitySession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Steffen Pingel
 * @author Timur Achmetow
 */
@SuppressWarnings("restriction")
public class TaskActivityProviderTest {

	protected List<ActivityEvent> events = new ArrayList<ActivityEvent>();

	private IndexReference reference;

	private TaskTask task1;

	@Before
	public void setUp() throws Exception {
		task1 = new TaskTask(LocalRepositoryConnector.CONNECTOR_KIND, LocalRepositoryConnector.REPOSITORY_URL, "2");
		task1.setSummary("1: hit http://task/url1");
		task1.setTaskKey("2");
		task1.setCreationDate(new Date());

		TaskTask task2 = new TaskTask(LocalRepositoryConnector.CONNECTOR_KIND, LocalRepositoryConnector.REPOSITORY_URL,
				"3");
		task2.setSummary("2: miss http://task/url2");
		task2.setTaskKey("3");
		task2.setCreationDate(new Date());

		reference = new IndexReference();
		TaskListIndex taskListIndex = reference.index();
		taskListIndex.getTaskList().addTask(task1);
		taskListIndex.getTaskList().addTask(task2);
		taskListIndex.waitUntilIdle();
	}

	@After
	public void tearDown() {
		((TaskList) reference.index().getTaskList()).reset();
		reference.dispose();
	}

	@Test
	public void testActivityStream() throws Exception {
		TaskTask searchTask = new TaskTask(LocalRepositoryConnector.CONNECTOR_KIND,
				LocalRepositoryConnector.REPOSITORY_URL, "1");
		searchTask.setSummary("summary");
		searchTask.setTaskKey("1");
		searchTask.setUrl("http://task/url1");

		TaskActivityProvider provider = new TaskActivityProvider();
		IActivitySession session = new IActivitySession() {
			public IActivityManager getManger() {
				return null;
			}

			public void fireActivityEvent(ActivityEvent event) {
				events.add(event);
			}
		};
		provider.open(session);
		provider.query(new TaskActivityScope(searchTask), null);

		ActivityEvent expected = new ActivityEvent(task1.getHandleIdentifier(), task1.getConnectorKind(),
				task1.getSummary(), task1.getCreationDate(), null);

		assertEquals(Collections.singletonList(expected), events);
	}
}