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

package org.eclipse.mylar.internal.java.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;

/**
 * @author Rob Elves
 */
public class JavaStackTraceHyperlinkDetector implements IHyperlinkDetector {

	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {

		if (region == null || textViewer == null)
			return null;

		IDocument document = textViewer.getDocument();

		int offset = region.getOffset();

		// String urlString= null;
		if (document == null)
			return null;

		IRegion lineInfo;
		String line;
		try {
			lineInfo = document.getLineInformationOfOffset(offset);
			line = document.get(lineInfo.getOffset(), lineInfo.getLength());
		} catch (BadLocationException ex) {
			return null;
		}

		// int offsetInLine = offset - lineInfo.getOffset();

		Pattern p = Pattern.compile("[ {1}|\\n].*({1}.*.{1}java:\\d*){1}", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(line);

		if (m.find()) {
			IRegion urlRegion = new Region(lineInfo.getOffset() + m.start() + 1, m.end() - m.start());
			return new IHyperlink[] { new JavaStackTraceFileHyperlink(urlRegion, m.group()) };
		}

		// StringMatcher javaElementMatcher = new StringMatcher("*(*.java:*)",
		// true, false);//[\r|\n|\b|^]
		//		
		// Position position = javaElementMatcher.find(line, 0, line.length());
		// if (position != null) {
		// //String linkText = line.substring(position.getStart() + 1,
		// position.getEnd() - 1);
		// IRegion urlRegion= new Region(lineInfo.getOffset() +
		// position.getStart() + 1, position.getEnd() - position.getStart() -
		// 2);
		// return new IHyperlink[] {new JavaStackTraceFileHyperlink(urlRegion,
		// line)};
		// }
		return null;
	}

}
