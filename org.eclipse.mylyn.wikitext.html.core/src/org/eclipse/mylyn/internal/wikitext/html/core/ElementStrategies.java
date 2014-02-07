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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

abstract class ElementStrategies<ElementType extends Enum<ElementType>, ElementStrategy, HtmlElementStrategyType extends HtmlElementStrategy<ElementType>> {

	private Map<ElementType, ElementStrategy> elementStrategyByElementType;

	private final List<HtmlElementStrategyType> elementStrategies;

	ElementStrategies(Class<ElementType> elementTypeClass, Set<ElementType> elementTypes,
			List<HtmlElementStrategyType> elementStrategies) {
		checkNotNull(elementTypeClass);
		checkNotNull(elementTypes);
		this.elementStrategies = ImmutableList.copyOf(checkNotNull(elementStrategies));

		initialize(elementTypeClass, elementTypes);
	}

	public ElementStrategy getStrategy(ElementType elementType, Attributes attributes) {
		checkNotNull(elementType);

		for (HtmlElementStrategyType strategy : elementStrategies) {
			if (strategy.matcher().matches(elementType, attributes)) {
				return getElementStrategy(strategy);
			}
		}
		return checkNotNull(elementStrategyByElementType.get(elementType));
	}

	abstract ElementStrategy getElementStrategy(HtmlElementStrategyType strategy);

	private void initialize(Class<ElementType> elementTypeClass, Set<ElementType> elementTypes) {
		Map<ElementType, ElementStrategy> elementStrategyByElementType = Maps.newHashMap();
		for (ElementType elementType : elementTypes) {
			addSupportedElementType(elementStrategyByElementType, elementType);
		}
		addImplicitElementTypes(elementStrategyByElementType, elementTypes);

		Map<ElementType, ElementStrategy> alternativesByElementType = Maps.newHashMap();
		for (ElementType elementType : EnumSet.allOf(elementTypeClass)) {
			if (!elementStrategyByElementType.containsKey(elementType)) {
				alternativesByElementType.put(elementType,
						calculateFallBackElementStrategy(elementStrategyByElementType, elementType));
			}
		}
		elementStrategyByElementType.putAll(alternativesByElementType);

		this.elementStrategyByElementType = ImmutableMap.copyOf(elementStrategyByElementType);
	}

	abstract void addImplicitElementTypes(Map<ElementType, ElementStrategy> blockStrategyByElementType,
			Set<ElementType> elementTypes);

	void addSupportedElementType(Map<ElementType, ElementStrategy> elementStrategyByElementType, ElementType elementType) {
		elementStrategyByElementType.put(elementType, getSupportedStrategy(elementType));
	}

	abstract ElementStrategy getSupportedStrategy(ElementType elementType);

	abstract ElementStrategy getUnsupportedElementStrategy();

	abstract ElementStrategy createSubstitutionElementStrategy(ElementType alternative);

	abstract Map<ElementType, List<ElementType>> getElementTypeToAlternatives();

	private ElementStrategy calculateFallBackElementStrategy(Map<ElementType, ElementStrategy> strategies,
			ElementType elementType) {
		ElementStrategy elementStrategy = null;
		List<ElementType> alternatives = getElementTypeToAlternatives().get(elementType);
		if (alternatives != null) {
			for (ElementType alternative : alternatives) {
				if (strategies.containsKey(alternative)) {
					elementStrategy = createSubstitutionElementStrategy(alternative);
					break;
				}
			}
		}
		if (elementStrategy == null) {
			elementStrategy = getUnsupportedElementStrategy();
		}
		return elementStrategy;
	}

}
