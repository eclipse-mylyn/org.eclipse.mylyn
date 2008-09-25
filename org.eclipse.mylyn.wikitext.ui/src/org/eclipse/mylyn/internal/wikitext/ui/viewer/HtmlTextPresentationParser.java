/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.ui.viewer;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.mylyn.internal.wikitext.ui.util.ImageCache;
import org.eclipse.mylyn.internal.wikitext.ui.viewer.annotation.BulletAnnotation;
import org.eclipse.mylyn.internal.wikitext.ui.viewer.annotation.HorizontalRuleAnnotation;
import org.eclipse.mylyn.internal.wikitext.ui.viewer.annotation.ImageAnnotation;
import org.eclipse.mylyn.wikitext.core.util.IgnoreDtdEntityResolver;
import org.eclipse.mylyn.wikitext.ui.annotation.AnchorHrefAnnotation;
import org.eclipse.mylyn.wikitext.ui.annotation.AnchorNameAnnotation;
import org.eclipse.mylyn.wikitext.ui.annotation.ClassAnnotation;
import org.eclipse.mylyn.wikitext.ui.annotation.IdAnnotation;
import org.eclipse.mylyn.wikitext.ui.annotation.TitleAnnotation;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Takes a valid XHTML document and converts the document into two distinct parts:
 * <ol>
 * <li>Text as it should be presented in a text viewer</li>
 * <li>A {@link TextPresentation}</li>
 * </ol>
 * 
 * @author David Green
 * 
 */
public class HtmlTextPresentationParser {
	/**
	 * Element names for spanning elements
	 */
	private static Set<String> spanElements = new HashSet<String>();

	/**
	 * element names for block elements
	 */
	private static Set<String> blockElements = new HashSet<String>();

	/**
	 * element names for elements that cause adjacent whitespace to be collapsed
	 */
	private static Set<String> whitespaceCollapsingElements = new HashSet<String>();

	static {
		spanElements.add("a");
		spanElements.add("abbr");
		spanElements.add("acronym");
		spanElements.add("b");
		spanElements.add("big");
		spanElements.add("blink");
		spanElements.add("cite");
		spanElements.add("code");
		spanElements.add("del");
		spanElements.add("dfn");
		spanElements.add("em");
		spanElements.add("font");
		spanElements.add("i");
		spanElements.add("img");
		spanElements.add("ins");
		spanElements.add("label");
		spanElements.add("q");
		spanElements.add("s");
		spanElements.add("samp");
		spanElements.add("small");
		spanElements.add("span");
		spanElements.add("strike");
		spanElements.add("strong");
		spanElements.add("sub");
		spanElements.add("sup");
		spanElements.add("tt");
		spanElements.add("u");
		spanElements.add("var");

		blockElements.add("div");
		blockElements.add("dl");
		blockElements.add("form");
		blockElements.add("h1");
		blockElements.add("h2");
		blockElements.add("h3");
		blockElements.add("h4");
		blockElements.add("h5");
		blockElements.add("h6");
		blockElements.add("ol");
		blockElements.add("p");
		blockElements.add("pre");
		blockElements.add("table");
		blockElements.add("textarea");
		blockElements.add("td");
		blockElements.add("tr");
		blockElements.add("ul");
		blockElements.add("tbody");
		blockElements.add("thead");
		blockElements.add("tfoot");
		blockElements.add("li");
		blockElements.add("dd");
		blockElements.add("dt");

		whitespaceCollapsingElements.add("br");
		whitespaceCollapsingElements.add("hr");
	}

	private static class ElementState {
		String elementName;

		int childCount = 0;

		int textChildCount = 0;

		final int originalOffset;

		int offset;

		boolean skipWhitespace = true;

		boolean spanElement;

		boolean blockElement;

		boolean noWhitespaceTextContainer;

		boolean collapsesAdjacentWhitespace;

		final FontState fontState;

		int orderedListIndex = 0;

		int indentLevel = 0;

		List<Annotation> annotations;

		char[] prefix;

		List<Annotation> prefixAnnotations;

		/**
		 * The last child that was just processed for this element
		 */
		ElementState lastChild;

		final ElementState parent;

