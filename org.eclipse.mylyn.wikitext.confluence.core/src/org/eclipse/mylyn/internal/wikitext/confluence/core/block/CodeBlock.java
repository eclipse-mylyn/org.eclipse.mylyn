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
package org.eclipse.mylyn.internal.wikitext.confluence.core.block;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;

/**
 * @author David Green
 */
public class CodeBlock extends AbstractConfluenceDelimitedBlock {

	private String title;

	public CodeBlock() {
		super("code");
	}

	@Override
	protected void beginBlock() {
		if (title != null) {
			Attributes attributes = new Attributes();
			attributes.setTitle(title);
			builder.beginBlock(BlockType.PANEL, attributes);
		}
		Attributes attributes = new Attributes();

		builder.beginBlock(BlockType.PREFORMATTED, new Attributes());
		builder.beginBlock(BlockType.CODE, attributes);
	}

	@Override
	protected void handleBlockContent(String content) {
		builder.characters(content);
		builder.characters("\n");
	}

	@Override
	protected void endBlock() {
		if (title != null) {
			builder.endBlock(); // panel	
		}
		builder.endBlock(); // code
		builder.endBlock(); // pre
	}

	@Override
	protected void resetState() {
		super.resetState();
		title = null;
	}

	@Override
	protected void setOption(String key, String value) {
		if (key.equals("title")) {
			title = value;
		}
	}
}
