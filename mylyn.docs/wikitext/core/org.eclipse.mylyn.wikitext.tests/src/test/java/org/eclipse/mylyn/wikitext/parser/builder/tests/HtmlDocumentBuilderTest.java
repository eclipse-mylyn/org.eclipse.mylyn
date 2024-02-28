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

package org.eclipse.mylyn.wikitext.parser.builder.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.ListAttributes;
import org.eclipse.mylyn.wikitext.parser.TableAttributes;
import org.eclipse.mylyn.wikitext.parser.TableCellAttributes;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentHandler;
import org.eclipse.mylyn.wikitext.parser.builder.UriProcessor;
import org.eclipse.mylyn.wikitext.util.XmlStreamWriter;
import org.junit.Before;
import org.junit.Test;

import com.google.common.io.Resources;

@SuppressWarnings("nls")
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

	@Test(expected = NullPointerException.class)
	public void setDocumentHandlerNull() {
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
	public void listStyles() {
		assertListStyle("decimal");
		assertListStyle("upper-alpha");
		assertListStyle("lower-alpha");
		assertListStyle("lower-roman");
		assertListStyle("upper-roman");
	}

	@Test
	public void spanMark() {
		builder.setEmitAsDocument(false);
		builder.beginDocument();
		builder.characters("normal text ");
		builder.beginSpan(SpanType.MARK, new Attributes());
		builder.characters("marked text");
		builder.endSpan();
		builder.endDocument();
		assertEquals("normal text <mark>marked text</mark>", out.toString());
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
		NullPointerException e = assertThrows(NullPointerException.class,
				() -> builder.setElementNameOfSpanType(null, "some-name"));
		assertTrue(e.getMessage().contains("Must provide spanType"));
	}

	@Test
	public void setElementNameOfSpanTypeRequiresElementName() {
		NullPointerException e = assertThrows(NullPointerException.class,
				() -> builder.setElementNameOfSpanType(SpanType.BOLD, null));
		assertTrue(e.getMessage().contains("Must provide elementName"));
	}

	@Test
	public void addLinkUriProcessor() {
		builder.setEmitAsDocument(false);
		builder.addLinkUriProcessor(new UriProcessor() {

			@Override
			public String process(String uri) {
				return "a-uri";
			}

			@Override
			public String target(String uri) {
				return "a-target";
			}
		});
		builder.beginDocument();
		builder.link("something", "text");
		builder.endDocument();
		assertEquals("<a href=\"a-uri\" target=\"a-target\">text</a>", out.toString());
	}

	@Test
	public void addLinkUriProcessorCopyConfiguration() {
		builder.setEmitAsDocument(false);
		builder.addLinkUriProcessor(h -> "a-target");
		StringWriter otherWriter = new StringWriter();
		HtmlDocumentBuilder other = new HtmlDocumentBuilder(otherWriter);
		builder.copyConfiguration(other);
		other.beginDocument();
		other.link("something", "text");
		other.endDocument();
		assertEquals("<a href=\"a-target\">text</a>", otherWriter.toString());
	}

	@Test
	public void tableWithAttributes() {
		builder.setEmitAsDocument(false);
		builder.beginBlock(BlockType.TABLE, tableAttributesWithBorderAndAlign());
		builder.beginBlock(BlockType.TABLE_ROW, new Attributes());
		builder.beginBlock(BlockType.TABLE_CELL_NORMAL, tableCellAttributesWithScope());
		builder.characters("cell content");
		builder.endBlock();
		builder.endBlock();
		builder.endBlock();
		assertExpected("tableWithAttributes");
	}

	@Test
	public void tableWithoutAttributes() {
		builder.setEmitAsDocument(false);
		builder.beginBlock(BlockType.TABLE, new TableAttributes());
		builder.beginBlock(BlockType.TABLE_ROW, new Attributes());
		builder.beginBlock(BlockType.TABLE_CELL_NORMAL, new Attributes());
		builder.characters("cell content");
		builder.endBlock();
		builder.endBlock();
		builder.endBlock();
		assertExpected("tableWithoutAttributes");
	}

	private TableAttributes tableAttributesWithBorderAndAlign() {
		TableAttributes attributes = new TableAttributes();
		attributes.setAlign("left");
		attributes.setBorder("1");
		return attributes;
	}

	private TableCellAttributes tableCellAttributesWithScope() {
		TableCellAttributes attributes = new TableCellAttributes();
		attributes.setScope("col");
		return attributes;
	}

	private void assertListStyle(String listStyleType) {
		setup();
		ListAttributes attributes = new ListAttributes();
		attributes.setCssStyle(String.format("list-style-type: %s;", listStyleType));
		builder.setEmitAsDocument(false);
		builder.beginDocument();
		builder.beginBlock(BlockType.NUMERIC_LIST, attributes);
		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("first");
		builder.endBlock();
		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("second");
		builder.endBlock();
		builder.endBlock();
		builder.endDocument();
		assertEquals("<ol style=\"list-style-type: " + listStyleType + ";\"><li>first</li><li>second</li></ol>",
				out.toString());
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
			return convertToUnixLineEndings(Resources.toString(resource, StandardCharsets.UTF_8));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private String convertToUnixLineEndings(String resource) {
		return resource.replaceAll("\\r\\n?", "\n");
	}
}
