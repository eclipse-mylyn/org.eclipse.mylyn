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
package org.eclipse.mylyn.internal.wikitext.ui.editor.help;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.Bundle;

// FIXME: move to internal
/**
 * A handle to cheat sheet content, which may modify the content to be more suitable for
 * cheat-sheet viewing by applying CSS styles.
 * 
 * @author David Green
 *
 */
public class CheatSheetContent extends HelpContent {

	private Pattern headingOpenTagPattern = Pattern.compile("(<h[4-6][^>]*)>",Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);

	public CheatSheetContent(Bundle provider, String resourcePath,
			String resourceContentLanguage, String markupLanguageName) {
		super(provider, resourcePath, resourceContentLanguage, markupLanguageName);
	}

	@Override
	public String getContent() throws IOException {
		String content = super.getContent();
		Matcher headingMatcher = headingOpenTagPattern.matcher(content);
		return headingMatcher.replaceAll("$1 style=\"color: DarkBlue;\">");
	}

}
