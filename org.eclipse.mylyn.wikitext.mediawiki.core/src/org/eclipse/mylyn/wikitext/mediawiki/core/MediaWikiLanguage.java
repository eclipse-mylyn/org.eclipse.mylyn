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
package org.eclipse.mylyn.wikitext.mediawiki.core;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylyn.internal.wikitext.mediawiki.core.block.HeadingBlock;
import org.eclipse.mylyn.internal.wikitext.mediawiki.core.block.ListBlock;
import org.eclipse.mylyn.internal.wikitext.mediawiki.core.block.ParagraphBlock;
import org.eclipse.mylyn.internal.wikitext.mediawiki.core.block.PreformattedBlock;
import org.eclipse.mylyn.internal.wikitext.mediawiki.core.block.TableBlock;
import org.eclipse.mylyn.internal.wikitext.mediawiki.core.phrase.EscapePhraseModifier;
import org.eclipse.mylyn.internal.wikitext.mediawiki.core.phrase.SimplePhraseModifier;
import org.eclipse.mylyn.internal.wikitext.mediawiki.core.token.HyperlinkExternalReplacementToken;
import org.eclipse.mylyn.internal.wikitext.mediawiki.core.token.HyperlinkInternalReplacementToken;
import org.eclipse.mylyn.internal.wikitext.mediawiki.core.token.ImageReplacementToken;
import org.eclipse.mylyn.internal.wikitext.mediawiki.core.token.LineBreakToken;
import org.eclipse.mylyn.internal.wikitext.mediawiki.core.token.TemplateReplacementToken;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.core.parser.markup.phrase.HtmlCommentPhraseModifier;
import org.eclipse.mylyn.wikitext.core.parser.markup.phrase.LimitedHtmlEndTagPhraseModifier;
import org.eclipse.mylyn.wikitext.core.parser.markup.phrase.LimitedHtmlStartTagPhraseModifier;
import org.eclipse.mylyn.wikitext.core.parser.markup.token.EntityReferenceReplacementToken;
import org.eclipse.mylyn.wikitext.core.parser.markup.token.ImpliedHyperlinkReplacementToken;
import org.eclipse.mylyn.wikitext.core.parser.markup.token.PatternLiteralReplacementToken;

/**
 * A dialect for <a href="http://www.mediawiki.org">MediaWiki</a> <a
 * href="http://en.wikipedia.org/wiki/Wikitext">Wikitext markup</a>, which is the wiki format used by <a
 * href="http://www.wikipedia.org>WikiPedia</a> and <a href="http://www.wikimedia.org/">several other major sites</a>.
 * 
 * @author David Green
 * 
 */
public class MediaWikiLanguage extends MarkupLanguage {
	private final List<Block> blocks = new ArrayList<Block>();

	private final List<Block> paragraphBreakingBlocks = new ArrayList<Block>();

	private static PatternBasedSyntax tokenSyntax = new PatternBasedSyntax();

	private static PatternBasedSyntax phraseModifierSyntax = new PatternBasedSyntax();

	{

		// IMPORTANT NOTE: Most items below have order dependencies.  DO NOT REORDER ITEMS BELOW!!

		blocks.add(new HeadingBlock());
		blocks.add(new ListBlock());
		blocks.add(new PreformattedBlock());
		blocks.add(new TableBlock());
		final ParagraphBlock paragraphBlock = new ParagraphBlock();
		blocks.add(paragraphBlock); // ORDER DEPENDENCY: this one must be last!!

		for (Block block : blocks) {
			if (block == paragraphBlock) {
				continue;
			}
			paragraphBreakingBlocks.add(block);
		}
	}
	static {
		phraseModifierSyntax.beginGroup("(?:(?<=[\\s\\.,\\\"'?!;:\\)\\(\\{\\}\\[\\]])|^)(?:", 0);
		phraseModifierSyntax.add(new EscapePhraseModifier());
		phraseModifierSyntax.add(new SimplePhraseModifier("'''''", new SpanType[] { SpanType.BOLD, SpanType.ITALIC },
				true));
		phraseModifierSyntax.add(new SimplePhraseModifier("'''", SpanType.BOLD, true));
		phraseModifierSyntax.add(new SimplePhraseModifier("''", SpanType.ITALIC, true));
		phraseModifierSyntax.endGroup(")(?=\\W|$)", 0);

		String[] allowedHtmlTags = new String[] {
				// HANDLED BY LineBreakToken "<br>",
				// HANDLED BY LineBreakToken "<br/>",
				"b", "big", "blockquote", "caption", "center", "cite", "code", "dd", "del", "div", "dl", "dt", "em",
				"font", "h1", "h2", "h3", "h4", "h5", "h6", "hr", "i", "ins", "li", "ol", "p", "pre", "rb", "rp", "rt",
				"ruby", "s", "small", "span", "strike", "strong", "sub", "sup", "table", "td", "th", "tr", "tt", "u",
				"ul", "var" };
		phraseModifierSyntax.add(new LimitedHtmlEndTagPhraseModifier(allowedHtmlTags));
		phraseModifierSyntax.add(new LimitedHtmlStartTagPhraseModifier(allowedHtmlTags));
		phraseModifierSyntax.add(new HtmlCommentPhraseModifier());

		tokenSyntax.add(new LineBreakToken());
		tokenSyntax.add(new EntityReferenceReplacementToken("(tm)", "#8482"));
		tokenSyntax.add(new EntityReferenceReplacementToken("(TM)", "#8482"));
		tokenSyntax.add(new EntityReferenceReplacementToken("(c)", "#169"));
		tokenSyntax.add(new EntityReferenceReplacementToken("(C)", "#169"));
		tokenSyntax.add(new EntityReferenceReplacementToken("(r)", "#174"));
		tokenSyntax.add(new EntityReferenceReplacementToken("(R)", "#174"));
		tokenSyntax.add(new ImageReplacementToken());
		tokenSyntax.add(new HyperlinkInternalReplacementToken());
		tokenSyntax.add(new HyperlinkExternalReplacementToken());
		tokenSyntax.add(new ImpliedHyperlinkReplacementToken());
		tokenSyntax.add(new PatternLiteralReplacementToken("(?:(?<=\\w\\s)(----)(?=\\s\\w))", "<hr/>")); // horizontal rule
		tokenSyntax.add(new TemplateReplacementToken());
		tokenSyntax.add(new org.eclipse.mylyn.internal.wikitext.mediawiki.core.token.EntityReferenceReplacementToken());
	}

	public MediaWikiLanguage() {
		setName("MediaWiki");
		setInternalLinkPattern("/wiki/{0}");
	}

	@Override
	protected PatternBasedSyntax getPhraseModifierSyntax() {
		return phraseModifierSyntax;
	}

	@Override
	protected PatternBasedSyntax getReplacementTokenSyntax() {
		return tokenSyntax;
	}

	@Override
	public List<Block> getBlocks() {
		return blocks;
	}

	public List<Block> getParagraphBreakingBlocks() {
		return paragraphBreakingBlocks;
	}

	/**
	 * Convert a page name to an href to the page.
	 * 
	 * @param pageName
	 *            the name of the page to target
	 * 
	 * @return the href to access the page
	 * 
	 * @see #getInternalPageHrefPrefix()
	 */
	public String toInternalHref(String pageName) {
		String pageId = pageName.replace(' ', '_');
		if (pageId.startsWith(":")) { // category
			pageId = pageId.substring(1);
		} else if (pageId.startsWith("#")) {
			// internal anchor
			return pageId;
		}
		return MessageFormat.format(super.internalLinkPattern, pageId);
	}

}
