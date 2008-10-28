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
package org.eclipse.mylyn.internal.wikitext.ui.editor.syntax;

import java.io.InputStream;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;

/**
 * 
 * 
 * @author David Green
 */
public class MarkupDocumentProvider extends TextFileDocumentProvider {

	private MarkupLanguage markupLanguage;

	public MarkupDocumentProvider() {
		super(new MarkupFileDocumentProvider());
	}

	public MarkupLanguage getMarkupLanguage() {
		return markupLanguage;
	}

	public void setMarkupLanguage(MarkupLanguage markupLanguage) {
		this.markupLanguage = markupLanguage;
	}

	@Override
	public void connect(Object element) throws CoreException {
		super.connect(element);
		IDocument document = super.getDocument(element);
		connectPartitioner(document);
	}

	private void connectPartitioner(IDocument document) {
		FastMarkupPartitioner partitioner = new FastMarkupPartitioner();
		partitioner.setMarkupLanguage(markupLanguage == null ? null : markupLanguage.clone());
		partitioner.connect(document);
		document.setDocumentPartitioner(partitioner);
	}

	@Override
	protected FileInfo createFileInfo(Object element) throws CoreException {
		if (dispatchToParent(element)) {
			// bug 247778: dispatch to MarkupFileDocumentProvider by returning null

			// unfortunately this means that we can't fixup EOL markers for better
			// cross-platform markup for opened files that aren't in the workspace
			return null;
		}
		return super.createFileInfo(element);
	}

	private boolean dispatchToParent(Object element) {
		return element instanceof IStorageEditorInput || element instanceof IFileEditorInput;
	}

	@Override
	protected DocumentProviderOperation createSaveOperation(final Object element, final IDocument document,
			final boolean overwrite) throws CoreException {
		if (dispatchToParent(element)) {
			//  bug 247778: dispatch to MarkupFileDocumentProvider
			return new DocumentProviderOperation() {
				@Override
				protected void execute(IProgressMonitor monitor) throws CoreException {
					getParentProvider().saveDocument(monitor, element, document, overwrite);
				}
			};
		} else {
			return super.createSaveOperation(element, document, overwrite);
		}
	}

	private static class MarkupFileDocumentProvider extends FileDocumentProvider {

		@Override
		protected void setDocumentContent(IDocument document, InputStream contentStream, String encoding)
				throws CoreException {
			super.setDocumentContent(document, contentStream, encoding);
			cleanUpEolMarkers(document);
		}

		/**
		 * override the default implementation to handle EOL issues on Mac, see bug 247777
		 */
		@Override
		protected void doSaveDocument(IProgressMonitor monitor, Object element, IDocument document, boolean overwrite)
				throws CoreException {
			String platformEolMarker = Text.DELIMITER;
			if (platformEolMarker.equals("\r")) { //$NON-NLS-1$
				// bug 247777: store document with *nix line delimiter
				// note that we don't modify the provided document here, we substitute another 
				// document instead.
				Document newDocument = new Document(document.get());
				replaceLineDelimiters(newDocument, "\n"); //$NON-NLS-1$
				document = newDocument;
			}
			super.doSaveDocument(monitor, element, document, overwrite);
		}
	}

	/**
	 * clean up EOL markers, since mixing markers can cause problems. For example, if the file has \n EOL markers, and
	 * the current platform uses \r (eg on a mac) then the user adding a new line immediately before \n can result in
	 * \r\n, which visually for the user appears to be two lines, but when the markup is parsed is treated as one line.
	 * This can be confusing for the user as line markers affect how the markup is interpreted. Generally we want the
	 * edited markup to render the same on all platforms, regardless of the platform-standard EOL marker.
	 */
	public static void cleanUpEolMarkers(IDocument document) {
		String platformEolMarker = Text.DELIMITER;
		replaceLineDelimiters(document, platformEolMarker);
	}

	private static void replaceLineDelimiters(IDocument document, String newLineDelimiter) {
		document.set(Pattern.compile("(\r\n|\n|\r)").matcher(document.get()).replaceAll(newLineDelimiter)); //$NON-NLS-1$
	}

}
