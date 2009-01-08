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
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.eclipse.mylyn.internal.wikitext.core.parser.builder.DefaultSplittingStrategy;
import org.eclipse.mylyn.internal.wikitext.core.parser.builder.NoSplittingStrategy;
import org.eclipse.mylyn.internal.wikitext.core.parser.builder.SplitOutlineItem;
import org.eclipse.mylyn.internal.wikitext.core.parser.builder.SplittingHtmlDocumentBuilder;
import org.eclipse.mylyn.internal.wikitext.core.parser.builder.SplittingOutlineParser;
import org.eclipse.mylyn.internal.wikitext.core.parser.builder.SplittingStrategy;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;

/**
 * An Ant task for converting lightweight markup to HTML format.
 * 
 * @author David Green
 * @since 1.0
 */
public class MarkupToHtmlTask extends MarkupTask {
	private final List<FileSet> filesets = new ArrayList<FileSet>();

	protected String htmlFilenameFormat = "$1.html"; //$NON-NLS-1$

	protected boolean overwrite = true;

	private final List<Stylesheet> stylesheets = new ArrayList<Stylesheet>();

	protected File file;

	protected String title;

	protected String linkRel;

	protected boolean multipleOutputFiles = false;

	protected boolean formatOutput = false;

	protected boolean navigationImages = false;

	protected String prependImagePrefix = null;

	private boolean useInlineCssStyles = true;

