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
 *     Tasktop Technologies - improvements
 *     Guy Perron 423242: Add ability to edit comment from compare navigator popup
 ******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.annotations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModelEvent;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelListener;
import org.eclipse.jface.text.source.IAnnotationModelListenerExtension;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.reviews.ui.ReviewsUiPlugin;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.ILineLocation;
import org.eclipse.mylyn.reviews.core.model.ILocation;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.mylyn.reviews.ui.ReviewBehavior;

/**
 * A model for review annotations.
 *
 * @author Shawn Minto
 * @author Steffen Pingel
 * @author Guy Perron
 */
public class ReviewAnnotationModel implements IAnnotationModel {

	private final Set<IAnnotationModelListener> annotationModelListeners = new HashSet<IAnnotationModelListener>(2);

	private final Set<Annotation> annotations = new LinkedHashSet<Annotation>();

	private ReviewBehavior behavior;

	private IDocument document;

	private final IDocumentListener documentListener = new IDocumentListener() {
		public void documentAboutToBeChanged(DocumentEvent event) {
		}

		public void documentChanged(DocumentEvent event) {
			// TODO consider hiding annotations if the document changes
			//updateAnnotations(false);
		}
	};

	private final EContentAdapter modelAdapter = new EContentAdapter() {
		@Override
		public void notifyChanged(Notification notification) {
			if (document == null) {
				return;
			}
			if (notification.getEventType() == Notification.ADD) {
				AnnotationModelEvent event = new AnnotationModelEvent(ReviewAnnotationModel.this);
				if (notification.getNewValue() instanceof IComment) {
					createCommentAnnotations(document, event, (IComment) notification.getNewValue());
				}
				fireModelChanged(event);
			}

			if (notification.getEventType() == Notification.REMOVE) {
				AnnotationModelEvent event = new AnnotationModelEvent(ReviewAnnotationModel.this);
				if (notification.getOldValue() instanceof IComment) {
					removeCommentAnnotations(document, event, (IComment) notification.getOldValue());
				}
				updateAnnotations();
			}

			if (notification.getEventType() == Notification.SET) {
				AnnotationModelEvent event = new AnnotationModelEvent(ReviewAnnotationModel.this);
				if (notification.getNewValue() instanceof IComment) {
					modifyCommentAnnotations(document, event, (IComment) notification.getOldValue(),
							(IComment) notification.getNewValue());
				}
				updateAnnotations();
			}
		}
	};

	private IReviewItem reviewItem;

	public ReviewAnnotationModel() {
	}

	public void addAnnotation(Annotation annotation, Position position) {
		// do nothing, we do not support external modification
	}

	public void addAnnotationModelListener(IAnnotationModelListener listener) {
		if (annotationModelListeners.add(listener)) {
			fireModelChanged(new AnnotationModelEvent(this, true));
		}
	}

	public void connect(final IDocument document) {
		this.document = document;
		connectItem();

		for (Annotation commentAnnotation : annotations) {
			try {
				if (commentAnnotation instanceof CommentAnnotation) {
					document.addPosition(((CommentAnnotation) commentAnnotation).getPosition());
				}
			} catch (BadLocationException e) {
				StatusHandler.log(new Status(IStatus.ERROR, ReviewsUiPlugin.PLUGIN_ID, e.getMessage(), e));
			}
		}
		document.addDocumentListener(documentListener);

		updateAnnotations();
	}

	public void disconnect(IDocument document) {
		for (Annotation commentAnnotation : annotations) {
			if (commentAnnotation instanceof CommentAnnotation) {
				document.removePosition(((CommentAnnotation) commentAnnotation).getPosition());
			}
		}
		document.removeDocumentListener(documentListener);

		disconnectItem();
		this.document = null;
	}

	public void disconnectItem() {
		if (reviewItem != null) {
			((EObject) reviewItem).eAdapters().remove(modelAdapter);
		}
		clear();
	}

	public Iterator<Annotation> getAnnotationIterator() {
		return annotations.iterator();
	}

	/**
	 * Returns the first annotation that this knows about for the given offset in the document
	 */
	public List<CommentAnnotation> getAnnotationsForOffset(int offset) {
		List<CommentAnnotation> result = new ArrayList<CommentAnnotation>();
//		for (CommentAnnotation annotation : Iterables.filter(this.annotations, CommentAnnotation.class)) {
		for (CommentAnnotation annotation : this.annotations.stream()
				.filter(CommentAnnotation.class::isInstance)
				.map(CommentAnnotation.class::cast)
				.collect(Collectors.toList())) {

			if (annotation.getPosition().offset <= offset
					&& (annotation.getPosition().length + annotation.getPosition().offset) >= offset) {
				result.add(annotation);
			}
		}
		return result;
	}

	public ReviewBehavior getBehavior() {
		return behavior;
	}

	public IDocument getDocument() {
		return document;
	}

	/**
	 * Returns the first annotation that this knows about for the given offset in the document
	 */
	public Annotation getFirstAnnotationForOffset(int offset) {
//		for (CommentAnnotation annotation : Iterables.filter(annotations, CommentAnnotation.class)) {
		for (CommentAnnotation annotation : this.annotations.stream()
				.filter(CommentAnnotation.class::isInstance)
				.map(CommentAnnotation.class::cast)
				.collect(Collectors.toList())) {

			if (annotation.getPosition().offset <= offset
					&& (annotation.getPosition().length + annotation.getPosition().offset) >= offset) {
				return annotation;
			}
		}
		return null;
	}

