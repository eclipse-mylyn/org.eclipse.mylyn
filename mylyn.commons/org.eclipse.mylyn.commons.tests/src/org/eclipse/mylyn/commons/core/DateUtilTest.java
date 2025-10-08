/*******************************************************************************
 * Copyright (c) 2010, 2024 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.commons.core;

import junit.framework.TestCase;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class DateUtilTest extends TestCase {

	public void testGetRelativeDuration() {
		assertEquals("1 sec", DateUtil.getRelativeDuration(1000));
		assertEquals("2 secs", DateUtil.getRelativeDuration(2500));
		assertEquals("1 min 6 secs", DateUtil.getRelativeDuration(66000));
		assertEquals("1 day", DateUtil.getRelativeDuration(86400000));
	}

	public void testGetRelativeDurationThreeSegments() {
		// 1 day 1 min 6 secs
		assertEquals("1 day", DateUtil.getRelativeDuration(86466000));
		// 1 day 1 sec
		assertEquals("1 day", DateUtil.getRelativeDuration(86401000));
		// 1 day 2 hours 6 secs
		assertEquals("1 day 2 hrs", DateUtil.getRelativeDuration(93606000));
	}

	public void testGetRelativeDurationNegative() {
		assertEquals("", DateUtil.getRelativeDuration(0));
		assertEquals("", DateUtil.getRelativeDuration(500));
		assertEquals("", DateUtil.getRelativeDuration(-1));
	}

}
