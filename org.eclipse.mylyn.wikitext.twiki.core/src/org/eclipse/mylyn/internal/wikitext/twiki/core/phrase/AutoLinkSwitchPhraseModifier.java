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
package org.eclipse.mylyn.internal.wikitext.twiki.core.phrase;

import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElementProcessor;
import org.eclipse.mylyn.wikitext.twiki.core.TWikiLanguage;

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
