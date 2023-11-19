/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
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

package org.eclipse.mylyn.wikitext.tracwiki.internal.validation;

import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.tracwiki.TracWikiLanguage;
import org.eclipse.mylyn.wikitext.validation.DocumentLocalReferenceValidationRule;

public class TracWikiReferenceValidationRule extends DocumentLocalReferenceValidationRule {

	@Override
	protected MarkupLanguage createMarkupLanguage() {
		return new TracWikiLanguage();
	}

}
