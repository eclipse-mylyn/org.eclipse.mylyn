/*******************************************************************************
 * Copyright (c) 2015, 2022 Blaine Lewis and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Blaine Lewis
 *     ArSysOp - adapt to SimRel 2022-12
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.reviews.internal.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.internal.tasks.core.TaskContainerDelta;
import org.eclipse.mylyn.internal.tasks.core.TaskContainerDelta.Kind;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataManager;
import org.eclipse.mylyn.reviews.core.spi.ReviewsConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

@SuppressWarnings({ "nls", "restriction" })
public class TaskReviewMappingStoreTest {

	final String taskUrl1 = "https://bugs.eclipse.org/bugs/show_bug.cgi?id=477635";

	final String taskUrl2 = "https://bugs.eclipse.org/bugs/show_bug.cgi?id=345343";

	final String notATaskUrl = "www.hello.com";

	ITask task1;

	ITask task2;

	final String reviewUrl1 = "https://git.eclipse.org/r/#/c/56269/";

	final String reviewUrl2 = "https://git.eclipse.org/r/#/c/43534/";

	final String reviewUrlNoTask = "https://git.eclipse.org/r/#/c/12333/";

	final String descriptionWithTaskUrl1 = "477635: [UCOSP] contribute reviews section to task editor showing"
			+ " associated reviews Change-Id: I3a38d375688aad7be36bfd58c3311d692eb51ed Task-Url: " + taskUrl1
			+ " Signed-off-by: Blaine Lewis <Blaine1@ualberta.ca>";

	final String descriptionWithTaskUrl2 = "477635: [UCOSP] contribute reviews section to task editor showing"
			+ " associated reviews Change-Id: I3a38d375688aad7be36bfd58c3311d692eb51ed Task-Url: " + taskUrl2
			+ " Signed-off-by: Blaine Lewis <Blaine1@ualberta.ca>";

	final String descriptionWithReviewAndTaskUrl = "477635: [UCOSP] contribute reviews section to task editor showing"
			+ " associated reviews " + reviewUrl2 + " Change-Id: I3a38d375688aad7be36bfd58c3311d692eb51ed Task-Url: "
			+ taskUrl1 + " Signed-off-by: Blaine Lewis <Blaine1@ualberta.ca>";

	final String descriptionWithNoTaskUrl = """
			477635: [UCOSP] contribute reviews section to task editor showing\
			 associated reviews Change-Id: I3a38d375688aad7be36bfd58c3311d692eb51ed\s\
			 Signed-off-by: Blaine Lewis <Blaine1@ualberta.ca>""";

	final String descriptionWithNotATaskUrl = "477635: [UCOSP] contribute reviews section to task editor showing"
			+ " associated reviews Change-Id: I3a38d375688aad7be36bfd58c3311d692eb51ed " + notATaskUrl
			+ " Signed-off-by: Blaine Lewis <Blaine1@ualberta.ca>";

	ITask review1;

	ITask review2;

	ITask reviewNoTask;

	private TaskDataManager taskDataManager;

	@Before
	public void setup() {
		review1 = new TaskTask("reviewKind", reviewUrl1, "3");
		review1.setUrl(reviewUrl1);

		review2 = new TaskTask("reviewKind", reviewUrl2, "4");
		review2.setUrl(reviewUrl2);

		reviewNoTask = new TaskTask("reviewKind", reviewUrlNoTask, "5");
		reviewNoTask.setUrl(reviewUrlNoTask);

		task1 = new TaskTask("taskKind", taskUrl1, "1");
		task1.setUrl(taskUrl1);

		task2 = new TaskTask("taskKind", taskUrl2, "2");
		task2.setUrl(taskUrl2);
	}

	public TaskReviewsMappingsStore getEmptyTaskReviewStore() {
		TaskRepositoryManager repositoryManager = mock(TaskRepositoryManager.class);
		ReviewsConnector reviewConnector = mock(ReviewsConnector.class);
		AbstractRepositoryConnector taskConnector = mock(AbstractRepositoryConnector.class);

		taskDataManager = mock(TaskDataManager.class);

		when(repositoryManager.getConnectorForRepositoryTaskUrl(ArgumentMatchers.anyString()))
		.thenReturn(reviewConnector);
		when(repositoryManager.getRepositoryConnector("reviewKind")).thenReturn(reviewConnector);
		when(repositoryManager.getRepositoryConnector("taskKind")).thenReturn(taskConnector);

		TaskReviewsMappingsStore taskReviewsMappingStore = new MockTaskReviewsMappingsStore(mock(TaskList.class),
				repositoryManager);

		return taskReviewsMappingStore;
	}

	public TaskData addReviewData(ITask review, String description) throws CoreException {
		TaskData reviewData = new TaskData(new TaskAttributeMapper(new TaskRepository("", "")), "", review.getUrl(),
				review.getTaskId());

		reviewData.getRoot().createMappedAttribute(TaskAttribute.DESCRIPTION).setValue(description);

		when(taskDataManager.getTaskData(review)).thenReturn(reviewData);

		return reviewData;
	}

	@Test
	public void testAdd() throws CoreException {
		TaskReviewsMappingsStore taskReviewsMappingStore = getEmptyTaskReviewStore();
		TaskData reviewData1 = addReviewData(review1, descriptionWithTaskUrl1);

		TaskContainerDelta delta = new TaskContainerDelta(review1, Kind.ADDED);
		taskReviewsMappingStore.addTaskAssocation(review1, reviewData1);
		taskReviewsMappingStore.containersChanged(Set.of(delta));

		assertTrue(taskReviewsMappingStore.getReviewUrls(taskUrl1).contains(reviewUrl1));
		assertTrue(taskReviewsMappingStore.getTaskUrl(review1).equals(taskUrl1));
	}

	@Test
	public void testAddTwice() throws CoreException {
		TaskReviewsMappingsStore taskReviewsMappingStore = getEmptyTaskReviewStore();
		TaskData reviewData1 = addReviewData(review1, descriptionWithTaskUrl1);

		TaskContainerDelta delta = new TaskContainerDelta(review1, Kind.ADDED);

		taskReviewsMappingStore.addTaskAssocation(review1, reviewData1);
		taskReviewsMappingStore.containersChanged(Set.of(delta));

		taskReviewsMappingStore.addTaskAssocation(review1, reviewData1);
		taskReviewsMappingStore.containersChanged(Set.of(delta));

		Collection<String> reviewUrls = taskReviewsMappingStore.getReviewUrls(taskUrl1);

		assertTrue(reviewUrls.contains(reviewUrl1));
		assertTrue(reviewUrls.size() == 1);
		assertTrue(taskReviewsMappingStore.getTaskUrl(review1).equals(taskUrl1));
	}

	@Test
	public void testAddMultiple() throws CoreException {
		TaskReviewsMappingsStore taskReviewsMappingStore = getEmptyTaskReviewStore();
		TaskData reviewData1 = addReviewData(review1, descriptionWithTaskUrl1);
		TaskData reviewData2 = addReviewData(review2, descriptionWithTaskUrl2);

		TaskContainerDelta delta1 = new TaskContainerDelta(review1, Kind.ADDED);
		TaskContainerDelta delta2 = new TaskContainerDelta(review2, Kind.ADDED);

		taskReviewsMappingStore.addTaskAssocation(review1, reviewData1);
		taskReviewsMappingStore.addTaskAssocation(review2, reviewData2);

		taskReviewsMappingStore.containersChanged(Set.of(delta1, delta2));

		assertTrue(taskReviewsMappingStore.getReviewUrls(taskUrl1).contains(reviewUrl1));
		assertTrue(taskReviewsMappingStore.getTaskUrl(review1).equals(taskUrl1));

		assertTrue(taskReviewsMappingStore.getReviewUrls(taskUrl2).contains(reviewUrl2));
		assertTrue(taskReviewsMappingStore.getTaskUrl(review2).equals(taskUrl2));
	}

	@Test
	public void testReviewNotLinkedToReview() throws CoreException {
		TaskReviewsMappingsStore taskReviewsMappingStore = getEmptyTaskReviewStore();
		TaskData reviewData1 = addReviewData(review1, descriptionWithReviewAndTaskUrl);

		TaskContainerDelta delta = new TaskContainerDelta(review1, Kind.ADDED);
		taskReviewsMappingStore.addTaskAssocation(review1, reviewData1);
		taskReviewsMappingStore.containersChanged(Set.of(delta));

		assertFalse(taskReviewsMappingStore.getReviewUrls(reviewUrl2).contains(reviewUrl1));
		assertTrue(taskReviewsMappingStore.getReviewUrls(taskUrl1).contains(reviewUrl1));
		assertTrue(taskReviewsMappingStore.getTaskUrl(review1).equals(taskUrl1));
	}

	@Test
	public void testRemove() throws CoreException {

		TaskReviewsMappingsStore taskReviewsMappingStore = getEmptyTaskReviewStore();
		TaskData reviewData1 = addReviewData(review1, descriptionWithTaskUrl1);

		TaskContainerDelta delta = new TaskContainerDelta(review1, Kind.ADDED);

		taskReviewsMappingStore.addTaskAssocation(review1, reviewData1);
		taskReviewsMappingStore.containersChanged(Set.of(delta));

		assertTrue(taskReviewsMappingStore.getReviewUrls(taskUrl1).contains(reviewUrl1));

		delta = new TaskContainerDelta(review1, Kind.DELETED);

		taskReviewsMappingStore.containersChanged(Set.of(delta));

		assertFalse(taskReviewsMappingStore.getReviewUrls(taskUrl1).contains(reviewUrl1));
		assertTrue(taskReviewsMappingStore.getTaskUrl(review1) == null);
	}

	@Test
	public void testUpdate() throws CoreException {
		TaskReviewsMappingsStore taskReviewsMappingStore = getEmptyTaskReviewStore();
		TaskData reviewData1 = addReviewData(review1, descriptionWithTaskUrl1);

		TaskContainerDelta delta = new TaskContainerDelta(review1, Kind.ADDED);

		taskReviewsMappingStore.addTaskAssocation(review1, reviewData1);
		taskReviewsMappingStore.containersChanged(Set.of(delta));

		assertTrue(taskReviewsMappingStore.getReviewUrls(taskUrl1).contains(reviewUrl1));

		reviewData1 = addReviewData(review1, descriptionWithTaskUrl2);

		delta = new TaskContainerDelta(review1, Kind.CONTENT);

		taskReviewsMappingStore.addTaskAssocation(review1, reviewData1);
		taskReviewsMappingStore.containersChanged(Set.of(delta));

		assertFalse(taskReviewsMappingStore.getReviewUrls(taskUrl1).contains(reviewUrl1));
		assertTrue(taskReviewsMappingStore.getReviewUrls(taskUrl2).contains(reviewUrl1));
		assertEquals(taskUrl2, taskReviewsMappingStore.getTaskUrl(review1));
	}

	@Test
	public void testUpdateNoTask() throws CoreException {
		TaskReviewsMappingsStore taskReviewsMappingStore = getEmptyTaskReviewStore();
		TaskData reviewData1 = addReviewData(review1, descriptionWithTaskUrl1);

		TaskContainerDelta delta = new TaskContainerDelta(review1, Kind.ADDED);

		taskReviewsMappingStore.addTaskAssocation(review1, reviewData1);
		taskReviewsMappingStore.containersChanged(Set.of(delta));

		assertTrue(taskReviewsMappingStore.getReviewUrls(taskUrl1).contains(reviewUrl1));

		reviewData1 = addReviewData(review1, descriptionWithNoTaskUrl);
		delta = new TaskContainerDelta(review1, Kind.CONTENT);

		taskReviewsMappingStore.addTaskAssocation(review1, reviewData1);
		taskReviewsMappingStore.containersChanged(Set.of(delta));

		assertFalse(taskReviewsMappingStore.getReviewUrls(taskUrl1).contains(reviewUrl1));
		assertTrue(taskReviewsMappingStore.getTaskUrl(review1) == null);
	}

	@Test
	public void testAddNoTask() throws CoreException {
		TaskReviewsMappingsStore taskReviewsMappingStore = getEmptyTaskReviewStore();
		TaskData reviewData1 = addReviewData(review1, descriptionWithNoTaskUrl);

		TaskContainerDelta delta = new TaskContainerDelta(review1, Kind.ADDED);

		taskReviewsMappingStore.addTaskAssocation(review1, reviewData1);
		taskReviewsMappingStore.containersChanged(Set.of(delta));

		assertTrue(taskReviewsMappingStore.getTaskUrl(review1) == null);
	}

	@Test
	public void testAddANonTaskUrl() throws CoreException {
		TaskReviewsMappingsStore taskReviewsMappingStore = getEmptyTaskReviewStore();
		TaskData reviewData1 = addReviewData(review1, descriptionWithNotATaskUrl);

		TaskContainerDelta delta = new TaskContainerDelta(review1, Kind.ADDED);
		taskReviewsMappingStore.addTaskAssocation(review1, reviewData1);
		taskReviewsMappingStore.containersChanged(Set.of(delta));
		assertTrue(taskReviewsMappingStore.getTaskUrl(review1) == null);
	}

	class MockTaskReviewsMappingsStore extends TaskReviewsMappingsStore {

		public MockTaskReviewsMappingsStore(TaskList taskList, TaskRepositoryManager repositoryManager) {
			super(taskList, repositoryManager);
		}

		@Override
		ITask getTaskByUrl(String url) {
			switch (url) {
				case taskUrl1:
					return task1;
				case taskUrl2:
					return task2;
				case reviewUrl1:
					return review1;
				case reviewUrl2:
					return review2;
			}
			return null;
		}

	}

}
