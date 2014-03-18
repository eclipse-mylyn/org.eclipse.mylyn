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
import static com.google.common.base.Predicates.isNull;
import static com.google.common.base.Predicates.not;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.mylyn.internal.wikitext.core.util.css.CssParser;
import org.eclipse.mylyn.internal.wikitext.core.util.css.CssRule;
import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;

import com.google.common.base.CharMatcher;
import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
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

	@Override
	public SpanStrategy getStrategy(SpanType elementType, Attributes attributes) {
		SpanStrategy strategy = super.getStrategy(elementType, attributes);
		if (elementType == SpanType.SPAN && strategy instanceof UnsupportedSpanStrategy) {
			strategy = Objects.firstNonNull(calculateAlternateSpanStrategy(attributes), strategy);
		}
		return strategy;
	}

	private SpanStrategy calculateAlternateSpanStrategy(Attributes attributes) {
		List<SpanStrategy> strategies = Lists.newArrayList();
		String cssStyle = attributes.getCssStyle();
		if (cssStyle != null) {
			Iterator<CssRule> rules = new CssParser().createRuleIterator(cssStyle);
			while (rules.hasNext()) {
				CssRule rule = rules.next();
				if (rule.name.equals("font-weight") && rule.value.equals("bold")) { //$NON-NLS-1$ //$NON-NLS-2$
					strategies.add(calculateAlternateSpanStrategy(SpanType.BOLD));
				} else if (rule.name.equals("font-style") && rule.value.equals("italic")) { //$NON-NLS-1$ //$NON-NLS-2$
					strategies.add(calculateAlternateSpanStrategy(SpanType.ITALIC));
				} else if (rule.name.equals("font-family")) { //$NON-NLS-1$
					if (isFontFamilyMonospace(rule)) {
						strategies.add(new SubstitutionWithoutCssSpanStrategy(SpanType.MONOSPACE));
					}
				}
			}
		}
		strategies = ImmutableList.copyOf(FluentIterable.from(strategies).filter(not(isNull())));
		if (strategies.isEmpty()) {
			return null;
		} else if (strategies.size() == 1) {
			return strategies.get(0);
		}
		return new CompositeSpanStrategy(strategies);
	}

	private boolean isFontFamilyMonospace(CssRule rule) {
		for (String value : Splitter.on(',').trimResults(CharMatcher.WHITESPACE).split(rule.value)) {
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
