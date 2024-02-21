/*******************************************************************************
 * Copyright (c) 2009, 2024 David Green and others.
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
import org.eclipse.mylyn.wikitext.parser.builder.XslfoDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.parser.outline.OutlineItem;
import org.eclipse.mylyn.wikitext.parser.outline.OutlineParser;

/**
 * @author David Green
 */
public class MarkupToXslfoTask extends MarkupTask {

	private final List<FileSet> filesets = new ArrayList<>();

	protected String xslfoFilenameFormat = "$1.fo"; //$NON-NLS-1$

	protected boolean overwrite = true;

	protected File file;

	protected File targetdir;

	private boolean generateBookmarks = true;

	private final XslfoDocumentBuilder.Configuration configuration = new XslfoDocumentBuilder.Configuration();

	@Override
	public void execute() throws BuildException {
		if (file == null && filesets.isEmpty()) {
			throw new BuildException(Messages.getString("MarkupToXslfoTask.0")); //$NON-NLS-1$
		}
		if (file != null && !filesets.isEmpty()) {
			throw new BuildException(Messages.getString("MarkupToXslfoTask.1")); //$NON-NLS-1$
		}
		if (file != null) {
			if (!file.exists()) {
				throw new BuildException(MessageFormat.format(Messages.getString("MarkupToXslfoTask.2"), file)); //$NON-NLS-1$
			} else if (!file.isFile()) {
				throw new BuildException(MessageFormat.format(Messages.getString("MarkupToXslfoTask.3"), file)); //$NON-NLS-1$
			} else if (!file.canRead()) {
				throw new BuildException(MessageFormat.format(Messages.getString("MarkupToXslfoTask.4"), file)); //$NON-NLS-1$
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
								MessageFormat.format(Messages.getString("MarkupToXslfoTask.5"), inputFile, //$NON-NLS-1$
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
						MessageFormat.format(Messages.getString("MarkupToXslfoTask.6"), file, e.getMessage()), e); //$NON-NLS-1$
			}
		}
	}

