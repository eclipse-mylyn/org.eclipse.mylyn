/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.trac.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylar.internal.trac.core.ITracClient;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.mylar.tasks.ui.editors.RepositoryTextViewer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

/**
 * @author Steffen Pingel
 */
public class TracHyperlinkDetector extends AbstractHyperlinkDetector {

	Pattern taskPattern = Pattern.compile("#(\\d*)");

	Pattern wikiPattern = Pattern.compile("\\[wiki:([^\\]]*)\\]", Pattern.CASE_INSENSITIVE | Pattern.DOTALL
			| Pattern.MULTILINE);

	public TracHyperlinkDetector() {
	}

	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		if (region == null || textViewer == null) {
			return null;
		}

		TaskRepository repository = getRepository(textViewer);
		if (repository == null) {
			return null;
		}

		IDocument document = textViewer.getDocument();
		if (document == null) {
			return null;
		}

		IRegion lineInfo;
		String line;
		try {
			lineInfo = document.getLineInformationOfOffset(region.getOffset());
			line = document.get(lineInfo.getOffset(), lineInfo.getLength());
		} catch (BadLocationException ex) {
			return null;
		}

		// TracRepositoryUi connector = (TracRepositoryUi)
		// TasksUiPlugin.getRepositoryUi(repository.getKind());
		return findHyperlinks(repository, line, region.getOffset() - lineInfo.getOffset(), lineInfo.getOffset());
	}

	private TaskRepository getRepository(ITextViewer textViewer) {
		if (textViewer instanceof RepositoryTextViewer) {
			RepositoryTextViewer viewer = (RepositoryTextViewer) textViewer;
			return viewer.getRepository();
		} else {
			// Get repository from files associated project -> repository
			// mapping
			IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			IEditorInput input = part.getEditorInput();
			IResource resource = (IResource) input.getAdapter(IResource.class);
			if (resource != null) {
				return TasksUiPlugin.getDefault().getRepositoryForResource(resource, true);
			}
		}
		return null;
	}

	/**
	 * Detects:
	 * 
	 * <ul>
	 * <li>#taskid
	 * <li>[wiki:page]
	 * <li>WikiPage
	 * </ul>
	 * 
	 * @param repository
	 * @param text
	 * @param region
	 * @return
	 */
	public IHyperlink[] findHyperlinks(TaskRepository repository, String text, int lineOffset, int regionOffset) {
		List<IHyperlink> links = new ArrayList<IHyperlink>();

		Matcher m = taskPattern.matcher(text);
		while (m.find()) {
			String id = m.group(1);
			if (lineOffset >= m.start() && lineOffset <= m.end()) {
				IRegion linkRegion = new Region(regionOffset + m.start(), m.end() - m.start());
				links.add(new TaskHyperlink(linkRegion, repository, id));
			}
		}

		m = wikiPattern.matcher(text);
		while (m.find()) {
			String id = m.group(1);
			if (lineOffset >= m.start() && lineOffset <= m.end()) {
				IRegion linkRegion = new Region(regionOffset + m.start(), m.end() - m.start());
				links.add(new WebHyperlink(linkRegion, repository.getUrl() + 
						ITracClient.WIKI_URL + id));
			}
		}
				
		return links.isEmpty() ? null : links.toArray(new IHyperlink[0]);
	}

}
