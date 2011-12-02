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

package org.eclipse.mylyn.internal.wikitext.textile.core;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.HtmlParser;
import org.eclipse.mylyn.wikitext.core.parser.LinkAttributes;
import org.eclipse.mylyn.wikitext.core.parser.builder.AbstractMarkupDocumentBuilder;
import org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;

/**
 * a document builder that emits Textile markup
 * 
 * @see HtmlParser
 * @author David Green
 * @since 1.6
 * @see TextileLanguage
 * @see TextileLanguage#createDocumentBuilder(Writer)
 */
public class TextileDocumentBuilder extends AbstractMarkupDocumentBuilder {

	private static final Pattern PATTERN_MULTIPLE_NEWLINES = Pattern.compile("(\r\n|\r|\n){2,}"); //$NON-NLS-1$

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

	private boolean previousWasExtended;

	private boolean emitAttributes = true;

	private class ContentBlock extends Block {

		protected final String prefix;

		protected final String suffix;

		protected final boolean requireAdjacentSeparator;

		protected final boolean emitWhenEmpty;

		ContentBlock(BlockType blockType, String prefix, String suffix, boolean requireAdjacentSeparator,
				boolean emitWhenEmpty) {
			super(blockType);
			this.prefix = prefix;
			this.suffix = suffix;
			this.requireAdjacentSeparator = requireAdjacentSeparator;
			this.emitWhenEmpty = emitWhenEmpty;
		}

		ContentBlock(String prefix, String suffix, boolean requireAdjacentWhitespace, boolean emitWhenEmpty) {
			this(null, prefix, suffix, requireAdjacentWhitespace, emitWhenEmpty);
		}

		@Override
		public void write(int c) throws IOException {
			if (getBlockType() != BlockType.CODE && getBlockType() != BlockType.PREFORMATTED) {
				c = normalizeWhitespace(c);
			}
			TextileDocumentBuilder.this.emitContent(c);
		}

		@Override
		public void write(String s) throws IOException {
			if (getBlockType() != BlockType.CODE && getBlockType() != BlockType.PREFORMATTED) {
				s = normalizeWhitespace(s);
			}
			TextileDocumentBuilder.this.emitContent(s);
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
			TextileDocumentBuilder.this.emitContent(suffix);
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

	}

	private class SpanBlock extends ContentBlock {

		public SpanBlock(String spanAttributes, boolean requireAdjacentWhitespace, boolean emitWhenEmpty) {
			super(null, "%" + spanAttributes, "%", requireAdjacentWhitespace, emitWhenEmpty); //$NON-NLS-1$ //$NON-NLS-2$
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
			super(null, "", "", true, true); //$NON-NLS-1$//$NON-NLS-2$
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
			super(blockType, blockType == BlockType.TABLE_CELL_NORMAL ? "|" : "|_.", "", false, true); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		@Override
		protected void emitContent(String content, boolean extended) throws IOException {
			if (content.length() == 0) {
				content = " "; //$NON-NLS-1$
			}
			super.emitContent(content, extended);
		}
	}

	public TextileDocumentBuilder(Writer out) {
		super(out);
	}

