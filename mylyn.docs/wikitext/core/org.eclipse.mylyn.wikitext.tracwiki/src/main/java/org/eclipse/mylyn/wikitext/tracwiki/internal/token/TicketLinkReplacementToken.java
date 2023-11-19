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
package org.eclipse.mylyn.wikitext.tracwiki.internal.token;

import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElementProcessor;
import org.eclipse.mylyn.wikitext.tracwiki.TracWikiLanguage;

/**
 * recognizes links to Trac tickets, eg: <code>#1</code> or <code>ticket:1</code> or <code>comment:1:ticket:2</code>
 * 
 * @author David Green
 */
public class TicketLinkReplacementToken extends PatternBasedElement {

	@Override
	protected String getPattern(int groupOffset) {
		return "((?:comment:(\\d+):)?(?:#|ticket:)(\\d+))"; //$NON-NLS-1$
	}

	@Override
	protected int getPatternGroupCount() {
		return 3;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new TicketLinkReplacementTokenProcessor();
	}

	private static class TicketLinkReplacementTokenProcessor extends PatternBasedElementProcessor {
		@Override
		public void emit() {
			String text = group(1);
			String ticketId = group(3);
			String commentNumber = group(2);
			String href = ((TracWikiLanguage) markupLanguage).toTicketHref(ticketId, commentNumber);
			builder.link(href, text);
		}
	}

}
