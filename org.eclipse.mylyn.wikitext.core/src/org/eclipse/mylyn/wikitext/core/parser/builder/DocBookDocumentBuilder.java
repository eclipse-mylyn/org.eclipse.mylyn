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
package org.eclipse.mylyn.wikitext.core.parser.builder;

import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.util.FormattingXMLStreamWriter;
import org.eclipse.mylyn.wikitext.core.util.XmlStreamWriter;

/**
 * 
 * 
 * @author David Green
 */
public class DocBookDocumentBuilder extends AbstractXmlDocumentBuilder {

	private static final Pattern CSS_CLASS_INLINE = Pattern.compile("(^|\\s+)inline(\\s+|$)");

	private static Set<Integer> entityReferenceToUnicode = new HashSet<Integer>();
	static {
		entityReferenceToUnicode.add(215);
		entityReferenceToUnicode.add(8211);
		entityReferenceToUnicode.add(8212);
		entityReferenceToUnicode.add(8220);
		entityReferenceToUnicode.add(8221);
		entityReferenceToUnicode.add(8216);
		entityReferenceToUnicode.add(8217);

	}

	private String bookTitle;

	private String doctype = "<!DOCTYPE book PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\" \"http://www.oasis-open.org/docbook/xml/4.5/docbookx.dtd\">";

	private Map<String, String> acronyms = new HashMap<String, String>();

	private int headingLevel = 0;

	private Stack<BlockDescription> blockDescriptions = new Stack<BlockDescription>();

	private boolean automaticGlossary = true;

	public DocBookDocumentBuilder(Writer out) {
		super(out);
	}

	public DocBookDocumentBuilder(XmlStreamWriter writer) {
		super(writer);
	}

	protected XmlStreamWriter createFormattingXmlStreamWriter(Writer out) {
		XmlStreamWriter writer = super.createXmlStreamWriter(out);
		return new FormattingXMLStreamWriter(writer) {
			@Override
			protected boolean preserveWhitespace(String elementName) {
				return elementName.equals("code") || elementName.startsWith("literal");
			}
		};
	}

	public String getDoctype() {
		return doctype;
	}

	public void setDoctype(String doctype) {
		this.doctype = doctype;
	}

	public String getBookTitle() {
		return bookTitle;
	}

	public void setBookTitle(String bookTitle) {
		this.bookTitle = bookTitle;
	}

	@Override
	public void acronym(String text, String definition) {
		String previousDef = acronyms.put(text, definition);
		if (previousDef != null && previousDef.length() > definition.length()) {
			acronyms.put(text, previousDef);
		}
		writer.writeStartElement("glossterm");
		characters(text);
		writer.writeEndElement();
	}

	@Override
	public void link(Attributes attributes, String href, final String text) {
		link(attributes, href, new ContentEmitter() {
			public void emit() {
				writer.writeCharacters(text);
			}
		});
	}

	private void link(Attributes attributes, String href, ContentEmitter emitter) {
		ensureBlockElementsOpen();
		if (href.startsWith("#")) {
			if (href.length() > 1) {
				writer.writeStartElement("link");
				writer.writeAttribute("linkend", href.substring(1));
				emitter.emit();
				writer.writeEndElement(); // link
			} else {
				emitter.emit();
			}
		} else {
			writer.writeStartElement("ulink");
			writer.writeAttribute("url", href);
			emitter.emit();
			writer.writeEndElement(); // ulink
		}
	}

	private interface ContentEmitter {
		public void emit();
	}

