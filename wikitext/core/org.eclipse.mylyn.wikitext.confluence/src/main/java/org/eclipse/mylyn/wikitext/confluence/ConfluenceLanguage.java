/*******************************************************************************
 * Copyright (c) 2007, 2015 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     Benjamin Muskalla - bug 469970
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.confluence;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylyn.wikitext.confluence.internal.ConfluenceContentState;
import org.eclipse.mylyn.wikitext.confluence.internal.ConfluenceDocumentBuilder;
import org.eclipse.mylyn.wikitext.confluence.internal.block.CodeBlock;
import org.eclipse.mylyn.wikitext.confluence.internal.block.ColorBlock;
import org.eclipse.mylyn.wikitext.confluence.internal.block.ExtendedPreformattedBlock;
import org.eclipse.mylyn.wikitext.confluence.internal.block.ExtendedQuoteBlock;
import org.eclipse.mylyn.wikitext.confluence.internal.block.HeadingBlock;
import org.eclipse.mylyn.wikitext.confluence.internal.block.HorizontalRuleBlock;
import org.eclipse.mylyn.wikitext.confluence.internal.block.ListBlock;
import org.eclipse.mylyn.wikitext.confluence.internal.block.ParagraphBlock;
import org.eclipse.mylyn.wikitext.confluence.internal.block.QuoteBlock;
import org.eclipse.mylyn.wikitext.confluence.internal.block.TableBlock;
import org.eclipse.mylyn.wikitext.confluence.internal.block.TableOfContentsBlock;
import org.eclipse.mylyn.wikitext.confluence.internal.block.TextBoxBlock;
import org.eclipse.mylyn.wikitext.confluence.internal.phrase.ColorPhraseModifier;
import org.eclipse.mylyn.wikitext.confluence.internal.phrase.ConfluenceWrappedPhraseModifier;
import org.eclipse.mylyn.wikitext.confluence.internal.phrase.EmphasisPhraseModifier;
import org.eclipse.mylyn.wikitext.confluence.internal.phrase.HyperlinkPhraseModifier;
import org.eclipse.mylyn.wikitext.confluence.internal.phrase.ImagePhraseModifier;
import org.eclipse.mylyn.wikitext.confluence.internal.phrase.SimplePhraseModifier;
import org.eclipse.mylyn.wikitext.confluence.internal.phrase.SimpleWrappedPhraseModifier;
import org.eclipse.mylyn.wikitext.confluence.internal.token.AnchorReplacementToken;
import org.eclipse.mylyn.wikitext.confluence.internal.token.EscapedCharacterReplacementToken;
import org.eclipse.mylyn.wikitext.confluence.internal.token.ImpliedHyperlinkReplacementToken;
import org.eclipse.mylyn.wikitext.confluence.internal.token.NumericEntityReferenceReplacementToken;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.markup.AbstractMarkupLanguage;
import org.eclipse.mylyn.wikitext.parser.markup.Block;
import org.eclipse.mylyn.wikitext.parser.markup.ContentState;
import org.eclipse.mylyn.wikitext.parser.markup.token.EntityReferenceReplacementToken;
import org.eclipse.mylyn.wikitext.parser.markup.token.PatternEntityReferenceReplacementToken;
import org.eclipse.mylyn.wikitext.parser.markup.token.PatternLineBreakReplacementToken;

/**
 * A confluence language for parsing Confluence markup.
 *
 * @author David Green
 * @see <a href="http://confluence.atlassian.com/display/DOC/Confluence+Notation+Guide+Overview">Confluence Notation
 *      Guide Overview</a>
 * @since 3.0
 */
public class ConfluenceLanguage extends AbstractMarkupLanguage {
	/**
	 * blocks that may be nested in side a quote block
	 *
	 * @see ExtendedQuoteBlock
	 */
	private final List<Block> nestedBlocks = new ArrayList<Block>();

	private boolean parseRelativeLinks = true;

	public ConfluenceLanguage() {
		setName("Confluence"); //$NON-NLS-1$
	}

	@Override
	protected void clearLanguageSyntax() {
		super.clearLanguageSyntax();
		nestedBlocks.clear();
	}

	public List<Block> getNestedBlocks() {
		return nestedBlocks;
	}

	@Override
	protected void addStandardBlocks(List<Block> blocks, List<Block> paragraphBreakingBlocks) {
		// IMPORTANT NOTE: Most items below have order dependencies.  DO NOT REORDER ITEMS BELOW!!

		HeadingBlock headingBlock = new HeadingBlock();
		blocks.add(headingBlock);
		paragraphBreakingBlocks.add(headingBlock);
		nestedBlocks.add(headingBlock);
		ListBlock listBlock = new ListBlock();
		blocks.add(listBlock);
		paragraphBreakingBlocks.add(listBlock);
		nestedBlocks.add(listBlock);
		blocks.add(new QuoteBlock());
		TableBlock tableBlock = new TableBlock();
		blocks.add(tableBlock);
		paragraphBreakingBlocks.add(tableBlock);
		nestedBlocks.add(tableBlock);
		ExtendedQuoteBlock quoteBlock = new ExtendedQuoteBlock();
		blocks.add(quoteBlock);
		paragraphBreakingBlocks.add(quoteBlock);
		ExtendedPreformattedBlock noformatBlock = new ExtendedPreformattedBlock();
		blocks.add(noformatBlock);
		paragraphBreakingBlocks.add(noformatBlock);
		HorizontalRuleBlock horizontalRuleBlock = new HorizontalRuleBlock();
		blocks.add(horizontalRuleBlock);
		paragraphBreakingBlocks.add(horizontalRuleBlock);

		blocks.add(new TextBoxBlock(BlockType.PANEL, "panel")); //$NON-NLS-1$
		blocks.add(new TextBoxBlock(BlockType.NOTE, "note")); //$NON-NLS-1$
		blocks.add(new TextBoxBlock(BlockType.INFORMATION, "info")); //$NON-NLS-1$
		blocks.add(new TextBoxBlock(BlockType.WARNING, "warning")); //$NON-NLS-1$
		blocks.add(new TextBoxBlock(BlockType.TIP, "tip")); //$NON-NLS-1$
		CodeBlock codeBlock = new CodeBlock();
		blocks.add(codeBlock);
		paragraphBreakingBlocks.add(codeBlock);
		blocks.add(new TableOfContentsBlock());
		ColorBlock colorBlock = new ColorBlock();
		blocks.add(colorBlock);
		paragraphBreakingBlocks.add(colorBlock);
	}

