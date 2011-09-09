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

package org.eclipse.mylyn.wikitext.textile.core;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.HtmlParser;
import org.eclipse.mylyn.wikitext.core.parser.LinkAttributes;

/**
 * a document builder that emits Textile markup
 * 
 * @see HtmlParser
 * @author David Green
 * @since 1.6
 */
public class TextileDocumentBuilder extends DocumentBuilder {

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

	private Block currentBlock = new ImplicitParagraphBlock();

	private boolean previousWasExtended;

	private Writer out;

	private boolean emitAttributes = true;

	private class Block {
		private Block previousBlock;

		private final BlockType blockType;

		Block(BlockType blockType) {
			this.blockType = blockType;
		}

		void write(int c) throws IOException {
		}

		void write(String s) throws IOException {
		}

		void open() throws IOException {
			previousBlock = currentBlock;
			currentBlock = this;
		}

		void close() throws IOException {
			currentBlock = previousBlock;
		}

		Block getPreviousBlock() {
			return previousBlock;
		}

		BlockType getBlockType() {
			return blockType;
		}
	}

	private class SuffixBlock extends Block {
		private final String suffix;

		private SuffixBlock(BlockType blockType, String suffix) {
			super(blockType);
			this.suffix = suffix;
		}

		@Override
		void close() throws IOException {
			out.write(suffix);
			super.close();
		}
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
		void write(int c) throws IOException {
			if (getBlockType() != BlockType.CODE && getBlockType() != BlockType.PREFORMATTED) {
				c = normalizeWhitespace(c);
			}
			content.write(c);
		}

		@Override
		void write(String s) throws IOException {
			if (getBlockType() != BlockType.CODE && getBlockType() != BlockType.PREFORMATTED) {
				s = normalizeWhitespace(s);
			}
			content.write(s);
		}

		@Override
		void open() throws IOException {
			super.open();
			previousWriter = out;
			out = content;
		}

