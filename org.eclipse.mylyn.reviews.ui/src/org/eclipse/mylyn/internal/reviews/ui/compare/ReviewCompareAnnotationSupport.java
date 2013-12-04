/*******************************************************************************
 * Copyright (c) 2009 Atlassian and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Atlassian - initial API and implementation
 *     Tasktop Technologies - cleanup and support for gotoAnnotation
 ******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.compare;

import java.lang.reflect.Field;
import java.util.Iterator;

import org.eclipse.compare.contentmergeviewer.TextMergeViewer;
import org.eclipse.compare.internal.MergeSourceViewer;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.reviews.ui.ReviewsUiPlugin;
import org.eclipse.mylyn.internal.reviews.ui.annotations.CommentAnnotation;
import org.eclipse.mylyn.internal.reviews.ui.annotations.ReviewAnnotationModel;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.ui.ReviewBehavior;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.texteditor.AbstractTextEditor;

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

	private MergeSourceViewer leftSourceViewer;

	private MergeSourceViewer rightSourceViewer;

	public ReviewCompareAnnotationSupport(Viewer contentViewer) {
		this.leftAnnotationModel = new ReviewAnnotationModel();
		this.rightAnnotationModel = new ReviewAnnotationModel();
		install(contentViewer);
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
				Field declaredField = clazz.getDeclaredField("fLeft"); //$NON-NLS-1$
				declaredField.setAccessible(true);
				leftSourceViewer = (MergeSourceViewer) declaredField.get(textMergeViewer);

				declaredField = clazz.getDeclaredField("fRight"); //$NON-NLS-1$
				declaredField.setAccessible(true);
				rightSourceViewer = (MergeSourceViewer) declaredField.get(textMergeViewer);

				leftViewerListener = registerInputListener(leftSourceViewer, leftAnnotationModel);
				rightViewerListener = registerInputListener(rightSourceViewer, rightAnnotationModel);
			} catch (Throwable t) {
				StatusHandler.log(new Status(IStatus.WARNING, ReviewsUiPlugin.PLUGIN_ID,
						"Could not initialize annotation model for " + Viewer.class.getName(), t)); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Jumps to the next annotation according to the given direction.
	 * 
	 * @param direction
	 *            the search direction
	 * @return the selected annotation or <code>null</code> if none
	 */
	public Annotation gotoAnnotation(Direction direction) {
		Position leftPosition = new Position(0, 0);
		Annotation leftAnnotation = findAnnotation(leftSourceViewer, direction, leftPosition, leftAnnotationModel);
		Position rightPosition = new Position(0, 0);
		Annotation rightAnnotation = findAnnotation(rightSourceViewer, direction, rightPosition, rightAnnotationModel);
		if (leftAnnotation == null && rightAnnotation != null) {
			selectAndReveal(rightSourceViewer, rightPosition);
			return rightAnnotation;
		} else if (leftAnnotation != null && rightAnnotation == null) {
			selectAndReveal(leftSourceViewer, leftPosition);
			return leftAnnotation;
		} else if (leftAnnotation != null && rightAnnotation != null) {
			if ((direction.isForwards() && leftPosition.offset <= rightPosition.offset)
					|| (direction.isBackwards() && leftPosition.offset >= rightPosition.offset)) {
				selectAndReveal(leftSourceViewer, leftPosition);
				return leftAnnotation;
			} else {
				selectAndReveal(rightSourceViewer, rightPosition);
				return rightAnnotation;
			}
		}
		return null;
	}

	// adapted from {@link AbstractTextEditor#selectAndReveal(int, int)}
	private void selectAndReveal(MergeSourceViewer sourceViewer, Position position) {
		StyledText widget = sourceViewer.getSourceViewer().getTextWidget();
		widget.setRedraw(false);
		{
			adjustHighlightRange(sourceViewer.getSourceViewer(), position.offset, position.length);
			sourceViewer.getSourceViewer().revealRange(position.offset, position.length);
			sourceViewer.getSourceViewer().setSelectedRange(position.offset, position.length);
		}
		widget.setRedraw(true);
	}

	// adapted from {@link AbstractTextEditor#selectAndReveal(int, int)}
	protected void adjustHighlightRange(SourceViewer sourceViewer, int offset, int length) {
		if (sourceViewer instanceof ITextViewerExtension5) {
			ITextViewerExtension5 extension = (ITextViewerExtension5) sourceViewer;
			extension.exposeModelRange(new Region(offset, length));
		} else if (!isVisible(sourceViewer, offset, length)) {
			sourceViewer.resetVisibleRegion();
		}
	}

	// adapted from {@link AbstractTextEditor#selectAndReveal(int, int)}
	private boolean isVisible(SourceViewer viewer, int offset, int length) {
		if (viewer instanceof ITextViewerExtension5) {
			ITextViewerExtension5 extension = (ITextViewerExtension5) viewer;
			IRegion overlap = extension.modelRange2WidgetRange(new Region(offset, length));
			return overlap != null;
		}
		return viewer.overlapsWithVisibleRegion(offset, length);
	}

	public void setReviewItem(IFileItem item, ReviewBehavior behavior) {
		leftAnnotationModel.setItem(item.getBase(), behavior);
		rightAnnotationModel.setItem(item.getTarget(), behavior);
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					// if listeners exist, just make sure the hover hack is in there
					leftViewerListener.forceCustomAnnotationHover();
					rightViewerListener.forceCustomAnnotationHover();
				} catch (Exception e) {
					StatusHandler.log(new Status(IStatus.ERROR, ReviewsUiPlugin.PLUGIN_ID,
							"Error attaching annotation hover", e));
				}
			}
		});
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

	/**
	 * Returns the annotation closest to the given range respecting the given direction. If an annotation is found, the
	 * annotations current position is copied into the provided annotation position.
	 * 
	 * @param viewer
	 *            the viewer
	 * @param direction
	 *            the search direction
	 * @param annotationPosition
	 *            the position of the found annotation
	 * @param annotationModel
	 *            the annotation model to process
	 * @return the found annotation
	 * @see borrowed and adapted from {@link AbstractTextEditor}
	 */
	protected Annotation findAnnotation(MergeSourceViewer viewer, Direction direction, Position annotationPosition,
			ReviewAnnotationModel annotationModel) {

		ITextSelection selection = (ITextSelection) viewer.getSourceViewer().getSelectionProvider().getSelection();
		final int offset = selection.getOffset();
		final int length = selection.getLength();

		Annotation nextAnnotation = null;
		Position nextAnnotationPosition = null;
		Annotation containingAnnotation = null;
		Position containingAnnotationPosition = null;
		boolean currentAnnotation = false;

		IDocument document = annotationModel.getDocument();
		if (document == null) {
			return null;
		}

		int endOfDocument = document.getLength();
		int distance = Integer.MAX_VALUE;

		Iterator<CommentAnnotation> e = annotationModel.getAnnotationIterator();
		while (e.hasNext()) {
			CommentAnnotation a = e.next();

			Position p = a.getPosition();
			if (p == null) {
				continue;
			}

			if (direction.isForwards() && p.offset == offset || direction.isBackwards()
					&& p.offset + p.getLength() == offset + length) {// || p.includes(offset)) {
				if (containingAnnotation == null
						|| (direction.isForwards() && p.length >= containingAnnotationPosition.length || direction.isBackwards()
								&& p.length >= containingAnnotationPosition.length)) {
					containingAnnotation = a;
					containingAnnotationPosition = p;
					currentAnnotation = p.length == length;
				}
			} else {
				int currentDistance = 0;

				if (direction.isForwards()) {
					currentDistance = p.getOffset() - offset;
					if (currentDistance < 0) {
						currentDistance = endOfDocument + currentDistance;
					}

					if (currentDistance < distance || currentDistance == distance
							&& p.length < nextAnnotationPosition.length) {
						distance = currentDistance;
						nextAnnotation = a;
						nextAnnotationPosition = p;
					}
				} else {
					currentDistance = offset + length - (p.getOffset() + p.length);
					if (currentDistance < 0) {
						currentDistance = endOfDocument + currentDistance;
					}

					if (currentDistance < distance || currentDistance == distance
							&& p.length < nextAnnotationPosition.length) {
						distance = currentDistance;
						nextAnnotation = a;
						nextAnnotationPosition = p;
					}
				}
			}
		}
		if (containingAnnotationPosition != null && (!currentAnnotation || nextAnnotation == null)) {
			annotationPosition.setOffset(containingAnnotationPosition.getOffset());
			annotationPosition.setLength(containingAnnotationPosition.getLength());
			return containingAnnotation;
		}
		if (nextAnnotationPosition != null) {
			annotationPosition.setOffset(nextAnnotationPosition.getOffset());
			annotationPosition.setLength(nextAnnotationPosition.getLength());
		}

		return nextAnnotation;
	}

}
