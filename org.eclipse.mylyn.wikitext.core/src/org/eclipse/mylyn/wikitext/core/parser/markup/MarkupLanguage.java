/*******************************************************************************
 * Copyright (c) 2007, 2011 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.core.parser.markup;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Writer;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Set;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.markup.token.ImpliedHyperlinkReplacementToken;
import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineParser;
import org.eclipse.mylyn.wikitext.core.util.ServiceLocator;

import com.google.common.collect.ImmutableSet;

/**
 * A markup language, which knows its formatting rules and is able to process content based on {@link Block},
 * {@link PatternBasedElementProcessor} and {@link PatternBasedElement} concepts. All markup languages supported by
 * WikiText extend this class.
 * <p>
 * The MarkupLanguage class provides basic functionality for determining which blocks process which markup content in a
 * particular document. In general multi-line documents are split into consecutive regions called blocks, and each line
 * in a block is processed with spanning sections called phrase modifiers, and tokens within a span are replaced with
 * their respective replacement tokens. These rules apply to most lightweight markup languages, however subclasses may
 * override this default functionality if required. For example, by default phrase modifiers are non-overlapping and
 * non-nested, however if required a subclass could permit such nesting.
 * </p>
 * <p>
 * Generally markup language classes are not accessed directly by client code, instead client code should configure and
 * call {@link MarkupParser}, accessing the markup language by name using the {@link ServiceLocator}.
 * </p>
 * 
 * @author David Green
 * @since 2.0
 */
public abstract class MarkupLanguage implements Cloneable {

	private static final DefaultIdGenerationStrategy DEFAULT_ID_GENERATION_STRATEGY = new DefaultIdGenerationStrategy();

	private String name;

	private String extendsLanguage;

	private Set<String> fileExtensions;

	private boolean filterGenerativeBlocks;

	private boolean blocksOnly;

	protected String internalLinkPattern = "{0}"; //$NON-NLS-1$

	private boolean enableMacros = true;

