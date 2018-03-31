/*******************************************************************************
 * Copyright (c) 2007, 2010 David Green and others.
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
package org.eclipse.mylyn.wikitext.mediawiki.internal.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.mediawiki.MediaWikiLanguage;
import org.eclipse.mylyn.wikitext.parser.outline.OutlineItem;
import org.eclipse.mylyn.wikitext.parser.outline.OutlineParser;

/**
 * @author David Green
 */
public class TableOfContentsBlock extends AbstractTableOfContentsBlock {

	static final Pattern startPattern = Pattern.compile("\\s*__TOC__\\s*(.*?)"); //$NON-NLS-1$

	private int blockLineNumber = 0;

	private Matcher matcher;

	@Override
	public int processLineContent(String line, int offset) {
		if (blockLineNumber++ > 0) {
			setClosed(true);
			return 0;
		}

		if (!getMarkupLanguage().isFilterGenerativeContents()) {
			OutlineParser outlineParser = new OutlineParser(new MediaWikiLanguage());
			OutlineItem rootItem = outlineParser.parse(state.getMarkupContent());

			emitToc(rootItem);
		}
		int start = matcher.start(1);
		if (start > 0) {
			setClosed(true);
		}
		return start;
	}

	@Override
	public boolean canStart(String line, int lineOffset) {
		if (lineOffset == 0 && !getMarkupLanguage().isFilterGenerativeContents()) {
			matcher = startPattern.matcher(line);
			blockLineNumber = 0;
			return matcher.matches();
		} else {
			matcher = null;
			return false;
		}
	}

}
