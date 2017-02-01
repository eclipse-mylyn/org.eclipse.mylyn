/*******************************************************************************
 * Copyright (c) 2010, 2011 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.textile.core.token;

import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElementProcessor;

/**
 * detects HTML entities, such as <code>&amp;amp;</code>
 * 
 * @author David Green
 */
public class EntityReplacementToken extends PatternBasedElement {

	@Override
	protected String getPattern(int groupOffset) {
		return "&(#?[a-zA-Z0-9]+?);"; //$NON-NLS-1$
	}

	@Override
	protected int getPatternGroupCount() {
		return 1;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {

		return new PatternBasedElementProcessor() {
			@Override
			public void emit() {
				String entity = group(1);
				getBuilder().entityReference(entity);
			}
		};
	}
}
