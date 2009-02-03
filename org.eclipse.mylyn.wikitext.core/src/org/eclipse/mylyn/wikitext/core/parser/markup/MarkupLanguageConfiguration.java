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

import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage.PatternBasedSyntax;

/**
 * A configuration for a markup language, which enables client code to modify the syntax of the markup language by
 * adding blocks, phrase modifiers, and tokens. Also provides a mechanism for disabling some common markup language
 * features.
 * 
 * @author David Green
 */
public class MarkupLanguageConfiguration implements Cloneable {

	private List<Block> blocks = new ArrayList<Block>();

	private List<PatternBasedElement> phraseModifiers = new ArrayList<PatternBasedElement>();

	private List<PatternBasedElement> tokens = new ArrayList<PatternBasedElement>();

	private boolean escapingHtmlAndXml = false;

	private boolean enableUnwrappedParagraphs = true;

	private boolean newlinesMustCauseLineBreak = false;

	private boolean optimizeForRepositoryUsage = false;

	public List<PatternBasedElement> getTokens() {
		return tokens;
	}

	public List<PatternBasedElement> getPhraseModifiers() {
		return phraseModifiers;
	}

	public List<Block> getBlocks() {
		return blocks;
	}

	/**
	 * Indicate if the given block should break paragraph content even if there is no blank line preceding it. The
	 * default implementation returns true.
	 * 
	 * @param block
	 *            the block
	 * @return true if the block should break paragraph content
	 */
	public boolean isParagraphBreaking(Block block) {
		return true;
	}

	/**
	 * Indicate if this language should escape HTML and XML tags. When HTML and XML tags are escaped they appear as
	 * literals in the page, rather than affecting the presentation. The default is false.
	 */
	public boolean isEscapingHtmlAndXml() {
		return escapingHtmlAndXml;
	}

	/**
	 * Indicate if this language should escape HTML and XML tags. When HTML and XML tags are escaped they appear as
	 * literals in the page, rather than affecting the presentation. The default is false.
	 */
	public void setEscapingHtmlAndXml(boolean escapingHtmlAndXml) {
		this.escapingHtmlAndXml = escapingHtmlAndXml;
	}

	/**
	 * indicate if unwrapped paragraphs should be supported. Unwrapped paragraphs are those that are not nested in
	 * &lt;p&gt; or &lt;div&gt; tags. The default is true.
	 */
	public boolean isEnableUnwrappedParagraphs() {
		return enableUnwrappedParagraphs;
	}

	/**
	 * indicate if unwrapped paragraphs should be supported. Unwrapped paragraphs are those that are not nested in
	 * &lt;p&gt; or &lt;div&gt; tags. The default is true.
	 */
	public void setEnableUnwrappedParagraphs(boolean enableUnwrappedParagraphs) {
		this.enableUnwrappedParagraphs = enableUnwrappedParagraphs;
	}

	/**
	 * Indicate if newlines in the markup must cause a line break. If false, then the default markup language behaviour
	 * should be observed. The default is false.
	 */
	public boolean isNewlinesMustCauseLineBreak() {
		return newlinesMustCauseLineBreak;
	}

	/**
	 * Indicate if newlines in the markup must cause a line break. If false, then the default markup language behaviour
	 * should be observed. The default is false.
	 */
	public void setNewlinesMustCauseLineBreak(boolean newlinesMustCauseLineBreak) {
		this.newlinesMustCauseLineBreak = newlinesMustCauseLineBreak;
	}

	/**
	 * Indicate if newlines in the markup must cause a line break. If false, then the default markup language behaviour
	 * should be observed. The default is false.
	 */
	public void addPhraseModifierExtensions(PatternBasedSyntax phraseModifierSyntax) {
		for (PatternBasedElement element : getPhraseModifiers()) {
			phraseModifierSyntax.add(element);
		}
	}

	/**
	 * indicate if the markup syntax should be optimized for use with a task repository.
	 */
	public boolean isOptimizeForRepositoryUsage() {
		return optimizeForRepositoryUsage;
	}

	/**
	 * indicate if the markup syntax should be optimized for use with a task repository.
	 */
	public void setOptimizeForRepositoryUsage(boolean optimizeForRepositoryUsage) {
		this.optimizeForRepositoryUsage = optimizeForRepositoryUsage;
	}

	public void addBlockExtensions(List<Block> blocks, List<Block> paragraphBreakingBlocks) {
		for (Block block : getBlocks()) {
			blocks.add(computeInsertPosition(block), block);
			if (isParagraphBreaking(block)) {
				paragraphBreakingBlocks.add(block);
			}
		}
	}

	protected int computeInsertPosition(Block block) {
		return 0;
	}

	public void addTokenExtensions(PatternBasedSyntax tokenSyntax) {
		for (PatternBasedElement element : getPhraseModifiers()) {
			tokenSyntax.add(element);
		}
	}

	@Override
	public MarkupLanguageConfiguration clone() {
		try {
			MarkupLanguageConfiguration copy = (MarkupLanguageConfiguration) super.clone();
			copy.blocks = new ArrayList<Block>(blocks.size());
			for (Block block : blocks) {
				copy.blocks.add(block.clone());
			}
			copy.phraseModifiers = new ArrayList<PatternBasedElement>(phraseModifiers.size());
			for (PatternBasedElement element : phraseModifiers) {
				copy.phraseModifiers.add(element.clone());
			}
			copy.tokens = new ArrayList<PatternBasedElement>(tokens.size());
			for (PatternBasedElement element : tokens) {
				copy.tokens.add(element.clone());
			}
			return copy;
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e);
		}
	}
}
