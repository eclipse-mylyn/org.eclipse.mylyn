/*******************************************************************************
 * Copyright (c) 2012, 2016 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *     Max Rydahl Andersen - Bug 474084
 *     Patrik Suzzi <psuzzi@gmail.com> - Bug 481670, 474084
 *     Jeremie Bresson - Bug 488246
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.asciidoc.core;

import java.io.Writer;
import java.util.List;

import org.eclipse.mylyn.internal.wikitext.asciidoc.core.AsciiDocContentState;
import org.eclipse.mylyn.internal.wikitext.asciidoc.core.AsciiDocDocumentBuilder;
import org.eclipse.mylyn.internal.wikitext.asciidoc.core.AsciiDocPreProcessor;
import org.eclipse.mylyn.internal.wikitext.asciidoc.core.block.CodeBlock;
import org.eclipse.mylyn.internal.wikitext.asciidoc.core.block.CommentBlock;
import org.eclipse.mylyn.internal.wikitext.asciidoc.core.block.HeadingBlock;
import org.eclipse.mylyn.internal.wikitext.asciidoc.core.block.HorizontalRuleBlock;
import org.eclipse.mylyn.internal.wikitext.asciidoc.core.block.ListBlock;
import org.eclipse.mylyn.internal.wikitext.asciidoc.core.block.ParagraphBlock;
import org.eclipse.mylyn.internal.wikitext.asciidoc.core.block.PreformattedBlock;
import org.eclipse.mylyn.internal.wikitext.asciidoc.core.block.PropertiesLineBlock;
import org.eclipse.mylyn.internal.wikitext.asciidoc.core.block.TableBlock;
import org.eclipse.mylyn.internal.wikitext.asciidoc.core.block.TitleLineBlock;
import org.eclipse.mylyn.internal.wikitext.asciidoc.core.block.UnderlinedHeadingBlock;
import org.eclipse.mylyn.internal.wikitext.asciidoc.core.phrase.BackslashEscapePhraseModifier;
import org.eclipse.mylyn.internal.wikitext.asciidoc.core.phrase.SimplePhraseModifier;
import org.eclipse.mylyn.internal.wikitext.asciidoc.core.token.AnchorLinkMacroReplacementToken;
import org.eclipse.mylyn.internal.wikitext.asciidoc.core.token.AnchorLinkReplacementToken;
import org.eclipse.mylyn.internal.wikitext.asciidoc.core.token.EmailLinkReplacementToken;
import org.eclipse.mylyn.internal.wikitext.asciidoc.core.token.ExplicitLinkReplacementToken;
import org.eclipse.mylyn.internal.wikitext.asciidoc.core.token.ImplicitFormattedLinkReplacementToken;
import org.eclipse.mylyn.internal.wikitext.asciidoc.core.token.ImplicitLinkReplacementToken;
import org.eclipse.mylyn.internal.wikitext.asciidoc.core.token.InlineCommentReplacementToken;
import org.eclipse.mylyn.internal.wikitext.asciidoc.core.token.InlineImageReplacementToken;
import org.eclipse.mylyn.internal.wikitext.asciidoc.core.token.PreserverHtmlEntityToken;
import org.eclipse.mylyn.internal.wikitext.asciidoc.core.token.XrefMacroReplacementToken;
import org.eclipse.mylyn.internal.wikitext.asciidoc.core.token.XrefReplacementToken;
import org.eclipse.mylyn.internal.wikitext.asciidoc.core.util.ReadAheadDispatcher;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.markup.AbstractMarkupLanguage;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;
import org.eclipse.mylyn.wikitext.core.parser.markup.ContentState;
import org.eclipse.mylyn.wikitext.core.parser.markup.token.PatternLineBreakReplacementToken;

/**
 * A markup language implementing Asciidoc syntax.
 *
 * @author Stefan Seelmann
 * @author Max Rydahl Andersen
 * @since 1.0
 */
public class AsciiDocLanguage extends AbstractMarkupLanguage {

	public AsciiDocLanguage() {
		setName("AsciiDoc"); //$NON-NLS-1$
	}

	@Override
	protected ContentState createState() {
		return new AsciiDocContentState();
	}

	@Override
	protected void addStandardTokens(PatternBasedSyntax tokenSyntax) {
		// HTML entities are preserved
		tokenSyntax.add(new PreserverHtmlEntityToken());
		// line ending with a + will cause a line Break
		tokenSyntax.add(new PatternLineBreakReplacementToken("(\\s\\+)\\s*$")); //$NON-NLS-1$
	}

