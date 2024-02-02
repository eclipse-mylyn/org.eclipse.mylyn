/*******************************************************************************
 * Copyright (c) 2015 Jeremie Bresson and others.
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

package org.eclipse.mylyn.wikitext.mediawiki.internal.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.parser.markup.Block;

/**
 * a block for MediaWiki <a href="http://www.mediawiki.org/wiki/Help:Magic_words#Behavior_switches">behavior switches</a> (processed as
 * empty string).
 *
 * @See also {@link TableOfContentsBlock}
 * @author Jeremie Bresson
 */
public class BehaviorSwitchBlock extends Block {

	private static final Pattern PATTERN = Pattern.compile(
			"__(FORCE|NO)?(EDIT|NEW)?(CC|CONTENT|DISAMBIG|END|GALLERY|HIDDENCAT|INDEX|SECTION|START|STATICREDIRECT|TC|TITLE|TOC)(CONVERT|LINK)?__"); //$NON-NLS-1$

	private Matcher matcher;

	@Override
	public boolean canStart(String line, int lineOffset) {
		matcher = PATTERN.matcher(line);
		if (lineOffset > 0) {
			matcher.region(lineOffset, line.length());
		}
		if (matcher.matches()) {
			return true;
		}
		matcher = null;
		return false;
	}

	@Override
	protected int processLineContent(String line, int offset) {
		setClosed(true);
		return matcher.end();
	}
}
