/*******************************************************************************
 * Copyright (c) 2012, 2022 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     ArSysOp - adapt to SimRel 2022-12
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

import org.eclipse.mylyn.internal.gerrit.core.client.GerritChange;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritConfiguration;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.GerritConfigX;
import org.eclipse.mylyn.internal.gerrit.core.client.data.GerritPerson;
import org.eclipse.mylyn.internal.gerrit.core.client.data.GerritQueryResult;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.internal.core.ReviewFileCommentsMapper;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.gerrit.common.data.AccountInfo;
import com.google.gerrit.reviewdb.Account;
import com.google.gerrit.reviewdb.Account.Id;
import com.google.gerrit.reviewdb.Branch.NameKey;
import com.google.gerrit.reviewdb.Change;
import com.google.gerrit.reviewdb.Change.Key;
import com.google.gerrit.reviewdb.Project;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("restriction")
public class GerritTaskDataHandlerTest {

	@Test
	public void testCreatePartialTaskData() {
		GerritTaskDataHandler handler = new GerritTaskDataHandler(new GerritConnector());
		TaskRepository repository = new TaskRepository(GerritConnector.CONNECTOR_KIND, "http://repository"); //$NON-NLS-1$
		TaskData data = handler.createPartialTaskData(repository, "1", null); //$NON-NLS-1$
		assertNull(data.getRoot().getAttribute(GerritTaskSchema.getDefault().UPLOADED.getKey()));
		assertTrue(data.isPartial());
	}

	@Test
	public void testCreateTaskData() {
		GerritTaskDataHandler handler = new GerritTaskDataHandler(new GerritConnector());
		TaskRepository repository = new TaskRepository(GerritConnector.CONNECTOR_KIND, "http://repository"); //$NON-NLS-1$
		TaskData data = handler.createTaskData(repository, "1", null); //$NON-NLS-1$
		TaskData data2 = handler.createTaskData(repository, "2", null); //$NON-NLS-1$
		assertEquals(GerritTaskSchema.getDefault().UPLOADED.createAttribute(data2.getRoot()),
				data.getRoot().getAttribute(GerritTaskSchema.getDefault().UPLOADED.getKey()));
	}

	@Test
	public void testUpdatePartialTaskData() {
		GerritConnector connector = mock(GerritConnector.class, Mockito.RETURNS_DEEP_STUBS);
		GerritConfiguration config = createMockConfig();
		when(connector.getConfiguration(any(TaskRepository.class))).thenReturn(config);
		GerritTaskDataHandler handler = new GerritTaskDataHandler(connector);
		TaskRepository repository = new TaskRepository(GerritConnector.CONNECTOR_KIND, "http://repository"); //$NON-NLS-1$
		TaskData data = handler.createTaskData(repository, "1", null); //$NON-NLS-1$
		handler.updatePartialTaskData(repository, data, createMockQueryResult("Joel K. User"));
		assertAssignee(data, "joel.user@mylyn.org", "Joel K. User");

		handler.updatePartialTaskData(repository, data, createMockQueryResult("Jacob F. User"));
		assertAssignee(data, "Jacob F. User", "Jacob F. User");

		handler.updatePartialTaskData(repository, data, createMockQueryResult(null));
		assertAssignee(data, "Anonymous", "Anonymous");
	}

	@Test
	public void testUpdateTaskData() {
		GerritTaskDataHandler handler = new GerritTaskDataHandler(new GerritConnector());
		TaskRepository repository = new TaskRepository(GerritConnector.CONNECTOR_KIND, "http://repository"); //$NON-NLS-1$
		TaskData data = handler.createTaskData(repository, "1", null); //$NON-NLS-1$
		handler.updateTaskData(repository, data, createMockGerritChange(), createMockReview(), false, null);
		assertAssignee(data, "joel.user@mylyn.org", "Joel K. User");
		assertNotNull(data.getRoot().getAttribute(ReviewFileCommentsMapper.FILE_ITEM_COMMENTS));
	}

	private void assertAssignee(TaskData data, String id, String name) {
		TaskAttribute assigneeAttribute = data.getRoot().getAttribute(TaskAttribute.USER_ASSIGNED);
		assertEquals(id, assigneeAttribute.getValue());
		assertEquals(name, data.getAttributeMapper().getValueLabel(assigneeAttribute));
	}

	private GerritConfiguration createMockConfig() {
		Account account = new Account(new Id(1));
		account.setFullName("Joel K. User");
		account.setPreferredEmail("joel.user@mylyn.org");
		GerritConfiguration value = new GerritConfiguration(new GerritConfigX(), Collections.<Project> emptyList(),
				account);
		return value;
	}

	private GerritQueryResult createMockQueryResult(String userName) {
		GerritQueryResult queryResult = mock(GerritQueryResult.class, Mockito.RETURNS_SMART_NULLS);
		when(queryResult.getReviewLabel()).thenReturn(null);
		when(queryResult.getUpdated()).thenReturn(new Timestamp(1));
		if (userName == null) {
			when(queryResult.getOwner()).thenReturn(null);
		} else {
			GerritPerson person = mock(GerritPerson.class);
			when(person.getName()).thenReturn(userName);
			when(queryResult.getOwner()).thenReturn(person);
		}
		return queryResult;
	}

	private GerritChange createMockGerritChange() {
		AccountInfo accountInfo = mock(AccountInfo.class);
		when(accountInfo.getFullName()).thenReturn("Joel K. User");
		when(accountInfo.getPreferredEmail()).thenReturn("joel.user@mylyn.org");
		GerritChange change = mock(GerritChange.class, Mockito.RETURNS_DEEP_STUBS);
		when(change.getChangeDetail().getAccounts().get(any(Id.class))).thenReturn(accountInfo);
		when(change.getChangeDetail().getChange()).thenReturn(mockChange());
		return change;
	}

	private IReview createMockReview() {
		IReview review = mock(IReview.class);
		doReturn(List.of()).when(review).getSets();
		return review;
	}

	private Change mockChange() {
		return new Change(new Key("1"), new Change.Id(1), new Id(1),
				new NameKey(new Project.NameKey("parent"), "branch"));
	}

}
