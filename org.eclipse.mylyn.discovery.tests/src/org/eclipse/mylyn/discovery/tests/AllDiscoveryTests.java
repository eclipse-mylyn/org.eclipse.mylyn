/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
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

import org.eclipse.mylyn.discovery.tests.core.BundleDiscoveryStrategyTest;
import org.eclipse.mylyn.discovery.tests.core.ConnectorDiscoveryTest;
import org.eclipse.mylyn.discovery.tests.core.DirectoryParserTest;
import org.eclipse.mylyn.discovery.tests.core.RemoteBundleDiscoveryStrategyTest;

/**
 * @author Steffen Pingel
 */
public class AllDiscoveryTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Tests for org.eclipse.mylyn.discovery");
		suite.addTestSuite(ConnectorDiscoveryTest.class);
		suite.addTestSuite(DirectoryParserTest.class);
		suite.addTestSuite(BundleDiscoveryStrategyTest.class);
		suite.addTestSuite(RemoteBundleDiscoveryStrategyTest.class);
		return suite;
	}

}