	@Override
	public void beginBlock(BlockType type, Attributes attributes) {
		if (headingLevel == 0) {
			beginHeading(1, new Attributes());
			endHeading();
		}

		String elementName;
		String[] elementNames = null;
		boolean allowTitle = false;
		boolean closeElementsOnBlockStart = false;
		BlockDescription previousBlock = null;
		if (!blockDescriptions.isEmpty()) {
			previousBlock = blockDescriptions.peek();
		}

		switch (type) {
		case BULLETED_LIST:
			elementName = "itemizedlist";
			break;
		case NUMERIC_LIST:
			elementName = "orderedlist";
			break;
		case DEFINITION_LIST:
			elementName = "variablelist";

			//			variablelist
			//				varlistentry+
			//					term+
			//					listitem
			//
			break;
		case DEFINITION_TERM:

			BlockDescription blockDescription = findBlockDescription(BlockType.DEFINITION_LIST);
			if (blockDescription.entrySize > 0) {
				endBlockEntry(blockDescription);
			}
			openBlockEntry(blockDescription, new String[] { "varlistentry" });

			elementName = "term";
			break;
		case DEFINITION_ITEM:
			elementName = "listitem";
			elementNames = new String[] { "para" };
			closeElementsOnBlockStart = true;
			break;
		case FOOTNOTE:
		case PARAGRAPH:
			elementName = "para";
			break;
		case CODE:
			elementName = "code";
			break;
		case PREFORMATTED:
			elementName = "literallayout";
			break;
		case QUOTE:
			elementName = "blockquote";
			break;
		case LIST_ITEM:
			elementName = "listitem";
			elementNames = new String[] { "para" };
			closeElementsOnBlockStart = true;
			break;
		case TABLE:
			elementName = "informaltable";
			break;
		case TABLE_CELL_HEADER:
			elementName = "th";
			break;
		case TABLE_CELL_NORMAL:
			elementName = "td";
			break;
		case TABLE_ROW:
			elementName = "tr";
			break;
		case INFORMATION:
			elementName = "important";
			allowTitle = true;
			break;
		case NOTE:
			elementName = "note";
			allowTitle = true;
			break;
		case WARNING:
			elementName = "warning";
			allowTitle = true;
			break;
		case TIP:
			elementName = "tip";
			allowTitle = true;
			break;
		case PANEL:
			elementName = "note"; // docbook has nothing better for 'note'
			allowTitle = true;
			break;
		default:
			throw new IllegalStateException(type.name());
		}
		if (previousBlock != null && previousBlock.closeElementsOnBlockStart) {
			endBlockEntry(previousBlock);
		}

		int blockSize = 1;
		writer.writeStartElement(elementName);
		applyAttributes(attributes);

		if (elementNames != null) {
			for (String name : elementNames) {
				writer.writeStartElement(name);
			}
		}

		if (allowTitle && attributes.getTitle() != null) {
			writer.writeStartElement("title");
			writer.writeCharacters(attributes.getTitle());
			writer.writeEndElement();
		}

		blockDescriptions.push(new BlockDescription(type, blockSize, elementNames, closeElementsOnBlockStart));
	}

	@Override
	public void endBlock() {
		final BlockDescription blockDescription = blockDescriptions.pop();
		int size = blockDescription.size + blockDescription.entrySize;
		for (int x = 0; x < size; ++x) {
			writer.writeEndElement();
		}
	}

	private void endBlockEntry(BlockDescription blockDescription) {
		for (int x = 0; x < blockDescription.entrySize; ++x) {
			writer.writeEndElement();
		}
		blockDescription.entrySize = 0;
	}

	private void openBlockEntry(BlockDescription blockDescription, String[] entry) {
		for (String ent : entry) {
			writer.writeStartElement(ent);
		}
		blockDescription.entrySize += entry.length;
	}

	@Override
	public void beginHeading(int level, Attributes attributes) {
		closeSections(Math.max(level - 1, 0));

		while (headingLevel < level) {
			headingLevel++;

			writer.writeStartElement(headingLevel == 1 ? "chapter" : "section");
			if (attributes != null) {
				applyAttributes(attributes);
				attributes = null;
			}
		}

		writer.writeStartElement("title");

	}

	@Override
	public void endHeading() {
		writer.writeEndElement(); // title
	}

