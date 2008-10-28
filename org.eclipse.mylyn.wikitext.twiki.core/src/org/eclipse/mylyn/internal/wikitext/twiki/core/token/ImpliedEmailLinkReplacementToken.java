package org.eclipse.mylyn.internal.wikitext.twiki.core.token;

import org.eclipse.mylyn.wikitext.core.parser.LinkAttributes;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElementProcessor;

public class ImpliedEmailLinkReplacementToken extends PatternBasedElement {

	@Override
	protected String getPattern(int groupOffset) {
		return "([a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+(?:[a-zA-Z]{2,6}))"; //$NON-NLS-1$
	}

	@Override
	protected int getPatternGroupCount() {
		return 1;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new EmailLinkProcessor();
	}

	private static class EmailLinkProcessor extends PatternBasedElementProcessor {

		@Override
		public void emit() {
			String email = group(1);
			builder.link(new LinkAttributes(), "mailto:"+email, email); //$NON-NLS-1$
		}
		
	}
}
