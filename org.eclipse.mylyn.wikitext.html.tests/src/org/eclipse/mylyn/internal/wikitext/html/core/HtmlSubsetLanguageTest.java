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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class HtmlSubsetLanguageTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void createNullName() {
		thrown.expect(NullPointerException.class);
		new HtmlSubsetLanguage(null);
	}

	@Test
	public void create() {
		HtmlSubsetLanguage language = new HtmlSubsetLanguage("Test");
		assertEquals("Test", language.getName());
	}

	@Test
	public void createDocumentBuilder() {
		DocumentBuilder builder = new HtmlSubsetLanguage("Test").createDocumentBuilder(new StringWriter(), false);
		assertNotNull(builder);
		assertTrue(builder instanceof HtmlSubsetDocumentBuilder);
	}
}
