/*******************************************************************************
 * Copyright (c) 2015 David Green.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.commonmark.blocks;

import org.eclipse.mylyn.internal.wikitext.commonmark.inlines.AllCharactersSpan;
import org.eclipse.mylyn.internal.wikitext.commonmark.inlines.AutoLinkSpan;
import org.eclipse.mylyn.internal.wikitext.commonmark.inlines.BackslashEscapeSpan;
import org.eclipse.mylyn.internal.wikitext.commonmark.inlines.CodeSpan;
import org.eclipse.mylyn.internal.wikitext.commonmark.inlines.HtmlEntitySpan;
import org.eclipse.mylyn.internal.wikitext.commonmark.inlines.HtmlTagSpan;
import org.eclipse.mylyn.internal.wikitext.commonmark.inlines.InlineParser;
import org.eclipse.mylyn.internal.wikitext.commonmark.inlines.LineBreakSpan;
import org.eclipse.mylyn.internal.wikitext.commonmark.inlines.PotentialBracketSpan;
import org.eclipse.mylyn.internal.wikitext.commonmark.inlines.PotentialEmphasisSpan;
import org.eclipse.mylyn.internal.wikitext.commonmark.inlines.StringCharactersSpan;

class InlineContent extends InlineParser {

	public InlineContent() {
		super(new LineBreakSpan(), new BackslashEscapeSpan(), new CodeSpan(), new AutoLinkSpan(), new HtmlTagSpan(),
				new HtmlEntitySpan(), new PotentialEmphasisSpan(), new PotentialBracketSpan(),
				new StringCharactersSpan(), new AllCharactersSpan());
	}

}
