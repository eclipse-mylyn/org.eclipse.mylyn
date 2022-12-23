/*******************************************************************************
 * Copyright (c) 2012, 2015 Ericsson
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Miles Parker, Tasktop Technologies - initial API and implementation
 *     Sebastien Dubois (Ericsson) - Improvements for bug 400266
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.providers;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.gerrit.ui.GerritCompareUi;
import org.eclipse.mylyn.internal.gerrit.ui.GerritReviewBehavior;
import org.eclipse.mylyn.internal.reviews.ui.ActiveReviewManager;
import org.eclipse.mylyn.internal.reviews.ui.ReviewsUiPlugin;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IFileVersion;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.navigator.CommonActionProvider;
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
			openAction = new Action() {

				@Override
				public void run() {
					IStructuredSelection selection = (IStructuredSelection) selectionProvider.getSelection();
					if (selection.size() == 1) {
						IFileItem fileItem = getFileFor(selection.getFirstElement());
						if (fileItem != null) {
							ActiveReviewManager reviewManager = ReviewsUiPlugin.getDefault().getReviewManager();
							if (reviewManager != null) {
								IWorkbenchPart currentPart = reviewManager.getCurrentPart();
								if (currentPart instanceof TaskEditor) {

									TaskEditor part = (TaskEditor) currentPart;
									TaskEditorInput input = (TaskEditorInput) part.getEditorInput();
									GerritReviewBehavior behavior = new GerritReviewBehavior(input.getTask());
									CompareConfiguration configuration = new CompareConfiguration();
									GerritCompareUi.openFileComparisonEditor(configuration, fileItem, behavior,
											selection);
								}
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
		if (element instanceof IComment) {
			return getFileFor(((IComment) element).getItem());
		}
		if (element instanceof IFileVersion) {
			return ((IFileVersion) element).getFile();
		}
		if (element instanceof IFileItem) {
			return (IFileItem) element;
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
