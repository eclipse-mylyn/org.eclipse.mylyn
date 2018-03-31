/*******************************************************************************
 * Copyright (c) 2007, 2011 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.tracwiki.internal.phrase;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElementProcessor;

/**
 * @author David Green
 */
public class EscapePhraseModifier extends PatternBasedElement {

	@Override
	protected String getPattern(int groupOffset) {
		String escapedContent = "(\\S(?:.*?\\S)?)"; //$NON-NLS-1$
		return "(?:(?:`" + escapedContent + // content //$NON-NLS-1$
				"`)|(?:\\{\\{" + escapedContent + // content //$NON-NLS-1$
				"\\}\\}))"; //$NON-NLS-1$
	}

	@Override
	protected int getPatternGroupCount() {
		return 2;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new EscapeProcessor();
	}

	private static class EscapeProcessor extends PatternBasedElementProcessor {

		private EscapeProcessor() {
		}

		@Override
		public void emit() {
			getBuilder().beginSpan(SpanType.MONOSPACE, new Attributes());
			String group = group(1);
			if (group == null) {
				group = group(2);
			}
			getBuilder().characters(group);
			getBuilder().endSpan();

		}

	}

}
