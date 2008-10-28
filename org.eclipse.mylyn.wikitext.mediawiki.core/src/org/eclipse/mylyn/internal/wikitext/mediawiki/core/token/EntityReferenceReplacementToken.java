/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.mediawiki.core.token;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElementProcessor;

/**
 * 
 * 
 * @author David Green
 */
public class EntityReferenceReplacementToken extends PatternBasedElement {

	private static final Set<String> allowedEntities = new HashSet<String>();
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
