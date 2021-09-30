/*******************************************************************************
 * Copyright (c) 2015, 2021 David Green.
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

package org.eclipse.mylyn.wikitext.commonmark.internal.blocks;

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.not;
import static org.eclipse.mylyn.wikitext.commonmark.internal.LinePredicates.empty;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.commonmark.internal.CommonMark;
import org.eclipse.mylyn.wikitext.commonmark.internal.Line;
import org.eclipse.mylyn.wikitext.commonmark.internal.LineSequence;
import org.eclipse.mylyn.wikitext.commonmark.internal.ProcessingContext;
import org.eclipse.mylyn.wikitext.commonmark.internal.ProcessingContextBuilder;
import org.eclipse.mylyn.wikitext.commonmark.internal.SourceBlock;
import org.eclipse.mylyn.wikitext.commonmark.internal.SourceBlocks;
import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;

import com.google.common.base.Predicate;

public class BlockQuoteBlock extends BlockWithNestedBlocks {

	private static final Pattern START_PATTERN = Pattern.compile("\\s{0,3}>\\s?(.*)");

	@Override
	public void process(ProcessingContext context, DocumentBuilder builder, LineSequence lineSequence) {
		builder.setLocator(lineSequence.getCurrentLine().toLocator());
		builder.beginBlock(BlockType.QUOTE, new Attributes());

		SourceBlocks sourceBlocks = CommonMark.sourceBlocks();
		BlockQuoteState blockQuoteState = new BlockQuoteState();
		sourceBlocks.process(context, builder, blockQuoteState.blockQuoteLineSequence(lineSequence),
				blockQuoteState.contextPredicate());

		builder.endBlock();
	}

	@Override
	public void createContext(ProcessingContextBuilder contextBuilder, LineSequence lineSequence) {
		SourceBlocks sourceBlocks = CommonMark.sourceBlocks();
		BlockQuoteState blockQuoteState = new BlockQuoteState();
		sourceBlocks.createContext(contextBuilder, blockQuoteState.blockQuoteLineSequence(lineSequence),
				blockQuoteState.contextPredicate());
	}

	private static class BlockQuoteState {

		private SourceBlock currentBlock;

		Predicate<SourceBlocks.BlockContext> contextPredicate() {
			return context -> {
				currentBlock = context.getCurrentBlock();
				return true;
			};
		}

		LineSequence blockQuoteLineSequence(LineSequence lineSequence) {
			return lineSequence.with(blockQuoteLinePredicate(lineSequence)).transform(blockQuoteLineTransform());
		}

		private Function<Line, Line> blockQuoteLineTransform() {
			return line -> {
				Matcher matcher = START_PATTERN.matcher(line.getText());
				if (matcher.matches()) {
					int start = matcher.start(1);
					return line.segment(start, matcher.end(1) - start);
				}
				return line;
			};
		}

		private Predicate<Line> blockQuoteLinePredicate(LineSequence lineSequence) {
			return and(not(empty()), not(blockStructureStart(lineSequence)));
		}

		private Predicate<Line> blockStructureStart(final LineSequence lineSequence) {
			return new Predicate<Line>() {

				@Override
				public boolean apply(Line line) {
					LineSequence lookAhead = createLookAhead(lineSequence, line);
					SourceBlock lineBlock = CommonMark.sourceBlocks().selectBlock(lookAhead);
					return lineBlock != null
							&& !(isLazyContinuation(lineBlock) || lineBlock instanceof BlockQuoteBlock);
				}

				private boolean isLazyContinuation(SourceBlock lineBlock) {
					return lineBlock instanceof ParagraphBlock
							&& (currentBlock instanceof BlockQuoteBlock || currentBlock instanceof ParagraphBlock);
				}

				private LineSequence createLookAhead(LineSequence lineSequence, Line line) {
					LineSequence lookAhead = lineSequence.lookAhead();
					while (lookAhead.getCurrentLine() != null
							&& lookAhead.getCurrentLine().getLineNumber() < line.getLineNumber()) {
						lookAhead.advance();
					}
					return lookAhead;
				}
			};
		}
	}

	@Override
	public boolean canStart(LineSequence lineSequence) {
		return canStart(lineSequence.getCurrentLine());
	}

	private boolean canStart(Line line) {
		return line != null && START_PATTERN.matcher(line.getText()).matches();
	}

}
