/*******************************************************************************
 * Copyright (c) 2011, 2014 Torkild U. Resheim.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Torkild U. Resheim - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.docs.epub.core.wikitext;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.eclipse.mylyn.docs.epub.core.Publication;
import org.eclipse.mylyn.docs.epub.opf.Item;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder.Stylesheet;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.util.XmlStreamWriter;

/**
 * This type can be used to populate an EPUB Publication with content directly from WikiText markup. The markup file
 * will be converted to XHTML. (Strictly this file should be converted to the subset of XHTML called <i>Open Publication
 * Structure</i>).
 *
 * @author Torkild U. Resheim
 * @since 1.0
 */
public class MarkupToOPS {

	private MarkupLanguage markupLanguage;

	/**
	 * Parses the markup file and populates the publication with the result.
	 *
	 * @param ops
	 *            the publication the content will be added to
	 * @param markupFile
	 *            the WikiText markup file
	 * @return the temporary folder used for generating the HTML from markup
	 * @since 2.0
	 */
	public File parse(Publication ops, File markupFile) throws IOException, FileNotFoundException {
		if (markupLanguage == null) {
			throw new IllegalStateException("must set markupLanguage"); //$NON-NLS-1$
		}
		// Create a temporary working folder
		File workingFolder = File.createTempFile("wikitext_", null); //$NON-NLS-1$
		if (workingFolder.delete() && workingFolder.mkdirs()) {
			File htmlFile = new File(workingFolder.getAbsolutePath() + File.separator + "markup.html"); //$NON-NLS-1$
			FileWriter out = new FileWriter(htmlFile);
			HtmlDocumentBuilder builder = new HtmlDocumentBuilder(out) {
				@Override
				protected XmlStreamWriter createXmlStreamWriter(Writer out) {
					return super.createFormattingXmlStreamWriter(out);
				}
			};

			List<Item> stylesheets = ops.getItemsByMIMEType(Publication.MIMETYPE_CSS);
			for (Item item : stylesheets) {
				File file = new File(item.getFile());
				Stylesheet css = new Stylesheet(file);
				builder.addCssStylesheet(css);
			}
			// Make sure we get the correct XHTML header
			builder.setEmitDtd(true);
			builder.setHtmlDtd(
					"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">"); //$NON-NLS-1$
			builder.setXhtmlStrict(true);

			MarkupParser markupParser = new MarkupParser();

			markupParser.setBuilder(builder);
			markupParser.setMarkupLanguage(markupLanguage);
			markupParser.parse(new FileReader(markupFile));
			ops.setGenerateToc(true);
			ops.setIncludeReferencedResources(true);
			Item item = ops.addItem(htmlFile);
			item.setSourcePath(markupFile.getAbsolutePath());
		}
		return workingFolder;
	}

	/**
	 * Sets the markup language to use when generating HTML from markup.
	 *
	 * @param markupLanguage
	 *            the markup language
	 */
	public void setMarkupLanguage(MarkupLanguage markupLanguage) {
		this.markupLanguage = markupLanguage;
	}

}
