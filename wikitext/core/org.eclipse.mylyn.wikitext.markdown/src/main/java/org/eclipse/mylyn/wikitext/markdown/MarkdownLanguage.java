/*******************************************************************************
 * Copyright (c) 2012, 2021 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *     Alexander Nyßen - support for inline links in phrases
 *     Pierre-Yves B. <pyvesdev@gmail.com> - Bug 552231 - Styling should not apply inside words
 *     Pierre-Yves B. <pyvesdev@gmail.com> - Bug 509033 - markdown misses
 *     Max Bureck (Fraunhofer FOKUS) - Bug 559037 - Extended automatic link replacement
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.markdown;

import java.io.Writer;
import java.util.List;

import org.eclipse.mylyn.wikitext.markdown.internal.GfmIdGenerationStrategy;
import org.eclipse.mylyn.wikitext.markdown.internal.MarkdownContentState;
import org.eclipse.mylyn.wikitext.markdown.internal.MarkdownDocumentBuilder;
import org.eclipse.mylyn.wikitext.markdown.internal.block.CodeBlock;
import org.eclipse.mylyn.wikitext.markdown.internal.block.HeadingBlock;
import org.eclipse.mylyn.wikitext.markdown.internal.block.HorizontalRuleBlock;
import org.eclipse.mylyn.wikitext.markdown.internal.block.InlineHtmlBlock;
import org.eclipse.mylyn.wikitext.markdown.internal.block.LinkDefinitionBlock;
import org.eclipse.mylyn.wikitext.markdown.internal.block.ListBlock;
import org.eclipse.mylyn.wikitext.markdown.internal.block.ParagraphBlock;
import org.eclipse.mylyn.wikitext.markdown.internal.block.QuoteBlock;
import org.eclipse.mylyn.wikitext.markdown.internal.block.UnderlinedHeadingBlock;
import org.eclipse.mylyn.wikitext.markdown.internal.phrase.BackslashEscapePhraseModifier;
import org.eclipse.mylyn.wikitext.markdown.internal.phrase.ExtendedAutomaticLinkReplacementToken;
import org.eclipse.mylyn.wikitext.markdown.internal.phrase.SimplePhraseModifier;
import org.eclipse.mylyn.wikitext.markdown.internal.phrase.SimpleWordModifier;
import org.eclipse.mylyn.wikitext.markdown.internal.token.AutomaticLinkReplacementToken;
import org.eclipse.mylyn.wikitext.markdown.internal.token.InlineImageReplacementToken;
import org.eclipse.mylyn.wikitext.markdown.internal.token.InlineLinkReplacementToken;
import org.eclipse.mylyn.wikitext.markdown.internal.token.PreserverHtmlEntityToken;
import org.eclipse.mylyn.wikitext.markdown.internal.token.ReferenceStyleImageReplacementToken;
import org.eclipse.mylyn.wikitext.markdown.internal.token.ReferenceStyleLinkReplacementToken;
import org.eclipse.mylyn.wikitext.markdown.internal.util.ReadAheadDispatcher;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.markup.AbstractMarkupLanguage;
import org.eclipse.mylyn.wikitext.parser.markup.Block;
import org.eclipse.mylyn.wikitext.parser.markup.ContentState;
import org.eclipse.mylyn.wikitext.parser.markup.IdGenerationStrategy;
import org.eclipse.mylyn.wikitext.parser.markup.phrase.HtmlEndTagPhraseModifier;
import org.eclipse.mylyn.wikitext.parser.markup.phrase.HtmlStartTagPhraseModifier;
import org.eclipse.mylyn.wikitext.parser.markup.token.PatternLineBreakReplacementToken;

/**
 * A markup language implementing Markdown syntax. http://daringfireball.net/projects/markdown/syntax
 *
 * @author Stefan Seelmann
 * @author Alexander Nyßen
 * @since 3.0
 */
public class MarkdownLanguage extends AbstractMarkupLanguage {

	private final boolean enableHeuristicFeatures;

	/**
	 * Constructs an instance of MarkdownLanguage with heuristic features disabled.
	 */
	public MarkdownLanguage() {
		this(false);
	}

	/**
	 * Constructs an instance of MarkdownLanguage, with the choice to enable heuristic features. Currently only extended
	 * hyperlink detection (without delimiters) is supported as a heuristic feature
	 *
	 * @param enableHeuristicFeatures
	 *            if {@code true} enables heristic features.
	 */
	public MarkdownLanguage(boolean enableHeuristicFeatures) {
		this.enableHeuristicFeatures = enableHeuristicFeatures;
		setName("Markdown"); //$NON-NLS-1$
	}

	@Override
	protected void addStandardTokens(PatternBasedSyntax tokenSyntax) {
		// HTML entities are preserved
		tokenSyntax.add(new PreserverHtmlEntityToken());
		// inline links have to be handled as tokens, as they can be embedded in phrases
		tokenSyntax.add(new InlineLinkReplacementToken());
		tokenSyntax.add(new InlineImageReplacementToken());
		// two or more spaces at end of line force a line break
		tokenSyntax.add(new PatternLineBreakReplacementToken("( {2,})$")); //$NON-NLS-1$
	}

