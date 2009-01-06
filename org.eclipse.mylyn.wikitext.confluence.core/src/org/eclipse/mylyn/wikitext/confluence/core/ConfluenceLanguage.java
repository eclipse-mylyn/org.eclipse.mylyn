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
package org.eclipse.mylyn.wikitext.confluence.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylyn.internal.wikitext.confluence.core.block.CodeBlock;
import org.eclipse.mylyn.internal.wikitext.confluence.core.block.ExtendedPreformattedBlock;
import org.eclipse.mylyn.internal.wikitext.confluence.core.block.ExtendedQuoteBlock;
import org.eclipse.mylyn.internal.wikitext.confluence.core.block.HeadingBlock;
import org.eclipse.mylyn.internal.wikitext.confluence.core.block.ListBlock;
import org.eclipse.mylyn.internal.wikitext.confluence.core.block.ParagraphBlock;
import org.eclipse.mylyn.internal.wikitext.confluence.core.block.QuoteBlock;
import org.eclipse.mylyn.internal.wikitext.confluence.core.block.TableBlock;
import org.eclipse.mylyn.internal.wikitext.confluence.core.block.TableOfContentsBlock;
import org.eclipse.mylyn.internal.wikitext.confluence.core.block.TextBoxBlock;
import org.eclipse.mylyn.internal.wikitext.confluence.core.phrase.ImagePhraseModifier;
import org.eclipse.mylyn.internal.wikitext.confluence.core.phrase.SimplePhraseModifier;
import org.eclipse.mylyn.internal.wikitext.confluence.core.phrase.SimpleWrappedPhraseModifier;
import org.eclipse.mylyn.internal.wikitext.confluence.core.token.AnchorReplacementToken;
import org.eclipse.mylyn.internal.wikitext.confluence.core.token.HyperlinkReplacementToken;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.core.parser.markup.token.EntityReferenceReplacementToken;
import org.eclipse.mylyn.wikitext.core.parser.markup.token.ImpliedHyperlinkReplacementToken;
import org.eclipse.mylyn.wikitext.core.parser.markup.token.PatternEntityReferenceReplacementToken;
import org.eclipse.mylyn.wikitext.core.parser.markup.token.PatternLineBreakReplacementToken;
import org.eclipse.mylyn.wikitext.core.parser.markup.token.PatternLiteralReplacementToken;

/**
 * A confluence language for parsing Confluence markup.
 * 
 * @author David Green
 */
public class ConfluenceLanguage extends MarkupLanguage {

	private final List<Block> blocks = new ArrayList<Block>();

	private final List<Block> paragraphBreakingBlocks = new ArrayList<Block>();

	/**
	 * blocks that may be nested in side a quote block
	 * 
	 * @see ExtendedQuoteBlock
	 */
	private final List<Block> nestedBlocks = new ArrayList<Block>();

	private PatternBasedSyntax tokenSyntax;

	private PatternBasedSyntax phraseModifierSyntax;

	private boolean syntaxInitialized;

	public ConfluenceLanguage() {
		setName("Confluence"); //$NON-NLS-1$
	}

	@Override
	public void processContent(MarkupParser parser, String markupContent, boolean asDocument) {
		if (!syntaxInitialized) {
			syntaxInitialized = true;
			initializeSyntax();
		}
		super.processContent(parser, markupContent, asDocument);
	}

	protected void initializeSyntax() {
		resetSyntax();

		initializeBlocks();
		initializePhraseModifiers();
		initializeTokens();
	}

	protected void initializeTokens() {
		tokenSyntax.add(new EntityReferenceReplacementToken("(tm)", "#8482")); //$NON-NLS-1$ //$NON-NLS-2$
		tokenSyntax.add(new EntityReferenceReplacementToken("(TM)", "#8482")); //$NON-NLS-1$ //$NON-NLS-2$
		tokenSyntax.add(new EntityReferenceReplacementToken("(c)", "#169")); //$NON-NLS-1$ //$NON-NLS-2$
		tokenSyntax.add(new EntityReferenceReplacementToken("(C)", "#169")); //$NON-NLS-1$ //$NON-NLS-2$
		tokenSyntax.add(new EntityReferenceReplacementToken("(r)", "#174")); //$NON-NLS-1$ //$NON-NLS-2$
		tokenSyntax.add(new EntityReferenceReplacementToken("(R)", "#174")); //$NON-NLS-1$ //$NON-NLS-2$
		tokenSyntax.add(new HyperlinkReplacementToken());
		tokenSyntax.add(new PatternEntityReferenceReplacementToken("(?:(?<=\\w\\s)(---)(?=\\s\\w))", "#8212")); // emdash //$NON-NLS-1$ //$NON-NLS-2$
		tokenSyntax.add(new PatternEntityReferenceReplacementToken("(?:(?<=\\w\\s)(--)(?=\\s\\w))", "#8211")); // endash //$NON-NLS-1$ //$NON-NLS-2$
		tokenSyntax.add(new PatternLiteralReplacementToken("(?:(?<=\\w\\s)(----)(?=\\s\\w))", "<hr/>")); // horizontal rule //$NON-NLS-1$ //$NON-NLS-2$
		tokenSyntax.add(new PatternLineBreakReplacementToken("(\\\\\\\\)")); // line break //$NON-NLS-1$
		tokenSyntax.add(new ImpliedHyperlinkReplacementToken());
		tokenSyntax.add(new AnchorReplacementToken());

		addTokenExtensions(tokenSyntax);
	}

