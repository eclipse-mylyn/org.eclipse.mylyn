/*******************************************************************************
 * Copyright (c) 2007, 2013 David Green and others.
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

import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.ui.WikiText;
import org.eclipse.mylyn.wikitext.validation.MarkupValidator;
import org.eclipse.mylyn.wikitext.validation.ValidationProblem;

/**
 * A validator that can be used to validate regions of a document in an editor. Delegates validation to a {@link MarkupValidator} and
 * coordinates the translation of errors and warnings to the editor framework.
 *
 * @author David Green
 * @see MarkupValidator
 */
public abstract class DocumentRegionValidator {
	protected IResource resource;

	protected IAnnotationModel annotationModel;

	protected MarkupLanguage markupLanguage;

	protected MarkupValidator delegate;

	/**
	 * Validate a region of a document. Validation results may be created as annotations on the annotation model, or as markers on the
	 * resource.
	 *
	 * @param monitor
	 *            the progress monitor
	 * @param document
	 *            the document representing the content of the resource
	 * @param region
	 *            the region of the document to validate
	 * @throws CoreException
	 */
	public void validate(IProgressMonitor monitor, IDocument document, IRegion region) throws CoreException {
		if (region.getLength() == 0) {
			return;
		}
		final int totalWork = region.getLength() * 2;
		monitor.beginTask(Messages.DocumentRegionValidator_validation, totalWork);
		try {
			clearProblems(SubMonitor.convert(monitor, totalWork / 2), document, region);
			computeProblems(SubMonitor.convert(monitor, totalWork / 2), document, region);
		} finally {
			monitor.done();
		}
	}

	protected void computeProblems(IProgressMonitor monitor, IDocument document, IRegion region) throws CoreException {
		if (delegate == null) {
			return;
		}
		final int totalWork = Integer.MAX_VALUE / 2;
		monitor.beginTask(Messages.DocumentRegionValidator_validating, totalWork);
		try {
			String markup = document.get();
			int offset = region.getOffset();
			int length = region.getLength();

			// guard against invalid length see bug 361636
			if (offset + length > markup.length()) {
				length = markup.length() - offset;
			}

			List<ValidationProblem> problems;
			if (length <= 0) {
				problems = Collections.emptyList();
			} else {
				problems = delegate.validate(markup, offset, length);
				monitor.worked(totalWork / 2);
			}

			// always createProblems even if there are no problems
			createProblems(SubMonitor.convert(monitor, totalWork / 2), document, region, problems);

		} finally {
			monitor.done();
		}
	}

	/**
	 * create problems
	 *
	 * @param monitor
	 * @param document
	 * @param region
	 * @param problems
	 */
	protected abstract void createProblems(IProgressMonitor monitor, IDocument document, IRegion region,
			List<ValidationProblem> problems) throws CoreException;

	/**
	 * Remove any problems in the given region of the document.
	 *
	 * @param monitor
	 * @param document
	 * @param region
	 * @throws CoreException
	 */
	protected abstract void clearProblems(IProgressMonitor monitor, IDocument document, IRegion region)
			throws CoreException;

	public MarkupLanguage getMarkupLanguage() {
		return markupLanguage;
	}

	public void setMarkupLanguage(MarkupLanguage markupLanguage) {
		if (markupLanguage == this.markupLanguage || markupLanguage != null && this.markupLanguage != null
				&& markupLanguage.getName().equals(this.markupLanguage.getName())) {
			return;
		}
		this.markupLanguage = markupLanguage;
		delegate = markupLanguage == null ? null : WikiText.getMarkupValidator(markupLanguage.getName());
	}

	public IResource getResource() {
		return resource;
	}

	public void setResource(IResource resource) {
		this.resource = resource;
	}

	public IAnnotationModel getAnnotationModel() {
		return annotationModel;
	}

	public void setAnnotationModel(IAnnotationModel annotationModel) {
		this.annotationModel = annotationModel;
	}

	/**
	 * indicate if the given offset and length overlap with the given region
	 *
	 * @param region
	 * @param offset
	 * @param length
	 * @return true if they overlap, otherwise false.
	 */
	protected boolean overlaps(IRegion region, int offset, int length) {
		int end = offset + length;
		if (region.getOffset() > offset) {
			return end > region.getOffset();
		} else if (offset < region.getOffset() + region.getLength()) {
			return true;
		}
		return false;
	}
}
