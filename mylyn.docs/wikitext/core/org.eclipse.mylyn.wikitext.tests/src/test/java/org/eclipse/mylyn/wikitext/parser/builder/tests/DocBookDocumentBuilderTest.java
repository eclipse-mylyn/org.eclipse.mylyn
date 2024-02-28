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
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.LinkAttributes;
import org.eclipse.mylyn.wikitext.parser.ListAttributes;
import org.eclipse.mylyn.wikitext.parser.TableAttributes;
import org.eclipse.mylyn.wikitext.parser.builder.DocBookDocumentBuilder;
import org.junit.Before;
import org.junit.Test;

/**
 * @author David Green
 */
@SuppressWarnings("nls")
public class DocBookDocumentBuilderTest {

	private final static String DOCBOOK_BEGIN = "<?xml version='1.0' ?><!DOCTYPE book PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\" \"http://www.oasis-open.org/docbook/xml/4.5/docbookx.dtd\"><book><title></title>";

	private final static String DOCBOOK_END = "</book>";

	private final static String DOCBOOK_BEGIN_CHAPTER = DOCBOOK_BEGIN + "<chapter><title></title>";

	private final static String DOCBOOK_END_CHAPTER = "</chapter>" + DOCBOOK_END;

	private StringWriter out;

	private DocBookDocumentBuilder builder;

	@Before
	public void setUp() {
		out = new StringWriter();
		builder = new DocBookDocumentBuilder(out);

	}

	@Test
	public void testLink() {
		builder.beginDocument();

		builder.characters("a ");
		builder.link(new LinkAttributes(), "#foo", "link to foo");
		builder.characters(" test");

		builder.endDocument();

		String docbook = out.toString();

		String expectedContent = "a <link linkend=\"foo\">link to foo</link> test";
		assertEquals(DOCBOOK_BEGIN + expectedContent + DOCBOOK_END, docbook);
	}

	@Test
	public void testExternalLink() {
		builder.beginDocument();

		builder.characters("an ");
		builder.link(new LinkAttributes(), "http://example.com", "external link");
		builder.characters(" test");

		builder.endDocument();

		String docbook = out.toString();

		String expectedContent = "an <ulink url=\"http://example.com\">external link</ulink> test";
		assertEquals(DOCBOOK_BEGIN + expectedContent + DOCBOOK_END, docbook);
	}

	@Test
	public void testLinkWithNullHref() {
		builder.beginDocument();

		builder.characters("a ");
		LinkAttributes attributes = new LinkAttributes();
		attributes.setId("some.id");
		builder.link(attributes, null, null);
		builder.characters(" test");

		builder.endDocument();

		String docbook = out.toString();

		String expectedContent = "a <anchor id=\"some.id\"></anchor> test";
		assertEquals(DOCBOOK_BEGIN + expectedContent + DOCBOOK_END, docbook);

	}

	@Test
	public void testSpanLinkWithNullHref() {
		builder.beginDocument();

		builder.characters("a ");
		LinkAttributes attributes = new LinkAttributes();
		attributes.setId("some.id");
		builder.beginSpan(SpanType.LINK, attributes);
		builder.endSpan();
		builder.characters(" test");

		builder.endDocument();

		String docbook = out.toString();

		String expectedContent = "a <anchor id=\"some.id\"></anchor> test";
		assertEquals(DOCBOOK_BEGIN + expectedContent + DOCBOOK_END, docbook);
	}

	@Test
	public void testDefinitionList() {
		builder.beginDocument();
		builder.beginBlock(BlockType.DEFINITION_LIST, new Attributes());

		builder.beginBlock(BlockType.DEFINITION_TERM, new Attributes());
		builder.characters("foo");
		builder.endBlock(); // DT

		builder.beginBlock(BlockType.DEFINITION_ITEM, new Attributes());
		builder.characters("Foo definition");
		builder.endBlock(); // DI

		builder.beginBlock(BlockType.DEFINITION_TERM, new Attributes());
		builder.characters("bar");
		builder.endBlock(); // DT

		builder.beginBlock(BlockType.DEFINITION_ITEM, new Attributes());
		builder.characters("Bar definition");
		builder.endBlock(); // DI

		builder.endBlock(); // DL
		builder.endDocument();

		String docbook = out.toString();

		String expectedContent = "<variablelist><varlistentry><term>foo</term><listitem><para>Foo definition</para></listitem></varlistentry><varlistentry><term>bar</term><listitem><para>Bar definition</para></listitem></varlistentry></variablelist>";
		assertEquals(DOCBOOK_BEGIN_CHAPTER + expectedContent + DOCBOOK_END_CHAPTER, docbook);
	}

