/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.tracwiki.core;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylyn.internal.wikitext.tracwiki.core.block.HeadingBlock;
import org.eclipse.mylyn.internal.wikitext.tracwiki.core.block.ListBlock;
import org.eclipse.mylyn.internal.wikitext.tracwiki.core.block.ParagraphBlock;
import org.eclipse.mylyn.internal.wikitext.tracwiki.core.block.PreformattedBlock;
import org.eclipse.mylyn.internal.wikitext.tracwiki.core.block.QuoteBlock;
import org.eclipse.mylyn.internal.wikitext.tracwiki.core.block.TableBlock;
import org.eclipse.mylyn.internal.wikitext.tracwiki.core.phrase.EscapePhraseModifier;
import org.eclipse.mylyn.internal.wikitext.tracwiki.core.phrase.SimplePhraseModifier;
import org.eclipse.mylyn.internal.wikitext.tracwiki.core.token.BangEscapeToken;
import org.eclipse.mylyn.internal.wikitext.tracwiki.core.token.HyperlinkReplacementToken;
import org.eclipse.mylyn.internal.wikitext.tracwiki.core.token.LineBreakToken;
import org.eclipse.mylyn.internal.wikitext.tracwiki.core.token.WikiWordReplacementToken;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.core.parser.markup.token.ImpliedHyperlinkReplacementToken;

/**
 * An implementation of the <a href="http://trac.edgewall.org/wiki/TracWiki">TracWiki</a> markup language.
 * 
 * @author David Green
 * @since 1.0
 */
public class TracWikiLanguage extends MarkupLanguage {
	private final List<Block> blocks = new ArrayList<Block>();

	private final List<Block> paragraphNestableBlocks = new ArrayList<Block>();

	private boolean autoLinking = true;

	private static PatternBasedSyntax tokenSyntax = new PatternBasedSyntax();

	private static PatternBasedSyntax phraseModifierSyntax = new PatternBasedSyntax();

	{

		// IMPORTANT NOTE: Most items below have order dependencies.  DO NOT REORDER ITEMS BELOW!!

		// TODO: traclinks, images, macros, processors

		ListBlock listBlock = new ListBlock();
		blocks.add(listBlock);
		paragraphNestableBlocks.add(listBlock);
		HeadingBlock headingBlock = new HeadingBlock();
		blocks.add(headingBlock);
		paragraphNestableBlocks.add(listBlock);
		PreformattedBlock preformattedBlock = new PreformattedBlock();
		blocks.add(preformattedBlock);
		paragraphNestableBlocks.add(preformattedBlock);
		QuoteBlock quoteBlock = new QuoteBlock();
		blocks.add(quoteBlock);
		paragraphNestableBlocks.add(quoteBlock);
		TableBlock tableBlock = new TableBlock();
		blocks.add(tableBlock);
		paragraphNestableBlocks.add(tableBlock);
		blocks.add(new ParagraphBlock()); // ORDER DEPENDENCY: this one must be last!!
	}
	static {
		phraseModifierSyntax.beginGroup("(?:(?<=[\\s\\.\\\"'?!;:\\)\\(\\{\\}\\[\\]])|^)(?:", 0); // always starts at the start of a line or after a non-word character excluding '!' //$NON-NLS-1$
		phraseModifierSyntax.add(new EscapePhraseModifier());
		phraseModifierSyntax.add(new SimplePhraseModifier("'''''", new SpanType[] { SpanType.BOLD, SpanType.ITALIC }, //$NON-NLS-1$
				true));
		phraseModifierSyntax.add(new SimplePhraseModifier("'''", SpanType.BOLD, true)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier("''", SpanType.ITALIC, true)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier("__", SpanType.UNDERLINED, true)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier("--", SpanType.DELETED, true)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier("^", SpanType.SUPERSCRIPT, true)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier(",,", SpanType.SUBSCRIPT, true)); //$NON-NLS-1$
		phraseModifierSyntax.endGroup(")(?=\\W|$)", 0); //$NON-NLS-1$

		tokenSyntax.add(new BangEscapeToken());
		tokenSyntax.add(new LineBreakToken());
		tokenSyntax.add(new HyperlinkReplacementToken());
		tokenSyntax.add(new ImpliedHyperlinkReplacementToken());
		tokenSyntax.add(new WikiWordReplacementToken());
	}

	public TracWikiLanguage() {
		setName("TracWiki"); //$NON-NLS-1$
	}

	@Override
	public List<Block> getBlocks() {
		return blocks;
	}

	public List<Block> getParagraphNestableBlocks() {
		return paragraphNestableBlocks;
	}

	@Override
	protected PatternBasedSyntax getPhraseModifierSyntax() {
		return phraseModifierSyntax;
	}

	@Override
	protected PatternBasedSyntax getReplacementTokenSyntax() {
		return tokenSyntax;
	}

	/**
	 * Convert a page name to an href to the page.
	 * 
	 * @param pageName
	 *            the name of the page to target
	 * 
	 * @return the href to access the page
	 * 
	 * @see MarkupLanguage#getInternalLinkPattern()
	 */
	public String toInternalHref(String pageName) {
		String pageId = pageName;
		if (pageId.startsWith("#")) { //$NON-NLS-1$
			// internal anchor
			return pageId;
		}
		return MessageFormat.format(super.internalLinkPattern, pageId);
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
	 * Indicate if the markup should match WikiWords as hyperlinks. The default is true.
	 */
	public boolean isAutoLinking() {
		return autoLinking;
	}

	/**
	 * Indicate if the markup should match WikiWords as hyperlinks. The default is true.
	 */
	public void setAutoLinking(boolean autoLinking) {
		this.autoLinking = autoLinking;
	}
}
