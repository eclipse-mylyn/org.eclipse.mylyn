/*******************************************************************************
 * Copyright (c) 2012, 2016 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.gerrit.tests;

import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.commons.sdk.util.TestConfiguration;
import org.eclipse.mylyn.commons.sdk.util.junit4.ManagedSuite;
import org.eclipse.mylyn.commons.sdk.util.junit4.ManagedTestSuite;
import org.eclipse.mylyn.gerrit.tests.core.GerritConnectorTest;
import org.eclipse.mylyn.gerrit.tests.core.GerritSynchronizationTest;
import org.eclipse.mylyn.gerrit.tests.core.client.GerritCapabilitiesTest;
import org.eclipse.mylyn.gerrit.tests.core.client.GerritClientTest;
import org.eclipse.mylyn.gerrit.tests.core.client.GerritVersionTest;
import org.eclipse.mylyn.gerrit.tests.core.client.compat.ChangeDetailXTest;
import org.eclipse.mylyn.gerrit.tests.core.client.compat.PatchScriptXTest;
import org.eclipse.mylyn.gerrit.tests.core.client.rest.AbandonInputTest;
import org.eclipse.mylyn.gerrit.tests.core.client.rest.AccountInfoTest;
import org.eclipse.mylyn.gerrit.tests.core.client.rest.AddReviewerResultTest;
import org.eclipse.mylyn.gerrit.tests.core.client.rest.ChangeInfoTest;
import org.eclipse.mylyn.gerrit.tests.core.client.rest.ProjectInfoTest;
import org.eclipse.mylyn.gerrit.tests.core.client.rest.RestoreInputTest;
import org.eclipse.mylyn.gerrit.tests.core.client.rest.ReviewInfoTest;
import org.eclipse.mylyn.gerrit.tests.core.client.rest.ReviewInputTest;
import org.eclipse.mylyn.gerrit.tests.core.client.rest.ReviewerInfoTest;
import org.eclipse.mylyn.gerrit.tests.core.client.rest.ReviewerInputTest;
import org.eclipse.mylyn.gerrit.tests.core.client.rest.SubmitInfoTest;
import org.eclipse.mylyn.gerrit.tests.core.client.rest.SubmitInputTest;
import org.eclipse.mylyn.gerrit.tests.support.GerritFixture;
import org.eclipse.mylyn.gerrit.tests.ui.GerritUrlHandlerTest;
import org.eclipse.mylyn.internal.gerrit.core.remote.GerritDataLocatorTest;
import org.eclipse.mylyn.internal.gerrit.core.remote.GerritReviewRemoteFactoryJUnit3Test;
import org.eclipse.mylyn.internal.gerrit.core.remote.PatchSetDetailRemoteFactoryTest;
import org.eclipse.mylyn.internal.gerrit.core.remote.PatchSetRemoteFactoryTest;
import org.eclipse.mylyn.internal.gerrit.core.remote.ReviewHarnessTest;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Steffen Pingel
 */
public class AllGerritTests {

	public static Test suite() {
		if (CommonTestUtil.fixProxyConfiguration()) {
			CommonTestUtil.dumpSystemInfo(System.err);
		}
		TestConfiguration testConfiguration = ManagedSuite.getTestConfigurationOrCreateDefault();
		TestSuite suite = new ManagedTestSuite(AllGerritTests.class.getName());
		addTests(suite, testConfiguration);
		return suite;
	}

	public static Test suite(TestConfiguration configuration) {
		TestSuite suite = new TestSuite(AllGerritTests.class.getName());
		addTests(suite, configuration);
		return suite;
	}

	private static void addTests(TestSuite suite, TestConfiguration configuration) {
		suite.addTestSuite(ReviewHarnessTest.class);
		suite.addTestSuite(GerritVersionTest.class);
		suite.addTestSuite(AbandonInputTest.class);
		suite.addTestSuite(AccountInfoTest.class);
		suite.addTestSuite(AddReviewerResultTest.class);
		suite.addTestSuite(ChangeInfoTest.class);
		suite.addTestSuite(ProjectInfoTest.class);
		suite.addTestSuite(RestoreInputTest.class);
		suite.addTestSuite(ReviewerInfoTest.class);
		suite.addTestSuite(ReviewerInputTest.class);
		suite.addTestSuite(ReviewInfoTest.class);
		suite.addTestSuite(ReviewInputTest.class);
		suite.addTestSuite(SubmitInfoTest.class);
		suite.addTestSuite(SubmitInputTest.class);
		suite.addTestSuite(ChangeDetailXTest.class);
		suite.addTestSuite(PatchScriptXTest.class);
		if (!configuration.isLocalOnly()) {
			// network tests
			//FIXME: AF: enable tests
			//https://github.com/eclipse-mylyn/org.eclipse.mylyn.reviews/issues/5
//			suite.addTestSuite(OpenIdAuthenticationTest.class);
//			List<GerritFixture> fixtures = configuration.discover(GerritFixture.class, "gerrit"); //$NON-NLS-1$
//			for (GerritFixture fixture : fixtures) {
//				if (!fixture.isExcluded()) {
//					addTests(suite, fixture);
//				}
//			}
		}
	}

	//FIXME: AF: enable tests
	//https://github.com/eclipse-mylyn/org.eclipse.mylyn.reviews/issues/5
	@SuppressWarnings("unused")
	private static void addTests(TestSuite suite, GerritFixture fixture) {
		fixture.createSuite(suite);
		fixture.add(GerritClientTest.class);
		fixture.add(GerritConnectorTest.class);
		fixture.add(GerritSynchronizationTest.class);
		fixture.add(GerritDataLocatorTest.class);
		fixture.add(GerritReviewRemoteFactoryJUnit3Test.class);
		fixture.add(PatchSetRemoteFactoryTest.class);
		fixture.add(PatchSetDetailRemoteFactoryTest.class);
		fixture.add(GerritUrlHandlerTest.class);
		fixture.add(GerritCapabilitiesTest.class);
		fixture.done();
	}

}
