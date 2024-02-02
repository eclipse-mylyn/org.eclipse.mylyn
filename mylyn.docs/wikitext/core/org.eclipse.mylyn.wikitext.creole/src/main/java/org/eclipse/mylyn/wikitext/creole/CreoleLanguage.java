/*******************************************************************************
 * Copyright (c) 2011 Igor Malinin and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Igor Malinin - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.creole;

import java.io.Writer;
import java.text.MessageFormat;
import java.util.List;

import org.eclipse.mylyn.wikitext.creole.internal.CreoleDocumentBuilder;
import org.eclipse.mylyn.wikitext.creole.internal.block.HeadingBlock;
import org.eclipse.mylyn.wikitext.creole.internal.block.HorizontalRuleBlock;
import org.eclipse.mylyn.wikitext.creole.internal.block.ListBlock;
import org.eclipse.mylyn.wikitext.creole.internal.block.NowikiBlock;
import org.eclipse.mylyn.wikitext.creole.internal.block.ParagraphBlock;
import org.eclipse.mylyn.wikitext.creole.internal.block.TableBlock;
import org.eclipse.mylyn.wikitext.creole.internal.phrase.EscapePhraseModifier;
import org.eclipse.mylyn.wikitext.creole.internal.phrase.NowikiPhraseModifier;
import org.eclipse.mylyn.wikitext.creole.internal.phrase.SimplePhraseModifier;
import org.eclipse.mylyn.wikitext.creole.internal.token.LinkReplacementToken;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.markup.AbstractMarkupLanguage;
import org.eclipse.mylyn.wikitext.parser.markup.Block;
import org.eclipse.mylyn.wikitext.parser.markup.token.ImpliedHyperlinkReplacementToken;
import org.eclipse.mylyn.wikitext.parser.markup.token.PatternLineBreakReplacementToken;

/**
 * A markup language implementing Creole syntax. See <a href="http://www.wikicreole.org/wiki/Creole1.0">Creole Formatting Rules</a> and
 * <a href="http://www.wikicreole.org/wiki/CreoleAdditions">Creole Additions</a> for details.
 *
 * @author Igor Malinin
 * @since 3.0
 */
public class CreoleLanguage extends AbstractMarkupLanguage {

	public CreoleLanguage() {
		setName("Creole"); //$NON-NLS-1$
	}

	private Block paragraphBreakingBlock(Block block) {
		paragraphBreakingBlocks.add(block);
		return block;
	}

	/**
	 * for the purpose of converting wiki words into links, determine if the wiki word exists.
	 *
	 * @see WikiWordReplacementToken
	 */
	public boolean computeInternalLinkExists(String link) {
		return true;
	}

	/**
	 * Convert a page name to an href to the page.
	 *
	 * @param pageName
	 *            the name of the page to target, usually a WikiWord with whitespace removed
	 * @return the href to access the page
	 * @see #getInternalPageHrefPrefix()
	 */
	public String toInternalHref(String pageName) {
		String pageId = pageName.replace(' ', '_');
		return MessageFormat.format(super.internalLinkPattern, pageId);
	}

	@Override
	protected void addStandardBlocks(List<Block> blocks, List<Block> paragraphBreakingBlocks) {
		// IMPORTANT NOTE: Most items below have order dependencies.  DO NOT REORDER ITEMS BELOW!!

		blocks.add(paragraphBreakingBlock(new ListBlock()));
		blocks.add(paragraphBreakingBlock(new HorizontalRuleBlock()));
		blocks.add(paragraphBreakingBlock(new HeadingBlock()));
		blocks.add(paragraphBreakingBlock(new NowikiBlock()));
		blocks.add(paragraphBreakingBlock(new TableBlock()));
	}

	@Override
	protected void addStandardPhraseModifiers(PatternBasedSyntax phraseModifierSyntax) {
		// IMPORTANT NOTE: Most items below have order dependencies.  DO NOT REORDER ITEMS BELOW!!
		phraseModifierSyntax.add(new EscapePhraseModifier());
		phraseModifierSyntax.add(new NowikiPhraseModifier());

		phraseModifierSyntax.add(new SimplePhraseModifier("**", SpanType.BOLD, true)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier("//", SpanType.ITALIC, true)); //$NON-NLS-1$

		// Creole Additions
		phraseModifierSyntax.add(new SimplePhraseModifier("##", SpanType.MONOSPACE, true)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier("^^", SpanType.SUPERSCRIPT, true)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier(",,", SpanType.SUBSCRIPT, true)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier("__", SpanType.UNDERLINED, true)); //$NON-NLS-1$
	}

	@Override
	protected void addStandardTokens(PatternBasedSyntax tokenSyntax) {
		// IMPORTANT NOTE: Most items below have order dependencies.  DO NOT REORDER ITEMS BELOW!!

		tokenSyntax.add(new PatternLineBreakReplacementToken("(\\\\\\\\)")); // line break //$NON-NLS-1$
		tokenSyntax.add(new LinkReplacementToken());
		tokenSyntax.add(new ImpliedHyperlinkReplacementToken());
	}

	@Override
	protected Block createParagraphBlock() {
		return new ParagraphBlock();
	}

	@Override
	public DocumentBuilder createDocumentBuilder(Writer out, boolean formatting) {
		return new CreoleDocumentBuilder(out);
	}

}
