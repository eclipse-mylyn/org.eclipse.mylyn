/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.core.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.eclipse.mylyn.internal.wikitext.core.util.ConcatenatingReader;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * a parser for well-formed XHTML, driving {@link DocumentBuilder}.
 * 
 * @see DocumentBuilder
 * @author David Green
 * @since 1.6
 */
public class HtmlParser {

	private static final Map<String, SpanType> elementNameToSpanType = new HashMap<String, SpanType>();
	static {
		elementNameToSpanType.put("a", SpanType.LINK); //$NON-NLS-1$
		elementNameToSpanType.put("b", SpanType.BOLD); //$NON-NLS-1$
		elementNameToSpanType.put("cite", SpanType.CITATION); //$NON-NLS-1$
		elementNameToSpanType.put("i", SpanType.ITALIC); //$NON-NLS-1$
		elementNameToSpanType.put("em", SpanType.EMPHASIS); //$NON-NLS-1$
		elementNameToSpanType.put("strong", SpanType.STRONG); //$NON-NLS-1$
		elementNameToSpanType.put("del", SpanType.DELETED); //$NON-NLS-1$
		elementNameToSpanType.put("ins", SpanType.INSERTED); //$NON-NLS-1$
		elementNameToSpanType.put("q", SpanType.QUOTE); //$NON-NLS-1$
		elementNameToSpanType.put("u", SpanType.UNDERLINED); //$NON-NLS-1$
		elementNameToSpanType.put("sup", SpanType.SUPERSCRIPT); //$NON-NLS-1$
		elementNameToSpanType.put("sub", SpanType.SUBSCRIPT); //$NON-NLS-1$
		elementNameToSpanType.put("span", SpanType.SPAN); //$NON-NLS-1$
		elementNameToSpanType.put("code", SpanType.CODE); //$NON-NLS-1$
		elementNameToSpanType.put("tt", SpanType.MONOSPACE); //$NON-NLS-1$
	}

	private static final Map<String, BlockType> elementNameToBlockType = new HashMap<String, DocumentBuilder.BlockType>();
	static {
		elementNameToBlockType.put("ul", BlockType.BULLETED_LIST); //$NON-NLS-1$
		elementNameToBlockType.put("code", BlockType.CODE); //$NON-NLS-1$
		elementNameToBlockType.put("div", BlockType.DIV); //$NON-NLS-1$
		elementNameToBlockType.put("footnote", BlockType.FOOTNOTE); //$NON-NLS-1$
		elementNameToBlockType.put("li", BlockType.LIST_ITEM); //$NON-NLS-1$
		elementNameToBlockType.put("ol", BlockType.NUMERIC_LIST); //$NON-NLS-1$
		elementNameToBlockType.put("dl", BlockType.DEFINITION_LIST); //$NON-NLS-1$
		elementNameToBlockType.put("dt", BlockType.DEFINITION_TERM); //$NON-NLS-1$
		elementNameToBlockType.put("dd", BlockType.DEFINITION_ITEM); //$NON-NLS-1$
		elementNameToBlockType.put("p", BlockType.PARAGRAPH); //$NON-NLS-1$
		elementNameToBlockType.put("pre", BlockType.PREFORMATTED); //$NON-NLS-1$
		elementNameToBlockType.put("blockquote", BlockType.QUOTE); //$NON-NLS-1$
		elementNameToBlockType.put("table", BlockType.TABLE); //$NON-NLS-1$
		elementNameToBlockType.put("th", BlockType.TABLE_CELL_HEADER); //$NON-NLS-1$
		elementNameToBlockType.put("td", BlockType.TABLE_CELL_NORMAL); //$NON-NLS-1$
		elementNameToBlockType.put("tr", BlockType.TABLE_ROW); //$NON-NLS-1$
	}

	private class DocumentBuilderAdapter implements ContentHandler {

		private Stack<ElementHandler> handlers = new Stack<ElementHandler>();

