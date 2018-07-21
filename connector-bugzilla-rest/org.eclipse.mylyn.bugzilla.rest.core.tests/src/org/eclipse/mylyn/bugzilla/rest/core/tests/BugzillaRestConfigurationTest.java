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
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.rest.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.bugzilla.rest.test.support.BugzillaRestTestFixture;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.commons.sdk.util.Junit4TestFixtureRunner;
import org.eclipse.mylyn.commons.sdk.util.Junit4TestFixtureRunner.FixtureDefinition;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestConfiguration;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestConnector;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gson.Gson;

@SuppressWarnings("restriction")
@RunWith(Junit4TestFixtureRunner.class)
@FixtureDefinition(fixtureClass = BugzillaRestTestFixture.class, fixtureType = "bugzillaREST")
//@RunOnlyWhenProperty(property = "default", value = "1")
public class BugzillaRestConfigurationTest {
	private final BugzillaRestTestFixture actualFixture;

	private static TaskRepositoryManager manager;

	private BugzillaRestConnector connector;

	public BugzillaRestConfigurationTest(BugzillaRestTestFixture fixture) {
		this.actualFixture = fixture;
	}

	@BeforeClass
	public static void setUpClass() {
		manager = new TaskRepositoryManager();
	}

	@Before
	public void setUp() {
		manager.addRepository(actualFixture.repository());
		connector = new BugzillaRestConnector();
	}

	@After
	public void tearDown() throws Exception {
		manager.clearRepositories();
	}

	@Test
	public void testConfigurationFromConnector() throws CoreException, IOException {
		BugzillaRestConfiguration configuration = connector.getRepositoryConfiguration(actualFixture.repository());
		assertNotNull(configuration);
		assertEquals(
				IOUtils.toString(
						CommonTestUtil.getResource(this, actualFixture.getTestDataFolder() + "/configuration.json")),
				new Gson().toJson(configuration).replaceAll(actualFixture.repository().getRepositoryUrl(),
						"http://dummy.url"));
	}

}
