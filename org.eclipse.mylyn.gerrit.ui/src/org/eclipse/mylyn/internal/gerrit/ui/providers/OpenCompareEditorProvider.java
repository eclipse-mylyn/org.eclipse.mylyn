/*******************************************************************************
 * Copyright (c) 2012 Ericsson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Miles Parker, Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.providers;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareUI;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.gerrit.ui.GerritReviewBehavior;
import org.eclipse.mylyn.internal.reviews.ui.compare.FileItemCompareEditorInput;
import org.eclipse.mylyn.internal.reviews.ui.views.ReviewExplorer;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IFileRevision;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.ITopic;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

/**
 * @author Miles Parker
 */
public class OpenCompareEditorProvider extends CommonActionProvider {

	private Action openAction;

	@Override
	public void init(ICommonActionExtensionSite site) {
		if (site.getViewSite() instanceof ICommonViewerWorkbenchSite) {
			final ICommonViewerWorkbenchSite viewSite = (ICommonViewerWorkbenchSite) site.getViewSite();
			final ISelectionProvider selectionProvider = viewSite.getSelectionProvider();
			CommonViewer viewer = (CommonViewer) selectionProvider;
			final ReviewExplorer explorer = (ReviewExplorer) viewer.getCommonNavigator();
			openAction = new Action() {

				@Override
				public void run() {
					IStructuredSelection selection = (IStructuredSelection) selectionProvider.getSelection();
					if (selection.size() == 1) {
						IFileItem fileItem = getFileFor(selection.getFirstElement());
						if (fileItem != null) {
							IWorkbenchPart currentPart = explorer.getCurrentPart();
							if (currentPart instanceof TaskEditor) {
								TaskEditor part = (TaskEditor) currentPart;
								TaskEditorInput input =  (TaskEditorInput) part.getEditorInput();
								GerritReviewBehavior behavior = new GerritReviewBehavior(input.getTask());
								CompareConfiguration configuration = new CompareConfiguration();
								CompareUI.openCompareEditor(new FileItemCompareEditorInput(configuration, fileItem,
										behavior));
							}
						}
					}
				}

				@Override
				public boolean isEnabled() {
					return true;
				}
			};
		}
	}

	private static IFileItem getFileFor(Object element) {
		//TODO Move to adapter?
		if (element instanceof ITopic) {
			return getFileFor(((ITopic) element).getItem());
		}
		if (element instanceof IFileRevision) {
			return ((IFileRevision) element).getFile();
		}
		if (element instanceof IFileItem) {
			return (IFileItem) element;
		}
		return null;
	}

	private static IReview getReviewFor(Object element) {
		if (element instanceof ITopic) {
			return ((ITopic) element).getReview();
		}
		if (element instanceof IFileRevision) {
			return ((IFileRevision) element).getReview();
		}
		if (element instanceof IFileItem) {
			return ((IFileItem) element).getReview();
		}
		return null;
	}

	@Override
	public void fillActionBars(IActionBars actionBars) {
		if (openAction.isEnabled()) {
			actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN, openAction);
		}
	}
}
