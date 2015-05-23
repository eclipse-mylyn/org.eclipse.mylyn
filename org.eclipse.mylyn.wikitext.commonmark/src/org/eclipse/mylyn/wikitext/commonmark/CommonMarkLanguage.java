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
package org.eclipse.mylyn.wikitext.commonmark;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.mylyn.internal.wikitext.commonmark.CommonMark;
import org.eclipse.mylyn.internal.wikitext.commonmark.LineSequence;
import org.eclipse.mylyn.internal.wikitext.commonmark.ProcessingContext;
import org.eclipse.mylyn.internal.wikitext.commonmark.ProcessingContextBuilder;
import org.eclipse.mylyn.internal.wikitext.commonmark.SourceBlocks;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;

public class CommonMarkLanguage extends MarkupLanguage {

	public CommonMarkLanguage() {
		setName("CommonMark");
	}

	@Override
	public void processContent(MarkupParser parser, String markupContent, boolean asDocument) {
		checkNotNull(parser);
		checkNotNull(markupContent);

		DocumentBuilder builder = checkNotNull(parser.getBuilder());
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

	private ProcessingContext createContext(SourceBlocks sourceBlocks, String markupContent) {
		ProcessingContextBuilder contextBuilder = ProcessingContext.builder();
		sourceBlocks.createContext(contextBuilder, LineSequence.create(markupContent));
		return contextBuilder.build();
	}
}
