/*******************************************************************************
 * Copyright (c) 2011, 2017 Tasktop Technologies.
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

package org.eclipse.mylyn.wikitext.confluence.internal;

import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.confluence.ConfluenceLanguage;
import org.eclipse.mylyn.wikitext.confluence.internal.util.Colors;
import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.HtmlParser;
import org.eclipse.mylyn.wikitext.parser.ImageAttributes;
import org.eclipse.mylyn.wikitext.parser.LinkAttributes;
import org.eclipse.mylyn.wikitext.parser.builder.AbstractMarkupDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.builder.EntityReferences;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;

/**
 * a document builder that emits Confluence markup
 *
 * @see HtmlParser
 * @author David Green
 * @see ConfluenceLanguage
 * @see ConfluenceLanguage#createDocumentBuilder(Writer)
 */
public class ConfluenceDocumentBuilder extends AbstractMarkupDocumentBuilder {

	private static final String NEWLINE_REGEX = "(?:\r\n|\r|\n)";

	private static final Pattern PATTERN_MULTIPLE_NEWLINES = Pattern
			.compile("(" + NEWLINE_REGEX + "(?:\\s+" + NEWLINE_REGEX + "|" + NEWLINE_REGEX + ")+)"); //$NON-NLS-1$

	private static final CharMatcher SPAN_MARKUP_CHARACTERS = CharMatcher.anyOf("*_+-^~{}[]?%@"); //$NON-NLS-1$

	private final Map<String, String> entityToLiteral = new HashMap<String, String>();

	{
		entityToLiteral.put("nbsp", " "); //$NON-NLS-1$//$NON-NLS-2$
		entityToLiteral.put("#160", " "); //$NON-NLS-1$//$NON-NLS-2$
		entityToLiteral.put("quot", "\""); //$NON-NLS-1$//$NON-NLS-2$
		entityToLiteral.put("amp", "&"); //$NON-NLS-1$//$NON-NLS-2$
		entityToLiteral.put("lt", "<"); //$NON-NLS-1$//$NON-NLS-2$
		entityToLiteral.put("gt", ">"); //$NON-NLS-1$//$NON-NLS-2$
		entityToLiteral.put("copy", "(c)"); //$NON-NLS-1$//$NON-NLS-2$
		entityToLiteral.put("reg", "(r)"); //$NON-NLS-1$//$NON-NLS-2$
		entityToLiteral.put("#8482", "(t)"); //$NON-NLS-1$//$NON-NLS-2$
		entityToLiteral.put("euro", "\u20ac"); //$NON-NLS-1$//$NON-NLS-2$
		entityToLiteral.put("#36", "$"); //$NON-NLS-1$//$NON-NLS-2$
		entityToLiteral.put("#37", "%"); //$NON-NLS-1$//$NON-NLS-2$
	}

	private interface ConfluenceBlock {

		void writeLineBreak() throws IOException;

	}

	private class ContentBlock extends NewlineDelimitedBlock implements ConfluenceBlock {

		private final String prefix;

		private final String suffix;

		private final boolean requireAdjacentSeparator;

		private final boolean emitWhenEmpty;

		private int consecutiveLineBreakCount = 0;

		private final boolean escaping;

		private final boolean trimmingNewlinesAndWhitespace;

		private final boolean collapsingConsecutiveNewlines;

		ContentBlock(BlockType blockType, String prefix, String suffix, boolean requireAdjacentSeparator,
				boolean emitWhenEmpty, int leadingNewlines, int trailingNewlines) {
			this(blockType, prefix, suffix, requireAdjacentSeparator, emitWhenEmpty, leadingNewlines, trailingNewlines,
					true, false, false);
		}

		ContentBlock(BlockType blockType, String prefix, String suffix, boolean requireAdjacentSeparator,
				boolean emitWhenEmpty, int leadingNewlines, int trailingNewlines, boolean escaping,
				boolean trimmingNewlinesAndWhitespace, boolean collapsingConsecutiveNewlines) {
			super(blockType, leadingNewlines, trailingNewlines);
			this.prefix = prefix;
			this.suffix = suffix;
			this.requireAdjacentSeparator = requireAdjacentSeparator;
			this.emitWhenEmpty = emitWhenEmpty;
			this.escaping = escaping;
			this.trimmingNewlinesAndWhitespace = trimmingNewlinesAndWhitespace;
			this.collapsingConsecutiveNewlines = collapsingConsecutiveNewlines;
		}