	/**
	 * process the file
	 *
	 * @param baseDir
	 * @param source
	 * @return
	 * @return the lightweight markup, or null if the file was not written
	 * @throws BuildException
	 */
	protected String processFile(MarkupLanguage markupLanguage, final File baseDir, final File source)
			throws BuildException {

		log(MessageFormat.format(Messages.getString("MarkupToXslfoTask.7"), source), Project.MSG_VERBOSE); //$NON-NLS-1$

		String markupContent = null;

		String name = source.getName();
		if (name.lastIndexOf('.') != -1) {
			name = name.substring(0, name.lastIndexOf('.'));
		}

		File outputFile = computeXslfoFile(source, name);
		if (targetdir != null) {
			outputFile = new File(targetdir, outputFile.getName());
		}
		if (!outputFile.exists() || overwrite || outputFile.lastModified() < source.lastModified()) {

			if (markupContent == null) {
				markupContent = readFully(source);
			}

			performValidation(source, markupContent);

			try (Writer out = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(outputFile)),
					StandardCharsets.UTF_8)) {
				XslfoDocumentBuilder builder = new XslfoDocumentBuilder(out);
				XslfoDocumentBuilder.Configuration configuration = this.configuration.clone();
				if (configuration.getTitle() == null) {
					configuration.setTitle(name);
				}
				builder.setConfiguration(configuration);
				builder.setBase(source.getParentFile().toURI());

				MarkupParser parser = new MarkupParser();
				parser.setMarkupLanguage(markupLanguage);
				parser.setBuilder(builder);

				if (generateBookmarks) {
					OutlineItem outline = new OutlineParser(markupLanguage).parse(markupContent);
					builder.setOutline(outline);
				}

				parser.parse(markupContent);
			} catch (IOException e) {
				throw new BuildException(
						MessageFormat.format(Messages.getString("MarkupToXslfoTask.8"), outputFile, e.getMessage()), e); //$NON-NLS-1$
			}
		}
		return markupContent;
	}

	protected File computeXslfoFile(final File source, String name) {
		return new File(source.getParentFile(), xslfoFilenameFormat.replace("$1", name)); //$NON-NLS-1$
	}

	/**
	 * @see #setXslfoFilenameFormat(String)
	 */
	public String getXslfoFilenameFormat() {
		return xslfoFilenameFormat;
	}

	/**
	 * The format of the XSL-FO output file. Consists of a pattern where the '$1' is replaced with the filename of the input file. Default
	 * value is <code>$1.fo</code>
	 */
	public void setXslfoFilenameFormat(String filenameFormat) {
		xslfoFilenameFormat = filenameFormat;
	}

	/**
	 * the file to process
	 */
	public File getFile() {
		return file;
	}

	/**
	 * the file to process
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * Adds a set of files to process.
	 */
	public void addFileset(FileSet set) {
		filesets.add(set);
	}

	public File getTargetdir() {
		return targetdir;
	}

	public void setTargetdir(File targetdir) {
		this.targetdir = targetdir;
	}

	public String getAuthor() {
		return configuration.getAuthor();
	}

	public String getCopyright() {
		return configuration.getCopyright();
	}

	public String getDate() {
		return configuration.getDate();
	}

	public float getFontSize() {
		return configuration.getFontSize();
	}

	public float[] getFontSizeMultipliers() {
		return configuration.getFontSizeMultipliers();
	}

	public float getPageHeight() {
		return configuration.getPageHeight();
	}

	public float getPageMargin() {
		return configuration.getPageMargin();
	}

	public float getPageWidth() {
		return configuration.getPageWidth();
	}

	public String getSubTitle() {
		return configuration.getSubTitle();
	}

	public String getTitle() {
		return configuration.getTitle();
	}

	public String getVersion() {
		return configuration.getVersion();
	}

	public boolean isPageBreakOnHeading1() {
		return configuration.isPageBreakOnHeading1();
	}

	public boolean isPageNumbering() {
		return configuration.isPageNumbering();
	}

	public boolean isPanelText() {
		return configuration.isPanelText();
	}

	public boolean isShowExternalLinks() {
		return configuration.isShowExternalLinks();
	}

	public boolean isUnderlineLinks() {
		return configuration.isUnderlineLinks();
	}

	public void setAuthor(String author) {
		configuration.setAuthor(author);
	}

	public void setCopyright(String copyright) {
		configuration.setCopyright(copyright);
	}

	public void setDate(String date) {
		configuration.setDate(date);
	}

	public void setFontSize(float fontSize) {
		configuration.setFontSize(fontSize);
	}

	public void setFontSizeMultipliers(float[] fontSizeMultipliers) {
		configuration.setFontSizeMultipliers(fontSizeMultipliers);
	}

	public void setPageBreakOnHeading1(boolean pageBreakOnHeading1) {
		configuration.setPageBreakOnHeading1(pageBreakOnHeading1);
	}

	public void setPageHeight(float pageHeight) {
		configuration.setPageHeight(pageHeight);
	}

	public void setPageMargin(float pageMargin) {
		configuration.setPageMargin(pageMargin);
	}

	public void setPageNumbering(boolean pageNumbering) {
		configuration.setPageNumbering(pageNumbering);
	}

	public void setPageWidth(float pageWidth) {
		configuration.setPageWidth(pageWidth);
	}

	public void setPanelText(boolean panelText) {
		configuration.setPanelText(panelText);
	}

	public void setShowExternalLinks(boolean showExternalLinks) {
		configuration.setShowExternalLinks(showExternalLinks);
	}

	public void setSubTitle(String subTitle) {
		configuration.setSubTitle(subTitle);
	}

	public void setTitle(String title) {
		configuration.setTitle(title);
	}

	public void setUnderlineLinks(boolean underlineLinks) {
		configuration.setUnderlineLinks(underlineLinks);
	}

	public void setVersion(String version) {
		configuration.setVersion(version);
	}

	/**
	 *
	 */
	public boolean isGenerateBookmarks() {
		return generateBookmarks;
	}

	/**
	 *
	 */
	public void setGenerateBookmarks(boolean generateBookmarks) {
		this.generateBookmarks = generateBookmarks;
	}

}
