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
 * recognizes links to Trac reports, eg: <code>{1}</code> or <code>report:1</code>
 * 
 * @author David Green
 */
public class ReportLinkReplacementToken extends PatternBasedElement {

	@Override
	protected String getPattern(int groupOffset) {
		return "(\\{(\\d+)\\})|(report:(\\d+))"; //$NON-NLS-1$
	}

	@Override
	protected int getPatternGroupCount() {
		return 4;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new ReportLinkReplacementTokenProcessor();
	}

	private static class ReportLinkReplacementTokenProcessor extends PatternBasedElementProcessor {
		@Override
		public void emit() {
			String text = group(1);
			String reportId = group(2);
			if (reportId == null) {
				text = group(3);
				reportId = group(4);
			}
			String href = ((TracWikiLanguage) markupLanguage).toReportHref(reportId);
			builder.link(href, text);
		}
	}

}
