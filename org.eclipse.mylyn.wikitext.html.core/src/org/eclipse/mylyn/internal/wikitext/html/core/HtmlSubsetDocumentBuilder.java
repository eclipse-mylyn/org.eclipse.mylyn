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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.Writer;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentHandler;

public class HtmlSubsetDocumentBuilder extends DocumentBuilder {

	private final HtmlDocumentBuilder delegate;

	private BlockStrategies blockStrategies;

	private SpanStrategies spanStrategies;

	private final Stack<BlockStrategy> blockStrategyState = new Stack<BlockStrategy>();

	private final Stack<SpanStrategy> spanStrategyState = new Stack<SpanStrategy>();

	private final Stack<Integer> headingState = new Stack<Integer>();

	private int supportedHeadingLevel;

	public HtmlSubsetDocumentBuilder(Writer out, boolean formatting) {
		this(new HtmlDocumentBuilder(checkNotNull(out, "Must provide a writer"), formatting)); //$NON-NLS-1$
	}

	HtmlSubsetDocumentBuilder(HtmlDocumentBuilder delegate) {
		this.delegate = checkNotNull(delegate, "Must provide a delegate"); //$NON-NLS-1$
	}

	void setSupportedBlockTypes(Set<BlockType> blockTypes) {
		checkState(blockStrategyState.isEmpty());
		blockStrategies = new BlockStrategies(blockTypes);
	}

	void setSupportedSpanTypes(Set<SpanType> spanTypes, List<SpanHtmlElementStrategy> spanElementStrategies) {
		checkState(spanStrategyState.isEmpty());
		spanStrategies = new SpanStrategies(spanTypes, spanElementStrategies);
	}

	void setSupportedHeadingLevel(int headingLevel) {
		this.supportedHeadingLevel = headingLevel;
	}

	void setDocumentHandler(HtmlDocumentHandler documentHandler) {
		delegate.setDocumentHandler(documentHandler);
	}

	@Override
	public void beginDocument() {
		delegate.beginDocument();
	}

	@Override
	public void endDocument() {
		delegate.endDocument();
	}

	@Override
	public void beginBlock(BlockType type, Attributes attributes) {
		pushBlockStrategy(type, attributes).beginBlock(delegate, type, attributes);
	}

	BlockStrategy pushBlockStrategy(BlockType type, Attributes attributes) {
		BlockStrategy strategy = blockStrategies.getStrategy(type, attributes);
		blockStrategyState.push(strategy);
		return strategy;
	}

	@Override
	public void endBlock() {
		blockStrategyState.pop().endBlock(delegate);
	}

	@Override
	public void beginSpan(SpanType type, Attributes attributes) {
		SpanStrategy strategy = pushSpanStrategy(type, attributes);
		strategy.beginSpan(delegate, type, attributes);
	}

	SpanStrategy pushSpanStrategy(SpanType type, Attributes attributes) {
		SpanStrategy strategy = spanStrategies.getStrategy(type, attributes);
		spanStrategyState.push(strategy);
		return strategy;
	}

	@Override
	public void endSpan() {
		spanStrategyState.pop().endSpan(delegate);
	}

	@Override
	public void beginHeading(int level, Attributes attributes) {
		headingState.push(level);
		if (headingLevelSupported(level)) {
			delegate.beginHeading(level, attributes);
		} else {
			beginBlock(BlockType.PARAGRAPH, attributes);
			beginSpan(SpanType.BOLD, new Attributes());
		}
	}

	boolean headingLevelSupported(int level) {
		return supportedHeadingLevel > 0 && level <= supportedHeadingLevel;
	}

	@Override
	public void endHeading() {
		Integer level = headingState.pop();
		if (headingLevelSupported(level)) {
			delegate.endHeading();
		} else {
			endSpan();
			endBlock();
		}
	}

	@Override
	public void characters(String text) {
		delegate.characters(text);
	}

	@Override
	public void entityReference(String entity) {
		delegate.entityReference(entity);
	}

	@Override
	public void image(Attributes attributes, String url) {
		delegate.image(attributes, url);
	}

	@Override
	public void link(Attributes attributes, String hrefOrHashName, String text) {
		delegate.link(attributes, hrefOrHashName, text);
	}

	@Override
	public void imageLink(Attributes linkAttributes, Attributes imageAttributes, String href, String imageUrl) {
		delegate.imageLink(linkAttributes, imageAttributes, href, imageUrl);
	}

	@Override
	public void acronym(String text, String definition) {
		delegate.acronym(text, definition);
	}

	@Override
	public void lineBreak() {
		delegate.lineBreak();
	}

	@Override
	public void charactersUnescaped(String literal) {
		delegate.charactersUnescaped(literal);
	}
}
