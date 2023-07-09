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

package org.eclipse.mylyn.hudson.tests;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.commons.sdk.util.ManagedSuite;
import org.eclipse.mylyn.commons.sdk.util.ManagedTestSuite;
import org.eclipse.mylyn.commons.sdk.util.TestConfiguration;
import org.eclipse.mylyn.hudson.tests.client.HudsonClientTest;
import org.eclipse.mylyn.hudson.tests.client.HudsonUrlTest;
import org.eclipse.mylyn.hudson.tests.client.HudsonValidationTest;
import org.eclipse.mylyn.hudson.tests.core.HudsonConnectorTest;
import org.eclipse.mylyn.hudson.tests.core.HudsonServerBehaviourTest;
import org.eclipse.mylyn.hudson.tests.integration.DockerIntegrationTest;
import org.eclipse.mylyn.hudson.tests.integration.HudsonIntegrationTest;
import org.eclipse.mylyn.hudson.tests.support.HudsonFixture;
import org.eclipse.mylyn.internal.hudson.core.HudsonCorePlugin;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Steffen Pingel
 */
public class AllHudsonTests {

	public static Test suite() {
		if (CommonTestUtil.fixProxyConfiguration()) {
			CommonTestUtil.dumpSystemInfo(System.err);
		}
		TestConfiguration testConfiguration = ManagedSuite.getTestConfigurationOrCreateDefault();
		TestSuite suite = new ManagedTestSuite(AllHudsonTests.class.getName());
		addTests(suite, testConfiguration);
		return suite;
	}

	public static Test suite(TestConfiguration configuration) {
		TestSuite suite = new TestSuite(AllHudsonTests.class.getName());
		addTests(suite, configuration);
		return suite;
	}

	private static void addTests(TestSuite suite, TestConfiguration configuration) {
		suite.addTestSuite(HudsonConnectorTest.class);
		suite.addTestSuite(HudsonServerBehaviourTest.class);
		suite.addTestSuite(HudsonUrlTest.class);
		if (!configuration.isLocalOnly()) {
			// network tests
			suite.addTestSuite(HudsonValidationTest.class);
			List<HudsonFixture> fixtures;
			try {
				fixtures = configuration.discover(HudsonFixture.class, "hudson");
			} catch (RuntimeException e) {
				StatusHandler.log(new Status(IStatus.ERROR, HudsonCorePlugin.ID_PLUGIN,
						"No hudson fixtures found, will look for jenkins fixtures", e));
				fixtures = new ArrayList<HudsonFixture>();
			}
			fixtures.addAll(configuration.discover(HudsonFixture.class, "jenkins"));
			for (HudsonFixture fixture : fixtures) {
				if (fixture.isExcluded()
						|| (fixture.isUseCertificateAuthentication() && CommonTestUtil.isCertificateAuthBroken())) {
					continue;
				}
				fixture.createSuite(suite);
				fixture.add(HudsonClientTest.class);
				if (!fixture.isUseCertificateAuthentication()) {
					fixture.add(HudsonIntegrationTest.class);
					fixture.add(DockerIntegrationTest.class);
				}
				fixture.done();
			}
		}
	}

}
