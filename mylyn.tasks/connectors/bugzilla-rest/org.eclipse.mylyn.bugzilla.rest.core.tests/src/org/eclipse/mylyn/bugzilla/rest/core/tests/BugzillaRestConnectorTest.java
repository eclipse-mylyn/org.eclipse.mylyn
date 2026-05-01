/*******************************************************************************
 * Copyright (c) 2013 Frank Becker and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.rest.core.tests;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.bugzilla.rest.test.support.AbstractDefaultBugzillaRestFixtureTest;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.commons.repositories.core.auth.UserCredentials;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.PrivilegeLevel;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestConfiguration;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestConnector;
import org.eclipse.mylyn.internal.bugzilla.rest.core.Duration;
import org.eclipse.mylyn.internal.tasks.core.IRepositoryConstants;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.benmanes.caffeine.cache.Caffeine;

@SuppressWarnings({ "nls", "restriction" })
public class BugzillaRestConnectorTest extends AbstractDefaultBugzillaRestFixtureTest {

	private BugzillaRestConnector connector;

	private BugzillaRestConfiguration configuration;

	@BeforeEach
	public void setUp() {
		connector = new BugzillaRestConnector();
	}

	@Test
	public void testReloadCache() throws Exception {
		connector = new BugzillaRestConnectorLocal(new Duration(5, TimeUnit.SECONDS));
		assertNotNull(connector);
		configuration = connector.getRepositoryConfiguration(fixture.repository());
		assertNotNull(configuration);

		// now wait until we know that the configuration is no longer valid
		// Parameter of Constructor from BugzillaRestConnector
		// (Default see BugzillaRestConnector.CONFIGURATION_CACHE_REFRESH_AFTER_WRITE_DURATION)
		BugzillaRestConfiguration configuration_new = waitAndGetConfiguration(6000L);
		assertEquals(configuration, configuration_new);

		waitForCacheRemoval();
		BugzillaRestConfiguration configuration_new1 = connector.getRepositoryConfiguration(fixture.repository());
		assertThat(configuration, not(configuration_new1));
	}

	private void waitForCacheRemoval() throws InterruptedException {
		mySync = this;
		synchronized (mySync) {
			mySync.wait();
		}
		mySync = null;
	}

	@Test
	public void testLoadCache() throws Exception {
		BugzillaRestConfiguration configuration = connector.getRepositoryConfiguration(fixture.repository());
		assertNotNull(configuration);
		BugzillaRestConfiguration configuration_new = waitAndGetConfiguration(6000L);
		assertEquals(configuration, configuration_new);
		BugzillaRestConfiguration configuration_new1 = waitAndGetConfiguration(3000L);
		assertEquals(configuration, configuration_new1);
	}

	@Test
	public void testRepositoryCacheNotChanged() throws Exception {
		TaskRepository repository = new TaskRepository(fixture.repository().getConnectorKind(),
				fixture.repository().getRepositoryUrl());
		UserCredentials credentials = CommonTestUtil.getCredentials(PrivilegeLevel.USER);
		repository.setCredentials(org.eclipse.mylyn.commons.net.AuthenticationType.REPOSITORY,
				new AuthenticationCredentials(credentials.getUserName(), credentials.getPassword()), true);

		// load a configuration into the cache
		BugzillaRestConfiguration configuration = connector.getRepositoryConfiguration(repository);
		assertNotNull(configuration);

		// test that with an wrong user the configuration is removed from the cache
		String orgUserName = repository.getProperty("org.eclipse.mylyn.tasklist.repositories.username");
		repository.setProperty("org.eclipse.mylyn.tasklist.repositories.username", "xxx");
		BugzillaRestConfiguration configurationForCompare = connector.getRepositoryConfiguration(repository);
		assertNull(configurationForCompare);

		// load a configuration into the cache
		repository.setProperty("org.eclipse.mylyn.tasklist.repositories.username", orgUserName);
		configurationForCompare = connector.getRepositoryConfiguration(repository);
		assertThat(configuration, not(equalTo(configurationForCompare)));
		// the following three properties do not remove the entry from the cache
		repository.setProperty(RepositoryLocation.PROPERTY_LABEL, "nolabel");
		BugzillaRestConfiguration newConfiguration = connector.getRepositoryConfiguration(repository);
		assertNotNull(newConfiguration);
		assertEquals(configurationForCompare, newConfiguration);
		repository.setProperty(TaskRepository.OFFLINE, "false");
		newConfiguration = connector.getRepositoryConfiguration(repository);
		assertNotNull(newConfiguration);
		assertEquals(configurationForCompare, newConfiguration);
		repository.setProperty(TaskRepository.OFFLINE, "false");
		newConfiguration = connector.getRepositoryConfiguration(repository);
		assertNotNull(newConfiguration);
		assertEquals(configurationForCompare, newConfiguration);
		repository.setProperty(IRepositoryConstants.PROPERTY_ENCODING, "UTF-16");
		newConfiguration = connector.getRepositoryConfiguration(repository);
		assertNotNull(newConfiguration);
		assertEquals(configurationForCompare, newConfiguration);
		repository.setProperty(TaskRepository.PROXY_HOSTNAME, "nohost");
		newConfiguration = connector.getRepositoryConfiguration(repository);
		assertNotNull(newConfiguration);
		assertEquals(configurationForCompare, newConfiguration);
		repository.setProperty(TaskRepository.PROXY_PORT, "noport");
		newConfiguration = connector.getRepositoryConfiguration(repository);
		assertNotNull(newConfiguration);
		assertEquals(configurationForCompare, newConfiguration);
		repository.setProperty("org.eclipse.mylyn.tasklist.repositories.savePassword", "true");
		newConfiguration = connector.getRepositoryConfiguration(repository);
		assertNotNull(newConfiguration);
		assertEquals(configurationForCompare, newConfiguration);
	}

	@Test
	public void testRepositoryCacheChanged() throws Exception {
		TaskRepository repository = new TaskRepository(fixture.repository().getConnectorKind(),
				fixture.repository().getRepositoryUrl());
		UserCredentials credentials = CommonTestUtil.getCredentials(PrivilegeLevel.USER);
		repository.setCredentials(org.eclipse.mylyn.commons.net.AuthenticationType.REPOSITORY,
				new AuthenticationCredentials(credentials.getUserName(), credentials.getPassword()), true);

		// load a configuration into the cache
		BugzillaRestConfiguration configuration = connector.getRepositoryConfiguration(repository);
		assertNotNull(configuration);

		// test that with an wrong user the configuration is removed from the cache
		String orgUserName = repository.getProperty("org.eclipse.mylyn.tasklist.repositories.username");
		repository.setProperty("org.eclipse.mylyn.tasklist.repositories.username", "xxx");
		BugzillaRestConfiguration configurationForCompare = connector.getRepositoryConfiguration(repository);
		assertNull(configurationForCompare);
		repository.setProperty("org.eclipse.mylyn.tasklist.repositories.username", orgUserName);
		configurationForCompare = connector.getRepositoryConfiguration(repository);
		assertNotNull(configurationForCompare);

		repository.setProperty(IRepositoryConstants.PROPERTY_TIMEZONE, "Europe/Brussels");
		BugzillaRestConfiguration newConfiguration = connector.getRepositoryConfiguration(repository);
		assertNotNull(newConfiguration);
		assertThat(configurationForCompare, not(equalTo(newConfiguration)));
		configurationForCompare = newConfiguration;

		repository.setProperty("version", "xxx");
		newConfiguration = connector.getRepositoryConfiguration(repository);
		assertNotNull(newConfiguration);
		assertThat(configurationForCompare, not(equalTo(newConfiguration)));
	}

	@Test
	public void testLoadCacheWrongRepository() throws Exception {
		TaskRepository taskRepository = new TaskRepository(connector.getConnectorKind(),
				"http://mylyn.org/bugzilla-rest-trunk-wrong/");
		AuthenticationCredentials credentials = new AuthenticationCredentials("username", "password");
		taskRepository.setCredentials(AuthenticationType.REPOSITORY, credentials, false);
		BugzillaRestConfiguration configuration = connector.getRepositoryConfiguration(taskRepository);
		assertNull(configuration);
	}

	BugzillaRestConnectorTest mySync;

	private BugzillaRestConfiguration waitAndGetConfiguration(long millis) throws InterruptedException, CoreException {
		Thread.sleep(millis);
		return connector.getRepositoryConfiguration(fixture.repository());
	}

	class BugzillaRestConnectorLocal extends BugzillaRestConnector {

		public BugzillaRestConnectorLocal(Duration refreshAfterWriteDuration) {
			super(refreshAfterWriteDuration);
		}

		@Override
		protected Caffeine<Object, Object> createCacheBuilder(Duration expireAfterWriteDuration,
				Duration refreshAfterWriteDuration) {
			return super.createCacheBuilder(expireAfterWriteDuration, refreshAfterWriteDuration)
					.removalListener((key, value, cause) -> {
						if (mySync != null) {
							synchronized (mySync) {
								mySync.notify();
							}
						}
					});
		}
	}

	@Test
	public void testUpdateTaskFromTaskData() throws Exception {
		TaskData taskData = new TaskData(new TaskAttributeMapper(fixture.repository()), connector.getConnectorKind(),
				fixture.repository().getRepositoryUrl(), "123");
		ITask task = new TaskTask(connector.getConnectorKind(), fixture.repository().getRepositoryUrl(), "");
		connector.updateTaskFromTaskData(fixture.repository(), task, taskData);
		assertThat(task.getUrl(), equalTo(fixture.repository().getRepositoryUrl() + "/rest.cgi/bug/123"));
	}
}
