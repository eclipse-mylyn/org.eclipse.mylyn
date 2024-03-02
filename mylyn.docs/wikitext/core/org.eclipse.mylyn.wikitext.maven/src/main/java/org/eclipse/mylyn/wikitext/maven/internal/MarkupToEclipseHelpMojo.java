/*******************************************************************************
 * Copyright (c) 2013, 2024 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.maven.internal;

import static java.text.MessageFormat.format;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.parser.util.MarkupToEclipseToc;
import org.eclipse.mylyn.wikitext.splitter.DefaultSplittingStrategy;
import org.eclipse.mylyn.wikitext.splitter.NoSplittingStrategy;
import org.eclipse.mylyn.wikitext.splitter.SplitOutlineItem;
import org.eclipse.mylyn.wikitext.splitter.SplittingHtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.splitter.SplittingMarkupToEclipseToc;
import org.eclipse.mylyn.wikitext.splitter.SplittingOutlineParser;
import org.eclipse.mylyn.wikitext.splitter.SplittingStrategy;
import org.eclipse.mylyn.wikitext.util.ServiceLocator;

@Mojo(name = "eclipse-help", defaultPhase = LifecyclePhase.COMPILE)
public class MarkupToEclipseHelpMojo extends AbstractMojo {
	/**
	 * Output folder.
	 */
	@Parameter(defaultValue = "${project.build.directory}/generated-eclipse-help", required = true)
	protected File outputFolder;

	/**
	 * Source folder.
	 */
	@Parameter(defaultValue = "${basedir}/src/main/docs", required = true)
	protected File sourceFolder;

	/**
	 * The filename format to use when generating output filenames for HTML files. Defaults to {@code $1.html} where
	 * {@code $1} is the name of the source file without extension.
	 */
	@Parameter
	protected String htmlFilenameFormat = "$1.html"; //$NON-NLS-1$

	/**
	 * The filename format to use when generating output filenames for Eclipse help table of contents XML files.
	 * Defaults to {@code $1-toc.xml} where {@code $1} is the name of the source file without extension.
	 */
	@Parameter
	protected String xmlFilenameFormat = "$1-toc.xml"; //$NON-NLS-1$

	/**
	 * Specify the title of the output document. If unspecified, the title is the filename (without extension).
	 */
	@Parameter
	protected String title;

	/**
	 * The 'rel' value for HTML links. If specified the value is applied to all generated links. The default value is
	 * null.
	 */
	@Parameter
	protected String linkRel;

	/**
	 * Indicate if output should be generated to multiple output files (true/false). Default is false.
	 */
	@Parameter
	protected boolean multipleOutputFiles = false;

	@Parameter(defaultValue = "utf-8")
	private final String sourceEncoding = "utf-8"; //$NON-NLS-1$

	/**
	 * Indicate if the output should be formatted (true/false). Default is false.
	 */
	@Parameter
	protected boolean formatOutput = false;

	/**
	 * Indicate if navigation links should be images (true/false). Only applicable for multi-file output. Default is
	 * false.
	 */
	@Parameter
	protected boolean navigationImages = false;

	/**
	 * If specified, the prefix is prepended to relative image urls.
	 */
	@Parameter
	protected String prependImagePrefix = null;

	@Parameter
	protected boolean useInlineCssStyles = true;

	@Parameter
	protected boolean suppressBuiltInCssStyles = false;

	/**
	 * Specify that hyperlinks to external resources (&lt;a href) should use a target attribute to cause them to be
	 * opened in a seperate window or tab. The value specified becomes the value of the target attribute on anchors
	 * where the href is an absolute URL.
	 */
	@Parameter
	protected String defaultAbsoluteLinkTarget;

	/**
	 * Indicate if the builder should attempt to conform to strict XHTML rules. The default is false.
	 */
	@Parameter
	protected boolean xhtmlStrict = false;

	/**
	 * Indicate if the builder should emit a DTD doctype declaration. The default is true.
	 */
	@Parameter
	protected boolean emitDoctype = true;

	/**
	 * The doctype to use. Defaults to
	 * {@code &lt;!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"&gt;}
	 * .
	 */
	@Parameter
	protected String htmlDoctype = null;

	/**
	 * The copyright notice to include in generated output files.
	 */
	@Parameter
	protected String copyrightNotice = null;

	/**
	 * The list of CSS stylesheet URLs relative to the {@link #sourceFolder}.
	 */
	@Parameter
	protected List<String> stylesheetUrls = new ArrayList<>();

	/**
	 * the prefix to URLs in the toc.xml, typically the relative path from the plugin to the help files. For example, if
	 * the help file is in 'help/index.html' then the help prefix would be 'help'
	 */
	@Parameter
	protected String helpPrefix;

	/**
	 * Indicates the heading level at which anchors of the form {@code &lt;anchor id="additions"/&gt;} should be
	 * emitted. A level of 0 corresponds to the root of the document, and levels 1-6 correspond to heading levels h1,
	 * h2...h6.
	 * <p>
	 * The default level is 0 (the document root)
	 * </p>
	 */
	@Parameter
	protected int tocAnchorLevel = 0;

	/**
	 * Indicates whether an embedded table of contents is generated. When true, a table of contents is generated in each
	 * HTML page. Using CSS the table of contents can be positioned on the left hand side in a column, with portions of
	 * the table of contents expanded or collapsed.
	 * <p>
	 * Defaults to false.
	 * </p>
	 */
	@Parameter
	protected boolean embeddedTableOfContents = false;

	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			ensureOutputFolderExists();
			ensureSourceFolderExists();

			ServiceLocator serviceLocator = ServiceLocator.getInstance(MarkupToEclipseHelpMojo.class.getClassLoader());
			Set<MarkupLanguage> markupLanguages = serviceLocator.getAllMarkupLanguages();
			if (markupLanguages.isEmpty()) {
				throw new MojoFailureException("No markup languages are available"); //$NON-NLS-1$
			}

			getLog().info(
					format("Generating Eclipse help content from sources: {0} -> {1}", sourceFolder, outputFolder)); //$NON-NLS-1$

			final FileToMarkupLanguage fileToMarkupLanguage = new FileToMarkupLanguage(markupLanguages);
			SourceFileTraversal fileTraversal = new SourceFileTraversal(sourceFolder);

			final AtomicInteger fileCount = new AtomicInteger();
			fileTraversal.traverse((relativePath, sourceFile) -> {
				fileCount.incrementAndGet();

				process(sourceFile, relativePath, fileToMarkupLanguage.get(sourceFile));
			});
			getLog().info(format("Processed {0} files", fileCount.get())); //$NON-NLS-1$
		} catch (BuildFailureException e) {
			getLog().error(e.getMessage(), e);
			throw new MojoFailureException(e.getMessage(), e.getCause());
		}
	}

	protected void process(File sourceFile, String relativePath, MarkupLanguage markupLanguage) {
		if (markupLanguage == null) {
			copy(sourceFile, relativePath);
		} else {
			processMarkup(sourceFile, relativePath, markupLanguage);
		}
	}

	private void copy(File sourceFile, String relativePath) {
		File targetFolder = new File(outputFolder, relativePath);
		ensureFolderExists("target folder", targetFolder, true); //$NON-NLS-1$
		File targetFile = new File(targetFolder, sourceFile.getName());
		try {
			Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new BuildFailureException(
					format("Cannot copy {0} to {1}: {2}", sourceFile, targetFile, e.getMessage()), e); //$NON-NLS-1$
		}
	}

	protected void processMarkup(File sourceFile, String relativePath, MarkupLanguage markupLanguage) {
		getLog().info(format("Processing markup file: {0}", sourceFile)); //$NON-NLS-1$

		String name = sourceFile.getName();
		if (name.lastIndexOf('.') != -1) {
			name = name.substring(0, name.lastIndexOf('.'));
		}
		File htmlOutputFile = computeHtmlFile(relativePath, name);
		if (!htmlOutputFile.exists() || htmlOutputFile.lastModified() < sourceFile.lastModified()) {
			String markupContent = readFully(sourceFile);

			if (!htmlOutputFile.getParentFile().exists()) {
				if (!htmlOutputFile.getParentFile().mkdirs()) {
					throw new BuildFailureException(format("Cannot create folder {0}", htmlOutputFile.getParentFile())); //$NON-NLS-1$
				}
			}

			Writer writer = createWriter(htmlOutputFile);
			try {
				HtmlDocumentBuilder builder = createRootBuilder(writer, name, relativePath);

				SplittingStrategy splittingStrategy = createSplittingStrategy();
				SplittingOutlineParser outlineParser = createOutlineParser(markupLanguage, splittingStrategy);

				SplitOutlineItem rootTocItem = outlineParser.parse(markupContent);
				rootTocItem.setSplitTarget(htmlOutputFile.getName());

				SplittingHtmlDocumentBuilder splittingBuilder = createSplittingBuilder(builder, rootTocItem,
						htmlOutputFile, relativePath);

				MarkupParser parser = new MarkupParser();
				parser.setMarkupLanguage(markupLanguage);
				parser.setBuilder(splittingBuilder);

				parser.parse(markupContent);

				createEclipseHelpToc(rootTocItem, sourceFile, relativePath, htmlOutputFile, name);
			} finally {
				close(writer, htmlOutputFile);
			}
		}
	}

	private void close(Writer writer, File file) {
		try {
			writer.close();
		} catch (IOException e) {
			throw new BuildFailureException(format("Cannot write to file {0}: {1}", file, e.getMessage())); //$NON-NLS-1$
		}
	}

	private void createEclipseHelpToc(SplitOutlineItem rootTocItem, File sourceFile, String relativePath,
			File htmlOutputFile, String name) {
		File tocOutputFile = computeTocFile(htmlOutputFile, name);
		if (!tocOutputFile.exists() || tocOutputFile.lastModified() < sourceFile.lastModified()) {
			Writer writer = createWriter(tocOutputFile);
			try {
				MarkupToEclipseToc toEclipseToc = createMarkupToEclipseToc(relativePath, htmlOutputFile, name);
				String tocXml = toEclipseToc.createToc(rootTocItem);

				writer.write(tocXml);
			} catch (IOException e) {
				throw new BuildFailureException(format("Cannot write to file {0}: {1}", tocOutputFile, e.getMessage()), //$NON-NLS-1$
						e);
			} finally {
				close(writer, tocOutputFile);
			}
		}
	}

	protected MarkupToEclipseToc createMarkupToEclipseToc(String relativePath, File htmlOutputFile, String name) {
		MarkupToEclipseToc toEclipseToc = new SplittingMarkupToEclipseToc();

		toEclipseToc.setBookTitle(title == null ? name : title);
		toEclipseToc.setCopyrightNotice(copyrightNotice);
		toEclipseToc.setAnchorLevel(tocAnchorLevel);
		toEclipseToc.setHelpPrefix(calculateHelpPrefix(relativePath));

		toEclipseToc.setHtmlFile(htmlOutputFile.getName());
		return toEclipseToc;
	}

	protected String calculateHelpPrefix(String relativePath) {
		String prefix = helpPrefix == null ? "" : helpPrefix; //$NON-NLS-1$
		if (relativePath.length() > 0) {
			if (prefix.length() > 0) {
				prefix += "/"; //$NON-NLS-1$
			}
			prefix += relativePath;
		}
		return prefix.length() == 0 ? null : prefix.replace('\\', '/');
	}

	private File computeTocFile(File htmlFile, String name) {
		return new File(htmlFile.getParentFile(), xmlFilenameFormat.replace("$1", name)); //$NON-NLS-1$
	}

	protected SplittingHtmlDocumentBuilder createSplittingBuilder(HtmlDocumentBuilder builder, SplitOutlineItem item,
			File htmlOutputFile, String relativePath) {
		SplittingHtmlDocumentBuilder splittingBuilder = new SplittingHtmlDocumentBuilder();
		splittingBuilder.setRootBuilder(builder);
		splittingBuilder.setOutline(item);
		splittingBuilder.setEmbeddedTableOfContents(embeddedTableOfContents);
		splittingBuilder.setRootFile(htmlOutputFile);
		splittingBuilder.setNavigationImages(navigationImages);
		splittingBuilder
		.setNavigationImagePath(computeResourcePath(splittingBuilder.getNavigationImagePath(), relativePath));
		splittingBuilder.setFormatting(formatOutput);
		return splittingBuilder;
	}

	private SplittingOutlineParser createOutlineParser(MarkupLanguage markupLanguage,
			SplittingStrategy splittingStrategy) {
		SplittingOutlineParser outlineParser = new SplittingOutlineParser();
		outlineParser.setMarkupLanguage(markupLanguage.clone());
		outlineParser.setSplittingStrategy(splittingStrategy);
		return outlineParser;
	}

	private SplittingStrategy createSplittingStrategy() {
		return multipleOutputFiles ? new DefaultSplittingStrategy() : new NoSplittingStrategy();
	}

	protected HtmlDocumentBuilder createRootBuilder(Writer writer, String name, String relativePath) {
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer, formatOutput);
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
		configureStylesheets(builder, relativePath);
		return builder;
	}

	protected void configureStylesheets(HtmlDocumentBuilder builder, String relativePath) {
		for (String cssStylesheetUrl : stylesheetUrls) {
			builder.addCssStylesheet(
					new HtmlDocumentBuilder.Stylesheet(computeResourcePath(cssStylesheetUrl, relativePath)));
		}
	}

	protected String computeResourcePath(String resourcePath, String relativePath) {
		if (resourcePath.startsWith("/") || isAbsoluteUri(resourcePath)) { //$NON-NLS-1$
			return resourcePath;
		}
		String path = resourcePath;
		String prefix = relativePath.replaceAll("[^\\\\/]+", "..").replace('\\', '/'); //$NON-NLS-1$ //$NON-NLS-2$
		if (prefix.length() > 0) {
			if (!resourcePath.startsWith("/")) { //$NON-NLS-1$
				prefix += '/';
			}
			path = prefix + resourcePath;
		}
		return path;
	}

	private boolean isAbsoluteUri(String resourcePath) {
		try {
			return new URI(resourcePath).getScheme() != null;
		} catch (URISyntaxException e) {
			throw new BuildFailureException(format("\"{0}\" is not a valid URI", resourcePath), e); //$NON-NLS-1$
		}
	}

	private Writer createWriter(File outputFile) {
		Writer writer;
		try {
			writer = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(outputFile)),
					StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new BuildFailureException(format("Cannot write to file {0}: {1}", outputFile, e.getMessage()), e); //$NON-NLS-1$
		}
		return writer;
	}

	protected File computeHtmlFile(final String relativePath, String name) {
		File parent = outputFolder;
		if (relativePath.length() > 0) {
			parent = new File(parent, relativePath);
		}
		return new File(parent, htmlFilenameFormat.replace("$1", name)); //$NON-NLS-1$
	}

	protected void ensureSourceFolderExists() {
		ensureFolderExists("Source folder", sourceFolder, false); //$NON-NLS-1$
	}

	protected void ensureOutputFolderExists() {
		ensureFolderExists("Output folder", outputFolder, true); //$NON-NLS-1$
	}

	protected void ensureFolderExists(String name, File folder, boolean createIfMissing) {
		if (folder.exists()) {
			if (!folder.isDirectory()) {
				throw new BuildFailureException(format("{0} exists but is not a folder: {1}", name, folder)); //$NON-NLS-1$
			}
			return;
		}
		if (!createIfMissing) {
			throw new BuildFailureException(format("{0} does not exist: {1}", name, folder)); //$NON-NLS-1$
		}
		if (!folder.mkdirs()) {
			throw new BuildFailureException(format("Cannot create {0}: {1}", name, folder)); //$NON-NLS-1$
		}
	}

	protected String readFully(File inputFile) {
		try {
			return Files.readString(inputFile.toPath(), Charset.forName(sourceEncoding));
		} catch (IOException e) {
			throw new BuildFailureException(format("Cannot read source file {0}: {1}", inputFile, e.getMessage()), e); //$NON-NLS-1$
		}
	}

}
