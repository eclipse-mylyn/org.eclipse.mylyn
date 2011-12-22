/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.tests.core;

import junit.framework.TestCase;

import org.eclipse.mylyn.commons.core.CoreUtil;

/**
 * @author Steffen Pingel
 */
public class CoreUtilTest extends TestCase {

	public void testCompareNullNull() {
		assertEquals(0, CoreUtil.compare(null, null));
	}

	public void testCompareNullString() {
		assertEquals(1, CoreUtil.compare(null, "abc"));
	}

	public void testCompareNumbersBigger() {
		assertEquals(1, CoreUtil.compare(4, 2));
	}

	public void testCompareNumbersEquals() {
		assertEquals(0, CoreUtil.compare(-4, -4));
	}

	public void testCompareNumbersNull() {
		assertEquals(1, CoreUtil.compare(null, 2));
	}

	public void testCompareNumbersSmaller() {
		assertEquals(-1, CoreUtil.compare(1, 2));
	}

	public void testCompareStringNull() {
		assertEquals(-1, CoreUtil.compare("abc", null));
	}

	public void testPropertyEquallsNullFalse() {
		assertFalse(CoreUtil.propertyEquals(false, null));
	}

	public void testPropertyEquallsNullTrue() {
		assertTrue(CoreUtil.propertyEquals(true, null));
	}

	public void testPropertyEqualsBooleanTrue() {
		assertTrue(CoreUtil.propertyEquals(true, Boolean.TRUE));
	}

	public void testPropertyEqualsNumbe() {
		assertFalse(CoreUtil.propertyEquals(true, 1));
	}

	public void testPropertyEqualsStringFalse() {
		assertFalse(CoreUtil.propertyEquals(false, "false"));
	}

	public void testPropertyEqualsStringFalseUnexpected() {
		assertFalse(CoreUtil.propertyEquals(true, "false"));
	}

	public void testPropertyEqualsStringTrue() {
		assertFalse(CoreUtil.propertyEquals(true, "true"));
	}

}
