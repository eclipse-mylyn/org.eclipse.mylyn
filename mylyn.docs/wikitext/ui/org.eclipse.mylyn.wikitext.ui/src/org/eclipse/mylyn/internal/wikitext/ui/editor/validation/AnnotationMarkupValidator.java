/*******************************************************************************
 * Copyright (c) 2007, 2011 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.ui.editor.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ISynchronizable;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.mylyn.wikitext.validation.ValidationProblem;

/**
 * @author David Green
 */
public class AnnotationMarkupValidator extends DocumentRegionValidator {

	@Override
	protected void clearProblems(IProgressMonitor monitor, IDocument document, IRegion region) {
		monitor.beginTask(Messages.AnnotationMarkupValidator_clearingProblems, 100);
		// nothing to do: we do this all in the createProblems method.
		monitor.done();
	}

	@Override
	protected void createProblems(IProgressMonitor monitor, IDocument document, IRegion region,
			List<ValidationProblem> problems) {
		Object lockObject;
		if (annotationModel instanceof ISynchronizable synchro) {
			lockObject = synchro.getLockObject();
		} else {
			lockObject = annotationModel;
		}
		synchronized (lockObject) {
			List<Annotation> toRemove = null;
			Iterator<Annotation> annotationIterator = annotationModel.getAnnotationIterator();
			while (annotationIterator.hasNext()) {
				Annotation annotation = annotationIterator.next();
				if (ValidationProblemAnnotation.isValidationAnnotation(annotation)) {
					Position position = annotationModel.getPosition(annotation);
					int offset = position.getOffset();
					if (overlaps(region, offset, position.getLength()) || offset >= document.getLength()) {
						if (toRemove == null) {
							toRemove = new ArrayList<>();
						}
						toRemove.add(annotation);
					}
				}
			}

			Map<Annotation, Position> annotationsToAdd = new HashMap<>();
			for (ValidationProblem problem : problems) {
				annotationsToAdd.put(new ValidationProblemAnnotation(problem),
						new Position(problem.getOffset(), problem.getLength()));
			}

			if (toRemove != null && annotationModel instanceof IAnnotationModelExtension annotationModelExtension) {
				Annotation[] annotationsToRemove = toRemove.toArray(new Annotation[toRemove.size()]);
				annotationModelExtension.replaceAnnotations(annotationsToRemove, annotationsToAdd);
			} else {
				if (toRemove != null) {
					for (Annotation annotation : toRemove) {
						annotationModel.removeAnnotation(annotation);
					}
				}
				for (Map.Entry<Annotation, Position> entry : annotationsToAdd.entrySet()) {
					annotationModel.addAnnotation(entry.getKey(), entry.getValue());
				}
			}
		}
	}

	@Override
	public void validate(IProgressMonitor monitor, IDocument document, IRegion region) throws CoreException {
		if (annotationModel == null) {
			return;
		}
		super.validate(monitor, document, region);
	}
}
