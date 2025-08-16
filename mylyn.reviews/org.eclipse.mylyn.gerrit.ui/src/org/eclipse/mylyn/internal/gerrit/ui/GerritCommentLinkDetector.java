/*******************************************************************************
 * Copyright (c) 2012, 2014 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylyn.commons.workbench.browser.UrlHyperlink;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.CommentLink;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.GerritConfigX;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Steffen Pingel
 */
public class GerritCommentLinkDetector {

	private static final Pattern PATTERN_HYPERLINK = Pattern.compile("href=\"([^\"]*)\""); //$NON-NLS-1$

	private List<IHyperlink> links;

	private final TaskRepository repository;

	private final List<CommentLink> commentLinks;

	public GerritCommentLinkDetector(TaskRepository repository, GerritConfigX config) {
		this(repository, config.getCommentLinks2());
	}

	public GerritCommentLinkDetector(TaskRepository repository, List<CommentLink> commentLinks) {
		this.repository = repository;
		this.commentLinks = commentLinks;
	}

	public List<IHyperlink> findHyperlinks(String text, int index, int textOffset) {
		if (commentLinks == null) {
			return null;
		}

		links = null;
		for (CommentLink commentLink : commentLinks) {
			Matcher replaceMatcher = PATTERN_HYPERLINK.matcher(commentLink.getReplace());
			if (replaceMatcher.find()) {
				String href = replaceMatcher.group(1);
				Pattern findPattern = Pattern.compile(commentLink.getFind());
				findHyperlinks(findPattern.matcher(text), index, textOffset, href);
			}
		}
		return links;
	}

	private void findHyperlinks(Matcher matcher, int index, int textOffset, String href) {
		while (matcher.find()) {
			if (index != -1 && (index < matcher.start() || index > matcher.end())) {
				continue;
			}
			if (links == null) {
				links = new ArrayList<>();
			}
			String url = href;
			for (int i = 1; i <= matcher.groupCount(); i++) {
				url = url.replaceAll(Pattern.quote("$" + i), matcher.group(i)); //$NON-NLS-1$
			}
			// prepend repository url to relative links
			if (!url.startsWith("http")) { //$NON-NLS-1$
				if (!repository.getUrl().endsWith("/")) { //$NON-NLS-1$
					url = "/" + url; //$NON-NLS-1$
				}
				url = repository.getUrl() + url;
			}

			int start = matcher.start();
			Region region = new Region(textOffset + start, matcher.end() - start);
			links.add(new UrlHyperlink(region, url));
		}
	}

}
