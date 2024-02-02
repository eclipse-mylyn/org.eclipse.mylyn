/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
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
package org.eclipse.mylyn.wikitext.confluence.internal.block;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;

/**
 * quoted text block, matches blocks that start with <code>{noformat}</code>. Creates an extended block type of {@link ParagraphBlock
 * paragraph}.
 *
 * @author David Green
 */
public class ExtendedPreformattedBlock extends AbstractConfluenceDelimitedBlock {

	public ExtendedPreformattedBlock() {
		super("noformat"); //$NON-NLS-1$
	}

	@Override
	protected void beginBlock() {
		Attributes attributes = new Attributes();
		builder.beginBlock(BlockType.PREFORMATTED, attributes);
	}

	@Override
	protected void endBlock() {
		builder.endBlock(); // pre
	}

	@Override
	protected int handleBlockContent(String content) {
		if (content.length() > 0) {
			builder.characters(content);
		} else if (blockLineCount == 1) {
			return -1;
		}
		builder.characters("\n"); //$NON-NLS-1$
		return -1;
	}

	@Override
	protected void setOption(String key, String value) {
		// no options
	}

}
