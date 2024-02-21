/*******************************************************************************
 * Copyright (c) 2007, 2024 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     Alexander Fedorov (ArSysOp) - ongoing support
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.ant.internal;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import org.apache.tools.ant.BuildException;
import org.eclipse.mylyn.wikitext.parser.util.MarkupToEclipseToc;
import org.eclipse.mylyn.wikitext.splitter.SplitOutlineItem;
import org.eclipse.mylyn.wikitext.splitter.SplittingMarkupToEclipseToc;

/**
 * An Ant task for converting lightweight markup such as Textile to eclipse help format.
 *
 * @author David Green
 */
public class MarkupToEclipseHelpTask extends MarkupToHtmlTask {

	private String xmlFilenameFormat = "$1-toc.xml"; //$NON-NLS-1$

	private String helpPrefix;

	private int tocAnchorLevel = 0;

	@Override
	void processed(String markupContent, SplitOutlineItem item, final File baseDir, final File source) {
		super.processed(markupContent, item, baseDir, source);

		String name = source.getName();
		if (name.lastIndexOf('.') != -1) {
			name = name.substring(0, name.lastIndexOf('.'));
		}

		File tocOutputFile = computeTocFile(source, name);
		if (!tocOutputFile.exists() || overwrite || tocOutputFile.lastModified() < source.lastModified()) {
			File htmlOutputFile = computeHtmlFile(source, name);
			try (Writer writer = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(tocOutputFile)),
					StandardCharsets.UTF_8)){

				MarkupToEclipseToc toEclipseToc = new SplittingMarkupToEclipseToc();

				toEclipseToc.setHelpPrefix(helpPrefix);
				toEclipseToc.setAnchorLevel(tocAnchorLevel);
				System.out.println("Help: " + baseDir + " " + htmlOutputFile); //$NON-NLS-1$//$NON-NLS-2$
				toEclipseToc.setBookTitle(title == null ? name : title);
				toEclipseToc.setCopyrightNotice(getCopyrightNotice());

				String basePath = baseDir.getAbsolutePath().replace('\\', '/');
				String outputFilePath = htmlOutputFile.getAbsolutePath().replace('\\', '/');
				if (outputFilePath.startsWith(basePath)) {
					String filePath = outputFilePath.substring(basePath.length());
					if (filePath.startsWith("/")) { //$NON-NLS-1$
						filePath = filePath.substring(1);
					}
					if (filePath.lastIndexOf('/') != -1) {
						String relativePart = filePath.substring(0, filePath.lastIndexOf('/'));
						toEclipseToc.setHelpPrefix(helpPrefix == null ? relativePart : helpPrefix + '/' + relativePart);
					}
				}

				toEclipseToc.setHtmlFile(htmlOutputFile.getName());

				String tocXml = toEclipseToc.createToc(item);

				try {
					writer.write(tocXml);
				} catch (Exception e) {
					throw new BuildException(String.format("Cannot write to file '%s': %s", tocXml, e.getMessage()), e); //$NON-NLS-1$
				}
			} catch (IOException e) {
				throw new BuildException(String.format("Cannot write to file '%s': %s", tocOutputFile, e.getMessage()), //$NON-NLS-1$
						e);
			}
		}
	}

	private File computeTocFile(File source, String name) {
		return new File(source.getParentFile(), xmlFilenameFormat.replace("$1", name)); //$NON-NLS-1$
	}

	/**
	 * @see #setXmlFilenameFormat(String)
	 */
	public String getXmlFilenameFormat() {
		return xmlFilenameFormat;
	}

	/**
	 * The format of the XML table of contents output file. Consists of a pattern where the '$1' is replaced with the filename of the input
	 * file. Default value is <code>$1-toc.xml</code>
	 */
	public void setXmlFilenameFormat(String xmlFilenameFormat) {
		this.xmlFilenameFormat = xmlFilenameFormat;
	}

	/**
	 * the prefix to URLs in the toc.xml, typically the relative path from the plugin to the help files. For example, if the help file is in
	 * 'help/index.html' then the help prefix would be 'help'
	 */
	public String getHelpPrefix() {
		return helpPrefix;
	}

	/**
	 * the prefix to URLs in the toc.xml, typically the relative path from the plugin to the help files. For example, if the help file is in
	 * 'help/index.html' then the help prefix would be 'help'
	 */
	public void setHelpPrefix(String helpPrefix) {
		this.helpPrefix = helpPrefix;
	}

	/**
	 * Indicates the heading level at which anchors of the form {@code &lt;anchor id="additions"/&gt;} should be emitted. A level of 0
	 * corresponds to the root of the document, and levels 1-6 correspond to heading levels h1, h2...h6.
	 * <p>
	 * The default level is 0 (the document root)
	 * </p>
	 */
	public int getTocAnchorLevel() {
		return tocAnchorLevel;
	}

	/**
	 * @see #getTocAnchorLevel()
	 */
	public void setTocAnchorLevel(int tocAnchorLevel) {
		this.tocAnchorLevel = tocAnchorLevel;
	}

}
