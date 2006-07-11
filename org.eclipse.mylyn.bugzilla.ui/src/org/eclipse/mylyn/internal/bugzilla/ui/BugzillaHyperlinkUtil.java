/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.internal.bugzilla.ui;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;

/**
 * @author Rob Elves (multiple bug/task hyperlink support)
 * @author Mik Kersten
 */
public class BugzillaHyperlinkUtil {

	private static final String regexp = "(bug|task)(\\s#|#|#\\s|\\s|)(\\s\\d+|\\d+)";

	private static final Pattern PATTERN = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);

	public static IHyperlink[] findBugHyperlinks(String repositoryUrl, int offset, String comment, int lineOffset) {
		ArrayList<IHyperlink> hyperlinksFound = new ArrayList<IHyperlink>();

		Matcher m = PATTERN.matcher(comment);
		while (m.find()) {
			if (offset >= m.start() && offset <= m.end()) {
				IHyperlink link = extractHyperlink(repositoryUrl, lineOffset, m);
				if (link != null)
					hyperlinksFound.add(link);
			}
		}

		if (hyperlinksFound.size() > 0) {
			return hyperlinksFound.toArray(new IHyperlink[1]);
		}
		return null;
	}

	private static IHyperlink extractHyperlink(String repositoryUrl, int lineOffset, Matcher m) {

		int start = m.start();
		int end = m.end();

		if (end == -1)
			end = m.group().length();

		try {

			String bugId = m.group(3).trim();
			start += lineOffset;
			end += lineOffset;

			IRegion sregion = new Region(start, end - start);
			return new BugzillaHyperLink(sregion, bugId, repositoryUrl);

		} catch (NumberFormatException e) {
			return null;
		}
	}
}
