/*******************************************************************************
 * Copyright (c) 2011, 2015 Tasktop Technologies.
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

package org.eclipse.mylyn.wikitext.textile.internal;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.HtmlParser;
import org.eclipse.mylyn.wikitext.parser.LinkAttributes;
import org.eclipse.mylyn.wikitext.parser.builder.AbstractMarkupDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.builder.EntityReferences;
import org.eclipse.mylyn.wikitext.textile.TextileLanguage;

/**
 * a document builder that emits Textile markup
 *
 * @see HtmlParser
 * @author David Green
 * @see TextileLanguage
 * @see TextileLanguage#createDocumentBuilder(Writer)
 */
public class TextileDocumentBuilder extends AbstractMarkupDocumentBuilder {

	private static final Pattern PATTERN_MULTIPLE_NEWLINES = Pattern.compile("(\r\n|\r|\n){2,}"); //$NON-NLS-1$

	private final Map<String, String> entityToLiteral = new HashMap<>();

	{
		entityToLiteral.put("nbsp", " "); //$NON-NLS-1$//$NON-NLS-2$
		entityToLiteral.put("#160", " "); //$NON-NLS-1$//$NON-NLS-2$
		entityToLiteral.put("copy", "(c)"); //$NON-NLS-1$//$NON-NLS-2$
		entityToLiteral.put("reg", "(r)"); //$NON-NLS-1$//$NON-NLS-2$
		entityToLiteral.put("#8482", "(t)"); //$NON-NLS-1$//$NON-NLS-2$
	}

	private boolean previousWasExtended;

	private boolean emitAttributes = true;

	private interface TextileBlock {
		void lineBreak() throws IOException;
	}

	private class ContentBlock extends NewlineDelimitedBlock implements TextileBlock {

		protected final String prefix;

		protected String suffix;

		protected final boolean requireAdjacentSeparator;

		protected final boolean emitWhenEmpty;

		private final boolean normalizingWhitespace;

		ContentBlock(BlockType blockType, String prefix, String suffix, boolean requireAdjacentSeparator,
				boolean emitWhenEmpty, boolean normalizingWhitespace, int leadingNewlines, int trailingNewlines) {
			super(blockType, leadingNewlines, trailingNewlines);
			this.prefix = prefix;
			this.suffix = suffix;
			this.requireAdjacentSeparator = requireAdjacentSeparator;
			this.emitWhenEmpty = emitWhenEmpty;
			this.normalizingWhitespace = normalizingWhitespace;
		}

		ContentBlock(String prefix, String suffix, boolean requireAdjacentWhitespace, boolean emitWhenEmpty,
				int leadingNewlines, int trailingNewlines) {
			this(null, prefix, suffix, requireAdjacentWhitespace, emitWhenEmpty, true, leadingNewlines,
					trailingNewlines);
		}

		@Override
		public void lineBreak() throws IOException {
			write('\n');
		}

		@Override
		public void write(int c) throws IOException {
			if (normalizingWhitespace) {
				c = normalizeWhitespace(c);
				if (c == ' ' && getLastChar() == ' ') {
					return;
				}
			}
			TextileDocumentBuilder.this.emitContent(c);
		}

		@Override
		public void write(String s) throws IOException {
			for (int x = 0; x < s.length(); ++x) {
				write(s.charAt(x));
			}
		}

		@Override
		public void open() throws IOException {
			super.open();
			pushWriter(new StringWriter());
			if (requireAdjacentSeparator) {
				clearRequireAdjacentSeparator();
			}
		}

		@Override
		public void close() throws IOException {
			Writer thisContent = popWriter();

			final String content = thisContent.toString();
			boolean extendedBlock = isExtended(content);

			if (content.length() > 0 || emitWhenEmpty) {
				if (requireAdjacentSeparator) {
					requireAdjacentSeparator();
				}

				emitContent(content, extendedBlock);

				if (requireAdjacentSeparator) {
					requireAdjacentSeparator();
				}
			}

			super.close();
			if (getBlockType() != null) {
				previousWasExtended = extendedBlock;
			}
		}

		protected void emitContent(final String content, final boolean extended) throws IOException {
			final String prefix = extended ? this.prefix.replace(".", "..") : this.prefix; //$NON-NLS-1$//$NON-NLS-2$
			final String suffix = extended ? this.suffix + "\n" : this.suffix; //$NON-NLS-1$
			TextileDocumentBuilder.this.emitContent(prefix);
			TextileDocumentBuilder.this.emitContent(content);
			emitSuffix(suffix);
		}

