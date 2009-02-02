/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.internal.wikitext.mediawiki.core.MediaWikiIdGenerationStrategy;
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
import org.eclipse.mylyn.wikitext.core.parser.markup.AbstractMarkupLanguage;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;
import org.eclipse.mylyn.wikitext.core.parser.markup.IdGenerationStrategy;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguageConfiguration;
import org.eclipse.mylyn.wikitext.core.parser.markup.phrase.HtmlCommentPhraseModifier;
import org.eclipse.mylyn.wikitext.core.parser.markup.phrase.LimitedHtmlEndTagPhraseModifier;
import org.eclipse.mylyn.wikitext.core.parser.markup.phrase.LimitedHtmlStartTagPhraseModifier;
import org.eclipse.mylyn.wikitext.core.parser.markup.token.EntityReferenceReplacementToken;
import org.eclipse.mylyn.wikitext.core.parser.markup.token.ImpliedHyperlinkReplacementToken;
import org.eclipse.mylyn.wikitext.core.parser.markup.token.PatternLiteralReplacementToken;

/**
 * A markup language for <a href="http://www.mediawiki.org">MediaWiki</a> <a
 * href="http://en.wikipedia.org/wiki/Wikitext">Wikitext markup</a>, which is the wiki format used by <a
 * href="http://www.wikipedia.org>WikiPedia</a> and <a href="http://www.wikimedia.org/">several other major sites</a>.
 * 
 * @author David Green
 * @since 1.0
 */
public class MediaWikiLanguage extends AbstractMarkupLanguage {
	private static final String CATEGORY_PREFIX = ":"; //$NON-NLS-1$

	private static final Pattern STANDARD_EXTERNAL_LINK_FORMAT = Pattern.compile(".*?/([^/]+)/(\\{\\d+\\})"); //$NON-NLS-1$

	private static final Pattern QUALIFIED_INTERNAL_LINK = Pattern.compile("([^/]+)/(.+)"); //$NON-NLS-1$

	public MediaWikiLanguage() {
		setName("MediaWiki"); //$NON-NLS-1$
		setInternalLinkPattern("/wiki/{0}"); //$NON-NLS-1$
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
		String pageId = pageName.replace(' ', '_');
		// FIXME: other character encodings occur here, not just ' '

		if (pageId.startsWith(CATEGORY_PREFIX) && pageId.length() > CATEGORY_PREFIX.length()) { // category
			return pageId.substring(CATEGORY_PREFIX.length());
		} else if (pageId.startsWith("#")) { //$NON-NLS-1$
			// internal anchor
			return pageId;
		}
		if (QUALIFIED_INTERNAL_LINK.matcher(pageId).matches()) {
			Matcher matcher = STANDARD_EXTERNAL_LINK_FORMAT.matcher(internalLinkPattern);
			if (matcher.matches()) {
				return internalLinkPattern.substring(0, matcher.start(1)) + pageId;
			}
		}
		return MessageFormat.format(super.internalLinkPattern, pageId);
	}

	@Override
	public IdGenerationStrategy getIdGenerationStrategy() {
		return new MediaWikiIdGenerationStrategy();
	}

	@Override
	protected void addStandardBlocks(MarkupLanguageConfiguration configuration, List<Block> blocks,
			List<Block> paragraphBreakingBlocks) {
		// IMPORTANT NOTE: Most items below have order dependencies.  DO NOT REORDER ITEMS BELOW!!

		blocks.add(new HeadingBlock());
		blocks.add(new ListBlock());

		if (hasPreformattedBlock(configuration)) {
			// preformatted blocks are lines that start with a single space, and thus are non-optimal for
			// repository usage.
			blocks.add(new PreformattedBlock());
		}

		blocks.add(new TableBlock());

		for (Block block : blocks) {
			if (block instanceof ParagraphBlock) {
				continue;
			}
			paragraphBreakingBlocks.add(block);
		}

	}

	private boolean hasPreformattedBlock(MarkupLanguageConfiguration configuration) {
		return configuration == null ? true : !configuration.isOptimizeForRepositoryUsage();
	}