	@Override
	public void beginDocument() {
		baseInHead = false;
		writer.writeStartDocument();
		writer.writeDTD(doctype);

		writer.writeStartElement("book");
		writer.writeStartElement("title");
		if (bookTitle != null) {
			writer.writeCharacters(bookTitle);
		}
		writer.writeEndElement();
	}

	@Override
	public void beginSpan(SpanType type, Attributes attributes) {
		ensureBlockElementsOpen();
		switch (type) {
		case BOLD:
		case STRONG:
			writer.writeStartElement("emphasis");
			writer.writeAttribute("role", "bold");
			break;
		case CITATION:
			writer.writeStartElement("citation");
			break;
		case CODE:
			writer.writeStartElement("code");
			break;
		case DELETED:
			writer.writeStartElement("emphasis");
			writer.writeAttribute("role", "del");
			break;
		case EMPHASIS:
			writer.writeStartElement("emphasis");
			break;
		case INSERTED:
			writer.writeStartElement("emphasis");
			writer.writeAttribute("role", "ins");
			break;
		case UNDERLINED:
			writer.writeStartElement("emphasis");
			writer.writeAttribute("role", "underline");
			break;
		case ITALIC:
			writer.writeStartElement("emphasis");
			writer.writeAttribute("role", "italic");
			break;
		case SPAN:
			writer.writeStartElement("phrase");
			break;
		case SUBSCRIPT:
			writer.writeStartElement("subscript");
			break;
		case SUPERSCRIPT:
			writer.writeStartElement("superscript");
			break;
		case MONOSPACE:
			writer.writeStartElement("literal");
			break;
		default:
			Logger.getLogger(DocBookDocumentBuilder.class.getName()).warning("No docbook mapping for " + type);
			writer.writeStartElement("phrase");
			break;
		}
		applyAttributes(attributes);
	}

	private void applyAttributes(Attributes attributes) {
		if (attributes.getId() != null) {
			writer.writeAttribute("id", attributes.getId());
		}
	}

	@Override
	public void endDocument() {
		closeSections(0);

		writeGlossaryAppendix();

		writer.writeEndElement(); // book
		writer.writeEndDocument();

		acronyms.clear();
	}

	private void closeSections(int toLevel) {
		if (toLevel < 0) {
			toLevel = 0;
		}
		while (headingLevel > toLevel) {
			writer.writeEndElement();
			--headingLevel;
		}
	}

	private void writeGlossaryAppendix() {
		if (!acronyms.isEmpty() && automaticGlossary) {
			writer.writeStartElement("appendix");
			writer.writeAttribute("id", "glossary");
			writer.writeStartElement("title");
			writer.writeAttribute("id", "glossary-end");
			writer.writeCharacters("Glossary");
			writer.writeEndElement(); // title
			writer.writeStartElement("glosslist");

			for (Map.Entry<String, String> glossEntry : new TreeMap<String, String>(acronyms).entrySet()) {

				writer.writeStartElement("glossentry");

				writer.writeStartElement("glossterm");
				writer.writeCharacters(glossEntry.getKey());
				writer.writeEndElement(); // glossterm

				writer.writeStartElement("glossdef");
				writer.writeStartElement("para");
				writer.writeCharacters(glossEntry.getValue());
				writer.writeEndElement(); // para
				writer.writeEndElement(); // glossdef

				writer.writeEndElement(); // glossentry
			}
			writer.writeEndElement(); // glosslist
			writer.writeEndElement(); // appendix
		}
	}

	@Override
	public void endSpan() {
		writer.writeEndElement();
	}

	@Override
	public void characters(String text) {
		ensureBlockElementsOpen();
		super.characters(text);
	}

	@Override
	public void charactersUnescaped(String literal) {
		ensureBlockElementsOpen();
		// note: this *may* have HTML tags in it
		writer.writeLiteral(literal);
		//		Logger.getLogger(DocBookDocumentBuilder.class.getName()).warning("HTML literal not supported in DocBook");
	}

