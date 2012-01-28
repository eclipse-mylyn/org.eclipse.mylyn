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
import org.eclipse.mylyn.reviews.core.model.ILineRange;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.mylyn.reviews.core.model.ITopic;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * The model for the annotations
 * 
 * @author Shawn Minto
 * @author Steffen Pingel
 */
public class ReviewAnnotationModel implements IAnnotationModel, IReviewAnnotationModel {

	private final Set<CommentAnnotation> annotations = new HashSet<CommentAnnotation>(32);

	private final Set<IAnnotationModelListener> annotationModelListeners = new HashSet<IAnnotationModelListener>(2);

	private final ITextEditor textEditor;

	private final IEditorInput editorInput;

	private IDocument editorDocument;

	private IReviewItem reviewItem;

	private boolean annotated = false;

	private final IDocumentListener documentListener = new IDocumentListener() {
		public void documentChanged(DocumentEvent event) {
			updateAnnotations(false);
		}

		public void documentAboutToBeChanged(DocumentEvent event) {
		}
	};

	private EContentAdapter modelAdapter;

	public ReviewAnnotationModel(ITextEditor editor, IEditorInput editorInput, IDocument document,
			IReviewItem reviewItem) {
		this.textEditor = editor;
		this.editorInput = editorInput;
		this.editorDocument = document;
		this.reviewItem = reviewItem;
		updateAnnotations(true);
	}

	protected void updateAnnotations(boolean force) {

		boolean annotate = false;

		// TODO make sure that the local files is in sync otherwise remove the annotations

		if (textEditor == null && editorInput == null && editorDocument != null) {
			annotate = true;
		} else {
			if (editorDocument == null) {
				annotate = false;
			} else if (!textEditor.isDirty() && editorInput != null && reviewItem != null) {
				annotate = true;
			} else {
				annotate = false;
			}
		}

		if (annotate) {
			if (!annotated || force) {
				createAnnotations();
				annotated = true;
			}
		} else {
			if (annotated) {
				clear();
				annotated = false;
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

	protected void createAnnotations() {
		AnnotationModelEvent event = new AnnotationModelEvent(this);
		clear(event);

		for (ITopic comment : reviewItem.getTopics()) {
			createCommentAnnotation(event, comment);
		}

		fireModelChanged(event);
	}

	private void createCommentAnnotation(AnnotationModelEvent event, ITopic comment) {
		try {

			int startLine = 0;
			int endLine = 0;

			ILineLocation location = (ILineLocation) comment.getLocation();
			final List<ILineRange> lineRanges = location.getRanges();
			startLine = location.getTotalMin();
			endLine = location.getTotalMax();

			int offset = 0;
			int length = 0;
			if (startLine != 0) {
				offset = editorDocument.getLineOffset(startLine - 1);
				if (endLine == 0) {
					endLine = startLine;
				}
				length = Math.max(editorDocument.getLineOffset(endLine - 1) - offset, 0);

			}
			length = Math.max(1, length);
			CommentAnnotation ca = new CommentAnnotation(offset, length, comment);
			annotations.add(ca);
			event.annotationAdded(ca);

		} catch (BadLocationException e) {
			StatusHandler.log(new Status(IStatus.ERROR, ReviewsUiPlugin.PLUGIN_ID, "Unable to add annotation.", e));
		}
	}

	public void addAnnotationModelListener(IAnnotationModelListener listener) {
		if (!annotationModelListeners.contains(listener)) {
			annotationModelListeners.add(listener);
			fireModelChanged(new AnnotationModelEvent(this, true));
		}
	}

	public void removeAnnotationModelListener(IAnnotationModelListener listener) {
		annotationModelListeners.remove(listener);
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

	public void connect(IDocument document) {
		for (CommentAnnotation commentAnnotation : annotations) {
			try {
				document.addPosition(commentAnnotation.getPosition());
			} catch (BadLocationException e) {
				StatusHandler.log(new Status(IStatus.ERROR, ReviewsUiPlugin.PLUGIN_ID, e.getMessage(), e));
			}
		}
		document.addDocumentListener(documentListener);

		modelAdapter = new EContentAdapter() {
			@Override
			public void notifyChanged(Notification notification) {
				if (notification.getEventType() == Notification.ADD) {
					AnnotationModelEvent event = new AnnotationModelEvent(ReviewAnnotationModel.this);
					if (notification.getNewValue() instanceof ITopic) {
						createCommentAnnotation(event, (ITopic) notification.getNewValue());
					}
					fireModelChanged(event);
				}
			}
		};
		((EObject) reviewItem).eAdapters().add(modelAdapter);
	}

	public void disconnect(IDocument document) {
		((EObject) reviewItem).eAdapters().remove(modelAdapter);

		for (CommentAnnotation commentAnnotation : annotations) {
			document.removePosition(commentAnnotation.getPosition());
		}
		document.removeDocumentListener(documentListener);
	}

	public void addAnnotation(Annotation annotation, Position position) {
		// do nothing, we do not support external modification
	}

	public void removeAnnotation(Annotation annotation) {
		// do nothing, we do not support external modification
	}

	public Iterator<CommentAnnotation> getAnnotationIterator() {
		return annotations.iterator();
	}

	public Position getPosition(Annotation annotation) {
		if (annotation instanceof CommentAnnotation) {
			return ((CommentAnnotation) annotation).getPosition();
		} else {
			// we dont understand any other annotations
			return null;
		}
	}

	public void update(IReviewItem reviewItem) {
		this.reviewItem = reviewItem;
		updateAnnotations(true);
	}

	public IReviewItem getItem() {
		return reviewItem;
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

	public void setEditorDocument(IDocument editorDocument) {
		this.editorDocument = editorDocument;
		updateAnnotations(true);
	}

	// FIXME
//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime
//				* result
//				+ ((crucibleFile.getCrucibleFileInfo().getFileDescriptor().getAbsoluteUrl() == null) ? 0
//						: crucibleFile.getCrucibleFileInfo().getFileDescriptor().getAbsoluteUrl().hashCode());
//		result = prime
//				* result
//				+ ((crucibleFile.getCrucibleFileInfo().getFileDescriptor().getRevision() == null) ? 0
//						: crucibleFile.getCrucibleFileInfo().getFileDescriptor().getRevision().hashCode());
//		return result;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj) {
//			return true;
//		}
//		if (obj == null) {
//			return false;
//		}
//		if (getClass() != obj.getClass()) {
//			return false;
//		}
//		CrucibleAnnotationModel other = (CrucibleAnnotationModel) obj;
//		if (crucibleFile.getCrucibleFileInfo().getFileDescriptor().getAbsoluteUrl() == null) {
//			if (other.crucibleFile.getCrucibleFileInfo().getFileDescriptor().getAbsoluteUrl() != null) {
//				return false;
//			}
//		} else if (!crucibleFile.getCrucibleFileInfo()
//				.getFileDescriptor()
//				.getAbsoluteUrl()
//				.equals(other.crucibleFile.getCrucibleFileInfo().getFileDescriptor().getAbsoluteUrl())) {
//			return false;
//		}
//		if (crucibleFile.getCrucibleFileInfo().getFileDescriptor().getRevision() == null) {
//			if (other.crucibleFile.getCrucibleFileInfo().getFileDescriptor().getRevision() != null) {
//				return false;
//			}
//		} else if (!crucibleFile.getCrucibleFileInfo()
//				.getFileDescriptor()
//				.getRevision()
//				.equals(other.crucibleFile.getCrucibleFileInfo().getFileDescriptor().getRevision())) {
//			return false;
//		}
//		return true;
//	}

}
