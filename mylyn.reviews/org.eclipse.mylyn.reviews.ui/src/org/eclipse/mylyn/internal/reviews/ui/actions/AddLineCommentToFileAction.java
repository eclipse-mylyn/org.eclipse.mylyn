/*******************************************************************************
 * Copyright (c) 2009, 2014 Atlassian and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Atlassian - initial API and implementation
 ******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.actions;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.source.LineRange;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.internal.reviews.ui.ReviewUiUtil;
import org.eclipse.mylyn.internal.reviews.ui.ReviewsUiPlugin;
import org.eclipse.mylyn.internal.reviews.ui.annotations.IReviewCompareSourceViewer;
import org.eclipse.mylyn.internal.reviews.ui.dialogs.AddCommentDialog;
import org.eclipse.mylyn.reviews.core.model.ILineLocation;
import org.eclipse.mylyn.reviews.core.model.ILineRange;
import org.eclipse.mylyn.reviews.core.model.ILocation;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.mylyn.reviews.internal.core.model.ReviewsFactory;
import org.eclipse.mylyn.reviews.ui.ReviewBehavior;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

/**
 * Action for adding a comment to a line in the active review
 * 
 * @author Shawn Minto
 * @author Miles Parker
 */
public class AddLineCommentToFileAction extends AbstractReviewAction {

	private final IReviewCompareSourceViewer compareSourceViewer;

	private IEditorInput editorInput;

	private LineRange selectedRange;

	public AddLineCommentToFileAction(IReviewCompareSourceViewer compareSourceViewer) {
		super(Messages.AddLineCommentToFileAction_Add_Comment);
		this.compareSourceViewer = compareSourceViewer;
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		super.selectionChanged(action, selection);
		editorInput = getEditorInputFromSelection(selection);
	}

	@Override
	protected boolean updateSelection(IStructuredSelection selection) {
		if (compareSourceViewer != null) {
			selectedRange = compareSourceViewer.getSelection();
			if (selectedRange != null) {
				return !ReviewUiUtil.isAnonymous(getItem());
			}
		}
		return false;
	}

	private LineRange getJavaEditorSelection(IEditorInput editorInput) {
		LineRange lines = null;

		IEditorPart editorPart = getActiveEditor();

		if (editorPart != null && editorInput != null) {
			lines = ReviewUiUtil.getSelectedLineNumberRangeFromEditorInput(editorPart, editorInput);
			if (lines == null) {
				StatusHandler.log(new Status(IStatus.INFO, ReviewsUiPlugin.PLUGIN_ID,
						"Editor is not an ITextEditor or there's no text selection available.")); //$NON-NLS-1$
			}
		}

		return lines;
	}

	@Override
	public String getToolTipText() {
		return Messages.AddLineCommentToFileAction_Add_Comment_tooltip;
	}

	protected LineRange getSelectedRange() {
		//if its the action from the compare editor, get currently selected lines
		if (compareSourceViewer != null) {
			LineRange selectedRange = compareSourceViewer.getSelection();
			int maxNumberOfLines = compareSourceViewer.getAnnotationModel().getDocument().getNumberOfLines();
			if (selectedRange.getStartLine() == maxNumberOfLines) {
				selectedRange = new LineRange(maxNumberOfLines - 1, 1);
			}
			return selectedRange;
		} else {
			return getJavaEditorSelection(getEditorInput());
		}
	}

	protected IEditorInput getEditorInput() {
		return editorInput;
	}

	private ILocation getLocation() {
		LineRange selectedRange = getSelectedRange();
		ILineLocation location = ReviewsFactory.eINSTANCE.createLineLocation();
		ILineRange range = ReviewsFactory.eINSTANCE.createLineRange();
		range.setStart(selectedRange.getStartLine());
		range.setEnd(selectedRange.getStartLine() + selectedRange.getNumberOfLines());
		location.getRanges().add(range);
		return location;
	}

	@Override
	public void run(IAction action) {
		IReviewItem item = getItem();
		ReviewBehavior reviewBehavior = compareSourceViewer.getAnnotationModel().getBehavior();

		AddCommentDialog dialog = new AddCommentDialog(WorkbenchUtil.getShell(), reviewBehavior, item, getLocation());
		dialog.open();
	}

	public IReviewItem getItem() {
		if (compareSourceViewer != null && compareSourceViewer.getAnnotationModel() != null) {
			return compareSourceViewer.getAnnotationModel().getItem();
		}
		return null;
	}

}
