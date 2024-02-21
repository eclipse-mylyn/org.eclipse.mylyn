/*******************************************************************************
 * Copyright (c) 2011, 2024 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     Alexander Fedorov (ArSysOp) - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.internal.parser.html;

import static java.util.Map.entry;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.LinkAttributes;
import org.eclipse.mylyn.wikitext.parser.ListAttributes;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * A parser for (X)HTML that is based on SAX. Subclasses determine the source of SAX events.
 *
 * @author David Green
 */
public abstract class AbstractSaxHtmlParser {

	private static final Pattern HEADING_PATTERN = Pattern.compile("h([1-6])"); //$NON-NLS-1$

	/**
	 * element names for block elements
	 */
	private static Set<String> blockElements = Set.of("div", //$NON-NLS-1$
			"dl", //$NON-NLS-1$
			"form", //$NON-NLS-1$
			"h1", //$NON-NLS-1$
			"h2", //$NON-NLS-1$
			"h3", //$NON-NLS-1$
			"h4", //$NON-NLS-1$
			"h5", //$NON-NLS-1$
			"h6", //$NON-NLS-1$
			"ol", //$NON-NLS-1$
			"p", //$NON-NLS-1$
			"pre", //$NON-NLS-1$
			"table", //$NON-NLS-1$
			"textarea", //$NON-NLS-1$
			"td", //$NON-NLS-1$
			"tr", //$NON-NLS-1$
			"ul", //$NON-NLS-1$
			"tbody", //$NON-NLS-1$
			"thead", //$NON-NLS-1$
			"tfoot", //$NON-NLS-1$
			"li", //$NON-NLS-1$
			"dd", //$NON-NLS-1$
			"dt", //$NON-NLS-1$
			"blockquote" //$NON-NLS-1$
			);

	/**
	 * element names for elements that cause adjacent whitespace to be collapsed
	 */
	private static Set<String> whitespaceCollapsingElements = Set.of("br", "hr");//$NON-NLS-1$ //$NON-NLS-2$

	private static Set<String> noCharacterContentElements = Set.of("ul", //$NON-NLS-1$
			"ol", //$NON-NLS-1$
			"table", //$NON-NLS-1$
			"tbody", //$NON-NLS-1$
			"thead", //$NON-NLS-1$
			"tr" //$NON-NLS-1$
			);

	private static final Map<String, SpanType> elementNameToSpanType = Map.ofEntries(entry("a", SpanType.LINK), //$NON-NLS-1$
			entry("b", SpanType.BOLD), //$NON-NLS-1$
			entry("cite", SpanType.CITATION), //$NON-NLS-1$
			entry("i", SpanType.ITALIC), //$NON-NLS-1$
			entry("em", SpanType.EMPHASIS), //$NON-NLS-1$
			entry("strong", SpanType.STRONG), //$NON-NLS-1$
			entry("del", SpanType.DELETED), //$NON-NLS-1$
			entry("strike", SpanType.DELETED), //$NON-NLS-1$
			entry("s", SpanType.DELETED), //$NON-NLS-1$
			entry("ins", SpanType.INSERTED), //$NON-NLS-1$
			entry("q", SpanType.QUOTE), //$NON-NLS-1$
			entry("u", SpanType.UNDERLINED), //$NON-NLS-1$
			entry("sup", SpanType.SUPERSCRIPT), //$NON-NLS-1$
			entry("sub", SpanType.SUBSCRIPT), //$NON-NLS-1$
			entry("span", SpanType.SPAN), //$NON-NLS-1$
			entry("font", SpanType.SPAN), //$NON-NLS-1$
			entry("code", SpanType.CODE), //$NON-NLS-1$
			entry("tt", SpanType.MONOSPACE), //$NON-NLS-1$
			entry("mark", SpanType.MARK) //$NON-NLS-1$
			);

