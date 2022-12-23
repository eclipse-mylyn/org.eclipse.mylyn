package org.eclipse.mylyn.bugzilla.rest.tests;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.commons.sdk.util.Junit4TestFixtureRunner;
import org.eclipse.mylyn.commons.sdk.util.Junit4TestFixtureRunner.FixtureDefinition;
import org.junit.runner.RunWith;

@RunWith(Junit4TestFixtureRunner.class)
@FixtureDefinition(fixtureClass = TckFixture.class, fixtureType = "bugzillaREST")
public abstract class AbstractTckTest {

	private final TckFixture fixture;

	protected final IProgressMonitor monitor = new NullProgressMonitor();

	public AbstractTckTest(TckFixture fixture) {
		this.fixture = fixture;
	}

	public TckFixture fixture() {
		return fixture;
	}

}