		private void emitSuffix(String suffix) throws IOException {
			if (suffix.equals("\n")) { //$NON-NLS-1$
				char lastChar = getLastChar();
				if (lastChar != '\n' && lastChar != 0) {
					TextileDocumentBuilder.this.emitContent(suffix);
				}
			} else {
				TextileDocumentBuilder.this.emitContent(suffix);
			}
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

		protected int normalizeWhitespace(int c) {
			return TextileDocumentBuilder.this.normalizeWhitespace(c);
		}
	}

	private class ParagraphBlock extends ContentBlock {

		ParagraphBlock(BlockType blockType, String prefix, boolean requireAdjacentSeparator, boolean emitWhenEmpty,
				boolean normalizingWhitespace, int leadingNewlines, int trailingNewlines) {
			super(blockType, prefix, "", requireAdjacentSeparator, emitWhenEmpty, normalizingWhitespace, //$NON-NLS-1$
					leadingNewlines, trailingNewlines);
		}

		@Override
		public void lineBreak() throws IOException {
			final char lastChar = getLastChar();
			if (consecutiveNewline(lastChar, '\n')) {
				return;
			}
			TextileDocumentBuilder.this.emitContent('\n');
		}

	}

	private final class TextileImplicitParagraphBlock extends ParagraphBlock {

		TextileImplicitParagraphBlock() {
			super(BlockType.PARAGRAPH, previousWasExtended ? "p. " : "", false, false, true, 2, 2); //$NON-NLS-1$//$NON-NLS-2$
		}

		@Override
		protected boolean isImplicitBlock() {
			return true;
		}
	}

	private class SpanBlock extends ContentBlock {

		public SpanBlock(String spanAttributes, boolean requireAdjacentWhitespace, boolean emitWhenEmpty) {
			super(null, "%" + spanAttributes, "%", requireAdjacentWhitespace, emitWhenEmpty, true, 0, 0); //$NON-NLS-1$ //$NON-NLS-2$
		}

		@Override
		protected void emitContent(final String content, final boolean extended) throws IOException {
			boolean nestedSpan = computeNestedSpan();

			if (!nestedSpan) {
				final String prefix = extended ? this.prefix.replace(".", "..") : this.prefix; //$NON-NLS-1$//$NON-NLS-2$
				TextileDocumentBuilder.this.emitContent(prefix);
			}
			TextileDocumentBuilder.this.emitContent(content);
			if (!nestedSpan) {
				final String suffix = extended ? this.suffix + "\n" : this.suffix; //$NON-NLS-1$
				TextileDocumentBuilder.this.emitContent(suffix);
			}
		}

		private boolean computeNestedSpan() {
			Block block = getPreviousBlock();
			while (block != null) {

				if (block instanceof SpanBlock) {
					return true;
				}

				block = block.getPreviousBlock();
			}
			return false;
		}
	}

	private class LinkBlock extends ContentBlock {

		private final LinkAttributes attributes;

		private LinkBlock(LinkAttributes attributes) {
			super(null, "", "", true, true, true, 0, 0); //$NON-NLS-1$//$NON-NLS-2$
			this.attributes = attributes;
		}

		@Override
		protected void emitContent(String content, boolean extended) throws IOException {
			if (content.matches("!.*?!")) { //$NON-NLS-1$
				TextileDocumentBuilder.this.emitContent(content);
			} else {
				TextileDocumentBuilder.this.emitContent('"');
				TextileDocumentBuilder.this.emitContent(content);
				TextileDocumentBuilder.this.emitContent('"');
			}
			TextileDocumentBuilder.this.emitContent(':');
			TextileDocumentBuilder.this.emitContent(attributes.getHref());
		}
	}

	private class TableCellBlock extends ContentBlock {
		public TableCellBlock(BlockType blockType) {
			super(blockType, blockType == BlockType.TABLE_CELL_NORMAL ? "|" : "|_.", "", false, true, true, 0, 0); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		@Override
		protected void emitContent(String content, boolean extended) throws IOException {
			if (content.length() == 0) {
				content = " "; //$NON-NLS-1$
			}
			content = content.replaceAll("(\\r|\\n)+", " "); //$NON-NLS-1$ //$NON-NLS-2$
			super.emitContent(content, extended);
		}
	}

	public class DefinitionItemBlock extends ContentBlock {

		public DefinitionItemBlock() {
			super(BlockType.DEFINITION_ITEM, " := ", "", false, true, true, 0, 1); //$NON-NLS-1$//$NON-NLS-2$
		}

		@Override
		public void lineBreak() throws IOException {
			TextileDocumentBuilder.this.emitContent('\n');
			suffix = " =:\n"; //$NON-NLS-1$
		}
	}

	public TextileDocumentBuilder(Writer out) {
		super(out);
		currentBlock = null;
	}

