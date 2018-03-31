/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.twiki.internal.phrase;

import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElementProcessor;
import org.eclipse.mylyn.wikitext.twiki.TWikiLanguage;

public class AutoLinkSwitchPhraseModifier extends PatternBasedElement {

	@Override
	protected String getPattern(int groupOffset) {
		return "(</?noautolink>)"; //$NON-NLS-1$
	}

	@Override
	protected int getPatternGroupCount() {
		return 1;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new SwitchProcessor();
	}

	private static class SwitchProcessor extends PatternBasedElementProcessor {
		@Override
		public void emit() {
			String switchText = group(1);
			TWikiLanguage twikiLanguage = (TWikiLanguage) markupLanguage;
			twikiLanguage.setAutoLinking(switchText.indexOf('/') != -1);
		}
	}
}
