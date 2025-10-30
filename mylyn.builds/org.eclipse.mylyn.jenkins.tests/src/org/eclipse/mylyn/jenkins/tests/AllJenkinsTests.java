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
import org.eclipse.mylyn.commons.sdk.util.TestConfiguration;
import org.eclipse.mylyn.commons.sdk.util.junit4.ManagedSuite;
import org.eclipse.mylyn.commons.sdk.util.junit4.ManagedTestSuite;
import org.eclipse.mylyn.jenkins.tests.core.JenkinsConnectorTest;
import org.eclipse.mylyn.jenkins.tests.core.JenkinsServerBehaviourTest;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Steffen Pingel
 */
public class AllJenkinsTests {

	public static Test suite() {
		if (CommonTestUtil.fixProxyConfiguration()) {
			CommonTestUtil.dumpSystemInfo(System.err);
		}
		TestConfiguration testConfiguration = ManagedSuite.getTestConfigurationOrCreateDefault();
		testConfiguration.setLocalOnly(CommonTestUtil.runNonCIServerTestsOnly());
		TestSuite suite = new ManagedTestSuite(AllJenkinsTests.class.getName());
		addTests(suite, testConfiguration);
		return suite;
	}

	public static Test suite(TestConfiguration configuration) {
		TestSuite suite = new TestSuite(AllJenkinsTests.class.getName());
		addTests(suite, configuration);
		return suite;
	}

	private static void addTests(TestSuite suite, TestConfiguration configuration) {
		suite.addTestSuite(JenkinsConnectorTest.class);
		suite.addTestSuite(JenkinsServerBehaviourTest.class);
		//FIXME: see https://github.com/eclipse-mylyn/org.eclipse.mylyn/issues/936
/*
		suite.addTestSuite(JenkinsUrlTest.class);

		if (!configuration.isLocalOnly()) {
			// network tests
			suite.addTestSuite(JenkinsValidationTest.class);
			List<JenkinsFixture> fixtures = configuration.discover(JenkinsFixture.class, "jenkins");
			for (JenkinsFixture fixture : fixtures) {
				if (fixture.isExcluded()
						|| fixture.isUseCertificateAuthentication() && CommonTestUtil.isCertificateAuthBroken()) {
					continue;
				}
				fixture.createSuite(suite);
				fixture.add(JenkinsClientTest.class);
				if (!fixture.isUseCertificateAuthentication()) {
					fixture.add(JenkinsIntegrationTest.class);
				}
				fixture.done();
			}
		}
 */
	}

}
