/*******************************************************************************
 * Copyright (c) 2015, 2024 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.commonmark.internal;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.base.Strings;

@SuppressWarnings("nls")
public class ToStringHelperTest {
	@Test
	public void toStringValue() {
		assertEquals(null, ToStringHelper.toStringValue(null));
		assertEquals("", ToStringHelper.toStringValue(""));
		assertEquals("abc", ToStringHelper.toStringValue("abc"));
		assertEquals("01234567890123456789...", ToStringHelper.toStringValue(Strings.repeat("0123456789", 10)));
		assertEquals("a\\r\\n\\tb", ToStringHelper.toStringValue("a\r\n\tb"));
	}
}
