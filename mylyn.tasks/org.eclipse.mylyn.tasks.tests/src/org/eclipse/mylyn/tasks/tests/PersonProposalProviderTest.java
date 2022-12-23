/*******************************************************************************
 * Copyright (c) 2004, 2015 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.internal.tasks.ui.PersonContentProposal;
import org.eclipse.mylyn.internal.tasks.ui.PersonProposalProvider;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryQuery;
import org.eclipse.mylyn.tasks.tests.connector.MockTask;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * @author Frank Becker
 * @author Steffen Pingel
 */
public class PersonProposalProviderTest extends TestCase {

	final private static Comparator<IContentProposal> CONTENT_COMPARATOR = new Comparator<IContentProposal>() {
		public int compare(IContentProposal o1, IContentProposal o2) {
			return o1.getContent().compareTo(o2.getContent());
		}
	};

	@Override
	protected void setUp() throws Exception {
		TaskTestUtil.resetTaskListAndRepositories();
	}

	@Override
	protected void tearDown() throws Exception {
		TaskTestUtil.resetTaskListAndRepositories();
	}

	public void testGetProposalsNullParameters() {
		PersonProposalProvider provider = new PersonProposalProvider((AbstractTask) null, (TaskData) null);
		IContentProposal[] result = provider.getProposals("", 0);
		assertNotNull(result);
		assertEquals(0, result.length);

		result = provider.getProposals(" ", 1);
		assertNotNull(result);
		assertEquals(0, result.length);
	}

	public void testGetProposalsNullContents() throws Exception {
		PersonProposalProvider provider = new PersonProposalProvider(MockRepositoryConnector.REPOSITORY_URL,
				MockRepositoryConnector.CONNECTOR_KIND);

		try {
			provider.getProposals(null, 0);
			fail();
		} catch (IllegalArgumentException e) {
			// expected
		}
	}

	public void testInvalidPosition() throws Exception {
		PersonProposalProvider provider = new PersonProposalProvider(MockRepositoryConnector.REPOSITORY_URL,
				MockRepositoryConnector.CONNECTOR_KIND);

		try {
			provider.getProposals("", -1);
			fail();
		} catch (IllegalArgumentException e) {
			// expected
		}
	}

	public void testGetProposalsTask() {
		MockTask task = new MockTask(null, "1", null);
		task.setOwner("foo");
		PersonProposalProvider provider = new PersonProposalProvider(task, (TaskData) null);

		assertProposalsForFoo(provider);
	}

	public void testGetProposalsTaskDataWithReporter() {
		MockTask task = new MockTask(null, "1", null);
		TaskData taskData = createMockTaskData();
		taskData.getRoot().createMappedAttribute(TaskAttribute.USER_REPORTER).setValue("foo");
		taskData.getRoot().getMappedAttribute(TaskAttribute.USER_REPORTER).getMetaData().setReadOnly(true);
		PersonProposalProvider provider = new PersonProposalProvider(task, taskData);

		assertProposalsForFoo(provider);
	}

	public void testGetProposalsTaskDataWithReporterPerson() {
		MockTask task = new MockTask(null, "1", null);
		TaskData taskData = createMockTaskData();
		taskData.getRoot().createMappedAttribute(TaskAttribute.USER_REPORTER).setValue("foo");
		taskData.getRoot().getMappedAttribute(TaskAttribute.USER_REPORTER).getMetaData().setReadOnly(true);
		taskData.getRoot()
				.getMappedAttribute(TaskAttribute.USER_REPORTER)
				.getMetaData()
				.setType(TaskAttribute.TYPE_PERSON);
		PersonProposalProvider provider = new PersonProposalProvider(task, taskData);

		assertProposalsForFoo(provider);
	}

	private TaskData createMockTaskData() {
		return new TaskData(new TaskAttributeMapper(TaskTestUtil.createMockRepository()),
				MockRepositoryConnector.CONNECTOR_KIND, MockRepositoryConnector.REPOSITORY_URL, "1");
	}

	static private void assertProposalsForFoo(PersonProposalProvider provider) {
		IContentProposal[] result = provider.getProposals("", 0);
		assertNotNull(result);
		assertEquals(1, result.length);
		assertEquals("foo", result[0].getContent());

		result = provider.getProposals("a", 1);
		assertNotNull(result);
		assertEquals(0, result.length);

		result = provider.getProposals("fo", 2);
		assertNotNull(result);
		assertEquals(1, result.length);
		assertEquals("foo", result[0].getContent());

		result = provider.getProposals("", 0);
		assertNotNull(result);
		assertEquals(1, result.length);
		assertEquals("foo", result[0].getContent());
	}

