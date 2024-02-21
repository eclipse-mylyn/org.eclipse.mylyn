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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.eclipse.mylyn.wikitext.ant.MarkupTask;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.builder.DocBookDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.util.XmlStreamWriter;

/**
 * An Ant task for converting markup to docbook format.
 *
 * @author David Green
 */
public class MarkupToDocbookTask extends MarkupTask {
	private final List<FileSet> filesets = new ArrayList<>();

	private String docbookFilenameFormat = "$1.xml"; //$NON-NLS-1$

	private String bookTitle;

	private boolean overwrite = true;

	protected File file;

	private String doctype;

	/**
	 * Adds a set of files to process.
	 */
	public void addFileset(FileSet set) {
		filesets.add(set);
	}

	@Override
	public void execute() throws BuildException {

		if (file == null && filesets.isEmpty()) {
			throw new BuildException(Messages.getString("MarkupToDocbookTask.1")); //$NON-NLS-1$
		}
		if (file != null && !filesets.isEmpty()) {
			throw new BuildException(Messages.getString("MarkupToDocbookTask.2")); //$NON-NLS-1$
		}
		if (file != null) {
			if (!file.exists()) {
				throw new BuildException(MessageFormat.format(Messages.getString("MarkupToDocbookTask.3"), file)); //$NON-NLS-1$
			} else if (!file.isFile()) {
				throw new BuildException(MessageFormat.format(Messages.getString("MarkupToDocbookTask.4"), file)); //$NON-NLS-1$
			} else if (!file.canRead()) {
				throw new BuildException(MessageFormat.format(Messages.getString("MarkupToDocbookTask.5"), file)); //$NON-NLS-1$
			}
		}

		MarkupLanguage markupLanguage = createMarkupLanguage();

		for (FileSet fileset : filesets) {

			File filesetBaseDir = fileset.getDir(getProject());
			DirectoryScanner ds = fileset.getDirectoryScanner(getProject());

			String[] files = ds.getIncludedFiles();
			if (files != null) {
				File baseDir = ds.getBasedir();
				for (String file : files) {
					File inputFile = new File(baseDir, file);
					try {
						processFile(markupLanguage, filesetBaseDir, inputFile);
					} catch (BuildException e) {
						throw e;
					} catch (Exception e) {
						throw new BuildException(
								MessageFormat.format(Messages.getString("MarkupToDocbookTask.6"), inputFile, //$NON-NLS-1$
										e.getMessage()),
								e);
					}
				}
			}
		}

		if (file != null) {
			try {
				processFile(markupLanguage, file.getParentFile(), file);
			} catch (BuildException e) {
				throw e;
			} catch (Exception e) {
				throw new BuildException(
						MessageFormat.format(Messages.getString("MarkupToDocbookTask.7"), file, e.getMessage()), e); //$NON-NLS-1$
			}
		}
	}

	private void processFile(MarkupLanguage markupLanguage, final File baseDir, final File source)
			throws BuildException {

		log(MessageFormat.format(Messages.getString("MarkupToDocbookTask.8"), source), Project.MSG_VERBOSE); //$NON-NLS-1$

		String markupContent = null;

		String name = source.getName();
		if (name.lastIndexOf('.') != -1) {
			name = name.substring(0, name.lastIndexOf('.'));
		}

		File docbookOutputFile = new File(source.getParentFile(), docbookFilenameFormat.replace("$1", name)); //$NON-NLS-1$
		if (!docbookOutputFile.exists() || overwrite || docbookOutputFile.lastModified() < source.lastModified()) {

			if (markupContent == null) {
				markupContent = readFully(source);
			}
			performValidation(source, markupContent);

			try (Writer writer = new OutputStreamWriter(
					new BufferedOutputStream(new FileOutputStream(docbookOutputFile)), StandardCharsets.UTF_8)) {
				DocBookDocumentBuilder builder = new DocBookDocumentBuilder(writer) {
					@Override
					protected XmlStreamWriter createXmlStreamWriter(Writer out) {
						return super.createFormattingXmlStreamWriter(out);
					}
				};
				MarkupParser parser = new MarkupParser();
				parser.setMarkupLanguage(markupLanguage);
				parser.setBuilder(builder);
				builder.setBookTitle(bookTitle == null ? name : bookTitle);
				if (doctype != null) {
					builder.setDoctype(doctype);
				}
				parser.parse(markupContent);
			} catch (IOException e) {
				throw new BuildException(
						MessageFormat.format(Messages.getString("MarkupToDocbookTask.11"), docbookOutputFile, //$NON-NLS-1$
								e.getMessage()),
						e);
			}
		}

	}

	/**
	 * The format of the DocBook output file. Consists of a pattern where the '$1' is replaced with the filename of the input file. Default
	 * value is <code>$1.xml</code>
	 *
	 * @see #setDocbookFilenameFormat(String)
	 */
	public String getDocbookFilenameFormat() {
		return docbookFilenameFormat;
	}

	/**
	 * The format of the DocBook output file. Consists of a pattern where the '$1' is replaced with the filename of the input file. Default
	 * value is <code>$1.xml</code>
	 *
	 * @param docbookFilenameFormat
	 */
	public void setDocbookFilenameFormat(String docbookFilenameFormat) {
		this.docbookFilenameFormat = docbookFilenameFormat;
	}

	/**
	 * Get the book title.
	 *
	 * @return the title, or null if the source filename is to be used as the title.
	 */
	public String getBookTitle() {
		return bookTitle;
	}

	/**
	 * The book title.
	 *
	 * @param bookTitle
	 *            the title, or null if the source filename is to be used as the title.
	 */
	public void setBookTitle(String bookTitle) {
		this.bookTitle = bookTitle;
	}

	/**
	 * Set the XML doctype of the docbook. The doctype should look something like this:
	 * <code>&lt;!DOCTYPE book PUBLIC "-//OASIS//DTD DocBook XML V4.5//EN" "http://www.oasis-open.org/docbook/xml/4.5/docbookx.dtd"&gt;</code>
	 */
	public void setDoctype(String doctype) {
		this.doctype = doctype;
	}

	/**
	 * The XML doctype of the docbook.
	 */
	public String getDoctype() {
		return doctype;
	}

	public boolean isOverwrite() {
		return overwrite;
	}

	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

}
