/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
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