	private static final Map<String, BlockType> elementNameToBlockType = Map.ofEntries(
			entry("ul", BlockType.BULLETED_LIST), //$NON-NLS-1$
			entry("code", BlockType.CODE), //$NON-NLS-1$
			entry("div", BlockType.DIV), //$NON-NLS-1$
			entry("footnote", BlockType.FOOTNOTE), //$NON-NLS-1$
			entry("li", BlockType.LIST_ITEM), //$NON-NLS-1$
			entry("ol", BlockType.NUMERIC_LIST), //$NON-NLS-1$
			entry("dl", BlockType.DEFINITION_LIST), //$NON-NLS-1$
			entry("dt", BlockType.DEFINITION_TERM), //$NON-NLS-1$
			entry("dd", BlockType.DEFINITION_ITEM), //$NON-NLS-1$
			entry("p", BlockType.PARAGRAPH), //$NON-NLS-1$
			entry("pre", BlockType.PREFORMATTED), //$NON-NLS-1$
			entry("blockquote", BlockType.QUOTE), //$NON-NLS-1$
			entry("table", BlockType.TABLE), //$NON-NLS-1$
			entry("th", BlockType.TABLE_CELL_HEADER), //$NON-NLS-1$
			entry("td", BlockType.TABLE_CELL_NORMAL), //$NON-NLS-1$
			entry("tr", BlockType.TABLE_ROW) //$NON-NLS-1$
			);

	private static final class ElementState {
		@SuppressWarnings("unused")
		final String elementName;

		final ElementState parent;

		final boolean preserveWhitespace;

		final boolean blockElement;

		final boolean noWhitespaceTextContainer;

		final boolean collapsesAdjacentWhitespace;

		final boolean canHaveCharacterContent;

		private ElementState(ElementState parent, String elementName) {
			this.parent = parent;
			this.elementName = elementName;
			blockElement = blockElements.contains(elementName);
			collapsesAdjacentWhitespace = blockElement || whitespaceCollapsingElements.contains(elementName);
			noWhitespaceTextContainer = "body".equals(elementName); //$NON-NLS-1$
			preserveWhitespace = parent != null && parent.preserveWhitespace || "pre".equals(elementName); //$NON-NLS-1$
			canHaveCharacterContent = !noCharacterContentElements.contains(elementName);
			if (parent != null) {
				parent.lastChild = this;
			}
		}

		int childCount = 0;

		int textChildCount = 0;

		/**
		 * The last child that was just processed for this element
		 */
		ElementState lastChild;

	}

	private class DocumentBuilderAdapter implements ContentHandler {

		private final Stack<ElementHandler> handlers = new Stack<>();

		private ElementState elementState;

		private final DocumentBuilder builder;

		private boolean processingContent;

		private final StringBuilder elementText = new StringBuilder();

		private final boolean asDocument;

		public DocumentBuilderAdapter(DocumentBuilder builder, boolean asDocument) {
			this.builder = builder;
			this.asDocument = asDocument;
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
			final String lowerCaseName = localName.toLowerCase();
			if (processingContent) {
				emitText(elementState, lowerCaseName, false);
			}
			elementState = new ElementState(elementState, lowerCaseName);

			if (!processingContent) {
				if (lowerCaseName.equals("body")) { //$NON-NLS-1$
					processingContent = true;
				}
			}

			if (elementState.parent != null) {
				++elementState.parent.childCount;
			}

			ElementHandler handler = computeElementHandler(lowerCaseName);
			handlers.push(handler);
			handler.start(atts);
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			emitText(elementState, null, true);

			final String lowerCaseName = localName.toLowerCase();
			elementState = elementState.parent;

			if (processingContent) {
				if (lowerCaseName.equals("body")) { //$NON-NLS-1$
					processingContent = false;
				}
			}
			if (!processingContent) {
				return;
			}
			ElementHandler handler = handlers.pop();
			handler.end();
		}

