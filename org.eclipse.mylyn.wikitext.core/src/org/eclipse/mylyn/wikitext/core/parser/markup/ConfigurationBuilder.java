/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.core.parser.markup;

import java.util.Locale;

import org.eclipse.mylyn.wikitext.core.parser.markup.block.EclipseErrorDetailsBlock;
import org.eclipse.mylyn.wikitext.core.parser.markup.block.JavaStackTraceBlock;

/**
 * A builder for creating {@link MarkupLanguageConfiguration}
 * 
 * @author David Green
 * @since 1.6
 */
public class ConfigurationBuilder {

	private final MarkupLanguageConfiguration configuration = new MarkupLanguageConfiguration();

	/**
	 * create an instance of the builder
	 */
	public static ConfigurationBuilder create() {
		return new ConfigurationBuilder();
	}

	/**
	 * causes builder to have repository-optimal settings
	 * 
	 * @see #disableUnwrappedParagraphs()
	 * @see #escapingHtmlAndXml()
	 * @see #newlinesMustCauseLineBreak()
	 * @see #optimizeForRepositoryUsage()
	 * @see EclipseErrorDetailsBlock
	 * @see JavaStackTraceBlock
	 */
	public ConfigurationBuilder repositorySettings() {
		ConfigurationBuilder builder = create();
		builder.disableUnwrappedParagraphs()
				.escapingHtmlAndXml()
				.newlinesMustCauseLineBreak()
				.optimizeForRepositoryUsage()
				.block(new EclipseErrorDetailsBlock())
				.block(new JavaStackTraceBlock());
		return builder;
	}

	/**
	 * create an instance of a {@link MarkupLanguageConfiguration configuration}.
	 */
	public MarkupLanguageConfiguration configuration() {
		// clone to avoid side-effects if the builder is used again after a call to this method
		return configuration.clone();
	}

	/**
	 * @see MarkupLanguageConfiguration#setEscapingHtmlAndXml(boolean)
	 */
	public ConfigurationBuilder escapingHtmlAndXml() {
		configuration.setEscapingHtmlAndXml(true);
		return this;
	}

	/**
	 * @see MarkupLanguageConfiguration#setNewlinesMustCauseLineBreak(boolean)
	 */
	public ConfigurationBuilder newlinesMustCauseLineBreak() {
		configuration.setNewlinesMustCauseLineBreak(true);
		return this;
	}

	/**
	 * @see MarkupLanguageConfiguration#setOptimizeForRepositoryUsage(boolean)
	 * @see #repositorySettings()
	 */
	public ConfigurationBuilder optimizeForRepositoryUsage() {
		configuration.setOptimizeForRepositoryUsage(true);
		return this;
	}

	/**
	 * @see MarkupLanguageConfiguration#setWikiWordLinking(Boolean)
	 */
	public ConfigurationBuilder disableWikiWordLinking() {
		configuration.setWikiWordLinking(false);
		return this;
	}

	/**
	 * @see MarkupLanguageConfiguration#setLocale(Locale)
	 */
	public ConfigurationBuilder locale(Locale locale) {
		configuration.setLocale(locale);
		return this;
	}

	/**
	 * @see MarkupLanguageConfiguration#setEnableUnwrappedParagraphs(boolean)
	 */
	public ConfigurationBuilder disableUnwrappedParagraphs() {
		configuration.setEnableUnwrappedParagraphs(false);
		return this;
	}

	/**
	 * @see MarkupLanguageConfiguration#getPhraseModifiers()
	 */
	public ConfigurationBuilder phraseModifier(PatternBasedElement phraseModifier) {
		configuration.getPhraseModifiers().add(phraseModifier);
		return this;
	}

	/**
	 * @see MarkupLanguageConfiguration#getBlocks()
	 */
	public ConfigurationBuilder block(Block block) {
		configuration.getBlocks().add(block);
		return this;
	}

	/**
	 * @see MarkupLanguageConfiguration#getTokens()
	 */
	public ConfigurationBuilder token(PatternBasedElement tokenSyntax) {
		configuration.getTokens().add(tokenSyntax);
		return this;
	}

}
