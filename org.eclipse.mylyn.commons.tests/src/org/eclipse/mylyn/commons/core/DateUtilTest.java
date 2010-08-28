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

package org.eclipse.mylyn.commons.core;

import junit.framework.TestCase;

/**
 * @author Steffen Pingel
 */
public class DateUtilTest extends TestCase {

	public void testGetFormattedDifference() {
		assertEquals("1 sec", DateUtil.getRelativeDuration(1000));
		assertEquals("2 secs", DateUtil.getRelativeDuration(2500));
		assertEquals("1 min 6 secs", DateUtil.getRelativeDuration(66000));
	}

}
