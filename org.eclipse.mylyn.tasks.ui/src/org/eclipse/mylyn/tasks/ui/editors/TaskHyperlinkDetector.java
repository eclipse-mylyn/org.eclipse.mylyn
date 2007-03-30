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

package org.eclipse.mylar.tasks.ui.editors;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

/**
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class TaskHyperlinkDetector extends AbstractHyperlinkDetector {

	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		if (region == null || textViewer == null) {
			return null;
		}

		TaskRepository repository = getRepository(textViewer);
		if (repository == null) {
			return null;
		}

		AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getRepositoryUi(repository.getKind());
		if (connectorUi == null) {
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

		return connectorUi.findHyperlinks(repository, line, region.getOffset() - lineInfo.getOffset(), lineInfo
				.getOffset());
	}

	private TaskRepository getRepository(ITextViewer textViewer) {
		IResource resource = (IResource) getAdapter(IResource.class);
		if (resource == null) {
			if (textViewer instanceof RepositoryTextViewer) {
				RepositoryTextViewer viewer = (RepositoryTextViewer) textViewer;
				return viewer.getRepository();
			} else {
				IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.getActiveEditor();
				IEditorInput input = part.getEditorInput();
				resource = (IResource) input.getAdapter(IResource.class);
			}
		}
		if (resource != null) {
			return TasksUiPlugin.getDefault().getRepositoryForResource(resource, true);
		}
		return null;
	}
}
