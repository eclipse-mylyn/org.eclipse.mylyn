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

import org.eclipse.mylyn.internal.trac.core.client.ITracClient.Version;
import org.eclipse.mylyn.trac.tests.core.TracAttachmentHandlerTest;
import org.eclipse.mylyn.trac.tests.core.TracRepositoryConnectorTest;
import org.eclipse.mylyn.trac.tests.core.TracRepositoryConnectorWebTest;
import org.eclipse.mylyn.trac.tests.core.TracRepositoryQueryTest;
import org.eclipse.mylyn.trac.tests.core.TracTaskDataHandlerXmlRpcTest;
import org.eclipse.mylyn.trac.tests.core.TracUtilTest;
import org.eclipse.mylyn.trac.tests.support.TracFixture;
import org.eclipse.mylyn.trac.tests.ui.TracHyperlinkUtilTest;
import org.eclipse.mylyn.trac.tests.ui.TracRepositorySettingsPageTest;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class AllTracTests {

	public static Test suite() {
		return suite(false);
	}

	public static Test suite(boolean defaultOnly) {
		TestSuite suite = new TestSuite("Tests for org.eclipse.mylyn.trac.tests");
		suite.addTest(AllTracHeadlessStandaloneTests.suite(defaultOnly));
		suite.addTestSuite(TracUtilTest.class);
		suite.addTestSuite(TracRepositoryQueryTest.class);
		// XXX fails when run from continuous build: suite.addTestSuite(TracTaskEditorTest.class);
		suite.addTestSuite(TracRepositorySettingsPageTest.class);
		suite.addTestSuite(TracHyperlinkUtilTest.class);
		// network tests
		if (defaultOnly) {
			addTests(suite, TracFixture.DEFAULT);
		} else {
			for (TracFixture fixture : TracFixture.ALL) {
				addTests(suite, fixture);
			}
		}
		return suite;
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