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
package org.eclipse.mylyn.internal.wikitext.confluence.core.token;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElementProcessor;

/**
 *
 *
 * @author David Green
 */
public class AnchorReplacementToken extends PatternBasedElement {

	@Override
	protected String getPattern(int groupOffset) {
		return "\\{anchor:([^\\}]+)\\}";
	}

	@Override
	protected int getPatternGroupCount() {
		return 1;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new AnchorReplacementTokenProcessor();
	}

	private static class AnchorReplacementTokenProcessor extends PatternBasedElementProcessor {
		@Override
		public void emit() {
			String name = group(1);
			Attributes attributes = new Attributes();
			attributes.setId(name);
			getBuilder().beginSpan(SpanType.SPAN, attributes);
			getBuilder().endSpan();
		}

	}

}
