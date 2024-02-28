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

package org.eclipse.mylyn.wikitext.parser.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.eclipse.mylyn.wikitext.parser.IdGenerator;
import org.eclipse.mylyn.wikitext.parser.markup.IdGenerationStrategy;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("nls")
public class IdGeneratorTest {

	private final IdGenerator generator = new IdGenerator();

	@Before
	public void before() {
		generator.setGenerationStrategy(new IdGenerationStrategy() {

			@Override
			public String generateId(String headingText) {
				return headingText;
			}
		});
	}

	@Test
	public void emptyState() {
		assertFalse(generator.hasAnchorNames());
		assertTrue(generator.getAnchorNames().isEmpty());
	}

	@Test
	public void newId() {
		assertEquals("abc", generator.newId("h1", "abc"));
		assertEquals("abc2", generator.newId("h1", "abc"));
		assertEquals("abc3", generator.newId("h2", "abc"));
		assertEquals("def", generator.newId("h1", "def"));

		assertTrue(generator.hasAnchorNames());
		assertEquals(Set.of("abc", "abc2", "abc3", "def"), generator.getAnchorNames());
	}

	@Test
	public void newIdNull() {
		assertEquals("h1-1", generator.newId("h1", null));
		assertEquals("h1-2", generator.newId("h1", null));
		assertEquals("h2-1", generator.newId("h2", null));
	}
}
