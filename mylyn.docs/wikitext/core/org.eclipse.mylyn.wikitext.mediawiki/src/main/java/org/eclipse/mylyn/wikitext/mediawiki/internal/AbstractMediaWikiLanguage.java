/*******************************************************************************
 * Copyright (c) 2010, 2022 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.mediawiki.internal;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.mediawiki.PageMapping;
import org.eclipse.mylyn.wikitext.mediawiki.Template;
import org.eclipse.mylyn.wikitext.mediawiki.TemplateResolver;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.markup.AbstractMarkupLanguage;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;

/**
 * @author David Green
 */
public abstract class AbstractMediaWikiLanguage extends AbstractMarkupLanguage {
	private static final String CATEGORY_PREFIX = ":"; //$NON-NLS-1$

	private static final Pattern STANDARD_EXTERNAL_LINK_FORMAT = Pattern.compile(".*?/([^/]+)/(\\{\\d+\\})"); //$NON-NLS-1$

	private static final Pattern QUALIFIED_INTERNAL_LINK = Pattern.compile("([^/]+)/(.+)"); //$NON-NLS-1$

	private PageMapping pageMapping;

	private Map<String, String> imageMapping;

	protected String mapPageNameToHref(String pageName) {
		if (pageMapping != null) {
			String mapping = pageMapping.mapPageNameToHref(pageName);
			if (mapping != null) {
				return mapping;
			}
		}

		String pageId;
		int anchorIndex = pageName.indexOf("#"); //$NON-NLS-1$
		if (anchorIndex < 0) {
			pageId = pageName;
		} else {
			String encodedAnchor = MediaWikiIdGenerationStrategy.headingTextToId(pageName.substring(anchorIndex + 1));
			pageId = pageName.substring(0, anchorIndex) + "#" + encodedAnchor; //$NON-NLS-1$
		}

		if (pageId.startsWith(CATEGORY_PREFIX) && pageId.length() > CATEGORY_PREFIX.length()) { // category
			return pageId.substring(CATEGORY_PREFIX.length());
		} else if (pageId.startsWith("#")) { //$NON-NLS-1$
			// internal anchor
			return pageId;
		}
		if (internalLinkPattern.contains("index.php?")) { //$NON-NLS-1$
			pageId = URLEncoder.encode(pageId, StandardCharsets.UTF_8);
		}
		if (QUALIFIED_INTERNAL_LINK.matcher(pageId).matches()) {
			Matcher matcher = STANDARD_EXTERNAL_LINK_FORMAT.matcher(internalLinkPattern);
			if (matcher.matches()) {
				String prefix = matcher.group(1);
				if (pageId.startsWith(prefix + '/')) {
					return internalLinkPattern.substring(0, matcher.start(1)) + pageId;
				} else {
					return internalLinkPattern.substring(0, matcher.start(2)) + pageId;
				}
			}
		}
		return MessageFormat.format(super.internalLinkPattern, pageId);
	}

	public PageMapping getPageMapping() {
		return pageMapping;
	}

	public void setPageMapping(PageMapping pageMapping) {
		this.pageMapping = pageMapping;
	}

	@Override
	public void processContent(MarkupParser parser, String markupContent, boolean asDocument) {
		if (isEnableMacros()) {
			markupContent = preprocessContent(markupContent);
		}
		super.processContent(parser, markupContent, asDocument);
	}

	/**
	 * preprocess content, which involves template substitution.
	 */
	private String preprocessContent(String markupContent) {
		return new TemplateProcessor(this).processTemplates(markupContent);
	}

	public abstract List<Template> getTemplates();

	public abstract List<TemplateResolver> getTemplateProviders();

	public abstract String getTemplateExcludes();

	public Set<String> getImageNames() {
		if (imageMapping == null) {
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(new HashSet<>(imageMapping.values()));
	}

	public void setImageNames(Set<String> imageNames) {
		if (imageMapping == null) {
			imageMapping = new HashMap<>();
		} else {
			imageMapping.clear();
		}
		for (String name : imageNames) {
			imageMapping.put(name.toLowerCase(), name);
		}
	}

	public String mapImageName(String imageName) {
		String substitute = imageMapping == null ? null : imageMapping.get(imageName.toLowerCase());
		if (substitute != null) {
			imageName = substitute;
		}
		return imageName;
	}

	@Override
	public MarkupLanguage clone() {
		AbstractMediaWikiLanguage copy = (AbstractMediaWikiLanguage) super.clone();
		copy.imageMapping = imageMapping;
		copy.pageMapping = pageMapping;
		return copy;
	}
}