	@Override
	protected void addStandardPhraseModifiers(PatternBasedSyntax phraseModifierSyntax) {
		phraseModifierSyntax.beginGroup("(?:(?<=[\\s\\.,\\\"'?!;:\\)\\(\\[\\]])|^)(?:", 0); //$NON-NLS-1$
		phraseModifierSyntax.add(new HyperlinkPhraseModifier(parseRelativeLinks));
		phraseModifierSyntax.add(new SimplePhraseModifier("*", SpanType.STRONG, true)); //$NON-NLS-1$
		phraseModifierSyntax.add(new EmphasisPhraseModifier());
		phraseModifierSyntax.add(new SimplePhraseModifier("??", SpanType.CITATION, true)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier("-", SpanType.DELETED, true)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier("+", SpanType.UNDERLINED, true)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier("^", SpanType.SUPERSCRIPT, false)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier("~", SpanType.SUBSCRIPT, false)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier("@", SpanType.CODE, false)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimpleWrappedPhraseModifier("{{", "}}", SpanType.MONOSPACE, false)); //$NON-NLS-1$ //$NON-NLS-2$
		phraseModifierSyntax.add(new ConfluenceWrappedPhraseModifier("{quote}", SpanType.QUOTE, true)); //$NON-NLS-1$
		phraseModifierSyntax.add(new ColorPhraseModifier());
		phraseModifierSyntax.add(new ImagePhraseModifier());
		phraseModifierSyntax.endGroup(")(?=\\W|$)", 0); //$NON-NLS-1$
	}

	@Override
	protected void addStandardTokens(PatternBasedSyntax tokenSyntax) {
		tokenSyntax.add(new PatternLineBreakReplacementToken("(\\\\\\\\ ?)")); // line break //$NON-NLS-1$
		tokenSyntax.add(new EscapedCharacterReplacementToken()); // ORDER DEPENDENCY must come after line break
		tokenSyntax.add(new EntityReferenceReplacementToken("(tm)", "#8482")); //$NON-NLS-1$ //$NON-NLS-2$
		tokenSyntax.add(new EntityReferenceReplacementToken("(TM)", "#8482")); //$NON-NLS-1$ //$NON-NLS-2$
		tokenSyntax.add(new EntityReferenceReplacementToken("(c)", "#169")); //$NON-NLS-1$ //$NON-NLS-2$
		tokenSyntax.add(new EntityReferenceReplacementToken("(C)", "#169")); //$NON-NLS-1$ //$NON-NLS-2$
		tokenSyntax.add(new EntityReferenceReplacementToken("(r)", "#174")); //$NON-NLS-1$ //$NON-NLS-2$
		tokenSyntax.add(new EntityReferenceReplacementToken("(R)", "#174")); //$NON-NLS-1$ //$NON-NLS-2$
		tokenSyntax.add(new PatternEntityReferenceReplacementToken("(?:(?<=(?:\\w\\s)|^)(---)(?=\\s\\w))", "#8212")); // emdash //$NON-NLS-1$ //$NON-NLS-2$
		tokenSyntax.add(new PatternEntityReferenceReplacementToken("(?:(?<=(?:\\w\\s)|^)(--)(?=\\s\\w))", "#8211")); // endash //$NON-NLS-1$ //$NON-NLS-2$
		tokenSyntax.add(new ImpliedHyperlinkReplacementToken());
		tokenSyntax.add(new AnchorReplacementToken());
		tokenSyntax.add(new NumericEntityReferenceReplacementToken());
	}

	@Override
	protected Block createParagraphBlock() {
		return new ParagraphBlock();
	}

	/**
	 *
	 */
	@Override
	public DocumentBuilder createDocumentBuilder(Writer out, boolean formatting) {
		return new ConfluenceDocumentBuilder(out);
	}

	/**
	 * Indicates if relative links (e.g. Confluence pages) should be treated as links.
	 *
	 * @param parseRelativeLinks
	 *            if relative links should be parsed
	 */
	public void setParseRelativeLinks(boolean parseRelativeLinks) {
		this.parseRelativeLinks = parseRelativeLinks;
	}

	/**
	 * Indicates if relative links (e.g. Confluence pages) are treated as links.
	 *
	 * @return {@code true} if relative links should be parsed as links, otherwise {@code false}
	 */
	public boolean isParseRelativeLinks() {
		return parseRelativeLinks;
	}

	/**
	 *
	 */
	@Override
	public ConfluenceLanguage clone() {
		ConfluenceLanguage copy = (ConfluenceLanguage) super.clone();
		copy.parseRelativeLinks = parseRelativeLinks;
		return copy;
	}

	@Override
	protected ContentState createState() {
		return new ConfluenceContentState();
	}
}
