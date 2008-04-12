/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * @author Rob Elves
 * @author Steffen Pingel
 * @author Eugene Kuleshov
 * @author Terry Hon
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

		List<TaskRepository> repositories = new ArrayList<TaskRepository>();

		TaskRepository selectedRepository = getRepository(textViewer);
		if (selectedRepository != null) {
			repositories.add(selectedRepository);
		} else {
			repositories.addAll(TasksUi.getRepositoryManager().getAllRepositories());
		}

		List<IHyperlink> hyperlinks = new ArrayList<IHyperlink>();
		for (TaskRepository repository : repositories) {
			AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(repository.getConnectorKind());
			if (connectorUi == null) {
				continue;
			}
			IHyperlink[] links = (connectorUi.findHyperlinks(repository, line, //
					region.getOffset() - lineInfo.getOffset(), lineInfo.getOffset()));
			if (links == null) {
				continue;
			}
			hyperlinks.addAll(Arrays.asList(links));
		}

		if (hyperlinks.isEmpty()) {
			return null;
		}
		return hyperlinks.toArray(new IHyperlink[hyperlinks.size()]);
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
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			if (window != null) {
				IWorkbenchPage activePage = window.getActivePage();
				if (activePage != null) {
					IWorkbenchPart part = activePage.getActivePart();
					if (part instanceof IEditorPart) {
						IEditorInput input = ((IEditorPart) part).getEditorInput();
						if (input != null) {
							resource = (IResource) input.getAdapter(IResource.class);
						}
					}
				}
			}
		}
		if (resource != null) {
			return TasksUiPlugin.getDefault().getRepositoryForResource(resource, true);
		}
		return null;
	}

}
