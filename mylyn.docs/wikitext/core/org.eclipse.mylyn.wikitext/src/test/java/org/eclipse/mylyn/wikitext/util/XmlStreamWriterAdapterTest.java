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

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stax.StAXResult;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;

public class XmlStreamWriterAdapterTest {

	private final StringWriter out = new StringWriter();

	private final XmlStreamWriter delegate = new DefaultXmlStreamWriter(out);

	private final XmlStreamWriterAdapter adapter = new XmlStreamWriterAdapter(delegate);

	@Test
	public void elementWithAttributes() {
		adapter.writeStartDocument();
		adapter.writeStartElement("test");
		adapter.writeAttribute("a", "test");
		adapter.writeAttribute("b", "test");
		adapter.writeEndElement();
		adapter.writeEndDocument();
		adapter.close();
		assertXml("<?xml version='1.0' ?><test a=\"test\" b=\"test\"></test>");
	}

	@Test
	public void elementWithAttributesNs() {
		adapter.setPrefix("pf", "uri:urn:test");
		adapter.writeStartDocument("utf-8", "1.1");
		adapter.writeStartElement("test");
		adapter.writeNamespace("pf", "uri:urn:test");
		adapter.writeAttribute("uri:urn:test", "a", "testv");
		adapter.writeAttribute("b", "test");
		adapter.writeEndElement();
		adapter.writeEndDocument();
		adapter.close();
		assertXml(
				"<?xml version='1.1' encoding='utf-8' ?><test xmlns:pf=\"uri:urn:test\" pf:a=\"testv\" b=\"test\"></test>");
	}

	@Test
	public void elementStartDocumentWithVersion() {
		adapter.writeStartDocument("1.1");
		adapter.writeEmptyElement("test");
		adapter.writeEndDocument();
		adapter.close();
		assertXml("<?xml version='1.1' ?><test/>");
	}

	@Test
	public void integrationTestWithSimpleDocument() {
		assertParseToXml("<?xml version='1.0' ?><test>abc</test>",
				"<?xml version='1.0' encoding='utf-8' ?><test>abc</test>");
	}

	@Test
	public void integrationTestWithMixedNamespaceAttributes() {
		assertParseToXml("<?xml version='1.0' ?><test xmlns:pf=\"uri:urn:test\" pf:a=\"testv\" b=\"test\"></test>",
				"<?xml version='1.0' ?><test xmlns:pf=\"uri:urn:test\" pf:a=\"testv\" b=\"test\"></test>");
	}

	@Test
	public void integrationTestWithMixedNestedElements() {
		assertParseToXml(
				"<?xml version='1.0' ?><test><one><one.1>text 1.1</one.1><one.2>text 1.2</one.2></one><two>text 2</two></test>",
				"<test><one><one.1>text 1.1</one.1><one.2>text 1.2</one.2></one><two>text 2</two></test>");
	}

	@Test
	public void integrationTestWithComment() {
		assertParseToXml("<?xml version='1.0' ?><html><!--  a comment  --></html>",
				"<?xml version='1.0' ?><html><!-- a comment --></html>");
	}

	@Test
	public void integrationTestWithCData() {
		assertParseToXml("<?xml version='1.0' ?><html><![CDATA[ <\n> ]]></html>",
				"<?xml version='1.0' ?><html><![CDATA[ <\n> ]]></html>");
	}

	@Test
	public void integrationTestWithDefaultNamespace() {
		assertParseToXml("<?xml version='1.0' ?><html xmlns=\"uri:urn:test\"></html>",
				"<?xml version='1.0' ?><html xmlns=\"uri:urn:test\"/>");
	}

	@Test
	public void integrationTestWithEmptyElementAndNamespace() {
		assertParseToXml("<?xml version='1.0' ?><x><a:y xmlns:a=\"test\"></a:y></x>",
				"<?xml version='1.0' ?><x><a:y xmlns:a=\"test\"/></x>");
	}

	@Test
	public void namespaces() {
		assertNull(adapter.getPrefix("uri:urn:test"));
		adapter.setPrefix("pf", "uri:urn:test");
		assertEquals("pf", adapter.getPrefix("uri:urn:test"));
		assertEquals("pf", adapter.getNamespaceContext().getPrefix("uri:urn:test"));
		assertEquals("uri:urn:test", adapter.getNamespaceContext().getNamespaceURI("pf"));
		assertEquals("pf", adapter.getNamespaceContext().getPrefixes("uri:urn:test").next());
	}

	@Test
	public void defaultNamespace() {
		adapter.setDefaultNamespace("uri:urn:test");
		adapter.writeStartDocument();
		adapter.writeEmptyElement("uri:urn:test", "test");
		adapter.writeNamespace("", "uri:urn:test");
		adapter.writeEndDocument();
		adapter.close();
		assertXml("<?xml version='1.0' ?><test xmlns=\"uri:urn:test\"/>");
	}

	@Test
	public void entityReference() {
		adapter.writeStartDocument();
		adapter.writeStartElement("test");
		adapter.writeCharacters("some text");
		adapter.writeEntityRef("#xa");
		adapter.writeEndElement();
		adapter.writeEndDocument();
		adapter.close();
		assertXml("<?xml version='1.0' ?><test>some text&#xa;</test>");
	}

	@Test
	public void emptyElementWithNamespace() {
		adapter.writeStartDocument();
		adapter.writeEmptyElement("a", "test", "uri:urn:a");
		adapter.writeNamespace("a", "uri:urn:a");
		adapter.writeEndDocument();
		adapter.close();
		assertXml("<?xml version='1.0' ?><a:test xmlns:a=\"uri:urn:a\"/>");
	}

	@Test
	public void writeDefaultNamespace() {
		adapter.writeStartDocument();
		adapter.writeStartElement("uri:urn:a", "test");
		adapter.writeDefaultNamespace("uri:urn:a");
		adapter.writeCharacters("test it");
		adapter.writeEndElement();
		adapter.writeEndDocument();
		adapter.close();
		assertXml("<?xml version='1.0' ?><test xmlns=\"uri:urn:a\">test it</test>");
	}

	@Test
	public void writeElementNsUriAndPrefix() {
		adapter.writeStartDocument();
		adapter.writeStartElement("p", "test", "uri:urn:a");
		adapter.writeNamespace("p", "uri:urn:a");
		adapter.writeCharacters("test it");
		adapter.writeEndElement();
		adapter.writeEndDocument();
		adapter.close();
		assertXml("<?xml version='1.0' ?><p:test xmlns:p=\"uri:urn:a\">test it</p:test>");
	}

	@Test
	public void writeElementNsUri() {
		adapter.setPrefix("p", "uri:urn:a");
		adapter.writeStartDocument();
		adapter.writeStartElement("uri:urn:a", "test");
		adapter.writeNamespace("p", "uri:urn:a");
		adapter.writeCharacters("test it");
		adapter.writeEndElement();
		adapter.writeEndDocument();
		adapter.close();
		assertXml("<?xml version='1.0' ?><p:test xmlns:p=\"uri:urn:a\">test it</p:test>");
	}

	private void assertParseToXml(String expectedResult, String input) {
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(new StreamSource(new StringReader(input)), new StAXResult(adapter));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		assertXml(expectedResult);
	}

	private void assertXml(String expected) {
		assertEquals(expected, out.toString());
	}
}