		public ElementState(ElementState parent, String elementName, ElementState elementState, int offset) {
			this.parent = parent;
			this.elementName = elementName;
			this.fontState = new FontState(elementState.fontState);
			this.offset = offset;
			this.originalOffset = offset;
			this.skipWhitespace = elementState.skipWhitespace;
			this.indentLevel = elementState.indentLevel;
			initState();
		}

		public ElementState(ElementState parent, String elementName, FontState fontState, int offset) {
			this.parent = parent;
			this.elementName = elementName;
			this.fontState = new FontState(fontState);
			this.offset = offset;
			this.originalOffset = offset;
			initState();
		}

		private void initState() {
			String elementName = this.elementName.toLowerCase();
			spanElement = spanElements.contains(elementName);
			blockElement = blockElements.contains(elementName);
			collapsesAdjacentWhitespace = whitespaceCollapsingElements.contains(elementName);
			noWhitespaceTextContainer = "body".equals(elementName);
		}

		public void addAnnotation(Annotation annotation) {
			if (annotations == null) {
				annotations = new ArrayList<Annotation>(2);
			}
			annotations.add(annotation);
		}

		public void addPrefixAnnotation(Annotation annotation) {
			if (prefixAnnotations == null) {
				prefixAnnotations = new ArrayList<Annotation>(1);
			}
			prefixAnnotations.add(annotation);
		}
	}

	private AnnotationModel annotationModel;

	private TextPresentation presentation;

	private String text;

	private Font defaultFont;

	private Color defaultForeground;

	private Color defaultBackground;

	private char[] bulletChars = new char[] { '\u2022', // solid round bullet, see http://www.fileformat.info/info/unicode/char/2022/index.htm
//		'\u26AA', // empty round bullet, see http://www.fileformat.info/info/unicode/char/26AA/index.htm
//		'\u25A0', // square bullet, see http://www.fileformat.info/info/unicode/char/25A0/index.htm
	};

	private CssStyleManager cssStyleManager;

	private boolean enableImages = false;

	private ImageCache imageCache = new ImageCache();

	public HtmlTextPresentationParser() {
	}

	public TextPresentation getPresentation() {
		return presentation;
	}

	public void setPresentation(TextPresentation presentation) {
		this.presentation = presentation;
		if (presentation != null && presentation.getDefaultStyleRange() != null) {
			if (presentation.getDefaultStyleRange().font != null) {
				this.defaultFont = presentation.getDefaultStyleRange().font;
			}
			if (presentation.getDefaultStyleRange().foreground != null) {
				this.defaultForeground = presentation.getDefaultStyleRange().foreground;
			}
			if (presentation.getDefaultStyleRange().foreground != null) {
				this.defaultForeground = presentation.getDefaultStyleRange().background;
			}
		}
	}

	public String getText() {
		return text;
	}

	public Font getDefaultFont() {
		return defaultFont;
	}

	public void setDefaultFont(Font defaultFont) {
		this.defaultFont = defaultFont;
	}

	public Color getDefaultForeground() {
		return defaultForeground;
	}

	public void setDefaultForeground(Color defaultForeground) {
		this.defaultForeground = defaultForeground;
	}

	public Color getDefaultBackground() {
		return defaultBackground;
	}

	public void setDefaultBackground(Color defaultBackground) {
		this.defaultBackground = defaultBackground;
	}

	/**
	 * Get the annotation model in which annotations are collected
	 * 
	 * @return the annotation model, or null if there is none.
	 */
	public AnnotationModel getAnnotationModel() {
		return annotationModel;
	}

	/**
	 * Set the annotation model if the parsing process should collect annotations for things like anchors and hover
	 * info.
	 * 
	 * @param annotationModel
	 *            the annotation model, or null if annotations should not be collected.
	 */
	public void setAnnotationModel(AnnotationModel annotationModel) {
		this.annotationModel = annotationModel;
	}

