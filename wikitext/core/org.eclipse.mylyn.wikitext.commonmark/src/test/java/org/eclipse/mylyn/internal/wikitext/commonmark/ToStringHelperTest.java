/*******************************************************************************
 * Copyright (c) 2015 David Green.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.commonmark;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.base.Strings;

public class ToStringHelperTest {
	@Test
	public void toStringValue() {
		assertEquals(null, ToStringHelper.toStringValue(null));
		assertEquals("", ToStringHelper.toStringValue(""));
		assertEquals("abc", ToStringHelper.toStringValue("abc"));
		assertEquals("01234567890123456789...",
				ToStringHelper.toStringValue(Strings.repeat("0123456789", 10)));
		assertEquals("a\\r\\n\\tb", ToStringHelper.toStringValue("a\r\n\tb"));
	}
}
