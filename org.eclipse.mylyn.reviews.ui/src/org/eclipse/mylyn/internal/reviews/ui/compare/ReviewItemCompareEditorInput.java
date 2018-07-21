/*******************************************************************************
 * Copyright (c) 2009, 2015 Atlassian and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Atlassian - initial API and implementation
 *     Tasktop Technologies - improvements
 *     Sebastien Dubois (Ericsson) - Improvements for bug 400266
 *     Guy Perron (Ericsson) - Bug 422673 Insert annotation navigation
 ******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.compare;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareViewerPane;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.compare.structuremergeviewer.ICompareInput;
import org.eclipse.compare.structuremergeviewer.IDiffElement;
import org.eclipse.compare.structuremergeviewer.StructureDiffViewer;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.reviews.ui.Messages;
import org.eclipse.mylyn.internal.reviews.ui.ReviewsImages;
import org.eclipse.mylyn.internal.reviews.ui.ReviewsUiPlugin;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.ui.ReviewBehavior;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Steffen Pingel
 * @author Sebastien Dubois
 * @author Miles Parker
 * @author Guy Perron
 * @author Jacques Bouthillier
 */
public abstract class ReviewItemCompareEditorInput extends CompareEditorInput {

	private static final String NAVIGATION_GROUP = "navigation"; //$NON-NLS-1$

	private static final String ID_NEXT_COMMENT = ReviewsUiPlugin.PLUGIN_ID + "navigate.comment.next"; //$NON-NLS-1$

	private static final String ID_PREVIOUS_COMMENT = ReviewsUiPlugin.PLUGIN_ID + "navigate.comment.previous"; //$NON-NLS-1$

	final ReviewBehavior behavior;

	private ReviewCompareAnnotationSupport currentSupport;

	public ReviewItemCompareEditorInput(CompareConfiguration configuration, ReviewBehavior behavior) {
		super(configuration);
		this.behavior = behavior;
	}

	@Override
	public Viewer findStructureViewer(Viewer oldViewer, ICompareInput input, Composite parent) {
		Viewer structureViewer = super.findStructureViewer(oldViewer, input, parent);
		if (structureViewer instanceof StructureDiffViewer) {
			StructureDiffViewer diffViewer = (StructureDiffViewer) structureViewer;
			diffViewer.setLabelProvider(new LabelProvider() {

				@Override
				public String getText(Object element) {
					if (element instanceof ITypedElement) {
						return ((ITypedElement) element).getName();
					}
					return "<no name>"; //$NON-NLS-1$
				}

				@Override
				public Image getImage(Object element) {
					if (element instanceof IDiffElement) {
						IDiffElement input = (IDiffElement) element;
						int kind = input.getKind();
						//We need to swap additions and deletions as work-around. See https://bugs.eclipse.org/bugs/show_bug.cgi?id=410534
						if (kind == Differencer.ADDITION) {
							kind = Differencer.DELETION;
						} else if (kind == Differencer.DELETION) {
							kind = Differencer.ADDITION;
						}
						return getCompareConfiguration().getImage(input.getImage(), kind);
					}
					return null;
				}
			});
		}
		return structureViewer;
	}

	@Override
	public Viewer findContentViewer(Viewer oldViewer, ICompareInput input, Composite parent) {
		Viewer contentViewer = super.findContentViewer(oldViewer, input, parent);
		if (input instanceof FileItemNode && ((FileItemNode) input).getFileItem() != null) {
			currentSupport = ReviewCompareAnnotationSupport.getAnnotationSupport(contentViewer);
			currentSupport.setReviewItem(((FileItemNode) input).getFileItem(), behavior);
			initializeGotoCommentHandlers(parent);
		}
		return contentViewer;
	}

	public void gotoComment(IComment comment) {
		if (currentSupport != null) {
			currentSupport.gotoAnnotationWithComment(comment);
		}
	}

	private void initializeGotoCommentHandlers(Composite parent) {
		ToolBarManager tbm = CompareViewerPane.getToolBarManager(parent);
		if (tbm != null) {
			if (tbm.find(NAVIGATION_GROUP) != null) {
				if (tbm.find(ID_NEXT_COMMENT) == null) {
					Action goToNextAction = new Action(Messages.Reviews_NextComment, ReviewsImages.NEXT_COMMENT) {
						@Override
						public void run() {
							if (currentSupport != null) {
								currentSupport.gotoAnnotation(Direction.FORWARDS);
							}
						}
					};
					goToNextAction.setId(ID_NEXT_COMMENT);
					goToNextAction.setToolTipText(Messages.Reviews_NextComment_Tooltip);
					tbm.appendToGroup(NAVIGATION_GROUP, goToNextAction);
				}

				if (tbm.find(ID_PREVIOUS_COMMENT) == null) {
					Action goToPreviousAction = new Action(Messages.Reviews_PreviousComment,
							ReviewsImages.PREVIOUS_COMMENT) {
						@Override
						public void run() {
							if (currentSupport != null) {
								currentSupport.gotoAnnotation(Direction.BACKWARDS);
							}
						}
					};
					goToPreviousAction.setId(ID_PREVIOUS_COMMENT);
					goToPreviousAction.setToolTipText(Messages.Reviews_PreviousComment_Tooltip);
					tbm.appendToGroup(NAVIGATION_GROUP, goToPreviousAction);
				}
			} else {// bug 430151
				StatusHandler.log(new Status(IStatus.ERROR, ReviewsUiPlugin.PLUGIN_ID,
						"Could not create comment navigation buttons", new Exception())); //$NON-NLS-1$
			}
			tbm.update(true);
		}
	}

	public String getItemTaskId() {
		return behavior.getTask().getTaskId();
	}
}