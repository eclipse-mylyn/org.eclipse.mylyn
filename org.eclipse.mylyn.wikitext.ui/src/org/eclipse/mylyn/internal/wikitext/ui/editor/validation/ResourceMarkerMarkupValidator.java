/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
 * Markup validators are capable of validating a region of a document. Any validation problems or errors are created as
 * markers on the given resource, extending type <code>org.eclipse.mylyn.wikitext.core.validation.problem</code>.
 * 
 * NOTE: this implementation may change in the future to use an {@link IAnnotationModel} instead of resource markers
 * 
 * @author David Green
 * 
 * @see ValidationRule
 * @see ValidationProblem
 */
public class ResourceMarkerMarkupValidator extends DocumentRegionValidator {

	@Override
	protected void clearProblems(IProgressMonitor monitor, IDocument document, IRegion region) throws CoreException {
		monitor.beginTask(Messages.ResourceMarkerMarkupValidator_clearingMarkers, 1); 
		// nothing to do: we do all of this in the createProblems method
		monitor.done();
	}

	@Override
	protected void createProblems(IProgressMonitor monitor, IDocument document, IRegion region,
			List<ValidationProblem> problems) throws CoreException {
		final int findMarkersWorkSize = 100;
		final int zeroProblemsStep = 10;
		monitor.beginTask(
				Messages.ResourceMarkerMarkupValidator_creatingMarkers, problems.size() + findMarkersWorkSize + zeroProblemsStep); 

		// find and remove any existing validation errors in the given region.
		List<IMarker> markersInRegion = new ArrayList<IMarker>(5);
		// we also track markers by offset, however we don't track multiple markers at the same offset
		Map<Integer, IMarker> markerByOffset = new HashMap<Integer, IMarker>();
		{
			IMarker[] findMarkers = resource.findMarkers("org.eclipse.mylyn.wikitext.core.validation.problem", true, //$NON-NLS-1$
					IResource.DEPTH_ZERO);
			for (IMarker marker : findMarkers) {
				int offset = marker.getAttribute(IMarker.CHAR_START, 0);
				int end = marker.getAttribute(IMarker.CHAR_END, offset);
				if (overlaps(region, offset, end - offset) || offset >= document.getLength()) {
					markersInRegion.add(marker);
					markerByOffset.put(offset, marker);
				}
			}
			monitor.worked(findMarkersWorkSize);
		}

		if (problems.isEmpty()) {
			for (IMarker marker : markersInRegion) {
				marker.delete();
			}
			monitor.worked(zeroProblemsStep);
			monitor.done();
			return;
		}
		monitor.worked(zeroProblemsStep);

		// bug 261747: compute a delta so that we can avoid flicker
		if (!markersInRegion.isEmpty()) {
			// find all problems for which there is a marker that matches, and remove the problem from our
			// collection of problems to create
			Iterator<ValidationProblem> problemIt = problems.iterator();
			while (problemIt.hasNext()) {
				ValidationProblem problem = problemIt.next();
				IMarker marker = markerByOffset.get(problem.getOffset());
				if (marker != null) {
					int charEnd = marker.getAttribute(IMarker.CHAR_END, -1);
					if ((problem.getOffset() + problem.getLength()) == charEnd) {
						if (toMarkerSeverity(problem.getSeverity()) == marker.getAttribute(IMarker.SEVERITY, -1)) {
							if (problem.getMessage().equals(marker.getAttribute(IMarker.MESSAGE, ""))) { //$NON-NLS-1$
								problemIt.remove();
								markerByOffset.remove(problem.getOffset());
								markersInRegion.remove(marker);
								monitor.worked(1);
							}
						}
					}
				}
			}
			// remove all markers that had no matching problem
			for (IMarker marker : markersInRegion) {
				marker.delete();
			}
		}

		for (ValidationProblem problem : problems) {
			IMarker marker = resource.createMarker(problem.getMarkerId());

			marker.setAttribute(IMarker.TRANSIENT, true);
			marker.setAttribute(IMarker.SEVERITY, toMarkerSeverity(problem.getSeverity()));
			marker.setAttribute(IMarker.MESSAGE, problem.getMessage());
			marker.setAttribute(IMarker.CHAR_START, problem.getOffset());
			marker.setAttribute(IMarker.CHAR_END, problem.getOffset() + problem.getLength());

			try {
				int line = document.getLineOfOffset(problem.getOffset());
				marker.setAttribute(IMarker.LINE_NUMBER, line + 1);

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