	@Test
	public void testAutomaticGlossaryOnByDefault() {
		assertTrue(builder.isAutomaticGlossary());
	}

	@Test
	public void testNestedListsCreatesValidDocbook() {
		builder.beginDocument();
		builder.beginBlock(BlockType.BULLETED_LIST, new Attributes());

		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("foo");

		builder.beginBlock(BlockType.BULLETED_LIST, new Attributes());

		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("bar");
		builder.endBlock(); // LI

		builder.endBlock(); // UL

		builder.characters("foo2");
		builder.endBlock(); // LI

		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("baz");
		builder.endBlock(); // LI

		builder.endBlock(); // UL
		builder.endDocument();

		String docbook = out.toString();

		// should look something like this:

		// <itemizedlist>
		// <listitem>
		// <para>foo</para>
		// <itemizedlist>
		// <listitem>
		// <para>bar</para>
		// </listitem>
		// </itemizedlist>
		// <para>foo2</para>
		// </listitem>
		// <listitem>
		// <para>baz</para>
		// </listitem>
		// </itemizedlist>

		String expectedContent = "<itemizedlist><listitem><para>foo</para><itemizedlist><listitem><para>bar</para></listitem></itemizedlist><para>foo2</para></listitem><listitem><para>baz</para></listitem></itemizedlist>";
		assertEquals(DOCBOOK_BEGIN_CHAPTER + expectedContent + DOCBOOK_END_CHAPTER, docbook);
	}

	@Test
	public void testOrderedListArabicType() {
		builder.beginDocument();
		ListAttributes listAttributes = new ListAttributes();
		listAttributes.setCssStyle("list-style-type: decimal;");
		builder.beginBlock(BlockType.NUMERIC_LIST, listAttributes);

		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("item");
		builder.endBlock(); // LI
		builder.endBlock(); // OL
		builder.endDocument();

		String docbook = out.toString();

		String expectedContent = "<orderedlist numeration=\"arabic\"><listitem><para>item</para></listitem></orderedlist>";
		assertEquals(DOCBOOK_BEGIN_CHAPTER + expectedContent + DOCBOOK_END_CHAPTER, docbook);
	}

	@Test
	public void testOrderedListLoweralphaType() {
		builder.beginDocument();
		ListAttributes listAttributes = new ListAttributes();
		listAttributes.setCssStyle("list-style-type: lower-alpha;");
		builder.beginBlock(BlockType.NUMERIC_LIST, listAttributes);

		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("item");
		builder.endBlock(); // LI
		builder.endBlock(); // OL
		builder.endDocument();

		String docbook = out.toString();

		String expectedContent = "<orderedlist numeration=\"loweralpha\"><listitem><para>item</para></listitem></orderedlist>";
		assertEquals(DOCBOOK_BEGIN_CHAPTER + expectedContent + DOCBOOK_END_CHAPTER, docbook);
	}

	@Test
	public void testOrderedListUpperalphaType() {
		builder.beginDocument();
		ListAttributes listAttributes = new ListAttributes();
		listAttributes.setCssStyle("list-style-type: upper-alpha;");
		builder.beginBlock(BlockType.NUMERIC_LIST, listAttributes);

		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("item");
		builder.endBlock(); // LI
		builder.endBlock(); // OL
		builder.endDocument();

		String docbook = out.toString();

		String expectedContent = "<orderedlist numeration=\"upperalpha\"><listitem><para>item</para></listitem></orderedlist>";
		assertEquals(DOCBOOK_BEGIN_CHAPTER + expectedContent + DOCBOOK_END_CHAPTER, docbook);
	}

