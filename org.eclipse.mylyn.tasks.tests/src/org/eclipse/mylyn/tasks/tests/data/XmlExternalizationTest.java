/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.core.data.ITaskDataConstants;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Steffen Pingel
 * @author Shawn Minto
 */
public class XmlExternalizationTest extends TestCase {

	private class SimpleCharacterReader extends DefaultHandler {

		private char ch;

		public SimpleCharacterReader() {
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			//System.err.println(Arrays.toString(ch));
			Assert.assertEquals(1, length);
			this.ch = ch[start];
		}

		public char getCharacter() {
			return ch;
		}

	}

	private final class SimpleCharacterWriter {
		private final TransformerHandler handler;

		public SimpleCharacterWriter(TransformerHandler handler) {
			this.handler = handler;
		}

		public void write(char character) throws SAXException {
			handler.startDocument();
			AttributesImpl atts = new AttributesImpl();
			handler.startElement("", "", ITaskDataConstants.ELEMENT_VALUE, atts); //$NON-NLS-1$ //$NON-NLS-2$
			///handler.startCDATA();
			handler.characters(new char[] { character }, 0, 1);
			//handler.endCDATA();
			handler.endElement("", "", ITaskDataConstants.ELEMENT_VALUE);
			handler.endDocument();
		}
	}

	public void testWriteandReadBadCharacterXml10() throws Exception {
		for (int i = 0; i < 0xFFFF; i++) {
			char badChar = (char) i;

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			SAXTransformerFactory transformerFactory = (SAXTransformerFactory) TransformerFactory.newInstance();
			TransformerHandler handler = transformerFactory.newTransformerHandler();
			Transformer serializer = handler.getTransformer();
			serializer.setOutputProperty(OutputKeys.VERSION, "1.0");
			serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8"); //$NON-NLS-1$
			serializer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
			handler.setResult(new StreamResult(out));
			SimpleCharacterWriter writer = new SimpleCharacterWriter(handler);
			writer.write(badChar);

			XMLReader parser = XMLReaderFactory.createXMLReader();
			parser.setFeature("http://apache.org/xml/features/continue-after-fatal-error", true);
			SimpleCharacterReader readHandler = new SimpleCharacterReader();
			parser.setErrorHandler(new ErrorHandler() {
				public void warning(SAXParseException exception) throws SAXException {
					exception.printStackTrace();
				}

				public void fatalError(SAXParseException exception) throws SAXException {
					exception.printStackTrace();
				}

				public void error(SAXParseException exception) throws SAXException {
					exception.printStackTrace();
				}
			});
			parser.setContentHandler(readHandler);
			parser.parse(new InputSource(new ByteArrayInputStream(out.toByteArray())));
			char character = readHandler.getCharacter();
			assertEquals(badChar, character);
		}
	}

	public void testWriteandReadBadCharacterXml11() throws Exception {
		for (int i = 0; i < 0xFFFF; i++) {
			char badChar = (char) i;

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			SAXTransformerFactory transformerFactory = (SAXTransformerFactory) TransformerFactory.newInstance();
			TransformerHandler handler = transformerFactory.newTransformerHandler();
			Transformer serializer = handler.getTransformer();
			serializer.setOutputProperty(OutputKeys.VERSION, "1.1");
			serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8"); //$NON-NLS-1$
			serializer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
			handler.setResult(new StreamResult(out));
			SimpleCharacterWriter writer = new SimpleCharacterWriter(handler);
			writer.write(badChar);

			XMLReader parser = XMLReaderFactory.createXMLReader();
			parser.setFeature("http://apache.org/xml/features/continue-after-fatal-error", true);
			SimpleCharacterReader readHandler = new SimpleCharacterReader();
			parser.setErrorHandler(new ErrorHandler() {
				public void warning(SAXParseException exception) throws SAXException {
					exception.printStackTrace();
				}

				public void fatalError(SAXParseException exception) throws SAXException {
					exception.printStackTrace();
				}

				public void error(SAXParseException exception) throws SAXException {
					exception.printStackTrace();
				}
			});
			parser.setContentHandler(readHandler);
			parser.parse(new InputSource(new ByteArrayInputStream(out.toByteArray())));
			char character = readHandler.getCharacter();
			assertEquals(badChar, character);
		}
	}

}
