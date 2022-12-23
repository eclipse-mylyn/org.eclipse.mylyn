package org.eclipse.mylyn.bugzilla.rest.core.tests;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestConnector;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestTaskSchema;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.junit.Before;
import org.junit.Test;

public class BugzillaRestConnectorNoFixtureTest {

	private BugzillaRestConnector connector;

	private TaskRepository repository;

	@Before
	public void setUp() {
		connector = new BugzillaRestConnector();
		repository = new TaskRepository(connector.getConnectorKind(), "http://test.repository.url");
	}

	@Test
	public void testGetRepositoryUrlFromTaskUrl() throws Exception {
		assertNull(connector.getRepositoryUrlFromTaskUrl(repository.getRepositoryUrl() + "/rest/bug/1"));
		assertThat(connector.getRepositoryUrlFromTaskUrl(repository.getRepositoryUrl() + "/rest.cgi/bug/1"),
				equalTo(repository.getRepositoryUrl()));
	}

	@Test
	public void testGetTaskUrl() throws Exception {
		assertThat(connector.getTaskUrl(repository.getRepositoryUrl(), "123"),
				equalTo(repository.getRepositoryUrl() + "/rest.cgi/bug/123"));
		assertThat(connector.getTaskUrl(repository.getRepositoryUrl(), "Test"),
				equalTo(repository.getRepositoryUrl() + "/rest.cgi/bug/Test"));
	}

	@Test
	public void testHasTaskChanged() {
		TaskData taskData = new TaskData(new TaskAttributeMapper(repository), connector.getConnectorKind(),
				repository.getRepositoryUrl(), "123");
		TaskTask task = new TaskTask(repository.getConnectorKind(), repository.getRepositoryUrl(), "123");
		Date now = new Date();
		task.setAttribute(BugzillaRestTaskSchema.getDefault().DATE_MODIFICATION.getKey(), "" + now.getTime());
		taskData.getRoot().createAttribute(BugzillaRestTaskSchema.getDefault().DATE_MODIFICATION.getKey()).setValue(
				"" + (now.getTime() + 1));
		assertTrue(connector.hasTaskChanged(repository, task, taskData));
	}

	@Test
	public void testHasTaskChangedEmptyModificationDate() {
		TaskData taskData = new TaskData(new TaskAttributeMapper(repository), connector.getConnectorKind(),
				repository.getRepositoryUrl(), "123");
		TaskTask task = new TaskTask(repository.getConnectorKind(), repository.getRepositoryUrl(), "123");
		Date now = new Date();
		taskData.getRoot().createAttribute(BugzillaRestTaskSchema.getDefault().DATE_MODIFICATION.getKey()).setValue(
				"" + (now.getTime()));
		assertTrue(connector.hasTaskChanged(repository, task, taskData));
	}

	@Test
	public void testHasNotTaskChanged() {
		TaskData taskData = new TaskData(new TaskAttributeMapper(repository), connector.getConnectorKind(),
				repository.getRepositoryUrl(), "123");
		TaskTask task = new TaskTask(repository.getConnectorKind(), repository.getRepositoryUrl(), "123");
		Date now = new Date();
		task.setAttribute(BugzillaRestTaskSchema.getDefault().DATE_MODIFICATION.getKey(), "" + now.getTime());
		taskData.getRoot().createAttribute(BugzillaRestTaskSchema.getDefault().DATE_MODIFICATION.getKey()).setValue(
				"" + (now.getTime()));
		assertTrue(!connector.hasTaskChanged(repository, task, taskData));
	}

	@Test
	public void testHasNotTaskChangedEmptyModificationDate() {
		TaskData taskData = new TaskData(new TaskAttributeMapper(repository), connector.getConnectorKind(),
				repository.getRepositoryUrl(), "123");
		TaskTask task = new TaskTask(repository.getConnectorKind(), repository.getRepositoryUrl(), "123");
		assertTrue(!connector.hasTaskChanged(repository, task, taskData));
	}

	@Test
	public void testTaskDataAndTaskWithodModdate() {
		TaskData taskData = new TaskData(new TaskAttributeMapper(repository), connector.getConnectorKind(),
				repository.getRepositoryUrl(), "123");
		TaskTask task = new TaskTask(repository.getConnectorKind(), repository.getRepositoryUrl(), "123");
		Date now = new Date();
		task.setAttribute(BugzillaRestTaskSchema.getDefault().DATE_MODIFICATION.getKey(), "" + now.getTime());
		assertTrue(connector.hasTaskChanged(repository, task, taskData));
	}

}
