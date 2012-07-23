/*******************************************************************************
 * Copyright (c) 2010, 2012 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.mediawiki.core;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.markup.AbstractMarkupLanguage;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.mediawiki.core.Template;
import org.eclipse.mylyn.wikitext.mediawiki.core.TemplateResolver;

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
		String pageId = pageName.replace(' ', '_');
		// FIXME: other character encodings occur here, not just ' '

		if (pageId.startsWith(CATEGORY_PREFIX) && pageId.length() > CATEGORY_PREFIX.length()) { // category
			return pageId.substring(CATEGORY_PREFIX.length());
		} else if (pageId.startsWith("#")) { //$NON-NLS-1$
			// internal anchor
			return pageId;
		}
		if (internalLinkPattern.contains("index.php?")) { //$NON-NLS-1$
			try {
				pageId = URLEncoder.encode(pageId, "UTF-8"); //$NON-NLS-1$
			} catch (UnsupportedEncodingException e) {
				throw new IllegalStateException(MessageFormat.format(
						Messages.getString("AbstractMediaWikiLanguage_cannotComputeUrl"), pageId), e); //$NON-NLS-1$
			}
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
		return Collections.unmodifiableSet(new HashSet<String>(imageMapping.values()));
	}

	public void setImageNames(Set<String> imageNames) {
		if (imageMapping == null) {
			imageMapping = new HashMap<String, String>();
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
		copy.imageMapping = this.imageMapping;
		copy.pageMapping = this.pageMapping;
		return copy;
	}
}