	@Override
	protected void addStandardPhraseModifiers(MarkupLanguageConfiguration configuration,
			PatternBasedSyntax phraseModifierSyntax) {
		phraseModifierSyntax.beginGroup("(?:(?<=[\\s\\.,\\\"'?!;:\\)\\(\\{\\}\\[\\]])|^)(?:", 0); //$NON-NLS-1$
		phraseModifierSyntax.add(new EscapePhraseModifier());
		phraseModifierSyntax.add(new SimplePhraseModifier("'''''", new SpanType[] { SpanType.BOLD, SpanType.ITALIC }, //$NON-NLS-1$
				true));
		phraseModifierSyntax.add(new SimplePhraseModifier("'''", SpanType.BOLD, true)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier("''", SpanType.ITALIC, true)); //$NON-NLS-1$
		phraseModifierSyntax.endGroup(")(?=\\W|$)", 0); //$NON-NLS-1$

		boolean escapingHtml = configuration == null ? false : configuration.isEscapingHtmlAndXml();

		if (!escapingHtml) {
			String[] allowedHtmlTags = new String[] { // HANDLED BY LineBreakToken "<br>",
					// HANDLED BY LineBreakToken "<br/>",
					"b", "big", "blockquote", "caption", "center", "cite", "code", "dd", "del", "div", "dl", "dt", "em", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$
					"font", "h1", "h2", "h3", "h4", "h5", "h6", "hr", "i", "ins", "li", "ol", "p", "pre", "rb", "rp", "rt", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$ //$NON-NLS-14$ //$NON-NLS-15$ //$NON-NLS-16$ //$NON-NLS-17$
					"ruby", "s", "small", "span", "strike", "strong", "sub", "sup", "table", "td", "th", "tr", "tt", "u", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$ //$NON-NLS-14$
					"ul", "var" }; //$NON-NLS-1$ //$NON-NLS-2$
			phraseModifierSyntax.add(new LimitedHtmlEndTagPhraseModifier(allowedHtmlTags));
			phraseModifierSyntax.add(new LimitedHtmlStartTagPhraseModifier(allowedHtmlTags));
			phraseModifierSyntax.add(new HtmlCommentPhraseModifier());
		}
	}

	@Override
	protected void addStandardTokens(MarkupLanguageConfiguration configuration, PatternBasedSyntax tokenSyntax) {
		tokenSyntax.add(new LineBreakToken());
		tokenSyntax.add(new EntityReferenceReplacementToken("(tm)", "#8482")); //$NON-NLS-1$ //$NON-NLS-2$
		tokenSyntax.add(new EntityReferenceReplacementToken("(TM)", "#8482")); //$NON-NLS-1$ //$NON-NLS-2$
		tokenSyntax.add(new EntityReferenceReplacementToken("(c)", "#169")); //$NON-NLS-1$ //$NON-NLS-2$
		tokenSyntax.add(new EntityReferenceReplacementToken("(C)", "#169")); //$NON-NLS-1$ //$NON-NLS-2$
		tokenSyntax.add(new EntityReferenceReplacementToken("(r)", "#174")); //$NON-NLS-1$ //$NON-NLS-2$
		tokenSyntax.add(new EntityReferenceReplacementToken("(R)", "#174")); //$NON-NLS-1$ //$NON-NLS-2$
		tokenSyntax.add(new ImageReplacementToken());
		tokenSyntax.add(new HyperlinkInternalReplacementToken());
		tokenSyntax.add(new HyperlinkExternalReplacementToken());
		tokenSyntax.add(new ImpliedHyperlinkReplacementToken());
		tokenSyntax.add(new PatternLiteralReplacementToken("(?:(?<=\\w\\s)(----)(?=\\s\\w))", "<hr/>")); // horizontal rule //$NON-NLS-1$ //$NON-NLS-2$
		tokenSyntax.add(new TemplateReplacementToken());
		tokenSyntax.add(new org.eclipse.mylyn.internal.wikitext.mediawiki.core.token.EntityReferenceReplacementToken());
	}

	@Override
	protected Block createParagraphBlock(MarkupLanguageConfiguration configuration) {
		ParagraphBlock paragraphBlock = new ParagraphBlock(hasPreformattedBlock(configuration));
		if (configuration != null && configuration.isNewlinesMustCauseLineBreak()) {
			paragraphBlock.setNewlinesCauseLineBreak(true);
		}
		return paragraphBlock;
	}

}
