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

package org.eclipse.mylyn.internal.wikitext.core.parser.html;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.core.parser.LinkAttributes;
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
	private static Set<String> blockElements = new HashSet<String>();

	/**
	 * element names for elements that cause adjacent whitespace to be collapsed
	 */
	private static Set<String> whitespaceCollapsingElements = new HashSet<String>();
	static {
		blockElements.add("div"); //$NON-NLS-1$
		blockElements.add("dl"); //$NON-NLS-1$
		blockElements.add("form"); //$NON-NLS-1$
		blockElements.add("h1"); //$NON-NLS-1$
		blockElements.add("h2"); //$NON-NLS-1$
		blockElements.add("h3"); //$NON-NLS-1$
		blockElements.add("h4"); //$NON-NLS-1$
		blockElements.add("h5"); //$NON-NLS-1$
		blockElements.add("h6"); //$NON-NLS-1$
		blockElements.add("ol"); //$NON-NLS-1$
		blockElements.add("p"); //$NON-NLS-1$
		blockElements.add("pre"); //$NON-NLS-1$
		blockElements.add("table"); //$NON-NLS-1$
		blockElements.add("textarea"); //$NON-NLS-1$
		blockElements.add("td"); //$NON-NLS-1$
		blockElements.add("tr"); //$NON-NLS-1$
		blockElements.add("ul"); //$NON-NLS-1$
		blockElements.add("tbody"); //$NON-NLS-1$
		blockElements.add("thead"); //$NON-NLS-1$
		blockElements.add("tfoot"); //$NON-NLS-1$
		blockElements.add("li"); //$NON-NLS-1$
		blockElements.add("dd"); //$NON-NLS-1$
		blockElements.add("dt"); //$NON-NLS-1$

		whitespaceCollapsingElements.add("br"); //$NON-NLS-1$
		whitespaceCollapsingElements.add("hr"); //$NON-NLS-1$
	}

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
		elementNameToSpanType.put("font", SpanType.SPAN); //$NON-NLS-1$
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

	private static final class ElementState {
		@SuppressWarnings("unused")
		final String elementName;

		final ElementState parent;

		final boolean preserveWhitespace;

		final boolean blockElement;

		final boolean noWhitespaceTextContainer;

		final boolean collapsesAdjacentWhitespace;

		private ElementState(ElementState parent, String elementName) {
			this.parent = parent;
			this.elementName = elementName;
			blockElement = blockElements.contains(elementName);
			collapsesAdjacentWhitespace = whitespaceCollapsingElements.contains(elementName);
			noWhitespaceTextContainer = "body".equals(elementName); //$NON-NLS-1$
			preserveWhitespace = (parent != null && parent.preserveWhitespace) || "pre".equals(elementName); //$NON-NLS-1$
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

		private final Stack<ElementHandler> handlers = new Stack<ElementHandler>();

		private ElementState elementState;

		private final DocumentBuilder builder;

		private boolean processingContent;

		private final StringBuilder elementText = new StringBuilder();

		public DocumentBuilderAdapter(DocumentBuilder builder) {
			this.builder = builder;
		}

		public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
			if (processingContent) {
				emitText(elementState, false);
			}

			final String lowerCaseName = localName.toLowerCase();
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

		public void endElement(String uri, String localName, String qName) throws SAXException {
			emitText(elementState, true);

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

		private void emitText(ElementState elementState, boolean elementClosing) {
			String text = elementText.toString();
			if (!elementState.preserveWhitespace) {
				if (elementClosing) {
					if (elementState.childCount == 0) {
						if (elementState.blockElement) {
							text = text.trim();
						}
					} else {
						if (elementState.blockElement) {
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
				}
			}
			elementText.delete(0, elementText.length());
			if (text.length() > 0) {
				handlers.peek().characters(text);
			}
		}

		public void characters(char[] ch, int start, int length) throws SAXException {
			if (processingContent) {
				if ((elementState.noWhitespaceTextContainer && (elementState.lastChild == null || elementState.lastChild.blockElement))
						|| (elementState.blockElement && !elementState.preserveWhitespace
								&& elementState.textChildCount == 0 && elementState.childCount == 0)
						|| (elementState.lastChild != null && elementState.lastChild.collapsesAdjacentWhitespace)) {
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
					if (Character.isWhitespace(c)) {
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
			if (processingContent && elementState.preserveWhitespace) {
				characters(ch, start, length);
			}
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

		private class SpanElementHandler extends ElementHandler {

			private final SpanType spanType;

			private boolean noop;

			private SpanElementHandler(SpanType spanType) {
				this.spanType = spanType;
			}

			@Override
			public void start(Attributes atts) {
				org.eclipse.mylyn.wikitext.core.parser.Attributes attributes = computeAttributes(spanType, atts);
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
				org.eclipse.mylyn.wikitext.core.parser.Attributes attributes = computeAttributes(SpanType.SPAN, atts);
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

		private ElementHandler computeElementHandler(String elementName) {

			BlockType blockType = elementNameToBlockType.get(elementName);
			if (blockType == null) {
				SpanType spanType = elementNameToSpanType.get(elementName);
				if (spanType != null) {
					return new SpanElementHandler(spanType);
				}
				if (elementName.equals("img")) { //$NON-NLS-1$
					return new ImageElementHandler();
				}
				if (elementName.equals("br")) { //$NON-NLS-1$
					return new LineBreakHandler();
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
			return new BlockElementHandler(blockType);
		}

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
			} else if (localName.equals("color")) { //$NON-NLS-1$
				attributes.appendCssStyle("color: " + atts.getValue(x) + ";"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}

	private static String trimRight(String text) {
		int len = text.length();

		while (0 < len && (text.charAt(len - 1) <= ' ')) {
			len--;
		}
		return len < text.length() ? text.substring(0, len) : text;
	}

	private static String trimLeft(String text) {
		final int len = text.length();
		int st = 0;

		while ((st < len) && (text.charAt(st) <= ' ')) {
			st++;
		}
		return st > 0 ? text.substring(st, len) : text;
	}

	protected abstract void parse(InputSource input, DocumentBuilder builder, ContentHandler contentHandler)
			throws IOException, SAXException;

	/**
	 * parse HTML from the given input, and emit an approximation of the source document to the given document builder
	 * 
	 * @param input
	 *            the source input
	 * @param builder
	 *            the builder to which output is provided
	 * @throws IOException
	 * @throws SAXException
	 */
	public void parse(InputSource input, DocumentBuilder builder) throws IOException, SAXException {
		parse(input, builder, createContentHandler(builder));
	}

	protected ContentHandler createContentHandler(DocumentBuilder builder) {
		return new DocumentBuilderAdapter(builder);
	}

}
