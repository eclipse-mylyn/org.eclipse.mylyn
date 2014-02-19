/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.security.storage.EncodingUtils;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.RepositoryTaskHandleUtil;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.core.util.TaskRepositoryKeyringMigrator;
import org.eclipse.mylyn.internal.tasks.core.util.TaskRepositorySecureStoreMigrator;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryQuery;
import org.eclipse.mylyn.tasks.tests.connector.MockTask;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class TaskRepositoryManagerTest extends TestCase {

	private static final String DEFAULT_KIND = MockRepositoryConnector.CONNECTOR_KIND;

	private static final String DEFAULT_URL = "http://eclipse.org";

	private static final String ANOTHER_URL = "http://codehaus.org";

	private final String USERNAME = ".username"; //$NON-NLS-1$

	private final String PASSWORD = ".password"; //$NON-NLS-1$

	private final String AUTH_REPOSITORY = "org.eclipse.mylyn.tasklist.repositories"; //$NON-NLS-1$

	private final String AUTH_PASSWORD = AUTH_REPOSITORY + PASSWORD;

	private final String AUTH_USERNAME = AUTH_REPOSITORY + USERNAME;

	private static final String SECURE_CREDENTIALS_STORE_NODE_ID = "org.eclipse.mylyn.commons.repository"; //$NON-NLS-1$

	private final String AUTH_HTTP = "org.eclipse.mylyn.tasklist.repositories.httpauth"; //$NON-NLS-1$

	private final String AUTH_HTTP_PASSWORD = AUTH_HTTP + PASSWORD;

	private final String AUTH_HTTP_USERNAME = AUTH_HTTP + USERNAME;

	private static final String AUTH_PROXY = "org.eclipse.mylyn.tasklist.repositories.proxy"; //$NON-NLS-1$

	private final String AUTH_PROXY_PASSWORD = AUTH_PROXY + PASSWORD;

	private final String AUTH_PROXY_USERNAME = AUTH_PROXY + USERNAME;

	private final String AUTH_SCHEME = "Basic"; //$NON-NLS-1$

	private final String AUTH_REALM = ""; //$NON-NLS-1$

	private TaskRepositoryManager manager;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		manager = TasksUiPlugin.getRepositoryManager();
		assertNotNull(manager);
		manager.clearRepositories();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (manager != null) {
			manager.clearRepositories();
		}
	}

	public void testsUseSecureStorage() throws Exception {
		TaskRepository repository = new TaskRepository("bugzilla", "http://repository2/");
		flushAuthenticationCredentials(repository);
		repository.setCredentials(AuthenticationType.REPOSITORY, new AuthenticationCredentials("testUserName",
				"testPassword"), true);

		repository.setCredentials(AuthenticationType.HTTP,
				new AuthenticationCredentials("httpUserName", "httpPassword"), true);

		ISecurePreferences securePreferences = getSecurePreferences(repository);
		assertEquals("testPassword", securePreferences.get(AUTH_PASSWORD, null));
		assertNull(securePreferences.get(AUTH_USERNAME, null));
		assertEquals("httpUserName", securePreferences.get(AUTH_HTTP_USERNAME, null));
		assertEquals("httpPassword", securePreferences.get(AUTH_HTTP_PASSWORD, null));
	}

	public void testsSaveCredentials() throws Exception {
		TaskRepository repository = new TaskRepository("bugzilla", "http://repository3/");
		repository.setCredentials(AuthenticationType.REPOSITORY, new AuthenticationCredentials("testUserName",
				"testPassword"), true);

		repository.setCredentials(AuthenticationType.HTTP,
				new AuthenticationCredentials("httpUserName", "httpPassword"), true);

		assertEquals("testUserName", repository.getCredentials(AuthenticationType.REPOSITORY).getUserName());
		assertEquals("testPassword", repository.getCredentials(AuthenticationType.REPOSITORY).getPassword());
		assertEquals("httpUserName", repository.getCredentials(AuthenticationType.HTTP).getUserName());
		assertEquals("httpPassword", repository.getCredentials(AuthenticationType.HTTP).getPassword());
	}

	public void testMigrationFromKeyring() throws Exception {
		Map<String, String> authInfo = createAuthInfo();

		TaskRepository repository = new TaskRepository("bugzilla", "http://example.com/");
		flushAuthenticationCredentials(repository);
		Platform.addAuthorizationInfo(new URL(repository.getUrl()), AUTH_REALM, AUTH_SCHEME, authInfo);
		new TaskRepositoryKeyringMigrator(AUTH_REALM, AUTH_SCHEME).migrateCredentials(Collections.singleton(repository));
		assertCredentialsMigrated(repository);

		repository = new TaskRepository("bugzilla", "I am not a url.");
		flushAuthenticationCredentials(repository);
		Platform.addAuthorizationInfo(new URL("http://eclipse.org/mylyn"), repository.getUrl(), AUTH_SCHEME, authInfo);
		new TaskRepositoryKeyringMigrator(AUTH_REALM, AUTH_SCHEME).migrateCredentials(Collections.singleton(repository));
		assertCredentialsMigrated(repository);
	}

	public void testMigrationFromKeyringAfterGetUserNameCalled() throws Exception {
		Map<String, String> authInfo = createAuthInfo();

		TaskRepository repository = new TaskRepository("bugzilla", "http://example.com/");
		flushAuthenticationCredentials(repository);
		Platform.addAuthorizationInfo(new URL(repository.getUrl()), AUTH_REALM, AUTH_SCHEME, authInfo);

		assertNull(repository.getUserName());

		new TaskRepositoryKeyringMigrator(AUTH_REALM, AUTH_SCHEME).migrateCredentials(Collections.singleton(repository));
		assertNotNull(repository.getUserName());
		assertTrue(repository.getUserName().equals("testuser"));
		assertCredentialsMigrated(repository);
	}

	private Map<String, String> createAuthInfo() {
		Map<String, String> authInfo = new HashMap<String, String>();
		authInfo.put(AUTH_USERNAME, "testuser");
		authInfo.put(AUTH_PASSWORD, "testpassword");
		authInfo.put(AUTH_HTTP_USERNAME, "testhttpuser");
		authInfo.put(AUTH_HTTP_PASSWORD, "testhttppassword");
		authInfo.put(AUTH_PROXY_USERNAME, "testproxyuser");
		authInfo.put(AUTH_PROXY_PASSWORD, "testproxypassword");
		return authInfo;
	}

	public void testMigrationFromOldSecureStoreNode() throws Exception {
		TaskRepository repository = new TaskRepository("bugzilla", "http://example.com/");
		flushAuthenticationCredentials(repository);
		repository.setProperty(AUTH_USERNAME, "testuser");

		ISecurePreferences oldNode = SecurePreferencesFactory.getDefault().node(ITasksCoreConstants.ID_PLUGIN);
		oldNode = oldNode.node(EncodingUtils.encodeSlashes(repository.getUrl()));
		oldNode.put(AUTH_PASSWORD, "testpassword", true);
		oldNode.put(AUTH_HTTP_USERNAME, "testhttpuser", false);
		oldNode.put(AUTH_HTTP_PASSWORD, "testhttppassword", true);
		oldNode.put(AUTH_PROXY_USERNAME, "testproxyuser", false);
		oldNode.put(AUTH_PROXY_PASSWORD, "testproxypassword", true);

		new TaskRepositorySecureStoreMigrator().migrateCredentials(Collections.singleton(repository));
		assertCredentialsMigrated(repository);
	}

	private void assertCredentialsMigrated(TaskRepository repository) throws CoreException, MalformedURLException,
			StorageException {
		assertEquals("testuser", repository.getProperty(AUTH_USERNAME));

		ISecurePreferences securePreferences = getSecurePreferences(repository);
		assertEquals("testpassword", securePreferences.get(AUTH_PASSWORD, null));
		assertEquals("testhttpuser", securePreferences.get(AUTH_HTTP_USERNAME, null));
		assertEquals("testhttppassword", securePreferences.get(AUTH_HTTP_PASSWORD, null));
		assertEquals("testproxyuser", securePreferences.get(AUTH_PROXY_USERNAME, null));
		assertEquals("testproxypassword", securePreferences.get(AUTH_PROXY_PASSWORD, null));

		for (String key : securePreferences.childrenNames()) {
			assertEquals(key.endsWith(PASSWORD), securePreferences.isEncrypted(key));
		}

		assertEquals("testpassword", repository.getCredentials(AuthenticationType.REPOSITORY).getPassword());
		assertEquals("testhttpuser", repository.getCredentials(AuthenticationType.HTTP).getUserName());
		assertEquals("testhttppassword", repository.getCredentials(AuthenticationType.HTTP).getPassword());
		assertEquals("testproxyuser", repository.getCredentials(AuthenticationType.PROXY).getUserName());
		assertEquals("testproxypassword", repository.getCredentials(AuthenticationType.PROXY).getPassword());
	}

	private ISecurePreferences getSecurePreferences(TaskRepository repository) {
		ISecurePreferences rootNode = SecurePreferencesFactory.getDefault().node(SECURE_CREDENTIALS_STORE_NODE_ID);
		return rootNode.node(EncodingUtils.encodeSlashes(repository.getUrl()));
	}

	private void flushAuthenticationCredentials(TaskRepository repository) {
		repository.flushAuthenticationCredentials();
		assertTrue(getSecurePreferences(repository).keys().length == 0);
		repository.setProperty(AUTH_REPOSITORY + ".enabled", Boolean.toString(true));
		repository.setProperty(AUTH_HTTP + ".enabled", Boolean.toString(true));
		repository.setProperty(AUTH_PROXY + ".enabled", Boolean.toString(true));
	}

	public void testRepositoryWithSlash() throws MalformedURLException {

		TaskRepository repository1 = new TaskRepository("bugzilla", "http://repository1/");
		manager.addRepository(repository1);
		assertNotNull(manager.getRepository("http://repository1"));
		assertNotNull(manager.getRepository("http://repository1/"));

		assertNotNull(manager.getRepository("bugzilla", "http://repository1"));
		assertNotNull(manager.getRepository("bugzilla", "http://repository1/"));

	}

	public void testQueryDeletion() {
		TaskRepository repository = new TaskRepository(MockRepositoryConnector.CONNECTOR_KIND,
				MockRepositoryConnector.REPOSITORY_URL);
		manager.addRepository(repository);

		MockTask task = new MockTask("1");
		MockRepositoryQuery query = new MockRepositoryQuery("Test");
		TasksUiPlugin.getTaskList().addQuery(query);
		TasksUiPlugin.getTaskList().addTask(task, query);

		assertNotNull(TasksUiPlugin.getTaskList().getTask(task.getHandleIdentifier()));
		TasksUiPlugin.getTaskList().deleteQuery(query);
		ITask task2 = TasksUiPlugin.getTaskList().getTask(task.getHandleIdentifier());
		assertNotNull(task2);
		assertEquals(1, ((AbstractTask) task2).getParentContainers().size());

	}

	public void testHandles() {
		String url = "http://foo.bar";
		String id = "123";
		String handle = RepositoryTaskHandleUtil.getHandle(url, id);
		assertEquals(url, RepositoryTaskHandleUtil.getRepositoryUrl(handle));
		assertEquals(id, RepositoryTaskHandleUtil.getTaskId(handle));
//		assertEquals(123, AbstractTask.getTaskIdAsInt(handle));
	}

	public void testMultipleNotAdded() throws MalformedURLException {
		TaskRepository repository = new TaskRepository(DEFAULT_KIND, DEFAULT_URL);
		manager.addRepository(repository);
		TaskRepository repository2 = new TaskRepository(DEFAULT_KIND, DEFAULT_URL);
		manager.addRepository(repository2);
		assertEquals(1, manager.getAllRepositories().size());
	}

	public void testGet() throws MalformedURLException {
		assertEquals("",
				TasksUiPlugin.getDefault().getPreferenceStore().getString(TaskRepositoryManager.PREF_REPOSITORIES));

		TaskRepository repository = new TaskRepository(DEFAULT_KIND, DEFAULT_URL);
		manager.addRepository(repository);
		assertEquals(repository, manager.getRepository(DEFAULT_KIND, DEFAULT_URL));
		assertNull(manager.getRepository(DEFAULT_KIND, "foo"));
		assertNull(manager.getRepository("foo", DEFAULT_URL));
	}

	public void testConnectorAddition() {
		AbstractRepositoryConnector connector = new MockRepositoryConnector();
		manager.addRepositoryConnector(connector);
		assertNotNull(manager.getRepositoryConnector(connector.getConnectorKind()));
	}

	public void testRepositoryPersistance() throws Exception {
		TaskRepository repository1 = new TaskRepository("bugzilla", "http://bugzilla");
		TaskRepository repository2 = new TaskRepository("jira", "http://jira");
		TaskRepository repository3 = new TaskRepository("local", "http://local");
		manager.addRepository(repository3);
		manager.addRepository(repository1);
		manager.addRepository(repository2);
		TaskTestUtil.saveNow();

		TasksUiPlugin.getExternalizationManager().load();
		//manager.readRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());
		if (manager.getRepositoryConnector("bugzilla") != null) {
			assertTrue(manager.getAllRepositories().contains(repository1));
		}
		if (manager.getRepositoryConnector("jira") != null) {
			assertTrue(manager.getAllRepositories().contains(repository2));
		}
		assertTrue(manager.getAllRepositories().contains(repository3));
	}

	public void testRepositoryAttributePersistance() throws Exception {
		assertEquals("",
				TasksUiPlugin.getDefault().getPreferenceStore().getString(TaskRepositoryManager.PREF_REPOSITORIES));

		String version = "123";
		String encoding = "UTF-16";
		String fakeTimeZone = "nowhere";
		Date now = new Date();
		String dateString = now.toString();

		TaskRepository repository1 = new TaskRepository("local", "http://bugzilla");
		repository1.setVersion(version);
		repository1.setCharacterEncoding(encoding);
		repository1.setTimeZoneId(fakeTimeZone);
		repository1.setSynchronizationTimeStamp(dateString);
		manager.addRepository(repository1);
		TaskTestUtil.saveNow();

		TasksUiPlugin.getExternalizationManager().load();
		//manager.readRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());
		TaskRepository temp = manager.getRepository(repository1.getConnectorKind(), repository1.getRepositoryUrl());
		assertNotNull(temp);
		assertEquals(version, temp.getVersion());
		assertEquals(encoding, temp.getCharacterEncoding());
		assertEquals(fakeTimeZone, temp.getTimeZoneId());
		assertEquals(dateString, temp.getSynchronizationTimeStamp());

	}

	public void testRepositoryPersistanceAfterDelete() throws MalformedURLException {

		TaskRepository repository = new TaskRepository(DEFAULT_KIND, DEFAULT_URL);
		manager.addRepository(repository);
		assertNotNull(manager.getRepository(repository.getConnectorKind(), repository.getRepositoryUrl()));

		TaskRepository repository2 = new TaskRepository(DEFAULT_KIND, ANOTHER_URL);
		manager.addRepository(repository2);
		assertNotNull(manager.getRepository(repository2.getConnectorKind(), repository2.getRepositoryUrl()));

		manager.removeRepository(repository2, TasksUiPlugin.getDefault().getRepositoriesFilePath());

		assertNull(manager.getRepository(repository2.getConnectorKind(), repository2.getRepositoryUrl()));
	}

	public void testRepositoryWithUnknownUrlHandler() {
		TaskRepository repository = new TaskRepository("eclipse.technology.mylar",
				"abc://news.eclipse.org/eclipse.technology.mylar");

		repository.setCredentials(AuthenticationType.REPOSITORY, new AuthenticationCredentials("testUser",
				"testPassword"), true);

		AuthenticationCredentials credentials = repository.getCredentials(AuthenticationType.REPOSITORY);
		assertNotNull(credentials);
		assertEquals("testUser", credentials.getUserName());
		assertEquals("testPassword", credentials.getPassword());
	}

	public void testRepositoryWithCustomAttributes() throws Exception {
		// Note: if a connector doesn't exist the associated repositories are not loaded (orphaned) 
		// causing this test to fail.
		AbstractRepositoryConnector connector = new MockRepositoryConnector();
		manager.addRepositoryConnector(connector);
		TaskRepository repository = new TaskRepository(MockRepositoryConnector.CONNECTOR_KIND,
				"http://mylyn.eclipse.org/");
		repository.setProperty("owner", "euxx");
		manager.addRepository(repository);
		TaskTestUtil.saveNow();

		TasksUiPlugin.getExternalizationManager().load();
		//manager.readRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());
		TaskRepository temp = manager.getRepository(repository.getConnectorKind(), repository.getRepositoryUrl());
		assertNotNull(temp);
		assertEquals("euxx", temp.getProperty("owner"));
	}

	public void testRepositoryPersistanceSameUrl() throws Exception {
		TaskRepository repository1 = new TaskRepository(LocalRepositoryConnector.CONNECTOR_KIND, "http://repository");
		TaskRepository repository2 = new TaskRepository(MockRepositoryConnector.CONNECTOR_KIND, "http://repository");
		manager.addRepository(repository1);
		manager.addRepository(repository2);
		assertEquals(2, manager.getAllRepositories().size());
		List<TaskRepository> repositoryList = new ArrayList<TaskRepository>();
		repositoryList.add(repository2);
		repositoryList.add(repository1);
		TaskTestUtil.saveNow();

		TasksUiPlugin.getExternalizationManager().load();
		//manager.readRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());
		assertEquals("got: " + manager.getAllRepositories(), 2, manager.getAllRepositories().size());
	}

	public void testDeletion() {
		TaskRepository repository = new TaskRepository(MockRepositoryConnector.CONNECTOR_KIND,
				MockRepositoryConnector.REPOSITORY_URL);
		manager.addRepository(repository);
		repository.setRepositoryUrl("http://newurl");
		manager.removeRepository(repository);
		assertNull(manager.getRepository("http://newurl"));
		assertNull(manager.getRepository(MockRepositoryConnector.CONNECTOR_KIND));
		assertEquals(Collections.emptySet(), manager.getRepositories(MockRepositoryConnector.CONNECTOR_KIND));
	}
}
