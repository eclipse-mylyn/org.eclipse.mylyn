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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TaskHyperlink;

/**
 * @author Steffen Pingel
 */
public class TaskRelationHyperlinkDetector extends TaskHyperlinkDetector {

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		if (region == null || textViewer == null || textViewer.getDocument() == null) {
			return null;
		}

		TaskRepository taskRepository = getTaskRepository(textViewer);
		if (taskRepository != null) {
			String prefix = extractPrefix(textViewer, region.getOffset());
			String postfix = extractPostfix(textViewer, region.getOffset());
			String taskKey = prefix + postfix;
			if (taskKey != null) {
				Region hyperlinkRegion = new Region(region.getOffset() - prefix.length(), taskKey.length());
				return new IHyperlink[] { new TaskHyperlink(hyperlinkRegion, taskRepository, taskKey) };
			}
		}
		return null;
	}

	private String extractPrefix(ITextViewer viewer, int offset) {
		int i = offset;
		IDocument document = viewer.getDocument();
		if (i > document.getLength()) {
			return "";
		}

		try {
			if (isSeparator(document.getChar(i))) {
				return "";
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
			return "";
		}
	}

	private String extractPostfix(ITextViewer viewer, int offset) {
		int i = offset;
		IDocument document = viewer.getDocument();
		int length = document.getLength();
		if (i > length) {
			return "";
		}

		try {
			if (isSeparator(document.getChar(i))) {
				return "";
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
		return "";
	}

	private boolean isSeparator(char ch) {
		return Character.isWhitespace(ch) || ch == ',';
	}

}