	public IReviewItem getItem() {
		return reviewItem;
	}

	public Position getPosition(Annotation annotation) {
		if (annotation instanceof CommentAnnotation) {
			return ((CommentAnnotation) annotation).getPosition();
		} else {
			// we dont understand any other annotations
			return null;
		}
	}

	public void removeAnnotation(Annotation annotation) {
		// do nothing, we do not support external modification
	}

	public void removeAnnotationModelListener(IAnnotationModelListener listener) {
		annotationModelListeners.remove(listener);
	}

	public void setItem(IReviewItem reviewItem, ReviewBehavior behavior) {
		disconnectItem();

		this.reviewItem = reviewItem;
		this.behavior = behavior;

		connectItem();
	}

	private void connectItem() {
		if (reviewItem != null) {
			((EObject) reviewItem).eAdapters().add(modelAdapter);
		}
	}

	private void createCommentAnnotations(IDocument document, AnnotationModelEvent event, IComment comment) {
		//TODO We need to ensure that this works properly with cases where 0 or many locations exist.
		for (ILocation location : comment.getLocations()) {
			if (location instanceof ILineLocation) {
				try {
					CommentAnnotation ca = createCommentAnnotation(document, comment, (ILineLocation) location);
					if (annotations.add(ca)) {
						event.annotationAdded(ca);
					}
				} catch (BadLocationException e) {
					StatusHandler.log(new Status(IStatus.ERROR, ReviewsUiPlugin.PLUGIN_ID, "Unable to add annotation.", //$NON-NLS-1$
							e));
				}
			}
		}
	}

	private void removeCommentAnnotations(IDocument document, AnnotationModelEvent event, IComment comment) {
		for (ILocation location : comment.getLocations()) {
			if (location instanceof ILineLocation) {
				try {
					CommentAnnotation ca = createCommentAnnotation(document, comment, (ILineLocation) location);
					annotations.remove(ca);
					reviewItem.getComments().remove(comment);
					event.annotationRemoved(ca);

				} catch (BadLocationException e) {
					StatusHandler.log(
							new Status(IStatus.ERROR, ReviewsUiPlugin.PLUGIN_ID, "Unable to remove annotation.", e)); //$NON-NLS-1$
				}
			}
		}
	}

	private void modifyCommentAnnotations(IDocument document, AnnotationModelEvent event, IComment oldcomment,
			IComment comment) {
		for (ILocation location : comment.getLocations()) {
			if (location instanceof ILineLocation) {
				try {
					CommentAnnotation oldCa = createCommentAnnotation(document, oldcomment, (ILineLocation) location);
					CommentAnnotation ca = new CommentAnnotation(oldCa.getPosition().offset, oldCa.getPosition().length,
							comment);
					annotations.remove(oldCa);
					annotations.add(ca);
					event.annotationRemoved(oldCa);
					event.annotationChanged(ca);

				} catch (BadLocationException e) {
					StatusHandler.log(
							new Status(IStatus.ERROR, ReviewsUiPlugin.PLUGIN_ID, "Unable to modify annotation.", e)); //$NON-NLS-1$
				}
			}
		}
	}

	private CommentAnnotation createCommentAnnotation(IDocument document, IComment comment, ILineLocation lineLocation)
			throws BadLocationException {
		int startLine = lineLocation.getRangeMin();
		int endLine = lineLocation.getRangeMax();
		int offset = 0;
		int length = 1;
		if (startLine != 0 && startLine <= document.getNumberOfLines()) {
			offset = document.getLineOffset(startLine - 1);
			if (endLine == 0) {
				endLine = startLine;
			}
			length = Math.max(document.getLineOffset(endLine - 1) - offset, 1);
		}
		return new CommentAnnotation(offset, length, comment);
	}

	protected void clear() {
		AnnotationModelEvent event = new AnnotationModelEvent(this);
		clear(event);
		fireModelChanged(event);
	}

	protected void clear(AnnotationModelEvent event) {
		for (Annotation commentAnnotation : annotations) {
			if (commentAnnotation instanceof CommentAnnotation) {
				event.annotationRemoved(commentAnnotation, ((CommentAnnotation) commentAnnotation).getPosition());
			}
		}
		annotations.clear();
	}

	protected void fireModelChanged(AnnotationModelEvent event) {
		event.markSealed();
		if (!event.isEmpty()) {
			for (IAnnotationModelListener listener : annotationModelListeners) {
				if (listener instanceof IAnnotationModelListenerExtension) {
					((IAnnotationModelListenerExtension) listener).modelChanged(event);
				} else {
					listener.modelChanged(this);
				}
			}
		}
	}

	protected void updateAnnotations() {
		AnnotationModelEvent event = new AnnotationModelEvent(this);
		clear(event);

		if (document != null && reviewItem != null) {
			for (IComment comment : reviewItem.getComments()) {
				createCommentAnnotations(document, event, comment);
			}
		}

		fireModelChanged(event);
	}

}
