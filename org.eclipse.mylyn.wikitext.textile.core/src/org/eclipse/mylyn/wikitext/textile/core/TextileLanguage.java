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
package org.eclipse.mylyn.wikitext.textile.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylyn.internal.wikitext.textile.core.TextileContentState;
import org.eclipse.mylyn.internal.wikitext.textile.core.block.CodeBlock;
import org.eclipse.mylyn.internal.wikitext.textile.core.block.FootnoteBlock;
import org.eclipse.mylyn.internal.wikitext.textile.core.block.HeadingBlock;
import org.eclipse.mylyn.internal.wikitext.textile.core.block.ListBlock;
import org.eclipse.mylyn.internal.wikitext.textile.core.block.ParagraphBlock;
import org.eclipse.mylyn.internal.wikitext.textile.core.block.PreformattedBlock;
import org.eclipse.mylyn.internal.wikitext.textile.core.block.QuoteBlock;
import org.eclipse.mylyn.internal.wikitext.textile.core.block.TableBlock;
import org.eclipse.mylyn.internal.wikitext.textile.core.block.TableOfContentsBlock;
import org.eclipse.mylyn.internal.wikitext.textile.core.block.TextileGlossaryBlock;
import org.eclipse.mylyn.internal.wikitext.textile.core.phrase.EscapeTextilePhraseModifier;
import org.eclipse.mylyn.internal.wikitext.textile.core.phrase.ImageTextilePhraseModifier;
import org.eclipse.mylyn.internal.wikitext.textile.core.phrase.SimpleTextilePhraseModifier;
import org.eclipse.mylyn.internal.wikitext.textile.core.token.FootnoteReferenceReplacementToken;
import org.eclipse.mylyn.internal.wikitext.textile.core.token.HyperlinkReplacementToken;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;
import org.eclipse.mylyn.wikitext.core.parser.markup.ContentState;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.core.parser.markup.phrase.HtmlEndTagPhraseModifier;
import org.eclipse.mylyn.wikitext.core.parser.markup.phrase.HtmlStartTagPhraseModifier;
import org.eclipse.mylyn.wikitext.core.parser.markup.token.AcronymReplacementToken;
import org.eclipse.mylyn.wikitext.core.parser.markup.token.EntityReferenceReplacementToken;
import org.eclipse.mylyn.wikitext.core.parser.markup.token.EntityWrappingReplacementToken;
import org.eclipse.mylyn.wikitext.core.parser.markup.token.PatternEntityReferenceReplacementToken;

/**
 * A textile dialect that parses <a href="http://en.wikipedia.org/wiki/Textile_(markup_language)">Textile markup</a>.
 * 
 * Based on the spec available at <a href="http://textile.thresholdstate.com/">http://textile.thresholdstate.com/</a>,
 * supports all current Textile markup constructs.
 * 
 * Additionally supported are <code>{toc}</code> and <code>{glossary}</code>.
 * 
 * @author David Green
 */
public class TextileLanguage extends MarkupLanguage {

	// we use the template pattern for creating new blocks
	private List<Block> blocks = new ArrayList<Block>();

	private List<Block> paragraphBreakingBlocks = new ArrayList<Block>();

	private PatternBasedSyntax tokenSyntax = new PatternBasedSyntax();

	private PatternBasedSyntax phraseModifierSyntax = new PatternBasedSyntax();

	@Override
	protected PatternBasedSyntax getPhraseModifierSyntax() {
		return phraseModifierSyntax;
	}

	@Override
	protected PatternBasedSyntax getReplacementTokenSyntax() {
		return tokenSyntax;
	}

	public TextileLanguage() {
		setName("Textile");
		initializeSyntax();
	}

	protected void initializeSyntax() {
		initializeBlocks();
		initializePhraseModifiers();
		initializeTokens();
	}

