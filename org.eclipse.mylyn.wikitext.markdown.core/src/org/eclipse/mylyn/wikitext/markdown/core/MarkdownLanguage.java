/*******************************************************************************
 * Copyright (c) 2012, 2013 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.markdown.core;

import java.util.List;

import org.eclipse.mylyn.internal.wikitext.markdown.core.MarkdownContentState;
import org.eclipse.mylyn.internal.wikitext.markdown.core.block.CodeBlock;
import org.eclipse.mylyn.internal.wikitext.markdown.core.block.HeadingBlock;
import org.eclipse.mylyn.internal.wikitext.markdown.core.block.HorizontalRuleBlock;
import org.eclipse.mylyn.internal.wikitext.markdown.core.block.InlineHtmlBlock;
import org.eclipse.mylyn.internal.wikitext.markdown.core.block.LinkDefinitionBlock;
import org.eclipse.mylyn.internal.wikitext.markdown.core.block.ListBlock;
import org.eclipse.mylyn.internal.wikitext.markdown.core.block.ParagraphBlock;
import org.eclipse.mylyn.internal.wikitext.markdown.core.block.QuoteBlock;
import org.eclipse.mylyn.internal.wikitext.markdown.core.block.UnderlinedHeadingBlock;
import org.eclipse.mylyn.internal.wikitext.markdown.core.phrase.BackslashEscapePhraseModifier;
import org.eclipse.mylyn.internal.wikitext.markdown.core.phrase.SimplePhraseModifier;
import org.eclipse.mylyn.internal.wikitext.markdown.core.token.AutomaticLinkReplacementToken;
import org.eclipse.mylyn.internal.wikitext.markdown.core.token.InlineImageReplacementToken;
import org.eclipse.mylyn.internal.wikitext.markdown.core.token.InlineLinkReplacementToken;
import org.eclipse.mylyn.internal.wikitext.markdown.core.token.PreserverHtmlEntityToken;
import org.eclipse.mylyn.internal.wikitext.markdown.core.token.ReferenceStyleImageReplacementToken;
import org.eclipse.mylyn.internal.wikitext.markdown.core.token.ReferenceStyleLinkReplacementToken;
import org.eclipse.mylyn.internal.wikitext.markdown.core.util.ReadAheadDispatcher;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.core.parser.markup.AbstractMarkupLanguage;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;
import org.eclipse.mylyn.wikitext.core.parser.markup.ContentState;
import org.eclipse.mylyn.wikitext.core.parser.markup.phrase.HtmlEndTagPhraseModifier;
import org.eclipse.mylyn.wikitext.core.parser.markup.phrase.HtmlStartTagPhraseModifier;
import org.eclipse.mylyn.wikitext.core.parser.markup.token.PatternLineBreakReplacementToken;

/**
 * A markup language implementing Markdown syntax. http://daringfireball.net/projects/markdown/syntax
 * 
 * @author Stefan Seelmann
 * @since 1.8
 */
public class MarkdownLanguage extends AbstractMarkupLanguage {

	public MarkdownLanguage() {
		setName("Markdown"); //$NON-NLS-1$
	}

	@Override
	protected void addStandardTokens(PatternBasedSyntax tokenSyntax) {
		// HTML entities are preserved
		tokenSyntax.add(new PreserverHtmlEntityToken());
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
		phraseModifierSyntax.add(new BackslashEscapePhraseModifier("*")); //$NON-NLS-1$ 
		phraseModifierSyntax.add(new BackslashEscapePhraseModifier("_")); //$NON-NLS-1$ 
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
		phraseModifierSyntax.add(new SimplePhraseModifier("__", SpanType.STRONG)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier("*", SpanType.EMPHASIS)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier("_", SpanType.EMPHASIS)); //$NON-NLS-1$
	}

	@Override
	protected void addStandardBlocks(List<Block> blocks, List<Block> paragraphBreakingBlocks) {
		blocks.add(new CodeBlock());
		blocks.add(new HorizontalRuleBlock());
		blocks.add(new HeadingBlock());
		blocks.add(new InlineHtmlBlock());
		blocks.add(new QuoteBlock());
		blocks.add(new ListBlock());
		blocks.add(new LinkDefinitionBlock());
	}

	@Override
	protected Block createParagraphBlock() {
		ParagraphBlock paragraphBlock = new ParagraphBlock();
		UnderlinedHeadingBlock headingBlock = new UnderlinedHeadingBlock();
		ReadAheadDispatcher readAheadBlock = new ReadAheadDispatcher(headingBlock, paragraphBlock);
		return readAheadBlock;
	}

	@Override
	protected ContentState createState() {
		return new MarkdownContentState();
	}
}
