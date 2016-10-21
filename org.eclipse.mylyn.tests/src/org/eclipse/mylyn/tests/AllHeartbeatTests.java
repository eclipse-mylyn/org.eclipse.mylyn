/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tests;

import org.eclipse.mylyn.commons.sdk.util.ManagedTestSuite;
import org.eclipse.mylyn.commons.sdk.util.TestConfiguration;

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
