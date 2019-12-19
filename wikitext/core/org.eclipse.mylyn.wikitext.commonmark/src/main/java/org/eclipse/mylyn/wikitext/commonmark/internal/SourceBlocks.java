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

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.builder.NoOpDocumentBuilder;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;

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
		this.supportedBlocks = ImmutableList.copyOf(supportedBlocks);
	}

	private interface SourceBlockRunnable {

		void run(LineSequence lineSequence, SourceBlock sourceBlock);
	}

	public void createContext(final ProcessingContextBuilder contextBuilder, LineSequence lineSequence,
			Predicate<BlockContext> contextPredicate) {
		process(lineSequence, new SourceBlockRunnable() {

			@Override
			public void run(LineSequence lineSequence, SourceBlock sourceBlock) {
				sourceBlock.createContext(contextBuilder, lineSequence);
			}
		}, contextPredicate);
	}

	@Override
	public void createContext(ProcessingContextBuilder contextBuilder, LineSequence lineSequence) {
		createContext(contextBuilder, lineSequence, Predicates.<BlockContext> alwaysTrue());
	}

	@Override
	public void process(final ProcessingContext context, final DocumentBuilder builder, LineSequence lineSequence) {
		process(context, builder, lineSequence, Predicates.<BlockContext> alwaysTrue());
	}

	public void process(final ProcessingContext context, final DocumentBuilder builder, LineSequence lineSequence,
			Predicate<BlockContext> predicate) {
		process(lineSequence, new SourceBlockRunnable() {

			@Override
			public void run(LineSequence lineSequence, SourceBlock sourceBlock) {
				sourceBlock.process(context, builder, lineSequence);
			}
		}, predicate);
	}

	public List<SourceBlock> calculateSourceBlocks(final ProcessingContext context, LineSequence lineSequence,
			Predicate<BlockContext> predicate) {
		final List<SourceBlock> sourceBlocks = new ArrayList<>();
		final NoOpDocumentBuilder builder = new NoOpDocumentBuilder();
		process(lineSequence, new SourceBlockRunnable() {

			@Override
			public void run(LineSequence lineSequence, SourceBlock sourceBlock) {
				sourceBlocks.add(sourceBlock);
				sourceBlock.process(context, builder, lineSequence);
			}
		}, predicate);
		return sourceBlocks;
	}

	private void process(LineSequence lineSequence, SourceBlockRunnable runnable, Predicate<BlockContext> predicate) {
		SourceBlock currentBlock = null;
		SourceBlock previousBlock = null;
		while (lineSequence.getCurrentLine() != null) {
			previousBlock = currentBlock;
			currentBlock = selectBlock(lineSequence);
			if (!predicate.apply(new BlockContext(previousBlock, currentBlock, lineSequence))) {
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
