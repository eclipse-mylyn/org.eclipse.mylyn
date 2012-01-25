/*******************************************************************************
 * Copyright (c) 2011, 2012 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.core.parser.html;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * @author David Green
 */
public class HtmlParser extends AbstractSaxHtmlParser {

	private static abstract class NodeHandler {

		public abstract void process(Stack<NodeHandler> stack, ContentHandler contentHandler) throws SAXException;

	}

	private static class TextHandler extends NodeHandler {

		private final TextNode node;

		public TextHandler(TextNode node) {
			this.node = node;
		}

		@Override
		public void process(java.util.Stack<NodeHandler> stack, ContentHandler contentHandler) throws SAXException {
			String text = isPreserveWhitespace() ? node.getWholeText() : node.text();
			contentHandler.characters(text.toCharArray(), 0, text.length());
		}

		private boolean isPreserveWhitespace() {
			Node parent = node.parent();
			while (parent != null) {
				if (parent.nodeName().equals("pre")) { //$NON-NLS-1$
					return true;
				}
				parent = parent.parent();
			}
			return false;
		}
	}

	private static class ElementHandler extends NodeHandler {

		private final Element element;

		public ElementHandler(Element element) {
			this.element = element;
		}

		@Override
		public void process(Stack<NodeHandler> stack, ContentHandler contentHandler) throws SAXException {
			contentHandler.startElement(null, element.nodeName(), element.nodeName(), computeAttributes());

			stack.push(new EndElementHandler(element));
			List<Node> childNodes = element.childNodes();
			if (!childNodes.isEmpty()) {
				for (int x = childNodes.size() - 1; x >= 0; --x) {
					Node child = childNodes.get(x);
					if (child instanceof Element) {
						stack.push(new ElementHandler((Element) child));
					} else if (child instanceof TextNode) {
						stack.push(new TextHandler((TextNode) child));
					}
				}
			}
		}

		private Attributes computeAttributes() {
			AttributesImpl attributes = new AttributesImpl();
			for (Attribute attr : element.attributes()) {
				attributes.addAttribute(null, attr.getKey(), null, null, attr.getValue());
			}
			return attributes;
		}

	}

	private static class EndElementHandler extends NodeHandler {

		private final Element element;

		public EndElementHandler(Element element) {
			this.element = element;
		}

		@Override
		public void process(Stack<NodeHandler> stack, ContentHandler contentHandler) throws SAXException {
			contentHandler.endElement(null, element.nodeName(), element.nodeName());
		}

	}

	private static class DocumentHandler extends NodeHandler {

		private final Document document;

		public DocumentHandler(Document doc) {
			this.document = doc;
		}

		@Override
		public void process(Stack<NodeHandler> stack, ContentHandler contentHandler) throws SAXException {
			contentHandler.startDocument();

			stack.push(new EndDocumentHandler());

			stack.push(new ElementHandler(document.body()));
		}
	}

	private static class EndDocumentHandler extends NodeHandler {

		public EndDocumentHandler() {
		}

		@Override
		public void process(Stack<NodeHandler> stack, ContentHandler contentHandler) throws SAXException {
			contentHandler.endDocument();

		}
	}

	private List<DocumentProcessor> processors = new ArrayList<DocumentProcessor>();

	@Override
	protected void parse(InputSource input, DocumentBuilder builder, ContentHandler contentHandler) throws IOException,
			SAXException {
		Document document = Jsoup.parse(readContent(input));

		for (DocumentProcessor processor : processors) {
			processor.process(document);
		}

		Stack<NodeHandler> stack = new Stack<NodeHandler>();
		stack.push(new DocumentHandler(document));
		while (!stack.isEmpty()) {
			NodeHandler handler = stack.pop();
			handler.process(stack, contentHandler);
		}
	}

	public List<DocumentProcessor> getProcessors() {
		return processors;
	}

	public void setProcessors(List<DocumentProcessor> processors) {
		this.processors = processors;
	}

	private String readContent(InputSource input) throws IOException {
		Reader reader = input.getCharacterStream();
		try {
			if (reader == null) {
				InputStream bytes = input.getByteStream();
				if (bytes == null) {
					String systemId = input.getSystemId();
					if (systemId != null) {
						bytes = new BufferedInputStream(new FileInputStream(systemId));
					}
					if (bytes == null) {
						throw new IllegalArgumentException();
					}
				}
				reader = new InputStreamReader(bytes, input.getEncoding() == null ? "utf-8" : input.getEncoding()); //$NON-NLS-1$
			}
			StringWriter writer = new StringWriter(2048);
			for (int i = reader.read(); i != -1; i = reader.read()) {
				writer.write(i);
			}
			return writer.toString();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
}
