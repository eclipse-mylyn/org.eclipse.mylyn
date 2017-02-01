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

import org.eclipse.jface.text.IDocument;
import org.eclipse.mylyn.internal.wikitext.ui.editor.syntax.FastMarkupPartitioner;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;

/**
 * A utility for configuring partitioning on a document.
 * 
 * @author David Green
 * @since 1.3
 */
public class WikiTextSourcePartitioning {
	/**
	 * Configure partitioning on a document using the specified markup language.
	 * 
	 * @param document
	 *            the document that should have its partitioning configured
	 * @param markupLanguage
	 *            the markup language to use, or null
	 */
	public static void configurePartitioning(IDocument document, MarkupLanguage markupLanguage) {
		FastMarkupPartitioner partitioner = new FastMarkupPartitioner();
		partitioner.setMarkupLanguage(markupLanguage == null ? null : markupLanguage.clone());
		partitioner.connect(document);
		document.setDocumentPartitioner(partitioner);
	}
}
