/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
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
import java.io.IOException;
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
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.XslfoDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;

/**
 * 
 * @author David Green
 * 
 * @since 1.1
 */
public class MarkupToXslfoTask extends MarkupTask {

	private final List<FileSet> filesets = new ArrayList<FileSet>();

	protected String xslfoFilenameFormat = "$1.fo"; //$NON-NLS-1$

	protected boolean overwrite = true;

	protected File file;

	protected String title;

	protected String subTitle;

	protected File targetdir;

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
						throw new BuildException(MessageFormat.format(
								Messages.getString("MarkupToXslfoTask.5"), inputFile, //$NON-NLS-1$
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
						Messages.getString("MarkupToXslfoTask.6"), file, e.getMessage()), e); //$NON-NLS-1$
			}
		}
	}

	/**
	 * process the file
	 * 
	 * @param baseDir
	 * @param source
	 * @return
	 * 
	 * @return the lightweight markup, or null if the file was not written
	 * 
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

			Writer out;
			try {
				out = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(outputFile)), "utf-8"); //$NON-NLS-1$
			} catch (Exception e) {
				throw new BuildException(MessageFormat.format(
						Messages.getString("MarkupToXslfoTask.8"), outputFile, e.getMessage()), e); //$NON-NLS-1$
			}
			try {
				XslfoDocumentBuilder builder = new XslfoDocumentBuilder(out);
				builder.setTitle(title == null ? name : title);
				builder.setSubTitle(subTitle);
				builder.setBase(source.getParentFile().toURI());

				MarkupParser parser = new MarkupParser();
				parser.setMarkupLanguage(markupLanguage);
				parser.setBuilder(builder);

				parser.parse(markupContent);
			} finally {
				try {
					out.close();
				} catch (Exception e) {
					throw new BuildException(MessageFormat.format(
							Messages.getString("MarkupToXslfoTask.9"), outputFile, //$NON-NLS-1$
							e.getMessage()), e);
				}
			}
		}
		return markupContent;
	}

	protected File computeXslfoFile(final File source, String name) {
		return new File(source.getParentFile(), xslfoFilenameFormat.replace("$1", name)); //$NON-NLS-1$
	}

	protected String readFully(File inputFile) {
		StringBuilder w = new StringBuilder((int) inputFile.length());
		try {
			Reader r = new InputStreamReader(new BufferedInputStream(new FileInputStream(inputFile)));
			try {
				int i;
				while ((i = r.read()) != -1) {
					w.append((char) i);
				}
			} finally {
				r.close();
			}
		} catch (IOException e) {
			throw new BuildException(MessageFormat.format(
					Messages.getString("MarkupToXslfoTask.10"), inputFile, e.getMessage()), e); //$NON-NLS-1$
		}
		return w.toString();
	}

	/**
	 * @see #setXslfoFilenameFormat(String)
	 */
	public String getXslfoFilenameFormat() {
		return xslfoFilenameFormat;
	}

	/**
	 * The format of the XSL-FO output file. Consists of a pattern where the '$1' is replaced with the filename of the
	 * input file. Default value is <code>$1.fo</code>
	 */
	public void setXslfoFilenameFormat(String filenameFormat) {
		this.xslfoFilenameFormat = filenameFormat;
	}

	/**
	 * The document title, as it appears in the head
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * The document title, as it appears in the head
	 */
	public void setTitle(String title) {
		this.title = title;
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

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

}
