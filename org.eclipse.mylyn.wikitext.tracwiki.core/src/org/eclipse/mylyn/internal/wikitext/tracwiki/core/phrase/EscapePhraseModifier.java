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
package org.eclipse.mylyn.internal.wikitext.tracwiki.core.phrase;



import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElementProcessor;

/**
 *
 *
 * @author David Green
 */
public class EscapePhraseModifier extends PatternBasedElement {

	@Override
	protected String getPattern(int groupOffset) {
		String escapedContent = "(\\S(?:.*?\\S)?)";
		return
		"(?:(?:`" +
		escapedContent + // content
		"`)|(?:\\{\\{" +
		escapedContent + // content
		"\\}\\}))";
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
