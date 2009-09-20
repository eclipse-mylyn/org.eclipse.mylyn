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

import org.eclipse.mylyn.internal.provisional.commons.ui.CommonUiUtil;

/**
 * @author Steffen Pingel
 */
public class CommonUiUtilTest extends TestCase {

	public void testToLabel() {
		assertNull(CommonUiUtil.toLabel(null));
		assertEquals("", CommonUiUtil.toLabel(""));
		assertEquals(" ", CommonUiUtil.toLabel(" "));
		assertEquals("abc def", CommonUiUtil.toLabel("abc def"));
		assertEquals("a&&b", CommonUiUtil.toLabel("a&b"));
		assertEquals("a&&b&&c", CommonUiUtil.toLabel("a&b&c"));
		assertEquals("&&", CommonUiUtil.toLabel("&"));
		assertEquals("&&&&", CommonUiUtil.toLabel("&&"));
	}

}
