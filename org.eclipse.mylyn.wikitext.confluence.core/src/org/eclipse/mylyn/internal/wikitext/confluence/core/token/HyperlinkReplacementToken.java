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
package org.eclipse.mylyn.internal.wikitext.confluence.core.token;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.LinkAttributes;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElementProcessor;

/**
 * 
 * 
 * @author David Green
 */
public class HyperlinkReplacementToken extends PatternBasedElement {

	@Override
	protected String getPattern(int groupOffset) {
		return "\\[([^\\]]+)\\]";
	}

	@Override
	protected int getPatternGroupCount() {
		return 1;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new HyperlinkReplacementTokenProcessor();
	}

	private static class HyperlinkReplacementTokenProcessor extends PatternBasedElementProcessor {
		@Override
		public void emit() {
			String linkComposite = group(1);
			String[] parts = linkComposite.split("\\s*\\|\\s*");
			String text = parts.length > 1 ? parts[0] : null;
			if (text != null) {
				text = text.trim();
			}
			String href = parts.length > 1 ? parts[1] : parts[0];
			if (href != null) {
				href = href.trim();
			}
			String tip = parts.length > 2 ? parts[2] : null;
			if (tip != null) {
				tip = tip.trim();
			}
			if (text == null || text.length() == 0) {
				text = href;
			}
			Attributes attributes = new LinkAttributes();
			attributes.setTitle(tip);
			getBuilder().link(attributes, href, text);
		}
	}
}
