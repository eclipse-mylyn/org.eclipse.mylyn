/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
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
package org.eclipse.mylyn.wikitext.parser.markup.block;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;

/**
 * A block that is delimited by <code>&lt;pre&gt;</code> and <code>&lt;/pre&gt;</code>
 *
 * @author David Green
 * @since 3.0
 */
public class HtmlPreformattedBlock extends AbstractHtmlBlock {

	public HtmlPreformattedBlock() {
		super("pre"); //$NON-NLS-1$
	}

	@Override
	protected void beginBlock() {
		builder.beginBlock(BlockType.PREFORMATTED, new Attributes());
	}

	@Override
	protected void endBlock() {
		builder.endBlock();
	}

	@Override
	protected void handleBlockContent(String content) {
		if (content.length() > 0) {
			builder.characters(content);
		} else if (blockLineCount == 1) {
			return;
		}
		builder.characters("\n"); //$NON-NLS-1$
	}

}