	@Override
	public void processContent(MarkupParser parser, String markupContent, boolean asDocument) {
		if (isEnableMacros()) {
			markupContent = preprocessContent(markupContent);
		}
		super.processContent(parser, markupContent, asDocument);
	}

	/**
	 * preprocess content, which involves attribute substitution.
	 */
	protected String preprocessContent(String markupContent) {
		return new AsciiDocPreProcessor().process(markupContent);
	}

	@Override
	protected void addStandardPhraseModifiers(PatternBasedSyntax phraseModifierSyntax) {
		// comments
		phraseModifierSyntax.add(new InlineCommentReplacementToken());

		// links
		phraseModifierSyntax.add(new EmailLinkReplacementToken());
		phraseModifierSyntax.add(new ExplicitLinkReplacementToken());
		phraseModifierSyntax.add(new ImplicitFormattedLinkReplacementToken());
		phraseModifierSyntax.add(new ImplicitLinkReplacementToken());
		phraseModifierSyntax.add(new AnchorLinkReplacementToken());
		phraseModifierSyntax.add(new AnchorLinkMacroReplacementToken());
		phraseModifierSyntax.add(new XrefReplacementToken());
		phraseModifierSyntax.add(new XrefMacroReplacementToken());

		// backslash escaped span elements
		phraseModifierSyntax.add(new BackslashEscapePhraseModifier("++")); //$NON-NLS-1$
		phraseModifierSyntax.add(new BackslashEscapePhraseModifier("**")); //$NON-NLS-1$
		phraseModifierSyntax.add(new BackslashEscapePhraseModifier("__")); //$NON-NLS-1$
		phraseModifierSyntax.add(new BackslashEscapePhraseModifier("+")); //$NON-NLS-1$
		phraseModifierSyntax.add(new BackslashEscapePhraseModifier("*")); //$NON-NLS-1$
		phraseModifierSyntax.add(new BackslashEscapePhraseModifier("_")); //$NON-NLS-1$

		phraseModifierSyntax.add(new InlineImageReplacementToken());

		// emphasis span elements
		phraseModifierSyntax.add(new SimplePhraseModifier("``", SpanType.CODE)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier("++", SpanType.CODE)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier("**", SpanType.STRONG)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier("__", SpanType.EMPHASIS)); //$NON-NLS-1$

		// emphasis span elements on word boundaries
		phraseModifierSyntax.add(new SimplePhraseModifier("`", SpanType.CODE, true)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier("+", SpanType.CODE, true)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier("*", SpanType.STRONG, true)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier("_", SpanType.EMPHASIS, true)); //$NON-NLS-1$

	}

	@Override
	protected void addStandardBlocks(List<Block> blocks, List<Block> paragraphBreakingBlocks) {
		ListBlock listBlock = new ListBlock();
		blocks.add(listBlock);
		paragraphBreakingBlocks.add(listBlock);

		TitleLineBlock titleLineBlock = new TitleLineBlock();
		PropertiesLineBlock propertiesLineBlock = new PropertiesLineBlock();

		TableBlock tableBlock = new TableBlock();

		PreformattedBlock preformattedBlock = new PreformattedBlock();
		CommentBlock commentBlock = new CommentBlock();
		HeadingBlock headingBlock = new HeadingBlock();
		CodeBlock codeBlock = new CodeBlock();
		HorizontalRuleBlock hrBlock = new HorizontalRuleBlock();

		blocks.add(titleLineBlock);
		blocks.add(propertiesLineBlock);

		blocks.add(tableBlock);

		blocks.add(preformattedBlock);
		blocks.add(headingBlock);
		blocks.add(codeBlock);
		blocks.add(commentBlock);
		blocks.add(hrBlock);

		paragraphBreakingBlocks.add(codeBlock);
		paragraphBreakingBlocks.add(commentBlock);
		paragraphBreakingBlocks.add(preformattedBlock);

	}

	@Override
	protected Block createParagraphBlock() {
		ParagraphBlock paragraphBlock = new ParagraphBlock();
		UnderlinedHeadingBlock headingBlock = new UnderlinedHeadingBlock();
		ReadAheadDispatcher readAheadBlock = new ReadAheadDispatcher(headingBlock, paragraphBlock);
		return readAheadBlock;
	}

	@Override
	public DocumentBuilder createDocumentBuilder(Writer out, boolean formatting) {
		return new AsciiDocDocumentBuilder(out);
	}
}
