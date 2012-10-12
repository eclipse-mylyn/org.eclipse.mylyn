/*******************************************************************************
 * Copyright (c) 2011, 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.index.tests;

import static junit.framework.Assert.assertEquals;
import static org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.createTempFolder;
import static org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.deleteFolderRecursively;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import junit.framework.Assert;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.commons.core.DelegatingProgressMonitor;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.index.core.TaskListIndex;
import org.eclipse.mylyn.internal.tasks.index.core.TaskListIndex.TaskCollector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.data.DefaultTaskSchema;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;
import org.junit.Test;

/**
 * @author David Green
 */
public class TaskListIndexTest extends AbstractTaskListIndexTest {

	private static final org.eclipse.mylyn.tasks.core.data.AbstractTaskSchema.Field FIELD_SUMMARY = DefaultTaskSchema.getInstance().SUMMARY;

	private static final org.eclipse.mylyn.tasks.core.data.AbstractTaskSchema.Field FIELD_DATE_CREATION = DefaultTaskSchema.getInstance().DATE_CREATION;

	private static class TestTaskCollector extends TaskCollector {

		private final List<ITask> tasks = new ArrayList<ITask>();

		@Override
		public void collect(ITask task) {
			tasks.add(task);
		}

		public List<ITask> getTasks() {
			return tasks;
		}
	}

	@Test
	public void testMatchesLocalTaskOnSummary() throws InterruptedException {
		setupIndex();

		ITask task = context.createLocalTask();

		index.waitUntilIdle();

		index.setDefaultField(TaskListIndex.FIELD_CONTENT);

		assertTrue(index.matches(task, task.getSummary()));
		assertFalse(index.matches(task, "" + System.currentTimeMillis()));

		index.setDefaultField(FIELD_SUMMARY);

		assertTrue(index.matches(task, task.getSummary()));
		assertFalse(index.matches(task, "" + System.currentTimeMillis()));
	}

	@Test
	public void testMatchesLocalTaskOnDescription() throws InterruptedException {
		setupIndex();

		ITask task = context.createLocalTask();

		index.waitUntilIdle();

		index.setDefaultField(TaskListIndex.FIELD_CONTENT);

		assertTrue(index.matches(task, ((LocalTask) task).getNotes()));
		assertFalse(index.matches(task, "unlikely-akjfsaow"));

		index.setDefaultField(FIELD_SUMMARY);

		assertFalse(index.matches(task, ((LocalTask) task).getNotes()));
	}

	@Test
	public void testMatchesRepositoryTaskOnSummary() throws InterruptedException, CoreException {
		setupIndex();

		ITask task = context.createRepositoryTask();

		index.waitUntilIdle();

		index.setDefaultField(TaskListIndex.FIELD_CONTENT);

		assertTrue(index.matches(task, task.getSummary()));
		assertFalse(index.matches(task, "" + System.currentTimeMillis()));

		index.setDefaultField(FIELD_SUMMARY);

		assertTrue(index.matches(task, task.getSummary()));
		assertFalse(index.matches(task, "" + System.currentTimeMillis()));
	}

	@Test
	public void testMatchesRepositoryTaskOnDescription() throws InterruptedException, CoreException {
		setupIndex();

		ITask task = context.createRepositoryTask();

		index.waitUntilIdle();

		index.setDefaultField(TaskListIndex.FIELD_CONTENT);

		TaskData taskData = context.getDataManager().getTaskData(task);
		assertNotNull(taskData);

		TaskMapper taskMapping = context.getMockRepositoryConnector().getTaskMapping(taskData);

		assertTrue(index.matches(task, taskMapping.getDescription()));
		assertFalse(index.matches(task, "unlikely-akjfsaow"));

		index.setDefaultField(FIELD_SUMMARY);

		assertFalse(index.matches(task, taskMapping.getDescription()));
	}

	@Test
	public void testFind() throws InterruptedException {
		setupIndex();

		ITask task = context.createLocalTask();

		index.waitUntilIdle();

		index.setDefaultField(FIELD_SUMMARY);

		assertCanFindTask(task);
	}

	@Test
	public void testMatchesRepositoryTaskOnCreationDate() throws InterruptedException, CoreException {
		setupIndex();

		ITask task = context.createRepositoryTask();

		Date creationDate = task.getCreationDate();
		assertNotNull(creationDate);

		index.waitUntilIdle();

		assertFalse(index.matches(task, FIELD_DATE_CREATION.getIndexKey() + ":[20010101 TO 20010105]"));

		String matchDate = new SimpleDateFormat("yyyyMMdd").format(creationDate);
		matchDate = Integer.toString(Integer.parseInt(matchDate) + 2);

		String patternString = FIELD_DATE_CREATION.getIndexKey() + ":[20111019 TO " + matchDate + "]";

		System.out.println(patternString);

		assertTrue(index.matches(task, patternString));
	}

