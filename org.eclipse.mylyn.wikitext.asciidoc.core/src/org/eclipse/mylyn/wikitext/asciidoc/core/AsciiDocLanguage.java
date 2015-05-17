/*******************************************************************************
 * Copyright (c) 2012 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.asciidoc.core;

import java.util.List;

import org.eclipse.mylyn.internal.wikitext.asciidoc.core.block.HeadingBlock;
import org.eclipse.mylyn.internal.wikitext.asciidoc.core.block.ParagraphBlock;
import org.eclipse.mylyn.internal.wikitext.asciidoc.core.block.PreformattedBlock;
import org.eclipse.mylyn.internal.wikitext.asciidoc.core.block.UnderlinedHeadingBlock;
import org.eclipse.mylyn.internal.wikitext.asciidoc.core.phrase.BackslashEscapePhraseModifier;
import org.eclipse.mylyn.internal.wikitext.asciidoc.core.phrase.SimplePhraseModifier;
import org.eclipse.mylyn.internal.wikitext.asciidoc.core.token.EmailLinkReplacementToken;
import org.eclipse.mylyn.internal.wikitext.asciidoc.core.token.ExplicitLinkReplacementToken;
import org.eclipse.mylyn.internal.wikitext.asciidoc.core.token.ImplicitFormattedLinkReplacementToken;
import org.eclipse.mylyn.internal.wikitext.asciidoc.core.token.ImplicitLinkReplacementToken;
import org.eclipse.mylyn.internal.wikitext.asciidoc.core.token.InlineImageReplacementToken;
import org.eclipse.mylyn.internal.wikitext.asciidoc.core.token.PreserverHtmlEntityToken;
import org.eclipse.mylyn.internal.wikitext.asciidoc.core.util.ReadAheadDispatcher;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.core.parser.markup.AbstractMarkupLanguage;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;
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
	protected void addStandardTokens(PatternBasedSyntax tokenSyntax) {
		// HTML entities are preserved
		tokenSyntax.add(new PreserverHtmlEntityToken());
		// line ending with a + will cause a line Break
		tokenSyntax.add(new PatternLineBreakReplacementToken("(\\s\\+)\\s*")); //$NON-NLS-1$
	}

	@Override
	protected void addStandardPhraseModifiers(PatternBasedSyntax phraseModifierSyntax) {
		// links
		phraseModifierSyntax.add(new EmailLinkReplacementToken());
		phraseModifierSyntax.add(new ExplicitLinkReplacementToken());
		phraseModifierSyntax.add(new ImplicitFormattedLinkReplacementToken());
		phraseModifierSyntax.add(new ImplicitLinkReplacementToken());

		// backslash escaped span elements
		phraseModifierSyntax.add(new BackslashEscapePhraseModifier("+")); //$NON-NLS-1$
		phraseModifierSyntax.add(new BackslashEscapePhraseModifier("*")); //$NON-NLS-1$
		phraseModifierSyntax.add(new BackslashEscapePhraseModifier("_")); //$NON-NLS-1$

		phraseModifierSyntax.add(new InlineImageReplacementToken());

		// emphasis span elements
		phraseModifierSyntax.add(new SimplePhraseModifier("`", SpanType.CODE)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier("+", SpanType.CODE)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier("*", SpanType.STRONG)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier("_", SpanType.EMPHASIS)); //$NON-NLS-1$

	}

	@Override
	protected void addStandardBlocks(List<Block> blocks, List<Block> paragraphBreakingBlocks) {
		blocks.add(new PreformattedBlock());
		blocks.add(new HeadingBlock());
	}

	@Override
	protected Block createParagraphBlock() {
		ParagraphBlock paragraphBlock = new ParagraphBlock();
		UnderlinedHeadingBlock headingBlock = new UnderlinedHeadingBlock();
		ReadAheadDispatcher readAheadBlock = new ReadAheadDispatcher(headingBlock, paragraphBlock);
		return readAheadBlock;
	}

}
