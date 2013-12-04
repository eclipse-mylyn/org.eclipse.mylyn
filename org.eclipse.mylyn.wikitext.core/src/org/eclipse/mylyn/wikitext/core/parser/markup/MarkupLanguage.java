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
import java.util.Collections;
import java.util.Set;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
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

	@Override
	public MarkupLanguage clone() {
		MarkupLanguage markupLanguage;
		try {
			markupLanguage = getClass().newInstance();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		markupLanguage.setName(name);
		markupLanguage.setExtendsLanguage(extendsLanguage);
		return markupLanguage;
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
	 * @see #getFileExtensions()
	 * @since 2.0
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

	public abstract void processContent(MarkupParser parser, String markupContent, boolean asDocument);

	/**
	 * Creates a {@link DocumentBuilder} suitable for emitting content in this markup language. Equivalent to
	 * {@code createDocumentBuilder(out,false)}.
	 * 
	 * @param out
	 *            the target to which content is written
	 * @return a document builder
	 * @throws UnsupportedOperationException
	 *             if the markup language has no corresponding document builder
	 * @see #createDocumentBuilder(Writer, boolean)
	 */
	public DocumentBuilder createDocumentBuilder(Writer out) {
		return createDocumentBuilder(out, false);
	}

	/**
	 * Creates a {@link DocumentBuilder} suitable for emitting content in this markup language.
	 * 
	 * @param out
	 *            the target to which content is written
	 * @param formatting
	 *            indicates if the builder should format the output using pretty-print rules. If not supported by the
	 *            document builder this parameter is ignored.
	 * @return a document builder
	 * @throws UnsupportedOperationException
	 *             if the markup language has no corresponding document builder
	 * @since 2.0
	 */
	public DocumentBuilder createDocumentBuilder(Writer out, boolean formatting) {
		throw new UnsupportedOperationException();
	}
}
