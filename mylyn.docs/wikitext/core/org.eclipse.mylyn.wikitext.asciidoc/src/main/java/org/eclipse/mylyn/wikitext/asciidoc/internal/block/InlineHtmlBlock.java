/*******************************************************************************
 * Copyright (c) 2012 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.asciidoc.internal.block;

import org.eclipse.mylyn.wikitext.parser.markup.Block;

/**
 * Asciidoc inline HTML.
 * 
 * @author Stefan Seelmann @author Max Rydahl Andersen
 */
public class InlineHtmlBlock extends Block {

	@Override
	public boolean canStart(String line, int lineOffset) {
		return line.startsWith("<"); //$NON-NLS-1$
	}

	@Override
	protected int processLineContent(String line, int offset) {
		// empty line: start new block
		if (markupLanguage.isEmptyLine(line)) {
			setClosed(true);
			return 0;
		}

		builder.charactersUnescaped(line);
		builder.characters("\n"); //$NON-NLS-1$

		return -1;
	}

}
