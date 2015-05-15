/*******************************************************************************
 * Copyright (c) 2015 Max Rydahl Andersen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Max Rydahl Andersen- initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.asciidoc.core.token;

import org.eclipse.mylyn.wikitext.core.parser.LinkAttributes;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElementProcessor;

/**
 * Detects email links: eclipse-dev@lists.jboss.org
 * 
 * @author Max Rydahl Andersen
 */
public class EmailLinkReplacementToken extends PatternBasedElement {


	private static final String EMAIL_PATTERN = 
			"([_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,}))";
	
	@Override
	protected String getPattern(int groupOffset) {
		return EMAIL_PATTERN; //$NON-NLS-1$
	}

	@Override
	protected int getPatternGroupCount() {
		return 4;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new EmailLinkReplacementTokenProcessor();
	}

	private static class EmailLinkReplacementTokenProcessor extends PatternBasedElementProcessor {
		@Override
		public void emit() {
			
			String text = group(1);
			String href = "mailto:" + text;
			
			builder.link(new LinkAttributes(), href, text);
			
		}
	}
}
