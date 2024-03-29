/*******************************************************************************
 * Copyright (c) 2007, 2011 David Green and others.
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
package org.eclipse.mylyn.wikitext.mediawiki.internal.token;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElementProcessor;

/**
 * @author David Green
 */
public class EntityReferenceReplacementToken extends PatternBasedElement {

	private static final Set<String> allowedEntities = new HashSet<>();
	static {
		allowedEntities.add(""); //$NON-NLS-1$
	}

	@Override
	protected String getPattern(int groupOffset) {
		return "&(#?[a-zA-Z0-9]{2,7});"; //$NON-NLS-1$
	}

	@Override
	protected int getPatternGroupCount() {
		return 1;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new EntityReferenceProcessor();
	}

	private static class EntityReferenceProcessor extends PatternBasedElementProcessor {

		@Override
		public void emit() {
			String entity = group(1);
			getBuilder().entityReference(entity);
		}

	}
}
