/*******************************************************************************
 * Copyright (c) 2013, 2014 Tasktop Technologies and others.
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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class SpanStrategies extends ElementStrategies<SpanType, SpanStrategy, SpanHtmlElementStrategy> {

	private static final Map<SpanType, List<SpanType>> spanTypeToAlternatives = createSpanTypeToAlternatives();

	private static Map<SpanType, List<SpanType>> createSpanTypeToAlternatives() {
		Map<SpanType, List<SpanType>> alternatives = Maps.newHashMap();
		addAlternatives(alternatives, SpanType.BOLD, SpanType.STRONG);
		addAlternatives(alternatives, SpanType.STRONG, SpanType.BOLD);
		addAlternatives(alternatives, SpanType.CODE, SpanType.MONOSPACE);
		addAlternatives(alternatives, SpanType.EMPHASIS, SpanType.ITALIC);
		addAlternatives(alternatives, SpanType.INSERTED, SpanType.UNDERLINED);
		addAlternatives(alternatives, SpanType.ITALIC, SpanType.EMPHASIS);
		addAlternatives(alternatives, SpanType.MONOSPACE, SpanType.CODE);
		return ImmutableMap.copyOf(alternatives);
	}

	private static void addAlternatives(Map<SpanType, List<SpanType>> alternatives, SpanType spanType,
			SpanType... spanTypes) {
		checkState(!alternatives.containsKey(spanType), "Duplicate %s", spanType); //$NON-NLS-1$
		checkArgument(spanTypes.length > 0);
		alternatives.put(spanType, ImmutableList.copyOf(spanTypes));
	}

	SpanStrategies(Set<SpanType> elementTypes, List<SpanHtmlElementStrategy> spanElementStrategies) {
		super(SpanType.class, elementTypes, spanElementStrategies);
	}

	@Override
	void addImplicitElementTypes(Map<SpanType, SpanStrategy> blockStrategyByElementType, Set<SpanType> elementTypes) {
		// nothing to do
	}

	@Override
	SpanStrategy getSupportedStrategy(SpanType elementType) {
		return SupportedSpanStrategy.instance;
	}

	@Override
	SpanStrategy getUnsupportedElementStrategy() {
		return UnsupportedSpanStrategy.instance;
	}

	@Override
	SpanStrategy createSubstitutionElementStrategy(SpanType alternative) {
		return new SubstitutionSpanStrategy(alternative);
	}

	@Override
	Map<SpanType, List<SpanType>> getElementTypeToAlternatives() {
		return spanTypeToAlternatives;
	}

	@Override
	SpanStrategy getElementStrategy(SpanHtmlElementStrategy strategy) {
		return strategy.spanStrategy();
	}

}