		private void emitText(ElementState elementState, String nextElementName, boolean elementClosing) {
			String text = elementText.toString();
			if (!elementState.canHaveCharacterContent) {
				text = text.trim();
			} else if (!elementState.preserveWhitespace) {
				if (elementClosing) {
					if (elementState.blockElement) {
						if (elementState.childCount == 0) {
							text = text.trim();
						} else {
							text = trimRight(text);
						}
					}
				} else {
					// careful: this can result in losing significant whitespace
					String originalText = text;
					if (elementState.blockElement && elementState.childCount == 0) {
						text = trimLeft(text);
						if (text.length() == 0 && originalText.length() > 0) {
							text = originalText.substring(0, 1);
						}
					}
					if (nextElementName != null && blockElements.contains(nextElementName)) {
						text = trimRight(text);
					}
				}
			}

			elementText.delete(0, elementText.length());
			if (text.length() > 0) {
				handlers.peek().characters(text);
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			if (processingContent) {
				if (elementState.noWhitespaceTextContainer
						&& (elementState.lastChild == null || elementState.lastChild.blockElement)
						|| elementState.blockElement && !elementState.preserveWhitespace
						&& elementState.textChildCount == 0 && elementState.childCount == 0
						|| elementState.lastChild != null && elementState.lastChild.collapsesAdjacentWhitespace) {
					// trim left here
					int skip = 0;
					while (skip < length && Character.isWhitespace(ch[start + skip])) {
						++skip;
					}
					start += skip;
					length -= skip;
				}
				// receiving characters makes the last element child irrelevant
				elementState.lastChild = null;

				if (length != 0) {
					++elementState.textChildCount;
					append(elementState, ch, start, length);
				}
			}
		}

		private void append(ElementState elementState, char[] ch, int start, int length) {
			if (!elementState.preserveWhitespace) {
				// collapse adjacent whitespace, and replace newlines with a space character
				int previousWhitespaceIndex = Integer.MIN_VALUE;
				for (int x = 0; x < length; ++x) {
					int index = start + x;
					char c = ch[index];
					// preserve ideographic whitespace U+3000
					if (Character.isWhitespace(c) && c != '\u3000') {
						if (previousWhitespaceIndex == index - 1) {
							previousWhitespaceIndex = index;
							continue;
						}
						previousWhitespaceIndex = index;
						elementText.append(c == '\t' ? c : ' ');
					} else {
						elementText.append(c);
					}
				}
			} else {
				elementText.append(ch, start, length);
			}
		}

		@Override
		public void setDocumentLocator(Locator locator) {
			// ignore
		}

		@Override
		public void startDocument() throws SAXException {
			if (asDocument) {
				builder.beginDocument();
			}
		}

		@Override
		public void endDocument() throws SAXException {
			if (asDocument) {
				builder.endDocument();
			} else {
				builder.flush();
			}
		}

		@Override
		public void startPrefixMapping(String prefix, String uri) throws SAXException {
			// ignore
		}

		@Override
		public void endPrefixMapping(String prefix) throws SAXException {
			// ignore
		}

		@Override
		public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
			if (processingContent && elementState.preserveWhitespace) {
				characters(ch, start, length);
			}
		}

		@Override
		public void processingInstruction(String target, String data) throws SAXException {
			// ignore
		}

		@Override
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

			BlockElementHandler(BlockType blockType) {
				this.blockType = blockType;
			}

			@Override
			public void start(Attributes atts) {
				builder.beginBlock(blockType, computeAttributes(atts));
			}

			@Override
			public void end() {
				builder.endBlock();
			}

			@Override
			public void characters(String s) {
				builder.characters(s);
			}

		}

		private class NumericListElementHandler extends BlockElementHandler {

			NumericListElementHandler() {
				super(BlockType.NUMERIC_LIST);
			}

