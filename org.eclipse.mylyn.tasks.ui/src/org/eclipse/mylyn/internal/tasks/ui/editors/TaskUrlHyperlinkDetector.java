/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     David Green - fix for bug 266693
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
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

	public TaskUrlHyperlinkDetector() {
	}

	@Override
	protected List<IHyperlink> detectHyperlinks(ITextViewer textViewer, String content, int indexInContent,
			int contentOffset) {
		List<IHyperlink> links = null;
		Matcher m = URL_PATTERN.matcher(content);
		while (m.find()) {
			if (isInRegion(indexInContent, m)) {
				String urlString = m.group(1);
				TaskUrlHyperlink link = null;
				if (getAdapter(TaskRepository.class) != null) {
					try {
						new URL(urlString);
						link = new TaskUrlHyperlink(determineRegion(contentOffset, m), urlString);
					} catch (MalformedURLException e) {
						// ignore
					}

				} else {
					if (TasksUiInternal.isTaskUrl(urlString)) {
						link = new TaskUrlHyperlink(determineRegion(contentOffset, m), urlString);
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

	private static boolean isInRegion(int offsetInText, Matcher m) {
		return (offsetInText == -1) || (offsetInText >= m.start() && offsetInText <= m.end());
	}

	private static IRegion determineRegion(int textOffset, Matcher m) {
		return new Region(textOffset + m.start(), m.end() - m.start());
	}

}
