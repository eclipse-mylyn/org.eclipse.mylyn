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

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class SpanStrategies extends ElementStrategies<SpanType, SpanStrategy> {

	private static final Map<SpanType, List<SpanType>> spanTypeToAlternatives = createSpanTypeToAlternatives();

	private static Map<SpanType, List<SpanType>> createSpanTypeToAlternatives() {
		Map<SpanType, List<SpanType>> alternatives = Maps.newHashMap();
		alternatives.put(SpanType.BOLD, ImmutableList.of(SpanType.STRONG));
		alternatives.put(SpanType.STRONG, ImmutableList.of(SpanType.BOLD));
		alternatives.put(SpanType.CODE, ImmutableList.of(SpanType.MONOSPACE));
		alternatives.put(SpanType.EMPHASIS, ImmutableList.of(SpanType.ITALIC));
		alternatives.put(SpanType.INSERTED, ImmutableList.of(SpanType.UNDERLINED));
		alternatives.put(SpanType.ITALIC, ImmutableList.of(SpanType.EMPHASIS));
		alternatives.put(SpanType.MONOSPACE, ImmutableList.of(SpanType.CODE));
		alternatives.put(SpanType.STRONG, ImmutableList.of(SpanType.BOLD));
		return ImmutableMap.copyOf(alternatives);
	}

	SpanStrategies(Set<SpanType> elementTypes) {
		super(SpanType.class, elementTypes);
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

}
