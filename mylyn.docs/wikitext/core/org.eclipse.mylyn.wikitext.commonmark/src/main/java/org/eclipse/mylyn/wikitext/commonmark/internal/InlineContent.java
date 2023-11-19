/*******************************************************************************
 * Copyright (c) 2015 David Green.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.commonmark.internal;

import org.eclipse.mylyn.wikitext.commonmark.internal.inlines.AllCharactersSpan;
import org.eclipse.mylyn.wikitext.commonmark.internal.inlines.AutoLinkSpan;
import org.eclipse.mylyn.wikitext.commonmark.internal.inlines.AutoLinkWithoutDemarcationSpan;
import org.eclipse.mylyn.wikitext.commonmark.internal.inlines.BackslashEscapeSpan;
import org.eclipse.mylyn.wikitext.commonmark.internal.inlines.CodeSpan;
import org.eclipse.mylyn.wikitext.commonmark.internal.inlines.HtmlEntitySpan;
import org.eclipse.mylyn.wikitext.commonmark.internal.inlines.HtmlTagSpan;
import org.eclipse.mylyn.wikitext.commonmark.internal.inlines.InlineParser;
import org.eclipse.mylyn.wikitext.commonmark.internal.inlines.LineBreakSpan;
import org.eclipse.mylyn.wikitext.commonmark.internal.inlines.PotentialBracketSpan;
import org.eclipse.mylyn.wikitext.commonmark.internal.inlines.PotentialEmphasisSpan;
import org.eclipse.mylyn.wikitext.commonmark.internal.inlines.SourceSpan;
import org.eclipse.mylyn.wikitext.commonmark.internal.inlines.StringCharactersSpan;

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
