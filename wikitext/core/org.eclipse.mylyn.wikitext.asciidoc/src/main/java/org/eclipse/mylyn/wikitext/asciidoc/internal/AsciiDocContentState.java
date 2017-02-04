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

package org.eclipse.mylyn.wikitext.asciidoc.internal;

import java.util.List;
import java.util.Map;

import org.eclipse.mylyn.wikitext.asciidoc.internal.util.LanguageSupport;
import org.eclipse.mylyn.wikitext.parser.markup.Block;
import org.eclipse.mylyn.wikitext.parser.markup.ContentState;
import org.eclipse.mylyn.wikitext.parser.markup.Processor;

/**
 * Extended {@link ContentState content state} to provide additional AsciiDoc information to {@link Block blocks} and
 * other {@link Processor processors}
 */
public class AsciiDocContentState extends ContentState {

	// latest title provided via .<optional title> syntax
	private String lastTitle;

	private String lastPropertiesText;

	public void setLastTitle(String text) {
		this.lastTitle = text;
	}

	/**
	 * @return last title provided via [{@code .<optional title>} syntax
	 */
	public String getLastTitle() {
		return lastTitle;
	}

	public void setLastPropertiesText(String text) {
		this.lastPropertiesText = text;
	}

	public Map<String, String> getLastProperties(List<String> positionalParameters) {
		return LanguageSupport.parseFormattingProperties(lastPropertiesText, positionalParameters);
	}

}
