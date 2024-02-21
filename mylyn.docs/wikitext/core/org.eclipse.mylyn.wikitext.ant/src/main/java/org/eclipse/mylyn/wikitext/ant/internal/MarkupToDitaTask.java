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
import org.eclipse.mylyn.wikitext.parser.builder.DitaBookMapDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.builder.DitaTopicDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.parser.outline.OutlineItem;
import org.eclipse.mylyn.wikitext.parser.outline.OutlineParser;
import org.eclipse.mylyn.wikitext.util.DefaultXmlStreamWriter;
import org.eclipse.mylyn.wikitext.util.FormattingXMLStreamWriter;

/**
 * An Ant task for converting markup to OASIS DITA format.
 *
 * @author David Green
 */
public class MarkupToDitaTask extends MarkupTask {
	public enum BreakStrategy {
		/**
		 * do not break: all output is one topic
		 */
		NONE,
		/**
		 * break on level-1 headings
		 */
		LEVEL1,
		/**
		 * break on the first level heading found in the document
		 */
		FIRST
	}

	private final List<FileSet> filesets = new ArrayList<>();

	private String filenameFormat;

	private String bookTitle;

	private boolean overwrite = true;

	protected File file;

	private String doctype;

	private String topicDoctype;

	private String topicFolder = "topics"; //$NON-NLS-1$

	private BreakStrategy topicStrategy = BreakStrategy.FIRST;

	private boolean formatting = true;

	/**
	 * Adds a set of files to process.
	 */
	public void addFileset(FileSet set) {
		filesets.add(set);
	}

