/*******************************************************************************
 * Copyright (c) 2006, 2009 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.PrivilegeLevel;
import org.eclipse.mylyn.commons.sdk.util.ManagedTestSuite;
import org.eclipse.mylyn.commons.sdk.util.TestConfiguration;
import org.eclipse.mylyn.commons.sdk.util.TestConfiguration.TestKind;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient.Version;
import org.eclipse.mylyn.trac.tests.core.TracAttachmentHandlerTest;
import org.eclipse.mylyn.trac.tests.core.TracRepositoryConnectorTest;
import org.eclipse.mylyn.trac.tests.core.TracRepositoryConnectorWebTest;
import org.eclipse.mylyn.trac.tests.core.TracRepositoryQueryTest;
import org.eclipse.mylyn.trac.tests.core.TracTaskDataHandlerXmlRpcTest;
import org.eclipse.mylyn.trac.tests.core.TracUtilTest;
import org.eclipse.mylyn.trac.tests.support.TracFixture;
import org.eclipse.mylyn.trac.tests.support.TracTestCleanupUtil;
import org.eclipse.mylyn.trac.tests.ui.TracHyperlinkUtilTest;
import org.eclipse.mylyn.trac.tests.ui.TracRepositorySettingsPageTest;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class AllTracTests {

	public static Test suite() {
		TestSuite suite = new ManagedTestSuite(AllTracTests.class.getName());
		addTests(suite, TestConfiguration.getDefault());
		return suite;
	}

	public static Test suite(TestConfiguration configuration) {
		TestSuite suite = new TestSuite(AllTracTests.class.getName());
		addTests(suite, configuration);
		return suite;
	}

	public static void addTests(TestSuite suite, TestConfiguration configuration) {
		if (configuration.hasKind(TestKind.INTEGRATION) && CommonTestUtil.getCredentials(PrivilegeLevel.ADMIN) != null) {
			suite.addTestSuite(TracTestCleanupUtil.class);
		}

		suite.addTest(AllTracHeadlessStandaloneTests.suite(configuration));
		suite.addTestSuite(TracUtilTest.class);
		// XXX fails when run from continuous build: suite.addTestSuite(TracTaskEditorTest.class);
		suite.addTestSuite(TracHyperlinkUtilTest.class);

		if (!configuration.isLocalOnly()) {
			suite.addTestSuite(TracRepositoryQueryTest.class);
			suite.addTestSuite(TracRepositorySettingsPageTest.class);
			// network tests
			if (configuration.isDefaultOnly()) {
				addTests(suite, TracFixture.DEFAULT);
			} else {
				for (TracFixture fixture : TracFixture.ALL) {
					addTests(suite, fixture);
				}
			}
		}
	}

	protected static void addTests(TestSuite suite, TracFixture fixture) {
		fixture.createSuite(suite);
		fixture.add(TracRepositoryConnectorTest.class);
		if (fixture.getAccessMode() == Version.XML_RPC) {
			fixture.add(TracTaskDataHandlerXmlRpcTest.class);
			fixture.add(TracAttachmentHandlerTest.class);
		} else {
			fixture.add(TracRepositoryConnectorWebTest.class);
		}
		fixture.done();
	}

}