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
import org.eclipse.mylyn.gerrit.tests.core.GerritConnectorTest;
import org.eclipse.mylyn.gerrit.tests.core.GerritProjectTest;
import org.eclipse.mylyn.gerrit.tests.core.GerritSynchronizationTest;
import org.eclipse.mylyn.gerrit.tests.core.client.GerritCapabilitiesTest;
import org.eclipse.mylyn.gerrit.tests.core.client.GerritClientTest;
import org.eclipse.mylyn.gerrit.tests.core.client.GerritVersionTest;
import org.eclipse.mylyn.gerrit.tests.core.client.OpenIdAuthenticationTest;
import org.eclipse.mylyn.gerrit.tests.core.client.compat.ChangeDetailXTest;
import org.eclipse.mylyn.gerrit.tests.core.client.compat.PatchScriptXTest;
import org.eclipse.mylyn.gerrit.tests.core.client.rest.AbandonInputTest;
import org.eclipse.mylyn.gerrit.tests.core.client.rest.AccountInfoTest;
import org.eclipse.mylyn.gerrit.tests.core.client.rest.AddReviewerResultTest;
import org.eclipse.mylyn.gerrit.tests.core.client.rest.ChangeInfoTest;
import org.eclipse.mylyn.gerrit.tests.core.client.rest.CommentInputTest;
import org.eclipse.mylyn.gerrit.tests.core.client.rest.ProjectInfoTest;
import org.eclipse.mylyn.gerrit.tests.core.client.rest.RestoreInputTest;
import org.eclipse.mylyn.gerrit.tests.core.client.rest.ReviewInfoTest;
import org.eclipse.mylyn.gerrit.tests.core.client.rest.ReviewInputTest;
import org.eclipse.mylyn.gerrit.tests.core.client.rest.ReviewerInfoTest;
import org.eclipse.mylyn.gerrit.tests.core.client.rest.ReviewerInputTest;
import org.eclipse.mylyn.gerrit.tests.core.client.rest.SubmitInfoTest;
import org.eclipse.mylyn.gerrit.tests.core.client.rest.SubmitInputTest;
import org.eclipse.mylyn.gerrit.tests.ui.GerritUrlHandlerTest;
import org.eclipse.mylyn.internal.gerrit.core.remote.GerritDataLocatorTest;
import org.eclipse.mylyn.internal.gerrit.core.remote.GerritReviewRemoteFactoryJUnit3Test;
import org.eclipse.mylyn.internal.gerrit.core.remote.PatchSetDetailRemoteFactoryTest;
import org.eclipse.mylyn.internal.gerrit.core.remote.PatchSetRemoteFactoryTest;
import org.eclipse.mylyn.internal.gerrit.core.remote.ReviewHarnessTest;
import org.junit.platform.suite.api.BeforeSuite;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * @author Steffen Pingel
 */
@Suite
@SelectClasses({ AbandonInputTest.class, AccountInfoTest.class, AddReviewerResultTest.class, ChangeDetailXTest.class,
	ChangeInfoTest.class, CommentInputTest.class, GerritVersionTest.class, PatchScriptXTest.class,
	ProjectInfoTest.class, RestoreInputTest.class, ReviewerInfoTest.class, ReviewerInputTest.class,
	ReviewHarnessTest.class, ReviewInfoTest.class, ReviewInputTest.class, SubmitInfoTest.class,
	SubmitInputTest.class,

	//FIXME: AF: enable tests
	//https://github.com/eclipse-mylyn/org.eclipse.mylyn.reviews/issues/5

	GerritCapabilitiesTest.class, GerritClientTest.class, GerritConnectorTest.class, GerritDataLocatorTest.class,
	GerritProjectTest.class, GerritReviewRemoteFactoryJUnit3Test.class, GerritSynchronizationTest.class,
	GerritUrlHandlerTest.class, PatchSetDetailRemoteFactoryTest.class, PatchSetRemoteFactoryTest.class,
	OpenIdAuthenticationTest.class })

public class AllGerritTests {

	@BeforeSuite
	static void suiteSetup() {
		if (CommonTestUtil.fixProxyConfiguration()) {
			CommonTestUtil.dumpSystemInfo(System.err);
		}

	}
}