	private boolean suppressBuiltInCssStyles = false;

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
				throw new BuildException(String.format(Messages.getString("MarkupToHtmlTask.3"), file)); //$NON-NLS-1$
			} else if (!file.isFile()) {
				throw new BuildException(String.format(Messages.getString("MarkupToHtmlTask.4"), file)); //$NON-NLS-1$
			} else if (!file.canRead()) {
				throw new BuildException(String.format(Messages.getString("MarkupToHtmlTask.5"), file)); //$NON-NLS-1$
			}
		}

		MarkupLanguage markupLanguage = createMarkupLanguage();

		for (Stylesheet stylesheet : stylesheets) {
			if (stylesheet.url == null && stylesheet.file == null) {
				throw new BuildException(Messages.getString("MarkupToHtmlTask.6")); //$NON-NLS-1$
			}
			if (stylesheet.url != null && stylesheet.file != null) {
				throw new BuildException(Messages.getString("MarkupToHtmlTask.7")); //$NON-NLS-1$
			}
			if (stylesheet.file != null) {
				if (!stylesheet.file.exists()) {
					throw new BuildException(Messages.getString("MarkupToHtmlTask.8") + stylesheet.file); //$NON-NLS-1$
				}
				if (!stylesheet.file.isFile()) {
					throw new BuildException(Messages.getString("MarkupToHtmlTask.9") + stylesheet.file); //$NON-NLS-1$
				}
				if (!stylesheet.file.canRead()) {
					throw new BuildException(Messages.getString("MarkupToHtmlTask.10") + stylesheet.file); //$NON-NLS-1$
				}
			}
		}

		Set<File> outputFolders = new HashSet<File>();

		for (FileSet fileset : filesets) {

			File filesetBaseDir = fileset.getDir(getProject());
			DirectoryScanner ds = fileset.getDirectoryScanner(getProject());

			String[] files = ds.getIncludedFiles();
			if (files != null) {
				File baseDir = ds.getBasedir();
				for (String file : files) {
					File inputFile = new File(baseDir, file);
					testForOutputFolderConflict(outputFolders, inputFile);
					try {
						processFile(markupLanguage, filesetBaseDir, inputFile);
					} catch (BuildException e) {
						throw e;
					} catch (Exception e) {
						throw new BuildException(String.format(Messages.getString("MarkupToHtmlTask.11"), inputFile, //$NON-NLS-1$
								e.getMessage()), e);
					}
				}
			}
		}
		if (file != null) {
			testForOutputFolderConflict(outputFolders, file);
			try {
				processFile(markupLanguage, file.getParentFile(), file);
			} catch (BuildException e) {
				throw e;
			} catch (Exception e) {
				throw new BuildException(
						String.format(Messages.getString("MarkupToHtmlTask.12"), file, e.getMessage()), e); //$NON-NLS-1$
			}
		}
	}

	private void testForOutputFolderConflict(Set<File> outputFolders, File inputFile) {
		if (multipleOutputFiles && !outputFolders.add(inputFile.getAbsoluteFile().getParentFile())) {
			log(String.format(Messages.getString("MarkupToHtmlTask.13")), Project.MSG_WARN); //$NON-NLS-1$
		}
	}

	/**
	 * process the file
	 * 
	 * @param baseDir
	 * @param source
	 * 
	 * @return the lightweight markup, or null if the file was not written
	 * 
	 * @throws BuildException
	 */
	protected String processFile(MarkupLanguage markupLanguage, final File baseDir, final File source)
			throws BuildException {

		log(String.format(Messages.getString("MarkupToHtmlTask.14"), source), Project.MSG_VERBOSE); //$NON-NLS-1$

		String markupContent = null;

		String name = source.getName();
		if (name.lastIndexOf('.') != -1) {
			name = name.substring(0, name.lastIndexOf('.'));
		}

		File htmlOutputFile = computeHtmlFile(source, name);
		if (!htmlOutputFile.exists() || overwrite || htmlOutputFile.lastModified() < source.lastModified()) {

			if (markupContent == null) {
				markupContent = readFully(source);
			}

			performValidation(source, markupContent);

			Writer writer;
			try {
				writer = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(htmlOutputFile)), "utf-8"); //$NON-NLS-1$
			} catch (Exception e) {
				throw new BuildException(String.format(
						Messages.getString("MarkupToHtmlTask.16"), htmlOutputFile, e.getMessage()), e); //$NON-NLS-1$
			}
			try {
				HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer, formatOutput);
				for (Stylesheet stylesheet : stylesheets) {
					if (stylesheet.url != null) {
						builder.addCssStylesheet(stylesheet.url);
					} else {
						builder.addCssStylesheet(stylesheet.file);
					}
				}

				builder.setTitle(title == null ? name : title);
				builder.setEmitDtd(true);
				builder.setUseInlineStyles(useInlineCssStyles);
				builder.setSuppressBuiltInStyles(suppressBuiltInCssStyles);
				builder.setLinkRel(linkRel);
				builder.setPrependImagePrefix(prependImagePrefix);

				SplittingStrategy splittingStrategy = multipleOutputFiles ? new DefaultSplittingStrategy()
						: new NoSplittingStrategy();
				SplittingOutlineParser outlineParser = new SplittingOutlineParser();
				outlineParser.setMarkupLanguage(markupLanguage.clone());
				outlineParser.setSplittingStrategy(splittingStrategy);
				SplitOutlineItem item = outlineParser.parse(markupContent);
				item.setSplitTarget(htmlOutputFile.getName());
				SplittingHtmlDocumentBuilder splittingBuilder = new SplittingHtmlDocumentBuilder();
				splittingBuilder.setRootBuilder(builder);
				splittingBuilder.setOutline(item);
				splittingBuilder.setRootFile(htmlOutputFile);
				splittingBuilder.setNavigationImages(navigationImages);
				splittingBuilder.setFormatting(formatOutput);

				MarkupParser parser = new MarkupParser();
				parser.setMarkupLanaguage(markupLanguage);
				parser.setBuilder(splittingBuilder);

				parser.parse(markupContent);

				processed(markupContent, item, baseDir, source);
			} finally {
				try {
					writer.close();
				} catch (Exception e) {
					throw new BuildException(String.format(Messages.getString("MarkupToHtmlTask.17"), htmlOutputFile, //$NON-NLS-1$
							e.getMessage()), e);
				}
			}
		}
		return markupContent;
	}

	void processed(String markupContent, SplitOutlineItem item, final File baseDir, final File source) {
	}

	protected File computeHtmlFile(final File source, String name) {
		return new File(source.getParentFile(), htmlFilenameFormat.replace("$1", name)); //$NON-NLS-1$
	}

	protected String readFully(File inputFile) {
		StringWriter w = new StringWriter();
		try {
			Reader r = new InputStreamReader(new BufferedInputStream(new FileInputStream(inputFile)));
			try {
				int i;
				while ((i = r.read()) != -1) {
					w.write((char) i);
				}
			} finally {
				r.close();
			}
		} catch (IOException e) {
			throw new BuildException(
					String.format(Messages.getString("MarkupToHtmlTask.19"), inputFile, e.getMessage()), e); //$NON-NLS-1$
		}
		return w.toString();
	}

	/**
	 * @see #setHtmlFilenameFormat(String)
	 */
	public String getHtmlFilenameFormat() {
		return htmlFilenameFormat;
	}

	/**
	 * The format of the HTML output file. Consists of a pattern where the '$1' is replaced with the filename of the
	 * input file. Default value is <code>$1.html</code>
	 * 
	 * @param htmlFilenameFormat
	 */
	public void setHtmlFilenameFormat(String htmlFilenameFormat) {
		this.htmlFilenameFormat = htmlFilenameFormat;
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

	public void addStylesheet(Stylesheet stylesheet) {
		if (stylesheet == null) {
			throw new IllegalArgumentException();
		}
		stylesheets.add(stylesheet);
	}

	/**
	 * indicate if output should be generated to multiple output files.
	 */
	public boolean isMultipleOutputFiles() {
		return multipleOutputFiles;
	}

	/**
	 * indicate if output should be generated to multiple output files.
	 */
	public void setMultipleOutputFiles(boolean multipleOutputFiles) {
		this.multipleOutputFiles = multipleOutputFiles;
	}

	/**
	 * indicate if the output should be formatted
	 */
	public boolean isFormatOutput() {
		return formatOutput;
	}

	/**
	 * indicate if the output should be formatted
	 */
	public void setFormatOutput(boolean formatOutput) {
		this.formatOutput = formatOutput;
	}

	/**
	 * indicate if navigation links should be images
	 */
	public boolean isNavigationImages() {
		return navigationImages;
	}

	/**
	 * indicate if navigation links should be images
	 */
	public void setNavigationImages(boolean navigationImages) {
		this.navigationImages = navigationImages;
	}

	/**
	 * @see HtmlDocumentBuilder#isUseInlineStyles()
	 */
	public boolean isUseInlineCssStyles() {
		return useInlineCssStyles;
	}

	/**
	 * @see HtmlDocumentBuilder#isUseInlineStyles()
	 */
	public void setUseInlineCssStyles(boolean useInlineCssStyles) {
		this.useInlineCssStyles = useInlineCssStyles;
	}

	/**
	 * @see HtmlDocumentBuilder#isSuppressBuiltInStyles()
	 */
	public boolean isSuppressBuiltInCssStyles() {
		return suppressBuiltInCssStyles;
	}

	/**
	 * @see HtmlDocumentBuilder#isSuppressBuiltInStyles()
	 */
	public void setSuppressBuiltInCssStyles(boolean suppressBuiltInCssStyles) {
		this.suppressBuiltInCssStyles = suppressBuiltInCssStyles;
	}

	public static class Stylesheet {
		private File file;

		private String url;

		public File getFile() {
			return file;
		}

		public void setFile(File file) {
			this.file = file;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}
	}

	/**
	 * The 'rel' value for HTML links. If specified the value is applied to all links generated by the builder. The
	 * default value is null.
	 * 
	 * Setting this value to "nofollow" is recommended for rendering HTML in areas where users may add links, for
	 * example in a blog comment. See <a
	 * href="http://en.wikipedia.org/wiki/Nofollow">http://en.wikipedia.org/wiki/Nofollow</a> for more information.
	 */
	public String getLinkRel() {
		return linkRel;
	}

	/**
	 * The 'rel' value for HTML links. If specified the value is applied to all links generated by the builder. The
	 * default value is null.
	 * 
	 * Setting this value to "nofollow" is recommended for rendering HTML in areas where users may add links, for
	 * example in a blog comment. See <a
	 * href="http://en.wikipedia.org/wiki/Nofollow">http://en.wikipedia.org/wiki/Nofollow</a> for more information.
	 */
	public void setLinkRel(String linkRel) {
		this.linkRel = linkRel;
	}

	public String getPrependImagePrefix() {
		return prependImagePrefix;
	}

	public void setPrependImagePrefix(String prependImagePrefix) {
		this.prependImagePrefix = prependImagePrefix;
	}

	/**
	 * indicate if target files should be overwritten even if their timestamps are newer than the source files.
	 */
	public boolean isOverwrite() {
		return overwrite;
	}

	/**
	 * indicate if target files should be overwritten even if their timestamps are newer than the source files.
	 */
	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

}
