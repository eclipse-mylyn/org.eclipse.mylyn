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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

class BlockStrategies extends ElementStrategies<BlockType, BlockStrategy> {

	private static final Map<BlockType, List<BlockType>> blockTypeToAlternatives = createBlockTypeToAlternatives();

	private static Map<BlockType, List<BlockType>> createBlockTypeToAlternatives() {
		Map<BlockType, List<BlockType>> alternatives = Maps.newHashMap();
		alternatives.put(BlockType.BULLETED_LIST, ImmutableList.of(BlockType.NUMERIC_LIST));
		alternatives.put(BlockType.NUMERIC_LIST, ImmutableList.of(BlockType.BULLETED_LIST));
		alternatives.put(BlockType.CODE, ImmutableList.of(BlockType.PREFORMATTED, BlockType.PARAGRAPH));
		alternatives.put(BlockType.DEFINITION_LIST, ImmutableList.of(BlockType.NUMERIC_LIST, BlockType.BULLETED_LIST));
		alternatives.put(BlockType.DIV, ImmutableList.of(BlockType.PARAGRAPH));
		alternatives.put(BlockType.FOOTNOTE, ImmutableList.of(BlockType.BULLETED_LIST, BlockType.NUMERIC_LIST));
		alternatives.put(BlockType.INFORMATION, ImmutableList.of(BlockType.PARAGRAPH));
		alternatives.put(BlockType.LIST_ITEM, ImmutableList.of(BlockType.PARAGRAPH));
		alternatives.put(BlockType.NOTE, ImmutableList.of(BlockType.PARAGRAPH));
		alternatives.put(BlockType.PANEL, ImmutableList.of(BlockType.PARAGRAPH));
		alternatives.put(BlockType.PREFORMATTED, ImmutableList.of(BlockType.CODE, BlockType.PARAGRAPH));
		alternatives.put(BlockType.QUOTE, ImmutableList.of(BlockType.PARAGRAPH));
		alternatives.put(BlockType.TABLE_CELL_HEADER, ImmutableList.of(BlockType.PARAGRAPH));
		alternatives.put(BlockType.TABLE_CELL_NORMAL, ImmutableList.of(BlockType.PARAGRAPH));
		alternatives.put(BlockType.TIP, ImmutableList.of(BlockType.PARAGRAPH));
		alternatives.put(BlockType.WARNING, ImmutableList.of(BlockType.PARAGRAPH));
		return ImmutableMap.copyOf(alternatives);
	}

	BlockStrategies(Set<BlockType> blockTypes) {
		super(BlockType.class, blockTypes);
	}

	@Override
	void addImplicitElementTypes(Map<BlockType, BlockStrategy> elementStrategyByElementType, Set<BlockType> elementTypes) {
		if (elementTypes.contains(BlockType.TABLE)) {
			addSupportedElementType(elementStrategyByElementType, BlockType.TABLE_ROW);
			addSupportedElementType(elementStrategyByElementType, BlockType.TABLE_CELL_HEADER);
			addSupportedElementType(elementStrategyByElementType, BlockType.TABLE_CELL_NORMAL);
		}
		if (elementTypes.contains(BlockType.BULLETED_LIST) || elementTypes.contains(BlockType.NUMERIC_LIST)) {
			addSupportedElementType(elementStrategyByElementType, BlockType.LIST_ITEM);
		}
		if (elementTypes.contains(BlockType.DEFINITION_LIST)) {
			addSupportedElementType(elementStrategyByElementType, BlockType.DEFINITION_ITEM);
			addSupportedElementType(elementStrategyByElementType, BlockType.DEFINITION_TERM);
		}
	}

	@Override
	BlockStrategy getSupportedStrategy(BlockType elementType) {
		return SupportedBlockStrategy.instance;
	}

	@Override
	BlockStrategy getUnsupportedElementStrategy() {
		return UnsupportedBlockStrategy.instance;
	}

	@Override
	BlockStrategy createSubstitutionElementStrategy(BlockType alternative) {
		return new SubstitutionBlockStrategy(alternative);
	}

	@Override
	Map<BlockType, List<BlockType>> getElementTypeToAlternatives() {
		return blockTypeToAlternatives;
	}

}
