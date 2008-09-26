/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Eugene Kuleshov - improvements
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
import org.eclipse.jface.text.Region;
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
		if (document == null || document.getLength() == 0) {
			return null;
		}

		String content;
		int contentOffset;

		try {
			if (region.getLength() == 0) {
				// expand the region to include the whole line
				IRegion lineInfo = document.getLineInformationOfOffset(region.getOffset());
				int lineLength = lineInfo.getLength();
				int lineOffset = lineInfo.getOffset();
				int lineEnd = lineOffset + lineLength;
				int regionEnd = region.getOffset() + region.getLength();
				if (lineOffset < region.getOffset()) {
					int regionLength = Math.max(regionEnd, lineEnd) - lineOffset;
					contentOffset = lineOffset;
					content = document.get(lineOffset, regionLength);
					region = new Region(0, regionLength);
				} else {
					int regionLength = Math.max(regionEnd, lineEnd) - region.getOffset();
					contentOffset = region.getOffset();
					content = document.get(contentOffset, regionLength);
					region = new Region(0, regionLength);
				}
			} else {
				content = document.get(region.getOffset(), region.getLength());
				contentOffset = region.getOffset();
				region = new Region(0, region.getLength());
			}
		} catch (BadLocationException ex) {
			return null;
		}

		List<TaskRepository> repositories = new ArrayList<TaskRepository>();

		TaskRepository selectedRepository = getTaskRepository(textViewer);
		if (selectedRepository != null) {
			repositories.add(selectedRepository);
		} else {
			repositories.addAll(TasksUi.getRepositoryManager().getAllRepositories());
		}

		List<IHyperlink> hyperlinks = new ArrayList<IHyperlink>();
		detectHyperlinks(content, contentOffset, region, repositories, hyperlinks);
		if (hyperlinks.isEmpty()) {
			return null;
		}
		return hyperlinks.toArray(new IHyperlink[hyperlinks.size()]);
	}

	private void detectHyperlinks(String content, int contentOffset, IRegion region, List<TaskRepository> repositories,
			List<IHyperlink> hyperlinks) {
		for (TaskRepository repository : repositories) {
			AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(repository.getConnectorKind());
			if (connectorUi == null) {
				continue;
			}
			IHyperlink[] links = connectorUi.findHyperlinks(repository, content, contentOffset, region);
			if (links == null) {
				continue;
			}
			hyperlinks.addAll(Arrays.asList(links));
		}
	}

	protected TaskRepository getTaskRepository(ITextViewer textViewer) {
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
			return TasksUiPlugin.getDefault().getRepositoryForResource(resource);
		}
		return null;
	}

}
