/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others. 
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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;

/**
 * Detects URLs based on a regular expression.
 * 
 * @author David Green
 */
public class TaskUrlHyperlinkDetector extends AbstractHyperlinkDetector {

	// based on RFC 3986
	// even though it's valid, the platform hyperlink detector doesn't detect hyperlinks that end with '.', ',' or ')'
	// so we do the same here
	private static final Pattern URL_PATTERN = Pattern.compile("([a-zA-Z][a-zA-Z+.-]{0,10}://[a-zA-Z0-9%._~!$&?#'()*+,;:@/=-]*[a-zA-Z0-9%_~!$&?#'(*+;:@/=-])"); //$NON-NLS-1$

	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		if (region == null || textViewer == null) {
			return null;
		}

		IDocument document = textViewer.getDocument();

		int offset = region.getOffset();
		if (document == null) {
			return null;
		}

		IRegion lineInfo;
		String line;
		try {
			lineInfo = document.getLineInformationOfOffset(offset);
			line = document.get(lineInfo.getOffset(), lineInfo.getLength());
		} catch (BadLocationException ex) {
			return null;
		}

		// offset of the region in the line
		final int offsetInLine = offset - lineInfo.getOffset();
		final int regionLength = region.getLength();

		List<IHyperlink> hyperlinks = null;
		Matcher matcher = URL_PATTERN.matcher(line);
		while (matcher.find()) {
			int urlOffsetInLine = matcher.start(1);
			int endUrlOffsetInLine = matcher.end(1);
			if ((regionLength == 0 && offsetInLine >= urlOffsetInLine && offsetInLine <= endUrlOffsetInLine)
					|| ((regionLength > 0 && offsetInLine <= urlOffsetInLine && (offsetInLine + regionLength) > urlOffsetInLine))) {
				// region length of 0 and offset hits within the hyperlink url
				// OR
				// region spans the start of the hyperlink url.

				// verify that the URL is valid
				try {
					String urlString = matcher.group(1);
					new URL(urlString);

					// URL looks okay, so add a hyperlink
					if (hyperlinks == null) {
						hyperlinks = new ArrayList<IHyperlink>(5);
					}
					IRegion urlRegion = new Region(lineInfo.getOffset() + urlOffsetInLine, endUrlOffsetInLine
							- urlOffsetInLine);
					hyperlinks.add(new TaskUrlHyperlink(urlRegion, urlString));
				} catch (MalformedURLException e) {
					// ignore
				}
			}
		}
		return hyperlinks == null ? null : hyperlinks.toArray(new IHyperlink[hyperlinks.size()]);
	}

}