	protected void initializePhraseModifiers() {
		phraseModifierSyntax.beginGroup("(?:(?<=[\\s\\.,\\\"'?!;:\\)\\(\\[\\]])|^)(?:", 0); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier("*", SpanType.STRONG, true)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier("_", SpanType.EMPHASIS, true)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier("??", SpanType.CITATION, true)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier("-", SpanType.DELETED, true)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier("+", SpanType.UNDERLINED, true)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier("^", SpanType.SUPERSCRIPT, false)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier("~", SpanType.SUBSCRIPT, false)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimpleWrappedPhraseModifier("{{", "}}", SpanType.MONOSPACE, false)); //$NON-NLS-1$ //$NON-NLS-2$
		phraseModifierSyntax.add(new ImagePhraseModifier());
		phraseModifierSyntax.endGroup(")(?=\\W|$)", 0); //$NON-NLS-1$

		addPhraseModifierExtensions(phraseModifierSyntax);
	}

	protected void initializeBlocks() {
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

		blocks.add(new TextBoxBlock(BlockType.PANEL, "panel")); //$NON-NLS-1$
		blocks.add(new TextBoxBlock(BlockType.NOTE, "note")); //$NON-NLS-1$
		blocks.add(new TextBoxBlock(BlockType.INFORMATION, "info")); //$NON-NLS-1$
		blocks.add(new TextBoxBlock(BlockType.WARNING, "warning")); //$NON-NLS-1$
		blocks.add(new TextBoxBlock(BlockType.TIP, "tip")); //$NON-NLS-1$
		CodeBlock codeBlock = new CodeBlock();
		blocks.add(codeBlock);
		paragraphBreakingBlocks.add(codeBlock);
		blocks.add(new TableOfContentsBlock());

		// extensions
		addBlockExtensions(blocks, paragraphBreakingBlocks);
		// ~extensions

		blocks.add(new ParagraphBlock()); // ORDER DEPENDENCY: this must come last
	}

	private void resetSyntax() {
		blocks.clear();
		paragraphBreakingBlocks.clear();
		tokenSyntax = new PatternBasedSyntax();
		phraseModifierSyntax = new PatternBasedSyntax();
	}

	/**
	 * subclasses may override this method to add blocks to the Textile language. Overriding classes should call
	 * <code>super.addBlockExtensions(blocks,paragraphBreakingBlocks)</code> if the default language extensions are
	 * desired.
	 * 
	 * @param blocks
	 *            the list of blocks to which extensions may be added
	 * @param paragraphBreakingBlocks
	 *            the list of blocks that end a paragraph
	 */
	protected void addBlockExtensions(List<Block> blocks, List<Block> paragraphBreakingBlocks) {
		// add block extensions
	}

	/**
	 * subclasses may override this method to add tokens to the Textile language. Overriding classes should call
	 * <code>super.addTokenExtensions(tokenSyntax)</code> if the default language extensions are desired.
	 * 
	 * @param tokenSyntax
	 *            the token syntax
	 */
	protected void addTokenExtensions(PatternBasedSyntax tokenSyntax) {
		// no token extensions
	}

	/**
	 * subclasses may override this method to add phrases to the Textile language. Overriding classes should call
	 * <code>super.addPhraseModifierExtensions(phraseModifierSyntax)</code> if the default language extensions are
	 * desired.
	 * 
	 * @param phraseModifierSyntax
	 *            the phrase modifier syntax
	 */
	protected void addPhraseModifierExtensions(PatternBasedSyntax phraseModifierSyntax) {
		// no phrase extensions
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

	public List<Block> getNestedBlocks() {
		return nestedBlocks;
	}
}
