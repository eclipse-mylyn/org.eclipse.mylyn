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

package org.eclipse.mylyn.internal.reviews.ui.annotations;

import java.lang.reflect.Method;

import org.eclipse.compare.contentmergeviewer.TextMergeViewer;
import org.eclipse.compare.internal.MergeSourceViewer;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.reviews.ui.ReviewsUiPlugin;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.ITopic;
import org.eclipse.swt.widgets.Display;

/**
 * Model for annotations in the diff view.
 * 
 * @author Thomas Ehrnhoefer
 */
@SuppressWarnings("restriction")
public class ReviewCompareAnnotationModel {

	static SourceViewer getSourceViewer(MergeSourceViewer sourceViewer) {
		if (SourceViewer.class.isInstance(sourceViewer)) {
			return SourceViewer.class.cast(sourceViewer);
		} else {
			Object returnValue;
			try {
				Method getSourceViewerRefl = MergeSourceViewer.class.getDeclaredMethod("getSourceViewer");
				getSourceViewerRefl.setAccessible(true);
				returnValue = getSourceViewerRefl.invoke(sourceViewer);
				if (returnValue instanceof SourceViewer) {
					return (SourceViewer) returnValue;
				}
			} catch (Exception e) {
				// ignore
			}
		}
		return null;
	}

	final ReviewAnnotationModel leftAnnotationModel;

	private final ReviewAnnotationModel rightAnnotationModel;

	private ReviewCompareInputListener leftViewerListener;

	private ReviewCompareInputListener rightViewerListener;

	private final ITopic commentToFocus;

	private TextMergeViewer fMergeViewer;

	private MergeSourceViewer fRightSourceViewer;

	private MergeSourceViewer fLeftSourceViewer;

	public ReviewCompareAnnotationModel(IFileItem reviewItem, ITopic commentToFocus) {
		this.leftAnnotationModel = new ReviewAnnotationModel(null, null, null, reviewItem.getTarget());
		this.rightAnnotationModel = new ReviewAnnotationModel(null, null, null, reviewItem.getBase());
		this.commentToFocus = commentToFocus;
	}

	public void attachToViewer(final TextMergeViewer viewer, final MergeSourceViewer fLeft,
			final MergeSourceViewer fRight) {
		fMergeViewer = viewer;
		fLeftSourceViewer = fLeft;
		fRightSourceViewer = fRight;

		/*
		 * only create listeners if they are not already existing
		 */
		if (!isListenerFor(leftViewerListener, fLeft, leftAnnotationModel)) {
			leftViewerListener = addTextInputListener(fLeft, leftAnnotationModel);
		} else {
			/*
			 * Using asyncExec here because if the underlying slaveDocument (part of the file that gets displayed when clicking
			 * on a java structure in the compare editor) is changed, but the master document is not, we do not get any event
			 * afterwards that would give us a place to hook our code to override the annotationHover. Since all is done in the
			 * UI thread, using this asyncExec hack works because the unconfigure and configure of the document is finished and
			 * our hover-hack stays.
			 */
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					try {
						// if listeners exist, just make sure the hover hack is in there
						leftViewerListener.forceCustomAnnotationHover();
					} catch (Exception e) {
						StatusHandler.log(new Status(IStatus.ERROR, ReviewsUiPlugin.PLUGIN_ID,
								"Error attaching annotation hover", e));
					}
				}
			});
		}
		if (!isListenerFor(rightViewerListener, fRight, rightAnnotationModel)) {
			rightViewerListener = addTextInputListener(fRight, rightAnnotationModel);
		} else {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					try {
						// if listeners exist, just make sure the hover hack is in there
						rightViewerListener.forceCustomAnnotationHover();
					} catch (Exception e) {
						StatusHandler.log(new Status(IStatus.ERROR, ReviewsUiPlugin.PLUGIN_ID,
								"Error attaching annotation hover", e));
					}
				}
			});
		}
	}

	private boolean isListenerFor(ReviewCompareInputListener listener, MergeSourceViewer viewer,
			ReviewAnnotationModel annotationModel) {
		if (listener == null) {
			return false;
		}
		return listener.isListenerFor(viewer, annotationModel);
	}

	private ReviewCompareInputListener addTextInputListener(final MergeSourceViewer sourceViewer,
			final ReviewAnnotationModel annotationModel) {
		ReviewCompareInputListener listener = new ReviewCompareInputListener(sourceViewer, annotationModel);
		SourceViewer viewer = getSourceViewer(sourceViewer);
		if (viewer != null) {
			viewer.addTextInputListener(listener);
		}
		return listener;
	}

	public void focusOnComment() {
		focusOnComment(commentToFocus);
	}

	public void focusOnComment(ITopic commentToFocus) {
		// FIXME
//		if (commentToFocus != null) {
//			CrucibleFile leftFile = leftAnnotationModel.getCrucibleFile();
//			VersionedVirtualFile virtualLeft = leftFile.getSelectedFile();
//
//			CrucibleFile rightFile = rightAnnotationModel.getCrucibleFile();
//			VersionedVirtualFile virtualRight = rightFile.getSelectedFile();
//
//			MergeSourceViewer focusViewer = null;
//			Map<String, IntRanges> lineRanges = commentToFocus.getLineRanges();
//			if (lineRanges != null) {
//				IntRanges range;
//				if ((range = lineRanges.get(virtualLeft.getRevision())) != null) {
//					// get the correct listener (new file is left)
//					leftViewerListener.focusOnLines(range);
//					focusViewer = fLeftSourceViewer;
//				} else if ((range = lineRanges.get(virtualRight.getRevision())) != null) {
//					rightViewerListener.focusOnLines(range);
//					focusViewer = fRightSourceViewer;
//				}
//				setActiveViewer(focusViewer);
//			}
//		}
	}

	private void setActiveViewer(MergeSourceViewer focusViewer) {
		try {
			Method setActiveViewer = TextMergeViewer.class.getDeclaredMethod("setActiveViewer",
					MergeSourceViewer.class, boolean.class);
			setActiveViewer.setAccessible(true);
			setActiveViewer.invoke(fMergeViewer, focusViewer, true);
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.WARNING, ReviewsUiPlugin.PLUGIN_ID, "Failed to activate viewer", e));
		}
	}

	public void registerContextMenu() {
		rightViewerListener.registerContextMenu();
		leftViewerListener.registerContextMenu();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((leftAnnotationModel == null) ? 0 : leftAnnotationModel.hashCode());
		result = prime * result + ((rightAnnotationModel == null) ? 0 : rightAnnotationModel.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ReviewCompareAnnotationModel other = (ReviewCompareAnnotationModel) obj;
		if (leftAnnotationModel == null) {
			if (other.leftAnnotationModel != null) {
				return false;
			}
		} else if (!leftAnnotationModel.equals(other.leftAnnotationModel)) {
			return false;
		}
		if (rightAnnotationModel == null) {
			if (other.rightAnnotationModel != null) {
				return false;
			}
		} else if (!rightAnnotationModel.equals(other.rightAnnotationModel)) {
			return false;
		}
		return true;
	}

}
