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

package org.eclipse.mylyn.internal.wikitext.asciidoc.core.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;

/**
 * AsciiDoc atx style headings.
 *
 * @author Stefan Seelmann
 * @author Max Rydahl Andersen
 * @author Jeremie Bresson
 */
public class HeadingBlock extends Block {

	private static final Pattern pattern = Pattern.compile("(={1,})\\s*(.+?)(\\s*)((?:=*\\s*))?"); //$NON-NLS-1$

	private Matcher matcher;

	@Override
	public boolean canStart(String line, int lineOffset) {
		if (lineOffset == 0) {
			Matcher m = pattern.matcher(line);
			if (m.matches() && m.group(1).length() < 6) {
				matcher = m;
				return true;
			}
		}
		matcher = null;
		return false;
	}

	@Override
	public int processLineContent(String line, int offset) {
		int level = matcher.group(1).length();
		String text = matcher.group(2);
		String closingGroup = matcher.group(4);

		builder.beginHeading(level, new Attributes());
		getMarkupLanguage().emitMarkupLine(getParser(), state, matcher.start(2), text, 0);
		if (closingGroup.length() > 0 && closingGroup.length() != level) {
			builder.characters(matcher.group(3));
			builder.characters(closingGroup);
		}
		builder.endHeading();

		setClosed(true);
		return -1;
	}

}
