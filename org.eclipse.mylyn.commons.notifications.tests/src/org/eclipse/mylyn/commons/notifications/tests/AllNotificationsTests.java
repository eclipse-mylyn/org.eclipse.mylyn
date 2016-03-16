/*******************************************************************************
 * Copyright (c) 2011, 2015 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.notifications.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.mylyn.commons.notifications.tests.core.NotificationEnvironmentTest;
import org.eclipse.mylyn.commons.notifications.tests.feed.FeedReaderTest;
import org.eclipse.mylyn.commons.notifications.tests.feed.ServiceMessageManagerTest;
import org.eclipse.mylyn.commons.notifications.tests.feed.ServiceMessageTest;

/**
 * @author Steffen Pingel
 */
public class AllNotificationsTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllNotificationsTests.class.getName());
		suite.addTestSuite(NotificationEnvironmentTest.class);
		suite.addTestSuite(FeedReaderTest.class);
		suite.addTestSuite(ServiceMessageManagerTest.class);
		suite.addTestSuite(ServiceMessageTest.class);
		return suite;
	}

}
