/*******************************************************************************
 * Copyright (c) 2010 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.mediawiki.core.tasks;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.eclipse.mylyn.internal.wikitext.core.parser.builder.DefaultSplittingStrategy;
import org.eclipse.mylyn.internal.wikitext.core.parser.builder.NoSplittingStrategy;
import org.eclipse.mylyn.internal.wikitext.core.parser.builder.SplitOutlineItem;
import org.eclipse.mylyn.internal.wikitext.core.parser.builder.SplittingHtmlDocumentBuilder;
import org.eclipse.mylyn.internal.wikitext.core.parser.builder.SplittingMarkupToEclipseToc;
import org.eclipse.mylyn.internal.wikitext.core.parser.builder.SplittingOutlineParser;
import org.eclipse.mylyn.internal.wikitext.core.parser.builder.SplittingStrategy;
import org.eclipse.mylyn.internal.wikitext.core.validation.StandaloneMarkupValidator;
import org.eclipse.mylyn.internal.wikitext.mediawiki.core.PageMapping;
import org.eclipse.mylyn.internal.wikitext.mediawiki.core.WikiTemplateResolver;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineItem;
import org.eclipse.mylyn.wikitext.core.util.anttask.MarkupTask;
import org.eclipse.mylyn.wikitext.core.validation.ValidationProblem;
import org.eclipse.mylyn.wikitext.core.validation.ValidationProblem.Severity;
import org.eclipse.mylyn.wikitext.mediawiki.core.MediaWikiLanguage;

/**
 * An Ant task for generating Eclipse help content from one or more MediaWiki pages. Example usage:
 * 
 * <pre>
 * <code>
 * 	&lt;mediawiki-to-eclipse-help
 *     		wikiBaseUrl="${mylyn.help.doc.url.base}"
 * 			validate="true"
 * 			failonvalidationerror="true"
 * 			prependImagePrefix="${imageFolder}"
 * 			formatoutput="true"
 * 			defaultAbsoluteLinkTarget="mylyn_external"
 *     		dest="${basedir}"
 *     		title="Mylyn"
 *     		generateUnifiedToc="false"&gt;
 *     		&lt;path name="Mylyn/User_Guide" title="Mylyn User Guide" generateToc="true"/&gt;
 *     		&lt;path name="Mylyn/FAQ" title="Mylyn FAQ" generateToc="true"/&gt;
 * 			&lt;stylesheet url="book.css"/&gt;
 *     		&lt;pageAppendum&gt;
 * 
 * = Updating This Document =
 * 
 * This document is maintained in a collaborative wiki.  If you wish to update or modify this document please visit 
 * {url}&lt;/pageAppendum&gt;
 *     	&lt;/mediawiki-to-eclipse-help&gt;
 * </code>
 * </pre>
 * 
 * @author David Green
 */
public class WikiToDocTask extends MarkupTask {
	protected String htmlFilenameFormat = "$1.html"; //$NON-NLS-1$

	private String wikiBaseUrl;

	private List<Path> paths = new ArrayList<Path>();

	private File dest;

	private PageAppendum pageAppendum;

	private final List<Stylesheet> stylesheets = new ArrayList<Stylesheet>();

	protected String linkRel;

	protected boolean multipleOutputFiles = true;

	protected boolean formatOutput = false;

	protected boolean navigationImages = true;

	protected String prependImagePrefix = "images"; //$NON-NLS-1$

	private final boolean useInlineCssStyles = true;

	private final boolean suppressBuiltInCssStyles = false;

	private String defaultAbsoluteLinkTarget;

	private final boolean xhtmlStrict = false;

	private final boolean emitDoctype = true;

	private final String htmlDoctype = null;

	private String helpPrefix;

	private boolean fetchImages = true;

	private File tocFile;

	private String title;

	private boolean generateUnifiedToc = true;

	private String templateExcludes;

	private boolean titleParameter;

	public WikiToDocTask() {
	}

