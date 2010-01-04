/*******************************************************************************
 * Copyright (c) 2009 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests.core;

import junit.framework.TestCase;

import org.eclipse.mylyn.bugzilla.tests.support.BugzillaFixture;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaVersion;
import org.eclipse.mylyn.internal.bugzilla.core.RepositoryConfiguration;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Frank Becker
 */
// TODO 3.4 merge into BugzillaRepositoryConnectorStandaloneTest when Bugzilla 3.6 is released 
public class BugzillaRepositoryConnectorConfigurationTest extends TestCase {

	private TaskRepository repository;

	private BugzillaRepositoryConnector connector;

	public final static BugzillaVersion BUGZILLA_3_5 = new BugzillaVersion("3.5"); //$NON-NLS-1$

	@Override
	public void setUp() throws Exception {
		repository = BugzillaFixture.current().repository();
		connector = BugzillaFixture.current().connector();
	}

	public void testGetRepositoryConfiguration() throws Exception {
		RepositoryConfiguration config = connector.getRepositoryConfiguration(repository, true, null);
		assertNotNull(config);
		String eTag = config.getETagValue();
		if (config.getInstallVersion().isSmallerOrEquals(BUGZILLA_3_5)) {
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
