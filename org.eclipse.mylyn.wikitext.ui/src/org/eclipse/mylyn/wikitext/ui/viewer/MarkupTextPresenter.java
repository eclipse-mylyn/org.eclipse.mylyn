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
package org.eclipse.mylyn.wikitext.ui.viewer;

import org.eclipse.jface.text.TextPresentation;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.swt.graphics.Drawable;

/**
 * An information presenter that supports markup. Uses the {@link MarkupParser} to parse the markup to HTML, and passes
 * the HTML to the superclass.
 * 
 * @author David Green
 * 
 * @since 1.0 
 */
public class MarkupTextPresenter extends HtmlTextPresenter {

	private MarkupLanguage markupLanguage;

	/**
	 * the markup language used by this presenter
	 */
	public MarkupLanguage getMarkupLanguage() {
		return markupLanguage;
	}

	/**
	 * the markup language used by this presenter
	 */
	public void setMarkupLanguage(MarkupLanguage markupLanguage) {
		this.markupLanguage = markupLanguage;
	}

	@Override
	public String updatePresentation(Drawable drawable, String hoverInfo, TextPresentation presentation, int maxWidth,
			int maxHeight) {
		if (markupLanguage == null) {
			throw new IllegalStateException();
		}
		if (hoverInfo == null || hoverInfo.length() == 0) {
			return hoverInfo;
		}
		MarkupParser parser = new MarkupParser();
		parser.setMarkupLanaguage(markupLanguage);
		String html = parser.parseToHtml(hoverInfo);

		return super.updatePresentation(drawable, html, presentation, maxWidth, maxHeight);
	}

}