	@Override
	public void execute() throws ConfigurationException {
		if (dest == null) {
			throw new ConfigurationException(Messages.getString("WikiToDocTask_specify_dest")); //$NON-NLS-1$
		}
		if (wikiBaseUrl == null) {
			throw new ConfigurationException(Messages.getString("WikiToDocTask_specify_wikiBaseUrl")); //$NON-NLS-1$
		}
		if (paths.isEmpty()) {
			throw new ConfigurationException(Messages.getString("WikiToDocTask_specify_paths")); //$NON-NLS-1$
		}
		if (getInternalLinkPattern() == null) {
			setInternalLinkPattern(computeDefaultInternalLinkPattern());
		}

		Set<String> pathNames = new HashSet<String>();
		for (Path path : paths) {
			if (path.name == null) {
				throw new ConfigurationException(Messages.getString("WikiToDocTask_path_must_have_name")); //$NON-NLS-1$
			}
			if (path.name != null) {
				if (!pathNames.add(path.name)) {
					throw new ConfigurationException(MessageFormat.format(
							Messages.getString("WikiToDocTask_path_name_must_be_unique"), path.name)); //$NON-NLS-1$
				}
			}
			if (!path.includeInUnifiedToc && path.getTocParentName() != null) {
				throw new ConfigurationException(MessageFormat.format(
						Messages.getString("WikiToDocTask_tocParentName_not_in_unified_toc"), path.name)); //$NON-NLS-1$
			}
		}
		if (generateUnifiedToc) {
			for (Path path : paths) {
				if (path.getTocParentName() != null) {
					if (!pathNames.contains(path.getTocParentName())) {
						throw new ConfigurationException(MessageFormat.format(
								Messages.getString("WikiToDocTask_unknown_tocParentName"), path.getTocParentName())); //$NON-NLS-1$
					}
				}
			}
		}

		MediaWikiLanguage markupLanguage = (MediaWikiLanguage) createMarkupLanguage();
		WikiTemplateResolver templateResolver = new WikiTemplateResolver();
		templateResolver.setWikiBaseUrl(wikiBaseUrl);
		markupLanguage.getTemplateProviders().add(templateResolver);
		markupLanguage.setTemplateExcludes(templateExcludes);

		for (Stylesheet stylesheet : stylesheets) {
			if (stylesheet.url == null && stylesheet.file == null) {
				throw new BuildException(Messages.getString("WikiToDocTask_stylesheet_file_or_url")); //$NON-NLS-1$
			}
			if (stylesheet.url != null && stylesheet.file != null) {
				throw new BuildException(Messages.getString("WikiToDocTask_stylesheet_not_both")); //$NON-NLS-1$
			}
			if (stylesheet.file != null) {
				if (!stylesheet.file.exists()) {
					throw new BuildException(MessageFormat.format(
							Messages.getString("WikiToDocTask_stylesheet_file_not_exist"), //$NON-NLS-1$
							stylesheet.file));
				}
				if (!stylesheet.file.isFile()) {
					throw new BuildException(MessageFormat.format(
							Messages.getString("WikiToDocTask_stylesheet_file_not_file"), //$NON-NLS-1$
							stylesheet.file));
				}
				if (!stylesheet.file.canRead()) {
					throw new BuildException(MessageFormat.format(
							Messages.getString("WikiToDocTask_stylesheet_file_cannot_read"), stylesheet.file)); //$NON-NLS-1$
				}
			}
		}
		if (!dest.exists()) {
			if (!dest.mkdirs()) {
				throw new BuildException(MessageFormat.format("Cannot create dest folder: {0}", dest.getAbsolutePath())); //$NON-NLS-1$
			}
		}

		if (tocFile == null) {
			tocFile = new File(dest, "toc.xml"); //$NON-NLS-1$
		}

		Map<String, String> pathNameToContent = new HashMap<String, String>();
		Map<String, SplitOutlineItem> pathNameToOutline = new HashMap<String, SplitOutlineItem>();
		for (Path path : paths) {
			getProject().log(
					MessageFormat.format(Messages.getString("WikiToDocTask_fetching_content_for_page"), path.name), Project.MSG_VERBOSE); //$NON-NLS-1$
			URL pathUrl = computeRawUrl(path.name);
			Reader input;
			try {
				input = new InputStreamReader(new BufferedInputStream(pathUrl.openStream()), "UTF-8"); //$NON-NLS-1$
				try {
					String content = readFully(input);
					content = preprocessMarkup(path, content);
					pathNameToContent.put(path.name, content);
					File dest = computeDestDir(path);
					String fileName = computeHtmlFilename(path.name);
					final File targetFile = new File(dest, fileName);
					SplitOutlineItem outline = computeOutline(path, markupLanguage, targetFile, content);
					outline.setResourcePath(targetFile.getAbsolutePath());
					pathNameToOutline.put(path.name, outline);
				} finally {
					input.close();
				}
			} catch (final IOException e) {
				final String message = MessageFormat.format("Cannot read from {0}: {1}", pathUrl, e.getMessage()); //$NON-NLS-1$
				throw new BuildException(message, e);
			}

		}
		for (Path path : paths) {
			getProject().log(
					MessageFormat.format(Messages.getString("WikiToDocTask_processing_page"), path.name), Project.MSG_DEBUG); //$NON-NLS-1$

			String markupContent = pathNameToContent.get(path.name);
			if (isValidate()) {
				performValidation(markupLanguage, path, markupContent);
			}

			Set<String> imageFilenames = null;
			if (!fetchImages) {
				getProject().log(Messages.getString("WikiToDocTask_skipping_images"), Project.MSG_WARN); //$NON-NLS-1$
			} else {
				imageFilenames = fetchImages(markupLanguage, path);
			}

			markupToDoc(markupLanguage, path, markupContent, pathNameToOutline, imageFilenames);

			if (path.isGenerateToc()) {
				createToc(path, pathNameToOutline.get(path.name));
			}

		}
		if (generateUnifiedToc) {
			createToc(paths, pathNameToOutline);
		}
	}

