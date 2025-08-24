/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tests;

import org.eclipse.mylyn.commons.sdk.util.TestConfiguration;
import org.eclipse.mylyn.commons.sdk.util.junit4.ManagedSuite;
import org.eclipse.mylyn.commons.sdk.util.junit4.ManagedTestSuite;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Mik Kersten
 */
public class AllTests {

	public static Test suite() {
		TestConfiguration configuration = ManagedSuite.getTestConfiguration();
		if (configuration == null) {
			configuration = new TestConfiguration();
			ManagedSuite.setTestConfiguration(configuration);
		}

		TestSuite suite = new ManagedTestSuite(AllTests.class.getName());
		AllNonConnectorTests.addTests(suite, configuration);
		suite.addTest(new JUnit4TestAdapter(AllConnectorTests.class));
		return suite;
	}

}
