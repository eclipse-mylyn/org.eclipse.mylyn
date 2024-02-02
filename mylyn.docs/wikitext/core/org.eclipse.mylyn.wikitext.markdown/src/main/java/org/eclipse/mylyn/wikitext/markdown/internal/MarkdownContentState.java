/*******************************************************************************
 * Copyright (c) 2013 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.markdown.internal;

import org.eclipse.mylyn.wikitext.parser.markup.ContentState;

/**
 * Extended version of ContentState that preprocesses Markdown for reference-style link support.
 * 
 * @author Stefan Seelmann
 */
public class MarkdownContentState extends ContentState {

	private LinkDefinitionParser linkDefinitionParser;

	private LinkDefinitionUsageTracker linkDefinitionUsageTracker;

	@Override
	protected void setMarkupContent(String markupContent) {
		super.setMarkupContent(markupContent);

		linkDefinitionParser = new LinkDefinitionParser();
		linkDefinitionParser.parse(markupContent);

		linkDefinitionUsageTracker = new LinkDefinitionUsageTracker(this, linkDefinitionParser);
	}

	/**
	 * Gets the {@link LinkDefinition} for the given link identifier, or <code>null</code> if there is no such {@link LinkDefinition}.
	 * 
	 * @param id
	 *            the link identifier.
	 * @return the {@link LinkDefinition} or <code>null</code>
	 */
	public LinkDefinition getLinkDefinition(String id) {
		linkDefinitionUsageTracker.linkDefinitionRequested(id);
		return linkDefinitionParser.getLinkDefinition(id);
	}

	public LinkDefinitionUsageTracker getLinkDefinitionUsageTracker() {
		return linkDefinitionUsageTracker;
	}
}
