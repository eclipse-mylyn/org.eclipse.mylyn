/*******************************************************************************
 * Copyright (c) 2015 David Green.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.commonmark;

import org.eclipse.mylyn.internal.wikitext.commonmark.blocks.AtxHeaderBlock;
import org.eclipse.mylyn.internal.wikitext.commonmark.blocks.BlockQuoteBlock;
import org.eclipse.mylyn.internal.wikitext.commonmark.blocks.EmptyBlock;
import org.eclipse.mylyn.internal.wikitext.commonmark.blocks.FencedCodeBlock;
import org.eclipse.mylyn.internal.wikitext.commonmark.blocks.HorizontalRuleBlock;
import org.eclipse.mylyn.internal.wikitext.commonmark.blocks.HtmlBlock;
import org.eclipse.mylyn.internal.wikitext.commonmark.blocks.HtmlType1Block;
import org.eclipse.mylyn.internal.wikitext.commonmark.blocks.HtmlCommentBlock;
import org.eclipse.mylyn.internal.wikitext.commonmark.blocks.HtmlProcessingInstructionBlock;
import org.eclipse.mylyn.internal.wikitext.commonmark.blocks.HtmlDoctypeBlock;
import org.eclipse.mylyn.internal.wikitext.commonmark.blocks.HtmlCdataBlock;
import org.eclipse.mylyn.internal.wikitext.commonmark.blocks.HtmlType7Block;
import org.eclipse.mylyn.internal.wikitext.commonmark.blocks.IndentedCodeBlock;
import org.eclipse.mylyn.internal.wikitext.commonmark.blocks.ListBlock;
import org.eclipse.mylyn.internal.wikitext.commonmark.blocks.ParagraphBlock;
import org.eclipse.mylyn.internal.wikitext.commonmark.blocks.SetextHeaderBlock;

public class CommonMark {

	private static final SourceBlocks SOURCE_BLOCKS = new SourceBlocks(new BlockQuoteBlock(), new AtxHeaderBlock(),
			new HorizontalRuleBlock(), new ListBlock(), new SetextHeaderBlock(), new FencedCodeBlock(),
			new IndentedCodeBlock(), new HtmlType1Block(), new HtmlCommentBlock(), new HtmlProcessingInstructionBlock(),
			new HtmlDoctypeBlock(), new HtmlCdataBlock(), new HtmlBlock(), new HtmlType7Block(), new ParagraphBlock(),
			new EmptyBlock());

	public static SourceBlocks sourceBlocks() {
		return SOURCE_BLOCKS;
	}

	private CommonMark() {
		// prevent instantiation
	}
}
