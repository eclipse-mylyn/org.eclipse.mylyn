package org.eclipse.mylyn.internal.bugzilla.rest.core.tests;

import static org.junit.Assert.assertNull;

import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.sdk.util.Junit4TestFixtureRunner;
import org.eclipse.mylyn.commons.sdk.util.Junit4TestFixtureRunner.FixtureDefinition;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestConfiguration;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestConnector;
import org.eclipse.mylyn.internal.bugzilla.rest.test.support.BugzillaRestTestFixture;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Junit4TestFixtureRunner.class)
@FixtureDefinition(fixtureClass = BugzillaRestTestFixture.class, fixtureType = "bugzillaREST")
public class BugzillaRestConnectorTest {
	private final BugzillaRestTestFixture actualFixture;

	private static TaskRepositoryManager manager;

	private BugzillaRestConnector connector;

	public BugzillaRestConnectorTest(BugzillaRestTestFixture fixture) {
		this.actualFixture = fixture;
	}

	@Before
	public void setUp() {
		connector = new BugzillaRestConnector();
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

}
