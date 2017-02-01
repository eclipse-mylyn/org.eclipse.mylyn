/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
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
 * A preformatted block is delimited by tripple-curlies {{{ }}}
 * 
 * @author David Green
 */
public class MonospacePhraseModifier extends PatternBasedElement {

	public class MonospaceElementProcessor extends PatternBasedElementProcessor {
		@Override
		public void emit() {
			getBuilder().beginSpan(SpanType.MONOSPACE, new Attributes());
			getBuilder().characters(group(1));
			getBuilder().endSpan();
		}
	}

	@Override
	protected String getPattern(int groupOffset) {
		return "\\{\\{\\{(.*?)\\}\\}\\}"; //$NON-NLS-1$
	}

	@Override
	protected int getPatternGroupCount() {
		return 1;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new MonospaceElementProcessor();
	}

}
