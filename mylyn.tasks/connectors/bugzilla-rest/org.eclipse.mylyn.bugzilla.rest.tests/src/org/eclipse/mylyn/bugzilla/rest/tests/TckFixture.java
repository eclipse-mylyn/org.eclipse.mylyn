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

package org.eclipse.mylyn.bugzilla.rest.tests;

import org.eclipse.mylyn.commons.sdk.util.AbstractTestFixture;
import org.eclipse.mylyn.commons.sdk.util.FixtureConfiguration;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;

@SuppressWarnings("nls")
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

	public AbstractRepositoryConnector connector() {
		return new org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestConnector();
	}

}
