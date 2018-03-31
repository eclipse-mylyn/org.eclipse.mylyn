/*******************************************************************************
 * Copyright (c) 2011 Igor Malinin and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Igor Malinin - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.creole.internal.validation;

import org.eclipse.mylyn.wikitext.creole.CreoleLanguage;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.validation.DocumentLocalReferenceValidationRule;

public class CreoleReferenceValidationRule extends DocumentLocalReferenceValidationRule {

	@Override
	protected MarkupLanguage createMarkupLanguage() {
		return new CreoleLanguage();
	}

}
