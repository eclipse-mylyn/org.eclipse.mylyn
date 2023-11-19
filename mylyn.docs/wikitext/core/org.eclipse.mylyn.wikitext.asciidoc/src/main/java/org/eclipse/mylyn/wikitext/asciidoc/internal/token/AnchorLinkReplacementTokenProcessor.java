/*******************************************************************************
 * Copyright (c) 2016 Jeremie Bresson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Jeremie Bresson - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.asciidoc.internal.token;

import org.eclipse.mylyn.wikitext.parser.LinkAttributes;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElementProcessor;

/**
 * Element processor for {@link AnchorLinkReplacementToken} and {@link AnchorLinkMacroReplacementToken}
 */
class AnchorLinkReplacementTokenProcessor extends PatternBasedElementProcessor {
	@Override
	public void emit() {
		LinkAttributes attribute = new LinkAttributes();
		attribute.setId(group(1));
		builder.link(attribute, null, ""); //$NON-NLS-1$
	}
}
