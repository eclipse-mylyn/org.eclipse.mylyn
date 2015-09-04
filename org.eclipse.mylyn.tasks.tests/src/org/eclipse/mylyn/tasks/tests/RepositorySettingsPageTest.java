/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositorySettingsPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import junit.framework.TestCase;

/**
 * @author Steffen Pingel
 */
public class RepositorySettingsPageTest extends TestCase {

	public void testNeedsEncoding() {
		TaskRepository repository = new TaskRepository(MockRepositoryConnector.CONNECTOR_KIND, "http://localhost/");
		repository.setCharacterEncoding("UTF-8");

		MockRepositorySettingsPage page = new MockRepositorySettingsPage(repository);
		page.setNeedsEncoding(true);

		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		page.createControl(shell);
		page.setVisible(true);

		TaskRepository repository2 = page.createTaskRepository();
		assertEquals("UTF-8", repository2.getCharacterEncoding());
	}

	public void testNeedsEncodingFalse() {
		MockRepositorySettingsPage page = new MockRepositorySettingsPage(null);
		page.setNeedsEncoding(false);

		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		page.createControl(shell);
		page.setVisible(true);

		page.createTaskRepository();
	}

	public void testNeedsAnonyoumousLoginFalse() {
		MockRepositorySettingsPage page = new MockRepositorySettingsPage(null);
		page.setNeedsAnonymousLogin(false);

		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		page.createControl(shell);
		page.setVisible(true);

		assertNull(page.getAnonymousButton());
	}

	public void testNeedsAnonyoumousLoginNoRepository() {
		MockRepositorySettingsPage page = new MockRepositorySettingsPage(null);
		page.setNeedsAnonymousLogin(true);

		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		page.createControl(shell);
		page.setVisible(true);

		assertNotNull(page.getAnonymousButton());

		assertTrue(page.getAnonymousButton().getSelection());
		assertFalse(page.getUserNameEditor().getTextControl(page.getParent()).isEnabled());
		assertFalse(page.getPasswordEditor().getTextControl(page.getParent()).isEnabled());
		assertEquals("", page.getUserName());
		assertEquals("", page.getPassword());
		page.getAnonymousButton().setSelection(false);
	}

	public void testNeedsAnonyoumousLogin() {
		TaskRepository repository = new TaskRepository(MockRepositoryConnector.CONNECTOR_KIND, "http://localhost/");

		TasksUiPlugin.getDefault().addRepositoryConnectorUi(new MockRepositoryConnectorUi());

		MockRepositorySettingsPage page = new MockRepositorySettingsPage(repository);
		page.setNeedsAnonymousLogin(true);

		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		page.createControl(shell);
		page.setVisible(true);

		assertNotNull(page.getAnonymousButton());

		assertTrue(page.getAnonymousButton().getSelection());
		assertFalse(page.getUserNameEditor().getTextControl(page.getParent()).isEnabled());
		assertFalse(page.getPasswordEditor().getTextControl(page.getParent()).isEnabled());
		assertEquals("", page.getUserName());
		assertEquals("", page.getPassword());

		page.getAnonymousButton().setSelection(false);
		page.getAnonymousButton().notifyListeners(SWT.Selection, new Event());
		assertTrue(page.getUserNameEditor().getTextControl(page.getParent()).isEnabled());
		assertTrue(page.getPasswordEditor().getTextControl(page.getParent()).isEnabled());
		page.getUserNameEditor().setStringValue("user");
		page.getPasswordEditor().setStringValue("password");
		assertEquals("user", page.getUserName());
		assertEquals("password", page.getPassword());

		page.getAnonymousButton().setSelection(true);
		page.getAnonymousButton().notifyListeners(SWT.Selection, new Event());
		assertFalse(page.getUserNameEditor().getTextControl(page.getParent()).isEnabled());
		assertFalse(page.getPasswordEditor().getTextControl(page.getParent()).isEnabled());
		assertEquals("", page.getUserName());
		assertEquals("", page.getPassword());

		page.getAnonymousButton().setSelection(false);
		page.getAnonymousButton().notifyListeners(SWT.Selection, new Event());
		assertTrue(page.getUserNameEditor().getTextControl(page.getParent()).isEnabled());
		assertTrue(page.getPasswordEditor().getTextControl(page.getParent()).isEnabled());
		assertEquals("user", page.getUserNameEditor().getStringValue());
		assertEquals("password", page.getPasswordEditor().getStringValue());
		assertEquals("user", page.getUserName());
		assertEquals("password", page.getPassword());
	}

