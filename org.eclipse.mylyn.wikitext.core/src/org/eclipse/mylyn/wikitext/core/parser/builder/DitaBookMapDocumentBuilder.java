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

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Stack;

import org.eclipse.mylyn.internal.wikitext.core.parser.builder.DitaTopicDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineItem;
import org.eclipse.mylyn.wikitext.core.util.FormattingXMLStreamWriter;
import org.eclipse.mylyn.wikitext.core.util.XmlStreamWriter;
import org.eclipse.mylyn.wikitext.core.util.anttask.MarkupToDitaTask;

/**
 * a document builder that can produce OASIS DITA output in the form of a book map and multiple topic output files, one
 * for each level-1 heading.
 * 
 * This document builder differs from others in that it implements {@link Closeable} and therefore must be closed after
 * use. Also this document builder produces multiple output files.
 * 
 * @author David Green
 * 
 * @see DocBookDocumentBuilder
 * @see MarkupToDitaTask
 */
public class DitaBookMapDocumentBuilder extends AbstractXmlDocumentBuilder implements Closeable {

	private String bookTitle;

	private String doctype = "<!DOCTYPE bookmap PUBLIC \"-//OASIS//DTD DITA 1.1 BookMap//EN\"  \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/bookmap.dtd\">";

	private String topicDoctype;

	private String topicFilenameSuffix = ".dita";

	private String topicFolder;

	private File targetFile;

	private DitaTopicDocumentBuilder currentTopic;

	private String latestHeadingId;

	private File currentTopicFile;

	private final Stack<Integer> headingLevels = new Stack<Integer>();

	private boolean mapEntryOpen;

	private String titleText;

	private Writer currentTopicOut;

	private OutlineItem outline;

	public DitaBookMapDocumentBuilder(Writer out) {
		super(out);
	}

	public DitaBookMapDocumentBuilder(XmlStreamWriter writer) {
		super(writer);
	}

	@Override
	protected XmlStreamWriter createXmlStreamWriter(Writer out) {
		XmlStreamWriter writer = super.createXmlStreamWriter(out);
		return new FormattingXMLStreamWriter(writer);
	}

	/**
	 * the book title as it should appear in the bookmap
	 */
	public String getBookTitle() {
		return bookTitle;
	}

	/**
	 * the book title as it should appear in the bookmap
	 */
	public void setBookTitle(String bookTitle) {
		this.bookTitle = bookTitle;
	}

	/**
	 * the doctype to be used for topics, or null if the default is to be used
	 * 
	 * @see #getDoctype()
	 */
	public String getTopicDoctype() {
		return topicDoctype;
	}

	/**
	 * the doctype to be used for topics, or null if the default is to be used
	 * 
	 * @see #setDoctype(String)
	 */
	public void setTopicDoctype(String topicDoctype) {
		this.topicDoctype = topicDoctype;
	}

	/**
	 * the doctype to be used for the bookmap, or null if the default is to be used
	 */
	public String getDoctype() {
		return doctype;
	}

	/**
	 * the doctype to be used for the bookmap, or null if the default is to be used
	 */
	public void setDoctype(String doctype) {
		this.doctype = doctype;
	}

	/**
	 * the filename suffix to use when producing topics. Should include the leading dot '.', for example '.dita'. The
	 * default value is <code>.dita</code>.
	 */
	public String getTopicFilenameSuffix() {
		return topicFilenameSuffix;
	}

	/**
	 * the filename suffix to use when producing topics. Should include the leading dot '.', for example '.dita'. The
	 * default value is <code>.dita</code>.
	 */
	public void setTopicFilenameSuffix(String topicFilenameSuffix) {
		this.topicFilenameSuffix = topicFilenameSuffix;
	}

	/**
	 * the relative folder name of the folder in which topic files should be produced, or null if the files should be
	 * created within the same folder as the bookmap.
	 */
	public String getTopicFolder() {
		return topicFolder;
	}

	/**
	 * the relative folder name of the folder in which topic files should be produced, or null if the files should be
	 * created within the same folder as the bookmap.
	 */
	public void setTopicFolder(String topicFolder) {
		this.topicFolder = topicFolder;
	}

	/**
	 * the target output file of the bookmap. used to compute relative paths to topic files.
	 */
	public File getTargetFile() {
		return targetFile;
	}

	/**
	 * the target output file of the bookmap. used to compute relative paths to topic files.
	 */
	public void setTargetFile(File targetFile) {
		this.targetFile = targetFile;
	}

