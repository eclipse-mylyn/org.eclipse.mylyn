/*******************************************************************************
 * Copyright (c) 2009 Atlassian and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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
import org.eclipse.mylyn.internal.provisional.commons.ui.WorkbenchUtil;
import org.eclipse.mylyn.internal.reviews.ui.ReviewUiUtil;
import org.eclipse.mylyn.internal.reviews.ui.ReviewsUiPlugin;
import org.eclipse.mylyn.internal.reviews.ui.annotations.IReviewCompareSourceViewer;
import org.eclipse.mylyn.internal.reviews.ui.dialogs.AddCommentDialog;
import org.eclipse.mylyn.reviews.core.model.ILineLocation;
import org.eclipse.mylyn.reviews.core.model.ILineRange;
import org.eclipse.mylyn.reviews.core.model.ILocation;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.mylyn.reviews.internal.core.model.ReviewsFactory;
import org.eclipse.mylyn.reviews.ui.ReviewUi;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

/**
 * Action for adding a comment to a line in the active review
 * 
 * @author Shawn Minto
 */
public class AddLineCommentToFileAction extends AbstractReviewAction {

	private final IReviewCompareSourceViewer compareSourceViewer;

	private IEditorInput editorInput;

	private LineRange selectedRange;

	private final IReviewItem item;

	public AddLineCommentToFileAction(IReviewCompareSourceViewer compareSourceViewer, IReviewItem item) {
		super("Add Comment...");
		this.compareSourceViewer = compareSourceViewer;
		this.item = item;
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
				return true;
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
						"Editor is not an ITextEditor or there's no text selection available."));
			}
		}

		return lines;
	}

	@Override
	public String getToolTipText() {
		return "Adds a Review Comment For the Selected Line";
	}

	protected LineRange getSelectedRange() {
		//if its the action from the compareeditor, get currently selected lines
		if (compareSourceViewer != null) {
			return compareSourceViewer.getSelection();
		} else {
			return getJavaEditorSelection(getEditorInput());
		}
	}

	protected IEditorInput getEditorInput() {
		return editorInput;
	}

	public ILocation getLocation() {
		LineRange selectedRange = getSelectedRange();
		ILineLocation location = ReviewsFactory.eINSTANCE.createLineLocation();
		ILineRange range = ReviewsFactory.eINSTANCE.createLineRange();
		range.setStart(selectedRange.getStartLine());
		range.setEnd(selectedRange.getStartLine() + selectedRange.getNumberOfLines());
		location.getRanges().add(range);
		return location;
	}

	public void run(IAction action) {
		AddCommentDialog dialog = new AddCommentDialog(WorkbenchUtil.getShell(), ReviewUi.getActiveReview(), item,
				getLocation());
		dialog.open();
	}

}
