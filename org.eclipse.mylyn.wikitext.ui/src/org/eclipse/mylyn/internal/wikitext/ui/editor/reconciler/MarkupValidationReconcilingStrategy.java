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
package org.eclipse.mylyn.internal.wikitext.ui.editor.reconciler;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.mylyn.internal.wikitext.ui.editor.validation.AnnotationMarkupValidator;
import org.eclipse.mylyn.internal.wikitext.ui.editor.validation.DocumentRegionValidator;
import org.eclipse.mylyn.internal.wikitext.ui.editor.validation.ResourceMarkerMarkupValidator;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.ui.WikiTextUiPlugin;

/**
 *
 *
 * @author David Green
 */
public class MarkupValidationReconcilingStrategy implements
IReconcilingStrategy, IReconcilingStrategyExtension {

	private IDocument document;
	private IProgressMonitor monitor;
	private final ISourceViewer viewer;
	private DocumentRegionValidator validator;
	private IResource resource;
	private MarkupLanguage markupLanguage;

	public MarkupValidationReconcilingStrategy(ISourceViewer viewer) {
		this.viewer = viewer;
	}

	public MarkupLanguage getMarkupLanguage() {
		return markupLanguage;
	}

	public void setMarkupLanguage(MarkupLanguage markupLanguage) {
		this.markupLanguage = markupLanguage;
	}

	public IResource getResource() {
		return resource;
	}

	public void setResource(IResource resource) {
		this.resource = resource;
	}

	public void reconcile(IRegion partition) {
		if (document == null) {
			return;
		}
		if (validator == null) {
			validator = resource==null?new AnnotationMarkupValidator():new ResourceMarkerMarkupValidator();
		}
		validator.setMarkupLanguage(markupLanguage);
		validator.setAnnotationModel(getAnnotationModel());
		validator.setResource(resource);
		try {
			validator.validate(monitor==null?new NullProgressMonitor():monitor, document, partition);
		} catch (CoreException e) {
			WikiTextUiPlugin.getDefault().log(e);
		}
	}

	private IAnnotationModel getAnnotationModel() {
		return viewer.getAnnotationModel();
	}

	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		reconcile(subRegion);
	}

	public void setDocument(IDocument document) {
		this.document = document;
	}

	public void initialReconcile() {
		if (document == null) {
			return;
		}
		reconcile(new Region(0,document.getLength()));
	}

	public void setProgressMonitor(IProgressMonitor monitor) {
		this.monitor = monitor;
	}

}
