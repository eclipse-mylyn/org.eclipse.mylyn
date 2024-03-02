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

package org.eclipse.mylyn.wikitext.core.parser.markup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.junit.Test;

@SuppressWarnings({ "nls", "restriction" })
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

	private final MarkupLanguage markupLanguage = new TestMarkupLanguage();

	@Test
	public void getFileExtensionsDefault() {
		assertEquals(new HashSet<>(Arrays.asList(markupLanguage.getName())), markupLanguage.getFileExtensions());
	}

	@Test
	public void setFileExtensionsNull() {
		NullPointerException exception = assertThrows(NullPointerException.class,
				() -> markupLanguage.setFileExtensions(null));
		assertEquals("Must specify file extensions", exception.getMessage());
	}

	@Test
	public void setFileExtensionsEmpty() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> markupLanguage.setFileExtensions(Collections.<String> emptySet()));
		assertEquals("File extensions must not be empty", exception.getMessage());
	}

	@Test
	public void getFileExtensionsSpecified() {
		markupLanguage.setFileExtensions(new HashSet<>(Arrays.asList(markupLanguage.getName(), "123")));
		assertEquals(new HashSet<>(Arrays.asList(markupLanguage.getName(), "123")), markupLanguage.getFileExtensions());
	}

	@Test
	public void documentBuilderUnsupported() {
		assertThrows(UnsupportedOperationException.class,
				() -> markupLanguage.createDocumentBuilder(new StringWriter()));
	}
}
