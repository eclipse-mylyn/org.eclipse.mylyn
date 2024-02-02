/*******************************************************************************
 * Copyright (c) 2009, 2016 Atlassian and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Atlassian - initial API and implementation
 *     Tasktop Technologies - cleanup and support for gotoAnnotation
 *     Guy Perron 423242: Add ability to edit comment from compare navigator popup
 ******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.compare;

import static org.eclipse.mylyn.internal.reviews.ui.compare.ReviewCompareAnnotationSupport.Side.LEFT_SIDE;
import static org.eclipse.mylyn.internal.reviews.ui.compare.ReviewCompareAnnotationSupport.Side.RIGHT_SIDE;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import org.eclipse.compare.contentmergeviewer.TextMergeViewer;
import org.eclipse.compare.internal.MergeSourceViewer;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.LineRange;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.projection.AnnotationBag;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.reviews.ui.ReviewsUiPlugin;
import org.eclipse.mylyn.internal.reviews.ui.annotations.CommentAnnotation;
import org.eclipse.mylyn.internal.reviews.ui.annotations.CommentAnnotationHoverInput;
import org.eclipse.mylyn.internal.reviews.ui.annotations.CommentPopupDialog;
import org.eclipse.mylyn.internal.reviews.ui.annotations.ReviewAnnotationModel;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.mylyn.reviews.ui.ReviewBehavior;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.texteditor.AbstractTextEditor;

/**
 * Manages annotation models for compare viewers.
 *
 * @author Thomas Ehrnhoefer
 * @author Steffen Pingel
 * @author Guy Perron
 */
@SuppressWarnings("restriction")
public class ReviewCompareAnnotationSupport {
	public enum Side {
		LEFT_SIDE, RIGHT_SIDE
	}

	private static String KEY_ANNOTAION_SUPPORT = ReviewItemSetCompareEditorInput.class.getName();

	private CommentPopupDialog commentPopupDialog = null;

	public static ReviewCompareAnnotationSupport getAnnotationSupport(Viewer contentViewer) {
		ReviewCompareAnnotationSupport support = (ReviewCompareAnnotationSupport) contentViewer
				.getData(KEY_ANNOTAION_SUPPORT);
		if (support == null) {
			support = new ReviewCompareAnnotationSupport(contentViewer);
			contentViewer.setData(KEY_ANNOTAION_SUPPORT, support);
		}
		return support;
	}

	public class MonitorObject {
	}

	MonitorObject myMonitorObject = new MonitorObject();

	private ReviewBehavior behavior;

	private final ReviewAnnotationModel leftAnnotationModel;

	private ReviewCompareInputListener leftViewerListener;

	private final ReviewAnnotationModel rightAnnotationModel;

	private ReviewCompareInputListener rightViewerListener;

	private MergeSourceViewer leftSourceViewer;

	private MergeSourceViewer rightSourceViewer;

	public ReviewCompareAnnotationSupport(Viewer contentViewer) {
		leftAnnotationModel = new ReviewAnnotationModel();
		rightAnnotationModel = new ReviewAnnotationModel();
		install(contentViewer);
		contentViewer.setData(KEY_ANNOTAION_SUPPORT, this);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		ReviewCompareAnnotationSupport other = (ReviewCompareAnnotationSupport) obj;
		if (!Objects.equals(leftAnnotationModel, other.leftAnnotationModel)) {
			return false;
		}
		if (!Objects.equals(rightAnnotationModel, other.rightAnnotationModel)) {
			return false;
		}
		return true;
	}

	public ReviewBehavior getBehavior() {
		return behavior;
	}

	@Override
	public int hashCode() {
		return Objects.hash(leftAnnotationModel, rightAnnotationModel);
	}

