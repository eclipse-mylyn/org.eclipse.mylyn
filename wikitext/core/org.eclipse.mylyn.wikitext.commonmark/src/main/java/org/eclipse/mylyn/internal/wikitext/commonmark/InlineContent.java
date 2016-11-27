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

package org.eclipse.mylyn.internal.wikitext.commonmark;

import org.eclipse.mylyn.internal.wikitext.commonmark.inlines.AllCharactersSpan;
import org.eclipse.mylyn.internal.wikitext.commonmark.inlines.AutoLinkSpan;
import org.eclipse.mylyn.internal.wikitext.commonmark.inlines.AutoLinkWithoutDemarcationSpan;
import org.eclipse.mylyn.internal.wikitext.commonmark.inlines.BackslashEscapeSpan;
import org.eclipse.mylyn.internal.wikitext.commonmark.inlines.CodeSpan;
import org.eclipse.mylyn.internal.wikitext.commonmark.inlines.HtmlEntitySpan;
import org.eclipse.mylyn.internal.wikitext.commonmark.inlines.HtmlTagSpan;
import org.eclipse.mylyn.internal.wikitext.commonmark.inlines.InlineParser;
import org.eclipse.mylyn.internal.wikitext.commonmark.inlines.LineBreakSpan;
import org.eclipse.mylyn.internal.wikitext.commonmark.inlines.PotentialBracketSpan;
import org.eclipse.mylyn.internal.wikitext.commonmark.inlines.PotentialEmphasisSpan;
import org.eclipse.mylyn.internal.wikitext.commonmark.inlines.SourceSpan;
import org.eclipse.mylyn.internal.wikitext.commonmark.inlines.StringCharactersSpan;

import com.google.common.collect.ImmutableList;

public class InlineContent {

	public static InlineParser commonMarkStrict() {
		ImmutableList.Builder<SourceSpan> spansBuilder = ImmutableList.builder();
		addStandardSpans(spansBuilder);
		addTerminatorSpans(spansBuilder);
		return new InlineParser(spansBuilder.build());
	}

	public static InlineParser markdown() {
		ImmutableList.Builder<SourceSpan> spansBuilder = ImmutableList.builder();
		addStandardSpans(spansBuilder);
		spansBuilder.add(new AutoLinkWithoutDemarcationSpan());
		addTerminatorSpans(spansBuilder);
		return new InlineParser(spansBuilder.build());
	}

	private static void addStandardSpans(ImmutableList.Builder<SourceSpan> spansBuilder) {
		spansBuilder.add(new LineBreakSpan());
		spansBuilder.add(new BackslashEscapeSpan());
		spansBuilder.add(new CodeSpan());
		spansBuilder.add(new AutoLinkSpan());
		spansBuilder.add(new HtmlTagSpan());
		spansBuilder.add(new HtmlEntitySpan());
		spansBuilder.add(new PotentialEmphasisSpan());
		spansBuilder.add(new PotentialBracketSpan());
	}

	private static void addTerminatorSpans(ImmutableList.Builder<SourceSpan> spansBuilder) {
		spansBuilder.add(new StringCharactersSpan());
		spansBuilder.add(new AllCharactersSpan());
	}

	private InlineContent() {
		// prevent instantiation
	}
}
