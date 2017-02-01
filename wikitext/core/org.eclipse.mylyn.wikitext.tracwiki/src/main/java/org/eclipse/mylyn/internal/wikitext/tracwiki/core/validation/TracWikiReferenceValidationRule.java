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

package org.eclipse.mylyn.internal.wikitext.tracwiki.core.validation;

import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.tracwiki.core.TracWikiLanguage;
import org.eclipse.mylyn.wikitext.validation.DocumentLocalReferenceValidationRule;

public class TracWikiReferenceValidationRule extends DocumentLocalReferenceValidationRule {

	@Override
	protected MarkupLanguage createMarkupLanguage() {
		return new TracWikiLanguage();
	}

}
