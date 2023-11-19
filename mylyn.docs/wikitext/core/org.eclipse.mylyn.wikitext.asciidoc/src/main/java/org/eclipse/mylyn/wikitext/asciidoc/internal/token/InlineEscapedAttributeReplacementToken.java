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

package org.eclipse.mylyn.wikitext.asciidoc.internal.token;

import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElementProcessor;

public class InlineEscapedAttributeReplacementToken extends PatternBasedElement {

	@Override
	protected String getPattern(int groupOffset) {
		return "\\\\(\\{\\w+\\})";
	}

	@Override
	protected int getPatternGroupCount() {
		return 1;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new InlineEscapedAttributeTokenReplacementProcessor();
	}

	private static class InlineEscapedAttributeTokenReplacementProcessor extends PatternBasedElementProcessor {
		@Override
		public void emit() {
			builder.characters(group(1));
		}
	}

}
