/*******************************************************************************
 * Copyright (c) 2012 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.core.util;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;

import org.eclipse.mylyn.wikitext.core.util.DefaultXmlStreamWriter;
import org.eclipse.mylyn.wikitext.core.util.FormattingXMLStreamWriter;
import org.junit.Before;
import org.junit.Test;

public class FormattingXmlStreamWriterTest {

	private StringWriter out;

	private FormattingXMLStreamWriter writer;

	@Before
	public void setUp() {
		out = new StringWriter();
		writer = new FormattingXMLStreamWriter(new DefaultXmlStreamWriter(out));
	}

	@Test
	public void testXmlDecl() {
		writer.writeStartDocument();
		writer.writeEndDocument();
		assertEquals("<?xml version='1.0' ?>", out.toString());
	}

	@Test
	public void testComment() {
		writer.writeStartDocument("utf-8", "1.0");
		writer.writeComment("test one two");
		writer.writeEndDocument();
		assertEquals("<?xml version='1.0' encoding='utf-8' ?>\n<!-- test one two -->", out.toString());
	}

	@Test
	public void testLoneElement() {
		writer.writeStartElement("test");
		writer.writeEndElement();
		assertEquals("<test></test>", out.toString());
	}

	@Test
	public void testLoneElementWithContent() {
		writer.writeStartElement("test");
		writer.writeCharacters("abc 123");
		writer.writeEndElement();
		assertEquals("<test>abc 123</test>", out.toString());
	}

	@Test
	public void testLoneElementWithContentAndNestedElement() {
		writer.writeStartElement("test");
		writer.writeCharacters("abc 123");
		writer.writeEmptyElement("inner");
		writer.writeEndElement();
		assertEquals("<test>abc 123\n\t<inner/>\n</test>", out.toString());
	}

	@Test
	public void testElementWithNestedElementWithContent() {
		writer.writeStartElement("root");
		writer.writeStartElement("test");
		writer.writeCharacters("abc 123");
		writer.writeEndElement();
		writer.writeEndElement();
		assertEquals("<root>\n\t<test>abc 123</test>\n</root>", out.toString());
	}

	@Test
	public void testElementWithComment() {
		writer.writeStartDocument("utf-8", "1.0");
		writer.writeStartElement("root");
		writer.writeComment("test one two");
		writer.writeEndElement();
		writer.writeEndDocument();
		assertEquals(
				"<?xml version='1.0' encoding='utf-8' ?>\n" + "<root>\n" + "\t<!-- test one two -->\n" + "</root>",
				out.toString());
	}

	@Test
	public void testElementAttributeOrdering() {
		writer.writeStartDocument();
		writer.writeStartElement("test");
		writer.writeAttribute("a", "test");
		writer.writeAttribute("b", "test");
		writer.writeAttribute("c", "test");
		writer.writeAttribute("d", "test");
		writer.writeAttribute("ever", "test");
		writer.writeAttribute("x", "test");
		writer.writeAttribute("xa", "test");
		writer.writeEndElement();
		writer.writeEndDocument();
		assertEquals("<?xml version='1.0' ?>\n"
				+ "<test a=\"test\" b=\"test\" c=\"test\" d=\"test\" ever=\"test\" x=\"test\" xa=\"test\"></test>",
				out.toString());
	}
}
