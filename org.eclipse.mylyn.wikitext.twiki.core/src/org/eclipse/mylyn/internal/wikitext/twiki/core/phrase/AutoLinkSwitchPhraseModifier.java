package org.eclipse.mylyn.internal.wikitext.twiki.core.phrase;

import org.eclipse.mylyn.internal.wikitext.twiki.core.TWikiLanguage;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElementProcessor;

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
