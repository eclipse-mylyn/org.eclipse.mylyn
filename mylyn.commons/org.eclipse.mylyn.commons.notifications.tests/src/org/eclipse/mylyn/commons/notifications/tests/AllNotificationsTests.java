/*******************************************************************************
 * Copyright (c) 2011, 2015 Tasktop Technologies.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.commons.notifications.tests;

import org.eclipse.mylyn.commons.notifications.tests.core.NotificationEnvironmentTest;
import org.eclipse.mylyn.commons.notifications.tests.feed.FeedReaderTest;
import org.eclipse.mylyn.commons.notifications.tests.feed.ServiceMessageManagerTest;
import org.eclipse.mylyn.commons.notifications.tests.feed.ServiceMessageTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author Steffen Pingel
 */
@RunWith(Suite.class)
@SuiteClasses({ NotificationEnvironmentTest.class, FeedReaderTest.class, ServiceMessageManagerTest.class,
	ServiceMessageTest.class })
public class AllNotificationsTests {

}