	@Override
	public void execute() throws BuildException {

		if (file == null && filesets.isEmpty()) {
			throw new BuildException(Messages.getString("MarkupToDitaTask.1")); //$NON-NLS-1$
		}
		if (file != null && !filesets.isEmpty()) {
			throw new BuildException(Messages.getString("MarkupToDitaTask.2")); //$NON-NLS-1$
		}
		if (file != null) {
			if (!file.exists()) {
				throw new BuildException(MessageFormat.format(Messages.getString("MarkupToDitaTask.3"), file)); //$NON-NLS-1$
			} else if (!file.isFile()) {
				throw new BuildException(MessageFormat.format(Messages.getString("MarkupToDitaTask.4"), file)); //$NON-NLS-1$
			} else if (!file.canRead()) {
				throw new BuildException(MessageFormat.format(Messages.getString("MarkupToDitaTask.5"), file)); //$NON-NLS-1$
			}
		}

		if (filenameFormat == null) {
			filenameFormat = switch (topicStrategy) {
				case NONE -> "$1.dita"; //$NON-NLS-1$
				default -> "$1.ditamap"; //$NON-NLS-1$
			};
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
								MessageFormat.format(Messages.getString("MarkupToDitaTask.6"), inputFile, //$NON-NLS-1$
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
						MessageFormat.format(Messages.getString("MarkupToDitaTask.7"), file, e.getMessage()), e); //$NON-NLS-1$
			}
		}
	}

	private void processFile(MarkupLanguage markupLanguage, final File baseDir, final File source)
			throws BuildException {

		log(MessageFormat.format(Messages.getString("MarkupToDitaTask.8"), source), Project.MSG_VERBOSE); //$NON-NLS-1$

		String markupContent = null;

		String name = source.getName();
		if (name.lastIndexOf('.') != -1) {
			name = name.substring(0, name.lastIndexOf('.'));
		}
		File outputFile = new File(source.getParentFile(), filenameFormat.replace("$1", name)); //$NON-NLS-1$
		if (!outputFile.exists() || overwrite || outputFile.lastModified() < source.lastModified()) {
			if (markupContent == null) {
				markupContent = readFully(source);
			}
			performValidation(source, markupContent);

			OutlineItem outline = new OutlineParser(markupLanguage).parse(markupContent);

			try (Writer writer = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(outputFile)),
					StandardCharsets.UTF_8)) {
				if (topicStrategy == BreakStrategy.NONE) {
					DitaTopicDocumentBuilder builder = new DitaTopicDocumentBuilder(new DefaultXmlStreamWriter(writer),
							formatting);
					builder.setRootTopicTitle(bookTitle);

					MarkupParser parser = new MarkupParser();
					parser.setMarkupLanguage(markupLanguage);
					parser.setBuilder(builder);
					if (topicDoctype != null) {
						builder.setDoctype(topicDoctype);
					}
					builder.setFilename(outputFile.getName());
					builder.setOutline(outline);

					parser.parse(markupContent);
				} else {

					try (DitaBookMapDocumentBuilder builder = new DitaBookMapDocumentBuilder(formatting
							? new FormattingXMLStreamWriter(new DefaultXmlStreamWriter(writer))
									: new DefaultXmlStreamWriter(writer))) {
						builder.setFormattingDependencies(formatting);

						MarkupParser parser = new MarkupParser();
						parser.setMarkupLanguage(markupLanguage);
						parser.setBuilder(builder);

						builder.setBookTitle(bookTitle == null ? name : bookTitle);

						if (doctype != null) {
							builder.setDoctype(doctype);
						}
						if (topicDoctype != null) {
							builder.setTopicDoctype(topicDoctype);
						}
						builder.setTargetFile(outputFile);
						builder.setTopicFolder(topicFolder);
						builder.setOutline(outline);
						switch (topicStrategy) {
							case FIRST:
								if (!outline.getChildren().isEmpty()) {
									builder.setTopicBreakLevel(outline.getChildren().get(0).getLevel());
								} else {
									builder.setTopicBreakLevel(1);
								}
								break;
							case LEVEL1:
								builder.setTopicBreakLevel(1);
								break;
						}

						parser.parse(markupContent);
					} catch (IOException e) {
						throw new BuildException(MessageFormat.format(Messages.getString("MarkupToDitaTask.12"), outputFile, //$NON-NLS-1$
								e.getMessage()), e);
					}
				}
			} catch (Exception e) {
				throw new BuildException(MessageFormat.format(Messages.getString("MarkupToDitaTask.11"), outputFile, //$NON-NLS-1$
						e.getMessage()), e);
			}
		}
	}

	/**
	 * The format of the DocBook output file. Consists of a pattern where the '$1' is replaced with the filename of the input file. Default
	 * value is <code>$1.ditamap</code>
	 *
	 * @see #setFilenameFormat(String)
	 */
	public String getFilenameFormat() {
		return filenameFormat;
	}

	/**
	 * The format of the DocBook output file. Consists of a pattern where the '$1' is replaced with the filename of the input file. Default
	 * value is <code>$1.ditamap</code>
	 */
	public void setFilenameFormat(String filenameFormat) {
		this.filenameFormat = filenameFormat;
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
	 * Set the XML doctype of the ditamap. The doctype should look something like this:
	 *
	 * <pre>
	 * &lt;!DOCTYPE bookmap PUBLIC \&quot;-//OASIS//DTD DITA 1.1 BookMap//EN\&quot;  \&quot;http://docs.oasis-open.org/dita/v1.1/OS/dtd/bookmap.dtd\&quot;&gt;
	 * </pre>
	 */
	public void setDoctype(String doctype) {
		this.doctype = doctype;
	}

	/**
	 * The XML doctype of the ditamap.
	 */
	public String getDoctype() {
		return doctype;
	}

	/**
	 * the XML doctype of topics
	 */
	public String getTopicDoctype() {
		return topicDoctype;
	}

	/**
	 * the XML doctype of topics The doctype should look something like this:
	 *
	 * <pre>
	 * &lt;!DOCTYPE topic PUBLIC \&quot;-//OASIS//DTD DITA 1.1 Topic//EN\&quot;&gt;
	 * </pre>
	 */
	public void setTopicDoctype(String topicDoctype) {
		this.topicDoctype = topicDoctype;
	}

	public boolean isOverwrite() {
		return overwrite;
	}

	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

	public String getTopicFolder() {
		return topicFolder;
	}

	public void setTopicFolder(String topicFolder) {
		this.topicFolder = topicFolder;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public BreakStrategy getTopicStrategy() {
		return topicStrategy;
	}

	public void setTopicStrategy(BreakStrategy topicStrategy) {
		this.topicStrategy = topicStrategy;
	}

	/**
	 * Indicate if the dita output should be formatted
	 */
	public boolean isFormatting() {
		return formatting;
	}

	/**
	 * Indicate if the dita output should be formatted
	 */
	public void setFormatting(boolean formatting) {
		this.formatting = formatting;
	}

}