			@Override
			public void start(Attributes atts) {
				ListAttributes listAttributes = new ListAttributes();
				populateCommonAttributes(listAttributes, atts);
				listAttributes.setStart(getAttribute(atts, "start")); //$NON-NLS-1$
				if (listAttributes.getCssStyle() == null
						|| !listAttributes.getCssStyle().contains("list-style-type:")) { //$NON-NLS-1$
					String typeAttribute = getAttribute(atts, "type"); //$NON-NLS-1$
					if (typeAttribute != null) {
						String listCssType = null;
						switch (typeAttribute) {
							case "1": //$NON-NLS-1$
								listCssType = "decimal"; //$NON-NLS-1$
								break;
							case "a": //$NON-NLS-1$
								listCssType = "lower-alpha"; //$NON-NLS-1$
								break;
							case "i": //$NON-NLS-1$
								listCssType = "lower-roman"; //$NON-NLS-1$
								break;
							case "A": //$NON-NLS-1$
								listCssType = "upper-alpha"; //$NON-NLS-1$
								break;
							case "I": //$NON-NLS-1$
								listCssType = "upper-roman"; //$NON-NLS-1$
								break;
						}
						listAttributes.appendCssStyle("list-style-type: " + listCssType + ";"); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
				builder.beginBlock(BlockType.NUMERIC_LIST, listAttributes);
			}
		}

		private class SpanElementHandler extends ElementHandler {

			private final SpanType spanType;

			private boolean noop;

			private SpanElementHandler(SpanType spanType) {
				this.spanType = spanType;
			}

			@Override
			public void start(Attributes atts) {
				org.eclipse.mylyn.wikitext.parser.Attributes attributes = computeAttributes(spanType, atts);
				if (spanType == SpanType.SPAN && attributes.getCssClass() == null && attributes.getCssStyle() == null
						&& attributes.getId() == null) {
					noop = true;
				} else {
					builder.beginSpan(spanType, attributes);
				}
			}

			@Override
			public void end() {
				if (!noop) {
					builder.endSpan();
				}
			}

		}

		private class FontElementHandler extends ElementHandler {

			private final SpanType spanType;

			private boolean noop;

			private FontElementHandler(SpanType spanType) {
				this.spanType = spanType;
			}

			@Override
			public void start(Attributes atts) {
				org.eclipse.mylyn.wikitext.parser.Attributes attributes = computeFontAttributes(atts);
				if (spanType == SpanType.SPAN && attributes.getCssClass() == null && attributes.getCssStyle() == null
						&& attributes.getId() == null) {
					noop = true;
				} else {
					builder.beginSpan(spanType, attributes);
				}
			}

			@Override
			public void end() {
				if (!noop) {
					builder.endSpan();
				}
			}

			private org.eclipse.mylyn.wikitext.parser.Attributes computeFontAttributes(Attributes atts) {
				org.eclipse.mylyn.wikitext.parser.Attributes attributes = computeAttributes(spanType, atts);
				for (int x = 0; x < atts.getLength(); ++x) {
					String localName = atts.getLocalName(x);
					if (localName.equals("face")) { //$NON-NLS-1$
						attributes.appendCssStyle("font-family: " + atts.getValue(x) + ";"); //$NON-NLS-1$ //$NON-NLS-2$
					} else if (localName.equals("size")) { //$NON-NLS-1$
						attributes.appendCssStyle("font-size: " + atts.getValue(x) + ";"); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
				return attributes;
			}
		}

		private class ContentElementHandler extends ElementHandler {

			@Override
			public void start(Attributes atts) {
				// ignore
			}

			@Override
			public void end() {
				// ignore
			}
		}

		private class HeadingElementHandler extends ElementHandler {

			int level;

			private HeadingElementHandler(int level) {
				this.level = level;
			}

			@Override
			void start(Attributes atts) {
				builder.beginHeading(level, computeAttributes(atts));
			}

			@Override
			void end() {
				builder.endHeading();
			}

		}

		private class ImageElementHandler extends ElementHandler {

			@Override
			public void start(Attributes atts) {
				org.eclipse.mylyn.wikitext.parser.Attributes attributes = computeAttributes(SpanType.SPAN, atts);
				builder.image(attributes, getValue("src", atts)); //$NON-NLS-1$
			}

			@Override
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

		private class LineBreakHandler extends ElementHandler {

			@Override
			public void start(Attributes atts) {
				// ignore
			}

			@Override
			public void end() {
				builder.lineBreak();
			}
		}

		private class HorizontalRuleHandler extends ElementHandler {

			@Override
			public void start(Attributes atts) {
				// ignore
			}

			@Override
			public void end() {
				builder.horizontalRule();
			}
		}

		private ElementHandler computeElementHandler(String elementName) {

			BlockType blockType = elementNameToBlockType.get(elementName);
			if (blockType == null) {
				SpanType spanType = elementNameToSpanType.get(elementName);
				if (spanType != null) {
					if (elementName.equals("font")) { //$NON-NLS-1$
						return new FontElementHandler(spanType);
					}
					return new SpanElementHandler(spanType);
				}
				if (elementName.equals("img")) { //$NON-NLS-1$
					return new ImageElementHandler();
				}
				if (elementName.equals("br")) { //$NON-NLS-1$
					return new LineBreakHandler();
				}
				if (elementName.equals("hr")) { //$NON-NLS-1$
					return new HorizontalRuleHandler();
				}
				Matcher headingMatcher = HEADING_PATTERN.matcher(elementName);
				if (headingMatcher.matches()) {
					return new HeadingElementHandler(Integer.parseInt(headingMatcher.group(1)));
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
			if (blockType == BlockType.NUMERIC_LIST) {
				return new NumericListElementHandler();
			}
			return new BlockElementHandler(blockType);
		}

	}

	private org.eclipse.mylyn.wikitext.parser.Attributes computeAttributes(SpanType spanType, Attributes atts) {
		org.eclipse.mylyn.wikitext.parser.Attributes attributes = spanType == SpanType.LINK
				? new LinkAttributes()
						: new org.eclipse.mylyn.wikitext.parser.Attributes();
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

	private org.eclipse.mylyn.wikitext.parser.Attributes computeAttributes(Attributes atts) {
		org.eclipse.mylyn.wikitext.parser.Attributes attributes = new org.eclipse.mylyn.wikitext.parser.Attributes();
		populateCommonAttributes(attributes, atts);
		return attributes;
	}

	private String getAttribute(Attributes atts, String name) {
		for (int x = 0; x < atts.getLength(); ++x) {
			String localName = atts.getLocalName(x);
			if (name.equals(localName)) {
				return atts.getValue(x);
			}
		}
		return null;
	}

	private void populateCommonAttributes(org.eclipse.mylyn.wikitext.parser.Attributes attributes, Attributes atts) {
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
			} else if (localName.equals("color")) { //$NON-NLS-1$
				attributes.appendCssStyle("color: " + atts.getValue(x) + ";"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}

	private static String trimRight(String text) {
		int len = text.length();

		while (0 < len && text.charAt(len - 1) <= ' ') {
			len--;
		}
		return len < text.length() ? text.substring(0, len) : text;
	}

	private static String trimLeft(String text) {
		final int len = text.length();
		int st = 0;

		while (st < len && text.charAt(st) <= ' ') {
			st++;
		}
		return st > 0 ? text.substring(st, len) : text;
	}

	protected abstract void parse(InputSource input, DocumentBuilder builder, ContentHandler contentHandler)
			throws IOException, SAXException;

	/**
	 * @param input
	 * @param builder
	 * @throws IOException
	 * @throws SAXException
	 */
	public void parse(InputSource input, DocumentBuilder builder, boolean asDocument) throws IOException, SAXException {
		parse(input, builder, createContentHandler(builder, asDocument));
	}

	protected ContentHandler createContentHandler(DocumentBuilder builder) {
		return createContentHandler(builder, true);
	}

	/**
	 *
	 */
	protected ContentHandler createContentHandler(DocumentBuilder builder, boolean asDocument) {
		return new DocumentBuilderAdapter(builder, asDocument);
	}

}