	@Test
	public void testMatchesOnRepositoryUrl() throws Exception {
		setupIndex();

		ITask repositoryTask = context.createRepositoryTask();
		ITask localTask = context.createLocalTask();

		index.waitUntilIdle();

		index.setDefaultField(TaskListIndex.FIELD_CONTENT);

		TaskData taskData = context.getDataManager().getTaskData(repositoryTask);

		// sanity
		assertNotNull(taskData);
		assertNotNull(taskData.getRepositoryUrl());
		assertFalse(taskData.getRepositoryUrl().length() == 0);

		// setup descriptions so that they will both match
		final String content = "RepositoryUrl";
		taskData.getRoot().getMappedAttribute(TaskAttribute.DESCRIPTION).setValue(content);
		((AbstractTask) localTask).setNotes(content);

		context.getDataManager().putSubmittedTaskData(repositoryTask, taskData, new DelegatingProgressMonitor());

		Set<ITask> changedElements = new HashSet<ITask>();
		changedElements.add(localTask);
		changedElements.add(repositoryTask);
		context.getTaskList().notifyElementsChanged(changedElements);

		index.waitUntilIdle();

		assertTrue(index.matches(localTask, content));
		assertTrue(index.matches(repositoryTask, content));

		String repositoryUrlQuery = content + " AND " + TaskListIndex.FIELD_REPOSITORY_URL.getIndexKey() + ":\""
				+ index.escapeFieldValue(repositoryTask.getRepositoryUrl()) + "\"";
		assertFalse(index.matches(localTask, repositoryUrlQuery));
		assertTrue(index.matches(repositoryTask, repositoryUrlQuery));
	}

	@Test
	public void testMatchesOnTaskKey() throws Exception {
		setupIndex();

		ITask repositoryTask = context.createRepositoryTask();

		index.waitUntilIdle();

		index.setDefaultField(TaskListIndex.FIELD_CONTENT);

		TaskData taskData = context.getDataManager().getTaskData(repositoryTask);

		// sanity
		assertNotNull(taskData);
		assertNotNull(taskData.getRoot().getMappedAttribute(TaskAttribute.TASK_KEY));

		String taskKey = repositoryTask.getTaskKey();
		assertTrue(taskKey.length() > 1);

		final String querySuffix = " AND " + TaskListIndex.FIELD_CONTENT.getIndexKey() + ":\""
				+ index.escapeFieldValue(taskData.getRoot().getMappedAttribute(TaskAttribute.SUMMARY).getValue())
				+ "\"";

		assertTrue(index.matches(repositoryTask, TaskListIndex.FIELD_TASK_KEY.getIndexKey() + ":" + taskKey
				+ querySuffix));

		// does not match on task key prefix
		assertTrue(index.matches(repositoryTask,
				TaskListIndex.FIELD_TASK_KEY.getIndexKey() + ":" + taskKey.substring(0, taskKey.length() - 1)
						+ querySuffix));
	}

	@Test
	public void testMatchesSummaryWithExpectedQueryBehaviour() throws InterruptedException {
		setupIndex();

		ITask task = context.createLocalTask();
		task.setSummary("one two three");

		context.getTaskList().notifyElementsChanged(Collections.singleton(task));

		index.waitUntilIdle();

		index.setDefaultField(FIELD_SUMMARY);

		// default search (without logical operators) should behave as a prefix search
		assertTrue(index.matches(task, "one"));
		assertTrue(index.matches(task, "two"));
		assertTrue(index.matches(task, "three"));
		assertTrue(index.matches(task, "thr"));
		assertFalse(index.matches(task, "one three"));

		// wildcard search should not match multiple terms separated by whitespace
		assertFalse(index.matches(task, "one*three"));

		// wildcard search should match multiple characters in a single term
		assertTrue(index.matches(task, "t*ee"));

		// logical operator makes it work with multiple terms
		assertTrue(index.matches(task, "one AND three"));
		assertTrue(index.matches(task, "one OR three"));
		assertTrue(index.matches(task, "one AND thr"));

		// logical operator requiring a non-existant term should not match
		assertFalse(index.matches(task, "one AND four"));
	}

	@Test
	public void testCharacterEscaping() {
		setupIndex();
		for (String special : new String[] { "+", "-", "&&", "||", "!", "(", ")", "{", "}", "[", "]", "^", "\"", "~",
				"*", "?", ":", "\\" }) {
			assertEquals("a\\" + special + "b", index.escapeFieldValue("a" + special + "b"));
		}
	}

