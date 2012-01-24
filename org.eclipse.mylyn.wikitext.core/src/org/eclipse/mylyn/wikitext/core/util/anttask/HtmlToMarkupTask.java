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

package org.eclipse.mylyn.wikitext.core.util.anttask;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.HtmlParser;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.xml.sax.InputSource;

/**
 * An Ant task to generate wiki markup from HTML sources. For best results ensure that jsoup is on the classpath.
 * 
 * @author David Green
 * @since 1.6
 */
public class HtmlToMarkupTask extends MarkupTask {

	private final List<FileSet> filesets = new ArrayList<FileSet>();

	protected File file;

	protected String outputFilenameFormat = "$1.$2"; //$NON-NLS-1$

	protected boolean overwrite = true;

	@Override
	public void execute() throws BuildException {
		if (file == null && filesets.isEmpty()) {
			throw new BuildException(Messages.getString("MarkupToHtmlTask.1")); //$NON-NLS-1$
		}
		if (file != null && !filesets.isEmpty()) {
			throw new BuildException(Messages.getString("MarkupToHtmlTask.2")); //$NON-NLS-1$
		}
		if (file != null) {
			if (!file.exists()) {
				throw new BuildException(MessageFormat.format(Messages.getString("MarkupToHtmlTask.3"), file)); //$NON-NLS-1$
			} else if (!file.isFile()) {
				throw new BuildException(MessageFormat.format(Messages.getString("MarkupToHtmlTask.4"), file)); //$NON-NLS-1$
			} else if (!file.canRead()) {
				throw new BuildException(MessageFormat.format(Messages.getString("MarkupToHtmlTask.5"), file)); //$NON-NLS-1$
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
						throw new BuildException(MessageFormat.format(
								Messages.getString("MarkupToHtmlTask.11"), inputFile, //$NON-NLS-1$
								e.getMessage()), e);
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
				throw new BuildException(MessageFormat.format(
						Messages.getString("MarkupToHtmlTask.12"), file, e.getMessage()), e); //$NON-NLS-1$
			}
		}
	}

	private void processFile(MarkupLanguage markupLanguage, File folder, File source) {

		log(MessageFormat.format(Messages.getString("MarkupToHtmlTask.14"), source), Project.MSG_VERBOSE); //$NON-NLS-1$

		if (isValidate()) {
			log(MessageFormat.format(Messages.getString("HtmlToMarkupTask.1"), source), Project.MSG_WARN); //$NON-NLS-1$
		}

		String name = source.getName();
		if (name.lastIndexOf('.') != -1) {
			name = name.substring(0, name.lastIndexOf('.'));
		}

		File outputFile = computeTargetFile(markupLanguage, source, name);
		if (!outputFile.exists() || overwrite || outputFile.lastModified() < source.lastModified()) {

			Writer writer;
			try {
				writer = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(outputFile)), "utf-8"); //$NON-NLS-1$
			} catch (Exception e) {
				throw new BuildException(MessageFormat.format(
						Messages.getString("MarkupToHtmlTask.16"), outputFile, e.getMessage()), e); //$NON-NLS-1$
			}
			try {
				DocumentBuilder builder = markupLanguage.createDocumentBuilder(writer);

				Reader input;
				InputStream in;
				try {
					in = new BufferedInputStream(new FileInputStream(source));
					input = getSourceEncoding() == null ? new InputStreamReader(in) : new InputStreamReader(in,
							getSourceEncoding());
				} catch (Exception e) {
					throw new BuildException(MessageFormat.format(
							Messages.getString("MarkupTask.cannotReadSource"), source, e.getMessage()), e); //$NON-NLS-1$
				}
				try {
					new HtmlParser().parse(new InputSource(input), builder);
				} catch (Exception e) {
					throw new BuildException(MessageFormat.format(
							Messages.getString("HtmlToMarkupTask.failedToProcessContent"), source, e.getMessage()), e); //$NON-NLS-1$
				} finally {
					try {
						in.close();
					} catch (Exception e) {
						throw new BuildException(MessageFormat.format(
								Messages.getString("MarkupTask.cannotReadSource"), source, e.getMessage()), e); //$NON-NLS-1$
					}
				}
			} finally {
				try {
					writer.close();
				} catch (Exception e) {
					throw new BuildException(MessageFormat.format(
							Messages.getString("MarkupToHtmlTask.17"), outputFile, //$NON-NLS-1$
							e.getMessage()), e);
				}
			}
		}
	}

	private File computeTargetFile(MarkupLanguage markupLanguage, File source, String name) {
		return new File(source.getParentFile(),
				outputFilenameFormat.replace("$1", name).replace("$2", markupLanguage.getName().toLowerCase())); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * The format of the HTML output file. Consists of a pattern where the '$1' is replaced with the filename of the
	 * input file, $2 is replaced with the markup language name (in lower-case). Default value is <code>$1.$2</code>
	 */
	public String getOutputFilenameFormat() {
		return outputFilenameFormat;
	}

	/**
	 * The format of the HTML output file. Consists of a pattern where the '$1' is replaced with the filename of the
	 * input file, $2 is replaced with the markup language name (in lower-case). Default value is <code>$1.$2</code>
	 */
	public void setOutputFilenameFormat(String outputFilenameFormat) {
		this.outputFilenameFormat = outputFilenameFormat;
	}

	/**
	 * indicate if target files should be overwritten even if their timestamps are newer than the source files.
	 */
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

	/**
	 * Adds a set of files to process.
	 */
	public void addFileset(FileSet set) {
		filesets.add(set);
	}
}