	private DitaTopicDocumentBuilder getCurrentTopic() {
		if (currentTopic == null) {
			try {
				currentTopicFile = computeFile(latestHeadingId);
				currentTopicOut = new OutputStreamWriter(new BufferedOutputStream(
						new FileOutputStream(currentTopicFile)), "utf-8");
			} catch (IOException e1) {
				throw new IllegalStateException(e1);
			}

			// create a DITA map entry
			String relativeTopic = currentTopicFile.getName();
			if (topicFolder != null) {
				relativeTopic = topicFolder + '/' + relativeTopic;
			}
			writer.writeEmptyElement("chapter");
			writer.writeAttribute("href", relativeTopic);
			titleText = "";
			mapEntryOpen = true;

			currentTopic = new DitaTopicDocumentBuilder(currentTopicOut);
			if (topicDoctype != null) {
				currentTopic.setDoctype(topicDoctype);
			}
			currentTopic.setOutline(outline);
			currentTopic.setFilename(currentTopicFile.getName());
			currentTopic.beginDocument();
		}
		return currentTopic;
	}

	private File computeFile(String headingId) {
		String name = DitaTopicDocumentBuilder.computeName(headingId, topicFilenameSuffix);

		File folder = targetFile.getParentFile();
		if (topicFolder != null) {
			folder = new File(folder, topicFolder);
		}
		return new File(folder, name);
	}

	@Override
	public void acronym(String text, String definition) {
		getCurrentTopic().acronym(text, definition);
	}

	@Override
	public void beginBlock(BlockType type, Attributes attributes) {
		getCurrentTopic().beginBlock(type, attributes);
	}

	@Override
	public void beginHeading(int level, Attributes attributes) {
		headingLevels.push(level);
		if (level == 1) {
			closeCurrentTopic();
			latestHeadingId = attributes.getId();

		}
		getCurrentTopic().beginHeading(level, attributes);
	}

	@Override
	public void beginSpan(SpanType type, Attributes attributes) {
		getCurrentTopic().beginSpan(type, attributes);
	}

	@Override
	public void characters(String text) {
		if (mapEntryOpen) {
			titleText += text;
		}
		getCurrentTopic().characters(text);
	}

	@Override
	public void charactersUnescaped(String literal) {
		getCurrentTopic().charactersUnescaped(literal);
	}

	@Override
	public void endBlock() {
		getCurrentTopic().endBlock();
	}

	@Override
	public void beginDocument() {
		writer.writeStartDocument();
		writer.writeDTD(doctype);

		writer.writeStartElement("bookmap"); //$NON-NLS-1$
		writer.writeStartElement("title"); //$NON-NLS-1$
		if (bookTitle != null) {
			writer.writeCharacters(bookTitle);
		}
		writer.writeEndElement();

	}

	@Override
	public void endDocument() {
		closeCurrentTopic();

		writer.writeEndElement();
		writer.writeEndDocument();
	}

	private void closeCurrentTopic() {
		if (currentTopic != null) {
			currentTopic.endDocument();
			currentTopic = null;
			if (currentTopicOut != null) {
				try {
					currentTopicOut.close();
				} catch (IOException e) {
					throw new IllegalStateException(e);
				}
				currentTopicOut = null;
			}
		}
	}

	@Override
	public void endHeading() {
		int level = headingLevels.pop();

		if (level == 1) {
			if (mapEntryOpen) {
				mapEntryOpen = false;
				writer.writeAttribute("navtitle", titleText);
				titleText = null;
			}
		}
		getCurrentTopic().endHeading();
	}

	@Override
	public void endSpan() {
		getCurrentTopic().endSpan();
	}

	@Override
	public void entityReference(String entity) {
		getCurrentTopic().entityReference(entity);
	}

	@Override
	public void image(Attributes attributes, String url) {
		getCurrentTopic().image(attributes, url);
	}

	@Override
	public void imageLink(Attributes linkAttributes, Attributes imageAttributes, String href, String imageUrl) {
		getCurrentTopic().imageLink(linkAttributes, imageAttributes, href, imageUrl);
	}

	@Override
	public void lineBreak() {
		getCurrentTopic().lineBreak();
	}

	@Override
	public void link(Attributes attributes, String hrefOrHashName, String text) {
		getCurrentTopic().link(attributes, hrefOrHashName, text);
	}

	/**
	 * users of this class must call close when done with it.
	 */
	public void close() throws IOException {
		if (currentTopicOut != null) {
			try {
				currentTopicOut.close();
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
			currentTopicOut = null;
		}
	}

	/**
	 * the outline if available, otherwise null
	 * 
	 * {@link #setOutline(OutlineItem)}
	 */
	public OutlineItem getOutline() {
		return outline;
	}

	/**
	 * Set the outline of the document being parsed if xref URLs are to be correctly computed. OASIS DITA has its own
	 * URL syntax for DITA-specific links, which need some translation at the time that we build the document.
	 */
	public void setOutline(OutlineItem outline) {
		this.outline = outline;
	}

}