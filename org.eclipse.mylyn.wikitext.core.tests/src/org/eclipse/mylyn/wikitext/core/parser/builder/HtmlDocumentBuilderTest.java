/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.core.parser.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.Resources;

public class HtmlDocumentBuilderTest {
	private StringWriter out;

	private HtmlDocumentBuilder builder;

	@Before
	public void setup() {
		out = new StringWriter();
		builder = new HtmlDocumentBuilder(out);
	}

	@Test
	public void emitAsDocumentFalse() {
		builder.setEmitAsDocument(false);
		assertFalse(builder.isEmitAsDocument());

		buildBasicDocument();
		assertExpected("emitAsDocumentFalse");
	}

	@Test
	public void emitAsDocumentTrue() {
		builder.setEmitAsDocument(true);
		assertTrue(builder.isEmitAsDocument());

		buildBasicDocument();
		assertExpected("emitAsDocumentTrue");
	}

	@Test
	public void emitAsDocumentFalseFormatting() {
		setupFormatting();
		builder.setEmitAsDocument(false);
		assertFalse(builder.isEmitAsDocument());

		buildBasicDocument();
		assertExpected("emitAsDocumentFalseFormatting");
	}

	@Test
	public void emitAsDocumentTrueFormatting() {
		setupFormatting();
		builder.setEmitAsDocument(true);
		assertTrue(builder.isEmitAsDocument());

		buildBasicDocument();
		assertExpected("emitAsDocumentTrueFormatting");
	}

	@Test
	public void blockCode() {
		builder.beginBlock(BlockType.CODE, new Attributes());
		builder.characters("test");
		builder.endBlock();
		assertEquals("<pre><code>test</code></pre>", out.toString());
	}

	@Test
	public void blockPreformatted() {
		builder.beginBlock(BlockType.PREFORMATTED, new Attributes());
		builder.characters("test");
		builder.endBlock();
		assertEquals("<pre>test</pre>", out.toString());
	}

	protected void setupFormatting() {
		builder = new HtmlDocumentBuilder(out, true);
	}

	protected void buildBasicDocument() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("test");
		builder.endBlock();
		builder.endDocument();
	}

	private void assertExpected(String resource) {
		assertEquals(loadResourceContent(resource), out.toString());
	}

	private String loadResourceContent(String resourceName) {
		try {
			String fileName = HtmlDocumentBuilderTest.class.getSimpleName() + '_' + resourceName + ".xml";
			URL resource = HtmlDocumentBuilderTest.class.getResource(fileName);
			return Resources.toString(resource, Charsets.UTF_8);
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}
}
