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
package org.eclipse.mylyn.internal.wikitext.twiki.core.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.HeadingAttributes;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;

/**
 * 
 * 
 * @author David Green
 */
public class HeadingBlock extends Block {

	private static final Pattern startPattern = Pattern.compile("---(\\+{1,6})\\s*(!!\\s*)?(.*)");

	private Matcher matcher;

	@Override
	public int processLineContent(String line, int offset) {
		offset = matcher.start(3);
		int level = matcher.group(1).length();
		
		String bangEscape = matcher.group(2);
		boolean omitFromToc = false;
		if (bangEscape != null && bangEscape.length() > 0) {
			omitFromToc = true;
		}

		if (offset > 0 && level > 0) {
			HeadingAttributes attributes = new HeadingAttributes();
			attributes.setOmitFromTableOfContents(omitFromToc);
			attributes.setId(state.getIdGenerator().newId("h" + level, line.substring(offset)));
			
			builder.beginHeading(level, attributes);
			builder.characters(line.substring(offset).trim());
			builder.endHeading();
		}
		
		setClosed(true);
		return -1;
	}

	@Override
	public boolean canStart(String line, int lineOffset) {
		if (lineOffset == 0) {
			matcher = startPattern.matcher(line);
			return matcher.matches();
		} else {
			matcher = null;
			return false;
		}
	}

}
