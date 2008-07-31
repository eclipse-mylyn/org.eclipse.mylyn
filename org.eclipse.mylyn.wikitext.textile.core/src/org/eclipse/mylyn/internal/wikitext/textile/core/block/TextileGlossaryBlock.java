/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
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

import org.eclipse.mylyn.wikitext.core.parser.markup.block.GlossaryBlock;

/**
 *
 *
 * @author David Green
 */
public class TextileGlossaryBlock extends GlossaryBlock {

	static final Pattern startPattern = Pattern.compile("\\s*\\{glossary(?::([^\\}]+))?\\}\\s*");

	private Matcher matcher;

	@Override
	public int processLineContent(String line,int offset) {
		if (blockLineNumber == 0) {
			String options = matcher.group(1);
			if (options != null) {
				String[] optionPairs = options.split("\\s*\\|\\s*");
				for (String optionPair: optionPairs) {
					String[] keyValue = optionPair.split("\\s*=\\s*");
					if (keyValue.length == 2) {
						String key = keyValue[0].trim();
						String value = keyValue[1].trim();

						if (key.equals("style")) {
							setStyle(value);
						}
					}
				}
			}
		}
		return super.processLineContent(line, offset);
	}

	@Override
	public boolean canStart(String line,int lineOffset) {
		if (lineOffset == 0 && !markupLanguage.isFilterGenerativeContents()) {
			matcher = startPattern.matcher(line);
			return matcher.matches();
		} else {
			matcher = null;
			return false;
		}
	}
}
