/*******************************************************************************
 * Copyright (c) 2013 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.markdown.core.block;

import org.eclipse.mylyn.internal.wikitext.markdown.core.LinkDefinitionParser;

/**
 * Markdown link/image definitions. Does not emit anything.
 * 
 * @author Stefan Seelmann
 */
public class LinkDefinitionBlock extends NestableBlock {

	@Override
	public boolean canStart(String line, int lineOffset) {
		return LinkDefinitionParser.LINK_DEFINITION_PATTERN.matcher(line.substring(lineOffset)).matches();
	}

	@Override
	protected int processLineContent(String line, int offset) {
		if (markupLanguage.isEmptyLine(line.substring(offset))) {
			setClosed(true);
			return offset;
		}

		return -1;
	}

}
