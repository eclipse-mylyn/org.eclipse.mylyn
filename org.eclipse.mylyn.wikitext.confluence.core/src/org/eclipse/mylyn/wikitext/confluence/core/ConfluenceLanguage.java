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
 * 
 * 
 * @author David Green
 */
public class ConfluenceLanguage extends MarkupLanguage {

	private List<Block> blocks = new ArrayList<Block>();

	private List<Block> paragraphBreakingBlocks = new ArrayList<Block>();

	private static PatternBasedSyntax tokenSyntax = new PatternBasedSyntax();

	private static PatternBasedSyntax phraseModifierSyntax = new PatternBasedSyntax();

	@Override
	protected PatternBasedSyntax getPhraseModifierSyntax() {
		return phraseModifierSyntax;
	}

	@Override
	protected PatternBasedSyntax getReplacementTokenSyntax() {
		return tokenSyntax;
	}

	{

		// IMPORTANT NOTE: Most items below have order dependencies.  DO NOT REORDER ITEMS BELOW!!

		blocks.add(new HeadingBlock());
		ListBlock listBlock = new ListBlock();
		blocks.add(listBlock);
		paragraphBreakingBlocks.add(listBlock);
		blocks.add(new QuoteBlock());
		TableBlock tableBlock = new TableBlock();
		blocks.add(tableBlock);
		paragraphBreakingBlocks.add(tableBlock);
		blocks.add(new ExtendedQuoteBlock());
		blocks.add(new ExtendedPreformattedBlock());
		// TODO: {color:red}{color}
		blocks.add(new TextBoxBlock(BlockType.PANEL, "panel"));
		blocks.add(new TextBoxBlock(BlockType.NOTE, "note"));
		blocks.add(new TextBoxBlock(BlockType.INFORMATION, "info"));
		blocks.add(new TextBoxBlock(BlockType.WARNING, "warning"));
		blocks.add(new TextBoxBlock(BlockType.TIP, "tip"));
		blocks.add(new CodeBlock());
		blocks.add(new TableOfContentsBlock());

		blocks.add(new ParagraphBlock()); // ORDER DEPENDENCY: this must come last
	}
	static {
		phraseModifierSyntax.beginGroup("(?:(?<=[\\s\\.,\\\"'?!;:\\)\\(\\[\\]])|^)(?:", 0);
		phraseModifierSyntax.add(new SimplePhraseModifier("*", SpanType.STRONG));
		phraseModifierSyntax.add(new SimplePhraseModifier("_", SpanType.EMPHASIS));
		phraseModifierSyntax.add(new SimplePhraseModifier("??", SpanType.CITATION));
		phraseModifierSyntax.add(new SimplePhraseModifier("-", SpanType.DELETED));
		phraseModifierSyntax.add(new SimplePhraseModifier("+", SpanType.UNDERLINED));
		phraseModifierSyntax.add(new SimplePhraseModifier("^", SpanType.SUPERSCRIPT));
		phraseModifierSyntax.add(new SimplePhraseModifier("~", SpanType.SUBSCRIPT));
		phraseModifierSyntax.add(new SimpleWrappedPhraseModifier("{{", "}}", SpanType.MONOSPACE));
		phraseModifierSyntax.add(new ImagePhraseModifier());
		phraseModifierSyntax.endGroup(")(?=\\W|$)", 0);

		tokenSyntax.add(new EntityReferenceReplacementToken("(tm)", "#8482"));
		tokenSyntax.add(new EntityReferenceReplacementToken("(TM)", "#8482"));
		tokenSyntax.add(new EntityReferenceReplacementToken("(c)", "#169"));
		tokenSyntax.add(new EntityReferenceReplacementToken("(C)", "#169"));
		tokenSyntax.add(new EntityReferenceReplacementToken("(r)", "#174"));
		tokenSyntax.add(new EntityReferenceReplacementToken("(R)", "#174"));
		tokenSyntax.add(new HyperlinkReplacementToken());
		tokenSyntax.add(new PatternEntityReferenceReplacementToken("(?:(?<=\\w\\s)(---)(?=\\s\\w))", "#8212")); // emdash
		tokenSyntax.add(new PatternEntityReferenceReplacementToken("(?:(?<=\\w\\s)(--)(?=\\s\\w))", "#8211")); // endash
		tokenSyntax.add(new PatternLiteralReplacementToken("(?:(?<=\\w\\s)(----)(?=\\s\\w))", "<hr/>")); // horizontal rule
		tokenSyntax.add(new PatternLineBreakReplacementToken("(\\\\\\\\)")); // line break
		tokenSyntax.add(new ImpliedHyperlinkReplacementToken());
		tokenSyntax.add(new AnchorReplacementToken());
	}

	public ConfluenceLanguage() {
		setName("Confluence");
	}

	@Override
	public List<Block> getBlocks() {
		return blocks;
	}

	public List<Block> getParagraphBreakingBlocks() {
		return paragraphBreakingBlocks;
	}

}
