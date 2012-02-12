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

package org.eclipse.mylyn.internal.reviews.ui.compare;

import java.lang.reflect.Field;

import org.eclipse.compare.contentmergeviewer.TextMergeViewer;
import org.eclipse.compare.internal.MergeSourceViewer;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.reviews.ui.ReviewsUiPlugin;
import org.eclipse.mylyn.internal.reviews.ui.annotations.ReviewAnnotationModel;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.ui.ReviewBehavior;
import org.eclipse.swt.widgets.Display;

/**
 * Manages annotation models for compare viewers.
 * 
 * @author Thomas Ehrnhoefer
 * @author Steffen Pingel
 */
@SuppressWarnings("restriction")
public class ReviewCompareAnnotationSupport {

	private static String KEY_ANNOTAION_SUPPORT = ReviewItemSetCompareEditorInput.class.getName();

	public static ReviewCompareAnnotationSupport getAnnotationSupport(Viewer contentViewer) {
		ReviewCompareAnnotationSupport support = (ReviewCompareAnnotationSupport) contentViewer.getData(KEY_ANNOTAION_SUPPORT);
		if (support == null) {
			support = new ReviewCompareAnnotationSupport(contentViewer);
			contentViewer.setData(KEY_ANNOTAION_SUPPORT, support);
		}
		return support;
	}

	private ReviewBehavior behavior;

	private final ReviewAnnotationModel leftAnnotationModel;

	private ReviewCompareInputListener leftViewerListener;

	private final ReviewAnnotationModel rightAnnotationModel;

	private ReviewCompareInputListener rightViewerListener;

	public ReviewCompareAnnotationSupport(Viewer contentViewer) {
		this.leftAnnotationModel = new ReviewAnnotationModel();
		this.rightAnnotationModel = new ReviewAnnotationModel();
		install(contentViewer);
	}

	public void attachToViewer(final TextMergeViewer viewer, final MergeSourceViewer fLeft,
			final MergeSourceViewer fRight) {
		// only create listeners if they are not already existing
		if (!isListenerFor(leftViewerListener, fLeft, leftAnnotationModel)) {
			leftViewerListener = registerInputListener(fLeft, leftAnnotationModel);
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
			rightViewerListener = registerInputListener(fRight, rightAnnotationModel);
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
		ReviewCompareAnnotationSupport other = (ReviewCompareAnnotationSupport) obj;
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

	public ReviewBehavior getBehavior() {
		return behavior;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((leftAnnotationModel == null) ? 0 : leftAnnotationModel.hashCode());
		result = prime * result + ((rightAnnotationModel == null) ? 0 : rightAnnotationModel.hashCode());
		return result;
	}

	public void install(Viewer contentViewer) {
		// FIXME: hack
		if (contentViewer instanceof TextMergeViewer) {
			TextMergeViewer textMergeViewer = (TextMergeViewer) contentViewer;
			try {
				Class<TextMergeViewer> clazz = TextMergeViewer.class;
				Field declaredField = clazz.getDeclaredField("fLeft");
				declaredField.setAccessible(true);
				final MergeSourceViewer fLeft = (MergeSourceViewer) declaredField.get(textMergeViewer);

				declaredField = clazz.getDeclaredField("fRight");
				declaredField.setAccessible(true);
				final MergeSourceViewer fRight = (MergeSourceViewer) declaredField.get(textMergeViewer);

				leftViewerListener = registerInputListener(fLeft, leftAnnotationModel);
				rightViewerListener = registerInputListener(fRight, rightAnnotationModel);
				//attachToViewer(textMergeViewer, fLeft, fRight);

				//configureSourceViewers(contentViewer, textMergeViewer, fLeft, fRight);
			} catch (Throwable t) {
				StatusHandler.log(new Status(IStatus.WARNING, ReviewsUiPlugin.PLUGIN_ID,
						"Could not initialize annotation model for " + Viewer.class.getName(), t)); //$NON-NLS-1$
			}
		}
	}

	public void setReviewItem(IFileItem item, ReviewBehavior behavior) {
		leftAnnotationModel.setItem(item.getTarget(), behavior);
		rightAnnotationModel.setItem(item.getBase(), behavior);
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

	private boolean isListenerFor(ReviewCompareInputListener listener, MergeSourceViewer viewer,
			ReviewAnnotationModel annotationModel) {
		if (listener == null) {
			return false;
		}
		return listener.isListenerFor(viewer, annotationModel);
	}

	private ReviewCompareInputListener registerInputListener(final MergeSourceViewer sourceViewer,
			final ReviewAnnotationModel annotationModel) {
		ReviewCompareInputListener listener = new ReviewCompareInputListener(sourceViewer, annotationModel);
		SourceViewer viewer = CompareUtil.getSourceViewer(sourceViewer);
		if (viewer != null) {
			viewer.addTextInputListener(listener);
		}
		listener.registerContextMenu();
		return listener;
	}

}
