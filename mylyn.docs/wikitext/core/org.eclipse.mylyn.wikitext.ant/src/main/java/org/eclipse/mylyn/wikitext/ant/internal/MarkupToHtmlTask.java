/*******************************************************************************
 * Copyright (c) 2007, 2012 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     Peter Stibrany - bug 294383
 *     Torkild U. Resheim - Handle links when transforming file based wiki
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.eclipse.mylyn.wikitext.ant.MarkupTask;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.builder.JavadocShortcutUriProcessor;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.splitter.DefaultSplittingStrategy;
import org.eclipse.mylyn.wikitext.splitter.NoSplittingStrategy;
import org.eclipse.mylyn.wikitext.splitter.SplitOutlineItem;
import org.eclipse.mylyn.wikitext.splitter.SplittingHtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.splitter.SplittingOutlineParser;
import org.eclipse.mylyn.wikitext.splitter.SplittingStrategy;

/**
 * An Ant task for converting lightweight markup to HTML format.
 *
 * @author David Green
 * @author Torkild U. Resheim
 */
public class MarkupToHtmlTask extends MarkupTask {
	private final List<FileSet> filesets = new ArrayList<>();

	protected String htmlFilenameFormat = "$1.html"; //$NON-NLS-1$

	protected boolean overwrite = true;

	private final List<Stylesheet> stylesheets = new ArrayList<>();

	protected File file;

	protected String title;

	protected String linkRel;

	protected boolean multipleOutputFiles = false;

	protected boolean formatOutput = false;

	protected boolean navigationImages = false;

	protected String prependImagePrefix = null;

	private boolean useInlineCssStyles = true;

	private boolean suppressBuiltInCssStyles = false;

	private String defaultAbsoluteLinkTarget;

	private boolean xhtmlStrict = false;

	private boolean emitDoctype = true;

	private String htmlDoctype = null;

	private String copyrightNotice = null;

	private String javadocRelativePath = null;

	private String javadocBasePackageName = null;

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

