/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.ui.editor.validation;

import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.mylyn.wikitext.core.validation.ValidationProblem;
import org.eclipse.mylyn.wikitext.core.validation.ValidationRule;
import org.eclipse.mylyn.wikitext.core.validation.ValidationProblem.Severity;

/**
 * Markup validators are capable of validating a region of a document.
 * Any validation problems or errors are created as
 * markers on the given resource, extending type <code>org.eclipse.mylyn.wikitext.validation.problem</code>.
 * 
 * NOTE: this implementation may change in the future to use an {@link IAnnotationModel} instead of
 * resource markers
 * 
 * @author David Green
 *
 * @see ValidationRule
 * @see ValidationProblem
 */
public class ResourceMarkerMarkupValidator extends DocumentRegionValidator {


	@Override
	protected void clearProblems(IProgressMonitor monitor, IDocument document, IRegion region) throws CoreException {
		// find and remove any existing validation errors in the given region.
		IMarker[] findMarkers = resource.findMarkers("org.eclipse.mylyn.wikitext.validation.problem", true, IResource.DEPTH_ZERO);
		monitor.beginTask("clearing markers", findMarkers.length==0?1:findMarkers.length);
		for (IMarker marker: findMarkers) {
			int offset = marker.getAttribute(IMarker.CHAR_START, 0);
			int end = marker.getAttribute(IMarker.CHAR_END, offset);
			if (overlaps(region,offset,end-offset) || offset >= document.getLength()) {
				marker.delete();
			}
			monitor.worked(1);
		}
		monitor.done();
	}

	@Override
	protected void createProblems(IProgressMonitor monitor, IDocument document,IRegion region, List<ValidationProblem> problems) throws CoreException {
		if (problems.isEmpty()) {
			return;
		}
		monitor.beginTask("creating markers", problems.size());
		for (ValidationProblem problem: problems) {
			IMarker marker = resource.createMarker(problem.getMarkerId());

			marker.setAttribute(IMarker.TRANSIENT, true);
			marker.setAttribute(IMarker.SEVERITY, toMarkerSeverity(problem.getSeverity()));
			marker.setAttribute(IMarker.MESSAGE, problem.getMessage());
			marker.setAttribute(IMarker.CHAR_START, problem.getOffset());
			marker.setAttribute(IMarker.CHAR_END,problem.getOffset()+problem.getLength());

			try {
				int line = document.getLineOfOffset(problem.getOffset());
				marker.setAttribute(IMarker.LINE_NUMBER, line+1);

			} catch (BadLocationException e) {
				// ignore
			}

			monitor.worked(1);
		}
		monitor.done();
	}

	private int toMarkerSeverity(Severity severity) {
		switch (severity) {
		case ERROR:
			return IMarker.SEVERITY_ERROR;
		case WARNING:
			return IMarker.SEVERITY_WARNING;
		default:
			throw new IllegalStateException(severity.name());
		}
	}

}
