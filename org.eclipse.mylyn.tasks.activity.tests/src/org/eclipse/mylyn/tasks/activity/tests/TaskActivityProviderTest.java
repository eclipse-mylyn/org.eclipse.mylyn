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
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.tasks.activity.core.ActivityEvent;
import org.eclipse.mylyn.tasks.activity.core.IActivityManager;
import org.eclipse.mylyn.tasks.activity.core.TaskActivityScope;
import org.eclipse.mylyn.tasks.activity.core.spi.IActivitySession;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.junit.Test;

/**
 * @author Steffen Pingel
 */
public class TaskActivityProviderTest {

	protected List<ActivityEvent> events = new ArrayList<ActivityEvent>();

	@Test
	public void testActivityStream() throws Exception {
		IRepositoryManager repositoryManager = new TaskRepositoryManager();
		TaskList taskList = new TaskList();
		LocalTask searchTask = new LocalTask("1", "summary");
		LocalTask task1 = new LocalTask("2", "1: hit");
		task1.setCreationDate(new Date());
		LocalTask task2 = new LocalTask("3", "2: miss");
		task2.setCreationDate(new Date());
		taskList.addTask(task1);
		taskList.addTask(task2);
		TaskActivityProvider provider = new TaskActivityProvider(repositoryManager, taskList);
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

		ActivityEvent expected = new ActivityEvent(task1.getHandleIdentifier(), TaskActivityProvider.ID_PROVIDER,
				task1.getSummary(), task1.getCreationDate(), null);
		assertEquals(Collections.singletonList(expected), events);
	}

}
