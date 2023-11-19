/*******************************************************************************
 * Copyright (c) 2017 Jeremie Bresson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Jeremie Bresson - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.asciidoc.internal.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.asciidoc.internal.AsciiDocContentState;
import org.eclipse.mylyn.wikitext.parser.markup.Block;

/**
 * AsciiDoc ":key: value" attribute definitions
 *
 * @author Jeremie Bresson
 */
public class AttributeDefinitionBlock extends Block {

	private static final Pattern pattern = Pattern.compile("^:(.*?):(.*)"); //$NON-NLS-1$

	private String key;

	private String value;

	@Override
	public boolean canStart(String line, int lineOffset) {
		if (lineOffset == 0) {
			Matcher matcher = pattern.matcher(line);
			if (matcher.matches()) {
				key = matcher.group(1);
				value = matcher.group(2);
				return true;
			}
		}
		key = null;
		value = null;
		return false;
	}

	@Override
	public int processLineContent(String line, int offset) {
		if (key.endsWith("!")) { //$NON-NLS-1$
			key = key.substring(0, key.length() - 1).trim();
			((AsciiDocContentState) getState()).removeAttribute(key);
		} else {
			String newKey = key.trim();
			String newValue = (value == null) ? "" : value.trim();
			((AsciiDocContentState) getState()).putAttribute(newKey, newValue);
		}
		setClosed(true);
		return -1;
	}

}
