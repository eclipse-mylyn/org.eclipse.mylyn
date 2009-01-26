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
 * recognizes links to Trac revision logs, eg: <code>r1:3</code> or <code>[1:3]</code> or <code>log:@1:3</code> or
 * <code>log:trunk@1:3</code> or <code>[2:5/trunk]</code>
 * 
 * @author David Green
 */
public class RevisionLogReplacementToken extends PatternBasedElement {

	@Override
	protected String getPattern(int groupOffset) {
		return "(r(\\d+):(\\d+))|(\\[(\\d+):(\\d+)(?:/(\\w+))?\\])|(log:(?:(\\w+))?@(\\d+):(\\d+))"; //$NON-NLS-1$
	}

	@Override
	protected int getPatternGroupCount() {
		return 11;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new RevisionLogReplacementTokenProcessor();
	}

	private static class RevisionLogReplacementTokenProcessor extends PatternBasedElementProcessor {
		@Override
		public void emit() {
			String text = group(1);
			String revision1 = group(2);
			String revision2 = group(3);
			String restriction = null;
			if (revision1 == null) {
				text = group(4);
				revision1 = group(5);
				revision2 = group(6);
				restriction = group(7);
				if (revision1 == null) {
					text = group(8);
					revision1 = group(10);
					revision2 = group(11);
					restriction = group(9);
				}
			}
			String href = ((TracWikiLanguage) markupLanguage).toRevisionLogHref(revision1, revision2, restriction);
			builder.link(href, text);
		}
	}

}
