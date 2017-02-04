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

package org.eclipse.mylyn.wikitext.asciidoc;

import java.io.Writer;
import java.util.List;

import org.eclipse.mylyn.wikitext.asciidoc.internal.AsciiDocContentState;
import org.eclipse.mylyn.wikitext.asciidoc.internal.AsciiDocDocumentBuilder;
import org.eclipse.mylyn.wikitext.asciidoc.internal.block.AttributeDefinitionBlock;
import org.eclipse.mylyn.wikitext.asciidoc.internal.block.CodeBlock;
import org.eclipse.mylyn.wikitext.asciidoc.internal.block.CommentBlock;
import org.eclipse.mylyn.wikitext.asciidoc.internal.block.HeadingBlock;
import org.eclipse.mylyn.wikitext.asciidoc.internal.block.HorizontalRuleBlock;
import org.eclipse.mylyn.wikitext.asciidoc.internal.block.ListBlock;
import org.eclipse.mylyn.wikitext.asciidoc.internal.block.ParagraphBlock;
import org.eclipse.mylyn.wikitext.asciidoc.internal.block.PreformattedBlock;
import org.eclipse.mylyn.wikitext.asciidoc.internal.block.PropertiesLineBlock;
import org.eclipse.mylyn.wikitext.asciidoc.internal.block.TableBlock;
import org.eclipse.mylyn.wikitext.asciidoc.internal.block.TitleLineBlock;
import org.eclipse.mylyn.wikitext.asciidoc.internal.block.UnderlinedHeadingBlock;
import org.eclipse.mylyn.wikitext.asciidoc.internal.phrase.BackslashEscapePhraseModifier;
import org.eclipse.mylyn.wikitext.asciidoc.internal.phrase.SimplePhraseModifier;
import org.eclipse.mylyn.wikitext.asciidoc.internal.token.AnchorLinkMacroReplacementToken;
import org.eclipse.mylyn.wikitext.asciidoc.internal.token.AnchorLinkReplacementToken;
import org.eclipse.mylyn.wikitext.asciidoc.internal.token.EmailLinkReplacementToken;
import org.eclipse.mylyn.wikitext.asciidoc.internal.token.ExplicitLinkReplacementToken;
import org.eclipse.mylyn.wikitext.asciidoc.internal.token.ImplicitFormattedLinkReplacementToken;
import org.eclipse.mylyn.wikitext.asciidoc.internal.token.ImplicitLinkReplacementToken;
import org.eclipse.mylyn.wikitext.asciidoc.internal.token.InlineAttributeReplacementToken;
import org.eclipse.mylyn.wikitext.asciidoc.internal.token.InlineCommentReplacementToken;
import org.eclipse.mylyn.wikitext.asciidoc.internal.token.InlineEscapedAttributeReplacementToken;
import org.eclipse.mylyn.wikitext.asciidoc.internal.token.InlineImageReplacementToken;
import org.eclipse.mylyn.wikitext.asciidoc.internal.token.PreserverHtmlEntityToken;
import org.eclipse.mylyn.wikitext.asciidoc.internal.token.XrefMacroReplacementToken;
import org.eclipse.mylyn.wikitext.asciidoc.internal.token.XrefReplacementToken;
import org.eclipse.mylyn.wikitext.asciidoc.internal.util.ReadAheadDispatcher;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.markup.AbstractMarkupLanguage;
import org.eclipse.mylyn.wikitext.parser.markup.Block;
import org.eclipse.mylyn.wikitext.parser.markup.ContentState;
import org.eclipse.mylyn.wikitext.parser.markup.token.PatternLineBreakReplacementToken;

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
		AsciiDocContentState state = new AsciiDocContentState();
		//set the initial attribute values:
		state.putAttribute(AsciiDocContentState.ATTRIBUTE_IDPREFIX, "_"); //$NON-NLS-1$
		state.putAttribute(AsciiDocContentState.ATTRIBUTE_IDSEPARATOR, "_"); //$NON-NLS-1$
		state.putAttribute(AsciiDocContentState.ATTRIBUTE_IMAGESDIR, ""); //$NON-NLS-1$
		return state;
	}

	@Override
	protected void addStandardTokens(PatternBasedSyntax tokenSyntax) {
		// HTML entities are preserved
		tokenSyntax.add(new PreserverHtmlEntityToken());
		// line ending with a + will cause a line Break
		tokenSyntax.add(new PatternLineBreakReplacementToken("(\\s\\+)\\s*$")); //$NON-NLS-1$
	}

	@Override
	protected void addStandardPhraseModifiers(PatternBasedSyntax phraseModifierSyntax) {
		// attribute
		phraseModifierSyntax.add(new InlineAttributeReplacementToken());
		phraseModifierSyntax.add(new InlineEscapedAttributeReplacementToken());

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

		AttributeDefinitionBlock attributeDefinition = new AttributeDefinitionBlock();

		TitleLineBlock titleLineBlock = new TitleLineBlock();
		PropertiesLineBlock propertiesLineBlock = new PropertiesLineBlock();

		TableBlock tableBlock = new TableBlock();

		PreformattedBlock preformattedBlock = new PreformattedBlock();
		CommentBlock commentBlock = new CommentBlock();
		HeadingBlock headingBlock = new HeadingBlock();
		CodeBlock codeBlock = new CodeBlock();
		HorizontalRuleBlock hrBlock = new HorizontalRuleBlock();

		blocks.add(attributeDefinition);

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
