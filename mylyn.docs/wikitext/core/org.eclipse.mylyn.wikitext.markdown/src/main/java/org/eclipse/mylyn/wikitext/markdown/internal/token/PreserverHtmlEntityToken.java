/*******************************************************************************
 * Copyright (c) 2012, 2014 Stefan Seelmann and others.
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

package org.eclipse.mylyn.wikitext.markdown.internal.token;

import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElementProcessor;

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
