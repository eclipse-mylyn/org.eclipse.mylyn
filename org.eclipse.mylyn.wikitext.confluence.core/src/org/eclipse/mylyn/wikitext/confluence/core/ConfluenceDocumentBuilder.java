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

package org.eclipse.mylyn.wikitext.confluence.core;

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

/**
 * a document builder that emits Confluence markup
 * 
 * @see HtmlParser
 * @author David Green
 * @since 1.6
 * @see ConfluenceLanguage
 * @see ConfluenceLanguage#createDocumentBuilder(Writer)
 */
public class ConfluenceDocumentBuilder extends AbstractMarkupDocumentBuilder {

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

	private class ContentBlock extends Block {

		private final String prefix;

		private final String suffix;

		private final StringWriter content = new StringWriter();

		private Writer previousWriter;

		ContentBlock(BlockType blockType, String prefix, String suffix) {
			super(blockType);
			this.prefix = prefix;
			this.suffix = suffix;
		}

		ContentBlock(String prefix, String suffix) {
			this(null, prefix, suffix);
		}

		@Override
		public void write(int c) throws IOException {
			if (getBlockType() != BlockType.CODE && getBlockType() != BlockType.PREFORMATTED) {
				c = normalizeWhitespace(c);
			}
			content.write(c);
		}

		@Override
		public void write(String s) throws IOException {
			if (getBlockType() != BlockType.CODE && getBlockType() != BlockType.PREFORMATTED) {
				s = normalizeWhitespace(s);
			}
			content.write(s);
		}

		@Override
		public void open() throws IOException {
			super.open();
			previousWriter = out;
			out = content;
		}

		@Override
		public void close() throws IOException {
			out = previousWriter;

			final String content = this.content.toString();
			final boolean extended = isExtended(content);
			emitContent(content, extended);

			super.close();

		}

