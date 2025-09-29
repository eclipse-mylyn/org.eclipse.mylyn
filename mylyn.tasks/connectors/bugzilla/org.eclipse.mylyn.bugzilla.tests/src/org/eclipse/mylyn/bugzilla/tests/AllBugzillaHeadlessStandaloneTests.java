/*******************************************************************************
 * Copyright (c) 2009, 2013 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Frank Becker - improvements
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests;

import org.eclipse.mylyn.bugzilla.tests.core.BugzillaAttributeMapperTest;
import org.eclipse.mylyn.bugzilla.tests.core.BugzillaAttributeTest;
import org.eclipse.mylyn.bugzilla.tests.core.BugzillaClientTest;
import org.eclipse.mylyn.bugzilla.tests.core.BugzillaConfigurationTest;
import org.eclipse.mylyn.bugzilla.tests.core.BugzillaCustomFieldsTest;
import org.eclipse.mylyn.bugzilla.tests.core.BugzillaFlagsTest;
import org.eclipse.mylyn.bugzilla.tests.core.BugzillaRepositoryConnectorConfigurationTest;
import org.eclipse.mylyn.bugzilla.tests.core.BugzillaRepositoryConnectorStandaloneTest;
import org.eclipse.mylyn.bugzilla.tests.core.BugzillaUtilTest;
import org.eclipse.mylyn.bugzilla.tests.core.BugzillaVersionTest;
import org.eclipse.mylyn.bugzilla.tests.core.RepositoryConfigurationTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;


/**
 * @author Steffen Pingel
 * @author Thomas Ehrnhoefer
 * @author Frank Becker
 */
@Suite
@SelectClasses({ BugzillaConfigurationTest.class, BugzillaVersionTest.class, BugzillaDateTimeTests.class,
	BugzillaAttributeMapperTest.class, BugzillaAttributeTest.class, RepositoryConfigurationTest.class,
	// needs fixture
	BugzillaRepositoryConnectorStandaloneTest.class,
	BugzillaRepositoryConnectorConfigurationTest.class, BugzillaClientTest.class, BugzillaUtilTest.class,
	BugzillaCustomFieldsTest.class, BugzillaFlagsTest.class
})

public class AllBugzillaHeadlessStandaloneTests {

}
