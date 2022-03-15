/*******************************************************************************
 * Copyright (c) 2013, 2014 Tasktop Technologies and others.
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
import static java.util.Objects.requireNonNull;

import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.mylyn.wikitext.html.HtmlLanguage;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentHandler;

public class HtmlSubsetLanguage extends HtmlLanguage {

	private final Set<BlockType> supportedBlockTypes;

	private final Set<SpanType> supportedSpanTypes;

	private final int headingLevel;

	private final HtmlDocumentHandler documentHandler;

	private final List<SpanHtmlElementStrategy> spanElementStrategies;

	private final Map<SpanType, String> tagNameSubstitutions;

	private final boolean xhtmlStrict;

	private final boolean supportsImages;

	public HtmlSubsetLanguage(String name, HtmlDocumentHandler documentHandler, int headingLevel,
			Set<BlockType> blockTypes, Set<SpanType> spanTypes, Map<SpanType, String> tagNameSubstitutions,
			List<SpanHtmlElementStrategy> spanElementStrategies, boolean xhtmlStrict, boolean supportsImages) {
		setName(requireNonNull(name));
		this.documentHandler = documentHandler;
		checkArgument(headingLevel >= 0 && headingLevel <= 6, "headingLevel must be between 0 and 6"); //$NON-NLS-1$
		this.headingLevel = headingLevel;
		this.supportedBlockTypes = Set.copyOf(requireNonNull(blockTypes));
		this.supportedSpanTypes = Set.copyOf(requireNonNull(spanTypes));
		this.tagNameSubstitutions = Map.copyOf(requireNonNull(tagNameSubstitutions));
		this.spanElementStrategies = List.copyOf(requireNonNull(spanElementStrategies));
		this.xhtmlStrict = xhtmlStrict;
		this.supportsImages = supportsImages;

		assertSubstitutedAreSupported();
	}

	public Set<BlockType> getSupportedBlockTypes() {
		return supportedBlockTypes;
	}

	public Set<SpanType> getSupportedSpanTypes() {
		return supportedSpanTypes;
	}

	public int getSupportedHeadingLevel() {
		return headingLevel;
	}

	public Map<SpanType, String> getTagNameSubstitutions() {
		return tagNameSubstitutions;
	}

	@Override
	public HtmlSubsetDocumentBuilder createDocumentBuilder(Writer out, boolean formatting) {
		HtmlSubsetDocumentBuilder builder = new HtmlSubsetDocumentBuilder(out, formatting);
		builder.setSupportedHeadingLevel(headingLevel);
		builder.setSupportedSpanTypes(supportedSpanTypes, spanElementStrategies);
		builder.setSupportedBlockTypes(supportedBlockTypes);
		builder.setXhtmlStrict(xhtmlStrict);
		builder.setSupportsImages(supportsImages);
		addSpanTagNameSubstitutions(builder);
		if (documentHandler != null) {
			builder.setDocumentHandler(documentHandler);
		}
		return builder;
	}

	private void addSpanTagNameSubstitutions(HtmlSubsetDocumentBuilder builder) {
		for (Entry<SpanType, String> entry : tagNameSubstitutions.entrySet()) {
			builder.setElementNameOfSpanType(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public HtmlSubsetLanguage clone() {
		HtmlSubsetLanguage copy = new HtmlSubsetLanguage(getName(), documentHandler, headingLevel, supportedBlockTypes,
				supportedSpanTypes, tagNameSubstitutions, spanElementStrategies, xhtmlStrict, supportsImages);
		copy.setFileExtensions(getFileExtensions());
		copy.setExtendsLanguage(getExtendsLanguage());
		copy.setParseCleansHtml(isParseCleansHtml());
		return copy;
	}

	private void assertSubstitutedAreSupported() {
		for (SpanType spanType : tagNameSubstitutions.keySet()) {
			checkState(supportedSpanTypes.contains(spanType),
					"SpanType [%s] is unsupported. Cannot add substitution to unsupported span types.", spanType); //$NON-NLS-1$
		}
	}

	public boolean isXhtmlStrict() {
		return xhtmlStrict;
	}

	public boolean getSupportsImages() {
		return supportsImages;
	}
}
