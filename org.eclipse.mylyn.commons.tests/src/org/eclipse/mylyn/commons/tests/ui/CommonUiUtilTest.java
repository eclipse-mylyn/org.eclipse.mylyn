/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.commons.tests.ui;

import junit.framework.TestCase;

import org.eclipse.jface.action.LegacyActionTools;

/**
 * @author Steffen Pingel
 */
public class CommonUiUtilTest extends TestCase {

	public void testToLabel() {
		assertNull(LegacyActionTools.escapeMnemonics(null));
		assertEquals("", LegacyActionTools.escapeMnemonics(""));
		assertEquals(" ", LegacyActionTools.escapeMnemonics(" "));
		assertEquals("abc def", LegacyActionTools.escapeMnemonics("abc def"));
		assertEquals("a&&b", LegacyActionTools.escapeMnemonics("a&b"));
		assertEquals("a&&b&&c", LegacyActionTools.escapeMnemonics("a&b&c"));
		assertEquals("&&", LegacyActionTools.escapeMnemonics("&"));
		assertEquals("&&&&", LegacyActionTools.escapeMnemonics("&&"));
	}

}
