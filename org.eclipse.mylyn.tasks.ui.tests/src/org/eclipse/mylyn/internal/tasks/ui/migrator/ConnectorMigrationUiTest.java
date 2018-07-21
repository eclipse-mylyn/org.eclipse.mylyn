/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.ui.migrator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.internal.commons.notifications.feed.ServiceMessage;
import org.eclipse.mylyn.internal.tasks.ui.TaskListBackupManager;
import org.eclipse.mylyn.internal.tasks.ui.migrator.ConnectorMigrationUi.CompleteMigrationJob;
import org.eclipse.mylyn.internal.tasks.ui.notifications.TaskListServiceMessageControl;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class ConnectorMigrationUiTest {

	private ConnectorMigrator migrator;

	private final TaskListView taskList = mock(TaskListView.class);

	private final TaskListBackupManager backupManager = mock(TaskListBackupManager.class);

	private final ConnectorMigrationUi migrationUi = spy(
			new ConnectorMigrationUi(taskList, backupManager, new DefaultTasksState()));

	private CompleteMigrationJob completeMigrationJob;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		doNothing().when(migrationUi).warnOfValidationFailure(any(List.class));
		doNothing().when(migrationUi).notifyMigrationComplete();
	}

	@After
	public void tearDown() {
		if (completeMigrationJob != null) {
			completeMigrationJob.dispose();
			// otherwise it will keep rescheduling itself and interfere with other tests
		}
	}

	@Test
	public void promptToMigrate() {
		ServiceMessage message = openMigrationPrompt(Window.OK);

		assertEquals("End of Connector Support", message.getTitle());
		assertTrue(message.getDescription().contains("<a href=\"migrate\">Click here</a>"));
		assertEquals(Dialog.DLG_IMG_MESSAGE_INFO, message.getImage());
		assertFalse(message.openLink("foo"));
		verify(migrationUi, never()).createMigrationWizard(any(ConnectorMigrator.class));
		verify(migrationUi, never()).createPromptToCompleteMigrationJob(any(ConnectorMigrator.class));

		message.openLink("migrate");
	}

	@Test
	public void finishMigrationWizard() throws Exception {
		ServiceMessage message = openMigrationPrompt(Window.OK);
		assertTrue(message.openLink("migrate"));
		verify(migrationUi).createPromptToCompleteMigrationJob(any(ConnectorMigrator.class));
	}

	@Test
	public void cancelMigrationWizard() throws Exception {
		ServiceMessage message = openMigrationPrompt(Window.CANCEL);
		assertFalse(message.openLink("migrate"));
		verify(migrationUi, never()).createPromptToCompleteMigrationJob(any(ConnectorMigrator.class));
	}

	@Test
	public void promptToMigrateOpensSecondMessage() {
		ServiceMessage message = openMigrationPrompt(Window.OK);
		Job job = mock(Job.class);
		doReturn(job).when(migrationUi).createPromptToCompleteMigrationJob(any(ConnectorMigrator.class));

		message.openLink("migrate");
		verify(migrationUi).createMigrationWizard(any(ConnectorMigrator.class));
		verify(migrationUi).createPromptToCompleteMigrationJob(any(ConnectorMigrator.class));
		verify(job).schedule();
	}

	@Test
	public void promptToCompleteMigration() throws InterruptedException {
		ServiceMessage message = openCompleteMigrationPrompt(Window.OK);

		assertEquals("Connector Migration", message.getTitle());
		assertTrue(message.getDescription().contains("<a href=\"complete-migration\">complete migration</a>"));
		assertEquals(Dialog.DLG_IMG_MESSAGE_WARNING, message.getImage());
		assertFalse(message.openLink("foo"));
		verify(migrationUi, never()).createCompleteMigrationWizard(any(ConnectorMigrator.class));

		message.openLink("complete-migration");
		verify(migrationUi).createCompleteMigrationWizard(any(ConnectorMigrator.class));
	}

	@Test
	public void finishCompleteMigrationWizard() throws Exception {
		ServiceMessage message = openCompleteMigrationPrompt(Window.OK);
		assertTrue(message.openLink("complete-migration"));
	}

	@Test
	public void cancelCompleteMigrationWizard() throws Exception {
		ServiceMessage message = openCompleteMigrationPrompt(Window.CANCEL);
		assertFalse(message.openLink("complete-migration"));
	}

	@Test
	public void secondMessageReopensAfterDelay() throws Exception {
		ServiceMessage message = openCompleteMigrationPrompt(Window.OK);
		TaskListServiceMessageControl messageControl = createMessageControl();

		Thread.sleep(2100);
		ServiceMessage message2 = captureMessage(messageControl);
		assertTrue(message2 != message);

		Thread.sleep(2100);
		ServiceMessage message3 = captureMessage(messageControl);
		assertTrue(message3 != message2);

		message3.openLink("complete-migration");
		Thread.sleep(2100);
		ServiceMessage message4 = captureMessage(messageControl);
		assertTrue(message4 == message3);
	}

	@Test
	public void backupTaskList() throws Exception {
		ImmutableMap<String, String> kinds = ImmutableMap.of("mock", "mock.new");
		TaskRepository repository = new TaskRepository("mock", "http://mock");
		ConnectorMigrator migrator = spy(createMigrator(true, true, kinds, ImmutableSet.of(repository)));
		IProgressMonitor monitor = mock(IProgressMonitor.class);
		migrationUi.backupTaskList(monitor);
		InOrder inOrder = inOrder(backupManager, migrationUi, migrator, monitor);
		inOrder.verify(monitor).subTask("Backing up task list");
		inOrder.verify(migrationUi).getBackupFileName(any(Date.class));
		inOrder.verify(backupManager).backupNow(true);
		inOrder.verify(monitor).worked(1);
	}

	@Test
	public void getBackupFileName() throws Exception {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.MILLISECOND, 0);
		date = cal.getTime();
		String fileName = migrationUi.getBackupFileName(date);
		Matcher m = Pattern.compile("connector-migration-(\\d{4}_\\d{2}_\\d{2}_\\d{6}).zip").matcher(fileName);
		Matcher dateFormatMatcher = Pattern.compile("\\d{4}-\\d{2}-\\d{2}-\\d{6}.zip").matcher(fileName);
		Matcher oldDateFormatMatcher = Pattern.compile("\\d{4}-\\d{2}-\\d{2}").matcher(fileName);
		assertTrue(m.matches());
		assertFalse(dateFormatMatcher.find());
		assertFalse(oldDateFormatMatcher.find());
		Date fileNameTime = new SimpleDateFormat("yyyy_MM_dd_HHmmss").parse(m.group(1));
		assertEquals(date, fileNameTime);
	}

	/**
	 * @param returnCode
	 *            the return code of the wizard that opens when the link in the prompt is clicked
	 */
	private ServiceMessage openMigrationPrompt(int returnCode) {
		TaskListServiceMessageControl messageControl = createMessageControl();
		migrator = spy(
				new ConnectorMigrator(ImmutableMap.of("mock", "mock.new"), "", new DefaultTasksState(), migrationUi));
		WizardDialog wizard = mock(WizardDialog.class);
		when(wizard.open()).thenReturn(returnCode);
		doReturn(wizard).when(migrationUi).createMigrationWizard(any(ConnectorMigrator.class));
		migrationUi.promptToMigrate(migrator);
		return captureMessage(messageControl);
	}

	/**
	 * @param returnCode
	 *            the return code of the wizard that opens when the link in the prompt is clicked
	 */
	private ServiceMessage openCompleteMigrationPrompt(int returnCode) throws InterruptedException {
		TaskListServiceMessageControl messageControl = createMessageControl();
		migrator = spy(
				new ConnectorMigrator(ImmutableMap.of("mock", "mock.new"), "", new DefaultTasksState(), migrationUi));
		when(migrationUi.getCompletionPromptFrequency()).thenReturn(2);
		WizardDialog wizard = mock(WizardDialog.class);
		when(wizard.open()).thenReturn(returnCode);
		doReturn(wizard).when(migrationUi).createCompleteMigrationWizard(any(ConnectorMigrator.class));
		completeMigrationJob = (CompleteMigrationJob) migrationUi.createPromptToCompleteMigrationJob(migrator);
		assertTrue(completeMigrationJob.isSystem());
		assertFalse(completeMigrationJob.isUser());
		completeMigrationJob.schedule();
		completeMigrationJob.join();
		return captureMessage(messageControl);
	}

	private TaskListServiceMessageControl createMessageControl() {
		TaskListServiceMessageControl messageControl = mock(TaskListServiceMessageControl.class);
		when(taskList.getServiceMessageControl()).thenReturn(messageControl);
		return messageControl;
	}

	private ServiceMessage captureMessage(TaskListServiceMessageControl messageControl) {
		spinEventLoop();
		ArgumentCaptor<ServiceMessage> messageCaptor = ArgumentCaptor.forClass(ServiceMessage.class);
		verify(messageControl, atLeastOnce()).setMessage(messageCaptor.capture());
		ServiceMessage message = messageCaptor.getValue();
		return message;
	}

	private void spinEventLoop() {
		while (Display.getCurrent().readAndDispatch()) {
		}
	}

	private ConnectorMigrator createMigrator(boolean hasOldConnector, boolean hasNewConnector,
			Map<String, String> kinds, Set<TaskRepository> repositories) {
		IRepositoryManager manager = mock(IRepositoryManager.class);
		AbstractRepositoryConnector connector = mock(AbstractRepositoryConnector.class);
		ConnectorMigrator migrator = new ConnectorMigrator(kinds, "", new DefaultTasksState(), migrationUi);
		when(manager.getRepositories("mock")).thenReturn(repositories);
		if (hasOldConnector) {
			when(manager.getRepositoryConnector("mock")).thenReturn(connector);
			when(manager.getRepositoryConnector("kind")).thenReturn(connector);
		}
		if (hasNewConnector) {
			when(manager.getRepositoryConnector("mock.new")).thenReturn(connector);
			when(manager.getRepositoryConnector("kind.new")).thenReturn(connector);
		}
		return migrator;
	}
}