	protected void initializeTokens() {
		tokenSyntax.add(new EntityReferenceReplacementToken("(tm)", "#8482"));
		tokenSyntax.add(new EntityReferenceReplacementToken("(TM)", "#8482"));
		tokenSyntax.add(new EntityReferenceReplacementToken("(c)", "#169"));
		tokenSyntax.add(new EntityReferenceReplacementToken("(C)", "#169"));
		tokenSyntax.add(new EntityReferenceReplacementToken("(r)", "#174"));
		tokenSyntax.add(new EntityReferenceReplacementToken("(R)", "#174"));
		tokenSyntax.add(new HyperlinkReplacementToken());
		tokenSyntax.add(new FootnoteReferenceReplacementToken());
		tokenSyntax.add(new EntityWrappingReplacementToken("\"", "#8220", "#8221"));
		tokenSyntax.add(new EntityWrappingReplacementToken("'", "#8216", "#8217"));
		tokenSyntax.add(new PatternEntityReferenceReplacementToken("(?:(?<=\\w)(')(?=\\w))", "#8217")); // apostrophe
		tokenSyntax.add(new PatternEntityReferenceReplacementToken("(?:(?<=\\w\\s)(--)(?=\\s\\w))", "#8212")); // emdash
		tokenSyntax.add(new PatternEntityReferenceReplacementToken("(?:(?<=\\w\\s)(-)(?=\\s\\w))", "#8211")); // endash
		tokenSyntax.add(new PatternEntityReferenceReplacementToken("(?:(?<=\\d\\s)(x)(?=\\s\\d))", "#215")); // mul
		tokenSyntax.add(new AcronymReplacementToken());

		addTokenExtensions(tokenSyntax);
	}

	protected void initializePhraseModifiers() {
		phraseModifierSyntax.add(new HtmlEndTagPhraseModifier());
		phraseModifierSyntax.add(new HtmlStartTagPhraseModifier());
		phraseModifierSyntax.beginGroup("(?:(?<=[\\s\\.,\\\"'?!;:\\)\\(\\{\\}\\[\\]])|^)(?:", 0);
		phraseModifierSyntax.add(new EscapeTextilePhraseModifier());
		phraseModifierSyntax.add(new SimpleTextilePhraseModifier("**", SpanType.BOLD));
		phraseModifierSyntax.add(new SimpleTextilePhraseModifier("??", SpanType.CITATION));
		phraseModifierSyntax.add(new SimpleTextilePhraseModifier("__", SpanType.ITALIC));
		phraseModifierSyntax.add(new SimpleTextilePhraseModifier("_", SpanType.EMPHASIS));
		phraseModifierSyntax.add(new SimpleTextilePhraseModifier("*", SpanType.STRONG));
		phraseModifierSyntax.add(new SimpleTextilePhraseModifier("+", SpanType.INSERTED));
		phraseModifierSyntax.add(new SimpleTextilePhraseModifier("~", SpanType.SUBSCRIPT));
		phraseModifierSyntax.add(new SimpleTextilePhraseModifier("^", SpanType.SUPERSCRIPT));
		phraseModifierSyntax.add(new SimpleTextilePhraseModifier("@", SpanType.CODE));
		phraseModifierSyntax.add(new SimpleTextilePhraseModifier("%", SpanType.SPAN));
		phraseModifierSyntax.add(new SimpleTextilePhraseModifier("-", SpanType.DELETED));
		phraseModifierSyntax.add(new ImageTextilePhraseModifier());
		phraseModifierSyntax.endGroup(")(?=\\W|$)", 0);

		addPhraseModifierExtensions(phraseModifierSyntax);
	}

	protected void initializeBlocks() {
		// IMPORTANT NOTE: Most items below have order dependencies.  DO NOT REORDER ITEMS BELOW!!

		blocks.add(new HeadingBlock());
		ListBlock listBlock = new ListBlock();
		blocks.add(listBlock);
		paragraphBreakingBlocks.add(listBlock);
		blocks.add(new PreformattedBlock());
		blocks.add(new QuoteBlock());
		blocks.add(new CodeBlock());
		blocks.add(new FootnoteBlock());
		TableBlock tableBlock = new TableBlock();
		blocks.add(tableBlock);
		paragraphBreakingBlocks.add(tableBlock);

		// extensions
		addBlockExtensions(blocks, paragraphBreakingBlocks);
		// ~extensions

		blocks.add(new ParagraphBlock()); // ORDER DEPENDENCY: this must come last
	}

	/**
	 * subclasses may override this method to add blocks to the Textile language. Overriding classes should call
	 * <code>super.addBlockExtensions(blocks,paragraphBreakingBlocks)</code> if the default language extensions are
	 * desired (glossary and table of contents).
	 * 
	 * @param blocks
	 *            the list of blocks to which extensions may be added
	 * @param paragraphBreakingBlocks
	 *            the list of blocks that end a paragraph
	 * 
	 * @param paragraphBreakingBlocks
	 */
	protected void addBlockExtensions(List<Block> blocks, List<Block> paragraphBreakingBlocks) {
		blocks.add(new TextileGlossaryBlock());
		blocks.add(new TableOfContentsBlock());
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

	public List<Block> getParagraphBreakingBlocks() {
		return paragraphBreakingBlocks;
	}

	@Override
	public List<Block> getBlocks() {
		return blocks;
	}

	@Override
	protected ContentState createState() {
		return new TextileContentState();
	}
}
