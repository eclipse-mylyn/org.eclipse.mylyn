/*******************************************************************************
 * Copyright (c) 2014 Frank Becker and others.
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


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.bugzilla.rest.test.support.AbstractDefaultBugzillaRestFixtureTest;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestConfiguration;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestConnector;
import org.eclipse.mylyn.internal.commons.core.FileUtil;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

@SuppressWarnings({ "nls", "restriction" })

public class BugzillaRestConfigurationTest extends AbstractDefaultBugzillaRestFixtureTest {
	private static TaskRepositoryManager manager;

	private BugzillaRestConnector connector;

	@BeforeAll
	public static void setUpClass() {
		manager = new TaskRepositoryManager();
	}

	@BeforeEach
	public void setUp() {
		manager.addRepository(fixture.repository());
		connector = new BugzillaRestConnector();
	}

	@AfterEach
	public void tearDown() throws Exception {
		manager.clearRepositories();
	}

	@Test
	public void testConfigurationFromConnector() throws CoreException, IOException {
		BugzillaRestConfiguration configuration = connector.getRepositoryConfiguration(fixture.repository());
		assertNotNull(configuration);
		assertEquals(FileUtil.readFile(
				CommonTestUtil.getResource(this, fixture.getTestDataFolder() + "/configuration.json")
		),
				new Gson().toJson(configuration)
				.replaceAll(fixture.getRepositoryUrl(), "http://dummy.url")
				.replaceAll(fixture.getRepositoryUrl().replaceFirst("https://", "http://"),
						"http://dummy.url"));

	}

}
