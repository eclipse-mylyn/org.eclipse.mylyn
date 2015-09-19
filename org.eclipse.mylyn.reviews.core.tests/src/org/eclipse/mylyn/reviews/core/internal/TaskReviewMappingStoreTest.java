/*******************************************************************************
 * Copyright (c) 2015 Blaine Lewis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Blaine Lewis
 *******************************************************************************/

package org.eclipse.mylyn.reviews.core.internal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.internal.tasks.core.TaskContainerDelta;
import org.eclipse.mylyn.internal.tasks.core.TaskContainerDelta.Kind;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataManager;
import org.eclipse.mylyn.reviews.core.spi.ReviewsConnector;
import org.eclipse.mylyn.reviews.internal.core.TaskReviewsMappingsStore;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import com.google.common.collect.ImmutableSet;

@SuppressWarnings("restriction")
public class TaskReviewMappingStoreTest {

	final String taskUrl1 = "https://bugs.eclipse.org/bugs/show_bug.cgi?id=477635";

	final String taskUrl2 = "https://bugs.eclipse.org/bugs/show_bug.cgi?id=345343";

	final String notATaskUrl = "www.hello.com";

	final ITask task1 = mock(ITask.class);

	final ITask task2 = mock(ITask.class);

	final String reviewUrl1 = "https://git.eclipse.org/r/#/c/56269/";

	final String reviewUrl2 = "https://git.eclipse.org/r/#/c/43534/";

	final String reviewUrlNoTask = "https://git.eclipse.org/r/#/c/12333/";

	final String descriptionWithTaskUrl1 = "477635: [UCOSP] contribute reviews section to task editor showing"
			+ " associated reviews Change-Id: I3a38d375688aad7be36bfd58c3311d692eb51ed Task-Url: " + taskUrl1
			+ " Signed-off-by: Blaine Lewis <Blaine1@ualberta.ca>";

	final String descriptionWithTaskUrl2 = "477635: [UCOSP] contribute reviews section to task editor showing"
			+ " associated reviews Change-Id: I3a38d375688aad7be36bfd58c3311d692eb51ed Task-Url: " + taskUrl2
			+ " Signed-off-by: Blaine Lewis <Blaine1@ualberta.ca>";

	final String descriptionWithNoTaskUrl = "477635: [UCOSP] contribute reviews section to task editor showing"
			+ " associated reviews Change-Id: I3a38d375688aad7be36bfd58c3311d692eb51ed "
			+ " Signed-off-by: Blaine Lewis <Blaine1@ualberta.ca>";

	final String descriptionWithNotATaskUrl = "477635: [UCOSP] contribute reviews section to task editor showing"
			+ " associated reviews Change-Id: I3a38d375688aad7be36bfd58c3311d692eb51ed " + notATaskUrl
			+ " Signed-off-by: Blaine Lewis <Blaine1@ualberta.ca>";

	final ITask review1 = mock(ITask.class);

	final ITask review2 = mock(ITask.class);

	final ITask reviewNoTask = mock(ITask.class);

	private TaskDataManager taskDataManager;

	@Before
	public void setup() {

		when(review1.getUrl()).thenReturn(reviewUrl1);
		when(review2.getUrl()).thenReturn(reviewUrl2);
		when(reviewNoTask.getUrl()).thenReturn(reviewUrlNoTask);
		when(task1.getUrl()).thenReturn(taskUrl1);
		when(task2.getUrl()).thenReturn(taskUrl2);
		when(task1.getTaskId()).thenReturn("1");
		when(task1.getTaskId()).thenReturn("2");
		when(review1.getTaskId()).thenReturn("3");
		when(review2.getTaskId()).thenReturn("4");
		when(reviewNoTask.getTaskId()).thenReturn("5");

	}

	public TaskReviewsMappingsStore getEmptyTaskReviewStore() {
		TaskRepositoryManager repositoryManager = mock(TaskRepositoryManager.class);
		ReviewsConnector connector = mock(ReviewsConnector.class);

		taskDataManager = mock(TaskDataManager.class);

		when(repositoryManager.getConnectorForRepositoryTaskUrl(Matchers.anyString())).thenReturn(connector);
		when(repositoryManager.getRepositoryConnector(Matchers.anyString())).thenReturn(connector);

		TaskReviewsMappingsStore taskReviewsMappingStore = new TaskReviewsMappingsStore(taskDataManager,
				repositoryManager);
		return taskReviewsMappingStore;
	}

	public void addReviewData(ITask review, String description) throws CoreException {
		TaskData reviewData = new TaskData(new TaskAttributeMapper(new TaskRepository("", "")), "", review.getUrl(),
				review.getTaskId());

		reviewData.getRoot().createMappedAttribute(TaskAttribute.DESCRIPTION).setValue(description);

		when(taskDataManager.getTaskData(review)).thenReturn(reviewData);
	}

	@Test
	public void testAdd() throws CoreException {
		TaskReviewsMappingsStore taskReviewsMappingStore = getEmptyTaskReviewStore();
		addReviewData(review1, descriptionWithTaskUrl1);

		TaskContainerDelta delta = new TaskContainerDelta(review1, Kind.ADDED);
		taskReviewsMappingStore.containersChanged(ImmutableSet.of(delta));

		assertTrue(taskReviewsMappingStore.getReviewUrls(taskUrl1).contains(reviewUrl1));
		assertTrue(taskReviewsMappingStore.getTaskUrl(reviewUrl1).equals(taskUrl1));
	}