	@Override
	protected Block computeBlock(BlockType type, Attributes attributes) {
		switch (type) {
			case BULLETED_LIST:
			case DEFINITION_LIST:
			case NUMERIC_LIST:
				if (currentBlock != null) {
					BlockType currentBlockType = currentBlock.getBlockType();
					if (currentBlockType == BlockType.LIST_ITEM || currentBlockType == BlockType.DEFINITION_ITEM
							|| currentBlockType == BlockType.DEFINITION_TERM) {
						return new NewlineDelimitedBlock(type, 1, 1);
					}
				}
				return new NewlineDelimitedBlock(type, 2, 1);
			case CODE:
				return new ContentBlock(type, "bc. ", "", false, false, false, 2, 2); //$NON-NLS-1$ //$NON-NLS-2$
			case DEFINITION_ITEM:
				return new DefinitionItemBlock();
			case DEFINITION_TERM:
				return new ContentBlock(type, "- ", "", false, true, true, 0, 0); //$NON-NLS-1$//$NON-NLS-2$
			case LIST_ITEM:
				char prefixChar = computeCurrentListType() == BlockType.NUMERIC_LIST ? '#' : '*';
				return new ContentBlock(type, computePrefix(prefixChar, computeListLevel()) + " ", "", false, true, //$NON-NLS-1$//$NON-NLS-2$
						true, 1, 1);
			case DIV:
				if (currentBlock == null) {
					return new ParagraphBlock(type, "", false, false, true, 2, 2); //$NON-NLS-1$
				} else {
					return new ParagraphBlock(type, "", true, false, true, 0, 0); //$NON-NLS-1$
				}
			case FOOTNOTE:
				return new ParagraphBlock(type, "fn1. ", false, false, true, 2, 2); //$NON-NLS-1$
			case INFORMATION:
			case NOTE:
			case PANEL:
			case TIP:
			case WARNING:
				attributes.appendCssClass(type.name().toLowerCase());
			case PARAGRAPH:
				String attributesMarkup = computeAttributes(attributes);

				return new ParagraphBlock(type,
						attributesMarkup.length() > 0 || previousWasExtended
								? "p" + attributesMarkup + ". " //$NON-NLS-1$ //$NON-NLS-2$
								: attributesMarkup,
						false, false, true, 2, 2);
			case PREFORMATTED:
				return new ContentBlock(type, "pre" + computeAttributes(attributes) + ". ", "", false, false, false, 2, //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
						2);
			case QUOTE:
				return new ContentBlock(type, "bq" + computeAttributes(attributes) + ". ", "", false, false, true, 2, //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
						2);
			case TABLE:
				return new SuffixBlock(type, "\n"); //$NON-NLS-1$
			case TABLE_CELL_HEADER:
			case TABLE_CELL_NORMAL:
				return new TableCellBlock(type);
			case TABLE_ROW:
				return new SuffixBlock(type, "|\n"); //$NON-NLS-1$
			default:
				Logger.getLogger(getClass().getName()).warning("Unexpected block type: " + type); //$NON-NLS-1$
				return new ContentBlock(type, "", "", false, false, true, 0, 0); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	@Override
	protected Block computeSpan(SpanType type, Attributes attributes) {
		String appendStyle = null;
		switch (type) {
			case UNDERLINED:
				appendStyle = "text-decoration:underline;";//$NON-NLS-1$
				break;
			case MONOSPACE:
				appendStyle = "font-family:monospace;";//$NON-NLS-1$
				break;
		}
		if (appendStyle != null) {
			attributes = new Attributes(attributes.getId(), attributes.getCssClass(), attributes.getCssStyle(),
					attributes.getLanguage());
			attributes.appendCssStyle(appendStyle);
		}
		Block block;
		String spanAttributes = computeAttributes(attributes);
		switch (type) {
			case BOLD:
				block = new ContentBlock("**" + spanAttributes, "**", true, false, 0, 0); //$NON-NLS-1$//$NON-NLS-2$
				break;
			case CITATION:
				block = new ContentBlock("??" + spanAttributes, "??", true, false, 0, 0); //$NON-NLS-1$//$NON-NLS-2$
				break;
			case DELETED:
				block = new ContentBlock("-" + spanAttributes, "-", true, false, 0, 0); //$NON-NLS-1$//$NON-NLS-2$
				break;
			case MARK:
			case EMPHASIS:
				block = new ContentBlock("_" + spanAttributes, "_", true, false, 0, 0); //$NON-NLS-1$//$NON-NLS-2$
				break;
			case INSERTED:
				block = new ContentBlock("+" + spanAttributes, "+", true, false, 0, 0); //$NON-NLS-1$//$NON-NLS-2$
				break;
			case CODE:
				block = new ContentBlock("@" + spanAttributes, "@", true, false, 0, 0); //$NON-NLS-1$//$NON-NLS-2$
				break;
			case ITALIC:
				block = new ContentBlock("__" + spanAttributes, "__", true, false, 0, 0); //$NON-NLS-1$//$NON-NLS-2$
				break;
			case LINK:
				if (attributes instanceof LinkAttributes) {
					block = new LinkBlock((LinkAttributes) attributes);
				} else {
					block = new SpanBlock(spanAttributes, true, false);
				}
				break;
			case MONOSPACE:
				block = new SpanBlock(spanAttributes, true, false);
				break;
			case STRONG:
				block = new ContentBlock("*" + spanAttributes, "*", true, false, 0, 0); //$NON-NLS-1$//$NON-NLS-2$
				break;
			case SUPERSCRIPT:
				block = new ContentBlock("^" + spanAttributes, "^", true, false, 0, 0); //$NON-NLS-1$//$NON-NLS-2$
				break;
			case SUBSCRIPT:
				block = new ContentBlock("~" + spanAttributes, "~", true, false, 0, 0); //$NON-NLS-1$//$NON-NLS-2$
				break;

//			case QUOTE: not supported by Textile
			case UNDERLINED:
			case SPAN:
			default:
				if (spanAttributes.length() == 0) {
					block = new SpanBlock("", true, false); //$NON-NLS-1$
				} else {
					block = new SpanBlock(spanAttributes, true, false);
				}
				break;
		}
		return block;
	}

	private String computeAttributes(Attributes attributes) {
		String attributeMarkup = ""; //$NON-NLS-1$
		if (emitAttributes) {
			String classId = ""; //$NON-NLS-1$
			if (attributes.getCssClass() != null) {
				classId = attributes.getCssClass();
			}
			if (attributes.getId() != null) {
				classId += "#" + attributes.getId(); //$NON-NLS-1$
			}
			if (classId.length() > 0) {
				attributeMarkup += "(" + classId + ")"; //$NON-NLS-1$//$NON-NLS-2$
			}
			if (attributes.getCssStyle() != null) {
				attributeMarkup += "{" + attributes.getCssStyle() + "}"; //$NON-NLS-1$//$NON-NLS-2$
			}
		}
		return attributeMarkup;
	}

	@Override
	protected ContentBlock computeHeading(int level, Attributes attributes) {
		return new ContentBlock("h" + level + computeAttributes(attributes) + ". ", "", false, false, 2, 2); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
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
			currentBlock.write(literal);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void image(Attributes attributes, String url) {
		if (url != null) {
			assertOpenBlock();
			try {
				currentBlock.write('!');
				writeAttributes(attributes);
				currentBlock.write(url);
				currentBlock.write('!');
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void link(Attributes attributes, String hrefOrHashName, String text) {
		assertOpenBlock();
		try {
			currentBlock.write('"');
			writeAttributes(attributes);
			currentBlock.write(text);
			currentBlock.write('"');
			currentBlock.write(':');
			if (hrefOrHashName != null) {
				currentBlock.write(hrefOrHashName);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void imageLink(Attributes linkAttributes, Attributes imageAttributes, String href, String imageUrl) {
		assertOpenBlock();
		try {
			currentBlock.write('!');
			writeAttributes(imageAttributes);
			currentBlock.write(imageUrl);
			currentBlock.write('!');
			currentBlock.write(':');
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
			if (currentBlock instanceof TextileBlock) {
				((TextileBlock) currentBlock).lineBreak();
			} else {
				currentBlock.write('\n');
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void writeAttributes(Attributes attributes) {
		if (!emitAttributes) {
			return;
		}
		try {
			currentBlock.write(computeAttributes(attributes));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * indicate if attributes (such as CSS styles, CSS class, id) should be emitted in the generated Textile markup. Defaults to true.
	 */
	public boolean isEmitAttributes() {
		return emitAttributes;
	}

	/**
	 * indicate if attributes (such as CSS styles, CSS class, id) should be emitted in the generated Textile markup. Defaults to true.
	 */
	public void setEmitAttributes(boolean emitAttributes) {
		this.emitAttributes = emitAttributes;
	}

	@Override
	protected Block createImplicitParagraphBlock() {
		return new TextileImplicitParagraphBlock();
	}

	private boolean consecutiveNewline(final char lastChar, final char nextChar) {
		return (nextChar == '\n' || nextChar == '\r') && lastChar == '\n' || nextChar == '\r' && lastChar == '\r';
	}
}
