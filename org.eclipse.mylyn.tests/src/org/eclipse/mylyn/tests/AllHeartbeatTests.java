/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
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
import org.eclipse.mylyn.commons.sdk.util.junit4.ManagedTestSuite;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Steffen Pingel
 */
public class AllHeartbeatTests {

	public static Test suite() {
		TestConfiguration configuration = new TestConfiguration();
		configuration.setLocalOnly(true);

		TestSuite suite = new ManagedTestSuite(AllHeartbeatTests.class.getName());
		AllNonConnectorTests.addTests(suite, configuration);
		return suite;
	}

}