		protected void emitContent(final String content, final boolean extended) throws IOException {
			final String prefix = extended ? this.prefix.replace(".", "..") : this.prefix; //$NON-NLS-1$//$NON-NLS-2$
			final String suffix = extended ? this.suffix + "\n" : this.suffix; //$NON-NLS-1$
			out.write(prefix);
			out.write(content);
			out.write(suffix);
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

	private class LinkBlock extends ContentBlock {

		private final LinkAttributes attributes;

		private LinkBlock(LinkAttributes attributes) {
			super(null, "", ""); //$NON-NLS-1$//$NON-NLS-2$
			this.attributes = attributes;
		}

		@Override
		protected void emitContent(String content, boolean extended) throws IOException {
			if (content.matches("!.*?!")) { //$NON-NLS-1$
				out.write(content);
			} else {
				out.write('"');
				out.write(content);
				out.write('"');
			}
			out.write(':');
			out.write(attributes.getHref());
		}
	}

	private class TableCellBlock extends ContentBlock {
		public TableCellBlock(BlockType blockType) {
			super(blockType, blockType == BlockType.TABLE_CELL_NORMAL ? "|" : "||", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		@Override
		protected void emitContent(String content, boolean extended) throws IOException {
			if (content.length() == 0) {
				content = " "; //$NON-NLS-1$
			}
			super.emitContent(content, extended);
		}
	}

	public ConfluenceDocumentBuilder(Writer out) {
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
			return new ContentBlock(type, "{code}", "{code}\n\n"); //$NON-NLS-1$ //$NON-NLS-2$
		case DEFINITION_ITEM:
		case DEFINITION_TERM:
		case LIST_ITEM:
			char prefixChar = computeCurrentListType() == BlockType.NUMERIC_LIST ? '#' : '*';
			return new ContentBlock(type, computePrefix(prefixChar, computeListLevel()) + " ", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		case DIV:
			if (currentBlock == null) {
				return new ContentBlock(type, "", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				return new ContentBlock(type, "", ""); //$NON-NLS-1$//$NON-NLS-2$
			}
		case FOOTNOTE:
			return new ContentBlock(type, "fn1. ", "\n\n"); // FIXME: footnote number?? //$NON-NLS-1$ //$NON-NLS-2$
		case INFORMATION:
		case NOTE:
		case PANEL:
		case TIP:
		case WARNING:
			attributes.appendCssClass(type.name().toLowerCase());
		case PARAGRAPH:
			String attributesMarkup = computeAttributes(attributes);

			return new ContentBlock(type, attributesMarkup, "\n\n"); //$NON-NLS-1$
		case PREFORMATTED:
			return new ContentBlock(type, "{noformat}" + computeAttributes(attributes) + ". ", "{noformat}\n\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		case QUOTE:
			return new ContentBlock(type, "bq" + computeAttributes(attributes) + ". ", "\n\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		case TABLE:
			return new SuffixBlock(type, "\n"); //$NON-NLS-1$
		case TABLE_CELL_HEADER:
		case TABLE_CELL_NORMAL:
			return new TableCellBlock(type);
		case TABLE_ROW:
			return new SuffixBlock(type, "|\n"); //$NON-NLS-1$
		default:
			Logger.getLogger(getClass().getName()).warning("Unexpected block type: " + type); //$NON-NLS-1$
			return new ContentBlock(type, "", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	@Override
	protected Block computeSpan(SpanType type, Attributes attributes) {
		Block block;
		String spanAttributes = computeAttributes(attributes);
		switch (type) {
		case BOLD:
			block = new ContentBlock("*" + spanAttributes, "*"); //$NON-NLS-1$//$NON-NLS-2$
			break;
		case CITATION:
			block = new ContentBlock("??" + spanAttributes, "??"); //$NON-NLS-1$//$NON-NLS-2$
			break;
		case DELETED:
			block = new ContentBlock("-" + spanAttributes, "-"); //$NON-NLS-1$//$NON-NLS-2$
			break;
		case EMPHASIS:
		case ITALIC:
			block = new ContentBlock("_" + spanAttributes, "_"); //$NON-NLS-1$//$NON-NLS-2$
			break;
		case INSERTED:
			block = new ContentBlock("+" + spanAttributes, "+"); //$NON-NLS-1$//$NON-NLS-2$
			break;
		case CODE:
			block = new ContentBlock("@" + spanAttributes, "@"); //$NON-NLS-1$//$NON-NLS-2$
			break;
		case LINK:
			if (attributes instanceof LinkAttributes) {
				block = new LinkBlock((LinkAttributes) attributes);
			} else {
				block = new ContentBlock("%" + spanAttributes, "%"); //$NON-NLS-1$//$NON-NLS-2$
			}
			break;
		case MONOSPACE:
			block = new ContentBlock("{{", "}}"); //$NON-NLS-1$//$NON-NLS-2$
		case SPAN:
			block = new ContentBlock("", ""); //$NON-NLS-1$//$NON-NLS-2$
			break;
		case STRONG:
			block = new ContentBlock("*" + spanAttributes, "*"); //$NON-NLS-1$//$NON-NLS-2$
			break;
		case SUBSCRIPT:
			block = new ContentBlock("^" + spanAttributes, "^"); //$NON-NLS-1$//$NON-NLS-2$
			break;
		case SUPERSCRIPT:
			block = new ContentBlock("~" + spanAttributes, "~"); //$NON-NLS-1$//$NON-NLS-2$
			break;
		case UNDERLINED:
			block = new ContentBlock("+", "+"); //$NON-NLS-1$//$NON-NLS-2$
			break;

//			case QUOTE: not supported by Textile		
		default:
			block = new ContentBlock("", ""); //$NON-NLS-1$//$NON-NLS-2$
		}
		return block;
	}

	private String computeAttributes(Attributes attributes) {
		String attributeMarkup = ""; //$NON-NLS-1$

		return attributeMarkup;
	}

	@Override
	protected ContentBlock computeHeading(int level, Attributes attributes) {
		return new ContentBlock("h" + level + ". ", "\n\n"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
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

		try {
			currentBlock.write(computeAttributes(attributes));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