	public void install(Viewer contentViewer) {
		// FIXME: hack
		if (contentViewer instanceof TextMergeViewer textMergeViewer) {
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

	public boolean hasAnnotation(Direction direction) {
		Position rightPosition = new Position(0, 0);
		return findAnnotation(rightSourceViewer, direction, rightPosition, rightAnnotationModel) != null;
	}

	/**
	 * Jumps to the next annotation according to the given direction.
	 *
	 * @param direction
	 *            the search direction
	 * @return the selected annotation or <code>null</code> if none
	 */
	public Annotation gotoAnnotation(Direction direction) {
		if (leftSourceViewer == null) {
			return null;
		}
		int currentLeftOffset = getSelection(leftSourceViewer).getOffset();

		Position nextLeftPosition = new Position(0, 0);
		Annotation leftAnnotation = findAnnotation(leftSourceViewer, direction, nextLeftPosition, leftAnnotationModel);
		Position nextRightPosition = new Position(0, 0);
		Annotation rightAnnotation = findAnnotation(rightSourceViewer, direction, nextRightPosition,
				rightAnnotationModel);
		if (leftAnnotation == null && rightAnnotation != null) {
			selectAndReveal(rightSourceViewer, nextRightPosition);
			return rightAnnotation;
		} else if (leftAnnotation != null && rightAnnotation == null) {
			selectAndReveal(leftSourceViewer, nextLeftPosition);
			return leftAnnotation;
		} else if (leftAnnotation != null && rightAnnotation != null) {
			nextLeftPosition.offset = getLineOffset(leftAnnotationModel, nextLeftPosition.offset);
			nextLeftPosition.length = 1;
			nextRightPosition.offset = getLineOffset(rightAnnotationModel, nextRightPosition.offset);
			nextRightPosition.length = 1;
			currentLeftOffset = getLineOffset(leftAnnotationModel, currentLeftOffset);

			if (calculateNextAnnotation(direction, nextLeftPosition, nextRightPosition,
					currentLeftOffset) == LEFT_SIDE) {
				return leftAnnotation;
			} else {
				return rightAnnotation;
			}
		}
		return null;
	}

	public void gotoAnnotationWithComment(IComment comment) {
		CommentAnnotation rightAnnotation = findComment(rightAnnotationModel, comment);
		if (rightAnnotation != null) {
			selectAndReveal(rightSourceViewer, rightAnnotation.getPosition());
		} else {
			CommentAnnotation leftAnnotation = findComment(leftAnnotationModel, comment);
			if (leftAnnotation != null) {
				selectAndReveal(leftSourceViewer, leftAnnotation.getPosition());
			}
		}
	}

	private CommentAnnotation findComment(ReviewAnnotationModel annotationModel, final IComment comment) {
		Optional<Annotation> annotation = StreamSupport
				.stream(Spliterators.spliteratorUnknownSize(annotationModel.getAnnotationIterator(),
						Spliterator.ORDERED), false)
				.filter(CommentAnnotation.class::isInstance)
				.filter(c -> ((CommentAnnotation) c).getComment().getId().equals(comment.getId()))
				.findFirst();

		if (annotation.isPresent()) {
			return (CommentAnnotation) annotation.get();
		}
		return null;
	}

	private int getLineOffset(ReviewAnnotationModel annotationModel, int offset) {
		try {
			int line = annotationModel.getDocument().getLineOfOffset(offset);
			return annotationModel.getDocument().getLineOffset(line);
		} catch (BadLocationException e) {
			StatusHandler.log(new Status(IStatus.ERROR, ReviewsUiPlugin.PLUGIN_ID, "Error displaying comment", e)); //$NON-NLS-1$
		}
		return 0;
	}

	public Side calculateNextAnnotation(Direction direction, Position nextLeftPosition, Position nextRightPosition,
			Integer currentLeftOffset) {
		if (direction == Direction.FORWARDS) {
			if (nextLeftPosition.offset == nextRightPosition.offset) {
				moveToAnnotation(rightSourceViewer, leftSourceViewer, nextLeftPosition);
				rightSourceViewer.getSourceViewer()
						.revealRange(nextLeftPosition.offset - 1, nextLeftPosition.length - 1);
				rightSourceViewer.getSourceViewer()
						.setSelectedRange(nextLeftPosition.offset - 1, nextLeftPosition.length - 1);
				return LEFT_SIDE;
			} else if (nextLeftPosition.offset < currentLeftOffset && nextRightPosition.offset < currentLeftOffset
					|| nextLeftPosition.offset > currentLeftOffset && nextRightPosition.offset > currentLeftOffset) {
				if (nextLeftPosition.offset < nextRightPosition.offset) {
					return moveToLeftAnnotation(nextLeftPosition);
				} else {
					return moveToRightAnnotation(nextRightPosition);
				}
			} else if (nextLeftPosition.offset < currentLeftOffset && nextRightPosition.offset > currentLeftOffset) {
				return moveToRightAnnotation(nextRightPosition);
			} else if (nextLeftPosition.offset > currentLeftOffset && nextRightPosition.offset < currentLeftOffset) {
				return moveToLeftAnnotation(nextLeftPosition);
			} else if (nextRightPosition.offset == currentLeftOffset) {
				return moveToLeftAnnotation(nextLeftPosition);
			} else {
				return moveToRightAnnotation(nextRightPosition);
			}

		} else if (nextLeftPosition.offset == nextRightPosition.offset) {
			moveToAnnotation(leftSourceViewer, rightSourceViewer, nextRightPosition);
			Position position = getNextLine(nextRightPosition.offset);
			leftSourceViewer.getSourceViewer().revealRange(position.offset, position.length);
			leftSourceViewer.getSourceViewer().setSelectedRange(position.offset, position.length);
			return RIGHT_SIDE;
		} else if (nextLeftPosition.offset > currentLeftOffset && nextRightPosition.offset > currentLeftOffset
				|| nextLeftPosition.offset < currentLeftOffset && nextRightPosition.offset < currentLeftOffset) {
			if (nextLeftPosition.offset > nextRightPosition.offset) {
				return moveToLeftAnnotation(nextLeftPosition);
			} else {
				return moveToRightAnnotation(nextRightPosition);
			}
		} else if (nextLeftPosition.offset > currentLeftOffset && nextRightPosition.offset < currentLeftOffset) {
			return moveToRightAnnotation(nextRightPosition);
		} else if (nextLeftPosition.offset < currentLeftOffset && nextRightPosition.offset > currentLeftOffset) {
			return moveToLeftAnnotation(nextLeftPosition);
		} else if (nextRightPosition.offset == currentLeftOffset) {
			return moveToLeftAnnotation(nextLeftPosition);
		} else {
			return moveToRightAnnotation(nextRightPosition);
		}
	}

	private Position getNextLine(int offset) {
		Position position = new Position(0, 0);
		try {
			int line = rightAnnotationModel.getDocument().getLineOfOffset(offset);
			IRegion region = rightAnnotationModel.getDocument().getLineInformation(line + 1);
			position.offset = region.getOffset();
			position.length = region.getLength();
		} catch (BadLocationException e) {
			StatusHandler.log(new Status(IStatus.ERROR, ReviewsUiPlugin.PLUGIN_ID, "Error displaying comment", e)); //$NON-NLS-1$
		}
		return position;
	}

	private Side moveToLeftAnnotation(Position nextLeftPosition) {
		moveToAnnotation(rightSourceViewer, leftSourceViewer, nextLeftPosition);
		return LEFT_SIDE;
	}

	private Side moveToRightAnnotation(Position nextRightPosition) {
		moveToAnnotation(leftSourceViewer, rightSourceViewer, nextRightPosition);
		return RIGHT_SIDE;
	}

	public void moveToAnnotation(MergeSourceViewer adjacentViewer, MergeSourceViewer annotationViewer,
			Position position) {
		adjacentViewer.getSourceViewer().revealRange(position.offset, position.length);
		adjacentViewer.getSourceViewer().setSelectedRange(position.offset, position.length);
		selectAndReveal(annotationViewer, position);
	}

	// adapted from {@link AbstractTextEditor#selectAndReveal(int, int)}
	private void selectAndReveal(MergeSourceViewer sourceViewer, Position position) {
		StyledText widget = sourceViewer.getSourceViewer().getTextWidget();
		widget.setRedraw(false);
		adjustHighlightRange(sourceViewer.getSourceViewer(), position.offset, position.length);
		sourceViewer.getSourceViewer().revealRange(position.offset, position.length);
		sourceViewer.getSourceViewer().setSelectedRange(position.offset, position.length);
		SourceViewer srcViewer = sourceViewer.getSourceViewer();

		IReviewItem reviewitem = ((ReviewAnnotationModel) srcViewer.getAnnotationModel()).getItem();

		List<CommentAnnotation> comments = getAnnotationsForLine(srcViewer, position.offset);

		Point p = sourceViewer.getLineRange(position, sourceViewer.getSourceViewer().getSelectedRange());
		LineRange range = new LineRange(p.x + 1, p.y);

		if (commentPopupDialog != null) {
			commentPopupDialog.dispose(false);
			commentPopupDialog = null;
		}
		commentPopupDialog = new CommentPopupDialog(
				ReviewsUiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(),
				SWT.NO_FOCUS | SWT.ON_TOP, reviewitem, range, true);
		CommentAnnotationHoverInput input = new CommentAnnotationHoverInput(comments,
				((ReviewAnnotationModel) srcViewer.getAnnotationModel()).getBehavior());
		commentPopupDialog.create();
		commentPopupDialog.setInput(input);

		commentPopupDialog.setHeightBasedOnMouse(sourceViewer.getSourceViewer().getControl().toDisplay(0, 0).y);

		Point location = sourceViewer.getSourceViewer()
				.getControl()
				.toDisplay(sourceViewer.getSourceViewer().getControl().getSize().x,
						sourceViewer.getViewportHeight() / 3);
		commentPopupDialog.setLocation(location);
		commentPopupDialog.open();
		commentPopupDialog.setFocus();

		widget.setRedraw(true);
	}

	private List<CommentAnnotation> getAnnotationsForLine(SourceViewer viewer, int offset) {
		IAnnotationModel model = viewer.getAnnotationModel();
		if (model == null) {
			return Collections.emptyList();
		}

		IDocument document = viewer.getDocument();
		int line = 0;
		try {
			line = document.getLineOfOffset(offset);
		} catch (BadLocationException e1) {
			StatusHandler.log(new Status(IStatus.ERROR, ReviewsUiPlugin.PLUGIN_ID, "Error fetching line", e1)); //$NON-NLS-1$
		}

		List<CommentAnnotation> commentAnnotations = new ArrayList<>();

		for (Iterator<Annotation> it = model.getAnnotationIterator(); it.hasNext();) {
			Annotation annotation = it.next();
			Position position = model.getPosition(annotation);
			if (position == null || !isPositionOnLine(position, line, document)) {
				continue;
			}
			if (annotation instanceof AnnotationBag bag) {
				Iterator<Annotation> e = bag.iterator();
				while (e.hasNext()) {
					annotation = e.next();
					position = model.getPosition(annotation);
					if (position != null && includeAnnotation(annotation, position, commentAnnotations)) {
						commentAnnotations.add((CommentAnnotation) annotation);
					}
				}
			} else if (includeAnnotation(annotation, position, commentAnnotations)) {
				commentAnnotations.add((CommentAnnotation) annotation);
			}
		}

		return commentAnnotations;
	}

	private boolean includeAnnotation(Annotation annotation, Position position, List<CommentAnnotation> annotations) {
		return annotation instanceof CommentAnnotation && !annotations.contains(annotation);
	}

	private boolean isPositionOnLine(Position position, int line, IDocument document) {
		if (position.getOffset() > -1 && position.getLength() > -1) {
			try {
				return line == document.getLineOfOffset(position.getOffset());
			} catch (BadLocationException x) {
				// ignore
			}
		}
		return false;
	}

// adapted from {@link AbstractTextEditor#selectAndReveal(int, int)}
	protected void adjustHighlightRange(SourceViewer sourceViewer, int offset, int length) {
		if (sourceViewer instanceof ITextViewerExtension5 extension) {
			extension.exposeModelRange(new Region(offset, length));
		} else if (!isVisible(sourceViewer, offset, length)) {
			sourceViewer.resetVisibleRegion();
		}
	}

// adapted from {@link AbstractTextEditor#selectAndReveal(int, int)}
	private boolean isVisible(SourceViewer viewer, int offset, int length) {
		if (viewer instanceof ITextViewerExtension5 extension) {
			IRegion overlap = extension.modelRange2WidgetRange(new Region(offset, length));
			return overlap != null;
		}
		return viewer.overlapsWithVisibleRegion(offset, length);
	}

	public void setReviewItem(IFileItem item, ReviewBehavior behavior) {
		leftAnnotationModel.setItem(item.getBase(), behavior);
		rightAnnotationModel.setItem(item.getTarget(), behavior);
		Display.getDefault().asyncExec(() -> {
			try {
				// if listeners exist, just make sure the hover hack is in there
				if (leftViewerListener != null) {
					leftViewerListener.forceCustomAnnotationHover();
				}
				if (rightViewerListener != null) {
					rightViewerListener.forceCustomAnnotationHover();
				}
			} catch (Exception e) {
				StatusHandler.log(new Status(IStatus.ERROR, ReviewsUiPlugin.PLUGIN_ID,
						"Error attaching annotation hover", e)); //$NON-NLS-1$
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
	 * Returns the annotation closest to the given range respecting the given direction. If an annotation is found, the annotations current
	 * position is copied into the provided annotation position.
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
	@SuppressWarnings("null")
	protected Annotation findAnnotation(MergeSourceViewer viewer, Direction direction, Position annotationPosition,
			ReviewAnnotationModel annotationModel) {
		if (viewer == null) {
			return null;
		}
		ITextSelection selection = getSelection(viewer);
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

		Iterator<Annotation> e = annotationModel.getAnnotationIterator();
		while (e.hasNext()) {
			Annotation a = e.next();

			Position p = a instanceof CommentAnnotation ? ((CommentAnnotation) a).getPosition() : null;
			if (p == null) {
				continue;
			}

			if (direction == Direction.FORWARDS && p.offset == offset
					|| direction == Direction.BACKWARDS && p.offset + p.getLength() == offset + length) {// || p.includes(offset)) {
				if (containingAnnotation == null
						|| direction == Direction.FORWARDS && p.length >= containingAnnotationPosition.length
						|| direction == Direction.BACKWARDS && p.length >= containingAnnotationPosition.length) {
					containingAnnotation = a;
					containingAnnotationPosition = p;
					currentAnnotation = p.length == length;
				}
			} else {
				int currentDistance = 0;

				if (direction == Direction.FORWARDS) {
					currentDistance = p.getOffset() - offset;
					if (currentDistance < 0) {
						currentDistance = endOfDocument + currentDistance;
					}

					if (currentDistance < distance
							|| currentDistance == distance && p.length < nextAnnotationPosition.length) {
						distance = currentDistance;
						nextAnnotation = a;
						nextAnnotationPosition = p;
					}
				} else {
					currentDistance = offset + length - (p.getOffset() + p.length);
					if (currentDistance < 0) {
						currentDistance = endOfDocument + currentDistance;
					}

					if (currentDistance < distance
							|| currentDistance == distance && p.length < nextAnnotationPosition.length) {
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

	private ITextSelection getSelection(MergeSourceViewer viewer) {
		return (ITextSelection) viewer.getSourceViewer().getSelectionProvider().getSelection();
	}

}