	@Override
	protected Block computeBlock(BlockType type, Attributes attributes) {
		switch (type) {
		case BULLETED_LIST:
		case DEFINITION_LIST:
		case NUMERIC_LIST:
			return new SuffixBlock(type, "\n"); //$NON-NLS-1$
		case CODE:
			return new ContentBlock(type, "bc. ", "\n\n", false, false); //$NON-NLS-1$ //$NON-NLS-2$
		case DEFINITION_ITEM:
		case DEFINITION_TERM:
		case LIST_ITEM:
			char prefixChar = computeCurrentListType() == BlockType.NUMERIC_LIST ? '#' : '*';
			return new ContentBlock(type, computePrefix(prefixChar, computeListLevel()) + " ", "\n", false, true); //$NON-NLS-1$ //$NON-NLS-2$
		case DIV:
			if (currentBlock == null) {
				return new ContentBlock(type, "", "\n", false, false); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				return new ContentBlock(type, "", "", false, false); //$NON-NLS-1$//$NON-NLS-2$
			}
		case FOOTNOTE:
			return new ContentBlock(type, "fn1. ", "\n\n", false, false); // FIXME: footnote number?? //$NON-NLS-1$ //$NON-NLS-2$
		case INFORMATION:
		case NOTE:
		case PANEL:
		case TIP:
		case WARNING:
			attributes.appendCssClass(type.name().toLowerCase());
		case PARAGRAPH:
			String attributesMarkup = computeAttributes(attributes);

			return new ContentBlock(type, attributesMarkup.length() > 0 || previousWasExtended
					? "p" + attributesMarkup + ". " //$NON-NLS-1$ //$NON-NLS-2$
					: attributesMarkup, "\n\n", false, false); //$NON-NLS-1$
		case PREFORMATTED:
			return new ContentBlock(type, "pre" + computeAttributes(attributes) + ". ", "\n\n", false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		case QUOTE:
			return new ContentBlock(type, "bq" + computeAttributes(attributes) + ". ", "\n\n", false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		case TABLE:
			return new SuffixBlock(type, "\n"); //$NON-NLS-1$
		case TABLE_CELL_HEADER:
		case TABLE_CELL_NORMAL:
			return new TableCellBlock(type);
		case TABLE_ROW:
			return new SuffixBlock(type, "|\n"); //$NON-NLS-1$
		default:
			Logger.getLogger(getClass().getName()).warning("Unexpected block type: " + type); //$NON-NLS-1$
			return new ContentBlock(type, "", "", false, false); //$NON-NLS-1$ //$NON-NLS-2$
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
			appendStyle = "text-decoration:underline;";//$NON-NLS-1$
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
			block = new ContentBlock("*" + spanAttributes, "*", true, false); //$NON-NLS-1$//$NON-NLS-2$
			break;
		case CITATION:
			block = new ContentBlock("??" + spanAttributes, "??", true, false); //$NON-NLS-1$//$NON-NLS-2$
			break;
		case DELETED:
			block = new ContentBlock("-" + spanAttributes, "-", true, false); //$NON-NLS-1$//$NON-NLS-2$
			break;
		case EMPHASIS:
			block = new ContentBlock("_" + spanAttributes, "_", true, false); //$NON-NLS-1$//$NON-NLS-2$
			break;
		case INSERTED:
			block = new ContentBlock("+" + spanAttributes, "+", true, false); //$NON-NLS-1$//$NON-NLS-2$
			break;
		case CODE:
			block = new ContentBlock("@" + spanAttributes, "@", true, false); //$NON-NLS-1$//$NON-NLS-2$
			break;
		case ITALIC:
			block = new ContentBlock("__" + spanAttributes, "__", true, false); //$NON-NLS-1$//$NON-NLS-2$
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

		case STRONG:
			block = new ContentBlock("*" + spanAttributes, "*", true, false); //$NON-NLS-1$//$NON-NLS-2$
			break;
		case SUBSCRIPT:
			block = new ContentBlock("^" + spanAttributes, "^", true, false); //$NON-NLS-1$//$NON-NLS-2$
			break;
		case SUPERSCRIPT:
			block = new ContentBlock("~" + spanAttributes, "~", true, false); //$NON-NLS-1$//$NON-NLS-2$
			break;

//			case QUOTE: not supported by Textile		
		case UNDERLINED:
		case SPAN:
		default:
			if (spanAttributes.length() == 0) {
				block = new SpanBlock("", false, false); //$NON-NLS-1$ 
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
		return new ContentBlock("h" + level + ". ", "\n\n", false, false); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
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
					currentBlock.write("(c)");break; //$NON-NLS-1$
				case '\u00AE': // &reg;
					currentBlock.write("(r)");break; //$NON-NLS-1$
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
			literal = "&" + entity + ";"; //$NON-NLS-1$//$NON-NLS-2$
		}
		try {
			currentBlock.write(literal);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void image(Attributes attributes, String url) {
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

	@Override
	public void link(Attributes attributes, String hrefOrHashName, String text) {
		assertOpenBlock();
		try {
			currentBlock.write('"');
			writeAttributes(attributes);
			currentBlock.write(text);
			currentBlock.write('"');
			currentBlock.write(':');
			currentBlock.write(hrefOrHashName);
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
			currentBlock.write('\n');
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
	 * indicate if attributes (such as CSS styles, CSS class, id) should be emitted in the generated Textile markup.
	 * Defaults to true.
	 */
	public boolean isEmitAttributes() {
		return emitAttributes;
	}

	/**
	 * indicate if attributes (such as CSS styles, CSS class, id) should be emitted in the generated Textile markup.
	 * Defaults to true.
	 */
	public void setEmitAttributes(boolean emitAttributes) {
		this.emitAttributes = emitAttributes;
	}

}
