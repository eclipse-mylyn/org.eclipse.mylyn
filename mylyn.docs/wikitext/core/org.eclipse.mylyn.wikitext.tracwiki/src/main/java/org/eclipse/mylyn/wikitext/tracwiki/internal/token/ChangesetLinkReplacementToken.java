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
 * recognizes links to Trac changesets, eg: <code>r1</code> or <code>[1]</code> or <code>[1/trunk]</code> or <code>changeset:1</code> or
 * <code>changeset:1/trunk</code>
 * 
 * @author David Green
 */
public class ChangesetLinkReplacementToken extends PatternBasedElement {

	@Override
	protected String getPattern(int groupOffset) {
		return "(r(\\d+))|(\\[(\\d+)(?:/(\\w+))?\\])|(changeset:(\\d+)(?:/(\\w+))?)"; //$NON-NLS-1$
	}

	@Override
	protected int getPatternGroupCount() {
		return 8;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new ChangesetReplacementTokenProcessor();
	}

	private static class ChangesetReplacementTokenProcessor extends PatternBasedElementProcessor {
		@Override
		public void emit() {
			String text = group(1);
			String changesetId = group(2);
			String restriction = null;
			if (changesetId == null) {
				text = group(3);
				changesetId = group(4);
				restriction = group(5);
				if (changesetId == null) {
					text = group(6);
					changesetId = group(7);
					restriction = group(8);
				}
			}
			String href = ((TracWikiLanguage) markupLanguage).toChangesetHref(changesetId, restriction);
			builder.link(href, text);
		}
	}

}