		ContentBlock(String prefix, String suffix, boolean requireAdjacentWhitespace, boolean emitWhenEmpty,
				int leadingNewlines, int trailingNewlines) {
			this(null, prefix, suffix, requireAdjacentWhitespace, emitWhenEmpty, leadingNewlines, trailingNewlines,
					true, false, false);
		}

		@Override
		public void write(int c) throws IOException {
			consecutiveLineBreakCount = 0;
			if (!isBlockTypePreservingWhitespace()) {
				c = normalizeWhitespace(c);
			}
			if (escaping) {
				ConfluenceDocumentBuilder.this.emitEscapedContent(c);
			} else {
				ConfluenceDocumentBuilder.this.emitContent(c);
			}
		}

		@Override
		public void write(String s) throws IOException {
			consecutiveLineBreakCount = 0;
			if (!isBlockTypePreservingWhitespace()) {
				s = normalizeWhitespace(s);
			}
			if (escaping) {
				ConfluenceDocumentBuilder.this.emitEscapedContent(s);
			} else {
				ConfluenceDocumentBuilder.this.emitContent(s);
			}
		}

		public void writeLineBreak() throws IOException {
			++consecutiveLineBreakCount;
			if (isTableCellBlock()) {
				if (consecutiveLineBreakCount == 1 ) {
					ConfluenceDocumentBuilder.this.emitContent('\n');
				} else {
					ConfluenceDocumentBuilder.this.emitContent("\u00A0\n"); // 'NO-BREAK SPACE'
				}
			} else if (consecutiveLineBreakCount == 1 || isBlockTypePreservingWhitespace()) {
				if (isPrefixedBlockTerminatedByNewlines()) {
					ConfluenceDocumentBuilder.this.emitContent("\\\\"); //$NON-NLS-1$
				} else {
					ConfluenceDocumentBuilder.this.emitContent('\n');
				}
			} else {
				if (getLastChar() != '\n') {
					ConfluenceDocumentBuilder.this.emitContent(' ');
				}
				ConfluenceDocumentBuilder.this.emitContent("\\\\"); //$NON-NLS-1$
			}
		}

		@Override
		public void open() throws IOException {
			super.open();
			pushWriter(new StringWriter());
			if (requireAdjacentSeparator) {
				clearRequireAdjacentSeparator();
			}

			// Emit here so that nested blocks can detect parent block type
			emitPrefix();
		}

		@Override
		public final void close() throws IOException {
			Writer thisContent = popWriter();
			String content = thisContent.toString();
			boolean contentIsEmpty = content.equals(prefix);

			if (!contentIsEmpty || emitWhenEmpty) {
				checkState(content.startsWith(prefix), "Expected content to start with prefix \"%s\"", content, prefix); //$NON-NLS-1$
				content = content.substring(prefix.length());

				if (requireAdjacentSeparator && !isSpanSuffixAdjacentToSpanPrefix()) {
					requireAdjacentSeparator();
				} else {
					clearRequireAdjacentSeparator();
				}

				emitPrefix();
				if (trimmingNewlinesAndWhitespace) {
					content = CharMatcher.whitespace().trimFrom(content);
				}
				if (collapsingConsecutiveNewlines) {
					content = PATTERN_MULTIPLE_NEWLINES.matcher(content).replaceAll("\n");
				}
				emitContent(content);
				emitSuffix(content);

				if (requireAdjacentSeparator) {
					requireAdjacentSeparator();
				}
			}

			super.close();
			consecutiveLineBreakCount = 0;
		}

		private boolean isSpanSuffixAdjacentToSpanPrefix() {
			if (!Strings.isNullOrEmpty(prefix) && isSpanMarkup(getLastChar()) && isSpanMarkup(prefix.charAt(0))) {
				return true;
			}
			return false;
		}

		protected void emitPrefix() throws IOException {
			ConfluenceDocumentBuilder.this.emitContent(prefix);
		}

		protected void emitContent(String content) throws IOException {
			ConfluenceDocumentBuilder.this.emitContent(content);
		}

		private void emitSuffix(String content) throws IOException {
			final String suffix = isExtended(content) ? this.suffix + "\n" : this.suffix; //$NON-NLS-1$
			ConfluenceDocumentBuilder.this.emitContent(suffix);
		}

