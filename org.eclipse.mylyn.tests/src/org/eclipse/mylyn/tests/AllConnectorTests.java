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

import org.eclipse.mylyn.tests.util.TestFixture;
import org.junit.jupiter.api.Test;

/**
 * @author Shawn Minto
 * @author Steffen Pingel
 * @author Frank Becker
 */
//@Suite
//@SelectClasses({ //
//FIXME: AF: enable tests back
//https://github.com/eclipse-mylyn/.github/issues/3
//	AllBugzillaRestCoreTests.class, //
//FIXME: AF: remove? we already did these tests during component build
//		AllBugzillaTests.class, //
//		AllJenkinsTests.class, //
//		AllGerritTests.class, //
//		AllTracTests.class //
//})
public class AllConnectorTests {
//	@BeforeSuite
	static void suiteSetup() {
		TestFixture.initializeTasksSettings();
	}

	/* * This dummy test is required to prevent JUnit from complaining about no tests found in this class.
	 *
	 */
	@Test
	public void dummy() {

	}
}
