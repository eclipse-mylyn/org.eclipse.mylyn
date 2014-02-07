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
import static com.google.common.base.Preconditions.checkState;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

class BlockStrategies extends ElementStrategies<BlockType, BlockStrategy, HtmlElementStrategy<BlockType>> {

	private static final Map<BlockType, List<BlockType>> blockTypeToAlternatives = createBlockTypeToAlternatives();

	private static Map<BlockType, List<BlockType>> createBlockTypeToAlternatives() {
		Map<BlockType, List<BlockType>> alternatives = Maps.newHashMap();
		addAlternatives(alternatives, BlockType.PARAGRAPH, BlockType.DIV);
		addAlternatives(alternatives, BlockType.BULLETED_LIST, BlockType.NUMERIC_LIST);
		addAlternatives(alternatives, BlockType.NUMERIC_LIST, BlockType.BULLETED_LIST);
		addAlternatives(alternatives, BlockType.CODE, BlockType.PREFORMATTED, BlockType.PARAGRAPH, BlockType.DIV);
		addAlternatives(alternatives, BlockType.DEFINITION_LIST, BlockType.NUMERIC_LIST, BlockType.BULLETED_LIST);
		addAlternatives(alternatives, BlockType.DIV, BlockType.PARAGRAPH);
		addAlternatives(alternatives, BlockType.FOOTNOTE, BlockType.PARAGRAPH, BlockType.DIV);
		addAlternatives(alternatives, BlockType.INFORMATION, BlockType.PARAGRAPH, BlockType.DIV);
		addAlternatives(alternatives, BlockType.LIST_ITEM, BlockType.PARAGRAPH, BlockType.DIV);
		addAlternatives(alternatives, BlockType.DEFINITION_ITEM, BlockType.PARAGRAPH, BlockType.DIV);
		addAlternatives(alternatives, BlockType.DEFINITION_TERM, BlockType.PARAGRAPH, BlockType.DIV);
		addAlternatives(alternatives, BlockType.NOTE, BlockType.PARAGRAPH, BlockType.DIV);
		addAlternatives(alternatives, BlockType.PANEL, BlockType.PARAGRAPH, BlockType.DIV);
		addAlternatives(alternatives, BlockType.PREFORMATTED, BlockType.CODE, BlockType.PARAGRAPH, BlockType.DIV);
		addAlternatives(alternatives, BlockType.QUOTE, BlockType.PARAGRAPH, BlockType.DIV);
		addAlternatives(alternatives, BlockType.TABLE_CELL_HEADER, BlockType.PARAGRAPH, BlockType.DIV);
		addAlternatives(alternatives, BlockType.TABLE_CELL_NORMAL, BlockType.PARAGRAPH, BlockType.DIV);
		addAlternatives(alternatives, BlockType.TIP, BlockType.PARAGRAPH, BlockType.DIV);
		addAlternatives(alternatives, BlockType.WARNING, BlockType.PARAGRAPH, BlockType.DIV);
		return ImmutableMap.copyOf(alternatives);
	}

	private static void addAlternatives(Map<BlockType, List<BlockType>> alternatives, BlockType blockType,
			BlockType... blockTypes) {
		checkState(!alternatives.containsKey(blockType), "Duplicate %s", blockType); //$NON-NLS-1$
		checkArgument(blockTypes.length > 0);
		alternatives.put(blockType, ImmutableList.copyOf(blockTypes));
	}

	BlockStrategies(Set<BlockType> blockTypes) {
		super(BlockType.class, blockTypes, Collections.<HtmlElementStrategy<BlockType>> emptyList());
		checkArgument(!blockTypes.isEmpty());
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

	@Override
	BlockStrategy getElementStrategy(HtmlElementStrategy<BlockType> strategy) {
		throw new UnsupportedOperationException();
	}

}
