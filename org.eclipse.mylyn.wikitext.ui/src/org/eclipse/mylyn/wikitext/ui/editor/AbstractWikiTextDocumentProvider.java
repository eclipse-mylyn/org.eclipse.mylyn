/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.ui.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.ui.editors.text.StorageDocumentProvider;
import org.eclipse.ui.texteditor.ResourceMarkerAnnotationModel;

/**
 * An abstract document provider for the {@link WikiTextSourceEditor}. Subclasses must implement
 * {@link #doSaveDocument(IProgressMonitor, Object, IDocument, boolean) mutable document storage}.
 * 
 * @author David Green
 * @since 1.3
 */
public abstract class AbstractWikiTextDocumentProvider extends StorageDocumentProvider implements WikiTextDocumentProvider {

	private MarkupLanguage markupLanguage;

	@Override
	protected void setupDocument(Object element, IDocument document) {
		super.setupDocument(element, document);
		WikiTextSourcePartitioning.configurePartitioning(document, markupLanguage);
	}

	public MarkupLanguage getMarkupLanguage() {
		return markupLanguage;
	}

	public void setMarkupLanguage(MarkupLanguage markupLanguage) {
		this.markupLanguage = markupLanguage;
	}

	@Override
	protected IAnnotationModel createAnnotationModel(Object element) throws CoreException {
		if (element instanceof IAdaptable) {
			IFile file = (IFile) ((IAdaptable) element).getAdapter(IFile.class);
			if (file != null) {
				return new ResourceMarkerAnnotationModel(file);
			}
		}

		return new AnnotationModel();
	}

	@Override
	protected abstract void doSaveDocument(IProgressMonitor monitor, Object element, IDocument document,
			boolean overwrite) throws CoreException;
}
