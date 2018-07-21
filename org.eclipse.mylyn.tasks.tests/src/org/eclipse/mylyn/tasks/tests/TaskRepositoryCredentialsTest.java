/*******************************************************************************
 * Copyright (c) 2004, 2014 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.tests;

import java.net.URL;
import java.util.Collections;
import java.util.Date;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Platform;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.repositories.core.ILocationService;
import org.eclipse.mylyn.commons.repositories.core.auth.ICredentialsStore;
import org.eclipse.mylyn.internal.commons.repositories.core.LocationService;
import org.eclipse.mylyn.internal.tasks.core.IRepositoryConstants;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.tests.util.TestUtils;

/**
 * @author Mik Kersten
 */
public class TaskRepositoryCredentialsTest extends TestCase {

	private static final String AUTH_REPOSITORY = "org.eclipse.mylyn.tasklist.repositories";

	private static final String AUTH_HTTP = "org.eclipse.mylyn.tasklist.repositories.httpauth";

	private static final String AUTH_CERT = "org.eclipse.mylyn.tasklist.repositories.certauth";

	private static final String AUTH_PROXY = "org.eclipse.mylyn.tasklist.repositories.proxy";

	private static final String PASSWORD = ".password";

	private static String getKeyPrefix(AuthenticationType type) {
		switch (type) {
		case HTTP:
			return AUTH_HTTP;
		case CERTIFICATE:
			return AUTH_CERT;
		case PROXY:
			return AUTH_PROXY;
		case REPOSITORY:
			return AUTH_REPOSITORY;
		}
		throw new IllegalArgumentException("Unknown authentication type: " + type); //$NON-NLS-1$
	}

	private String getPassword(AuthenticationType authType) {
		ICredentialsStore store = service.getCredentialsStore(taskRepository.getRepositoryUrl());
		String password = store.get(getKeyPrefix(authType) + PASSWORD, null);
		return password;
	}

	private TaskRepository taskRepository;

	private ILocationService service;

	@Override
	protected void setUp() throws Exception {
		service = LocationService.getDefault();
		taskRepository = new TaskRepository("kind", "http://url");
	}

	@Override
	protected void tearDown() throws Exception {
		taskRepository.flushAuthenticationCredentials();
	}

	@SuppressWarnings("deprecation")
	public void testPlatformAuthHandlerAvailable() throws Exception {
		if (!TestUtils.isCompatibilityAuthInstalled()) {
			System.err.println("Skipping TaskRepositoryCredentialsTest.testPlatformAuthHandlerAvailable()");
			return;
		}
		URL url = new URL("http://mylyn");
		Platform.addAuthorizationInfo(url, "", "", Collections.EMPTY_MAP);
		assertNotNull("Tests require org.eclipse.core.runtime.compatibility.auth",
				Platform.getAuthorizationInfo(url, "", ""));
	}

	public void testLabel() {
		TaskRepository repository = new TaskRepository("kind", "http://foo.bar");
		assertTrue(repository.getRepositoryLabel().equals(repository.getRepositoryUrl()));

		repository.setProperty(IRepositoryConstants.PROPERTY_LABEL, "label");
		assertTrue(repository.getRepositoryLabel().equals("label"));
	}

	@SuppressWarnings("deprecation")
	public void testPassword() throws Exception {
		assertCredentials(AuthenticationType.REPOSITORY);

		// test old API
		taskRepository.setCredentials(AuthenticationType.REPOSITORY, new AuthenticationCredentials("user", "pwd"), true);
		assertEquals("user", taskRepository.getUserName());
		assertEquals("pwd", taskRepository.getPassword());

		assertEquals(null, taskRepository.getHttpUser());
		assertEquals(null, taskRepository.getHttpPassword());
	}

	@SuppressWarnings("deprecation")
	public void testHttpPassword() throws Exception {
		assertCredentials(AuthenticationType.HTTP);

		TaskRepository taskRepository = new TaskRepository("kind", "url");
		taskRepository.setCredentials(AuthenticationType.HTTP, new AuthenticationCredentials("user", "pwd"), true);
		assertEquals("user", taskRepository.getHttpUser());
		assertEquals("pwd", taskRepository.getHttpPassword());
	}

	@SuppressWarnings("deprecation")
	public void testProxyPassword() throws Exception {
		assertCredentials(AuthenticationType.PROXY);

		TaskRepository taskRepository = new TaskRepository("kind", "url");
		taskRepository.setCredentials(AuthenticationType.PROXY, new AuthenticationCredentials("user", "pwd"), false);
		assertEquals("user", taskRepository.getProxyUsername());
		assertEquals("pwd", taskRepository.getProxyPassword());
	}

	@SuppressWarnings("deprecation")
	public void testFlushCredentials() throws Exception {
		TaskRepository taskRepository = new TaskRepository("kind", "url");
		taskRepository.setCredentials(AuthenticationType.REPOSITORY, new AuthenticationCredentials("user", "pwd"),
				false);
		taskRepository.setCredentials(AuthenticationType.HTTP, new AuthenticationCredentials("user", "pwd"), true);
		taskRepository.setCredentials(AuthenticationType.PROXY, new AuthenticationCredentials("user", "pwd"), true);

		assertNotNull(taskRepository.getCredentials(AuthenticationType.REPOSITORY));
		assertNotNull(taskRepository.getCredentials(AuthenticationType.HTTP));
		assertNotNull(taskRepository.getCredentials(AuthenticationType.PROXY));

		taskRepository.flushAuthenticationCredentials();

		assertNotNull(taskRepository.getUserName());// username is not flushed
		assertNull(taskRepository.getPassword());
		assertNull(taskRepository.getHttpUser());
		assertNull(taskRepository.getHttpPassword());
		assertNull(taskRepository.getProxyUsername());
		assertNull(taskRepository.getProxyPassword());

		assertNull(taskRepository.getCredentials(AuthenticationType.REPOSITORY));
		assertNull(taskRepository.getCredentials(AuthenticationType.HTTP));
		assertNull(taskRepository.getCredentials(AuthenticationType.PROXY));
	}

