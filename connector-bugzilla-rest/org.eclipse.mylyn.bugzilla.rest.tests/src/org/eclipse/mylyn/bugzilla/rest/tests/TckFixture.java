package org.eclipse.mylyn.bugzilla.rest.tests;

import org.eclipse.mylyn.commons.sdk.util.AbstractTestFixture;
import org.eclipse.mylyn.commons.sdk.util.FixtureConfiguration;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;

public class TckFixture extends AbstractTestFixture {

	private final FixtureConfiguration config;

	public TckFixture(FixtureConfiguration config) {
		super("org.eclipse.mylyn.bugzilla.rest", config);
		this.config = config;
		setInfo("Bugzilla", config.getVersion(), config.getInfo());
	}

	public String getVersion() {
		return config.getVersion();
	}

	@Override
	protected AbstractTestFixture getDefault() {
		return this;
	}

	public TaskRepository createRepository() {
		return new TaskRepository(getConnectorKind(), getRepositoryUrl());
	}

	@SuppressWarnings("restriction")
	public AbstractRepositoryConnector connector() {
		return new org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestConnector();
	}

}
