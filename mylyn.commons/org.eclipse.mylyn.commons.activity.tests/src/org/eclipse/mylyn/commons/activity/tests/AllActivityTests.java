/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.activity.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Steffen Pingel
 */
public class AllActivityTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllActivityTests.class.getName());
		suite.addTestSuite(MonitorUserActivityJobTest.class);
		suite.addTestSuite(UserActivityManagerTest.class);
		return suite;
	}

}