	public void testPlatformIsRunning() {
		assertTrue(Platform.isRunning());
	}

	public void assertCredentials(AuthenticationType authType) throws Exception {
		assertNull(getPassword(authType));
		try {
			taskRepository.flushAuthenticationCredentials();

			assertNull(taskRepository.getCredentials(authType));
			assertTrue(taskRepository.getSavePassword(authType));

			taskRepository.setCredentials(authType, new AuthenticationCredentials("user", "pwd"), true);
			AuthenticationCredentials credentials = taskRepository.getCredentials(authType);
			assertNotNull(credentials);
			assertEquals("user", credentials.getUserName());
			assertEquals("pwd", credentials.getPassword());

			assertEquals("pwd", getPassword(authType));

			// test not saving password
			taskRepository.setCredentials(authType, new AuthenticationCredentials("user1", "pwd1"), false);
			assertFalse(taskRepository.getSavePassword(authType));
			credentials = taskRepository.getCredentials(authType);
			assertNotNull(credentials);
			assertEquals("user1", credentials.getUserName());
			assertEquals("pwd1", credentials.getPassword());

			// make sure old passwords are not stored
			assertNull(getPassword(authType));

			taskRepository.setCredentials(authType, new AuthenticationCredentials("user2", "pwd2"), true);
			assertTrue(taskRepository.getSavePassword(authType));
			credentials = taskRepository.getCredentials(authType);
			assertNotNull(credentials);
			assertEquals("user2", credentials.getUserName());
			assertEquals("pwd2", credentials.getPassword());
		} finally {
			taskRepository.flushAuthenticationCredentials();
		}
	}

	public void testConfigUpdateStoring() throws Exception {
		Date stamp = taskRepository.getConfigurationDate();
		assertNull("unset configuration date returns null", stamp);
		stamp = new Date();
		stamp.setTime(stamp.getTime() - 35000L);

		taskRepository.setConfigurationDate(stamp);
		assertEquals("Time stamp set", stamp.getTime(), taskRepository.getConfigurationDate().getTime());
	}

	public void testDoNotPersistCredentials() throws Exception {
		taskRepository.setCredentials(AuthenticationType.REPOSITORY, new AuthenticationCredentials("user", "pwd"), true);
		assertEquals("pwd", taskRepository.getCredentials(AuthenticationType.REPOSITORY).getPassword());

		taskRepository.setShouldPersistCredentials(false);
		taskRepository.setCredentials(AuthenticationType.REPOSITORY, new AuthenticationCredentials("user", "newpwd"),
				true);
		assertEquals("newpwd", taskRepository.getCredentials(AuthenticationType.REPOSITORY).getPassword());

		taskRepository.setShouldPersistCredentials(true);
		assertEquals("pwd", taskRepository.getCredentials(AuthenticationType.REPOSITORY).getPassword());
	}

	public void testSetCredentialsDoesNotAffectExistingRepositoryWhenShouldNotPersistIsSetToTrue() throws Exception {
		taskRepository.setCredentials(AuthenticationType.REPOSITORY, new AuthenticationCredentials("user", "pwd"), true);
		assertEquals("pwd", taskRepository.getCredentials(AuthenticationType.REPOSITORY).getPassword());

		TaskRepository newRepository = new TaskRepository("kind", "http://url");
		newRepository.setShouldPersistCredentials(false);
		newRepository.setCredentials(AuthenticationType.REPOSITORY, new AuthenticationCredentials("newuser", "newpwd"),
				true);
		assertEquals("pwd", taskRepository.getCredentials(AuthenticationType.REPOSITORY).getPassword());
		assertEquals("newpwd", newRepository.getCredentials(AuthenticationType.REPOSITORY).getPassword());

		taskRepository.setCredentials(AuthenticationType.REPOSITORY, new AuthenticationCredentials("user", "pwd2"),
				true);
		assertEquals("pwd2", taskRepository.getCredentials(AuthenticationType.REPOSITORY).getPassword());
		assertEquals("newpwd", newRepository.getCredentials(AuthenticationType.REPOSITORY).getPassword());
	}

	public void testSetCredentialsAffectExistingRepository() throws Exception {
		taskRepository.setCredentials(AuthenticationType.REPOSITORY, new AuthenticationCredentials("user", "pwd"), true);
		assertEquals("pwd", taskRepository.getCredentials(AuthenticationType.REPOSITORY).getPassword());

		TaskRepository newRepository = new TaskRepository("kind", "http://url");
		newRepository.setCredentials(AuthenticationType.REPOSITORY, new AuthenticationCredentials("newuser", "newpwd"),
				true);
		assertEquals("newpwd", taskRepository.getCredentials(AuthenticationType.REPOSITORY).getPassword());
		assertEquals("newpwd", newRepository.getCredentials(AuthenticationType.REPOSITORY).getPassword());
	}

}
