/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.notifications.tests.feed;

import org.eclipse.mylyn.internal.commons.notifications.feed.ServiceMessage;

import junit.framework.TestCase;

public class ServiceMessageTest extends TestCase {

	public void testServiceMessage() {
		assertEquals("0", new ServiceMessage("2763").getId());
	}

}
