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
package org.eclipse.mylyn.internal.wikitext.tracwiki.core.token;

import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElementProcessor;
import org.eclipse.mylyn.wikitext.tracwiki.core.TracWikiLanguage;

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
