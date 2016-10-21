/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tests;

import org.eclipse.mylyn.bugzilla.rest.core.tests.AllBugzillaRestCoreTests;
import org.eclipse.mylyn.bugzilla.tests.AllBugzillaTests;
import org.eclipse.mylyn.commons.sdk.util.ManagedSuite;
import org.eclipse.mylyn.commons.sdk.util.ManagedSuite.TestConfigurationProperty;
import org.eclipse.mylyn.gerrit.tests.AllGerritTests;
import org.eclipse.mylyn.hudson.tests.AllHudsonTests;
import org.eclipse.mylyn.tests.util.TestFixture;
import org.eclipse.mylyn.trac.tests.AllTracTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Shawn Minto
 * @author Steffen Pingel
 * @author Frank Becker
 */
@RunWith(ManagedSuite.class)
@Suite.SuiteClasses({ AllBugzillaRestCoreTests.class, AllBugzillaTests.class, AllHudsonTests.class,
		AllGerritTests.class, AllTracTests.class })
@TestConfigurationProperty()
public class AllConnectorTests {
	static {
		TestFixture.initializeTasksSettings();
	}
}
