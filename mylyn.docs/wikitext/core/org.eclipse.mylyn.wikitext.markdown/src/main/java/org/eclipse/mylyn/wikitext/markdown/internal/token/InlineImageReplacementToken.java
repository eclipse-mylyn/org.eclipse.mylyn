/*******************************************************************************
 * Copyright (c) 2013 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.markdown.internal.token;

import org.eclipse.mylyn.wikitext.parser.ImageAttributes;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElementProcessor;

/**
 * Detects inline images: ![Alt text](/path/to/img.jpg "Optional title").
 * 
 * @author Stefan Seelmann
 */
public class InlineImageReplacementToken extends PatternBasedElement {

	@Override
	protected String getPattern(int groupOffset) {
		return "!\\[\\s*(.*?)\\s*\\]\\(\\s*(.+?)(?:\\s\"(.*?)\")?\\s*\\)"; //$NON-NLS-1$
	}

	@Override
	protected int getPatternGroupCount() {
		return 3;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new InlineLinkReplacementTokenProcessor();
	}

	private static class InlineLinkReplacementTokenProcessor extends PatternBasedElementProcessor {
		@Override
		public void emit() {
			String altText = group(1);
			String href = group(2);
			String title = group(3);
			ImageAttributes attributes = new ImageAttributes();
			attributes.setTitle(title);
			attributes.setAlt(altText);
			builder.image(attributes, href);
		}
	}

}
