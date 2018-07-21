/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.ui.views;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Calendar;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.DateRange;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityManager;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskScheduleContentProvider.StateTaskContainer;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("restriction")
public class PresentationFilterTest {

	private static boolean filterHidden;

	private static boolean filterNonMatching;

	private static IPreferenceStore preferenceStore;

	private static PresentationFilter filter;

	@BeforeClass
	public static void recordSettings() {
		preferenceStore = TasksUiPlugin.getDefault().getPreferenceStore();
		filterHidden = preferenceStore.getBoolean(ITasksUiPreferenceConstants.FILTER_HIDDEN);
		filterNonMatching = preferenceStore.getBoolean(ITasksUiPreferenceConstants.FILTER_NON_MATCHING);
	}

	@AfterClass
	public static void resetSettings() {
		preferenceStore.setValue(ITasksUiPreferenceConstants.FILTER_HIDDEN, filterHidden);
		preferenceStore.setValue(ITasksUiPreferenceConstants.FILTER_NON_MATCHING, filterNonMatching);
	}

	@Before
	public void setup() {
		preferenceStore.setValue(ITasksUiPreferenceConstants.FILTER_HIDDEN, false);
		preferenceStore.setValue(ITasksUiPreferenceConstants.FILTER_NON_MATCHING, false);
		filter = new PresentationFilter();
	}

	@Test
	public void readSettings() {
		preferenceStore.setValue(ITasksUiPreferenceConstants.FILTER_HIDDEN, true);
		preferenceStore.setValue(ITasksUiPreferenceConstants.FILTER_NON_MATCHING, false);
		filter.updateSettings();
		assertTrue(filter.isFilterHiddenQueries());
		assertFalse(filter.isFilterNonMatching());
		preferenceStore.setValue(ITasksUiPreferenceConstants.FILTER_HIDDEN, false);
		preferenceStore.setValue(ITasksUiPreferenceConstants.FILTER_NON_MATCHING, true);
		filter.updateSettings();
		assertFalse(filter.isFilterHiddenQueries());
		assertTrue(filter.isFilterNonMatching());
	}

	@Test
	public void filterHiddenQueries() {
		IRepositoryQuery visible = new RepositoryQuery("kind", "visible");
		IRepositoryQuery hidden = new RepositoryQuery("kind", "hidden");
		hidden.setAttribute(ITasksCoreConstants.ATTRIBUTE_HIDDEN, "true");
		assertTrue(filter.select(null, visible));
		assertTrue(filter.select(null, hidden));
		filter.setFilterHiddenQueries(true);
		assertTrue(filter.select(null, visible));
		assertFalse(filter.select(null, hidden));
	}

	@Test
	public void filterLocalTasks() {
		AbstractTaskCategory category = new TaskCategory("category");
		LocalTask task = new LocalTask("1", "task");
		LocalTask subTask = new LocalTask("2", "subtask");
		addChild(task, subTask);
		addChild(category, task);

		assertTrue(filter.select(category, task));
		assertTrue(filter.select(task, subTask));

		filter.setFilterNonMatching(true);

		assertTrue(filter.select(category, task));
		assertTrue(filter.select(task, subTask));
	}

	@Test
	public void filterSubTasksInQuery() {
		RepositoryQuery query = new RepositoryQuery("kind", "query");
		assertMatchingSubTask(query, query, true, true);
	}

	@Test
	public void filterSubTasksInCategory() {
		TaskCategory category = new TaskCategory("category");
		assertMatchingSubTask(category, category, true, true);
	}

	@Test
	public void filterScheduledSubTasks() {
		ScheduledTaskContainer schedule = new ScheduledTaskContainer(mock(TaskActivityManager.class),
				new DateRange(Calendar.getInstance()));
		RepositoryQuery query = new RepositoryQuery("kind", "query");
		assertMatchingSubTask(query, schedule, true, false);
	}

	@Test
	public void filterSubTasksInStateContainers() {
		StateTaskContainer schedule = mock(StateTaskContainer.class);
		RepositoryQuery query = new RepositoryQuery("kind", "query");
		assertMatchingSubTask(query, schedule, false, false);
	}

	private void assertMatchingSubTask(AbstractTaskContainer taskContainer, AbstractTaskContainer subTaskContainer,
			boolean subTasksVisibleInContainer, boolean subTaskVisibleInTask) {
		TaskTask task = new TaskTask("kind", "http://eclipse.org", "task");
		TaskTask subTask = new TaskTask("kind", "http://eclipse.org", "subTask");
		TaskTask matchingSubTask = new TaskTask("kind", "http://eclipse.org", "matchingSubTask");
		addChild(task, subTask);
		addChild(task, matchingSubTask);
		addChild(taskContainer, task);
		addChild(subTaskContainer, matchingSubTask);

		assertTrue(filter.select(taskContainer, task));
		assertTrue(filter.select(task, subTask));
		assertTrue(filter.select(task, matchingSubTask));
		assertTrue(filter.select(subTaskContainer, matchingSubTask));

		filter.setFilterNonMatching(true);

		assertTrue(filter.select(taskContainer, task));
		assertFalse(filter.select(task, subTask));
		assertEquals(subTaskVisibleInTask, filter.select(task, matchingSubTask));
		assertEquals(subTasksVisibleInContainer, filter.select(subTaskContainer, matchingSubTask));
	}

	private void addChild(AbstractTaskContainer container, AbstractTask task) {
		container.internalAddChild(task);
		task.getParentContainers().add(container);
	}
}