	@Override
	public MarkupLanguage clone() {
		MarkupLanguage markupLanguage;
		try {
			markupLanguage = getClass().newInstance();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		markupLanguage.setName(name);
		markupLanguage.internalLinkPattern = internalLinkPattern;
		markupLanguage.enableMacros = enableMacros;
		return markupLanguage;
	}

	/**
	 * Create new state for tracking a document and its contents during a parse session. Subclasses may override this
	 * method to provide additional state tracking capability.
	 * 
	 * @return the new state.
	 */
	protected ContentState createState() {
		ContentState contentState = new ContentState();
		contentState.getIdGenerator().setGenerationStrategy(getIdGenerationStrategy());
		return contentState;
	}

	/**
	 * get the id strategy employed by this markup language.
	 */
	public IdGenerationStrategy getIdGenerationStrategy() {
		return DEFAULT_ID_GENERATION_STRATEGY;
	}

	/**
	 * configure the markup language with a configuration that may alter the language syntax and capabilities.
	 * 
	 * @param configuration
	 *            the configuration to use
	 * @throws UnsupportedOperationException
	 *             markup languages that do not support configuration must throw this exception.
	 */
	public void configure(MarkupLanguageConfiguration configuration) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * The name of the markup language, typically the same as the name of the markup language supported by this markup
	 * language. This value may be displayed to the user.
	 * 
	 * @return the name, or null if unknown
	 */
	public String getName() {
		return name;
	}

	/**
	 * The name of the markup language, typically the same as the name of the markup language supported by this markup
	 * language. This value may be displayed to the user.
	 * 
	 * @param name
	 *            the name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Provides the normal file extensions of this markup language. The default implementation returns a set of
	 * {@link #getName()}.
	 * 
	 * @return the file extensions
	 * @since 2.0
	 */
	public Set<String> getFileExtensions() {
		if (fileExtensions == null) {
			return Collections.singleton(getName());
		}
		return fileExtensions;
	}

	/**
	 * Sets the normal file extensions of this markup language.
	 * 
	 * @return the file extensions
	 * @since 2.0
	 * @see #getFileExtensions()
	 */
	public void setFileExtensions(Set<String> fileExtensions) {
		checkNotNull(fileExtensions, "Must specify file extensions"); //$NON-NLS-1$
		checkArgument(!fileExtensions.isEmpty(), "File extensions must not be empty"); //$NON-NLS-1$
		this.fileExtensions = ImmutableSet.copyOf(fileExtensions);
	}

	/**
	 * The name of the markup language that is extended by this one
	 * 
	 * @return the name, or null if this markup language does not extend another.
	 */
	public String getExtendsLanguage() {
		return extendsLanguage;
	}

	/**
	 * The name of the markup language that is extended by this one
	 * 
	 * @param extendsLanguage
	 *            the name, or null if this markup language does not extend another.
	 */
	public void setExtendsLanguage(String extendsLanguage) {
		this.extendsLanguage = extendsLanguage;
	}

	/**
	 * Indicate if generative contents should be filtered. This option is used with the {@link OutlineParser}.
	 */
	public boolean isFilterGenerativeContents() {
		return filterGenerativeBlocks;
	}

	/**
	 * Indicate if table of contents should be filtered. This option is used with the {@link OutlineParser}.
	 */
	public void setFilterGenerativeContents(boolean filterGenerativeBlocks) {
		this.filterGenerativeBlocks = filterGenerativeBlocks;
	}

	/**
	 * indicate if the parser should detect blocks only. This is useful for use in a document partitioner where the
	 * partition boundaries are defined by blocks.
	 */
	public boolean isBlocksOnly() {
		return blocksOnly;
	}

	/**
	 * indicate if the parser should detect blocks only. This is useful for use in a document partitioner where the
	 * partition boundaries are defined by blocks.
	 */
	public void setBlocksOnly(boolean blocksOnly) {
		this.blocksOnly = blocksOnly;
	}

	public abstract void processContent(MarkupParser parser, String markupContent, boolean asDocument);

	/**
	 * The pattern to use when creating hyperlink targets for internal links. The pattern is implementation-specific,
	 * however implementations are encouraged to use {@link MessageFormat}, where the 0th parameter is the internal
	 * link.
	 * 
	 * @see MessageFormat
	 */
	public String getInternalLinkPattern() {
		return internalLinkPattern;
	}

	/**
	 * The pattern to use when creating hyperlink targets for internal links. The pattern is implementation-specific,
	 * however implementations are encouraged to use {@link MessageFormat}, where the 0th parameter is the internal
	 * link.
	 * 
	 * @see MessageFormat
	 */
	public void setInternalLinkPattern(String internalLinkPattern) {
		this.internalLinkPattern = internalLinkPattern;
	}

	/**
	 * Indicate if this markup language detects 'raw' hyperlinks; that is hyperlinks without any special markup. The
	 * default implementation checks the markup syntax for use of {@link ImpliedHyperlinkReplacementToken} and returns
	 * true if it is in the syntax.
	 * 
	 * @return true if raw hyperlinks are detected by this markup language, otherwise false.
	 * @since 1.1
	 */
	public boolean isDetectingRawHyperlinks() {
		return false;
	}

	/**
	 * Indicate if macro processing is enabled. Generally such processing is enabled except when used in a source
	 * editor.
	 * <p>
	 * Macros are defined as text substitution prior to normal processing. Such preprocessing changes the markup before
	 * it is processed, and as such has the side-effect of changing computed offsets when parsing markup.
	 * </p>
	 * <p>
	 * The default value is true.
	 * </p>
	 * 
	 * @return true if macros are enabled, otherwise false
	 * @since 1.3
	 */
	public boolean isEnableMacros() {
		return enableMacros;
	}

	/**
	 * Indicate if macro processing is enabled. Generally such processing is enabled except when used in a source
	 * editor.
	 * <p>
	 * Macros are defined as text substitution prior to normal processing. Such preprocessing changes the markup before
	 * it is processed, and as such has the side-effect of changing computed offsets when parsing markup.
	 * </p>
	 * 
	 * @param enableMacros
	 *            true if macros are enabled, otherwise false
	 * @since 1.3
	 */
	public void setEnableMacros(boolean enableMacros) {
		this.enableMacros = enableMacros;
	}

	/**
	 * Create a document builder suitable for emitting content in this markup language
	 * 
	 * @param out
	 *            the target to which content is written
	 * @return a document builder
	 * @throws UnsupportedOperationException
	 *             if the markup language has no corresponding document builder
	 * @since 1.6
	 */
	public DocumentBuilder createDocumentBuilder(Writer out) {
		throw new UnsupportedOperationException();
	}
}
