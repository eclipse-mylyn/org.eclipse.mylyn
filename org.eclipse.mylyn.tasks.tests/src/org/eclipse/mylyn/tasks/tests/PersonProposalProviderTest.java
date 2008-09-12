/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import junit.framework.TestCase;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData;
import org.eclipse.mylyn.internal.tasks.ui.PersonProposalProvider;
import org.eclipse.mylyn.internal.tasks.ui.TaskListManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryQuery;
import org.eclipse.mylyn.tasks.tests.connector.MockTask;

/**
 * @author Frank Becker
 * @author Steffen Pingel
 */
public class PersonProposalProviderTest extends TestCase {

	private TaskListManager manager;

	@Override
	protected void setUp() throws Exception {
		manager = TasksUiPlugin.getTaskListManager();
		manager.resetTaskList();
	}

	@Override
	protected void tearDown() throws Exception {
		manager.resetTaskList();
		TasksUiPlugin.getRepositoryManager().clearRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());
	}

	public void testGetProposalsNullParameters() {
		PersonProposalProvider provider = new PersonProposalProvider((AbstractTask) null, (RepositoryTaskData) null);
		IContentProposal[] result = provider.getProposals("", 0);
		assertNotNull(result);
		assertEquals(0, result.length);

		result = provider.getProposals(" ", 1);
		assertNotNull(result);
		assertEquals(0, result.length);
	}

	public void testGetProposalsCurrentTask() {
		MockTask task = new MockTask(null, "1", null);
		task.setOwner("foo");
		PersonProposalProvider provider = new PersonProposalProvider(task, (RepositoryTaskData) null);
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

	public void testGetProposalsMultipleAddresses() {
		IContentProposal[] result;

		MockTask task = new MockTask(null, "1", null);
		task.setOwner("foo");
		PersonProposalProvider provider = new PersonProposalProvider(task, (RepositoryTaskData) null);

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
		IContentProposal[] result;

		MockTask task1 = new MockTask(MockRepositoryConnector.REPOSITORY_URL, "1");
		task1.setOwner("foo");
		PersonProposalProvider provider = new PersonProposalProvider(MockRepositoryConnector.REPOSITORY_URL,
				MockRepositoryConnector.REPOSITORY_KIND);
		MockRepositoryQuery query = new MockRepositoryQuery("summary");
		manager.getTaskList().addQuery(query);
		manager.getTaskList().addTask(task1, query);

		result = provider.getProposals("f,xx", 1);
		assertNotNull(result);
		assertEquals(1, result.length);
		assertEquals("foo,xx", result[0].getContent());
		assertEquals("foo", result[0].getLabel());
		assertEquals(3, result[0].getCursorPosition());
	}

}
