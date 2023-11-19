/*******************************************************************************
 * Copyright (c) 2007, 2010, 2015 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.mediawiki.internal;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class MediaWikiIdGenerationStrategyTest {

	MediaWikiIdGenerationStrategy generationStrategy;

	@Before
	public void setUp() throws Exception {
		generationStrategy = new MediaWikiIdGenerationStrategy();
	}

	@Test
	public void testSimple() {
		assertEquals("Bugzilla_Connector", generationStrategy.generateId("Bugzilla Connector"));
		assertEquals("JIRA_Connector", generationStrategy.generateId("JIRA Connector"));
		assertEquals("Keyboard_mappings_on_Linux", generationStrategy.generateId("Keyboard mappings on Linux"));
		assertEquals("Alt.2BClick_navigation", generationStrategy.generateId("Alt+Click navigation"));
	}

	@Test
	public void testWithDots() {
		assertEquals("com.foo.Bar", generationStrategy.generateId("com.foo.Bar"));
	}

	@Test
	public void testHeadingTextToId() {
		//Bug 388657
		assertEquals("Anchor_Text.3F", generationStrategy.generateId("Anchor Text?"));
		assertEquals("This.2FSection", generationStrategy.generateId("This/Section"));
		assertEquals("C.23_Implementation", generationStrategy.generateId("C# Implementation"));
	}
}
