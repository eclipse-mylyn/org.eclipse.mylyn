package org.eclipse.mylyn.internal.bugzilla.rest.core.tests;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.concurrent.TimeUnit;

import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.sdk.util.Junit4TestFixtureRunner;
import org.eclipse.mylyn.commons.sdk.util.Junit4TestFixtureRunner.FixtureDefinition;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestConfiguration;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestConnector;
import org.eclipse.mylyn.internal.bugzilla.rest.core.Duration;
import org.eclipse.mylyn.internal.bugzilla.rest.test.support.BugzillaRestTestFixture;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
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

	private static TaskRepositoryManager manager;

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
		System.out.println(new java.util.Date().toString());
		configuration = connector.getRepositoryConfiguration(actualFixture.repository());
		System.out.println(new java.util.Date().toString());
		assertNotNull(configuration);
		Thread.sleep(6000L);
		System.out.println(new java.util.Date().toString());
		BugzillaRestConfiguration configuration_new = connector.getRepositoryConfiguration(actualFixture.repository());
		System.out.println("after configuration_new");
		System.out.println(new java.util.Date().toString());
		assertEquals(configuration, configuration_new);
		mySync = this;
		synchronized (mySync) {
			try {
				mySync.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		mySync = null;

		System.out.println("after configuration reload");
		System.out.println(new java.util.Date().toString());
		BugzillaRestConfiguration configuration_new1 = connector.getRepositoryConfiguration(actualFixture.repository());
		System.out.println(new java.util.Date().toString());
		assertThat(configuration, not(configuration_new1));
	}

	@Test
	public void testLoadCache() throws Exception {
		BugzillaRestConfiguration configuration = connector.getRepositoryConfiguration(actualFixture.repository());
		assertNotNull(configuration);
		Thread.sleep(7000L);
		BugzillaRestConfiguration configuration_new = connector.getRepositoryConfiguration(actualFixture.repository());
		assertEquals(configuration, configuration_new);
		Thread.sleep(4000L);
		BugzillaRestConfiguration configuration_new1 = connector.getRepositoryConfiguration(actualFixture.repository());
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
