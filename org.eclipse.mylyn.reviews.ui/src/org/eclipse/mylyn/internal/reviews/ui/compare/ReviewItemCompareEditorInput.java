/*******************************************************************************
 * Copyright (c) 2009 Atlassian and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Atlassian - initial API and implementation
 *     Tasktop Technologies - improvements
 *     Sebastien Dubois (Ericsson) - Improvements for bug 400266
 *     Guy Perron (Ericsson) - Bug 422673 Insert annotation navigation
 ******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.compare;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareViewerPane;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.compare.structuremergeviewer.ICompareInput;
import org.eclipse.compare.structuremergeviewer.IDiffElement;
import org.eclipse.compare.structuremergeviewer.StructureDiffViewer;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.internal.reviews.ui.Messages;
import org.eclipse.mylyn.internal.reviews.ui.ReviewsImages;
import org.eclipse.mylyn.internal.reviews.ui.ReviewsUiPlugin;
import org.eclipse.mylyn.reviews.ui.ReviewBehavior;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerActivation;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.services.IServiceLocator;

/**
 * @author Steffen Pingel
 * @author Sebastien Dubois
 * @author Miles Parker
 * @author Guy Perron
 * @author Jacques Bouthillier
 */
public abstract class ReviewItemCompareEditorInput extends CompareEditorInput {

	final ReviewBehavior behavior;

	private IHandlerActivation gotoNextCommentHandler;

	private IHandlerActivation gotoPreviousCommentHandler;

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
			ReviewCompareAnnotationSupport support = ReviewCompareAnnotationSupport.getAnnotationSupport(contentViewer);
			support.setReviewItem(((FileItemNode) input).getFileItem(), behavior);

			if (gotoNextCommentHandler == null && gotoPreviousCommentHandler == null) {
				initializeGotoCommentHandlers(parent, support);
			}
		}
		return contentViewer;
	}

	private void initializeGotoCommentHandlers(Composite parent, ReviewCompareAnnotationSupport support) {
		ToolBarManager tbm = CompareViewerPane.getToolBarManager(parent);
		if (tbm != null) {
			IServiceLocator serviceLocator = getServiceLocator();
			if (serviceLocator != null) {
				final IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(
						IHandlerService.class);
				if (handlerService != null) {
					gotoNextCommentHandler = handlerService.activateHandler(ReviewsUiPlugin.PLUGIN_ID
							+ ".commands.navigate.comment.next", //$NON-NLS-1$
							new GotoCommentHandler(Direction.FORWARDS, support));
					gotoPreviousCommentHandler = handlerService.activateHandler(ReviewsUiPlugin.PLUGIN_ID
							+ ".commands.navigate.comment.previous", //$NON-NLS-1$
							new GotoCommentHandler(Direction.BACKWARDS, support));
					final List<IHandlerActivation> activations = new ArrayList<IHandlerActivation>(Arrays.asList(
							gotoNextCommentHandler, gotoPreviousCommentHandler));
					parent.addDisposeListener(new DisposeListener() {
						@Override
						public void widgetDisposed(DisposeEvent e) {
							handlerService.deactivateHandlers(activations);
							activations.clear();
						}
					});
				}

				CommandContributionItemParameter p = new CommandContributionItemParameter(
						serviceLocator,
						ReviewsUiPlugin.PLUGIN_ID + ".navigate.comment.next", //$NON-NLS-1$
						ReviewsUiPlugin.PLUGIN_ID + ".commands.navigate.comment.next", //$NON-NLS-1$ // command id
						null, ReviewsImages.NEXT_COMMENT, ReviewsImages.NEXT_COMMENT, null,
						Messages.Reviews_NextComment, Messages.Reviews_NextComment.substring(0, 1),
						Messages.Reviews_NextComment_Tooltip, CommandContributionItem.STYLE_PUSH, null, true);

				tbm.appendToGroup("navigation", new CommandContributionItem(p)); //$NON-NLS-1$

				p = new CommandContributionItemParameter(
						serviceLocator, //
						ReviewsUiPlugin.PLUGIN_ID + ".navigate.comment.previous", //$NON-NLS-1$
						ReviewsUiPlugin.PLUGIN_ID + ".commands.navigate.comment.previous", //$NON-NLS-1$ // command id
						null, ReviewsImages.PREVIOUS_COMMENT, ReviewsImages.PREVIOUS_COMMENT, null,
						Messages.Reviews_PreviousComment, Messages.Reviews_PreviousComment.substring(0, 1),
						Messages.Reviews_PreviousComment_Tooltip, CommandContributionItem.STYLE_PUSH, null, true);
				tbm.appendToGroup("navigation", new CommandContributionItem(p)); //$NON-NLS-1$
				tbm.update(true);
			}
		}
	}
}