	protected void performValidation(MarkupLanguage markupLanguage, Path path, String markupContent) {
		getProject().log(MessageFormat.format("Validating {0}", path.name), Project.MSG_VERBOSE); //$NON-NLS-1$

		StandaloneMarkupValidator markupValidator = StandaloneMarkupValidator.getValidator(markupLanguage.getName());

		List<ValidationProblem> problems = markupValidator.validate(markupContent);

		int errorCount = 0;
		int warningCount = 0;
		for (ValidationProblem problem : problems) {
			int messageLevel = Project.MSG_ERR;
			if (problem.getSeverity() == Severity.ERROR) {
				++errorCount;
			} else if (problem.getSeverity() == Severity.WARNING) {
				++warningCount;
				messageLevel = Project.MSG_WARN;
			}
			log(String.format("%s:%s %s", path.name, problem.getOffset(), problem.getMessage()), messageLevel); //$NON-NLS-1$
		}

		if ((errorCount > 0 && isFailOnValidationError()) || (warningCount > 0 && isFailOnValidationWarning())) {
			throw new BuildException(MessageFormat.format(
					"Validation failed with {0} errors and {1} warnings: {0}", errorCount, //$NON-NLS-1$
					warningCount, path.name));
		}
	}

	private void createToc(final Path path, OutlineItem rootItem) {
		SplittingMarkupToEclipseToc markupToEclipseToc = new SplittingMarkupToEclipseToc() {
			@Override
			protected String computeFile(OutlineItem item) {
				if (item instanceof SplitOutlineItem) {
					return computeTocRelativeFile(item, path);
				}
				return super.computeFile(item);
			}
		};
		markupToEclipseToc.setBookTitle(path.getTitle());
		markupToEclipseToc.setHtmlFile(computeTocRelativeFile(rootItem, path));
		String tocContents = markupToEclipseToc.createToc(rootItem);

		File tocFile = new File(dest, path.name.replaceAll("[^a-zA-Z0-9]", "-") + "-toc.xml"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

		try {
			Writer writer = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(tocFile)), "UTF-8"); //$NON-NLS-1$
			try {
				writer.write(tocContents);
			} finally {
				writer.close();
			}
		} catch (IOException e) {
			String message = MessageFormat.format("Cannot write {0}: {1}", tocFile, e.getMessage()); //$NON-NLS-1$
			throw new BuildException(message, e);
		}
	}

	private String computeTocRelativeFile(Map<OutlineItem, Path> outlineItemToPath, final OutlineItem item) {
		Path path = outlineItemToPath.get(item);

		OutlineItem pathItem = item;
		while (path == null && pathItem.getParent() != null) {
			pathItem = pathItem.getParent();
			path = outlineItemToPath.get(pathItem);
		}

		return computeTocRelativeFile(item, path);
	}

	private String computeTocRelativeFile(OutlineItem item, Path path) {
		String name = ((SplitOutlineItem) item).getSplitTarget();
		File pathDestDir = computeDestDir(path);
		File tocParentFile = tocFile.getParentFile();
		String prefix = computePrefixPath(pathDestDir, tocParentFile);
		String relativePath = prefix + '/' + name;
		relativePath = relativePath.replace('\\', '/');
		if (helpPrefix != null) {
			String helpPath = helpPrefix;
			helpPath = helpPath.replace('\\', '/');
			if (!helpPath.endsWith("/")) { //$NON-NLS-1$
				helpPath += "/"; //$NON-NLS-1$
			}
			relativePath = helpPath + relativePath;
		}
		relativePath = relativePath.replaceAll("/{2,}", "/"); //$NON-NLS-1$//$NON-NLS-2$
		return relativePath;
	}

	private String computePrefixPath(File destDir, File tocParentFile) {
		String prefix = destDir.getAbsolutePath().substring(tocParentFile.getAbsolutePath().length());
		prefix = prefix.replace('\\', '/');
		if (prefix.startsWith("/")) { //$NON-NLS-1$
			prefix = prefix.substring(1);
		}
		if (prefix.endsWith("/")) { //$NON-NLS-1$
			prefix = prefix.substring(0, prefix.length() - 1);
		}
		return prefix;
	}

	private void createToc(List<Path> paths, final Map<String, SplitOutlineItem> pathNameToOutline) {
		getProject().log(
				MessageFormat.format(Messages.getString("WikiToDocTask_writing_toc"), tocFile), Project.MSG_VERBOSE); //$NON-NLS-1$
		final OutlineItem rootItem = new OutlineItem(null, 0,
				"<root>", 0, -1, title == null ? computeTitle(paths.get(0)) : title); //$NON-NLS-1$
		final Map<OutlineItem, Path> outlineItemToPath = new HashMap<OutlineItem, Path>();
		final Map<String, OutlineItem> nameToItem = new HashMap<String, OutlineItem>();

		// create root-level items
		for (Path path : paths) {
			if (path.includeInUnifiedToc) {
				SplitOutlineItem pathItem = pathNameToOutline.get(path.name);
				outlineItemToPath.put(pathItem, path);

				nameToItem.put(path.name, pathItem);

				if (path.getTocParentName() == null) {
					rootItem.getChildren().add(pathItem);
				}
			}
		}
		for (Path path : paths) {
			if (path.includeInUnifiedToc) {
				if (path.getTocParentName() != null) {
					SplitOutlineItem pathItem = pathNameToOutline.get(path.name);

					if (nameToItem.containsKey(path.getTocParentName())) {
						nameToItem.get(path.getTocParentName()).getChildren().add(pathItem);
					} else {
						throw new ConfigurationException(MessageFormat.format(
								Messages.getString("WikiToDocTask_unknown_tocParentName"), path.getTocParentName())); //$NON-NLS-1$
					}
				}
			}
		}
		SplittingMarkupToEclipseToc markupToEclipseToc = new SplittingMarkupToEclipseToc() {
			@Override
			protected String computeFile(OutlineItem item) {
				if (item instanceof SplitOutlineItem) {
					return computeTocRelativeFile(outlineItemToPath, item);
				}
				return super.computeFile(item);
			}
		};
		markupToEclipseToc.setBookTitle(rootItem.getLabel());
		markupToEclipseToc.setHtmlFile(computeTocRelativeFile(outlineItemToPath, rootItem.getChildren().get(0)));
		String tocContents = markupToEclipseToc.createToc(rootItem);

		try {
			Writer writer = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(tocFile)), "UTF-8"); //$NON-NLS-1$
			try {
				writer.write(tocContents);
			} finally {
				writer.close();
			}
		} catch (IOException e) {
			String message = MessageFormat.format("Cannot write {0}: {1}", tocFile, e.getMessage()); //$NON-NLS-1$
			throw new BuildException(message, e);
		}
	}

	private String computeDefaultInternalLinkPattern() {
		String internalLinkPattern = wikiBaseUrl;
		if (!internalLinkPattern.endsWith("/")) { //$NON-NLS-1$
			internalLinkPattern += "/"; //$NON-NLS-1$
		}
		if (titleParameter) {
			// parameter encoding is handled in AbstractMediaWikiLanguage
			internalLinkPattern += "index.php?title={0}"; //$NON-NLS-1$ 
		} else {
			internalLinkPattern += "{0}"; //$NON-NLS-1$
		}
		return internalLinkPattern;
	}

	private Set<String> fetchImages(MarkupLanguage markupLanguage, Path path) {
		File dest = computeDestDir(path);
		if (prependImagePrefix != null) {
			dest = new File(dest, prependImagePrefix);
			if (!dest.exists()) {
				if (!dest.mkdirs()) {
					throw new BuildException(MessageFormat.format(
							"Cannot create images folder: {0}", dest.getAbsolutePath())); //$NON-NLS-1$
				}
			}
		}
		getProject().log(
				MessageFormat.format(Messages.getString("WikiToDocTask_fetching_images_for_page"), path.name), Project.MSG_VERBOSE); //$NON-NLS-1$
		MediaWikiApiImageFetchingStrategy imageFetchingStrategy = new MediaWikiApiImageFetchingStrategy();
		imageFetchingStrategy.setTask(this);
		imageFetchingStrategy.setDest(dest);
		imageFetchingStrategy.setPageName(path.name);
		try {
			imageFetchingStrategy.setUrl(new URL(wikiBaseUrl));
		} catch (MalformedURLException e) {
			throw new BuildException(e);
		}
		return imageFetchingStrategy.fetchImages();
	}

	private String preprocessMarkup(Path path, String content) {
		if (pageAppendum != null) {
			String pageAppendum = this.pageAppendum.text;
			String qualifiedUrl = computeQualifiedWebPageUrl(path.name);
			String appendum = pageAppendum.replace("{url}", qualifiedUrl); //$NON-NLS-1$
			appendum = appendum.replace("{name}", path.name); //$NON-NLS-1$
			appendum = appendum.replace("{title}", computeTitle(path)); //$NON-NLS-1$
			content += appendum;
			getProject().log(
					MessageFormat.format(
							Messages.getString("WikiToDocTask_appending_markup_to_page"), path.name, appendum), //$NON-NLS-1$
					Project.MSG_VERBOSE);
		}
		return content;
	}

	private void markupToDoc(MarkupLanguage markupLanguage, Path path, String markupContent,
			Map<String, SplitOutlineItem> pathNameToOutline, Set<String> imageFilenames) throws BuildException {
		File pathDir = computeDestDir(path);
		if (!pathDir.exists()) {
			if (!pathDir.mkdirs()) {
				throw new BuildException(MessageFormat.format(
						Messages.getString("WikiToDocTask_cannot_create_dest_folder"), //$NON-NLS-1$
						pathDir.getAbsolutePath()));
			}
		}
		String fileName = computeHtmlFilename(path.name);
		File htmlOutputFile = new File(pathDir, fileName);
		Writer writer;
		try {
			writer = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(htmlOutputFile)), "utf-8"); //$NON-NLS-1$
		} catch (Exception e) {
			throw new BuildException(MessageFormat.format(
					Messages.getString("WikiToDocTask_cannot_create_output_file"), htmlOutputFile, //$NON-NLS-1$
					e.getMessage()), e);
		}

		try {
			HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer, formatOutput);
			for (Stylesheet stylesheet : stylesheets) {
				HtmlDocumentBuilder.Stylesheet builderStylesheet;

				if (stylesheet.url != null) {
					String relativePath = ""; //$NON-NLS-1$
					File currentDest = pathDir;
					while (!currentDest.equals(dest)) {
						currentDest = currentDest.getParentFile();
						relativePath += "../"; //$NON-NLS-1$
					}
					builderStylesheet = new HtmlDocumentBuilder.Stylesheet(relativePath + stylesheet.url);
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

			builder.setTitle(computeTitle(path));
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

			MarkupLanguage markupLanguageClone = markupLanguage.clone();
			if (markupLanguageClone instanceof MediaWikiLanguage) {
				MediaWikiLanguage mediaWikiLanguage = (MediaWikiLanguage) markupLanguageClone;
				mediaWikiLanguage.setPageMapping(new PathPageMapping(path, paths, pathNameToOutline));
				if (imageFilenames != null) {
					mediaWikiLanguage.setImageNames(imageFilenames);
				}
			}

			SplitOutlineItem item = pathNameToOutline.get(path.name);
			SplittingHtmlDocumentBuilder splittingBuilder = new SplittingHtmlDocumentBuilder();
			splittingBuilder.setRootBuilder(builder);
			splittingBuilder.setOutline(item);
			splittingBuilder.setRootFile(htmlOutputFile);
			splittingBuilder.setNavigationImages(navigationImages);
			splittingBuilder.setFormatting(formatOutput);
			splittingBuilder.setNavigationImagePath(computeNavigationImagePath(pathDir));

			MarkupParser parser = new MarkupParser();
			parser.setMarkupLanguage(markupLanguageClone);
			parser.setBuilder(splittingBuilder);

			parser.parse(markupContent);

		} finally {
			try {
				writer.close();
			} catch (Exception e) {
				throw new BuildException(MessageFormat.format(
						Messages.getString("WikiToDocTask_cannot_write_output_file"), htmlOutputFile, //$NON-NLS-1$
						e.getMessage()), e);
			}
		}
	}

	protected String computeTitle(Path path) {
		return path.title == null ? path.name : path.title;
	}

	private String computeNavigationImagePath(File pathDir) {
		String relativePath = ""; //$NON-NLS-1$
		File currentDest = pathDir;
		while (!currentDest.equals(dest)) {
			currentDest = currentDest.getParentFile();
			relativePath += "../"; //$NON-NLS-1$
		}
		return relativePath + "images"; //$NON-NLS-1$
	}

	protected SplitOutlineItem computeOutline(Path path, MarkupLanguage markupLanguage, File defaultFile,
			String markupContent) {
		SplittingStrategy splittingStrategy = multipleOutputFiles
				? new DefaultSplittingStrategy()
				: new NoSplittingStrategy();
		SplittingOutlineParser outlineParser = new SplittingOutlineParser();
		outlineParser.setMarkupLanguage(markupLanguage);
		outlineParser.setSplittingStrategy(splittingStrategy);
		SplitOutlineItem item = outlineParser.parse(markupContent);
		item.setSplitTarget(defaultFile.getName());
		item.setLabel(computeTitle(path));
		return item;
	}

	private File computeDestDir(Path path) {
		String name = path.name;
		name.replace(' ', '_');
		File dest = new File(this.dest, name);
		return dest;
	}

	private String readFully(Reader input) throws IOException {
		StringWriter content = new StringWriter(1024 * 8);
		int i;
		while ((i = input.read()) != -1) {
			content.write(i);
		}
		return content.toString();
	}

	private URL computeRawUrl(String path) {
		try {
			String qualifiedUrl = wikiBaseUrl;
			if (!qualifiedUrl.endsWith("/")) { //$NON-NLS-1$
				qualifiedUrl += "/"; //$NON-NLS-1$
			}
			// ignore titleParameter here, we always reference index.php when getting the raw page content
			qualifiedUrl += "index.php?title=" + URLEncoder.encode(path, "UTF-8") + "&action=raw"; //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
			return new URL(qualifiedUrl);
		} catch (IOException e) {
			throw new BuildException(MessageFormat.format(
					Messages.getString("WikiToDocTask_cannot_compute_raw_url"), path, e.getMessage()), //$NON-NLS-1$
					e);
		}
	}

	private String computeQualifiedWebPageUrl(String path) {
		String qualifiedUrl = wikiBaseUrl;
		if (!qualifiedUrl.endsWith("/")) { //$NON-NLS-1$
			qualifiedUrl += "/"; //$NON-NLS-1$
		}
		if (titleParameter) {
			try {
				qualifiedUrl += "index.php?title=" + URLEncoder.encode(path, "UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$
			} catch (IOException e) {
				throw new BuildException(MessageFormat.format(
						Messages.getString("WikiToDocTask_cannot_compute_url"), path, e.getMessage()), //$NON-NLS-1$
						e);
			}
		} else {
			qualifiedUrl += path;
		}
		return qualifiedUrl;
	}

	protected String computeHtmlFilename(String name) {
		if (name.lastIndexOf('/') != -1) {
			name = name.substring(name.lastIndexOf('/') + 1);
		}
		return htmlFilenameFormat.replace("$1", name.replaceAll("\\s|_", "-")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public static class Path {
		private String title;

		private String name;

		private boolean generateToc = false;

		private boolean includeInUnifiedToc = true;

		private String tocParentName;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public boolean isGenerateToc() {
			return generateToc;
		}

		public void setGenerateToc(boolean generateToc) {
			this.generateToc = generateToc;
		}

		public boolean isIncludeInUnifiedToc() {
			return includeInUnifiedToc;
		}

		public void setIncludeInUnifiedToc(boolean includeInUnifiedToc) {
			this.includeInUnifiedToc = includeInUnifiedToc;
		}

		public String getTocParentName() {
			return tocParentName;
		}

		public void setTocParentName(String tocParentName) {
			this.tocParentName = tocParentName;
		}

		@Override
		public String toString() {
			String s = name;
			if (tocParentName != null) {
				s = tocParentName + '/' + s;
			}
			return s;
		}
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

		private final Map<String, String> attributes = new HashMap<String, String>();

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

	private static final Pattern PAGE_NAME_PATTERN = Pattern.compile("([^#]*)(?:#(.*))?"); //$NON-NLS-1$

	public static class PageAppendum {
		String text;

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public void addText(String text) {
			if (this.text == null) {
				this.text = text;
			} else {
				this.text += text;
			}
		}
	}

	private class PathPageMapping implements PageMapping {
		private final Path currentPath;

		private final Map<String, Path> nameToPath = new HashMap<String, Path>();

		private final Map<String, SplitOutlineItem> pathNameToOutline;

		private PathPageMapping(Path currentPath, List<Path> paths, Map<String, SplitOutlineItem> pathNameToOutline) {
			this.currentPath = currentPath;
			this.pathNameToOutline = pathNameToOutline;
			for (Path path : paths) {
				nameToPath.put(path.name.replace(' ', '_'), path);
			}
		}

		public String mapPageNameToHref(String pageName) {
			Matcher matcher = PAGE_NAME_PATTERN.matcher(pageName);
			if (matcher.matches()) {
				String name = matcher.group(1);
				String hashId = matcher.group(2);

				if (currentPath.name.equals(name)) {
					return hashId == null ? "#" : hashId; //$NON-NLS-1$
				}
				name = name.replace(' ', '_');
				Path path = nameToPath.get(name);
				if (path != null) {
					File destDir = computeDestDir(path);
					File currentDest = computeDestDir(currentPath);
					String relativePath = ""; //$NON-NLS-1$
					while (!currentDest.equals(dest)) {
						currentDest = currentDest.getParentFile();
						relativePath += "../"; //$NON-NLS-1$
					}
					String relativeDir = destDir.getAbsolutePath().substring(dest.getAbsolutePath().length());
					//URL path separator is always forward slash
					if (File.separatorChar == '\\') {
						relativeDir = relativeDir.replace('\\', '/');
					}
					if (relativeDir.startsWith("/")) { //$NON-NLS-1$
						relativeDir = relativeDir.substring(1);
					}
					relativePath += relativeDir;
					if (!relativePath.endsWith("/")) { //$NON-NLS-1$
						relativePath += "/"; //$NON-NLS-1$
					}
					String fileName = computeHtmlFilename(name);

					// FIXME handle hashId mapping to split files
					if (hashId != null) {
						SplitOutlineItem outline = pathNameToOutline.get(name);
						SplitOutlineItem item = outline.getOutlineItemById(hashId);

						if (item == null) {
							getProject().log(
									MessageFormat.format(
											Messages.getString("WikiToDocTask_missing_id_in_page_reference"), hashId, //$NON-NLS-1$
											name, currentPath.name), Project.MSG_WARN);
						} else {
							fileName = item.getSplitTarget();
						}
						relativePath += fileName;
						relativePath += '#' + hashId;
					} else {
						relativePath += fileName;
					}

					return relativePath;
				}
			}
			return null;
		}
	}

	public String getWikiBaseUrl() {
		return wikiBaseUrl;
	}

	public void setWikiBaseUrl(String wikiBaseUrl) {
		this.wikiBaseUrl = wikiBaseUrl;
	}

	public List<Path> getPaths() {
		return paths;
	}

	public void setPaths(List<Path> paths) {
		this.paths = paths;
	}

	public File getDest() {
		return dest;
	}

	public void setDest(File dest) {
		this.dest = dest;
	}

	public String getLinkRel() {
		return linkRel;
	}

	public void setLinkRel(String linkRel) {
		this.linkRel = linkRel;
	}

	public boolean isMultipleOutputFiles() {
		return multipleOutputFiles;
	}

	public void setMultipleOutputFiles(boolean multipleOutputFiles) {
		this.multipleOutputFiles = multipleOutputFiles;
	}

	public boolean isFormatOutput() {
		return formatOutput;
	}

	public void setFormatOutput(boolean formatOutput) {
		this.formatOutput = formatOutput;
	}

	public boolean isNavigationImages() {
		return navigationImages;
	}

	public void setNavigationImages(boolean navigationImages) {
		this.navigationImages = navigationImages;
	}

	public String getPrependImagePrefix() {
		return prependImagePrefix;
	}

	public void setPrependImagePrefix(String prependImagePrefix) {
		this.prependImagePrefix = prependImagePrefix;
	}

	public String getDefaultAbsoluteLinkTarget() {
		return defaultAbsoluteLinkTarget;
	}

	public void setDefaultAbsoluteLinkTarget(String defaultAbsoluteLinkTarget) {
		this.defaultAbsoluteLinkTarget = defaultAbsoluteLinkTarget;
	}

	public List<Stylesheet> getStylesheets() {
		return stylesheets;
	}

	public boolean isUseInlineCssStyles() {
		return useInlineCssStyles;
	}

	public boolean isSuppressBuiltInCssStyles() {
		return suppressBuiltInCssStyles;
	}

	public boolean isXhtmlStrict() {
		return xhtmlStrict;
	}

	public boolean isEmitDoctype() {
		return emitDoctype;
	}

	public String getHtmlDoctype() {
		return htmlDoctype;
	}

	public String getHelpPrefix() {
		return helpPrefix;
	}

	public void setHelpPrefix(String helpPrefix) {
		this.helpPrefix = helpPrefix;
	}

	public void addStylesheet(Stylesheet stylesheet) {
		if (stylesheet == null) {
			throw new IllegalArgumentException();
		}
		stylesheets.add(stylesheet);
	}

	public void addPath(Path path) {
		if (path == null) {
			throw new IllegalArgumentException();
		}
		paths.add(path);
	}

	@Override
	protected MarkupLanguage createMarkupLanguage() throws BuildException {
		if (getMarkupLanguage() == null) {
			MarkupLanguage markupLanguage = new MediaWikiLanguage();
			if (getInternalLinkPattern() != null) {
				markupLanguage.setInternalLinkPattern(getInternalLinkPattern());
			}
			if (getMarkupLanguageConfiguration() != null) {
				markupLanguage.configure(getMarkupLanguageConfiguration());
			}
			return markupLanguage;
		}
		return super.createMarkupLanguage();
	}

	public String getHtmlFilenameFormat() {
		return htmlFilenameFormat;
	}

	public void setHtmlFilenameFormat(String htmlFilenameFormat) {
		this.htmlFilenameFormat = htmlFilenameFormat;
	}

	public boolean isFetchImages() {
		return fetchImages;
	}

	public void setFetchImages(boolean fetchImages) {
		this.fetchImages = fetchImages;
	}

	public void setPageAppendum(PageAppendum pageAppendum) {
		this.pageAppendum = pageAppendum;
	}

	public PageAppendum getPageAppendum() {
		return pageAppendum;
	}

	public void addPageAppendum(PageAppendum pageAppendum) {
		if (pageAppendum == null) {
			throw new IllegalArgumentException();
		}
		if (this.pageAppendum != null) {
			throw new BuildException(Messages.getString("WikiToDocTask_only_one_page_appendum")); //$NON-NLS-1$
		}
		this.pageAppendum = pageAppendum;
	}

	public File getTocFile() {
		return tocFile;
	}

	public void setTocFile(File tocFile) {
		this.tocFile = tocFile;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isGenerateUnifiedToc() {
		return generateUnifiedToc;
	}

	public void setGenerateUnifiedToc(boolean generateUnifiedToc) {
		this.generateUnifiedToc = generateUnifiedToc;
	}

	public String getTemplateExcludes() {
		return templateExcludes;
	}

	public void setTemplateExcludes(String templateExcludes) {
		this.templateExcludes = templateExcludes;
	}

	/**
	 * indicates if the title should be provided as an HTTP parameter, for example <code>index.php?title=Main</code>
	 * 
	 * @return true if index.php and HTTP parameters should be used in the URL, otherwise false. Defaults to false.
	 * @since 1.8
	 */
	public boolean isTitleParameter() {
		return titleParameter;
	}

	/**
	 * indicates if the title should be provided as an HTTP parameter, for example <code>index.php?title=Main</code>
	 * 
	 * @param titleParameter
	 *            true if index.php and HTTP parameters should be used in the URL, otherwise false.
	 * @since 1.8
	 */
	public void setTitleParameter(boolean titleParameter) {
		this.titleParameter = titleParameter;
	}

}
