/*******************************************************************************
 * Copyright (c) 2013, 2015 Tasktop Technologies and others.
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

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import java.io.Writer;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentHandler;

public class HtmlSubsetDocumentBuilder extends DocumentBuilder {

	private final HtmlDocumentBuilder delegate;

	private BlockStrategies blockStrategies;

	private SpanStrategies spanStrategies;

	private final Stack<BlockStrategy> blockStrategyState = new Stack<BlockStrategy>();

	private final Stack<SpanStrategy> spanStrategyState = new Stack<SpanStrategy>();

	private final Stack<Integer> headingState = new Stack<Integer>();

	private int supportedHeadingLevel;

	private boolean implicitBlock;

	private BlockSeparator blockSeparator;

	private boolean supportsImages = true;

	public HtmlSubsetDocumentBuilder(Writer out, boolean formatting) {
		this(new HtmlDocumentBuilder(requireNonNull(out, "Must provide a writer"), formatting)); //$NON-NLS-1$
	}

	HtmlSubsetDocumentBuilder(HtmlDocumentBuilder delegate) {
		this.delegate = requireNonNull(delegate, "Must provide a delegate"); //$NON-NLS-1$
	}

	void setSupportedBlockTypes(Set<BlockType> blockTypes) {
		checkState(blockStrategyState.isEmpty());
		blockStrategies = new BlockStrategies(blockTypes);
	}

	void setSupportedSpanTypes(Set<SpanType> spanTypes, List<SpanHtmlElementStrategy> spanElementStrategies) {
		checkState(spanStrategyState.isEmpty());
		spanStrategies = new SpanStrategies(spanTypes, spanElementStrategies);
	}

	public void setElementNameOfSpanType(SpanType spanType, String elementName) {
		delegate.setElementNameOfSpanType(spanType, elementName);
	}

	void setSupportedHeadingLevel(int headingLevel) {
		this.supportedHeadingLevel = headingLevel;
	}

	void setDocumentHandler(HtmlDocumentHandler documentHandler) {
		delegate.setDocumentHandler(documentHandler);
	}

	void setSupportsImages(boolean supportsImagesFlag) {
		this.supportsImages = supportsImagesFlag;
	}

	boolean getSupportsImages() {
		return this.supportsImages;
	}

	@Override
	public void beginDocument() {
		delegate.beginDocument();
	}

	@Override
	public void endDocument() {
		flush();
		delegate.endDocument();
	}

	@Override
	public void flush() {
		assertCloseImplicitBlock();
		delegate.flush();
	}

	@Override
	public void beginBlock(BlockType type, Attributes attributes) {
		assertCloseImplicitBlock();
		emitBlockSeparator();
		pushBlockStrategy(type, attributes).beginBlock(delegate, type, attributes);
	}

	BlockStrategy pushBlockStrategy(BlockType type, Attributes attributes) {
		BlockStrategy strategy = blockStrategies.getStrategy(type, attributes);
		blockStrategyState.push(strategy);
		return strategy;
	}

	@Override
	public void endBlock() {
		BlockStrategy blockStrategy = blockStrategyState.pop();
		blockStrategy.endBlock(delegate);
		this.blockSeparator = blockStrategy.trailingSeparator();
	}

	@Override
	public void beginSpan(SpanType type, Attributes attributes) {
		assertOpenBlock();
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
		assertCloseImplicitBlock();
		emitBlockSeparator();
		headingState.push(level);
		if (headingLevelSupported(level)) {
			delegate.beginHeading(level, attributes);
		} else {
			beginBlock(BlockType.PARAGRAPH, attributes);
			beginSpan(SpanType.BOLD, new Attributes());
		}
	}

	private void emitBlockSeparator() {
		if (blockSeparator != null) {
			blockSeparator.emit(delegate);
			blockSeparator = null;
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
		assertOpenBlock();
		delegate.characters(text);
	}

	@Override
	public void entityReference(String entity) {
		assertOpenBlock();
		delegate.entityReference(entity);
	}

	@Override
	public void image(Attributes attributes, String url) {
		if (supportsImages) {
			assertOpenBlock();
			delegate.image(attributes, url);
		}
	}

	@Override
	public void link(Attributes attributes, String hrefOrHashName, String text) {
		assertOpenBlock();
		delegate.link(attributes, hrefOrHashName, text);
	}

	@Override
	public void imageLink(Attributes linkAttributes, Attributes imageAttributes, String href, String imageUrl) {
		assertOpenBlock();
		delegate.imageLink(linkAttributes, imageAttributes, href, imageUrl);
	}

	@Override
	public void acronym(String text, String definition) {
		assertOpenBlock();
		delegate.acronym(text, definition);
	}

	@Override
	public void lineBreak() {
		assertOpenBlock();
		delegate.lineBreak();
	}

	@Override
	public void charactersUnescaped(String literal) {
		delegate.charactersUnescaped(literal);
	}

	@Override
	public void horizontalRule() {
		delegate.horizontalRule();
	}

	void setXhtmlStrict(boolean xhtmlStrict) {
		delegate.setXhtmlStrict(xhtmlStrict);
	}

	private void assertOpenBlock() {
		if (blockStrategyState.isEmpty() && headingState.isEmpty()) {
			emitBlockSeparator();
			if (delegate.isXhtmlStrict()) {
				beginBlock(BlockType.PARAGRAPH, new Attributes());
				implicitBlock = true;
			}
		}
	}

	private void assertCloseImplicitBlock() {
		if (implicitBlock) {
			endBlock();
			implicitBlock = false;
		}
	}

	HtmlDocumentBuilder getDelegate() {
		return delegate;
	}
}
