/*******************************************************************************
 * Copyright (c) 2007, 2013 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.core.parser.markup;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;
import java.util.Collections;

import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.collect.Sets;

public class MarkupLanguageTest {

	private final class TestMarkupLanguage extends MarkupLanguage {

		public TestMarkupLanguage() {
			setName("TestLanguage");
		}

		@Override
		public void processContent(MarkupParser parser, String markupContent, boolean asDocument) {
			throw new UnsupportedOperationException();
		}

	}

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private final MarkupLanguage markupLanguage = new TestMarkupLanguage();

	@Test
	public void getFileExtensionsDefault() {
		assertEquals(Sets.newHashSet(markupLanguage.getName()), markupLanguage.getFileExtensions());
	}

	@Test
	public void setFileExtensionsNull() {
		thrown.expect(NullPointerException.class);
		thrown.expectMessage("Must specify file extensions");
		markupLanguage.setFileExtensions(null);
	}

	@Test
	public void setFileExtensionsEmpty() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("File extensions must not be empty");
		markupLanguage.setFileExtensions(Collections.<String> emptySet());
	}

	@Test
	public void getFileExtensionsSpecified() {
		markupLanguage.setFileExtensions(Sets.newHashSet(markupLanguage.getName(), "123"));
		assertEquals(Sets.newHashSet(markupLanguage.getName(), "123"), markupLanguage.getFileExtensions());
	}

	@Test
	public void documentBuilderUnsupported() {
		thrown.expect(UnsupportedOperationException.class);
		markupLanguage.createDocumentBuilder(new StringWriter());
	}
}