	@Test
	public void testAttributeMetadataAffectsIndexing() throws CoreException, InterruptedException {
		setupIndex();

		ITask repositoryTask = context.createRepositoryTask();

		index.waitUntilIdle();
		index.setDefaultField(TaskListIndex.FIELD_CONTENT);

		TaskData taskData = context.getDataManager().getTaskData(repositoryTask);

		// sanity
		assertNotNull(taskData);

		final String content = "c" + System.currentTimeMillis();

		// setup data so that it will match
		TaskAttribute attribute = taskData.getRoot().createAttribute("unusualIndexedAttribute");
		attribute.setValue(content);

		// update
		context.getDataManager().putSubmittedTaskData(repositoryTask, taskData, new DelegatingProgressMonitor());

		// verify index doesn't match search term
		assertFalse(index.matches(repositoryTask, content));

		// now make data indexable
		attribute.getMetaData().putValue(TaskAttribute.META_INDEXED_AS_CONTENT, "true");
		// update
		context.getDataManager().putSubmittedTaskData(repositoryTask, taskData, new DelegatingProgressMonitor());

		// should now match
		assertTrue(index.matches(repositoryTask, content));
	}

	/**
	 * Verify that multiple threads can concurrently use the index to find tasks, i.e. that no threads are blocked from
	 * finding tasks by other threads.
	 */
	@Test
	public void testMultithreadedAccessOnFind() throws CoreException, InterruptedException, ExecutionException {
		setupIndex();

		final ITask repositoryTask = context.createRepositoryTask();

		index.waitUntilIdle();
		index.setDefaultField(TaskListIndex.FIELD_CONTENT);

		final int nThreads = 10;
		final int[] concurrencyLevel = new int[1];
		ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
		try {
			Collection<Callable<Object>> tasks = new HashSet<Callable<Object>>();
			for (int x = 0; x < nThreads; ++x) {
				tasks.add(new Callable<Object>() {

					public Object call() throws Exception {
						final int[] hitCount = new int[1];
						index.find(repositoryTask.getSummary(), new TaskCollector() {

							@Override
							public void collect(ITask task) {
								synchronized (concurrencyLevel) {
									++concurrencyLevel[0];
									if (concurrencyLevel[0] < nThreads) {
										try {
											concurrencyLevel.wait(5000L);
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
									} else {
										concurrencyLevel.notifyAll();
									}
								}
								++hitCount[0];
							}
						}, 100);
						return hitCount[0] == 1;
					}
				});
			}
			List<Future<Object>> futures = executorService.invokeAll(tasks);
			for (Future<Object> future : futures) {
				assertEquals(Boolean.TRUE, future.get());
			}
			Assert.assertEquals(nThreads, concurrencyLevel[0]);
		} finally {
			executorService.shutdownNow();
		}
	}

	@Test
	public void testRepositoryUrlChanged() throws InterruptedException, CoreException {
		setupIndex();

		ITask repositoryTask = context.createRepositoryTask();
		final String originalHandle = repositoryTask.getHandleIdentifier();

		index.waitUntilIdle();

		final String newUrl = context.getMockRepository().getRepositoryUrl() + "/changed";

		context.refactorMockRepositoryUrl(newUrl);

		Assert.assertFalse(originalHandle.equals(repositoryTask.getHandleIdentifier()));

		index.waitUntilIdle();

		Assert.assertTrue(index.matches(
				repositoryTask,
				TaskListIndex.FIELD_IDENTIFIER.getIndexKey() + ":"
						+ index.escapeFieldValue(repositoryTask.getHandleIdentifier())));
	}

	@Test
	public void testSetLocation() throws InterruptedException, IOException {
		setupIndex();
		index.setDefaultField(FIELD_SUMMARY);

		ITask task = context.createLocalTask();

		index.waitUntilIdle();

		assertCanFindTask(task);

		File newLocation = createTempFolder(TaskListIndexTest.class.getSimpleName());
		try {
			assertEquals(0, newLocation.list().length);

			index.setLocation(newLocation);

			index.waitUntilIdle();
			assertCanFindTask(task);

			assertFalse(newLocation.list().length == 0);
		} finally {
			disposeIndex();
			deleteFolderRecursively(newLocation);
			assertFalse(newLocation.exists());
		}
	}

	private void assertCanFindTask(ITask task) {
		TestTaskCollector collector = new TestTaskCollector();
		index.find(task.getSummary(), collector, 1000);

		assertEquals(1, collector.getTasks().size());
		assertTrue(collector.getTasks().contains(task));
	}

}