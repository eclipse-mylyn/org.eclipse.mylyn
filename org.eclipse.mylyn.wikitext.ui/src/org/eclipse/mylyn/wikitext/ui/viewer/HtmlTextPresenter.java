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

import java.util.regex.Pattern;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.mylyn.internal.wikitext.ui.viewer.HtmlTextPresentationParser;
import org.eclipse.swt.graphics.Drawable;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

/**
 * An information presenter that supports HTML markup. Uses the {@link HtmlTextPresentationParser HTML parser} to parse
 * the HTML to readable text, and creates the appropriate corresponding {@link TextPresentation text presentation}.
 * 
 * @author David Green
 * 
 * @since 1.0 
 */
public class HtmlTextPresenter implements DefaultInformationControl.IInformationPresenter,
		DefaultInformationControl.IInformationPresenterExtension {

	private static Pattern HTML_OPEN_TAG_PATTERN = Pattern.compile("<html", Pattern.CASE_INSENSITIVE); //$NON-NLS-1$

	@SuppressWarnings("deprecation")
	public String updatePresentation(Display display, String hoverInfo, TextPresentation presentation, int maxWidth,
			int maxHeight) {
		return updatePresentation((Drawable) display, hoverInfo, presentation, maxWidth, maxHeight);
	}

	public String updatePresentation(Drawable drawable, String hoverInfo, TextPresentation presentation, int maxWidth,
			int maxHeight) {
		if (hoverInfo == null || hoverInfo.length() == 0) {
			return hoverInfo;
		}
		HtmlTextPresentationParser parser = new HtmlTextPresentationParser();
		parser.setPresentation(presentation);
		parser.setDefaultFont(JFaceResources.getFontRegistry().defaultFont());
		String html = hoverInfo;
		if (!HTML_OPEN_TAG_PATTERN.matcher(html).find()) {
			html = "<html><body>" + html + "</body></html>"; //$NON-NLS-1$ //$NON-NLS-2$
		}

		GC gc = new GC(drawable);
		try {
			html = html.replaceAll("<br>", "<br/>"); //$NON-NLS-1$ //$NON-NLS-2$
			parser.setMaxWidth(maxWidth);
			parser.setGC(gc);
			parser.parse(html);
			return parser.getText();
		} catch (Exception e) {
			return exceptionToHoverInfo(hoverInfo, presentation, e);
		} finally {
			gc.dispose();
		}
	}

	protected String exceptionToHoverInfo(String hoverInfo, TextPresentation presentation, Exception e) {
		presentation.clear();
		return hoverInfo;
	}

}
