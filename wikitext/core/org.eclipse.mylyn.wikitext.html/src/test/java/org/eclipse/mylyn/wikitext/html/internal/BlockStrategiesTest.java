/*******************************************************************************
 * Copyright (c) 2013, 2021 Tasktop Technologies and others.
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

package org.eclipse.mylyn.wikitext.html.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class BlockStrategiesTest {

	@Test(expected = NullPointerException.class)
	public void createNull() {
		new BlockStrategies(null);
	}

	@Test
	public void createEmpty() {
		BlockStrategies strategies = new BlockStrategies(new HashSet<BlockType>());
		assertUnsupported(strategies, BlockType.PARAGRAPH);
	}

	@Test
	public void createNonEmpty() {
		BlockStrategies strategies = new BlockStrategies(
				new HashSet<>(Arrays.asList(BlockType.PARAGRAPH, BlockType.CODE)));
		assertSupported(strategies, BlockType.PARAGRAPH);
		assertSupported(strategies, BlockType.CODE);
		for (BlockType blockType : BlockType.values()) {
			assertNotNull(strategies.getStrategy(blockType, new Attributes()));
		}
	}

	@Test
	public void alternatives() {
		BlockStrategies strategies = new BlockStrategies(new HashSet<>(Arrays.asList(BlockType.PARAGRAPH)));
		assertTrue(strategies.getStrategy(BlockType.CODE, new Attributes()) instanceof SubstitutionBlockStrategy);
	}

	@Test
	public void unsupportedByDefault() {
		BlockStrategies strategies = new BlockStrategies(new HashSet<>(Arrays.asList(BlockType.PARAGRAPH)));
		for (BlockType blockType : BlockType.values()) {
			if (blockType == BlockType.PARAGRAPH) {
				continue;
			}
			assertUnsupported(strategies, blockType);
		}
	}

	@Test
	public void table() {
		BlockStrategies strategies = new BlockStrategies(new HashSet<>(Arrays.asList(BlockType.TABLE)));
		assertSupported(strategies, BlockType.TABLE);
		assertSupported(strategies, BlockType.TABLE_ROW);
		assertSupported(strategies, BlockType.TABLE_CELL_HEADER);
		assertSupported(strategies, BlockType.TABLE_CELL_NORMAL);
	}

	@Test
	public void lists() {
		for (BlockType listType : new BlockType[] { BlockType.BULLETED_LIST, BlockType.NUMERIC_LIST }) {
			BlockStrategies strategies = new BlockStrategies(new HashSet<>(Arrays.asList(listType)));
			assertSupported(strategies, listType);
			assertSupported(strategies, BlockType.LIST_ITEM);
		}
	}

	@Test
	public void definitionList() {
		BlockStrategies strategies = new BlockStrategies(new HashSet<>(Arrays.asList(BlockType.DEFINITION_LIST)));

		assertSupported(strategies, BlockType.DEFINITION_LIST);
		assertSupported(strategies, BlockType.DEFINITION_ITEM);
		assertSupported(strategies, BlockType.DEFINITION_TERM);
	}

	@Test
	public void fallBackToUnsupported() {
		BlockStrategies strategies = new BlockStrategies(new HashSet<>(Arrays.asList(BlockType.PARAGRAPH)));
		BlockStrategy strategy = strategies.getStrategy(BlockType.TABLE_ROW, new Attributes());
		assertNotNull(strategy);
		assertEquals(UnsupportedBlockStrategy.class, strategy.getClass());
	}

	@Test
	public void fallBackToNoOp() {
		BlockStrategies strategies = new BlockStrategies(new HashSet<>(Arrays.asList(BlockType.PARAGRAPH)));
		List<BlockType> unsupportedBlockTypes = ImmutableList.of(BlockType.TABLE, BlockType.BULLETED_LIST,
				BlockType.NUMERIC_LIST, BlockType.DEFINITION_LIST);
		for (BlockType blockType : unsupportedBlockTypes) {
			BlockStrategy strategy = strategies.getStrategy(blockType, new Attributes());
			assertNotNull(strategy);
			assertEquals(NoOpBlockStrategy.class, strategy.getClass());
		}
	}

	@Test
	public void fallBack() {
		for (BlockType supportedType : ImmutableList.of(BlockType.PARAGRAPH, BlockType.DIV)) {

			List<BlockType> fallBackTypes = ImmutableList.of(BlockType.CODE, BlockType.DEFINITION_ITEM,
					BlockType.DEFINITION_TERM, BlockType.FOOTNOTE, BlockType.INFORMATION, BlockType.LIST_ITEM,
					BlockType.NOTE, BlockType.PREFORMATTED, BlockType.QUOTE, BlockType.TABLE_CELL_HEADER,
					BlockType.TABLE_CELL_NORMAL, BlockType.TIP, BlockType.WARNING);
			for (BlockType blockType : fallBackTypes) {
				assertFallback(supportedType, blockType);
			}
		}
	}

	@Test
	public void fallBackParagraphToDiv() {
		assertFallback(BlockType.DIV, BlockType.PARAGRAPH);
	}

	@Test
	public void fallBackDivToParagraph() {
		assertFallback(BlockType.PARAGRAPH, BlockType.DIV);
	}

	protected void assertFallback(BlockType supportedType, BlockType blockType) {
		BlockStrategies strategies = new BlockStrategies(new HashSet<>(Arrays.asList(supportedType)));
		BlockStrategy strategy = strategies.getStrategy(blockType, new Attributes());
		assertNotNull(strategy);
		assertEquals(blockType.name(), SubstitutionBlockStrategy.class, strategy.getClass());
		assertEquals(blockType.name(), supportedType, ((SubstitutionBlockStrategy) strategy).getBlockType());
	}

	private void assertUnsupported(BlockStrategies strategies, BlockType blockType) {
		BlockStrategy blockStrategy = strategies.getStrategy(blockType, new Attributes());
		assertNotNull(blockStrategy);
		assertFalse(blockStrategy instanceof SupportedBlockStrategy);
	}

	private void assertSupported(BlockStrategies strategies, BlockType blockType) {
		assertTrue(strategies.getStrategy(blockType, new Attributes()) instanceof SupportedBlockStrategy);
	}
}
