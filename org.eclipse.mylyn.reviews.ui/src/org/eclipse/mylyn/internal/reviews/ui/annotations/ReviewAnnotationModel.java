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
 ******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.annotations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import org.eclipse.mylyn.reviews.core.model.ILineLocation;
import org.eclipse.mylyn.reviews.core.model.ILocation;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.mylyn.reviews.core.model.ITopic;
import org.eclipse.mylyn.reviews.ui.ReviewBehavior;

/**
 * A model for review annotations.
 * 
 * @author Shawn Minto
 * @author Steffen Pingel
 */
public class ReviewAnnotationModel implements IAnnotationModel {

	private final Set<IAnnotationModelListener> annotationModelListeners = new HashSet<IAnnotationModelListener>(2);

	private final List<CommentAnnotation> annotations = new ArrayList<CommentAnnotation>();

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
				if (notification.getNewValue() instanceof ITopic) {
					createCommentAnnotations(document, event, (ITopic) notification.getNewValue());
				}
				fireModelChanged(event);
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

		for (CommentAnnotation commentAnnotation : annotations) {
			try {
				document.addPosition(commentAnnotation.getPosition());
			} catch (BadLocationException e) {
				StatusHandler.log(new Status(IStatus.ERROR, ReviewsUiPlugin.PLUGIN_ID, e.getMessage(), e));
			}
		}
		document.addDocumentListener(documentListener);

		updateAnnotations();
	}

	public void disconnect(IDocument document) {
		for (CommentAnnotation commentAnnotation : annotations) {
			document.removePosition(commentAnnotation.getPosition());
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

	public Iterator<CommentAnnotation> getAnnotationIterator() {
		return annotations.iterator();
	}

	/**
	 * Returns the first annotation that this knows about for the given offset in the document
	 */
	public List<CommentAnnotation> getAnnotationsForOffset(int offset) {
		List<CommentAnnotation> result = new ArrayList<CommentAnnotation>();
		for (CommentAnnotation annotation : this.annotations) {
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

	/**
	 * Returns the first annotation that this knows about for the given offset in the document
	 */
	public CommentAnnotation getFirstAnnotationForOffset(int offset) {
		for (CommentAnnotation annotation : annotations) {
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

	private void createCommentAnnotations(IDocument document, AnnotationModelEvent event, ITopic topic) {
		int startLine = 0;
		int endLine = 0;
		//TODO We need to ensure that this works properly with cases where 0 or many locations exist.
		for (ILocation location : topic.getLocations()) {
			if (location instanceof ILineLocation) {
				ILineLocation lineLocation = (ILineLocation) location;
				try {
					startLine = lineLocation.getRangeMin();
					endLine = lineLocation.getRangeMax();

					int offset = 0;
					int length = 0;
					if (startLine != 0) {
						offset = document.getLineOffset(startLine - 1);
						if (endLine == 0) {
							endLine = startLine;
						}
						length = Math.max(document.getLineOffset(endLine - 1) - offset, 0);

					}
					length = Math.max(1, length);
					CommentAnnotation ca = new CommentAnnotation(offset, length, topic);
					annotations.add(ca);
					event.annotationAdded(ca);
				} catch (BadLocationException e) {
					StatusHandler.log(new Status(IStatus.ERROR, ReviewsUiPlugin.PLUGIN_ID, "Unable to add annotation.",
							e));
				}
			}
		}
	}

	protected void clear() {
		AnnotationModelEvent event = new AnnotationModelEvent(this);
		clear(event);
		fireModelChanged(event);
	}

	protected void clear(AnnotationModelEvent event) {
		for (CommentAnnotation commentAnnotation : annotations) {
			event.annotationRemoved(commentAnnotation, commentAnnotation.getPosition());
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
			for (ITopic comment : reviewItem.getTopics()) {
				createCommentAnnotations(document, event, comment);
			}
		}

		fireModelChanged(event);
	}

}