	private void ensureBlockElementsOpen() {
		if (!blockDescriptions.isEmpty()) {
			BlockDescription blockDescription = blockDescriptions.peek();
			if (blockDescription.entrySize == 0 && blockDescription.nestedElementNames != null) {
				openBlockEntry(blockDescription, blockDescription.nestedElementNames);
			}
		}
	}

	@Override
	public void entityReference(String entity) {
		ensureBlockElementsOpen();
		if (entity.startsWith("#")) {
			String numeric = entity.substring(1);
			int base = 10;
			if (numeric.startsWith("x")) {
				numeric = entity.substring(1);
				base = 16;
			}
			int unicodeValue = Integer.parseInt(numeric, base);
			if (entityReferenceToUnicode.contains(unicodeValue)) {
				writer.writeCharacters("" + ((char) unicodeValue));
				return;
			}
		}
		writer.writeEntityRef(entity);
	}

	@Override
	public void image(Attributes attributes, String url) {
		ensureBlockElementsOpen();
		String cssClass = attributes.getCssClass();
		boolean inlined = false;
		if (cssClass != null && CSS_CLASS_INLINE.matcher(cssClass).find()) {
			inlined = true;
		}
		emitImage(attributes, url, inlined);
	}

	private void emitImage(Attributes attributes, String url, boolean inline) {
		ensureBlockElementsOpen();
		writer.writeStartElement(inline ? "inlinemediaobject" : "mediaobject");
		applyAttributes(attributes);
		writer.writeStartElement("imageobject");
		writer.writeEmptyElement("imagedata");
		writer.writeAttribute("fileref", makeUrlAbsolute(url));
		writer.writeEndElement(); // imageobject
		writer.writeEndElement(); // inlinemediaobject or mediaobject
	}

	@Override
	public void imageLink(Attributes linkAttributes, final Attributes imageAttributes, String href,
			final String imageUrl) {
		link(linkAttributes, href, new ContentEmitter() {
			public void emit() {
				emitImage(imageAttributes, imageUrl, true);
			}
		});
	}

	@Override
	public void lineBreak() {
		ensureBlockElementsOpen();
		// no equivalent in DocBook.
		characters("\n");
	}

	private BlockDescription findBlockDescription(BlockType type) {
		for (int x = blockDescriptions.size() - 1; x >= 0; --x) {
			BlockDescription blockDescription = blockDescriptions.get(x);
			if (blockDescription.type == type) {
				return blockDescription;
			}
		}
		return null;
	}

	private static class BlockDescription {
		BlockType type;

		int size;

		int entrySize; // the size of an entry, if it is open, otherwise 0

		final String[] nestedElementNames;

		final boolean closeElementsOnBlockStart;

		public BlockDescription(BlockType type, int size, String[] nestedElementNames, boolean closeElementsOnBlockStart) {
			this.size = size;
			this.entrySize = nestedElementNames == null ? 0 : nestedElementNames.length;
			this.type = type;
			this.nestedElementNames = nestedElementNames;
			this.closeElementsOnBlockStart = closeElementsOnBlockStart;
		}
	}

	/**
	 * Indicate if this builder should generate an automatic glossary if acronyms are used. When the automatic glossary
	 * is enabled and acronyms are used in the document, then an <code>appendix</code> with title 'Glossary' is added to
	 * the document, with a <code>glosslist</code> generated for all of the acronyms that appear in the document.
	 * 
	 * The default is true.
	 */
	public boolean isAutomaticGlossary() {
		return automaticGlossary;
	}

	/**
	 * Indicate if this builder should generate an automatic glossary if acronyms are used. When the automatic glossary
	 * is enabled and acronyms are used in the document, then an <code>appendix</code> with title 'Glossary' is added to
	 * the document, with a <code>glosslist</code> generated for all of the acronyms that appear in the document.
	 * 
	 * The default is true.
	 */
	public void setAutomaticGlossary(boolean automaticGlossary) {
		this.automaticGlossary = automaticGlossary;
	}

}
