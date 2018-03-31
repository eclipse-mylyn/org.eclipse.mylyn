/*******************************************************************************
 * Copyright (c) 2015 David Green.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.commonmark.internal;

import org.eclipse.mylyn.wikitext.commonmark.internal.blocks.AtxHeaderBlock;
import org.eclipse.mylyn.wikitext.commonmark.internal.blocks.BlockQuoteBlock;
import org.eclipse.mylyn.wikitext.commonmark.internal.blocks.EmptyBlock;
import org.eclipse.mylyn.wikitext.commonmark.internal.blocks.FencedCodeBlock;
import org.eclipse.mylyn.wikitext.commonmark.internal.blocks.HorizontalRuleBlock;
import org.eclipse.mylyn.wikitext.commonmark.internal.blocks.HtmlBlock;
import org.eclipse.mylyn.wikitext.commonmark.internal.blocks.HtmlCdataBlock;
import org.eclipse.mylyn.wikitext.commonmark.internal.blocks.HtmlCommentBlock;
import org.eclipse.mylyn.wikitext.commonmark.internal.blocks.HtmlDoctypeBlock;
import org.eclipse.mylyn.wikitext.commonmark.internal.blocks.HtmlProcessingInstructionBlock;
import org.eclipse.mylyn.wikitext.commonmark.internal.blocks.HtmlType1Block;
import org.eclipse.mylyn.wikitext.commonmark.internal.blocks.HtmlType7Block;
import org.eclipse.mylyn.wikitext.commonmark.internal.blocks.IndentedCodeBlock;
import org.eclipse.mylyn.wikitext.commonmark.internal.blocks.ListBlock;
import org.eclipse.mylyn.wikitext.commonmark.internal.blocks.ParagraphBlock;
import org.eclipse.mylyn.wikitext.commonmark.internal.blocks.SetextHeaderBlock;

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
