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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.css.CssParser;
import org.eclipse.mylyn.wikitext.parser.css.CssRule;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class SpanStrategies extends ElementStrategies<SpanType, SpanStrategy, SpanHtmlElementStrategy> {

	private static final Map<SpanType, List<SpanType>> spanTypeToAlternatives = createSpanTypeToAlternatives();

	private static Map<SpanType, List<SpanType>> createSpanTypeToAlternatives() {
		Map<SpanType, List<SpanType>> alternatives = new HashMap<>();
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
	SpanStrategy getUnsupportedElementStrategy(SpanType elementType) {
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

	@Override
	public SpanStrategy getStrategy(SpanType elementType, Attributes attributes) {
		SpanStrategy strategy = super.getStrategy(elementType, attributes);
		if (elementType == SpanType.SPAN && strategy instanceof UnsupportedSpanStrategy) {
			SpanStrategy alternateStrategy = calculateAlternateSpanStrategy(attributes);
			if (alternateStrategy != null) {
				strategy = alternateStrategy;
			}
		}
		return strategy;
	}

	private SpanStrategy calculateAlternateSpanStrategy(Attributes attributes) {
		List<SpanStrategy> strategies = new ArrayList<>();
		String cssStyle = attributes.getCssStyle();
		if (cssStyle != null) {
			Iterator<CssRule> rules = new CssParser().createRuleIterator(cssStyle);
			while (rules.hasNext()) {
				CssRule rule = rules.next();
				if (rule.name.equals("font-weight") && rule.value.equals("bold")) { //$NON-NLS-1$ //$NON-NLS-2$
					strategies.add(calculateAlternateSpanStrategy(SpanType.BOLD));
				} else if (rule.name.equals("font-style") && rule.value.equals("italic")) { //$NON-NLS-1$ //$NON-NLS-2$
					strategies.add(calculateAlternateSpanStrategy(SpanType.ITALIC));
				} else if (rule.name.equals("text-decoration") && rule.value.equalsIgnoreCase("underline")) { //$NON-NLS-1$ //$NON-NLS-2$
					strategies.add(calculateAlternateSpanStrategy(SpanType.UNDERLINED));
				} else if (rule.name.equals("text-decoration") && rule.value.equalsIgnoreCase("line-through")) { //$NON-NLS-1$ //$NON-NLS-2$
					strategies.add(calculateAlternateSpanStrategy(SpanType.DELETED));
				} else if (rule.name.equals("font-family") && isFontFamilyMonospace(rule)) { //$NON-NLS-1$
					strategies.add(new SubstitutionWithoutCssSpanStrategy(SpanType.MONOSPACE));
				}
			}
		}
		strategies = ImmutableList.copyOf(FluentIterable.from(strategies).filter(Objects::nonNull));
		if (strategies.isEmpty()) {
			return null;
		} else if (strategies.size() == 1) {
			return strategies.get(0);
		}
		return new CompositeSpanStrategy(strategies);
	}

	private boolean isFontFamilyMonospace(CssRule rule) {
		for (String value : Splitter.on(',').trimResults(CharMatcher.whitespace()).split(rule.value)) {
			if ("monospace".equalsIgnoreCase(value)) { //$NON-NLS-1$
				return true;
			}
		}
		return false;
	}

	private SpanStrategy calculateAlternateSpanStrategy(SpanType spanType) {
		SpanStrategy strategy = super.getStrategy(spanType, new Attributes());
		if (strategy instanceof SupportedSpanStrategy) {
			return new SubstitutionWithoutCssSpanStrategy(spanType);
		} else if (strategy instanceof SubstitutionSpanStrategy) {
			return new SubstitutionWithoutCssSpanStrategy(((SubstitutionSpanStrategy) strategy).getType());
		}
		return null;
	}
}
