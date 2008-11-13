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

package org.eclipse.mylyn.tasks.ui;

import java.util.ArrayList;
import java.util.Iterator;
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
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Base class for hyperlink detectors that provides methods for extracting text from an {@link ITextViewer}.
 * 
 * @author Rob Elves
 * @author Steffen Pingel
 * @author Eugene Kuleshov
 * @author Terry Hon
 * @since 3.1
 */
public abstract class AbstractTaskHyperlinkDetector extends AbstractHyperlinkDetector {

	/**
	 * @since 3.1
	 */
	public AbstractTaskHyperlinkDetector() {
	}

	/**
	 * @since 3.1
	 */
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, final IRegion region, boolean canShowMultipleHyperlinks) {
		IDocument document = textViewer.getDocument();
		if (document == null || document.getLength() == 0) {
			return null;
		}

		String content;
		int contentOffset;
		int index;
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
					index = region.getOffset() - lineOffset;
				} else {
					// the line starts after region, may never happen 
					int regionLength = Math.max(regionEnd, lineEnd) - region.getOffset();
					contentOffset = region.getOffset();
					content = document.get(contentOffset, regionLength);
					index = 0;
				}
			} else {
				content = document.get(region.getOffset(), region.getLength());
				contentOffset = region.getOffset();
				index = -1;
			}
		} catch (BadLocationException ex) {
			return null;
		}

		List<IHyperlink> hyperlinks = detectHyperlinks(textViewer, content, index, contentOffset);
		if (hyperlinks == null) {
			return null;
		}

		// filter hyperlinks that do not match original region
		if (region.getLength() == 0) {
			for (Iterator<IHyperlink> it = hyperlinks.iterator(); it.hasNext();) {
				IHyperlink hyperlink = it.next();
				IRegion hyperlinkRegion = hyperlink.getHyperlinkRegion();
				if (!isInRegion(region, hyperlinkRegion)) {
					it.remove();
				}
			}
		}
		if (hyperlinks.isEmpty()) {
			return null;
		}
		return hyperlinks.toArray(new IHyperlink[hyperlinks.size()]);
	}

	/**
	 * @since 3.1
	 */
	protected abstract List<IHyperlink> detectHyperlinks(ITextViewer textViewer, String content, int index,
			int contentOffset);

	private boolean isInRegion(IRegion detectInRegion, IRegion hyperlinkRegion) {
		return detectInRegion.getOffset() >= hyperlinkRegion.getOffset()
				&& detectInRegion.getOffset() <= hyperlinkRegion.getOffset() + hyperlinkRegion.getLength();
	}

	/**
	 * @since 3.1
	 */
	protected List<TaskRepository> getTaskRepositories(ITextViewer textViewer) {
		List<TaskRepository> repositories = new ArrayList<TaskRepository>();
		TaskRepository selectedRepository = getTaskRepository(textViewer);
		if (selectedRepository != null) {
			repositories.add(selectedRepository);
		} else {
			repositories.addAll(TasksUi.getRepositoryManager().getAllRepositories());
		}
		return repositories;
	}

	/**
	 * @since 3.1
	 */
	protected TaskRepository getTaskRepository(ITextViewer textViewer) {
		TaskRepository repository = (TaskRepository) getAdapter(TaskRepository.class);
		if (repository != null) {
			return repository;
		}

		IResource resource = (IResource) getAdapter(IResource.class);
		if (resource == null) {
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