	public void testGetProposalNoOwnerAndNoPerson() throws Exception {
		PersonProposalProvider provider = new PersonProposalProvider(MockRepositoryConnector.REPOSITORY_URL,
				MockRepositoryConnector.CONNECTOR_KIND);

		IContentProposal[] result = provider.getProposals("", 0);

		assertNotNull(result);
		assertEquals(0, result.length);
	}

	public void testGetProposalsMultipleAddresses() {
		IContentProposal[] result;

		MockTask task = new MockTask(null, "1", null);
		task.setOwner("foo");
		PersonProposalProvider provider = new PersonProposalProvider(task, (TaskData) null);

		result = provider.getProposals("f,xx", 1);
		assertNotNull(result);
		assertEquals(1, result.length);
		assertEquals("foo,xx", result[0].getContent());
		assertEquals("foo", result[0].getLabel());
		assertEquals(3, result[0].getCursorPosition());

		result = provider.getProposals("f xx", 1);
		assertNotNull(result);
		assertEquals(1, result.length);
		assertEquals("foo xx", result[0].getContent());
		assertEquals("foo", result[0].getLabel());
		assertEquals(3, result[0].getCursorPosition());

		result = provider.getProposals("a,xx", 1);
		assertNotNull(result);
		assertEquals(0, result.length);

		result = provider.getProposals("xx,f", 4);
		assertNotNull(result);
		assertEquals(1, result.length);
		assertEquals("xx,foo", result[0].getContent());
		assertEquals("foo", result[0].getLabel());
		assertEquals(6, result[0].getCursorPosition());

		result = provider.getProposals("xx f", 4);
		assertNotNull(result);
		assertEquals(1, result.length);
		assertEquals("xx foo", result[0].getContent());
		assertEquals("foo", result[0].getLabel());
		assertEquals(6, result[0].getCursorPosition());

		result = provider.getProposals("xx,a", 4);
		assertNotNull(result);
		assertEquals(0, result.length);

		result = provider.getProposals("xyz,f,yy", 4);
		assertNotNull(result);
		assertEquals(1, result.length);
		assertEquals("xyz,foo,yy", result[0].getContent());
		assertEquals("foo", result[0].getLabel());
		assertEquals(7, result[0].getCursorPosition());

		result = provider.getProposals("xx f yy", 4);
		assertNotNull(result);
		assertEquals(1, result.length);
		assertEquals("xx foo yy", result[0].getContent());
		assertEquals("foo", result[0].getLabel());
		assertEquals(6, result[0].getCursorPosition());

		result = provider.getProposals("xx,a,yy", 4);
		assertNotNull(result);
		assertEquals(0, result.length);

		result = provider.getProposals("xx,,yy", 3);
		assertNotNull(result);
		assertEquals(1, result.length);
		assertEquals("xx,foo,yy", result[0].getContent());
		assertEquals("foo", result[0].getLabel());
		assertEquals(6, result[0].getCursorPosition());

		result = provider.getProposals("x yy", 2);
		assertNotNull(result);
		assertEquals(1, result.length);
		assertEquals("x foo", result[0].getContent());
		assertEquals("foo", result[0].getLabel());
		assertEquals(5, result[0].getCursorPosition());

		result = provider.getProposals(", ", 1);
		assertNotNull(result);
		assertEquals(1, result.length);
		assertEquals(",foo ", result[0].getContent());
		assertEquals("foo", result[0].getLabel());
		assertEquals(4, result[0].getCursorPosition());

		result = provider.getProposals(", ", 0);
		assertNotNull(result);
		assertEquals(1, result.length);
		assertEquals("foo, ", result[0].getContent());
		assertEquals("foo", result[0].getLabel());
		assertEquals(3, result[0].getCursorPosition());
	}

	public void testConstructorRepositoryUrlKind() throws Exception {
		MockTask task1 = new MockTask(MockRepositoryConnector.REPOSITORY_URL, "1");
		task1.setOwner("foo");
		PersonProposalProvider provider = new PersonProposalProvider(MockRepositoryConnector.REPOSITORY_URL,
				MockRepositoryConnector.CONNECTOR_KIND);
		MockRepositoryQuery query = new MockRepositoryQuery("summary");
		TasksUiPlugin.getTaskList().addQuery(query);
		TasksUiPlugin.getTaskList().addTask(task1, query);

		IContentProposal[] result = provider.getProposals("f,xx", 1);
		assertNotNull(result);
		assertEquals(1, result.length);
		assertEquals("foo,xx", result[0].getContent());
		assertEquals("foo", result[0].getLabel());
		assertEquals(3, result[0].getCursorPosition());
	}

