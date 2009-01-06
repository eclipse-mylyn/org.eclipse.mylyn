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
package org.eclipse.mylyn.wikitext.core.parser.util;

import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineItem;
import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineParser;
import org.eclipse.mylyn.wikitext.core.util.DefaultXmlStreamWriter;
import org.eclipse.mylyn.wikitext.core.util.FormattingXMLStreamWriter;
import org.eclipse.mylyn.wikitext.core.util.XmlStreamWriter;

/**
 * A conversion utility targeting the <a href="http://help.eclipse.org/help33/index.jsp?topic=/org.eclipse.platform.doc.isv/reference/extension-points/org_eclipse_help_toc.html"
 * >Eclipse help table of contents format</a>.
 * 
 * @author David Green
 */
public class MarkupToEclipseToc {

	private String bookTitle;

	private String htmlFile;

	private MarkupLanguage markupLanguage;

	private String helpPrefix;

	public String parse(String markupContent) {
		if (markupLanguage == null) {
			throw new IllegalStateException("Must set markupLanguage"); //$NON-NLS-1$
		}
		OutlineParser parser = new OutlineParser(markupLanguage);

		OutlineItem root = parser.parse(markupContent);

		return createToc(root);
	}

	public String createToc(OutlineItem root) {
		StringWriter out = new StringWriter(8096);

		XmlStreamWriter writer = createXmlStreamWriter(out);

		writer.writeStartDocument("utf-8", "1.0"); //$NON-NLS-1$ //$NON-NLS-2$

		writer.writeStartElement("toc"); //$NON-NLS-1$
		writer.writeAttribute("topic", adjustForPrefix(getHtmlFile())); //$NON-NLS-1$
		writer.writeAttribute("label", getBookTitle()); //$NON-NLS-1$

		emitToc(writer, root.getChildren());

		writer.writeEndElement(); // toc

		writer.writeEndDocument();
		writer.close();

		return out.toString();
	}

	private void emitToc(XmlStreamWriter writer, List<OutlineItem> children) {
		for (OutlineItem item : children) {
			writer.writeStartElement("topic"); //$NON-NLS-1$

			String file = computeFile(item);

			file = adjustForPrefix(file);

			String suffix = ""; //$NON-NLS-1$

			// bug 260065: only append document anchor name if this is not the first item in the file.
			OutlineItem previous = item.getPrevious();
			if (previous != null && previous.getParent() != null) {
				String fileOfPrevious = computeFile(previous);
				fileOfPrevious = adjustForPrefix(fileOfPrevious);

				if (file.equals(fileOfPrevious)) {
					suffix = "#" + item.getId(); //$NON-NLS-1$
				}
			}

			writer.writeAttribute("href", file + suffix); //$NON-NLS-1$ 
			writer.writeAttribute("label", item.getLabel()); //$NON-NLS-1$

			if (!item.getChildren().isEmpty()) {
				emitToc(writer, item.getChildren());
			}

			writer.writeEndElement(); // topic
		}
	}

	private String adjustForPrefix(String file) {
		if (helpPrefix != null) {
			if (helpPrefix.endsWith("/")) { //$NON-NLS-1$
				file = helpPrefix + file;
			} else {
				file = helpPrefix + '/' + file;
			}
		}
		return file;
	}

	protected String computeFile(OutlineItem item) {
		return getHtmlFile();
	}

	public MarkupLanguage getMarkupLanguage() {
		return markupLanguage;
	}

	public void setMarkupLanguage(MarkupLanguage markupLanguage) {
		this.markupLanguage = markupLanguage;
	}

	public String getBookTitle() {
		return bookTitle;
	}

	public void setBookTitle(String bookTitle) {
		this.bookTitle = bookTitle;
	}

	public String getHtmlFile() {
		return htmlFile;
	}

	public void setHtmlFile(String htmlFile) {
		this.htmlFile = htmlFile;
	}

	protected XmlStreamWriter createXmlStreamWriter(Writer out) {
		XmlStreamWriter writer = new DefaultXmlStreamWriter(out);
		return new FormattingXMLStreamWriter(writer);
	}

	/**
	 * the prefix to URLs in the toc.xml, typically the relative path from the plugin to the help files. For example, if
	 * the help file is in 'help/index.html' then the help prefix would be 'help'
	 */
	public void setHelpPrefix(String helpPrefix) {
		this.helpPrefix = helpPrefix;
	}

}
