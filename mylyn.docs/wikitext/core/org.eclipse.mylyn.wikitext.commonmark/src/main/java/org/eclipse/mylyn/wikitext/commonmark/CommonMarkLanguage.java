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
package org.eclipse.mylyn.wikitext.commonmark;

import static java.util.Objects.requireNonNull;

import org.eclipse.mylyn.wikitext.commonmark.internal.CommonMark;
import org.eclipse.mylyn.wikitext.commonmark.internal.CommonMarkIdGenerationStrategy;
import org.eclipse.mylyn.wikitext.commonmark.internal.InlineContent;
import org.eclipse.mylyn.wikitext.commonmark.internal.LineSequence;
import org.eclipse.mylyn.wikitext.commonmark.internal.ProcessingContext;
import org.eclipse.mylyn.wikitext.commonmark.internal.ProcessingContextBuilder;
import org.eclipse.mylyn.wikitext.commonmark.internal.SourceBlocks;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.markup.IdGenerationStrategy;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;

/**
 * @since 3.0
 */
public class CommonMarkLanguage extends MarkupLanguage {

	private boolean strictlyConforming = false;

	public CommonMarkLanguage() {
		setName("CommonMark");
	}

	@Override
	public void processContent(MarkupParser parser, String markupContent, boolean asDocument) {
		requireNonNull(parser);
		requireNonNull(markupContent);

		DocumentBuilder builder = requireNonNull(parser.getBuilder());
		if (asDocument) {
			builder.beginDocument();
		}

		SourceBlocks sourceBlocks = CommonMark.sourceBlocks();

		ProcessingContext context = createContext(sourceBlocks, markupContent);

		sourceBlocks.process(context, builder, LineSequence.create(markupContent));

		if (asDocument) {
			builder.endDocument();
		}
	}

	@Override
	public IdGenerationStrategy getIdGenerationStrategy() {
		return isStrictlyConforming() ? null : new CommonMarkIdGenerationStrategy();
	}

	private ProcessingContext createContext(SourceBlocks sourceBlocks, String markupContent) {
		ProcessingContextBuilder contextBuilder = ProcessingContext.builder()
				.idGenerationStrategy(getIdGenerationStrategy());
		if (!strictlyConforming) {
			contextBuilder.inlineParser(InlineContent.markdown());
		}
		sourceBlocks.createContext(contextBuilder, LineSequence.create(markupContent));
		return contextBuilder.build();
	}

	public void setStrictlyConforming(boolean strictlyConforming) {
		this.strictlyConforming = strictlyConforming;
	}

	public boolean isStrictlyConforming() {
		return strictlyConforming;
	}

	@Override
	public CommonMarkLanguage clone() {
		CommonMarkLanguage language = (CommonMarkLanguage) super.clone();
		language.strictlyConforming = this.strictlyConforming;
		return language;
	}
}
