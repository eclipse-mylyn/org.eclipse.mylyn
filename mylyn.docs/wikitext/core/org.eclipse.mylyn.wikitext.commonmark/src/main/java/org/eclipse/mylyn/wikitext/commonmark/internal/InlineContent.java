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
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.commonmark.internal;

import java.util.ArrayList;
import java.util.List;

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

public class InlineContent {

	public static InlineParser commonMarkStrict() {
		List<SourceSpan> spansBuilder = new ArrayList<>();
		addStandardSpans(spansBuilder);
		addTerminatorSpans(spansBuilder);
		return new InlineParser(List.copyOf(spansBuilder));
	}

	public static InlineParser markdown() {
		List<SourceSpan> spansBuilder = new ArrayList<>();
		addStandardSpans(spansBuilder);
		spansBuilder.add(new AutoLinkWithoutDemarcationSpan());
		addTerminatorSpans(spansBuilder);
		return new InlineParser(List.copyOf(spansBuilder));
	}

	private static void addStandardSpans(List<SourceSpan> spansBuilder) {
		spansBuilder.add(new LineBreakSpan());
		spansBuilder.add(new BackslashEscapeSpan());
		spansBuilder.add(new CodeSpan());
		spansBuilder.add(new AutoLinkSpan());
		spansBuilder.add(new HtmlTagSpan());
		spansBuilder.add(new HtmlEntitySpan());
		spansBuilder.add(new PotentialEmphasisSpan());
		spansBuilder.add(new PotentialBracketSpan());
	}

	private static void addTerminatorSpans(List<SourceSpan> spansBuilder) {
		spansBuilder.add(new StringCharactersSpan());
		spansBuilder.add(new AllCharactersSpan());
	}

	private InlineContent() {
		// prevent instantiation
	}
}