	public void testSavePassword() {
		TaskRepository repository = new TaskRepository(MockRepositoryConnector.CONNECTOR_KIND, "http://localhost/");
		TasksUiPlugin.getDefault().addRepositoryConnectorUi(new MockRepositoryConnectorUi());

		assertTrue(repository.getSavePassword(AuthenticationType.REPOSITORY));

		MockRepositorySettingsPage page = new MockRepositorySettingsPage(repository);
		try {
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			page.createControl(shell);
			assertTrue(page.getSavePassword());
		} finally {
			page.dispose();
		}

		repository.setCredentials(AuthenticationType.REPOSITORY, null, false);
		page = new MockRepositorySettingsPage(repository);
		try {
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			page.createControl(shell);
			assertFalse(page.getSavePassword());
		} finally {
			page.dispose();
		}
	}

	public void testSaveHttpPassword() {
		TaskRepository repository = new TaskRepository(MockRepositoryConnector.CONNECTOR_KIND, "http://localhost/");
		TasksUiPlugin.getDefault().addRepositoryConnectorUi(new MockRepositoryConnectorUi());

		assertTrue(repository.getSavePassword(AuthenticationType.HTTP));

		MockRepositorySettingsPage page = new MockRepositorySettingsPage(repository);
		page.setNeedsHttpAuth(true);
		try {
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			page.createControl(shell);
			assertTrue(page.getSaveHttpPassword());
		} finally {
			page.dispose();
		}

		repository.setCredentials(AuthenticationType.HTTP, null, false);
//		page = new MockRepositorySettingsPage(repository);
		page.setNeedsHttpAuth(true);
		try {
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			page.createControl(shell);
			assertFalse(page.getSaveHttpPassword());
		} finally {
			page.dispose();
		}
	}

	public void testSaveProxyPassword() {
		TaskRepository repository = new TaskRepository(MockRepositoryConnector.CONNECTOR_KIND, "http://localhost/");
		TasksUiPlugin.getDefault().addRepositoryConnectorUi(new MockRepositoryConnectorUi());

		assertTrue(repository.getSavePassword(AuthenticationType.PROXY));

		MockRepositorySettingsPage page = new MockRepositorySettingsPage(repository);
		page.setNeedsProxy(true);
		try {
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			page.createControl(shell);
			assertTrue(page.getSaveProxyPassword());
		} finally {
			page.dispose();
		}

		repository.setCredentials(AuthenticationType.PROXY, null, false);
		page = new MockRepositorySettingsPage(repository);
		page.setNeedsProxy(true);
		try {
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			page.createControl(shell);
			assertFalse(page.getSaveProxyPassword());
		} finally {
			page.dispose();
		}
	}

	public void testValidUrl() throws Exception {
		TaskRepository repository = new TaskRepository(MockRepositoryConnector.CONNECTOR_KIND, "http://localhost/");
		MockRepositorySettingsPage page = new MockRepositorySettingsPage(repository);
		assertFalse(page.isValidUrl(""));
		assertFalse(page.isValidUrl("http:/google.com"));
		assertFalse(page.isValidUrl("http:/google.com/"));

		assertTrue(page.isValidUrl("http://google.com"));
		assertTrue(page.isValidUrl("https://google.com"));
		assertTrue(page.isValidUrl("http://mylyn.org/bugzilla34"));
		assertTrue(page.isValidUrl("http://www.mylyn.org/bugzilla34"));
	}

	public void testInjectConnectorIntoSettingsPage() throws Exception {
		TaskRepository repository = new TaskRepository(MockRepositoryConnector.CONNECTOR_KIND, "http://localhost/");
		MockRepositoryConnector connector = new MockRepositoryConnector();
		MockRepositorySettingsPage page = new MockRepositorySettingsPage(repository, connector);
		assertSame(page.getConnector(), connector);
	}

	public void testNeedsRepositoryCredentialsDefaultsToTrue() {
		TaskRepository repository = new TaskRepository(MockRepositoryConnector.CONNECTOR_KIND, "http://localhost/");
		MockRepositorySettingsPage page = new MockRepositorySettingsPage(repository);
		assertTrue(page.needsRepositoryCredentials());
	}

	public void testNeedsRepositoryCredentialsCanBeSetToFalse() {
		TaskRepository repository = new TaskRepository(MockRepositoryConnector.CONNECTOR_KIND, "http://localhost/");
		MockRepositorySettingsPage page = new MockRepositorySettingsPage(repository);
		page.setNeedsRepositoryCredentials(false);
		assertFalse(page.needsRepositoryCredentials());
	}

	public void testNeedsRepositoryCredentialsPageCompletesWithoutCredentials() {
		TaskRepository repository = new TaskRepository(MockRepositoryConnector.CONNECTOR_KIND, "http://localhost/");
		MockRepositorySettingsPage page = new MockRepositorySettingsPage(repository);
		page.setNeedsRepositoryCredentials(false);

		try {
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			page.createControl(shell);
			page.setUrl("http://example.com");
			assertNull(page.getRepositoryUserNameEditor());
			assertNull(page.getRepositoryPasswordEditor());
			assertTrue(page.isPageComplete());
			assertNull(page.getMessage());
			assertNotNull(page.createTaskRepository());
		} finally {
			page.dispose();
		}

	}

}