		private boolean isExtended(String content) {
			if (getBlockType() != null) {
				switch (getBlockType()) {
				case CODE:
				case PREFORMATTED:
				case QUOTE:
					return PATTERN_MULTIPLE_NEWLINES.matcher(content).find();
				}
			}
			return false;
		}

		private boolean isPrefixedBlockTerminatedByNewlines() {
			return suffix.isEmpty() && !prefix.isEmpty();
		}

		private boolean isBlockTypePreservingWhitespace() {
			return getBlockType() == BlockType.CODE || getBlockType() == BlockType.PREFORMATTED;
		}

		private boolean isTableCellBlock() {
			return currentBlock instanceof TableCellBlock;
		}
	}

	private class LinkBlock extends ContentBlock {

		private final LinkAttributes attributes;

		private LinkBlock(LinkAttributes attributes) {
			super(null, "[", "]", true, true, 0, 0); //$NON-NLS-1$//$NON-NLS-2$
			this.attributes = attributes;
		}

		@Override
		protected void emitContent(String content) throws IOException {
			//[Example|http://example.com|title]
			// [Example|http://example.com]
			if (!Strings.isNullOrEmpty(content)) {
				super.emitContent(content);
				super.emitContent(" | ");//$NON-NLS-1$
			}
			if (attributes.getHref() != null) {
				super.emitContent(attributes.getHref());
			}
			if (!Strings.isNullOrEmpty(attributes.getTitle())) {
				super.emitContent(" | ");//$NON-NLS-1$
				super.emitContent(attributes.getTitle());
			}
		}
	}

	private class TableCellBlock extends ContentBlock {
		public TableCellBlock(BlockType blockType) {
			super(blockType, blockType == BlockType.TABLE_CELL_NORMAL ? "|" : "||", "", false, true, 0, 0, true, true, //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
					true);
		}

		@Override
		protected void emitContent(String content) throws IOException {
			if (Strings.isNullOrEmpty(content) || content.trim().isEmpty()) {
				content = " "; //$NON-NLS-1$
			}
			super.emitContent(content);
		}

	}

	private class ImplicitParagraphBlock extends ContentBlock {

		ImplicitParagraphBlock() {
			super(BlockType.PARAGRAPH, "", "", false, false, 2, 2); //$NON-NLS-1$//$NON-NLS-2$
		}

		@Override
		protected boolean isImplicitBlock() {
			return true;
		}
	}

	public ConfluenceDocumentBuilder(Writer out) {
		super(out);
		currentBlock = null;
	}

