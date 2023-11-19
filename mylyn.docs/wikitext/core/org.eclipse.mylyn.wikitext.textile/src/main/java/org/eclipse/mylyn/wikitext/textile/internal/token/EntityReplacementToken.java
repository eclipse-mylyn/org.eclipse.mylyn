/*******************************************************************************
 * Copyright (c) 2010, 2011 David Green and others.
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

package org.eclipse.mylyn.wikitext.textile.internal.token;

import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElementProcessor;

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
