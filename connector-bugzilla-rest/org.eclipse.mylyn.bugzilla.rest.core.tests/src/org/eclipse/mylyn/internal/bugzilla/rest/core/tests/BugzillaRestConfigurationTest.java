/*******************************************************************************
 * Copyright (c) 2014 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.rest.core.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.commons.sdk.util.Junit4TestFixtureRunner;
import org.eclipse.mylyn.commons.sdk.util.Junit4TestFixtureRunner.FixtureDefinition;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestConnector;
import org.eclipse.mylyn.internal.bugzilla.rest.core.RepositoryKey;
import org.eclipse.mylyn.internal.bugzilla.rest.test.support.BugzillaRestTestFixture;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@SuppressWarnings("restriction")
@RunWith(Junit4TestFixtureRunner.class)
@FixtureDefinition(fixtureClass = BugzillaRestTestFixture.class, fixtureType = "bugzillaREST")
//@RunOnlyWhenProperty(property = "default", value = "1")
public class BugzillaRestConfigurationTest {
	private final BugzillaRestTestFixture actualFixture;

	private static TaskRepositoryManager manager;

	public BugzillaRestConfigurationTest(BugzillaRestTestFixture fixture) {
		this.actualFixture = fixture;
	}

	@BeforeClass
	public static void setUpClass() {
		manager = new TaskRepositoryManager();
	}

	@Test
	public void testRepositoryKey() throws CoreException {
		RepositoryKey rep1 = new RepositoryKey(new TaskRepository("xx", "url"));
		RepositoryKey rep2 = new RepositoryKey(new TaskRepository("xx1", "url1"));
		RepositoryKey rep3 = new RepositoryKey(new TaskRepository("xx", "url"));
		assertTrue(rep1.equals(rep1));
		assertTrue(rep1.equals(rep3));
		assertFalse(rep1.equals(rep2));
	}

	@Test
	public void testConfigurationFromConnector() throws CoreException {
		BugzillaRestConnector connector = new BugzillaRestConnector();
		assertNotNull(connector);
		assertNull(connector.getRepositoryConfiguration(actualFixture.repository()));
	}

}
