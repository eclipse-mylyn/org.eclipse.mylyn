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
package org.eclipse.mylyn.wikitext.core.parser.markup.block;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;

/**
 * A block that is delimited by <code>&lt;pre&gt;</code> and <code>&lt;/pre&gt;</code>
 * 
 * @author David Green
 * @since 1.2
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
