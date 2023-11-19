/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies and others.
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

package org.eclipse.mylyn.wikitext.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.StringWriter;

import org.junit.Test;

public class DefaultXmlStreamWriterTest {

	private final StringWriter out = new StringWriter();

	private final DefaultXmlStreamWriter writer = new DefaultXmlStreamWriter(out);

	@Test
	public void elementWithAttributes() {
		writer.writeStartDocument();
		writer.writeStartElement("test");
		writer.writeAttribute("a", "test");
		writer.writeAttribute("b", "test");
		writer.writeEndElement();
		assertXml("<?xml version='1.0' ?><test a=\"test\" b=\"test\"></test>");
	}

	@Test
	public void elementWithAttributesNs() {
		writer.setPrefix("pf", "uri:urn:test");
		writer.writeStartDocument("utf-8", "1.1");
		writer.writeStartElement("test");
		writer.writeNamespace("pf", "uri:urn:test");
		writer.writeNamespace("px", "uri:urn:test2");
		writer.writeAttribute("uri:urn:test", "a", "testv");
		writer.writeAttribute("px", "uri:urn:test2", "a", "testv");
		writer.writeAttribute("b", "test");
		writer.writeEndElement();
		assertXml(
				"<?xml version='1.1' encoding='utf-8' ?><test xmlns:pf=\"uri:urn:test\" xmlns:px=\"uri:urn:test2\" pf:a=\"testv\" px:a=\"testv\" b=\"test\"></test>");
	}

	@Test
	public void elementStartDocumentWithVersion() {
		writer.writeStartDocument("1.1");
		writer.writeEmptyElement("test");
		writer.writeEndDocument();
		writer.close();
		assertXml("<?xml version='1.1' ?><test/>");
	}

	@Test
	public void namespaces() {
		assertNull(writer.getPrefix("uri:urn:test"));
		writer.setPrefix("pf", "uri:urn:test");
		assertEquals("pf", writer.getPrefix("uri:urn:test"));
	}

	@Test
	public void defaultNamespace() {
		writer.setDefaultNamespace("uri:urn:test");
		writer.writeStartDocument();
		writer.writeEmptyElement("uri:urn:test", "test");
		writer.writeNamespace("", "uri:urn:test");
		assertXml("<?xml version='1.0' ?><test xmlns=\"uri:urn:test\"/>");
	}

	@Test
	public void entityReference() {
		writer.writeStartDocument();
		writer.writeStartElement("test");
		writer.writeCharacters("some text");
		writer.writeEntityRef("#xa");
		writer.writeEndElement();
		writer.writeEndDocument();
		writer.close();
		assertXml("<?xml version='1.0' ?><test>some text&#xa;</test>");
	}

	@Test
	public void emptyElementWithNamespace() {
		writer.writeStartDocument();
		writer.writeEmptyElement("a", "test", "uri:urn:a");
		writer.writeNamespace("a", "uri:urn:a");
		assertXml("<?xml version='1.0' ?><a:test xmlns:a=\"uri:urn:a\"/>");
	}

	@Test
	public void writeDefaultNamespace() {
		writer.writeStartDocument();
		writer.writeStartElement("uri:urn:a", "test");
		writer.writeDefaultNamespace("uri:urn:a");
		writer.writeCharacters("test it");
		writer.writeEndElement();
		writer.writeEndDocument();
		writer.close();
		assertXml("<?xml version='1.0' ?><test xmlns=\"uri:urn:a\">test it</test>");
	}

	@Test
	public void writeElementNsUriAndPrefix() {
		writer.writeStartDocument();
		writer.writeStartElement("p", "test", "uri:urn:a");
		writer.writeNamespace("p", "uri:urn:a");
		writer.writeCharacters("test it");
		writer.writeEndElement();
		assertXml("<?xml version='1.0' ?><p:test xmlns:p=\"uri:urn:a\">test it</p:test>");
	}

	@Test
	public void writeElementNsUri() {
		writer.setPrefix("p", "uri:urn:a");
		writer.writeStartDocument();
		writer.writeStartElement("uri:urn:a", "test");
		writer.writeNamespace("p", "uri:urn:a");
		writer.writeCharacters("test it");
		writer.writeEndElement();
		assertXml("<?xml version='1.0' ?><p:test xmlns:p=\"uri:urn:a\">test it</p:test>");
	}

	@Test
	public void writeElementNsUriEmptyPrefix() {
		writer.writeStartDocument();
		writer.writeStartElement("", "test", "uri:urn:a");
		writer.writeCharacters("test it");
		writer.writeEndElement();
		assertXml("<?xml version='1.0' ?><test>test it</test>");
	}

	@Test
	public void writeEmptyElementNsUri() {
		writer.setPrefix("p", "uri:urn:a");
		writer.writeStartDocument();
		writer.writeEmptyElement("uri:urn:a", "test");
		writer.writeNamespace("p", "uri:urn:a");
		assertXml("<?xml version='1.0' ?><p:test xmlns:p=\"uri:urn:a\"/>");
	}

	@Test
	public void writeComment() {
		writer.writeStartDocument();
		writer.writeEmptyElement("test");
		writer.writeComment("some comment text");
		assertXml("<?xml version='1.0' ?><test/><!-- some comment text -->");
	}

	@Test
	public void writeCData() {
		writer.writeStartDocument();
		writer.writeStartElement("test");
		writer.writeCData("one\ntwo");
		writer.writeEndElement();
		assertXml("<?xml version='1.0' ?><test><![CDATA[one\ntwo]]></test>");
	}

	@Test
	public void contentEncodingNewlinesAndSpecialCharacters() {
		writer.writeStartDocument();
		writer.writeStartElement("test");
		writer.writeCharacters("one\ntwo <>");
		writer.writeEndElement();
		assertXml("<?xml version='1.0' ?><test>one\ntwo &lt;&gt;</test>");
	}

	@Test
	public void attributeEncoding() {
		writer.writeStartDocument();
		writer.writeEmptyElement("test");
		writer.writeAttribute("a", "one\ntwo <>'\"\r\n");
		writer.writeEndDocument();
		writer.close();
		assertXml("<?xml version='1.0' ?><test a=\"one&#xA;two &lt;&gt;&apos;&quot;&#xD;&#xA;\"/>");
	}

	@Test
	public void flushAfterClose() {
		writer.writeStartDocument();
		writer.writeEndDocument();
		writer.close();
		writer.flush();
		assertXml("<?xml version='1.0' ?>");
	}

	@Test
	public void flush() {
		writer.writeStartDocument();
		writer.flush();
		assertXml("<?xml version='1.0' ?>");
	}

	@Test
	public void closeMultipleTimes() {
		writer.writeStartDocument();
		writer.close();
		writer.close();
		assertXml("<?xml version='1.0' ?>");
	}

	private void assertXml(String expected) {
		writer.writeEndDocument();
		writer.close();
		assertEquals(expected, out.toString());
	}
}
