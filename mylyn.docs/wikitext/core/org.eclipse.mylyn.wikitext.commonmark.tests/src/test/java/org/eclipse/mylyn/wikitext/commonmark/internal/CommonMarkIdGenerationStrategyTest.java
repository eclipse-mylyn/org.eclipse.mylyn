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

@SuppressWarnings("nls")
public class CommonMarkIdGenerationStrategyTest {

	private final CommonMarkIdGenerationStrategy strategy = new CommonMarkIdGenerationStrategy();

	@Test
	public void simple() {
		assertId("abc", "abc");
		assertId("abc123", "abc123");
		assertId("a_bc", "a_bc");
	}

	@Test
	public void mixedCase() {
		assertId("abc", "AbC");
	}

	@Test
	public void whitespace() {
		assertId("a-bc", "a bc");
		assertId("a-bc", "a  \tbc");
		assertId("abc", " abc");
		assertId("abc", "abc ");
	}

	@Test
	public void allWhitespace() {
		assertId("", "   \t");
	}

	@Test
	public void hyphenated() {
		assertId("a-b", "a-b");
		assertId("ab", "-ab");
		assertId("ab", "ab-");
	}

	@Test
	public void punctuationAndSpecialCharacters() {
		assertId("a-b", "a.b");
		assertId("a-b", "a....b");
		assertId("a-b", "a,b");
		assertId("a-b", "a;b");
		assertId("a-b", "a*b");
		assertId("a-b", "a&b");
		assertId("ab", ".ab");
	}

	private void assertId(String expected, String headingText) {
		assertEquals(expected, strategy.generateId(headingText));
	}
}
