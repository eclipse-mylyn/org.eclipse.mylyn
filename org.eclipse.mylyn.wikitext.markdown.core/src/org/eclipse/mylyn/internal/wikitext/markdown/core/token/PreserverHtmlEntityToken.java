/*******************************************************************************
 * Copyright (c) 2012, 2013 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.markdown.core.token;

import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElementProcessor;

/**
 * A phrase modifier that detects HTML and XML entities in the source.
 * 
 * @author Stefan Seelmann
 */
public class PreserverHtmlEntityToken extends PatternBasedElement {

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
