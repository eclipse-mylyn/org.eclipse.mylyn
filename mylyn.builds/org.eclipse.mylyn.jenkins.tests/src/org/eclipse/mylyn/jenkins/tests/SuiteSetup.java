/*******************************************************************************
 * Copyright (c) 2024 George Lindholm
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html.
 *
 * Contributors:
 *      See git history
 *******************************************************************************/

package org.eclipse.mylyn.jenkins.tests;

import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.commons.sdk.util.ManagedSuite;
import org.eclipse.mylyn.commons.sdk.util.TestConfiguration;
import org.eclipse.mylyn.jenkins.tests.support.JenkinsFixture;
import org.junit.jupiter.api.Test;

public class SuiteSetup {
	private static boolean notLocalOnly = false;

	private static boolean useCertificateAuthentication = false;

	private static boolean fixtureActive = false;

	static {
		if (CommonTestUtil.fixProxyConfiguration()) {
			CommonTestUtil.dumpSystemInfo(System.err);
		}
		TestConfiguration configuration = ManagedSuite.getTestConfigurationOrCreateDefault();

		if (!configuration.isLocalOnly()) {
			// network tests
			notLocalOnly = true;
			for (JenkinsFixture fixture : configuration.discover(JenkinsFixture.class, "jenkins")) { //$NON-NLS-1$
				if (fixture.isExcluded()
						|| fixture.isUseCertificateAuthentication() && CommonTestUtil.isCertificateAuthBroken()) {
					continue;
				}
				fixtureActive = true;
				useCertificateAuthentication = !fixture.isUseCertificateAuthentication();
			}
		}
	}

	@Test
	public void dummyTest() {
		// This is a dummy test to ensure the setup method is run
	}

	public static boolean isNotLocalOnly() {
		return notLocalOnly;
	}

	public static boolean isUseCertificateAuthentication() {
		return useCertificateAuthentication;
	}

	public static boolean isFixtureActive() {
		return fixtureActive;
	}

}