	@Test
	public void testAddTwice() throws CoreException {
		TaskReviewsMappingsStore taskReviewsMappingStore = getEmptyTaskReviewStore();
		addReviewData(review1, descriptionWithTaskUrl1);

		TaskContainerDelta delta = new TaskContainerDelta(review1, Kind.ADDED);
		taskReviewsMappingStore.containersChanged(ImmutableSet.of(delta));
		taskReviewsMappingStore.containersChanged(ImmutableSet.of(delta));

		Collection<String> reviewUrls = taskReviewsMappingStore.getReviewUrls(taskUrl1);

		assertTrue(reviewUrls.contains(reviewUrl1));
		assertTrue(reviewUrls.size() == 1);
		assertTrue(taskReviewsMappingStore.getTaskUrl(reviewUrl1).equals(taskUrl1));
	}

	@Test
	public void testAddMultiple() throws CoreException {
		TaskReviewsMappingsStore taskReviewsMappingStore = getEmptyTaskReviewStore();
		addReviewData(review1, descriptionWithTaskUrl1);
		addReviewData(review2, descriptionWithTaskUrl2);

		TaskContainerDelta delta1 = new TaskContainerDelta(review1, Kind.ADDED);
		TaskContainerDelta delta2 = new TaskContainerDelta(review2, Kind.ADDED);

		taskReviewsMappingStore.containersChanged(ImmutableSet.of(delta1, delta2));

		assertTrue(taskReviewsMappingStore.getReviewUrls(taskUrl1).contains(reviewUrl1));
		assertTrue(taskReviewsMappingStore.getTaskUrl(reviewUrl1).equals(taskUrl1));

		assertTrue(taskReviewsMappingStore.getReviewUrls(taskUrl2).contains(reviewUrl2));
		assertTrue(taskReviewsMappingStore.getTaskUrl(reviewUrl2).equals(taskUrl2));
	}

	@Test
	public void testRemove() throws CoreException {

		TaskReviewsMappingsStore taskReviewsMappingStore = getEmptyTaskReviewStore();
		addReviewData(review1, descriptionWithTaskUrl1);

		TaskContainerDelta delta = new TaskContainerDelta(review1, Kind.ADDED);
		taskReviewsMappingStore.containersChanged(ImmutableSet.of(delta));
		assertTrue(taskReviewsMappingStore.getReviewUrls(taskUrl1).contains(reviewUrl1));

		delta = new TaskContainerDelta(review1, Kind.DELETED);

		taskReviewsMappingStore.containersChanged(ImmutableSet.of(delta));
		assertFalse(taskReviewsMappingStore.getReviewUrls(taskUrl1).contains(reviewUrl1));
		assertTrue(taskReviewsMappingStore.getTaskUrl(reviewUrl1) == null);
	}

	@Test
	public void testUpdate() throws CoreException {
		TaskReviewsMappingsStore taskReviewsMappingStore = getEmptyTaskReviewStore();
		addReviewData(review1, descriptionWithTaskUrl1);

		TaskContainerDelta delta = new TaskContainerDelta(review1, Kind.ADDED);
		taskReviewsMappingStore.containersChanged(ImmutableSet.of(delta));
		assertTrue(taskReviewsMappingStore.getReviewUrls(taskUrl1).contains(reviewUrl1));

		addReviewData(review1, descriptionWithTaskUrl2);

		delta = new TaskContainerDelta(review1, Kind.CONTENT);
		taskReviewsMappingStore.containersChanged(ImmutableSet.of(delta));
		assertFalse(taskReviewsMappingStore.getReviewUrls(taskUrl1).contains(reviewUrl1));
		assertTrue(taskReviewsMappingStore.getReviewUrls(taskUrl2).contains(reviewUrl1));

	}

	@Test
	public void testUpdateNoTask() throws CoreException {
		TaskReviewsMappingsStore taskReviewsMappingStore = getEmptyTaskReviewStore();
		addReviewData(review1, descriptionWithTaskUrl1);

		TaskContainerDelta delta = new TaskContainerDelta(review1, Kind.ADDED);
		taskReviewsMappingStore.containersChanged(ImmutableSet.of(delta));
		assertTrue(taskReviewsMappingStore.getReviewUrls(taskUrl1).contains(reviewUrl1));

		addReviewData(review1, descriptionWithNoTaskUrl);

		delta = new TaskContainerDelta(review1, Kind.CONTENT);
		taskReviewsMappingStore.containersChanged(ImmutableSet.of(delta));
		assertFalse(taskReviewsMappingStore.getReviewUrls(taskUrl1).contains(reviewUrl1));
	}

	@Test
	public void testAddNoTask() throws CoreException {
		TaskReviewsMappingsStore taskReviewsMappingStore = getEmptyTaskReviewStore();
		addReviewData(review1, descriptionWithNoTaskUrl);

		TaskContainerDelta delta = new TaskContainerDelta(review1, Kind.ADDED);
		taskReviewsMappingStore.containersChanged(ImmutableSet.of(delta));
		assertTrue(taskReviewsMappingStore.getTaskUrl(reviewUrl1) == null);
	}

	@Test
	public void testAddANonTaskUrl() throws CoreException {
		TaskReviewsMappingsStore taskReviewsMappingStore = getEmptyTaskReviewStore();
		addReviewData(review1, descriptionWithNotATaskUrl);

		TaskContainerDelta delta = new TaskContainerDelta(review1, Kind.ADDED);
		taskReviewsMappingStore.containersChanged(ImmutableSet.of(delta));
		assertTrue(taskReviewsMappingStore.getTaskUrl(reviewUrl1) == null);
	}

}
