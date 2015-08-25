/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.notifications.tests.feed;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.commons.notifications.feed.ServiceMessage;

public class ServiceMessageTest extends TestCase {

	public void testServiceMessage() {
		assertEquals("0", new ServiceMessage("2763").getId());
	}

}
