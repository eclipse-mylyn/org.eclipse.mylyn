/*******************************************************************************
 * Copyright (c) 2015, 2022 David Green and others.
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

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.builder.NoOpDocumentBuilder;

public class SourceBlocks extends SourceBlock {

	public static class BlockContext {

		private final SourceBlock currentBlock;

		private final LineSequence lineSequence;

		private final SourceBlock previousBlock;

		public BlockContext(SourceBlock previousBlock, SourceBlock currentBlock, LineSequence lineSequence) {
			this.previousBlock = previousBlock;
			this.currentBlock = currentBlock;
			this.lineSequence = lineSequence;
		}

		public SourceBlock getPreviousBlock() {
			return previousBlock;
		}

		public SourceBlock getCurrentBlock() {
			return currentBlock;
		}

		public LineSequence getLineSequence() {
			return lineSequence;
		}
	}

	private final List<SourceBlock> supportedBlocks;

	public SourceBlocks(SourceBlock... blocks) {
		this(Arrays.asList(requireNonNull(blocks)));
	}

	SourceBlocks(List<SourceBlock> supportedBlocks) {
		this.supportedBlocks = List.copyOf(supportedBlocks);
	}

	private interface SourceBlockRunnable {

		void run(LineSequence lineSequence, SourceBlock sourceBlock);
	}

	public void createContext(final ProcessingContextBuilder contextBuilder, LineSequence lineSequence,
			Predicate<BlockContext> contextPredicate) {
		process(lineSequence, (lineSequence1, sourceBlock) -> sourceBlock.createContext(contextBuilder, lineSequence1),
				contextPredicate);
	}

	@Override
	public void createContext(ProcessingContextBuilder contextBuilder, LineSequence lineSequence) {
		createContext(contextBuilder, lineSequence, x -> true);
	}

	@Override
	public void process(final ProcessingContext context, final DocumentBuilder builder, LineSequence lineSequence) {
		process(context, builder, lineSequence, x -> true);
	}

	public void process(final ProcessingContext context, final DocumentBuilder builder, LineSequence lineSequence,
			Predicate<BlockContext> predicate) {
		process(lineSequence, (lineSequence1, sourceBlock) -> sourceBlock.process(context, builder, lineSequence1),
				predicate);
	}

	public List<SourceBlock> calculateSourceBlocks(final ProcessingContext context, LineSequence lineSequence,
			Predicate<BlockContext> predicate) {
		final List<SourceBlock> sourceBlocks = new ArrayList<>();
		final NoOpDocumentBuilder builder = new NoOpDocumentBuilder();
		process(lineSequence, (lineSequence1, sourceBlock) -> {
			sourceBlocks.add(sourceBlock);
			sourceBlock.process(context, builder, lineSequence1);
		}, predicate);
		return sourceBlocks;
	}

	private void process(LineSequence lineSequence, SourceBlockRunnable runnable, Predicate<BlockContext> predicate) {
		SourceBlock currentBlock = null;
		SourceBlock previousBlock = null;
		while (lineSequence.getCurrentLine() != null) {
			previousBlock = currentBlock;
			currentBlock = selectBlock(lineSequence);
			if (!predicate.test(new BlockContext(previousBlock, currentBlock, lineSequence))) {
				break;
			}
			if (currentBlock != null) {
				runnable.run(lineSequence, currentBlock);
			} else {
				lineSequence.advance();
			}
		}
	}

	public SourceBlock selectBlock(LineSequence lineSequence) {
		for (SourceBlock candidate : supportedBlocks) {
			if (candidate.canStart(lineSequence)) {
				return candidate;
			}
		}
		return null;
	}

	@Override
	public boolean canStart(LineSequence lineSequence) {
		return true;
	}
}
