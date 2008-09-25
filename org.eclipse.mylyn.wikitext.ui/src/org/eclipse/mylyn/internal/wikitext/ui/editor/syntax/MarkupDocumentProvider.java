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
import org.eclipse.ui.editors.text.FileDocumentProvider;

// FIXME: move to internal, rename

/**
 * 
 * 
 * @author David Green
 */
public class MarkupDocumentProvider extends FileDocumentProvider {

	private MarkupLanguage markupLanguage;

	@Override
	protected IDocument createDocument(Object element) throws CoreException {
		IDocument document = super.createDocument(element);
		if (document != null) {
			connectPartitioner(document);
		}
		return document;
	}

	@Override
	protected IDocument createEmptyDocument() {
		IDocument document = super.createEmptyDocument();
		connectPartitioner(document);
		return document;
	}

	private void connectPartitioner(IDocument document) {
		FastMarkupPartitioner partitioner = new FastMarkupPartitioner();
		partitioner.setMarkupLanguage(markupLanguage == null ? null : markupLanguage.clone());
		partitioner.connect(document);
		document.setDocumentPartitioner(partitioner);
	}

	public MarkupLanguage getMarkupLanguage() {
		return markupLanguage;
	}

	public void setMarkupLanguage(MarkupLanguage markupLanguage) {
		this.markupLanguage = markupLanguage;
	}

	@Override
	protected void setDocumentContent(IDocument document, InputStream contentStream, String encoding)
			throws CoreException {
		super.setDocumentContent(document, contentStream, encoding);
		cleanUpEolMarkers(document);
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
		document.set(Pattern.compile("(\r\n|\n|\r)").matcher(document.get()).replaceAll(newLineDelimiter));
	}

	/**
	 * override the default implementation to handle EOL issues on Mac, see bug 247777
	 */
	@Override
	protected void doSaveDocument(IProgressMonitor monitor, Object element, IDocument document, boolean overwrite)
			throws CoreException {
		String platformEolMarker = Text.DELIMITER;
		if (platformEolMarker.equals("\r")) {
			// bug 247777: store document with *nix line delimiter
			Document newDocument = new Document(document.get());
			replaceLineDelimiters(newDocument, "\n");
			document = newDocument;
		}
		super.doSaveDocument(monitor, element, document, overwrite);
	}
}