		Set<File> outputFolders = new HashSet<>();

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
						throw new BuildException(
								MessageFormat.format(Messages.getString("MarkupToHtmlTask.11"), inputFile, //$NON-NLS-1$
										e.getMessage()),
								e);
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
						MessageFormat.format(Messages.getString("MarkupToHtmlTask.12"), file, e.getMessage()), e); //$NON-NLS-1$
			}
		}
	}

	private void testForOutputFolderConflict(Set<File> outputFolders, File inputFile) {
		if (multipleOutputFiles) {
			File outputFolder = inputFile.getAbsoluteFile().getParentFile();
			if (!outputFolders.add(outputFolder)) {
				log(MessageFormat.format(Messages.getString("MarkupToHtmlTask.13"), outputFolder), Project.MSG_WARN); //$NON-NLS-1$
			}
		}
	}

	/**
	 * process the file
	 *
	 * @param baseDir
	 * @param source
	 * @return the lightweight markup, or null if the file was not written
	 * @throws BuildException
	 */
	protected String processFile(MarkupLanguage markupLanguage, final File baseDir, final File source)
			throws BuildException {

		log(MessageFormat.format(Messages.getString("MarkupToHtmlTask.14"), source), Project.MSG_VERBOSE); //$NON-NLS-1$

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
			try (Writer writer = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(htmlOutputFile)),
					StandardCharsets.UTF_8)) {
				HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer, formatOutput);
				for (Stylesheet stylesheet : stylesheets) {
					HtmlDocumentBuilder.Stylesheet builderStylesheet;

					if (stylesheet.url != null) {
						builderStylesheet = new HtmlDocumentBuilder.Stylesheet(stylesheet.url);
					} else {
						builderStylesheet = new HtmlDocumentBuilder.Stylesheet(stylesheet.file);
					}
					builder.addCssStylesheet(builderStylesheet);

					if (!stylesheet.attributes.isEmpty()) {
						for (Map.Entry<String, String> attr : stylesheet.attributes.entrySet()) {
							builderStylesheet.getAttributes().put(attr.getKey(), attr.getValue());
						}
					}
				}

				builder.setTitle(title == null ? name : title);
				builder.setEmitDtd(emitDoctype);
				if (emitDoctype && htmlDoctype != null) {
					builder.setHtmlDtd(htmlDoctype);
				}
				builder.setUseInlineStyles(useInlineCssStyles);
				builder.setSuppressBuiltInStyles(suppressBuiltInCssStyles);
				builder.setLinkRel(linkRel);
				builder.setDefaultAbsoluteLinkTarget(defaultAbsoluteLinkTarget);
				builder.setPrependImagePrefix(prependImagePrefix);
				builder.setXhtmlStrict(xhtmlStrict);
				builder.setCopyrightNotice(copyrightNotice);
				builder.setHtmlFilenameFormat(htmlFilenameFormat);
				if (javadocRelativePath != null || javadocBasePackageName != null) {
					builder.addLinkUriProcessor(
							new JavadocShortcutUriProcessor(javadocRelativePath, javadocBasePackageName));
				}

				SplittingStrategy splittingStrategy = multipleOutputFiles
						? new DefaultSplittingStrategy()
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
				parser.setMarkupLanguage(markupLanguage);
				parser.setBuilder(splittingBuilder);

				parser.parse(markupContent);

				processed(markupContent, item, baseDir, source);
			} catch (IOException e) {
				throw new BuildException(
						MessageFormat.format(Messages.getString("MarkupToHtmlTask.16"), htmlOutputFile, e.getMessage()), //$NON-NLS-1$
						e);
			}
		}
		return markupContent;
	}

	void processed(String markupContent, SplitOutlineItem item, final File baseDir, final File source) {
	}

	protected File computeHtmlFile(final File source, String name) {
		return new File(source.getParentFile(), htmlFilenameFormat.replace("$1", name)); //$NON-NLS-1$
	}

	/**
	 * @see #setHtmlFilenameFormat(String)
	 */
	public String getHtmlFilenameFormat() {
		return htmlFilenameFormat;
	}

	/**
	 * The format of the HTML output file. Consists of a pattern where the '$1' is replaced with the filename of the input file. Default
	 * value is <code>$1.html</code>
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

	public static class Attribute {
		private String name;

		private String value;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

	public static class Stylesheet {
		private File file;

		private String url;

		private final Map<String, String> attributes = new HashMap<>();

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

		public void addConfiguredAttribute(Attribute attribute) {
			attributes.put(attribute.getName(), attribute.getValue());
		}
	}

	/**
	 * The 'rel' value for HTML links. If specified the value is applied to all links generated by the builder. The default value is null.
	 * Setting this value to "nofollow" is recommended for rendering HTML in areas where users may add links, for example in a blog comment.
	 * See <a href="http://en.wikipedia.org/wiki/Nofollow">http://en.wikipedia.org/wiki/Nofollow</a> for more information.
	 */
	public String getLinkRel() {
		return linkRel;
	}

	/**
	 * The 'rel' value for HTML links. If specified the value is applied to all links generated by the builder. The default value is null.
	 * Setting this value to "nofollow" is recommended for rendering HTML in areas where users may add links, for example in a blog comment.
	 * See <a href="http://en.wikipedia.org/wiki/Nofollow">http://en.wikipedia.org/wiki/Nofollow</a> for more information.
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

	/**
	 * A default target attribute for links that have absolute (not relative) urls. By default this value is null. Setting this value will
	 * cause all HTML anchors to have their target attribute set if it's not explicitly specified.
	 */
	public String getDefaultAbsoluteLinkTarget() {
		return defaultAbsoluteLinkTarget;
	}

	/**
	 * A default target attribute for links that have absolute (not relative) urls. By default this value is null. Setting this value will
	 * cause all HTML anchors to have their target attribute set if it's not explicitly specified.
	 */
	public void setDefaultAbsoluteLinkTarget(String defaultAbsoluteLinkTarget) {
		this.defaultAbsoluteLinkTarget = defaultAbsoluteLinkTarget;
	}

	/**
	 * Indicate if the builder should attempt to conform to strict XHTML rules. The default is false.
	 *
	 * @see HtmlDocumentBuilder#isXhtmlStrict()
	 */
	public boolean isXhtmlStrict() {
		return xhtmlStrict;
	}

	/**
	 * Indicate if the builder should attempt to conform to strict XHTML rules. The default is false.
	 *
	 * @see HtmlDocumentBuilder#isXhtmlStrict()
	 */
	public void setXhtmlStrict(boolean xhtmlStrict) {
		this.xhtmlStrict = xhtmlStrict;
	}

	/**
	 * Indicate if the builder should emit DOCTYPE declaration. Default is true.
	 *
	 * @see HtmlDocumentBuilder#isEmitDtd()
	 */
	public boolean getEmitDoctype() {
		return emitDoctype;
	}

	/**
	 * Indicate if the builder should emit DOCTYPE declaration. Default is true.
	 *
	 * @see HtmlDocumentBuilder#isEmitDtd()
	 */
	public void setEmitDoctype(boolean emitDtd) {
		emitDoctype = emitDtd;
	}

	/**
	 * The DTD to use in the output document. Ignored if {@link #getEmitDoctype() emitDoctype} is false.
	 *
	 * @see HtmlDocumentBuilder#isEmitDtd()
	 * @return the DTD to use, or null if the default DTD should be used
	 */
	public String getHtmlDoctype() {
		return htmlDoctype;
	}

	/**
	 * The DTD to use in the output document. Ignored if {@link #getEmitDoctype() emitDoctype} is false. The doctype should take the form:
	 * <code>&lt;!DOCTYPE html ...&gt;</code>
	 *
	 * @param htmlDoctype
	 *            the DTD to use, or null if the default DTD should be used
	 */
	public void setHtmlDoctype(String htmlDoctype) {
		this.htmlDoctype = htmlDoctype;
	}

	/**
	 * the copyright notice that should appear in the generated output
	 */
	public String getCopyrightNotice() {
		return copyrightNotice;
	}

	/**
	 * the copyright notice that should appear in the generated output
	 *
	 * @param copyrightNotice
	 *            the notice, or null if there should be none
	 */
	public void setCopyrightNotice(String copyrightNotice) {
		this.copyrightNotice = copyrightNotice;
	}

	/**
	 * Provides the relative path to related javadoc documentation.
	 *
	 * @return the relative path, or null
	 * @see JavadocShortcutUriProcessor
	 * @since 3.0.26
	 */
	public String getJavadocRelativePath() {
		return javadocRelativePath;
	}

	/**
	 * Sets the relative path to related javadoc documentation.
	 *
	 * @param javadocRelativePath
	 *            the relative path, or null
	 * @see JavadocShortcutUriProcessor
	 * @since 3.0.26
	 */
	public void setJavadocRelativePath(String javadocRelativePath) {
		this.javadocRelativePath = javadocRelativePath;
	}

	/**
	 * Provides the base Java package name of the related javadoc documentation.
	 *
	 * @return the package name, or null
	 * @see JavadocShortcutUriProcessor
	 * @since 3.0.26
	 */
	public String getJavadocBasePackageName() {
		return javadocBasePackageName;
	}

	/**
	 * Sets the base Java package name of the related javadoc documentation.
	 *
	 * @param javadocBasePackageName
	 *            the package name, or null
	 * @see JavadocShortcutUriProcessor
	 * @since 3.0.26
	 */
	public void setJavadocBasePackageName(String javadocBasePackageName) {
		this.javadocBasePackageName = javadocBasePackageName;
	}

}
