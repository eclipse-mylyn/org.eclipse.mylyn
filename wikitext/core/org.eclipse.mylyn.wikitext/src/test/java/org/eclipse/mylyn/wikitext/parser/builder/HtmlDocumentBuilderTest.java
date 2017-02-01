/*******************************************************************************
 * Copyright (c) 2007, 2015 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.parser.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentHandler;
import org.eclipse.mylyn.wikitext.util.XmlStreamWriter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.Resources;

public class HtmlDocumentBuilderTest {
	private StringWriter out;

	private HtmlDocumentBuilder builder;

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

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
	public void emitAsDocumentDefaultTrue() {
		builder = new HtmlDocumentBuilder(out);
		assertTrue(builder.isEmitAsDocument());
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

	@Test
	public void setDocumentHandlerNull() {
		thrown.expect(NullPointerException.class);
		builder.setDocumentHandler(null);
	}

	@Test
	public void withDocumentHandler() {
		builder.setDocumentHandler(new HtmlDocumentHandler() {

			@Override
			public void endDocument(HtmlDocumentBuilder builder, XmlStreamWriter writer) {
				writer.writeLiteral("END");
			}

			@Override
			public void beginDocument(HtmlDocumentBuilder builder, XmlStreamWriter writer) {
				writer.writeLiteral("START");
			}
		});
		builder.beginDocument();
		builder.characters("test");
		builder.endDocument();
		assertEquals("STARTtestEND", out.toString());
	}

	@Test
	public void providesWriter() {
		assertNotNull(builder.getWriter());
	}

	@Test
	public void horizontalRule() {
		builder.setEmitAsDocument(false);
		builder.beginDocument();
		builder.horizontalRule();
		builder.endDocument();
		assertEquals("<hr/>", out.toString());
	}

	@Test
	public void entityFiltered() {
		assertEntityFiltered("&#732;", "tilde");
		assertEntityFiltered("&#9;", "Tab");
		assertEntityFiltered("&#169;", "copy");
		assertEntityFiltered("&#169;", "COPY");
	}

	@Test
	public void entityFilteredUnmatched() {
		assertEntityFiltered("&amp;bogus;", "bogus");
	}

	@Test
	public void entityUnfiltered() {
		builder.setFilterEntityReferences(false);
		assertEntity("&tilde;", "tilde");
	}

	@Test
	public void cssClassesAreApplied() {
		Attributes attributes = new Attributes();
		attributes.setCssClass("aclass");
		builder.setEmitAsDocument(false);
		builder.beginDocument();
		builder.beginBlock(BlockType.PREFORMATTED, attributes);
		builder.characters("content");
		builder.endBlock();
		builder.endDocument();
		assertEquals("<pre class=\"aclass\">content</pre>", out.toString());
	}

	@Test
	public void cssClassesAreAppliedToNestedElements() {
		Attributes attributes = new Attributes();
		attributes.setCssClass("aclass");
		builder.setEmitAsDocument(false);
		builder.beginDocument();
		builder.beginBlock(BlockType.CODE, attributes);
		builder.characters("content");
		builder.endBlock();
		builder.endDocument();
		assertEquals("<pre class=\"aclass\"><code class=\"aclass\">content</code></pre>", out.toString());
	}

	@Test
	public void filterEntityReferences() {
		assertEntityReferenceToNumericValue("&#160;", "nbsp");
		assertEntityReferenceToNumericValue("&#8817;", "nge");
		assertEntityReferenceToNumericValue("&#8807;&#824;", "ngE");
		assertEntityReferenceToNumericValue("&amp;notarealthing;", "notarealthing");
		assertEntityReferenceToNumericValue("&#160;", "#160");
	}

	@Test
	public void setElementNameOfSpanType() {
		builder.setElementNameOfSpanType(SpanType.BOLD, "new-bold");
		builder.setElementNameOfSpanType(SpanType.LINK, "new-a");

		assertElementNameOfSpanType(out, builder);

		StringWriter out2 = new StringWriter();
		HtmlDocumentBuilder builder2 = new HtmlDocumentBuilder(out2);
		builder.copyConfiguration(builder2);

		assertElementNameOfSpanType(out2, builder2);
	}

	@Test
	public void setElementNameOfSpanTypeRequiresSpanType() {
		thrown.expect(NullPointerException.class);
		thrown.expectMessage("Must provide spanType");
		builder.setElementNameOfSpanType(null, "some-name");
	}

	@Test
	public void setElementNameOfSpanTypeRequiresElementName() {
		thrown.expect(NullPointerException.class);
		thrown.expectMessage("Must provide elementName");
		builder.setElementNameOfSpanType(SpanType.BOLD, null);
	}

	private void assertElementNameOfSpanType(StringWriter out, HtmlDocumentBuilder builder) {
		builder.setEmitAsDocument(false);
		builder.beginDocument();

		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.characters("bold text");
		builder.endSpan();

		builder.link("http://example.com", "example.com");

		builder.endDocument();

		assertEquals("<new-bold>bold text</new-bold><new-a href=\"http://example.com\">example.com</new-a>",
				out.toString());
	}

	private void assertEntityReferenceToNumericValue(String expected, String entityReference) {
		StringWriter out = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(out);
		builder.setEmitAsDocument(false);
		builder.setFilterEntityReferences(true);
		builder.entityReference(entityReference);
		builder.flush();
		assertEquals(expected, out.toString());
	}

	private void assertEntityFiltered(String expected, String entity) {
		setup();
		builder.setFilterEntityReferences(true);
		builder.setEmitAsDocument(false);
		builder.beginDocument();
		builder.entityReference(entity);
		builder.endDocument();
		assertEquals(expected, out.toString());
	}

	private void assertEntity(String expected, String entity) {
		builder.setEmitAsDocument(false);
		builder.beginDocument();
		builder.entityReference(entity);
		builder.endDocument();
		assertEquals(expected, out.toString());
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
