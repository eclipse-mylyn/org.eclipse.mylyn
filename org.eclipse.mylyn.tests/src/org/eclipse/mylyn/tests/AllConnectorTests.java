/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tests;

import org.eclipse.mylyn.commons.sdk.util.ManagedSuite;
import org.eclipse.mylyn.commons.sdk.util.ManagedSuite.TestConfigurationProperty;
import org.eclipse.mylyn.tests.util.TestFixture;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Shawn Minto
 * @author Steffen Pingel
 * @author Frank Becker
 */
@RunWith(ManagedSuite.class)
@Suite.SuiteClasses({ //
//FIXME: AF: enable tests back
//https://github.com/eclipse-mylyn/.github/issues/3
//	AllBugzillaRestCoreTests.class, //
//FIXME: AF: remove? we already did these tests during component build
//		AllBugzillaTests.class, //
//		AllHudsonTests.class, //
//		AllGerritTests.class, //
//		AllTracTests.class //
})
@TestConfigurationProperty()
public class AllConnectorTests {
	static {
		TestFixture.initializeTasksSettings();
	}
}
