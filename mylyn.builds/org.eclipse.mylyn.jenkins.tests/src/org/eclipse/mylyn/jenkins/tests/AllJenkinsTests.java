/*******************************************************************************
 * Copyright (c) 2010, 2015 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.jenkins.tests;

import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.jenkins.tests.client.JenkinsClientTest;
import org.eclipse.mylyn.jenkins.tests.client.JenkinsUrlTest;
import org.eclipse.mylyn.jenkins.tests.client.JenkinsUrlUtilTest;
import org.eclipse.mylyn.jenkins.tests.client.JenkinsValidationTest;
import org.eclipse.mylyn.jenkins.tests.core.JenkinsConnectorTest;
import org.eclipse.mylyn.jenkins.tests.core.JenkinsServerBehaviourTest;
import org.eclipse.mylyn.jenkins.tests.integration.JenkinsIntegrationTest;
import org.junit.platform.suite.api.BeforeSuite;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * @author Steffen Pingel
 */
@Suite
@SelectClasses({ JenkinsConnectorTest.class, JenkinsServerBehaviourTest.class, JenkinsUrlTest.class,
	JenkinsValidationTest.class,
	//Jenkins fixure
	JenkinsUrlUtilTest.class, JenkinsClientTest.class, JenkinsIntegrationTest.class })
public class AllJenkinsTests {

	@BeforeSuite
	static void suiteSetup() {
		if (CommonTestUtil.fixProxyConfiguration()) {
			CommonTestUtil.dumpSystemInfo(System.err);
		}
	}
}
