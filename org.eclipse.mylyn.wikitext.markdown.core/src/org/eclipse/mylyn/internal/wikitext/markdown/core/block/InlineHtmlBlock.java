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

import java.util.regex.Pattern;

import org.eclipse.mylyn.internal.wikitext.markdown.core.token.AutomaticLinkReplacementToken;

/**
 * Markdown inline HTML.
 * 
 * @author Stefan Seelmann
 */
public class InlineHtmlBlock extends NestableBlock {

	private static final Pattern AUTOMATIC_LINK_PATTERN = Pattern.compile(AutomaticLinkReplacementToken.AUTOMATIC_LINK_REGEX);

	@Override
	public boolean canStart(String line, int lineOffset) {
		return line.substring(lineOffset).trim().startsWith("<") && !AUTOMATIC_LINK_PATTERN.matcher(line).matches(); //$NON-NLS-1$
	}

	@Override
	protected int processLineContent(String line, int offset) {
		String text = line.substring(offset);

		// empty line: start new block
		if (markupLanguage.isEmptyLine(text)) {
			setClosed(true);
			return offset;
		}

		builder.charactersUnescaped(text);

		return -1;
	}

}
