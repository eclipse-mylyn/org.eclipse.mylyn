/*******************************************************************************
 * Copyright (c) 2007, 2011 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.twiki.internal.token;

import org.eclipse.mylyn.wikitext.parser.ImageAttributes;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElementProcessor;
import org.eclipse.mylyn.wikitext.twiki.TWikiLanguage;

/**
 * Token that replaces <code>%ICON{"<icon type>"}%</code> with the appropriate image tag. Supports %ICON{"help"}%, %ICON{"tip"}%, and
 * %ICON{"warning"}%
 * 
 * @author David Green
 */
public class IconReplacementToken extends PatternBasedElement {

	// TODO: check http://twiki.org/cgi-bin/view/TWiki04x02/TWikiDocGraphics to see if this covers all graphics

	@Override
	protected String getPattern(int groupOffset) {
		return "%ICON\\{\"([a-zA-Z]+)\"\\}%"; //$NON-NLS-1$
	}

	@Override
	protected int getPatternGroupCount() {
		return 1;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new IconProcessor();
	}

	private static class IconProcessor extends PatternBasedElementProcessor {
		@Override
		public void emit() {
			String iconType = group(1);
			String iconUrl = ((TWikiLanguage) markupLanguage).toIconUrl(iconType);
			builder.image(new ImageAttributes(), iconUrl);
		}
	}

}
