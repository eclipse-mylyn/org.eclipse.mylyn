/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractTaskHyperlinkDetector;
import org.eclipse.mylyn.tasks.ui.TaskHyperlink;

/**
 * @author Steffen Pingel
 */
public class TaskRelationHyperlinkDetector extends AbstractTaskHyperlinkDetector {

	private static Pattern HYPERLINK_PATTERN = Pattern.compile("([^\\s,]+)"); //$NON-NLS-1$

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		if (region.getLength() > 0) {
			return super.detectHyperlinks(textViewer, region, canShowMultipleHyperlinks);
		} else {
			if (textViewer.getDocument() == null) {
				return null;
			}

			TaskRepository taskRepository = getTaskRepository(textViewer);
			if (taskRepository != null) {
				String prefix = extractPrefix(textViewer, region.getOffset());
				String postfix = extractPostfix(textViewer, region.getOffset());
				String taskKey = prefix + postfix;
				if (taskKey.length() > 0) {
					Region hyperlinkRegion = new Region(region.getOffset() - prefix.length(), taskKey.length());
					return new IHyperlink[] { new TaskHyperlink(hyperlinkRegion, taskRepository, taskKey) };
				}
			}
		}
		return null;
	}

	@Override
	protected List<IHyperlink> detectHyperlinks(ITextViewer textViewer, String content, int index, int contentOffset) {
		List<IHyperlink> links = null;
		for (TaskRepository repository : getTaskRepositories(textViewer)) {
			Matcher m = HYPERLINK_PATTERN.matcher(content);
			while (m.find()) {
				if (links == null) {
					links = new ArrayList<IHyperlink>();
				}
				Region region = new Region(contentOffset + m.start(), m.end() - m.start());
				links.add(new TaskHyperlink(region, repository, m.group()));
			}
		}
		return links;
	}

	private String extractPrefix(ITextViewer viewer, int offset) {
		int i = offset;
		IDocument document = viewer.getDocument();
		if (i > document.getLength()) {
			return ""; //$NON-NLS-1$
		}

		try {
			if (isSeparator(document.getChar(i))) {
				return ""; //$NON-NLS-1$
			}
			while (i > 0) {
				char ch = document.getChar(i - 1);
				if (isSeparator(ch)) {
					break;
				}
				i--;
			}
			return document.get(i, offset - i);
		} catch (BadLocationException e) {
			return ""; //$NON-NLS-1$
		}
	}

	private String extractPostfix(ITextViewer viewer, int offset) {
		int i = offset;
		IDocument document = viewer.getDocument();
		int length = document.getLength();
		if (i > length) {
			return ""; //$NON-NLS-1$
		}

		try {
			if (isSeparator(document.getChar(i))) {
				return ""; //$NON-NLS-1$
			}
			while (i < length - 1) {
				char ch = document.getChar(i + 1);
				if (isSeparator(ch)) {
					break;
				}
				i++;
			}
			return document.get(offset, i - offset + 1);
		} catch (BadLocationException e) {
		}
		return ""; //$NON-NLS-1$
	}

	private boolean isSeparator(char ch) {
		return Character.isWhitespace(ch) || ch == ',';
	}

}
