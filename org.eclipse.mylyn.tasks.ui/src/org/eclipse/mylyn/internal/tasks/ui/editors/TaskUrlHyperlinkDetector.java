/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others. 
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     David Green - fix for bug 266693
 *     Abner Ballardo - fix for bug 288427
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylyn.commons.workbench.browser.UrlHyperlink;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractTaskHyperlinkDetector;

/**
 * Detects URLs based on a regular expression.
 * 
 * @author David Green
 */
public class TaskUrlHyperlinkDetector extends AbstractTaskHyperlinkDetector {

	// based on RFC 3986
	// even though it's valid, the platform hyperlink detector doesn't detect hyperlinks that end with '.', ',' or ')'
	// so we do the same here
	private static final Pattern URL_PATTERN = Pattern.compile("([a-zA-Z][a-zA-Z+.-]{0,10}://[a-zA-Z0-9%._~!$&?#'()*+,;:@/=-]*[a-zA-Z0-9%_~!$&?#'(*+;:@/=-])"); //$NON-NLS-1$

	private static final String CLOSED_PARENTHESIS_PATTERN = "[^)]"; //$NON-NLS-1$

	private static final String OPEN_PARENTHESIS_PATTERN = "[^(]"; //$NON-NLS-1$

	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	public TaskUrlHyperlinkDetector() {
	}

	@Override
	protected List<IHyperlink> detectHyperlinks(ITextViewer textViewer, String content, int indexInContent,
			int contentOffset) {
		List<IHyperlink> links = null;
		Matcher m = URL_PATTERN.matcher(content);
		while (m.find()) {
			if (isInRegion(indexInContent, m)) {
				String urlString = getUrlString(content, m);
				IHyperlink link = null;
				if (getAdapter(TaskRepository.class) != null) {
					try {
						new URL(urlString);
						link = createTaskUrlHyperlink(contentOffset, m, urlString);
					} catch (MalformedURLException e) {
						// ignore
					}

				} else {
					if (TasksUiInternal.isTaskUrl(urlString)) {
						link = createTaskUrlHyperlink(contentOffset, m, urlString);
					}
				}

				if (link != null) {
					if (links == null) {
						links = new ArrayList<IHyperlink>();
					}
					links.add(link);
				}
			}
		}
		return links;
	}

	private String getUrlString(String content, Matcher m) {
		String urlString = m.group(1);
		// check if the urlString has more opening parenthesis than closing 
		int parenthesisDiff = urlString.replaceAll(OPEN_PARENTHESIS_PATTERN, EMPTY_STRING).length()
				- urlString.replaceAll(CLOSED_PARENTHESIS_PATTERN, EMPTY_STRING).length();

		if (parenthesisDiff > 0) {
			// if any open paranthesis were not closed assume that trailing closing parenthesis are part of URL
			for (int i = m.end(); i - m.end() < parenthesisDiff; i++) {
				if (i >= content.length() || content.charAt(i) != ')') {
					break;
				}
				urlString += ')';
			}
		}
		return urlString;
	}

	private static boolean isInRegion(int offsetInText, Matcher m) {
		return (offsetInText == -1) || (offsetInText >= m.start() && offsetInText <= m.end());
	}

	private static IHyperlink createTaskUrlHyperlink(int textOffset, Matcher m, String urlString) {
		return new UrlHyperlink(new Region(textOffset + m.start(), urlString.length()), urlString);
	}

}
