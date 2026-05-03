/*******************************************************************************
 * Copyright (c) 2016 Frank Becker and others.
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

import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.junit.platform.suite.api.BeforeSuite;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({ RepositoryKeyTest.class, BugzillaRestFlagMapperTest.class,
		BugzillaRestConnectorNoFixtureTest.class,
	// Needs fixture
	BugzillaRestClientTest.class, BugzillaRestConfigurationTest.class, BugzillaRestConnectorTest.class })
@SuppressWarnings("restriction")
public class AllBugzillaRestCoreTests {
	@BeforeSuite
	static void suiteSetup() {
		if (CommonTestUtil.fixProxyConfiguration()) {
			CommonTestUtil.dumpSystemInfo(System.err);
		}
	}
}
