/*******************************************************************************
 * Copyright (c) 2007, 2011 David Green and others.
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
package org.eclipse.mylyn.wikitext.tracwiki.internal.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.markup.Block;

/**
 * @author David Green
 */
public class HeadingBlock extends Block {

	private static final Pattern pattern = Pattern
			.compile("\\s*(\\={1,6})\\s*([^=]*[^=\\s])\\s*\\1(?:\\s+\\#(\\S+)?)?\\s*"); //$NON-NLS-1$

	private int blockLineCount = 0;

	private Matcher matcher;

	@Override
	public boolean canStart(String line, int lineOffset) {
		blockLineCount = 0;
		if (lineOffset == 0) {
			matcher = pattern.matcher(line);
			return matcher.matches();
		} else {
			matcher = null;
			return false;
		}
	}

	@Override
	public int processLineContent(String line, int offset) {
		if (blockLineCount > 0) {
			throw new IllegalStateException();
		}
		++blockLineCount;

		int level = matcher.group(1).length();

		String text = matcher.group(2);

		String id = matcher.group(3);

		Attributes attributes = new Attributes();
		attributes.setId(id);
		if (attributes.getId() == null) {
			attributes.setId(state.getIdGenerator().newId("h" + level, text)); //$NON-NLS-1$
		}

		builder.beginHeading(level, attributes);
		builder.characters(text);
		builder.endHeading();

		setClosed(true);
		return -1;
	}

}
