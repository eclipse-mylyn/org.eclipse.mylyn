/*******************************************************************************
 * Copyright (c) 2013 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.markdown.internal.block;

import org.eclipse.mylyn.wikitext.markdown.internal.LinkDefinitionParser;

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