	@Test
	public void testOrderedListLowerromanType() {
		builder.beginDocument();
		ListAttributes listAttributes = new ListAttributes();
		listAttributes.setCssStyle("list-style-type: lower-roman;");
		builder.beginBlock(BlockType.NUMERIC_LIST, listAttributes);

		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("item");
		builder.endBlock(); // LI
		builder.endBlock(); // OL
		builder.endDocument();

		String docbook = out.toString();

		String expectedContent = "<orderedlist numeration=\"lowerroman\"><listitem><para>item</para></listitem></orderedlist>";
		assertEquals(DOCBOOK_BEGIN_CHAPTER + expectedContent + DOCBOOK_END_CHAPTER, docbook);
	}

	@Test
	public void testOrderedListUpperromanType() {
		builder.beginDocument();
		ListAttributes listAttributes = new ListAttributes();
		listAttributes.setCssStyle("list-style-type: upper-roman;");
		builder.beginBlock(BlockType.NUMERIC_LIST, listAttributes);

		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("item");
		builder.endBlock(); // LI
		builder.endBlock(); // OL
		builder.endDocument();

		String docbook = out.toString();

		String expectedContent = "<orderedlist numeration=\"upperroman\"><listitem><para>item</para></listitem></orderedlist>";
		assertEquals(DOCBOOK_BEGIN_CHAPTER + expectedContent + DOCBOOK_END_CHAPTER, docbook);
	}

	@Test
	public void testDiv() {
		builder.beginDocument();
		builder.beginBlock(BlockType.DIV, new Attributes());

		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("foo");
		builder.endBlock(); // PARAGRAPH

		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("bar");
		builder.endBlock(); // PARAGRAPH

		builder.endBlock(); // DIV
		builder.endDocument();

		String docbook = out.toString();

		String expectedContent = "<para>foo</para><para>bar</para>";
		assertEquals(DOCBOOK_BEGIN_CHAPTER + expectedContent + DOCBOOK_END_CHAPTER, docbook);
	}

	@Test
	public void testSpanLink() {
		builder.beginDocument();
		builder.beginBlock(BlockType.DIV, new Attributes());

		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		LinkAttributes attributes = new LinkAttributes();
		attributes.setHref("#test1234");
		builder.beginSpan(SpanType.LINK, attributes);
		builder.beginSpan(SpanType.EMPHASIS, new Attributes());
		builder.characters("link text");
		builder.endSpan();
		builder.endSpan();
		builder.endBlock(); // PARAGRAPH

		builder.endBlock(); // DIV
		builder.endDocument();

		String docbook = out.toString();

		String expectedContent = "<para><link linkend=\"test1234\"><emphasis>link text</emphasis></link></para>";
		assertEquals(DOCBOOK_BEGIN_CHAPTER + expectedContent + DOCBOOK_END_CHAPTER, docbook);
	}

	@Test
	public void testTableClass() {
		builder.beginDocument();
		TableAttributes tableAttributes = new TableAttributes();
		tableAttributes.appendCssClass("foo");
		builder.beginBlock(BlockType.TABLE, tableAttributes);

		builder.beginBlock(BlockType.TABLE_ROW, new Attributes());

		builder.beginBlock(BlockType.TABLE_CELL_NORMAL, new Attributes());
		builder.characters("text");
		builder.endBlock(); // cell

		builder.endBlock(); // row

		builder.endBlock(); // table
		builder.endDocument();

		String docbook = out.toString();

		String expectedContent = "<informaltable role=\"foo\"><tr><td>text</td></tr></informaltable>";
		assertEquals(DOCBOOK_BEGIN_CHAPTER + expectedContent + DOCBOOK_END_CHAPTER, docbook);
	}

	@Test
	public void testMarked() {
		builder.beginDocument();
		builder.characters("normal text ");
		builder.beginSpan(SpanType.MARK, new Attributes());
		builder.characters("marked text");
		builder.endSpan();
		builder.endDocument();

		String docbook = out.toString();

		String expectedContent = "normal text <emphasis role=\"marked\">marked text</emphasis>";
		assertEquals(DOCBOOK_BEGIN + expectedContent + DOCBOOK_END, docbook);
	}

}
