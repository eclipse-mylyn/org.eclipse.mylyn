package org.eclipse.mylyn.internal.bugzilla.rest.core.tests;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.sdk.util.Junit4TestFixtureRunner;
import org.eclipse.mylyn.commons.sdk.util.Junit4TestFixtureRunner.FixtureDefinition;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestConfiguration;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestConnector;
import org.eclipse.mylyn.internal.bugzilla.rest.core.Duration;
import org.eclipse.mylyn.internal.bugzilla.rest.test.support.BugzillaRestTestFixture;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

@RunWith(Junit4TestFixtureRunner.class)
@FixtureDefinition(fixtureClass = BugzillaRestTestFixture.class, fixtureType = "bugzillaREST")
public class BugzillaRestConnectorTest {
	private final BugzillaRestTestFixture actualFixture;

	private BugzillaRestConnector connector;

	private BugzillaRestConfiguration configuration;

	public BugzillaRestConnectorTest(BugzillaRestTestFixture fixture) {
		this.actualFixture = fixture;
	}

	@Before
	public void setUp() {
		connector = new BugzillaRestConnector();
	}

	@Test
	public void testReloadCache() throws Exception {
		connector = new BugzillaRestConnectorLocal(new Duration(5, TimeUnit.SECONDS));
		assertNotNull(connector);
		configuration = connector.getRepositoryConfiguration(actualFixture.repository());
		assertNotNull(configuration);

		// now wait until we know that the configuration is no longer valid
		// Parameter of Constructor from BugzillaRestConnector
		// (Default see BugzillaRestConnector.CONFIGURATION_CACHE_REFRESH_AFTER_WRITE_DURATION)
		BugzillaRestConfiguration configuration_new = waitAndGetConfiguration(6000L);
		assertEquals(configuration, configuration_new);

		waitForCacheRemoval();
		BugzillaRestConfiguration configuration_new1 = connector.getRepositoryConfiguration(actualFixture.repository());
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
		BugzillaRestConfiguration configuration = connector.getRepositoryConfiguration(actualFixture.repository());
		assertNotNull(configuration);
		BugzillaRestConfiguration configuration_new = waitAndGetConfiguration(6000L);
		assertEquals(configuration, configuration_new);
		BugzillaRestConfiguration configuration_new1 = waitAndGetConfiguration(3000L);
		assertEquals(configuration, configuration_new1);
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
		return connector.getRepositoryConfiguration(actualFixture.repository());
	}

	class BugzillaRestConnectorLocal extends BugzillaRestConnector {

		public BugzillaRestConnectorLocal(Duration refreshAfterWriteDuration) {
			super(refreshAfterWriteDuration);
		}

		@Override
		protected CacheBuilder<Object, Object> createCacheBuilder(Duration expireAfterWriteDuration,
				Duration refreshAfterWriteDuration) {
			return super.createCacheBuilder(expireAfterWriteDuration, refreshAfterWriteDuration).removalListener(
					new RemovalListener<Object, Object>() {
						@Override
						public void onRemoval(RemovalNotification<Object, Object> notification) {
							if (mySync != null) {
								synchronized (mySync) {
									mySync.notify();
								}
							}
						}
					});
		}
	}

}
