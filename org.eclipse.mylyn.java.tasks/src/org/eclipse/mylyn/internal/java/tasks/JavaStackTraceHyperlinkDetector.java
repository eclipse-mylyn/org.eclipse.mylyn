/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.java.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylyn.tasks.ui.AbstractTaskHyperlinkDetector;

/**
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class JavaStackTraceHyperlinkDetector extends AbstractTaskHyperlinkDetector {

	private static final Pattern stackTracePattern = Pattern.compile("\\S*\\(([\\w\\$]*\\.java:\\d*)\\)",
			Pattern.CASE_INSENSITIVE);

	private static IRegion determineRegion(int textOffset, Matcher m, int group) {
		return new Region(textOffset + m.start(group), m.end(group) - m.start(group));
	}

	private static boolean isInRegion(int offsetInText, Matcher m) {
		return (offsetInText == -1) || (offsetInText >= m.start() && offsetInText <= m.end());
	}

	@Override
	protected List<IHyperlink> detectHyperlinks(ITextViewer textViewer, String content, int offsetInContent,
			int contentOffset) {
		List<IHyperlink> links = null;
		Matcher m = stackTracePattern.matcher(content);
		while (m.find()) {
			if (isInRegion(offsetInContent, m)) {
				if (links == null) {
					links = new ArrayList<IHyperlink>();
				}
				links.add(new JavaStackTraceFileHyperlink(determineRegion(contentOffset, m, 0), m.group(),
						determineRegion(contentOffset, m, 1)));
			}
		}
		return links;
	}

//	private static final Pattern stackTracePattern = Pattern.compile("\\S*\\.java:\\d*\\)", Pattern.CASE_INSENSITIVE);
//
//	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
//		IDocument document = textViewer.getDocument();
//		if (document == null) {
//			return null;
//		}
//
//		IRegion lineInfo;
//		String line;
//		try {
//			lineInfo = document.getLineInformationOfOffset(region.getOffset());
//			line = document.get(lineInfo.getOffset(), lineInfo.getLength());
//		} catch (BadLocationException ex) {
//			return null;
//		}
//
//		Matcher m = stackTracePattern.matcher(line);
//		if (m.find()) {
//			IRegion urlRegion = new Region(lineInfo.getOffset() + m.start(), m.end() - m.start());
//			return new IHyperlink[] { new JavaStackTraceFileHyperlink(urlRegion, m.group()) };
//		}
//
//		return null;
//	}

}