	@Override
	protected Block computeBlock(BlockType type, Attributes attributes) {
		switch (type) {
		case BULLETED_LIST:
		case DEFINITION_LIST:
		case NUMERIC_LIST:
			return new NewlineDelimitedBlock(type, doubleNewlineDelimiterCount(), 1);
		case CODE:
			return new ContentBlock(type, "{code}", "{code}\n\n", false, false, 2, 2, false, false, false); //$NON-NLS-1$ //$NON-NLS-2$
		case DEFINITION_ITEM:
		case DEFINITION_TERM:
		case LIST_ITEM:
			char prefixChar = computeCurrentListType() == BlockType.NUMERIC_LIST ? '#' : '*';
			return new ContentBlock(type, computePrefix(prefixChar, computeListLevel()) + " ", "", false, true, 1, 1, //$NON-NLS-1$//$NON-NLS-2$
					true, true, true);
		case DIV:
			if (currentBlock == null) {
				return new ContentBlock(type, "", "", false, false, 2, 2); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				return new ContentBlock(type, "", "", true, false, 0, 0); //$NON-NLS-1$//$NON-NLS-2$
			}
		case FOOTNOTE:
			return new ContentBlock(type, "fn1. ", "", false, false, 2, 2); //$NON-NLS-1$ //$NON-NLS-2$
		case INFORMATION:
		case NOTE:
		case PANEL:
		case TIP:
		case WARNING:
			attributes.appendCssClass(type.name().toLowerCase());
		case PARAGRAPH:
			return new ContentBlock(type, "", "", false, false, doubleNewlineDelimiterCount(), //$NON-NLS-1$
					doubleNewlineDelimiterCount());
		case PREFORMATTED:
			return new ContentBlock(type, "{noformat}", "{noformat}", false, false, doubleNewlineDelimiterCount(), //$NON-NLS-1$//$NON-NLS-2$
					doubleNewlineDelimiterCount(), false, false, false);
		case QUOTE:
			return new ContentBlock(type, "{quote}", "{quote}", false, false, doubleNewlineDelimiterCount(), //$NON-NLS-1$//$NON-NLS-2$
					doubleNewlineDelimiterCount());
		case TABLE:
			return new SuffixBlock(type, "\n"); //$NON-NLS-1$
		case TABLE_CELL_HEADER:
		case TABLE_CELL_NORMAL:
			return new TableCellBlock(type);
		case TABLE_ROW:
			return new SuffixBlock(type, "|\n"); //$NON-NLS-1$
		default:
			Logger.getLogger(getClass().getName()).warning("Unexpected block type: " + type); //$NON-NLS-1$
			return new ContentBlock(type, "", "", false, false, 0, 0); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private int doubleNewlineDelimiterCount() {
		if (currentBlock != null) {
			BlockType currentBlockType = currentBlock.getBlockType();
			if (currentBlockType == BlockType.LIST_ITEM || currentBlockType == BlockType.DEFINITION_ITEM
					|| currentBlockType == BlockType.DEFINITION_TERM || currentBlockType == BlockType.BULLETED_LIST
					|| currentBlockType == BlockType.NUMERIC_LIST || currentBlockType == BlockType.DEFINITION_LIST) {
				return 1;
			}
		}
		return 2;
	}

	@Override
	protected Block computeSpan(SpanType type, Attributes attributes) {
		Block block;
		switch (type) {
		case BOLD:
			block = new ContentBlock("*", "*", true, false, 0, 0); //$NON-NLS-1$//$NON-NLS-2$
			break;
		case CITATION:
			block = new ContentBlock("??", "??", true, false, 0, 0); //$NON-NLS-1$//$NON-NLS-2$
			break;
		case DELETED:
			block = new ContentBlock("-", "-", true, false, 0, 0); //$NON-NLS-1$//$NON-NLS-2$
			break;
		case MARK:
		case EMPHASIS:
		case ITALIC:
			block = new ContentBlock("_", "_", true, false, 0, 0); //$NON-NLS-1$//$NON-NLS-2$
			break;
		case INSERTED:
			block = new ContentBlock("+", "+", true, false, 0, 0); //$NON-NLS-1$//$NON-NLS-2$
			break;
		case CODE:
			block = new ContentBlock("@", "@", true, false, 0, 0); //$NON-NLS-1$//$NON-NLS-2$
			break;
		case LINK:
			if (attributes instanceof LinkAttributes) {
				block = new LinkBlock((LinkAttributes) attributes);
			} else {
				block = new ContentBlock("%", "%", true, true, 0, 0); //$NON-NLS-1$//$NON-NLS-2$
			}
			break;
		case MONOSPACE:
			block = new ContentBlock("{{", "}}", true, false, 0, 0); //$NON-NLS-1$//$NON-NLS-2$
			break;
		case STRONG:
			block = new ContentBlock("*", "*", true, false, 0, 0); //$NON-NLS-1$//$NON-NLS-2$
			break;
		case SUPERSCRIPT:
			block = new ContentBlock("^", "^", true, false, 0, 0); //$NON-NLS-1$//$NON-NLS-2$
			break;
		case SUBSCRIPT:
			block = new ContentBlock("~", "~", true, false, 0, 0); //$NON-NLS-1$//$NON-NLS-2$
			break;
		case UNDERLINED:
			block = new ContentBlock("+", "+", true, false, 0, 0); //$NON-NLS-1$//$NON-NLS-2$
			break;
//			case QUOTE: not supported
		case SPAN:
		default:
			block = null;
			if (attributes.getCssStyle() != null) {
				Matcher colorMatcher = Pattern.compile("color:\\s*([^;\\t]+)").matcher(attributes.getCssStyle()); //$NON-NLS-1$
				if (colorMatcher.find()) {
					String color = Colors.asHex(colorMatcher.group(1));
					if (color.equalsIgnoreCase("black") || color.equals("#010101")) { //$NON-NLS-1$ //$NON-NLS-2$
						color = null;
					}
					if (color != null) {
						block = new ContentBlock("{color:" + color + "}", "{color}", true, false, 0, 0); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
				}
			}
			if (block == null) {
				block = new ContentBlock("", "", false, false, 0, 0); //$NON-NLS-1$//$NON-NLS-2$
			}
		}
		return block;
	}

	@Override
	protected boolean isSeparator(int i) {
		return !isSpanMarkup((char) i) && (super.isSeparator(i) || isEscapeCharacter(i));
	}

	private boolean isEscapeCharacter(int i) {
		return (char) i == '\\';
	}

	private boolean isSpanMarkup(char character) {
		return SPAN_MARKUP_CHARACTERS.matches(character);
	}

	@Override
	protected ContentBlock computeHeading(int level, Attributes attributes) {
		return new ContentBlock("h" + level + ". ", "", false, false, 2, 2); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	public void characters(String text) {
		assertOpenBlock();
		try {
			for (int x = 0; x < text.length(); ++x) {
				char c = text.charAt(x);
				switch (c) {
				case '\u00A0':// &nbsp;
					currentBlock.write(' ');
					break;
				case '\u00A9': // &copy;
					currentBlock.write("(c)"); //$NON-NLS-1$
					break;
				case '\u00AE': // &reg;
					currentBlock.write("(r)"); //$NON-NLS-1$
					break;
				default:
					currentBlock.write(c);
					break;
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void entityReference(String entity) {
		assertOpenBlock();
		String literal = entityToLiteral.get(entity);
		if (literal == null) {
			literal = EntityReferences.instance().equivalentString(entity);
			if (literal == null) {
				literal = "&" + entity + ";"; //$NON-NLS-1$//$NON-NLS-2$
			}

		}
		try {
			super.emitContent(literal);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void image(Attributes attributes, String url) {
		if (url != null) {
			assertOpenBlock();
			try {
				super.emitContent('!');
				currentBlock.write(url);
				writeImageAttributes(attributes);
				super.emitContent('!');
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void link(Attributes attributes, String hrefOrHashName, String text) {
		assertOpenBlock();
		LinkAttributes linkAttributes = new LinkAttributes();
		linkAttributes.setTitle(attributes.getTitle());
		linkAttributes.setHref(hrefOrHashName);
		beginSpan(SpanType.LINK, linkAttributes);
		characters(text);
		endSpan();
	}

	@Override
	public void imageLink(Attributes linkAttributes, Attributes imageAttributes, String href, String imageUrl) {
		assertOpenBlock();
		try {
			super.emitContent('!');
			currentBlock.write(imageUrl);
			writeImageAttributes(imageAttributes);
			super.emitContent('!');
			super.emitContent(':');
			currentBlock.write(href);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void acronym(String text, String definition) {
		assertOpenBlock();
		try {
			currentBlock.write(text);
			currentBlock.write('(');
			currentBlock.write(definition);
			currentBlock.write(')');
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void lineBreak() {
		assertOpenBlock();
		try {
			if (currentBlock instanceof ConfluenceBlock) {
				((ConfluenceBlock) currentBlock).writeLineBreak();
			} else {
				currentBlock.write('\n');
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	void emitEscapedContent(String s) throws IOException {
		for (int x = 0; x < s.length(); ++x) {
			emitEscapedContent(s.charAt(x));
		}
	}

	void emitEscapedContent(int c) throws IOException {
		if (c == '\\') {
			super.emitContent("&#92;");
		} else {
			emitEscapeCharacter(c);
			super.emitContent(c);
		}
	}

	void emitEscapeCharacter(int c) throws IOException {
		if ((c == '#' && getLastChar() == '&') || (c == '{' || c == '\\' || c == '[' || c == ']'|| c == '!' || c == '|') ) {
			if (getLastChar() == '\\') {
				super.emitContent(' ');
			}
			super.emitContent('\\');
		}
	}

	@Override
	protected Block createImplicitParagraphBlock() {
		return new ImplicitParagraphBlock();
	}

	private void writeImageAttributes(Attributes attributes) throws IOException {
		if (attributes instanceof ImageAttributes) {
			ImageAttributes imageAttributes = (ImageAttributes) attributes;
			String attributeMarkup = "";
			if (!Strings.isNullOrEmpty(imageAttributes.getAlt())) {
				attributeMarkup = "alt=\"" + imageAttributes.getAlt() + "\"";
			}
			if (!Strings.isNullOrEmpty(imageAttributes.getTitle())) {
				if (!attributeMarkup.isEmpty()) {
					attributeMarkup += ",";
				}
				attributeMarkup += "title=\"" + imageAttributes.getTitle() + "\"";
			}
			if (!attributeMarkup.isEmpty()) {
				super.emitContent('|');
				currentBlock.write(attributeMarkup);
			}
		}
	}
}
