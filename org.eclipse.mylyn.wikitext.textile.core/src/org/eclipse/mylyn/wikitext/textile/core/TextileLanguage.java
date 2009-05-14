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
package org.eclipse.mylyn.wikitext.textile.core;

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
import org.eclipse.mylyn.internal.wikitext.textile.core.phrase.SimpleTextilePhraseModifier.Mode;
import org.eclipse.mylyn.internal.wikitext.textile.core.token.FootnoteReferenceReplacementToken;
import org.eclipse.mylyn.internal.wikitext.textile.core.token.HyperlinkReplacementToken;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.core.parser.builder.NoOpDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.markup.AbstractMarkupLanguage;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;
import org.eclipse.mylyn.wikitext.core.parser.markup.ContentState;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguageConfiguration;
import org.eclipse.mylyn.wikitext.core.parser.markup.phrase.HtmlEndTagPhraseModifier;
import org.eclipse.mylyn.wikitext.core.parser.markup.phrase.HtmlStartTagPhraseModifier;
import org.eclipse.mylyn.wikitext.core.parser.markup.token.AcronymReplacementToken;
import org.eclipse.mylyn.wikitext.core.parser.markup.token.EntityReferenceReplacementToken;
import org.eclipse.mylyn.wikitext.core.parser.markup.token.EntityWrappingReplacementToken;
import org.eclipse.mylyn.wikitext.core.parser.markup.token.PatternEntityReferenceReplacementToken;

/**
 * A textile dialect that parses <a href="http://en.wikipedia.org/wiki/Textile_(markup_language)">Textile markup</a>.
 * Based on the spec available at <a href="http://textile.thresholdstate.com/">http://textile.thresholdstate.com/</a>,
 * supports all current Textile markup constructs. Additionally supported are <code>{toc}</code> and
 * <code>{glossary}</code>.
 * 
 * @author David Green
 * @since 1.0
 */
public class TextileLanguage extends AbstractMarkupLanguage {

	private boolean preprocessFootnotes = false;

	private TextileContentState currentState;

