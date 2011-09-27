/*******************************************************************************
 * Copyright (c) 2011 Igor Malinin and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Igor Malinin - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.creole.core.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.markup.Block;

/**
 * @author Igor Malinin
 */
public class HorizontalRuleBlock extends Block {

	private static final Pattern pattern = Pattern.compile("\\s*-{4}\\s*"); //$NON-NLS-1$

	private Matcher matcher;

	@Override
	public int processLineContent(String line, int offset) {
		builder.charactersUnescaped("<hr/>"); //$NON-NLS-1$
		setClosed(true);
		return -1;
	}

	@Override
	public boolean canStart(String line, int lineOffset) {
		if (lineOffset == 0) {
			matcher = pattern.matcher(line);
			return matcher.matches();
		} else {
			matcher = null;
			return false;
		}
	}

}
