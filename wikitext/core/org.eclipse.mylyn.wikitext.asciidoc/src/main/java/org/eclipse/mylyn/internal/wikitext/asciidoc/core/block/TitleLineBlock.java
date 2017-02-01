/*******************************************************************************
 * Copyright (c) 2007, 2016 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     Max Rydahl Andersen - Bug 474084
 *     Patrik Suzzi <psuzzi@gmail.com> - Bug 474084
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.asciidoc.core.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.internal.wikitext.asciidoc.core.AsciiDocContentState;
import org.eclipse.mylyn.wikitext.parser.markup.Block;
import org.eclipse.mylyn.wikitext.parser.markup.ContentState;

/**
 * "Block" which detects the optional titles for blocks. i.e. {@code .This is a title}. The title is stored in
 * {@link ContentState contentstate} to be used by the one that needs it. This blocks emits nothing in the resulting
 * document.
 *
 * @author Max Rydahl Andersen
 */
public class TitleLineBlock extends Block {

	private static final Pattern pattern = Pattern.compile("^\\.([^ ]+.*)"); //$NON-NLS-1$

	private Matcher matcher;

	@Override
	public boolean canStart(String line, int lineOffset) {
		if (lineOffset == 0) {
			Matcher m = pattern.matcher(line);
			if (m.matches()) {
				matcher = m;
				return true;
			}
		}
		matcher = null;
		return false;
	}

	@Override
	public int processLineContent(String line, int offset) {
		String text = matcher.group(1);

		((AsciiDocContentState) state).setLastTitle(text);

		setClosed(true);
		matcher = null;
		return -1;
	}

}
