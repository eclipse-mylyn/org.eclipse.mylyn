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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.mylar.internal.tasklist.ui.editors.RepositoryTextViewer;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;

/**
 * @author Rob Elves
 */
public class BugzillaTaskHyperlinkDetector implements IHyperlinkDetector {

	private TaskRepository repository;

	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		if (region == null || textViewer == null)
			return null;

		if (!(textViewer instanceof RepositoryTextViewer))
			return null;

		RepositoryTextViewer viewer = (RepositoryTextViewer) textViewer;

		repository = viewer.getRepository();

		if (repository == null)
			return null;

		IDocument document = textViewer.getDocument();

		int offset = region.getOffset();

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

		int offsetInLine = offset - lineInfo.getOffset();

		IHyperlink[] links = BugzillaHyperlinkUtil.findBugHyperlinks(repository.getUrl(), offsetInLine, lineInfo.getLength(),
				line, lineInfo.getOffset());

		return links;

	}

}
