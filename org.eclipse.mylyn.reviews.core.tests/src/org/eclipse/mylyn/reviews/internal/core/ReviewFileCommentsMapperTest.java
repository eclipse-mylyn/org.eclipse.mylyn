/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.internal.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class ReviewFileCommentsMapperTest {

	private TaskData taskData;

	private IReview review;

	private ReviewFileCommentsMapper mapper;

	@Before
	public void setup() {
		TaskRepository repository = new TaskRepository("kind", "url");
		TaskAttributeMapper attributeMapper = new TaskAttributeMapper(repository);
		taskData = new TaskData(attributeMapper, "kind", "url", "id");
		review = mock(IReview.class);
		mapper = new ReviewFileCommentsMapper(review);
	}

	@Test
	public void emptyReview() {
		doReturn(ImmutableList.of()).when(review).getSets();
		mapper.applyTo(taskData);
		TaskAttribute comments = taskData.getRoot().getAttribute(ReviewFileCommentsMapper.FILE_ITEM_COMMENTS);
		assertNotNull(comments);
		assertEquals(ImmutableMap.of(), comments.getAttributes());
	}

	@Test
	public void reviewWithoutComments() {
		IReviewItemSet set = mock(IReviewItemSet.class);
		IFileItem file = mock(IFileItem.class);
		doReturn(ImmutableList.of(set)).when(review).getSets();
		doReturn(ImmutableList.of(file)).when(set).getItems();
		doReturn(ImmutableList.of()).when(file).getAllComments();
		mapper.applyTo(taskData);
		TaskAttribute comments = taskData.getRoot().getAttribute(ReviewFileCommentsMapper.FILE_ITEM_COMMENTS);
		assertNotNull(comments);
		assertEquals(ImmutableMap.of(), comments.getAttributes());
	}

	@Test
	public void reviewWithComments() {
		IReviewItemSet set1 = mock(IReviewItemSet.class);
		IReviewItemSet set2 = mock(IReviewItemSet.class);
		IFileItem file1 = mock(IFileItem.class);
		IFileItem file2 = mock(IFileItem.class);
		IFileItem file3 = mock(IFileItem.class);
		IComment comment1 = mock(IComment.class);
		IComment comment2 = mock(IComment.class);
		IComment comment3 = mock(IComment.class);
		doReturn(ImmutableList.of(set1, set2)).when(review).getSets();
		doReturn(ImmutableList.of(file1)).when(set1).getItems();
		doReturn(ImmutableList.of(file2, file3)).when(set2).getItems();
		doReturn(ImmutableList.of(comment1)).when(file1).getAllComments();
		doReturn(ImmutableList.of()).when(file2).getAllComments();
		doReturn(ImmutableList.of(comment2, comment3)).when(file2).getAllComments();
		doReturn("id1").when(comment1).getId();
		doReturn("id2").when(comment2).getId();
		doReturn("id3").when(comment3).getId();
		doReturn("comment 1").when(comment1).getDescription();
		doReturn("comment 2").when(comment2).getDescription();
		doReturn("comment 3").when(comment3).getDescription();
		mapper.applyTo(taskData);
		TaskAttribute comments = taskData.getRoot().getAttribute(ReviewFileCommentsMapper.FILE_ITEM_COMMENTS);
		assertNotNull(comments);
		assertComments(ImmutableMap.of("id1", "comment 1", "id2", "comment 2", "id3", "comment 3"), comments);
	}

	private void assertComments(ImmutableMap<String, String> children, TaskAttribute attribute) {
		for (String attributeId : children.keySet()) {
			assertNotNull(attribute.getAttribute(attributeId));
			assertEquals(children.get(attributeId), attribute.getAttribute(attributeId).getValue());
		}
	}

}
