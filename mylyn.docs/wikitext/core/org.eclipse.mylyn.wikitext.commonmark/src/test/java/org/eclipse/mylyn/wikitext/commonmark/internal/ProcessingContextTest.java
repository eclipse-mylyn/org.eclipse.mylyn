/*******************************************************************************
 * Copyright (c) 2015 David Green.
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

package org.eclipse.mylyn.wikitext.commonmark.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.mylyn.wikitext.commonmark.internal.ProcessingContext.NamedUriWithTitle;
import org.eclipse.mylyn.wikitext.commonmark.internal.inlines.InlineParser;
import org.eclipse.mylyn.wikitext.commonmark.internal.inlines.SourceSpan;
import org.junit.Test;

public class ProcessingContextTest {

	@Test
	public void empty() {
		ProcessingContext context = ProcessingContext.builder().build();
		assertNotNull(context);
		assertTrue(context.isEmpty());
	}

	@Test
	public void referenceDefinition() {
		ProcessingContext context = ProcessingContext.builder().referenceDefinition("onE", "/uri", "a title").build();
		assertNotNull(context);
		assertFalse(context.isEmpty());
		assertNotNull(context.namedUriWithTitle("one"));
		assertNotNull(context.namedUriWithTitle("One"));
		NamedUriWithTitle link = context.namedUriWithTitle("ONE");
		assertEquals("onE", link.getName());
		assertEquals("/uri", link.getUri());
		assertEquals("a title", link.getTitle());
		assertNull(context.namedUriWithTitle("Unknown"));
	}

	@Test
	public void referenceDefinitionEmptyName() {
		assertTrue(ProcessingContext.builder().referenceDefinition("", "one", "two").build().isEmpty());
	}

	@Test
	public void referenceDefinitionDuplicate() {
		ProcessingContext context = ProcessingContext.builder()
				.referenceDefinition("a", "/uri", "a title")
				.referenceDefinition("a", "/uri2", "a title2")
				.build();
		NamedUriWithTitle uriWithTitle = context.namedUriWithTitle("a");
		assertEquals("/uri", uriWithTitle.getUri());
	}

	@Test
	public void generateHeadingId() {
		ProcessingContext processingContext = ProcessingContext.builder().build();
		assertEquals("a", processingContext.generateHeadingId(1, "a"));
		assertEquals("a2", processingContext.generateHeadingId(1, "a"));
		assertEquals("a3", processingContext.generateHeadingId(2, "a"));
		assertEquals("h1-3", processingContext.generateHeadingId(1, null));
		assertEquals("h1-4", processingContext.generateHeadingId(1, ""));
	}

	@Test
	public void inlineParser() {
		InlineParser inlineParser = new InlineParser(List.<SourceSpan> of());
		ProcessingContext context = ProcessingContext.builder().inlineParser(inlineParser).build();
		assertSame(inlineParser, context.getInlineParser());
		assertNotNull(ProcessingContext.builder().build().getInlineParser());
	}
}