	public void testCurrentUser() throws Exception {
		TaskTask task = TaskTestUtil.createMockTask("1");
		task.setOwner("user");
		TasksUiPlugin.getTaskList().addTask(task);
		TaskRepository repository = TaskTestUtil.createMockRepository();
		repository.setCredentials(AuthenticationType.REPOSITORY, new AuthenticationCredentials("user", ""), false);
		TasksUi.getRepositoryManager().addRepository(repository);

		PersonProposalProvider provider = new PersonProposalProvider(MockRepositoryConnector.REPOSITORY_URL,
				MockRepositoryConnector.CONNECTOR_KIND);
		IContentProposal[] result = provider.getProposals("user", 1);
		assertNotNull(result);
		assertEquals(1, result.length);
		assertTrue(((PersonContentProposal) result[0]).isCurrentUser());
	}

	public void testGetProposalByPrettyName() throws Exception {
		Map<String, String> users = new HashMap<String, String>();
		users.put("11", "foo");
		users.put("22", "bar");
		users.put("33", "far");
		users.put("21", "boo");
		MockTask task1 = new MockTask(null, "1", null);
		task1.setOwner("11");

		PersonProposalProvider provider = new PersonProposalProvider(task1, (TaskData) null, users);
		IContentProposal[] result = provider.getProposals("", 0);
		Arrays.sort(result, CONTENT_COMPARATOR);
		assertNotNull(result);
		assertEquals(4, result.length);
		assertEquals("11", result[0].getContent());
		assertEquals("21", result[1].getContent());
		assertEquals("22", result[2].getContent());
		assertEquals("33", result[3].getContent());

		result = provider.getProposals("f", 1);
		Arrays.sort(result, CONTENT_COMPARATOR);
		assertNotNull(result);
		assertEquals(2, result.length);
		assertEquals("11", result[0].getContent());
		assertEquals("33", result[1].getContent());

		result = provider.getProposals("b", 1);
		Arrays.sort(result, CONTENT_COMPARATOR);
		assertNotNull(result);
		assertEquals(2, result.length);
		assertEquals("21", result[0].getContent());
		assertEquals("22", result[1].getContent());

		result = provider.getProposals("1", 1);
		Arrays.sort(result, CONTENT_COMPARATOR);
		assertNotNull(result);
		assertEquals(1, result.length);
		assertEquals("11", result[0].getContent());

		result = provider.getProposals("3", 1);
		Arrays.sort(result, CONTENT_COMPARATOR);
		assertNotNull(result);
		assertEquals(1, result.length);
		assertEquals("33", result[0].getContent());

		result = provider.getProposals("2", 1);
		Arrays.sort(result, CONTENT_COMPARATOR);
		assertNotNull(result);
		assertEquals(2, result.length);
		assertEquals("21", result[0].getContent());
		assertEquals("22", result[1].getContent());
	}

	public void testGetProposalByOwnerId() throws Exception {
		TaskTask task = TaskTestUtil.createMockTask("1");
		task.setOwner("Joel User");
		TasksUiPlugin.getTaskList().addTask(task);
		TaskRepository repository = TaskTestUtil.createMockRepository();
		TasksUi.getRepositoryManager().addRepository(repository);

		PersonProposalProvider provider = new PersonProposalProvider(MockRepositoryConnector.REPOSITORY_URL,
				MockRepositoryConnector.CONNECTOR_KIND);
		IContentProposal[] result = provider.getProposals("joel", 1);
		assertEquals(1, result.length);
		assertEquals("Joel User", result[0].getLabel());
		assertEquals("Joel User", result[0].getContent());

		task.setOwnerId("joel.user@mylyn.org");
		provider = new PersonProposalProvider(MockRepositoryConnector.REPOSITORY_URL,
				MockRepositoryConnector.CONNECTOR_KIND);
		result = provider.getProposals("joel", 1);
		assertEquals(1, result.length);
		assertEquals("Joel User <joel.user@mylyn.org>", result[0].getLabel());
		assertEquals("joel.user@mylyn.org", result[0].getContent());

		task.setOwnerId("");
		task.setOwner("");
		provider = new PersonProposalProvider(MockRepositoryConnector.REPOSITORY_URL,
				MockRepositoryConnector.CONNECTOR_KIND);
		result = provider.getProposals("joel", 1);
		assertEquals(0, result.length);
	}
}
