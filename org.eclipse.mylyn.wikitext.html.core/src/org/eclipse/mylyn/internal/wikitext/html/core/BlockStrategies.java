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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

class BlockStrategies {

	private static final Map<BlockType, List<BlockType>> blockTypeToAlternatives = Maps.newHashMap();

	static {
		blockTypeToAlternatives.put(BlockType.BULLETED_LIST, Lists.newArrayList(BlockType.NUMERIC_LIST));
		blockTypeToAlternatives.put(BlockType.NUMERIC_LIST, Lists.newArrayList(BlockType.BULLETED_LIST));
		blockTypeToAlternatives.put(BlockType.CODE, Lists.newArrayList(BlockType.PREFORMATTED, BlockType.PARAGRAPH));
		blockTypeToAlternatives.put(BlockType.DEFINITION_LIST,
				Lists.newArrayList(BlockType.NUMERIC_LIST, BlockType.BULLETED_LIST));
		blockTypeToAlternatives.put(BlockType.DIV, Lists.newArrayList(BlockType.PARAGRAPH));
		blockTypeToAlternatives.put(BlockType.FOOTNOTE,
				Lists.newArrayList(BlockType.BULLETED_LIST, BlockType.NUMERIC_LIST));
		blockTypeToAlternatives.put(BlockType.INFORMATION, Lists.newArrayList(BlockType.PARAGRAPH));
		blockTypeToAlternatives.put(BlockType.LIST_ITEM, Lists.newArrayList(BlockType.PARAGRAPH));
		blockTypeToAlternatives.put(BlockType.NOTE, Lists.newArrayList(BlockType.PARAGRAPH));
		blockTypeToAlternatives.put(BlockType.PANEL, Lists.newArrayList(BlockType.PARAGRAPH));
		blockTypeToAlternatives.put(BlockType.PREFORMATTED, Lists.newArrayList(BlockType.CODE, BlockType.PARAGRAPH));
		blockTypeToAlternatives.put(BlockType.QUOTE, Lists.newArrayList(BlockType.PARAGRAPH));
		blockTypeToAlternatives.put(BlockType.TABLE_CELL_HEADER, Lists.newArrayList(BlockType.PARAGRAPH));
		blockTypeToAlternatives.put(BlockType.TABLE_CELL_NORMAL, Lists.newArrayList(BlockType.PARAGRAPH));
		blockTypeToAlternatives.put(BlockType.TIP, Lists.newArrayList(BlockType.PARAGRAPH));
		blockTypeToAlternatives.put(BlockType.WARNING, Lists.newArrayList(BlockType.PARAGRAPH));
	}

	private Map<BlockType, BlockStrategy> blockStrategyByBlockType;

	BlockStrategies(Set<BlockType> blockTypes) {
		checkNotNull(blockTypes);
		checkArgument(!blockTypes.isEmpty());

		initialize(blockTypes);
	}

	public BlockStrategy getBlockStrategy(BlockType blockType) {
		return checkNotNull(blockStrategyByBlockType.get(checkNotNull(blockType)));
	}

	private void initialize(Set<BlockType> blockTypes) {
		Map<BlockType, BlockStrategy> blockStrategyByBlockType = Maps.newHashMap();
		for (BlockType blockType : blockTypes) {
			addSupportedBlockType(blockStrategyByBlockType, blockType);
		}
		addImplicitBlockTypes(blockStrategyByBlockType, blockTypes);

		Map<BlockType, BlockStrategy> alternativesByBlockType = Maps.newHashMap();
		for (BlockType blockType : BlockType.values()) {
			if (!blockStrategyByBlockType.containsKey(blockType)) {
				alternativesByBlockType.put(blockType, calculateFallBackBlockType(blockStrategyByBlockType, blockType));
			}
		}
		blockStrategyByBlockType.putAll(alternativesByBlockType);

		this.blockStrategyByBlockType = ImmutableMap.copyOf(blockStrategyByBlockType);
	}

	private void addImplicitBlockTypes(Map<BlockType, BlockStrategy> blockStrategyByBlockType, Set<BlockType> blockTypes) {
		if (blockTypes.contains(BlockType.TABLE)) {
			addSupportedBlockType(blockStrategyByBlockType, BlockType.TABLE_ROW);
			addSupportedBlockType(blockStrategyByBlockType, BlockType.TABLE_CELL_HEADER);
			addSupportedBlockType(blockStrategyByBlockType, BlockType.TABLE_CELL_NORMAL);
		}
		if (blockTypes.contains(BlockType.BULLETED_LIST) || blockTypes.contains(BlockType.NUMERIC_LIST)) {
			addSupportedBlockType(blockStrategyByBlockType, BlockType.LIST_ITEM);
		}
		if (blockTypes.contains(BlockType.DEFINITION_LIST)) {
			addSupportedBlockType(blockStrategyByBlockType, BlockType.DEFINITION_ITEM);
			addSupportedBlockType(blockStrategyByBlockType, BlockType.DEFINITION_TERM);
		}
	}

	private void addSupportedBlockType(Map<BlockType, BlockStrategy> blockStrategyByBlockType, BlockType blockType) {
		blockStrategyByBlockType.put(blockType, SupportedBlockStrategy.instance);
	}

	private BlockStrategy calculateFallBackBlockType(Map<BlockType, BlockStrategy> strategies, BlockType blockType) {
		BlockStrategy blockStrategy = null;
		List<BlockType> alternatives = blockTypeToAlternatives.get(blockType);
		if (alternatives != null) {
			for (BlockType alternative : alternatives) {
				if (strategies.containsKey(alternative)) {
					blockStrategy = new SubstitutionBlockStrategy(alternative);
					break;
				}
			}
		}
		if (blockStrategy == null) {
			blockStrategy = UnsupportedBlockStrategy.instance;
		}
		return blockStrategy;
	}

}
