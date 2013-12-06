/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.html.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.collect.Sets;

public class BlockStrategiesTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void createNull() {
		thrown.expect(NullPointerException.class);
		new BlockStrategies(null);
	}

	@Test
	public void createEmpty() {
		thrown.expect(IllegalArgumentException.class);
		new BlockStrategies(Sets.<BlockType> newHashSet());
	}

	@Test
	public void createNonEmpty() {
		BlockStrategies strategies = new BlockStrategies(Sets.newHashSet(BlockType.PARAGRAPH, BlockType.CODE));
		assertSupported(strategies, BlockType.PARAGRAPH);
		assertSupported(strategies, BlockType.CODE);
		for (BlockType blockType : BlockType.values()) {
			assertNotNull(strategies.getStrategy(blockType));
		}
	}

	@Test
	public void alternatives() {
		BlockStrategies strategies = new BlockStrategies(Sets.newHashSet(BlockType.PARAGRAPH));
		assertTrue(strategies.getStrategy(BlockType.CODE) instanceof SubstitutionBlockStrategy);
	}

	@Test
	public void unsupportedByDefault() {
		BlockStrategies strategies = new BlockStrategies(Sets.newHashSet(BlockType.PARAGRAPH));
		for (BlockType blockType : BlockType.values()) {
			if (blockType == BlockType.PARAGRAPH) {
				continue;
			}
			assertUnsupported(strategies, blockType);
		}
	}

	@Test
	public void table() {
		BlockStrategies strategies = new BlockStrategies(Sets.newHashSet(BlockType.TABLE));
		assertSupported(strategies, BlockType.TABLE);
		assertSupported(strategies, BlockType.TABLE_ROW);
		assertSupported(strategies, BlockType.TABLE_CELL_HEADER);
		assertSupported(strategies, BlockType.TABLE_CELL_NORMAL);
	}

	@Test
	public void lists() {
		for (BlockType listType : new BlockType[] { BlockType.BULLETED_LIST, BlockType.NUMERIC_LIST }) {
			BlockStrategies strategies = new BlockStrategies(Sets.newHashSet(listType));
			assertSupported(strategies, listType);
			assertSupported(strategies, BlockType.LIST_ITEM);
		}
	}

	@Test
	public void definitionList() {
		BlockStrategies strategies = new BlockStrategies(Sets.newHashSet(BlockType.DEFINITION_LIST));

		assertSupported(strategies, BlockType.DEFINITION_LIST);
		assertSupported(strategies, BlockType.DEFINITION_ITEM);
		assertSupported(strategies, BlockType.DEFINITION_TERM);
	}

	private void assertUnsupported(BlockStrategies strategies, BlockType blockType) {
		BlockStrategy blockStrategy = strategies.getStrategy(blockType);
		assertNotNull(blockStrategy);
		assertFalse(blockStrategy instanceof SupportedBlockStrategy);
	}

	private void assertSupported(BlockStrategies strategies, BlockType blockType) {
		assertTrue(strategies.getStrategy(blockType) instanceof SupportedBlockStrategy);
	}
}
