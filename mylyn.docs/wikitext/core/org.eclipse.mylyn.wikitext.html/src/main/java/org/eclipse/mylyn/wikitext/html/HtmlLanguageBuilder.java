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

package org.eclipse.mylyn.wikitext.html;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.mylyn.wikitext.html.internal.FontElementStrategy;
import org.eclipse.mylyn.wikitext.html.internal.HtmlSubsetLanguage;
import org.eclipse.mylyn.wikitext.html.internal.LiteralHtmlDocumentHandler;
import org.eclipse.mylyn.wikitext.html.internal.SpanHtmlElementStrategy;
import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentHandler;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;

import com.google.common.base.Strings;

/**
 * Provides a way to build HTML languages that support a specific set of HTML tags.
 *
 * @author david.green
 * @see HtmlLanguage#builder()
 * @since 3.0
 */
public class HtmlLanguageBuilder {
	private String name;

	private final Set<BlockType> blockTypes = new HashSet<>();

	private final Set<SpanType> spanTypes = new HashSet<>();

	private final Map<SpanType, String> spanTypeToElementNameSubstitution = new HashMap<>();

	private final List<SpanHtmlElementStrategy> spanElementStrategies = new ArrayList<>();

	private int headingLevel;

	private LiteralHtmlDocumentHandler documentHandler;

	private boolean xhtmlStrict;

	private boolean supportsImages = true;

	HtmlLanguageBuilder() {
		// prevent direct instantiation
	}

	/**
	 * Sets the {@link MarkupLanguage#getName() name} of the markup language.
	 *
	 * @param name
	 *            the name
	 * @return this builder
	 */
	public HtmlLanguageBuilder name(String name) {
		requireNonNull(name, "Must provide a name"); //$NON-NLS-1$
		checkArgument(!Strings.isNullOrEmpty(name), "Name must not be empty"); //$NON-NLS-1$
		checkArgument(!name.equalsIgnoreCase(HtmlLanguage.NAME_HTML), "Name must not be equal to %s", //$NON-NLS-1$
				HtmlLanguage.NAME_HTML);
		checkArgument(name.equals(name.trim()), "Name must not have leading or trailing whitespace"); //$NON-NLS-1$
		this.name = name;
		return this;
	}

	/**
	 * Adds the given {@link BlockType} to the supported syntax of the language created by this builder.
	 * <p>
	 * Adding {@link BlockType#TABLE}, {@link BlockType#BULLETED_LIST}, {@link BlockType#NUMERIC_LIST} or
	 * {@link BlockType#DEFINITION_LIST} will cause the corresponding related blocks to be added. For example, adding
	 * {@link BlockType#BULLETED_LIST} also adds {@link BlockType#LIST_ITEM}.
	 * </p>
	 *
	 * @param blockType
	 *            the block type
	 * @return this builder
	 */
	public HtmlLanguageBuilder add(BlockType blockType) {
		blockTypes.add(requireNonNull(blockType, "Must provide a blockType")); //$NON-NLS-1$
		return this;
	}

	/**
	 * Adds the given {@link SpanType} to the supported syntax of the language created by this builder.
	 *
	 * @param spanType
	 *            the span type
	 * @return this builder
	 */
	public HtmlLanguageBuilder add(SpanType spanType) {
		spanTypes.add(requireNonNull(spanType, "Must provide a spanType")); //$NON-NLS-1$
		return this;
	}

	/**
	 * Adds to the syntax of the language created by this builder an {@code alternativeTagName} to be used when the
	 * given {@link SpanType} is {@link DocumentBuilder#beginSpan(SpanType, Attributes) started}.
	 *
	 * @param spanType
	 *            the span type
	 * @param alternativeTagName
	 *            the tag name to be used
	 * @return this builder
	 * @see HtmlDocumentBuilder#setElementNameOfSpanType(SpanType, String)
	 */
	public HtmlLanguageBuilder addSubstitution(SpanType spanType, String alternativeTagName) {
		requireNonNull(spanType, "Must provide a spanType"); //$NON-NLS-1$
		requireNonNull(alternativeTagName, "Must provide an alternativeTagName"); //$NON-NLS-1$
		spanTypeToElementNameSubstitution.put(spanType, alternativeTagName);
		return this;
	}

	/**
	 * Adds support for headings up to and including the specified level.
	 *
	 * @param level
	 *            the level which must be a number between 1 and 6 inclusive
	 * @return this builder
	 */
	public HtmlLanguageBuilder addHeadings(int level) {
		checkArgument(level > 0 && level <= 6, "Heading level must be between 1 and 6"); //$NON-NLS-1$
		headingLevel = level;
		return this;
	}

	/**
	 * Adds support for the {@code <font>} HTML tag as a {@link SpanType#SPAN}. The resulting document builder will
	 * convert {@link SpanType#SPAN} with {@code size} or {@code colour} CSS rules to {@code <font>} when generating
	 * HTML.
	 *
	 * @return
	 */
	public HtmlLanguageBuilder addSpanFont() {
		spanElementStrategies.add(new FontElementStrategy());
		return this;
	}

	/**
	 * Indicate if the resulting document builder should attempt to conform to strict XHTML rules. The default is false.
	 *
	 * @param xhtmlStrict
	 *            true if the language should attempt to conform to XHTML strict rules, otherwise false
	 * @return this builder
	 */
	public HtmlLanguageBuilder setXhtmlStrict(boolean xhtmlStrict) {
		this.xhtmlStrict = xhtmlStrict;
		return this;
	}

	/**
	 * Provides a prefix and suffix which are emitted as literals at the start and end of content created using the
	 * {@link MarkupLanguage#createDocumentBuilder(java.io.Writer, boolean) document builder}.
	 *
	 * @param prefix
	 *            the prefix which is an HTML literal value that precedes the content, for example {@code "<div>"} or
	 *            {@code "<html><body>"}. May be empty.
	 * @param suffix
	 *            the prefix which is an HTML literal value that precedes the content, for example {@code "</div>"} or
	 *            {@code "</body></html>"}. May be empty.
	 * @return this builder
	 * @see HtmlDocumentHandler
	 */
	public HtmlLanguageBuilder document(String prefix, String suffix) {
		requireNonNull(prefix, "Must provide a prefix"); //$NON-NLS-1$
		requireNonNull(suffix, "Must provide a suffix"); //$NON-NLS-1$
		documentHandler = new LiteralHtmlDocumentHandler(prefix, suffix);
		return this;
	}

	/**
	 * Indicate if the resulting document builder should support HTML img tags or strip them out. The default is true.
	 *
	 * @param supportsImages
	 *            true if the language should support HTML image tags, false if they are to be stripped out
	 * @return this builder
	 */
	public HtmlLanguageBuilder setSupportsImages(boolean supportsImages) {
		this.supportsImages = supportsImages;
		return this;
	}

	public HtmlLanguage create() {
		checkState(name != null, "Name must be provided to create an HtmlLanguage"); //$NON-NLS-1$

		return new HtmlSubsetLanguage(name, documentHandler, headingLevel, blockTypes, spanTypes,
				spanTypeToElementNameSubstitution, spanElementStrategies, xhtmlStrict, supportsImages);
	}
}
