/*******************************************************************************
 * Copyright (c) 2007, 2010 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.textile.core.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineItem;
import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineParser;
import org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;

/**
 * @author David Green
 */
public class TableOfContentsBlock extends AbstractTableOfContentsBlock {

	private static final String OPTION_MAX_LEVEL = "maxLevel";//$NON-NLS-1$

	private static final String OPTION_STYLE = "style"; //$NON-NLS-1$

	private static final String OPTION_CLASS = "class"; //$NON-NLS-1$

	static final Pattern startPattern = Pattern.compile("\\s*\\{toc(?::([^\\}]+))?\\}\\s*"); //$NON-NLS-1$

	private int blockLineNumber = 0;

	private Matcher matcher;

	@Override
	public int processLineContent(String line, int offset) {
		if (blockLineNumber++ > 0) {
			setClosed(true);
			return 0;
		}

		if (!getMarkupLanguage().isFilterGenerativeContents()) {
			String options = matcher.group(1);
			if (options != null) {
				String[] optionPairs = options.split("\\s*\\|\\s*"); //$NON-NLS-1$
				for (String optionPair : optionPairs) {
					String[] keyValue = optionPair.split("\\s*=\\s*"); //$NON-NLS-1$
					if (keyValue.length == 2) {
						String key = keyValue[0].trim();
						String value = keyValue[1].trim();

						if (key.equals(OPTION_STYLE)) {
							setStyle(value);
						} else if (key.equals(OPTION_MAX_LEVEL)) {
							try {
								maxLevel = Integer.parseInt(value);
							} catch (NumberFormatException e) {
							}
						} else if (key.equals(OPTION_CLASS)) {
							setCssClass(value);
						}
					}
				}
			}

			OutlineParser outlineParser = new OutlineParser(new TextileLanguage());
			OutlineItem rootItem = outlineParser.parse(state.getMarkupContent());

			emitToc(rootItem);
		}
		return -1;
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
