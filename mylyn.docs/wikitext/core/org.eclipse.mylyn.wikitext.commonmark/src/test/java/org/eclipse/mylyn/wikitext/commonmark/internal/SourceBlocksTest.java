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

package org.eclipse.mylyn.wikitext.commonmark.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.builder.EventDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.builder.event.BeginBlockEvent;
import org.eclipse.mylyn.wikitext.parser.builder.event.CharactersEvent;
import org.eclipse.mylyn.wikitext.parser.builder.event.DocumentBuilderEvent;
import org.eclipse.mylyn.wikitext.parser.builder.event.EndBlockEvent;
import org.junit.Test;

public class SourceBlocksTest {

	private final SourceBlock block1 = mockBlock(BlockType.QUOTE, "b1");

	private final SourceBlock block2 = mockBlock(BlockType.PARAGRAPH, "b2");

	private final SourceBlocks sourceBlocks = new SourceBlocks(block1, block2);

	@Test(expected = NullPointerException.class)
	public void requiresBlocks() {
		assertNotNull(new SourceBlocks((SourceBlock[]) null));
	}

	@Test(expected = NullPointerException.class)
	public void requiresBlocksCollection() {
		assertNotNull(new SourceBlocks((List<SourceBlock>) null));
	}

	@Test
	public void canStart() {
		assertTrue(sourceBlocks.canStart(LineSequence.create("any")));
		assertTrue(sourceBlocks.canStart(LineSequence.create("")));
	}

	@Test
	public void process() {
		EventDocumentBuilder builder = new EventDocumentBuilder();
		sourceBlocks.process(ProcessingContext.builder().build(), builder,
				LineSequence.create("one\nb2\nmore\n\nb1 and\n\n\nb2"));
		List<DocumentBuilderEvent> expectedEvents = List.of(//
				new BeginBlockEvent(BlockType.PARAGRAPH, new Attributes()), //
				new CharactersEvent("b2"), //
				new CharactersEvent("more"), //
				new EndBlockEvent(), //
				new BeginBlockEvent(BlockType.QUOTE, new Attributes()), //
				new CharactersEvent("b1 and"), //
				new EndBlockEvent(), //
				new BeginBlockEvent(BlockType.PARAGRAPH, new Attributes()), //
				new CharactersEvent("b2"), //
				new EndBlockEvent());
		assertEquals(
				builder.getDocumentBuilderEvents()
						.getEvents()
						.stream()
						.map(x -> x.toString())
						.collect(Collectors.joining("\n")),
				expectedEvents, builder.getDocumentBuilderEvents().getEvents());
	}

	private SourceBlock mockBlock(final BlockType blockType, final String startString) {
		return new SourceBlock() {

			@Override
			public void process(ProcessingContext context, DocumentBuilder builder, LineSequence lineSequence) {
				builder.beginBlock(blockType, new Attributes());
				for (Line line : lineSequence.with(LinePredicates.empty().negate())) {
					builder.characters(line.getText());
				}
				builder.endBlock();
			}

			@Override
			public boolean canStart(LineSequence lineSequence) {
				return lineSequence.getCurrentLine() != null
						&& lineSequence.getCurrentLine().getText().startsWith(startString);
			}
		};
	}
}
