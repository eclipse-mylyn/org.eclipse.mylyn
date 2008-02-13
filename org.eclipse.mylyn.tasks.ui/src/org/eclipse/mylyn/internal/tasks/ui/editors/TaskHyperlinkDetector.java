/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

/**
 * @author Rob Elves
 * @author Steffen Pingel
 * @author Eugene Kuleshov
 */
public class TaskHyperlinkDetector extends AbstractHyperlinkDetector {

	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		if (region == null || textViewer == null) {
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

		if (line.length() == 0) {
			return null;
		}
		
		TaskRepository repository = getRepository(textViewer);
		if (repository == null) {
			repository = guessRepository(line);
		}
		if (repository == null) {
			return null;
		}

		AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(repository.getConnectorKind());
		if (connectorUi == null) {
			return null;
		}

		return connectorUi.findHyperlinks(repository, line, //
				region.getOffset() - lineInfo.getOffset(), lineInfo.getOffset());
	}

	private TaskRepository getRepository(ITextViewer textViewer) {
		TaskRepository repository = (TaskRepository) getAdapter(TaskRepository.class);
		if (repository != null) {
			return repository;
		}

		IResource resource = (IResource) getAdapter(IResource.class);
		if (resource == null) {
			if (textViewer instanceof RepositoryTextViewer) {
				RepositoryTextViewer viewer = (RepositoryTextViewer) textViewer;
				return viewer.getRepository();
			}

			// use currently active editor (if any)
			IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IWorkbenchPart part = activePage.getActivePart();
			if (part != null && part instanceof IEditorPart) {
				IEditorInput input = ((IEditorPart) part).getEditorInput();
				if (input != null) {
					resource = (IResource) input.getAdapter(IResource.class);
				}
			}
		}
		if (resource != null) {
			return TasksUiPlugin.getDefault().getRepositoryForResource(resource, true);
		}
		return null;
	}

	// TODO: extract similar code to OpenCorrespondingTaskAction#reconsile(ILinkedTaskInfo info)
	private TaskRepository guessRepository(String text) {
		TaskRepositoryManager manager = TasksUiPlugin.getRepositoryManager();
		for (AbstractRepositoryConnector c : manager.getRepositoryConnectors()) {
			for (TaskRepository repository : manager.getRepositories(c.getConnectorKind())) {
				String[] ids = c.getTaskIdsFromComment(repository, text);
				if (ids != null && ids.length > 0 && ids[0].length() > 0) {
					return repository;
				}
			}
		}
		return null;
	}

}