		@Override
		void close() throws IOException {
			out = previousWriter;

			final String content = this.content.toString();
			final boolean extended = isExtended(content);
			emitContent(content, extended);

			super.close();
			if (getBlockType() != null) {
				previousWasExtended = extended;
			}
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

	private class ImplicitParagraphBlock extends Block {

		private boolean hasContent = false;

		ImplicitParagraphBlock() {
			super(BlockType.PARAGRAPH);
		}

		@Override
		void write(int c) throws IOException {
			hasContent = true;
			out.write(normalizeWhitespace(c));
		}

		@Override
		void write(String s) throws IOException {
			hasContent = true;
			out.write(normalizeWhitespace(s));
		}

		@Override
		void close() throws IOException {
			if (hasContent) {
				out.write("\n\n"); //$NON-NLS-1$
			}
			super.close();
		}
	}

	public TextileDocumentBuilder(Writer out) {
		this.out = out;
	}

	@Override
	public void beginDocument() {
		// nothing to do
	}

	@Override
	public void endDocument() {
		while (currentBlock != null) {
			endBlock();
		}
	}

	@Override
	public void beginBlock(BlockType type, Attributes attributes) {
		Block block = computeBlock(type, attributes);
		try {
			if (currentBlock instanceof ImplicitParagraphBlock) {
				currentBlock.close();
				currentBlock = null;
			}
			block.open();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Block computeBlock(BlockType type, Attributes attributes) {
		switch (type) {
		case BULLETED_LIST:
		case DEFINITION_LIST:
		case NUMERIC_LIST:
			return new SuffixBlock(type, "\n"); //$NON-NLS-1$
		case CODE:
			return new ContentBlock(type, "bc. ", "\n\n"); //$NON-NLS-1$ //$NON-NLS-2$
		case DEFINITION_ITEM:
		case DEFINITION_TERM:
		case LIST_ITEM:
			char prefixChar = computeCurrentListType() == BlockType.NUMERIC_LIST ? '#' : '*';
			return new ContentBlock(type, computePrefix(prefixChar, computeListLevel()) + " ", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		case DIV:
			return new ContentBlock(type, "", ""); //$NON-NLS-1$//$NON-NLS-2$
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

			return new ContentBlock(type, attributesMarkup.length() > 0 || previousWasExtended
					? "p" + attributesMarkup + ". " //$NON-NLS-1$ //$NON-NLS-2$
					: attributesMarkup, "\n\n"); //$NON-NLS-1$
		case PREFORMATTED:
			return new ContentBlock(type, "pre" + computeAttributes(attributes) + ". ", "\n\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		case QUOTE:
			return new ContentBlock(type, "bq" + computeAttributes(attributes) + ". ", "\n\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		case TABLE:
			return new SuffixBlock(type, "\n"); //$NON-NLS-1$
		case TABLE_CELL_HEADER:
			return new ContentBlock(type, "|_.", ""); //$NON-NLS-1$ //$NON-NLS-2$ 
		case TABLE_CELL_NORMAL:
			return new ContentBlock(type, "|", ""); //$NON-NLS-1$ //$NON-NLS-2$ 
		case TABLE_ROW:
			return new SuffixBlock(type, "|\n"); //$NON-NLS-1$
		default:
			Logger.getLogger(getClass().getName()).warning("Unexpected block type: " + type); //$NON-NLS-1$
			return new ContentBlock(type, "", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private String computePrefix(char c, int count) {
		String prefix = ""; //$NON-NLS-1$
		while (count-- > 0) {
			prefix += c;
		}
		return prefix;
	}

	private int computeListLevel() {
		int level = 0;
		Block b = currentBlock;
		while (b != null) {
			if (b.getBlockType() != null) {
				switch (b.getBlockType()) {
				case BULLETED_LIST:
				case NUMERIC_LIST:
				case DEFINITION_LIST:
					++level;
				}
			}
			b = b.getPreviousBlock();
		}
		return level;
	}

	private BlockType computeCurrentListType() {
		Block b = currentBlock;
		while (b != null) {
			if (b.getBlockType() != null) {
				switch (b.getBlockType()) {
				case BULLETED_LIST:
				case NUMERIC_LIST:
				case DEFINITION_LIST:
					return b.getBlockType();
				}
			}
			b = b.getPreviousBlock();
		}
		return null;
	}

	@Override
	public void endBlock() {
		if (currentBlock != null) {
			try {
				currentBlock.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void beginSpan(SpanType type, Attributes attributes) {
		Block block;
		switch (type) {
		case BOLD:
			block = new ContentBlock("*" + computeAttributes(attributes), "*"); //$NON-NLS-1$//$NON-NLS-2$
			break;
		case CITATION:
			block = new ContentBlock("??" + computeAttributes(attributes), "??"); //$NON-NLS-1$//$NON-NLS-2$
			break;
		case DELETED:
			block = new ContentBlock("-" + computeAttributes(attributes), "-"); //$NON-NLS-1$//$NON-NLS-2$
			break;
		case EMPHASIS:
			block = new ContentBlock("_" + computeAttributes(attributes), "_"); //$NON-NLS-1$//$NON-NLS-2$
			break;
		case INSERTED:
			block = new ContentBlock("+" + computeAttributes(attributes), "+"); //$NON-NLS-1$//$NON-NLS-2$
			break;
		case CODE:
			block = new ContentBlock("@" + computeAttributes(attributes), "@"); //$NON-NLS-1$//$NON-NLS-2$
			break;
		case ITALIC:
			block = new ContentBlock("__" + computeAttributes(attributes), "__"); //$NON-NLS-1$//$NON-NLS-2$
			break;
		case LINK:
			if (attributes instanceof LinkAttributes) {
				block = new LinkBlock((LinkAttributes) attributes);
			} else {
				block = new ContentBlock("%" + computeAttributes(attributes), "%"); //$NON-NLS-1$//$NON-NLS-2$
			}
			break;
		case MONOSPACE:
			block = new ContentBlock("%{font-family:monospace;}", "%"); //$NON-NLS-1$//$NON-NLS-2$
		case SPAN:
			block = new ContentBlock("%" + computeAttributes(attributes), "%"); //$NON-NLS-1$//$NON-NLS-2$
			break;
		case STRONG:
			block = new ContentBlock("*" + computeAttributes(attributes), "*"); //$NON-NLS-1$//$NON-NLS-2$
			break;
		case SUBSCRIPT:
			block = new ContentBlock("^" + computeAttributes(attributes), "^"); //$NON-NLS-1$//$NON-NLS-2$
			break;
		case SUPERSCRIPT:
			block = new ContentBlock("~" + computeAttributes(attributes), "~"); //$NON-NLS-1$//$NON-NLS-2$
			break;
		case UNDERLINED:
			block = new ContentBlock("%{text-decoration:underline;}", "%"); //$NON-NLS-1$//$NON-NLS-2$
			break;

//			case QUOTE: not supported by Textile		
		default:
			block = new ContentBlock("%" + computeAttributes(attributes), "%"); //$NON-NLS-1$//$NON-NLS-2$
		}
		try {
			block.open();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
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
	public void endSpan() {
		endBlock();
	}

	@Override
	public void beginHeading(int level, Attributes attributes) {
		Block block = new ContentBlock("h" + level + ". ", "\n\n"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
		try {
			block.open();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void endHeading() {
		endBlock();
	}

	@Override
	public void characters(String text) {
		assertOpenBlock();
		try {
			currentBlock.write(text);
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

	private void assertOpenBlock() {
		if (currentBlock == null) {
			currentBlock = new ImplicitParagraphBlock();
		}
	}

	@Override
	public void charactersUnescaped(String literal) {
		characters(literal);
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

	private int normalizeWhitespace(int c) {
		if (c == '\r' || c == '\n') {
			c = ' ';
		}
		return c;
	}

	private String normalizeWhitespace(String s) {
		s = s.replaceAll("(\r|\n)", " "); //$NON-NLS-1$//$NON-NLS-2$
		return s;
	}
}
