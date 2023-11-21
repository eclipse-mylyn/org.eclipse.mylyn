/*******************************************************************************
 * Copyright (c) 2015 Max Rydahl Andersen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Max Rydahl Andersen- initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.asciidoc.internal.token;

import org.eclipse.mylyn.wikitext.parser.LinkAttributes;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElementProcessor;

/**
 * Detects email links: eclipse-dev@lists.jboss.org
 *
 * @author Max Rydahl Andersen
 */
public class EmailLinkReplacementToken extends PatternBasedElement {

	private static final String EMAIL_PATTERN = "([_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" //$NON-NLS-1$
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,}))"; //$NON-NLS-1$

	@Override
	protected String getPattern(int groupOffset) {
		return EMAIL_PATTERN;
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
			String href = "mailto:" + text; //$NON-NLS-1$

			builder.link(new LinkAttributes(), href, text);

		}
	}
}