	@Override
	protected void addStandardPhraseModifiers(PatternBasedSyntax phraseModifierSyntax) {
		// inline HTML
		phraseModifierSyntax.add(new HtmlEndTagPhraseModifier());
		phraseModifierSyntax.add(new HtmlStartTagPhraseModifier());
		// images
		phraseModifierSyntax.add(new InlineImageReplacementToken());
		phraseModifierSyntax.add(new ReferenceStyleImageReplacementToken());
		// links
		phraseModifierSyntax.add(new InlineLinkReplacementToken());
		phraseModifierSyntax.add(new ReferenceStyleLinkReplacementToken());
		phraseModifierSyntax.add(new AutomaticLinkReplacementToken());
		// backslash escaped span elements
		phraseModifierSyntax.add(new BackslashEscapePhraseModifier("**")); //$NON-NLS-1$
		phraseModifierSyntax.add(new BackslashEscapePhraseModifier("__")); //$NON-NLS-1$
		phraseModifierSyntax.add(new BackslashEscapePhraseModifier("~~")); //$NON-NLS-1$
		phraseModifierSyntax.add(new BackslashEscapePhraseModifier("*")); //$NON-NLS-1$
		phraseModifierSyntax.add(new BackslashEscapePhraseModifier("_")); //$NON-NLS-1$
		phraseModifierSyntax.add(new BackslashEscapePhraseModifier("~")); //$NON-NLS-1$
		phraseModifierSyntax.add(new BackslashEscapePhraseModifier("\\")); //$NON-NLS-1$
		phraseModifierSyntax.add(new BackslashEscapePhraseModifier("`")); //$NON-NLS-1$
		phraseModifierSyntax.add(new BackslashEscapePhraseModifier("{")); //$NON-NLS-1$
		phraseModifierSyntax.add(new BackslashEscapePhraseModifier("}")); //$NON-NLS-1$
		phraseModifierSyntax.add(new BackslashEscapePhraseModifier("[")); //$NON-NLS-1$
		phraseModifierSyntax.add(new BackslashEscapePhraseModifier("]")); //$NON-NLS-1$
		phraseModifierSyntax.add(new BackslashEscapePhraseModifier("(")); //$NON-NLS-1$
		phraseModifierSyntax.add(new BackslashEscapePhraseModifier(")")); //$NON-NLS-1$
		phraseModifierSyntax.add(new BackslashEscapePhraseModifier("#")); //$NON-NLS-1$
		phraseModifierSyntax.add(new BackslashEscapePhraseModifier("+")); //$NON-NLS-1$
		phraseModifierSyntax.add(new BackslashEscapePhraseModifier("-")); //$NON-NLS-1$
		phraseModifierSyntax.add(new BackslashEscapePhraseModifier(".")); //$NON-NLS-1$
		phraseModifierSyntax.add(new BackslashEscapePhraseModifier("!")); //$NON-NLS-1$
		// emphasis span elements
		phraseModifierSyntax.add(new SimplePhraseModifier("``", SpanType.CODE)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier("`", SpanType.CODE)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier("**", SpanType.STRONG)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimpleWordModifier("__", SpanType.STRONG)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier("*", SpanType.EMPHASIS)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimpleWordModifier("_", SpanType.EMPHASIS)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier("~~", SpanType.DELETED)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier("~", SpanType.DELETED)); //$NON-NLS-1$
		if (enableHeuristicFeatures) {
			phraseModifierSyntax.add(new ExtendedAutomaticLinkReplacementToken());
		}
	}

	@Override
	protected void addStandardBlocks(List<Block> blocks, List<Block> paragraphBreakingBlocks) {
		CodeBlock codeBlock = new CodeBlock();
		HorizontalRuleBlock horizontalRuleBlock = new HorizontalRuleBlock();
		HeadingBlock headingBlock = new HeadingBlock();
		InlineHtmlBlock inlineHtmlBlock = new InlineHtmlBlock();
		QuoteBlock quoteBlock = new QuoteBlock();
		ListBlock listBlock = new ListBlock();
		LinkDefinitionBlock linkDefinitionBlock = new LinkDefinitionBlock();

		blocks.add(codeBlock);
		blocks.add(horizontalRuleBlock);
		blocks.add(headingBlock);
		blocks.add(inlineHtmlBlock);
		blocks.add(quoteBlock);
		blocks.add(listBlock);
		blocks.add(linkDefinitionBlock);

		paragraphBreakingBlocks.add(horizontalRuleBlock);
		paragraphBreakingBlocks.add(headingBlock);
		paragraphBreakingBlocks.add(quoteBlock);
		paragraphBreakingBlocks.add(listBlock);
	}

	@Override
	protected Block createParagraphBlock() {
		ParagraphBlock paragraphBlock = new ParagraphBlock();
		UnderlinedHeadingBlock headingBlock = new UnderlinedHeadingBlock();
		return new ReadAheadDispatcher(headingBlock, paragraphBlock);
	}

	@Override
	protected ContentState createState() {
		return new MarkdownContentState();
	}

	@Override
	public DocumentBuilder createDocumentBuilder(Writer out, boolean formatting) {
		return new MarkdownDocumentBuilder(out);
	}

	@Override
	public IdGenerationStrategy getIdGenerationStrategy() {
		return new GfmIdGenerationStrategy();
	}

}
