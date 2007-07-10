/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilerExtension;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.mylyn.tasks.ui.editors.TaskFormPage.ErrorAnnotation;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector;
import org.eclipse.ui.texteditor.spelling.SpellingContext;
import org.eclipse.ui.texteditor.spelling.SpellingProblem;

/**
 * Adapted from org.eclipse.jdt.internal.ui.text.spelling.PropertiesSpellingReconcileStrategy
 * 
 * @author Jeff Pound
 * @author Rob Elves
 */
public class TaskSpellingReconcileStrategy implements IReconcilerExtension, IReconcilingStrategy {

	/**
	 * Spelling problem collector that forwards {@link SpellingProblem}s as {@link IProblem}s to the
	 * {@link org.eclipse.jdt.core.IProblemRequestor}.
	 */
	private class SpellingProblemCollector implements ISpellingProblemCollector {

		/** Annotation model */
		private IAnnotationModel fAnnotationModel;

		/** Annotations to add */
		private Map<ErrorAnnotation, Position> fAddAnnotations;

		/**
		 * Initializes this collector with the given annotation model.
		 * 
		 * @param annotationModel
		 *            the annotation model
		 */
		public SpellingProblemCollector(IAnnotationModel annotationModel) {
			fAnnotationModel = annotationModel;
		}

		/*
		 * @see org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector#accept(org.eclipse.ui.texteditor.spelling.SpellingProblem)
		 */
		public void accept(SpellingProblem problem) {
			try {
				int line = fDocument.getLineOfOffset(problem.getOffset()) + 1;

				fAddAnnotations.put(new ErrorAnnotation(line, null), new Position(problem.getOffset(),
						problem.getLength()));

			} catch (BadLocationException x) {
				// drop this SpellingProblem
			}
		}

		/*
		 * @see org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector#beginCollecting()
		 */
		public void beginCollecting() {
			fAddAnnotations = new HashMap<ErrorAnnotation, Position>();
		}

		/*
		 * @see org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector#endCollecting()
		 */
		@SuppressWarnings("unchecked")
		public void endCollecting() {

			List<Annotation> removeAnnotations = new ArrayList<Annotation>();
			for (Iterator iter = fAnnotationModel.getAnnotationIterator(); iter.hasNext();) {
				Annotation annotation = (Annotation) iter.next();
				if (ErrorAnnotation.ERROR_TYPE.equals(annotation.getType()))
					removeAnnotations.add(annotation);
			}

			for (Iterator iter = removeAnnotations.iterator(); iter.hasNext();)
				fAnnotationModel.removeAnnotation((Annotation) iter.next());
			for (Iterator iter = fAddAnnotations.keySet().iterator(); iter.hasNext();) {
				Annotation annotation = (Annotation) iter.next();
				fAnnotationModel.addAnnotation(annotation, fAddAnnotations.get(annotation));
			}

			fAddAnnotations = null;
		}
	}

	/** The taskId of the problem */
	public static final int SPELLING_PROBLEM_ID = 0x80000000;

	/** The document to operate on. */
	private IDocument fDocument;

	/** The progress monitor. */
	private IProgressMonitor fProgressMonitor;

	/**
	 * The spelling context containing the Java properties content type.
	 * <p>
	 * Since his reconcile strategy is for the Properties File editor which normally edits Java properties files we
	 * always use the Java properties file content type for performance reasons.
	 * </p>
	 * 
	 * @since 3.2
	 */
	private SpellingContext fSpellingContext;

	private IAnnotationModel annotationModel;

	public TaskSpellingReconcileStrategy() {
		this.annotationModel = null;
		fSpellingContext = new SpellingContext();
		fSpellingContext.setContentType(Platform.getContentTypeManager().getContentType(IContentTypeManager.CT_TEXT));
	}

	/*
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension#initialReconcile()
	 */
	public void initialReconcile() {
		reconcile(new Region(0, fDocument.getLength()));
	}

	/*
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategy#reconcile(org.eclipse.jface.text.reconciler.DirtyRegion,org.eclipse.jface.text.IRegion)
	 */
	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		reconcile(subRegion);
	}

	/*
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategy#reconcile(org.eclipse.jface.text.IRegion)
	 */
	public void reconcile(IRegion region) {
		TaskSpellingReconcileStrategy.SpellingProblemCollector collector = new SpellingProblemCollector(annotationModel);
		EditorsUI.getSpellingService().check(fDocument, fSpellingContext, collector, fProgressMonitor);
	}

	/*
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategy#setDocument(org.eclipse.jface.text.IDocument)
	 */
	public void setDocument(IDocument document) {
		fDocument = document;
	}

	/*
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension#setProgressMonitor(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void setProgressMonitor(IProgressMonitor monitor) {
		fProgressMonitor = monitor;
	}

	public String getDocumentPartitioning() {
		// ignore
		return null;
	}

	public void setAnnotationModel(IAnnotationModel model) {
		annotationModel = model;
	}
}