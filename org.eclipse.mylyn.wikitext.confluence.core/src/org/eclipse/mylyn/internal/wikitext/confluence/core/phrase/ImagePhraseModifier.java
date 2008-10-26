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
package org.eclipse.mylyn.internal.wikitext.confluence.core.phrase;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElementProcessor;

/**
 * 
 * 
 * @author David Green
 */
public class ImagePhraseModifier extends PatternBasedElement {

	protected static final int CONTENT_GROUP = 1;

	@Override
	protected String getPattern(int groupOffset) {

		return "!([^\\|!\\s]+)(?:\\|([^!]*))?!";
	}

	@Override
	protected int getPatternGroupCount() {
		return 2;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new ImagePhraseModifierProcessor();
	}

	private static class ImagePhraseModifierProcessor extends PatternBasedElementProcessor {
		@Override
		public void emit() {
			String imageUrl = group(CONTENT_GROUP);

			Attributes attributes = new Attributes();
			builder.image(attributes, imageUrl);
		}
	}

}
