/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.html.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class HtmlSubsetLanguageTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void createNullName() {
		thrown.expect(NullPointerException.class);
		new HtmlSubsetLanguage(null, ImmutableSet.of(BlockType.PARAGRAPH));
	}

	@Test
	public void createNullBlockTypes() {
		thrown.expect(NullPointerException.class);
		new HtmlSubsetLanguage("Test", null);
	}

	@Test
	public void create() {
		HtmlSubsetLanguage language = newHtmlSubsetLanguage();
		assertEquals("Test", language.getName());
	}

	@Test
	public void supportedBlockTypes() {
		assertEquals(Sets.newHashSet(BlockType.PARAGRAPH, BlockType.CODE),
				newHtmlSubsetLanguage(BlockType.PARAGRAPH, BlockType.CODE).getSupportedBlockTypes());
	}

	protected HtmlSubsetLanguage newHtmlSubsetLanguage(BlockType... blocks) {
		return new HtmlSubsetLanguage("Test", Sets.newHashSet(blocks));
	}

	@Test
	public void createDocumentBuilder() {
		DocumentBuilder builder = newHtmlSubsetLanguage().createDocumentBuilder(new StringWriter(), false);
		assertNotNull(builder);
		assertTrue(builder instanceof HtmlSubsetDocumentBuilder);
	}
}
