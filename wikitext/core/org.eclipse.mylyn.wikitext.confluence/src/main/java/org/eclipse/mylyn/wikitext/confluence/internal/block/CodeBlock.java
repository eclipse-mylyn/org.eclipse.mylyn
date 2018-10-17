/*******************************************************************************
 * Copyright (c) 2007, 2013 David Green and others.
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
 * @author David Green
 */
public class CodeBlock extends AbstractConfluenceDelimitedBlock {

	private String title;

	private String language;

	public CodeBlock() {
		super("code"); //$NON-NLS-1$
	}

	@Override
	protected void beginBlock() {
		if (title != null) {
			Attributes attributes = new Attributes();
			attributes.setTitle(title);
			builder.beginBlock(BlockType.PANEL, attributes);
		}
		Attributes attributes = new Attributes();
		if (language != null) {
			// chili-style class and atlassian-style class
			attributes.setCssClass(language + " code-" + language); //$NON-NLS-1$
		}
		builder.beginBlock(BlockType.CODE, attributes);
	}

	@Override
	protected int handleBlockContent(String content) {
		builder.characters(content);
		builder.characters("\n"); //$NON-NLS-1$
		return -1;
	}

	@Override
	protected void endBlock() {
		if (title != null) {
			builder.endBlock(); // panel
		}
		builder.endBlock(); // code
	}

	@Override
	protected void resetState() {
		super.resetState();
		title = null;
	}

	@Override
	protected void setOption(String key, String value) {
		if (key.equals("title")) { //$NON-NLS-1$
			title = value;
		} else if (key.equals("language")) { //$NON-NLS-1$
			language = value;
		}
	}

	@Override
	protected void setOption(String option) {
		language = option.toLowerCase();
	}
}