	public TextileLanguage() {
		setName("Textile"); //$NON-NLS-1$
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
	 */
	@Override
	protected void addBlockExtensions(List<Block> blocks, List<Block> paragraphBreakingBlocks) {
		blocks.add(new TextileGlossaryBlock());
		blocks.add(new TableOfContentsBlock());
		super.addBlockExtensions(blocks, paragraphBreakingBlocks);
	}

	@Override
	protected ContentState createState() {
		if (currentState != null) {
			TextileContentState temp = currentState;
			currentState = null;
			return temp;
		}
		return new TextileContentState();
	}

	@Override
	protected void addStandardBlocks(List<Block> blocks, List<Block> paragraphBreakingBlocks) {
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
	}

	@Override
	protected void addStandardPhraseModifiers(PatternBasedSyntax phraseModifierSyntax) {
		boolean escapingHtml = configuration == null ? false : configuration.isEscapingHtmlAndXml();

		phraseModifierSyntax.add(new HtmlEndTagPhraseModifier(escapingHtml));
		phraseModifierSyntax.add(new HtmlStartTagPhraseModifier(escapingHtml));
		phraseModifierSyntax.beginGroup("(?:(?<=[\\s\\.,\\\"'?!;:\\)\\(\\{\\}\\[\\]])|^)(?:", 0); //$NON-NLS-1$
		phraseModifierSyntax.add(new EscapeTextilePhraseModifier());
		phraseModifierSyntax.add(new SimpleTextilePhraseModifier("**", SpanType.BOLD, Mode.NESTING)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimpleTextilePhraseModifier("??", SpanType.CITATION, Mode.NESTING)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimpleTextilePhraseModifier("__", SpanType.ITALIC, Mode.NESTING)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimpleTextilePhraseModifier("_", SpanType.EMPHASIS, Mode.NESTING)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimpleTextilePhraseModifier("*", SpanType.STRONG, Mode.NESTING)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimpleTextilePhraseModifier("+", SpanType.INSERTED, Mode.NESTING)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimpleTextilePhraseModifier("~", SpanType.SUBSCRIPT, Mode.NORMAL)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimpleTextilePhraseModifier("^", SpanType.SUPERSCRIPT, Mode.NORMAL)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimpleTextilePhraseModifier("@", SpanType.CODE, Mode.SPECIAL)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimpleTextilePhraseModifier("%", SpanType.SPAN, Mode.NESTING)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimpleTextilePhraseModifier("-", SpanType.DELETED, Mode.NESTING)); //$NON-NLS-1$
		phraseModifierSyntax.add(new ImageTextilePhraseModifier());
		phraseModifierSyntax.endGroup(")(?=\\W|$)", 0); //$NON-NLS-1$
	}

	@Override
	protected void addStandardTokens(PatternBasedSyntax tokenSyntax) {
		tokenSyntax.add(new EntityReferenceReplacementToken("(tm)", "#8482")); //$NON-NLS-1$ //$NON-NLS-2$
		tokenSyntax.add(new EntityReferenceReplacementToken("(TM)", "#8482")); //$NON-NLS-1$ //$NON-NLS-2$
		tokenSyntax.add(new EntityReferenceReplacementToken("(c)", "#169")); //$NON-NLS-1$ //$NON-NLS-2$
		tokenSyntax.add(new EntityReferenceReplacementToken("(C)", "#169")); //$NON-NLS-1$ //$NON-NLS-2$
		tokenSyntax.add(new EntityReferenceReplacementToken("(r)", "#174")); //$NON-NLS-1$ //$NON-NLS-2$
		tokenSyntax.add(new EntityReferenceReplacementToken("(R)", "#174")); //$NON-NLS-1$ //$NON-NLS-2$
		tokenSyntax.add(new HyperlinkReplacementToken());
		tokenSyntax.add(new FootnoteReferenceReplacementToken());
		if (configuration == null || !configuration.isOptimizeForRepositoryUsage()) {
			tokenSyntax.add(new EntityWrappingReplacementToken("\"", "#8220", "#8221")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			tokenSyntax.add(new EntityWrappingReplacementToken("'", "#8216", "#8217")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			tokenSyntax.add(new PatternEntityReferenceReplacementToken("(?:(?<=\\w)(')(?=\\w))", "#8217")); // apostrophe //$NON-NLS-1$ //$NON-NLS-2$
		}
		tokenSyntax.add(new PatternEntityReferenceReplacementToken("(?:(?<=\\w\\s)(--)(?=\\s\\w))", "#8212")); // emdash //$NON-NLS-1$ //$NON-NLS-2$
		tokenSyntax.add(new PatternEntityReferenceReplacementToken("(?:(?<=\\w\\s)(-)(?=\\s\\w))", "#8211")); // endash //$NON-NLS-1$ //$NON-NLS-2$
		tokenSyntax.add(new PatternEntityReferenceReplacementToken("(?:(?<=\\d\\s)(x)(?=\\s\\d))", "#215")); // mul //$NON-NLS-1$ //$NON-NLS-2$
		if (configuration == null || !configuration.isOptimizeForRepositoryUsage()) {
			tokenSyntax.add(new AcronymReplacementToken());
		}
	}

	@Override
	protected Block createParagraphBlock() {
		ParagraphBlock paragraphBlock = new ParagraphBlock();
		if (configuration != null && !configuration.isEnableUnwrappedParagraphs()) {
			paragraphBlock.setEnableUnwrapped(false);
		}
		return paragraphBlock;
	}

	/**
	 * indicate if footnotes should be preprocessed to avoid false-positives when footnote references are used
	 * inadvertently. The default is false.
	 */
	public boolean isPreprocessFootnotes() {
		return preprocessFootnotes;
	}

	/**
	 * indicate if footnotes should be preprocessed to avoid false-positives when footnote references are used
	 * inadvertently. The default is false.
	 */
	public void setPreprocessFootnotes(boolean preprocessFootnotes) {
		this.preprocessFootnotes = preprocessFootnotes;
	}

	@Override
	public void configure(MarkupLanguageConfiguration configuration) throws UnsupportedOperationException {
		if (configuration.isOptimizeForRepositoryUsage()) {
			setPreprocessFootnotes(true);
		}
		super.configure(configuration);
	}

	@Override
	public TextileLanguage clone() {
		TextileLanguage copy = (TextileLanguage) super.clone();
		copy.preprocessFootnotes = preprocessFootnotes;
		return copy;
	}

	@Override
	public void processContent(MarkupParser parser, String markupContent, boolean asDocument) {
		if (preprocessFootnotes) {
			boolean previousBlocksOnly = isBlocksOnly();
			boolean previousFilterGenerativeContents = isFilterGenerativeContents();
			setBlocksOnly(true);
			setFilterGenerativeContents(true);

			DocumentBuilder builder = parser.getBuilder();
			parser.setBuilder(new NoOpDocumentBuilder());
			currentState = new TextileContentState();
			TextileContentState preprocessingState = currentState;
			super.processContent(parser, markupContent, asDocument);

			setBlocksOnly(previousBlocksOnly);
			setFilterGenerativeContents(previousFilterGenerativeContents);

			currentState = new TextileContentState();
			currentState.setFootnoteNumbers(preprocessingState.getFootnoteNumbers());
			parser.setBuilder(builder);
			super.processContent(parser, markupContent, asDocument);

			currentState = null;
		} else {
			currentState = null;
			super.processContent(parser, markupContent, asDocument);
		}
	}
}
