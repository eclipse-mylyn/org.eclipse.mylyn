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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.commons.core.DelegatingProgressMonitor;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.index.core.TaskListIndex;
import org.eclipse.mylyn.internal.tasks.index.core.TaskListIndex.IndexField;
import org.eclipse.mylyn.internal.tasks.index.core.TaskListIndex.TaskCollector;
import org.eclipse.mylyn.internal.tasks.index.tests.util.MockTestContext;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author David Green
 */
public class TaskListIndexTest {

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

	private MockTestContext context;

	private TaskListIndex index;

	private File tempDir;

	@Before
	public void setup() throws IOException {
		tempDir = File.createTempFile(TaskListIndexTest.class.getSimpleName(), ".tmp");
		tempDir.delete();
		tempDir.mkdirs();

		assertTrue(tempDir.exists() && tempDir.isDirectory());

		context = new MockTestContext();
	}

	@After
	public void tearDown() {
		if (index != null) {
			try {
				index.waitUntilIdle();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			index.close();
			index = null;
		}
		if (tempDir != null) {
			delete(tempDir);
			assertFalse(tempDir.exists());
		}
	}

	private void delete(File file) {
		if (file.isDirectory()) {
			File[] children = file.listFiles();
			if (children != null) {
				for (File child : children) {
					delete(child);
				}
			}
		}
		if (!file.delete()) {
			Logger.getLogger(TaskListIndexTest.class.getName()).severe("Cannot delete: " + file);
		}
	}

	private void setupIndex() {
		index = new TaskListIndex(context.getTaskList(), context.getDataManager(),
				(IRepositoryManager) context.getRepositoryManager(), tempDir, 0L);
		index.setDefaultField(IndexField.CONTENT);
		index.setReindexDelay(0L);
	}

	@Test
	public void testMatchesLocalTaskOnSummary() throws InterruptedException {
		setupIndex();

		ITask task = context.createLocalTask();

		index.waitUntilIdle();

		index.setDefaultField(IndexField.CONTENT);

		assertTrue(index.matches(task, task.getSummary()));
		assertFalse(index.matches(task, "" + System.currentTimeMillis()));

		index.setDefaultField(IndexField.SUMMARY);

		assertTrue(index.matches(task, task.getSummary()));
		assertFalse(index.matches(task, "" + System.currentTimeMillis()));
	}

	@Test
	public void testMatchesLocalTaskOnDescription() throws InterruptedException {
		setupIndex();

		ITask task = context.createLocalTask();

		index.waitUntilIdle();

		index.setDefaultField(IndexField.CONTENT);

		assertTrue(index.matches(task, ((LocalTask) task).getNotes()));
		assertFalse(index.matches(task, "unlikely-akjfsaow"));

		index.setDefaultField(IndexField.SUMMARY);

		assertFalse(index.matches(task, ((LocalTask) task).getNotes()));
	}

	@Test
	public void testMatchesRepositoryTaskOnSummary() throws InterruptedException, CoreException {
		setupIndex();

		ITask task = context.createRepositoryTask();

		index.waitUntilIdle();

		index.setDefaultField(IndexField.CONTENT);

		assertTrue(index.matches(task, task.getSummary()));
		assertFalse(index.matches(task, "" + System.currentTimeMillis()));

		index.setDefaultField(IndexField.SUMMARY);

		assertTrue(index.matches(task, task.getSummary()));
		assertFalse(index.matches(task, "" + System.currentTimeMillis()));
	}

	@Test
	public void testMatchesRepositoryTaskOnDescription() throws InterruptedException, CoreException {
		setupIndex();

		ITask task = context.createRepositoryTask();

		index.waitUntilIdle();

		index.setDefaultField(IndexField.CONTENT);

		TaskData taskData = context.getDataManager().getTaskData(task);
		assertNotNull(taskData);

		TaskMapper taskMapping = context.getMockRepositoryConnector().getTaskMapping(taskData);

		assertTrue(index.matches(task, taskMapping.getDescription()));
		assertFalse(index.matches(task, "unlikely-akjfsaow"));

		index.setDefaultField(IndexField.SUMMARY);

		assertFalse(index.matches(task, taskMapping.getDescription()));
	}

	@Test
	public void testFind() throws InterruptedException {
		setupIndex();

		ITask task = context.createLocalTask();

		index.waitUntilIdle();

		index.setDefaultField(IndexField.SUMMARY);

		TestTaskCollector collector = new TestTaskCollector();
		index.find(task.getSummary(), collector, 1000);

		assertEquals(1, collector.getTasks().size());
		assertTrue(collector.getTasks().contains(task));
	}

	@Test
	public void testMatchesRepositoryTaskOnCreationDate() throws InterruptedException, CoreException {
		setupIndex();

		ITask task = context.createRepositoryTask();

		Date creationDate = task.getCreationDate();
		assertNotNull(creationDate);

		index.waitUntilIdle();

		assertFalse(index.matches(task, IndexField.CREATION_DATE.fieldName() + ":[20010101 TO 20010105]"));

		String matchDate = new SimpleDateFormat("yyyyMMdd").format(creationDate);
		matchDate = Integer.toString(Integer.parseInt(matchDate) + 2);

		String patternString = IndexField.CREATION_DATE.fieldName() + ":[20111019 TO " + matchDate + "]";

		System.out.println(patternString);

		assertTrue(index.matches(task, patternString));
	}

	@Test
	public void testMatchesOnRepositoryUrl() throws Exception {
		setupIndex();

		ITask repositoryTask = context.createRepositoryTask();
		ITask localTask = context.createLocalTask();

		index.waitUntilIdle();

		index.setDefaultField(IndexField.CONTENT);

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

		String repositoryUrlQuery = content + " AND " + IndexField.REPOSITORY_URL.fieldName() + ":\""
				+ index.escapeFieldValue(repositoryTask.getRepositoryUrl()) + "\"";
		assertFalse(index.matches(localTask, repositoryUrlQuery));
		assertTrue(index.matches(repositoryTask, repositoryUrlQuery));
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
		index.setDefaultField(IndexField.CONTENT);

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
		index.setDefaultField(IndexField.CONTENT);

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

		Assert.assertTrue(index.matches(repositoryTask,
				IndexField.IDENTIFIER.fieldName() + ":" + index.escapeFieldValue(repositoryTask.getHandleIdentifier())));
	}

}
