/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.discovery.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.mylyn.commons.sdk.util.TestConfiguration;
import org.eclipse.mylyn.discovery.tests.core.BundleDiscoveryStrategyTest;
import org.eclipse.mylyn.discovery.tests.core.ConnectorDiscoveryRemoteTest;
import org.eclipse.mylyn.discovery.tests.core.ConnectorDiscoveryTest;
import org.eclipse.mylyn.discovery.tests.core.DirectoryParserTest;
import org.eclipse.mylyn.discovery.tests.core.RemoteBundleDiscoveryStrategyTest;
import org.eclipse.mylyn.discovery.tests.core.util.WebUtilTest;

/**
 * @author Steffen Pingel
 */
public class AllDiscoveryTests {

	public static Test suite() {
		return suite(TestConfiguration.getDefault());
	}

	public static Test suite(TestConfiguration configuration) {
		TestSuite suite = new TestSuite(AllDiscoveryTests.class.getName());
		suite.addTestSuite(ConnectorDiscoveryTest.class);
		suite.addTestSuite(DirectoryParserTest.class);
		suite.addTestSuite(BundleDiscoveryStrategyTest.class);
		suite.addTestSuite(WebUtilTest.class);
		if (!configuration.isLocalOnly()) {
			suite.addTestSuite(RemoteBundleDiscoveryStrategyTest.class);
			suite.addTestSuite(ConnectorDiscoveryRemoteTest.class);
		}
		return suite;
	}

}
