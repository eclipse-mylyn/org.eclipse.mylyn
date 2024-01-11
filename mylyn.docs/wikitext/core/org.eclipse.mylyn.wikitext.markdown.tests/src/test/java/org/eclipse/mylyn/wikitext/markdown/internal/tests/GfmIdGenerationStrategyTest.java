/*******************************************************************************
 * Copyright (c) 2007, 2024 David Green and others.
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

package org.eclipse.mylyn.wikitext.markdown.internal.tests;

import static org.junit.Assert.assertEquals;

import org.eclipse.mylyn.wikitext.markdown.internal.GfmIdGenerationStrategy;
import org.junit.Test;

public class GfmIdGenerationStrategyTest {

	private final GfmIdGenerationStrategy strategy = new GfmIdGenerationStrategy();

	@Test
	public void withNonAlphaNumeric() {
		assertEquals("one-two", strategy.generateId("%One & Two!"));
	}

	@Test
	public void withMultipleConsecutiveSpaces() {
		assertEquals("with-spaces", strategy.generateId("with  spaces"));
	}

	@Test
	public void withLeadingSpaces() {
		assertEquals("withleadingspaces", strategy.generateId("  withleadingspaces"));
	}

	@Test
	public void withUnderscore() {
		assertEquals("with_underscore", strategy.generateId("with_underscore"));
	}

	@Test
	public void withHyphen() {
		assertEquals("with-hyphen", strategy.generateId("with-hyphen"));
	}

	@Test
	public void alphaNumeric() {
		assertEquals("alpha01234numeric", strategy.generateId("alpha01234numeric"));
	}

	@Test
	public void mixedCase() {
		assertEquals("mixedcase", strategy.generateId("MixedCase"));
	}

	@Test
	public void allWhitespace() {
		assertEquals("", strategy.generateId("  \t "));
	}

}
