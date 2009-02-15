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
package org.eclipse.mylyn.wikitext.ui.viewer;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.mylyn.internal.wikitext.ui.WikiTextUiPlugin;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.swt.widgets.Composite;

/**
 * A viewer that can show wikitext markup (lightweight markup languages) by converting the markup to HTML. Designed for
 * read-only display of markup.
 * 
 * @see MarkupViewerConfiguration
 * 
 * @author David Green
 * @since 1.0
 */
public class MarkupViewer extends HtmlViewer {

	private MarkupParser parser = new MarkupParser();

	public MarkupViewer(Composite parent, IVerticalRuler ruler, int styles) {
		super(parent, ruler, styles);
		setEditable(false);
	}

	public MarkupViewer(Composite parent, IVerticalRuler verticalRuler, IOverviewRuler overviewRuler,
			boolean showAnnotationsOverview, int styles) {
		super(parent, verticalRuler, overviewRuler, showAnnotationsOverview, styles);
		setEditable(false);
	}

	public void setMarkup(String source) {
		try {
			String htmlText = parser.parseToHtml(source);
			setHtml(htmlText);
		} catch (Throwable t) {
			if (getTextPresentation() != null) {
				getTextPresentation().clear();
			}
			setDocumentNoMarkup(new Document(source), new AnnotationModel());
			if (WikiTextUiPlugin.getDefault() != null) {
				WikiTextUiPlugin.getDefault().log(IStatus.ERROR, Messages.getString("MarkupViewer.0"), t); //$NON-NLS-1$
			} else {
				t.printStackTrace();
			}
		}
	}

	public MarkupParser getParser() {
		return parser;
	}

	public void setParser(MarkupParser parser) {
		this.parser = parser;
	}

	public MarkupLanguage getMarkupLanguage() {
		return parser.getMarkupLanguage();
	}

	public void setMarkupLanguage(MarkupLanguage markupLanguage) {
		parser.setMarkupLanguage(markupLanguage);
	}

	@Override
	public void setDocument(IDocument document, IAnnotationModel annotationModel, int modelRangeOffset,
			int modelRangeLength) {
		String markupContent = null;
		if (document != null) {
			markupContent = document.get();
			if (markupContent.length() > 0) {
				String htmlText = parser.parseToHtml(markupContent);
				document.set(htmlText);
			}
		}
		try {
			super.setDocument(document, annotationModel, modelRangeOffset, modelRangeLength);
		} catch (Exception e) {
			if (document != null) {
				document.set(markupContent);
				setDocumentNoMarkup(document, annotationModel);
			}
		}
	}
}