	/**
	 * The maximum width in pixels. Only used when {@link #setGC(GC) a GC is provided}.
	 */
	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}

	/**
	 * The GC, to be used in conjunction with {@link #setMaxWidth(int) the maximum width}
	 */
	public void setGC(GC gc) {
		this.gc = gc;
	}

	public void parse(String xhtmlContent) throws SAXException, IOException {
		parse(new InputSource(new StringReader(xhtmlContent)));
	}

	public void parse(InputSource xhtmlInput) throws SAXException, IOException {
		if (presentation == null) {
			throw new IllegalStateException("Must specify presentation");
		}
		if (defaultFont == null) {
			throw new IllegalStateException("Must specify defaultFont");
		}

		cssStyleManager = new CssStyleManager(defaultFont);

		SAXParserFactory factory = SAXParserFactory.newInstance();

		factory.setNamespaceAware(true);
		factory.setValidating(false);
		SAXParser saxParser;
		try {
			saxParser = factory.newSAXParser();
		} catch (ParserConfigurationException e) {
			throw new IllegalStateException(e);
		}
		XMLReader parser = saxParser.getXMLReader();
		parser.setEntityResolver(IgnoreDtdEntityResolver.getInstance());
		parser.setContentHandler(new HtmlContentHandler());
		parser.parse(xhtmlInput);
	}

	private class HtmlContentHandler implements ContentHandler {

		private final Stack<ElementState> state = new Stack<ElementState>();

		private int lastNewlineOffset = 0;

		private final StringBuilder out = new StringBuilder(2048);

		private final List<StyleRange> styleRanges = new ArrayList<StyleRange>();

		private final Map<Annotation, Position> annotationToPosition = new IdentityHashMap<Annotation, Position>();

		private final StringBuilder elementText = new StringBuilder();

		public void characters(char[] ch, int start, int length) throws SAXException {
			if (!state.isEmpty()) {
				ElementState elementState = state.peek();
				if (elementState.noWhitespaceTextContainer
						|| (elementState.blockElement && elementState.skipWhitespace
								&& elementState.textChildCount == 0 && elementState.childCount == 0)
						|| (elementState.lastChild != null && elementState.lastChild.collapsesAdjacentWhitespace)) {
					// trim left here, since we must properly eliminate whitespace in ordered lists where we've already
					// prepended a number to the list item text
					int skip = 0;
					while (Character.isWhitespace(ch[start + skip]) && skip < length) {
						++skip;
					}
					start += skip;
					length -= skip;
				}
				if (length != 0) {
					++elementState.textChildCount;
					append(elementState, ch, start, length);
				}
			}
		}

		private void append(ElementState elementState, char[] ch, int start, int length) {
			if (elementState.skipWhitespace) {
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

		public void emitText(ElementState elementState, boolean elementClosing) {
			if (state.isEmpty() || elementText.length() == 0) {
				return;
			}

			String text = elementText.toString();
			if (elementState.skipWhitespace) {
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
				emitChars(elementState, text.toCharArray());
			}
		}

		private void emitChar(char c) {
			out.append(c);
			lastNewlineOffset = getOffset();
		}

		private void emitChars(ElementState elementState, char[] chars) {
			int indentLevel = elementState.indentLevel;

			boolean enforceMaxWidth = maxWidth > 0 && gc != null;

			// find the offset of the last natural break
			int lastBreakPosition = lastNewlineOffset + 1;
			if (enforceMaxWidth) {
				for (int x = out.length() - 1; x >= 0; --x) {
					char ch = out.charAt(x);
					if (x == (lastNewlineOffset + 1)) {
						break;
					}

					if (ch == '-') {
						lastBreakPosition = x + 1;
						break;
					} else if (Character.isWhitespace(ch)) {
						lastBreakPosition = x;
						break;
					}

				}
			}

			for (int x = 0; x < chars.length; ++x) {
				char c = chars[x];
				if (lastNewlineOffset == getOffset() && (c != '\n' && c != '\r') && indentLevel > 0) {
					for (int y = 0; y < indentLevel; ++y) {
						out.append('\t');
					}
				}
				if (x == 0) {
					char[] prefix = computePrefix(elementState);
					if (prefix != null) {
						int offset = out.length();
						for (int i = 0; i < prefix.length; ++i) {
							out.append(prefix[i]);
						}
						List<Annotation> prefixAnnotations = computePrefixAnnotations(elementState);
						if (prefixAnnotations != null && annotationModel != null) {
							for (Annotation annotation : prefixAnnotations) {
								annotationModel.addAnnotation(annotation, new Position(offset, 1));
							}
						}
					}
				}
				out.append(c);

				if (c == '-') {
					lastBreakPosition = getOffset() + 1;
				} else if (Character.isWhitespace(c)) {
					lastBreakPosition = getOffset();
				}

				if (c == '\n') {
					lastNewlineOffset = getOffset();
					lastBreakPosition = getOffset() + 1;
				} else if (enforceMaxWidth) {
					Point extent = gc.textExtent(out.substring(lastNewlineOffset + 1, out.length()));
					final int rightMargin = 2;
					if (extent.x >= (maxWidth - rightMargin)) {
						if (lastBreakPosition <= getOffset()) {
							out.insert(lastBreakPosition, '\n');
							lastNewlineOffset = lastBreakPosition;
							lastBreakPosition = lastNewlineOffset + 1;
						} else {
							out.append('\n');
							lastNewlineOffset = getOffset();
							lastBreakPosition = getOffset() + 1;
						}
					}
				}
			}
		}

		public void endElement(String uri, String localName, String name) throws SAXException {
			ElementState elementState = state.peek();

			emitText(elementState, true);
			emitStyles();

			if (elementState.annotations != null) {
				for (Annotation annotation : elementState.annotations) {
					annotationToPosition.put(annotation, new Position(elementState.originalOffset, getOffset()
							- elementState.originalOffset));
				}
			}

			char[] ch = elementToCharacters.get(localName.toLowerCase());
			if (ch != null) {
				boolean skip = false;
				if (state.size() > 1) {
					if (elementState.elementName.equals("ul") || elementState.elementName.equals("ol")
							|| elementState.elementName.equals("dl")) {
						ElementState parentState = state.get(state.size() - 2);
						if (parentState.elementName.equals("li") || parentState.elementName.equals("dt")
								|| parentState.elementName.equals("dd")) {
							skip = true;
						}
					}
				}
				if (!skip) {
					emitPartial(elementState, ch);
				}
			}

			ElementState lastChild = state.pop();

			if (localName.equals("hr")) {
				emitChar('\n');
			} else if (localName.equals("img")) {
				if (enableImages) {
					for (Annotation annotation : elementState.annotations) {
						if (annotation instanceof ImageAnnotation) {
							ImageAnnotation imageAnnotation = (ImageAnnotation) annotation;
							// ensure that the image is painted on a new line
							if (out.length() > 0) {
								char lastChar = out.charAt(out.length() - 1);
								if (lastChar != '\n' && lastChar != '\r') {
									emitChar('\n');
									Position position = annotationToPosition.get(imageAnnotation);
									annotationToPosition.put(imageAnnotation, new Position(position.getOffset() + 1,
											position.getLength()));
								}
							}
							if (imageAnnotation.getImage() != null) {
								// ensure that there are enough blank lines to display
								// the image
								int height = imageAnnotation.getImage().getBounds().height;
								gc.setFont(defaultFont);
								Point extent = gc.textExtent("\n");
								if (extent.y > 0) {
									int numNewlines = (int) Math.ceil(((double) height) / ((double) extent.y));
									for (int x = 0; x < numNewlines && x < 1000; ++x) {
										emitChar('\n');
									}
								}
							}
						}
					}
				}
			}

			if (!state.isEmpty()) {
				elementState = state.peek();
				elementState.offset = getOffset();
				elementState.lastChild = lastChild;
			}
		}

		private void emitPartial(ElementState elementState, char[] ch) {
			int matchShift = -1;
			for (int shift = 0; shift < ch.length; ++shift) {
				if (endsWith(out, ch, shift, ch.length - shift)) {
					matchShift = shift;
					break;
				}
			}
			if (matchShift > 0) {
				char[] c2 = new char[ch.length - matchShift];
				System.arraycopy(ch, matchShift, c2, 0, c2.length);
				emitChars(elementState, c2);
			} else if (matchShift == -1) {
				emitChars(elementState, ch);
			}
		}

		private boolean endsWith(StringBuilder out, char[] ch, int offset, int length) {
			if (out.length() >= length) {
				for (int x = 0; x < length; ++x) {
					if (out.charAt((out.length() - length) + x) != ch[x + offset]) {
						return false;
					}
				}
				return true;
			}
			return false;
		}

		public void startElement(String uri, String localName, String name, Attributes atts) throws SAXException {
			ElementState parentElementState = state.peek();

			emitText(parentElementState, false);

			emitStyles();

			++parentElementState.childCount;

			ElementState elementState = state.push(new ElementState(parentElementState, localName, state.peek(),
					getOffset()));
			if ("pre".equals(localName)) {
				elementState.skipWhitespace = false;
			} else if ("ul".equals(localName) || "ol".equals(localName) || "blockquote".equals(localName)
					|| "dd".equals(localName)) {
				++elementState.indentLevel;
			}
			// process default styles based on simple name-based CSS selector
			String defaultCssStyle = elementToCssStyle.get(localName);
			if (defaultCssStyle != null) {
				cssStyleManager.processCssStyles(elementState.fontState, parentElementState.fontState, defaultCssStyle);
			}
			// process default styles based on simple 2-level name-based CSS selector
			if (parentElementState.elementName != null) {
				String twoLevelCssSelector = parentElementState.elementName + " " + localName;
				defaultCssStyle = elementToCssStyle.get(twoLevelCssSelector);
				if (defaultCssStyle != null) {
					cssStyleManager.processCssStyles(elementState.fontState, parentElementState.fontState,
							defaultCssStyle);
				}
			}

			int numAtts = atts.getLength();
			for (int x = 0; x < numAtts; ++x) {
				String attName = atts.getLocalName(x);
				if ("style".equals(attName)) {
					String styleValue = atts.getValue(x);
					cssStyleManager.processCssStyles(elementState.fontState, parentElementState.fontState, styleValue);
				} else if ("id".equals(attName)) {
					elementState.addAnnotation(new IdAnnotation(atts.getValue(x)));
				} else if ("href".equals(attName)) {
					elementState.addAnnotation(new AnchorHrefAnnotation(atts.getValue(x)));
				} else if ("href".equals(attName)) {
					elementState.addAnnotation(new TitleAnnotation(atts.getValue(x), localName));
				} else if ("name".equals(attName)) {
					if ("a".equals(localName)) {
						elementState.addAnnotation(new AnchorNameAnnotation(atts.getValue(x)));
					}
				} else if ("class".equals(attName)) {
					elementState.addAnnotation(new ClassAnnotation(atts.getValue(x)));
				} else if ("title".equals(attName)) {
					elementState.addAnnotation(new TitleAnnotation(atts.getValue(x), localName));
				}
			}
			if ("li".equals(localName)) {
				ElementState parentState = state.size() > 1 ? state.get(state.size() - 2) : null;
				boolean numeric = parentState == null ? false : parentState.elementName.equals("ol");

				int index = parentState == null ? 1 : ++parentState.orderedListIndex;
				if (lastNewlineOffset != getOffset()) {
					emitChars(state.peek(), "\n".toCharArray());
				}
				if (numeric) {
					elementState.prefix = (Integer.toString(index) + ". ").toCharArray();
				} else {
					elementState.prefix = new char[] { calculateBulletChar(elementState.indentLevel), ' ', ' ' };
					elementState.addPrefixAnnotation(new BulletAnnotation(elementState.indentLevel));
				}
			} else if ("p".equals(localName)) {
				// account for the case of a paragraph following an unescaped block (eg: Textile with first char being a space).
				if (out.length() > 0) {
					char lastChar = out.charAt(out.length() - 1);
					if (lastChar != '\n') {
						emitChars(state.peek(), "\n\n".toCharArray());
					}
				}
			} else if ("hr".equals(localName)) {
				elementState.addAnnotation(new HorizontalRuleAnnotation());
			} else if ("img".equals(localName) && enableImages) {
				String url = atts.getValue("src");
				if (url != null && url.trim().length() > 0) {
					elementState.addAnnotation(new ImageAnnotation(url.trim(), imageCache.getMissingImage()));
				}
			}
		}

		private char calculateBulletChar(int indentLevel) {
			return bulletChars[Math.min(indentLevel - 1, bulletChars.length - 1)];
		}

		public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
			if (!state.isEmpty()) {
				ElementState elementState = state.peek();
				if (!elementState.skipWhitespace) {
					++elementState.textChildCount;
					characters(ch, start, length);
				}
			}
		}

		public void endDocument() throws SAXException {
			// ORDER DEPENDENCY: do this first
			if (annotationModel != null) {
				for (Map.Entry<Annotation, Position> a : annotationToPosition.entrySet()) {
					annotationModel.addAnnotation(a.getKey(), a.getValue());
				}
			}

			// bug# 236787 trim trailing whitespace and adjust style ranges.
			trimTrailingWhitespace();

			text = out.toString();
			presentation.replaceStyleRanges(styleRanges.toArray(new StyleRange[styleRanges.size()]));
			if (annotationModel != null) {
				String idPrefix = IdAnnotation.class.getPackage().getName();
				Iterator<?> annotationIterator = annotationModel.getAnnotationIterator();
				while (annotationIterator.hasNext()) {
					Annotation annotation = (Annotation) annotationIterator.next();
					if (annotation.getType().startsWith(idPrefix) && !annotationToPosition.containsKey(annotation)) {
						annotationIterator.remove();
					}
				}
			}

			state.clear();
		}

		/**
		 * trim trailing whitespace from the output buffer and adjust style ranges and annotations
		 */
		private void trimTrailingWhitespace() {
			int length = out.length();
			for (int x = length - 1; x >= 0; --x) {
				if (Character.isWhitespace(out.charAt(x))) {
					if (Util.annotationsIncludeOffset(annotationModel, x)) {
						return;
					}
					length = x;
				} else {
					break;
				}
			}
			if (length != out.length()) {
				out.delete(length, out.length());
				Iterator<StyleRange> styleIt = styleRanges.iterator();
				while (styleIt.hasNext()) {
					StyleRange styleRange = styleIt.next();
					if (styleRange.start >= length) {
						styleIt.remove();
					} else {
						int styleEnd = styleRange.start + styleRange.length;
						if (styleEnd > length) {
							styleRange.length -= styleEnd - length;
						}
					}
				}
			}
		}

		public void startDocument() throws SAXException {
			ElementState elementState = state.push(new ElementState(null, "<document>", new FontState(), getOffset()));
			elementState.fontState.size = defaultFont.getFontData()[0].getHeight();
			elementState.fontState.foreground = defaultForeground == null ? null : defaultForeground.getRGB();
			elementState.fontState.background = defaultBackground == null ? null : defaultBackground.getRGB();
		}

		public void processingInstruction(String target, String data) throws SAXException {
		}

		public void skippedEntity(String name) throws SAXException {
		}

		public void setDocumentLocator(Locator locator) {
		}

		public void startPrefixMapping(String prefix, String uri) throws SAXException {
		}

		public void endPrefixMapping(String prefix) throws SAXException {
		}

		private void emitStyles() {
			if (state.isEmpty()) {
				return;
			}
			ElementState elementState = state.peek();
			int offset = elementState.offset;
			if (offset >= getOffset()) {
				// 0-length styles??
				return;
			}
			if (elementState.fontState.equals(state.get(0).fontState)) {
				// no different than the default state
				return;
			}

			int length = getOffset() - offset;
			StyleRange styleRange = cssStyleManager.createStyleRange(elementState.fontState, offset, length);
			styleRanges.add(styleRange);
		}

		private int getOffset() {
			return out.length();
		}

	}

	private static Map<String, char[]> elementToCharacters = new HashMap<String, char[]>();
	static {
		elementToCharacters.put("p", "\n\n".toCharArray());
		elementToCharacters.put("br", "\n".toCharArray());
		elementToCharacters.put("tr", "\n".toCharArray());
		elementToCharacters.put("table", "\n\n".toCharArray());
		elementToCharacters.put("ol", "\n\n".toCharArray());
		elementToCharacters.put("ul", "\n\n".toCharArray());
		elementToCharacters.put("dl", "\n\n".toCharArray());
		elementToCharacters.put("h1", "\n\n".toCharArray());
		elementToCharacters.put("h2", "\n\n".toCharArray());
		elementToCharacters.put("h3", "\n\n".toCharArray());
		elementToCharacters.put("h4", "\n\n".toCharArray());
		elementToCharacters.put("h5", "\n\n".toCharArray());
		elementToCharacters.put("h6", "\n\n".toCharArray());
		elementToCharacters.put("blockquote", "\n\n".toCharArray());
		elementToCharacters.put("pre", "\n\n".toCharArray());
		elementToCharacters.put("th", " \t".toCharArray());
		elementToCharacters.put("td", " \t".toCharArray());
		elementToCharacters.put("dt", "\n".toCharArray());
		elementToCharacters.put("dd", "\n".toCharArray());
	}

	private static Map<String, String> elementToCssStyle = new HashMap<String, String>();
	static {
		// these are the default styles that would normally be applied by a browser
		elementToCssStyle.put("h1", "font-size: 150%; font-weight: bold;");
		elementToCssStyle.put("h2", "font-size: 125%; font-weight: bold;");
		elementToCssStyle.put("h3", "font-size: 110%; font-weight: bold;");
		elementToCssStyle.put("h4", "font-weight: bold;");
		elementToCssStyle.put("h5", "font-size: 83%; font-weight: bold;");
		elementToCssStyle.put("h6", "font-size: 67%; font-weight: bold;");
		elementToCssStyle.put("b", "font-weight: bold;");
		elementToCssStyle.put("strong", "font-weight: bold;");
		elementToCssStyle.put("i", "font-style: italic;");
		elementToCssStyle.put("cite", "font-style: italic;");
		elementToCssStyle.put("em", "font-style: italic;");
		elementToCssStyle.put("var", "font-style: italic;");
		elementToCssStyle.put("dfn", "font-style: italic;");
		elementToCssStyle.put("code", "font-family: monospace;");
		elementToCssStyle.put("pre", "font-family: monospace;");
		elementToCssStyle.put("ins", "text-decoration: underline;");
		elementToCssStyle.put("del", "text-decoration: line-through;");
		elementToCssStyle.put("sup", "font-size: smaller; vertical-align: super;");
		elementToCssStyle.put("sup a", "text-decoration: none;"); // a hack for footnotes
		elementToCssStyle.put("a", "text-decoration: underline; color: blue;");
		elementToCssStyle.put("sub", "font-size: smaller; vertical-align: sub;");
		elementToCssStyle.put("dt", "font-weight: bold;");
		elementToCssStyle.put("blockquote", "color: rgb(38,86,145);");
	}

	private GC gc;

	private int maxWidth;

	private static String trimLeft(String text) {
		final int len = text.length();
		int st = 0;

		while ((st < len) && (text.charAt(st) <= ' ')) {
			st++;
		}
		return st > 0 ? text.substring(st, len) : text;
	}

	public List<Annotation> computePrefixAnnotations(ElementState elementState) {
		while (elementState != null) {
			if (elementState.prefixAnnotations != null) {
				List<Annotation> prefixAnnotations = elementState.prefixAnnotations;
				elementState.prefixAnnotations = null;
				return prefixAnnotations;
			}
			elementState = elementState.parent;
		}
		return null;
	}

	public char[] computePrefix(ElementState elementState) {
		while (elementState != null) {
			if (elementState.prefix != null) {
				char[] prefix = elementState.prefix;
				elementState.prefix = null;
				return prefix;
			}
			elementState = elementState.parent;
		}
		return null;
	}

	private static String trimRight(String text) {
		int len = text.length();

		while (0 < len && (text.charAt(len - 1) <= ' ')) {
			len--;
		}
		return len < text.length() ? text.substring(0, len) : text;
	}

	/**
	 * get the bullet characters that are to be used when presenting bulleted lists. For an indent level of one, the
	 * first character is used, for indent level 2 the second character is used, etc unless the indent level exceeds the
	 * number of characters provided in which case the last character is used.
	 * 
	 * Note that not all characters are available with all fonts.
	 */
	public char[] getBulletChars() {
		return bulletChars;
	}

	/**
	 * set the bullet characters that are to be used when presenting bulleted lists. For an indent level of one, the
	 * first character is used, for indent level 2 the second character is used, etc unless the indent level exceeds the
	 * number of characters provided in which case the last character is used.
	 * 
	 * Note that not all characters are available with all fonts.
	 */
	public void setBulletChars(char[] bulletChars) {
		this.bulletChars = bulletChars;
	}

	/**
	 * indicate if image display is enabled. The default is false.
	 */
	public boolean isEnableImages() {
		return enableImages;
	}

	/**
	 * indicate if image display is enabled. The default is false.
	 */
	public void setEnableImages(boolean enableImages) {
		this.enableImages = enableImages;
	}

	public ImageCache getImageCache() {
		return imageCache;
	}

	public void setImageCache(ImageCache imageCache) {
		this.imageCache = imageCache;
	}

}