/*******************************************************************************
 * Copyright (c) 2009, 2012 Frank Becker and others.
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

package org.eclipse.mylyn.bugzilla.tests.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import org.eclipse.mylyn.bugzilla.tests.AbstractBugzillaFixtureTest;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.PrivilegeLevel;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaVersion;
import org.eclipse.mylyn.internal.bugzilla.core.RepositoryConfiguration;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Frank Becker
 */
// TODO 3.5 merge into BugzillaRepositoryConnectorStandaloneTest when Bugzilla 3.6 is released
@SuppressWarnings("nls")
public class BugzillaRepositoryConnectorConfigurationTest extends AbstractBugzillaFixtureTest {

	@BeforeEach
	public void checkExcluded() {
		assumeFalse(fixture.isExcluded());
	}

	private TaskRepository repository;

	private BugzillaRepositoryConnector connector;

	public final static BugzillaVersion BUGZILLA_3_5 = new BugzillaVersion("3.5"); //$NON-NLS-1$

	@BeforeEach
	void setUp() throws Exception {
		fixture.client(PrivilegeLevel.USER);
		repository = fixture.repository();
		connector = fixture.connector();
	}

	@Test
	public void testGetRepositoryConfiguration() throws Exception {
		RepositoryConfiguration config = connector.getRepositoryConfiguration(repository, true, null);
		assertNotNull(config);
		String eTag = config.getETagValue();
		if (config.getInstallVersion().compareTo(BUGZILLA_3_5) < 0) {
			// older Bugzilla versions do not support the eTag
			assertNull(eTag);

			config.setETagValue("wrongETag");
			config = connector.getRepositoryConfiguration(repository, true, null);
			assertNotNull(config);
			String eTagNew = config.getETagValue();
			assertNull(eTagNew);
		} else {
			assertNotNull(eTag);

			config.setETagValue("wrongETag");
			config = connector.getRepositoryConfiguration(repository, true, null);
			assertNotNull(config);
			assertNotNull(config.getETagValue());
			String eTagNew = config.getETagValue();
			assertNotNull(eTagNew);
			assertEquals(eTag, eTagNew);
		}
	}

}