		private final DocumentBuilder builder;

		private boolean processingContent;

		public DocumentBuilderAdapter(DocumentBuilder builder) {
			this.builder = builder;
		}

		public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
			if (!processingContent) {
				if (localName.toLowerCase().equals("body")) { //$NON-NLS-1$
					processingContent = true;
				}
			}
			ElementHandler handler = computeElementHandler(localName);
			handlers.push(handler);
			handler.start(atts);
		}

		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (processingContent) {
				if (localName.toLowerCase().equals("body")) { //$NON-NLS-1$
					processingContent = false;
				}
			}
			if (!processingContent) {
				return;
			}
			ElementHandler handler = handlers.pop();
			handler.end();
		}

		public void characters(char[] ch, int start, int length) throws SAXException {
			if (processingContent) {
				handlers.peek().characters(new String(ch, start, length));
			}
		}

		public void setDocumentLocator(Locator locator) {
			// ignore
		}

		public void startDocument() throws SAXException {
			builder.beginDocument();
		}

		public void endDocument() throws SAXException {
			builder.endDocument();
		}

		public void startPrefixMapping(String prefix, String uri) throws SAXException {
			// ignore
		}

		public void endPrefixMapping(String prefix) throws SAXException {
			// ignore
		}

		public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
			// ignore
		}

		public void processingInstruction(String target, String data) throws SAXException {
			// ignore
		}

		public void skippedEntity(String name) throws SAXException {
			// ignore
		}

		private abstract class ElementHandler {

			abstract void start(Attributes atts);

			abstract void end();

			void characters(String s) {
				builder.characters(s);
			}

		}

		private class BlockElementHandler extends ElementHandler {

			private final BlockType blockType;

			private BlockElementHandler(BlockType blockType) {
				this.blockType = blockType;
			}

			public void start(Attributes atts) {
				builder.beginBlock(blockType, computeAttributes(atts));
			}

			public void end() {
				builder.endBlock();
			}

			public void characters(String s) {
				builder.characters(s);
			}

		}

		private class SpanElementHandler extends ElementHandler {

			private final SpanType spanType;

			private SpanElementHandler(SpanType spanType) {
				this.spanType = spanType;
			}

			public void start(Attributes atts) {
				builder.beginSpan(spanType, computeAttributes(spanType, atts));
			}

			public void end() {
				builder.endBlock();
			}

		}

		private class ContentElementHandler extends ElementHandler {

			public void start(Attributes atts) {
				// ignore
			}

			public void end() {
				// ignore
			}
		}

		private class ImageElementHandler extends ElementHandler {

			public void start(Attributes atts) {
				org.eclipse.mylyn.wikitext.core.parser.Attributes attributes = computeAttributes(SpanType.SPAN, atts);
				builder.image(attributes, getValue("src", atts)); //$NON-NLS-1$
			}

			public void end() {
				// nothing to do
			}

		}

		private class PreformattedBlockElementHandler extends ElementHandler {

			private Attributes atts;

			@Override
			void start(Attributes atts) {
				// delay start
				this.atts = new AttributesImpl(atts);
			}

			@Override
			void end() {
				if (atts == null) {
					builder.endBlock();
				}
			}

			@Override
			void characters(String s) {
				if (atts != null) {
					builder.beginBlock(BlockType.PREFORMATTED, computeAttributes(atts));
					atts = null;
				}
				super.characters(s);
			}

		}

		private ElementHandler computeElementHandler(String elementName) {
			elementName = elementName.toLowerCase();

			BlockType blockType = elementNameToBlockType.get(elementName);
			if (blockType == null) {
				SpanType spanType = elementNameToSpanType.get(elementName);
				if (spanType != null) {
					return new SpanElementHandler(spanType);
				}
				if (elementName.equals("img")) { //$NON-NLS-1$
					return new ImageElementHandler();
				}
				return new ContentElementHandler();
			}
			if (blockType == BlockType.CODE && !handlers.isEmpty()) {
				ElementHandler outerHandler = handlers.peek();
				if (!(outerHandler instanceof PreformattedBlockElementHandler)) {
					return new SpanElementHandler(SpanType.CODE);
				}
			}
			if (blockType == BlockType.PREFORMATTED) {
				return new PreformattedBlockElementHandler();
			}
			return new BlockElementHandler(blockType);
		}

	}

	/**
	 * parse well-formed XHTML from the given input, and emit an approximation of the source document to the given
	 * document builder
	 * 
	 * @param input
	 *            the source input
	 * @param builder
	 *            the builder to which output is provided
	 * @throws IOException
	 * @throws SAXException
	 */
	public void parse(InputSource input, DocumentBuilder builder) throws IOException, SAXException {
		if (input == null) {
			throw new IllegalArgumentException();
		}
		if (builder == null) {
			throw new IllegalArgumentException();
		}
		XMLReader xmlReader = XMLReaderFactory.createXMLReader();
		xmlReader.setContentHandler(new DocumentBuilderAdapter(builder));

		Reader reader = input.getCharacterStream();
		if (reader == null) {
			final InputStream in = input.getByteStream();
			if (in == null) {
				throw new IllegalArgumentException("input must provide a byte stream or a character stream"); //$NON-NLS-1$
			}
			reader = new InputStreamReader(in, input.getEncoding() == null ? "utf-8" : input.getEncoding()); //$NON-NLS-1$
		}
		reader = new ConcatenatingReader(
				new StringReader(
						"<?xml version='1.0'?><!DOCTYPE html [ <!ENTITY nbsp \"&#160;\"> <!ENTITY copy \"&#169;\"> <!ENTITY reg \"&#174;\"> <!ENTITY euro \"&#8364;\"> ]>"), reader); //$NON-NLS-1$

		input = new InputSource(reader);
		xmlReader.parse(input);
	}

	private org.eclipse.mylyn.wikitext.core.parser.Attributes computeAttributes(SpanType spanType, Attributes atts) {
		org.eclipse.mylyn.wikitext.core.parser.Attributes attributes = spanType == SpanType.LINK
				? new LinkAttributes()
				: new org.eclipse.mylyn.wikitext.core.parser.Attributes();
		populateCommonAttributes(attributes, atts);
		if (spanType == SpanType.LINK) {
			String href = getValue("href", atts); //$NON-NLS-1$
			if (href != null) {
				((LinkAttributes) attributes).setHref(href);
			}
		}
		return attributes;
	}

	private String getValue(String name, Attributes atts) {
		for (int x = 0; x < atts.getLength(); ++x) {
			String localName = atts.getLocalName(x);
			if (localName.equals(name)) {
				return atts.getValue(x);
			}
		}
		return null;
	}

	private org.eclipse.mylyn.wikitext.core.parser.Attributes computeAttributes(Attributes atts) {
		org.eclipse.mylyn.wikitext.core.parser.Attributes attributes = new org.eclipse.mylyn.wikitext.core.parser.Attributes();
		populateCommonAttributes(attributes, atts);
		return attributes;
	}

	private void populateCommonAttributes(org.eclipse.mylyn.wikitext.core.parser.Attributes attributes, Attributes atts) {
		for (int x = 0; x < atts.getLength(); ++x) {
			String localName = atts.getLocalName(x);
			if (localName.equals("id") || localName.equals("name")) { //$NON-NLS-1$ //$NON-NLS-2$
				attributes.setId(atts.getValue(x));
			} else if (localName.equals("style")) { //$NON-NLS-1$
				attributes.setCssStyle(atts.getValue(x));
			} else if (localName.equals("class")) { //$NON-NLS-1$
				attributes.setCssClass(atts.getValue(x));
			} else if (localName.equals("title")) { //$NON-NLS-1$
				attributes.setTitle(atts.getValue(x));
			}
		}
	}

}
