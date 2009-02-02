/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.core.parser.markup;

import java.util.ArrayList;
import java.util.List;

/**
 * a standard implementation of a markup language usually extends this class, which provides default support for common
 * functionality.
 * 
 * @author David Green
 * 
 * @since 1.0
 */
public abstract class AbstractMarkupLanguage extends MarkupLanguage {

	// we use the template pattern for creating new blocks
	protected List<Block> blocks = new ArrayList<Block>();

	protected List<Block> paragraphBreakingBlocks = new ArrayList<Block>();

	protected PatternBasedSyntax tokenSyntax = new PatternBasedSyntax();

	protected PatternBasedSyntax phraseModifierSyntax = new PatternBasedSyntax();

	@Override
	protected PatternBasedSyntax getPhraseModifierSyntax() {
		return phraseModifierSyntax;
	}

	@Override
	protected PatternBasedSyntax getReplacementTokenSyntax() {
		return tokenSyntax;
	}

	@Override
	public final List<Block> getBlocks() {
		return blocks;
	}

	public final List<Block> getParagraphBreakingBlocks() {
		return paragraphBreakingBlocks;
	}

	@Override
	protected final void initializeSyntax(MarkupLanguageConfiguration configuration) {
		if (!blocks.isEmpty()) {
			clearLanguageSyntax();
		}
		initializeBlocks(configuration);
		initializePhraseModifiers(configuration);
		initializeTokens(configuration);
	}

	protected void clearLanguageSyntax() {
		blocks.clear();
		paragraphBreakingBlocks.clear();
		tokenSyntax.clear();
		phraseModifierSyntax.clear();
	}

	protected final void initializeTokens(MarkupLanguageConfiguration configuration) {
		addStandardTokens(configuration, tokenSyntax);
		addTokenExtensions(configuration, tokenSyntax);
		if (configuration != null) {
			configuration.addTokenExtensions(tokenSyntax);
		}
	}

	protected final void initializePhraseModifiers(MarkupLanguageConfiguration configuration) {
		addStandardPhraseModifiers(configuration, phraseModifierSyntax);
		addPhraseModifierExtensions(configuration, phraseModifierSyntax);
		if (configuration != null) {
			configuration.addPhraseModifierExtensions(phraseModifierSyntax);
		}
	}

	protected final void initializeBlocks(MarkupLanguageConfiguration configuration) {
		addStandardBlocks(configuration, blocks, paragraphBreakingBlocks);
		// extensions
		addBlockExtensions(configuration, blocks, paragraphBreakingBlocks);
		if (configuration != null) {
			configuration.addBlockExtensions(blocks, paragraphBreakingBlocks);
		}
		// ~extensions

		blocks.add(createParagraphBlock(configuration)); // ORDER DEPENDENCY: this must come last
	}

	protected abstract void addStandardTokens(MarkupLanguageConfiguration configuration, PatternBasedSyntax tokenSyntax);

	protected abstract void addStandardPhraseModifiers(MarkupLanguageConfiguration configuration,
			PatternBasedSyntax phraseModifierSyntax);

	protected abstract void addStandardBlocks(MarkupLanguageConfiguration configuration, List<Block> blocks,
			List<Block> paragraphBreakingBlocks);

	protected abstract Block createParagraphBlock(MarkupLanguageConfiguration configuration);

	/**
	 * subclasses may override this method to add blocks to the language. Overriding classes should call
	 * <code>super.addBlockExtensions(blocks,paragraphBreakingBlocks)</code> if the default language extensions are
	 * desired.
	 * 
	 * @param configuration
	 * 
	 * @param blocks
	 *            the list of blocks to which extensions may be added
	 * @param paragraphBreakingBlocks
	 *            the list of blocks that end a paragraph
	 */
	protected void addBlockExtensions(MarkupLanguageConfiguration configuration, List<Block> blocks,
			List<Block> paragraphBreakingBlocks) {
		// no block extensions
	}

	/**
	 * subclasses may override this method to add tokens to the language. Overriding classes should call
	 * <code>super.addTokenExtensions(tokenSyntax)</code> if the default language extensions are desired.
	 * 
	 * @param configuration
	 * 
	 * @param tokenSyntax
	 *            the token syntax
	 */
	protected void addTokenExtensions(MarkupLanguageConfiguration configuration, PatternBasedSyntax tokenSyntax) {
		// no token extensions
	}

	/**
	 * subclasses may override this method to add phrases to the language. Overriding classes should call
	 * <code>super.addPhraseModifierExtensions(phraseModifierSyntax)</code> if the default language extensions are
	 * desired.
	 * 
	 * @param configuration
	 * 
	 * @param phraseModifierSyntax
	 *            the phrase modifier syntax
	 */
	protected void addPhraseModifierExtensions(MarkupLanguageConfiguration configuration,
			PatternBasedSyntax phraseModifierSyntax) {
		// no phrase extensions
	}

	@Override
	protected void doDeepClone(MarkupLanguage c) {
		AbstractMarkupLanguage copy = (AbstractMarkupLanguage) c;
		copy.blocks = new ArrayList<Block>();
		copy.paragraphBreakingBlocks = new ArrayList<Block>();
		for (Block block : blocks) {
			Block blockCopy = block.clone();
			doDeepCloneBlock(copy, block, blockCopy);
		}
		copy.phraseModifierSyntax = phraseModifierSyntax.clone();
		copy.tokenSyntax = tokenSyntax.clone();
	}

	protected void doDeepCloneBlock(AbstractMarkupLanguage copy, Block block, Block blockCopy) {
		copy.blocks.add(blockCopy);
		if (paragraphBreakingBlocks.contains(block)) {
			copy.paragraphBreakingBlocks.add(blockCopy);
		}
	}
}
