/*******************************************************************************
 * Copyright (c) 2012, 2013 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.markdown.core.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;

/**
 * Markdown headings.
 * 
 * @author Stefan Seelmann
 */
public class HeadingBlock extends NestableBlock {

	private static final Pattern pattern = Pattern.compile("(#{1,6})\\s*(.+?)\\s*(?:#*\\s*)?"); //$NON-NLS-1$

	private Matcher matcher;

	@Override
	public boolean canStart(String line, int lineOffset) {
		matcher = pattern.matcher(line.substring(lineOffset));
		return matcher.matches();
	}

	@Override
	protected int processLineContent(String line, int offset) {
		int level = matcher.group(1).length();

		Attributes attributes = new Attributes();
		int textStart = offset + matcher.start(2);
		int textEnd = offset + matcher.end(2);
		String lineExcludingClosingHash = line.substring(0, textEnd);

		attributes.setId(state.getIdGenerator().newId("h" + level, lineExcludingClosingHash)); //$NON-NLS-1$

		builder.beginHeading(level, attributes);
		markupLanguage.emitMarkupLine(getParser(), state, lineExcludingClosingHash, textStart);
		builder.endHeading();

		setClosed(true);
		return -1;
	}

}
