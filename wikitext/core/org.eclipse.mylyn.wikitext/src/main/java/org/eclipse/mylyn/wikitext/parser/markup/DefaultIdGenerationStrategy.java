/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
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

package org.eclipse.mylyn.wikitext.parser.markup;

/**
 * A default ID generation strategy which removes all non-alphanumeric characters from the heading text to produce an
 * id.
 *
 * @author David Green
 * @since 3.0
 */
public class DefaultIdGenerationStrategy extends IdGenerationStrategy {

	@Override
	public String generateId(String headingText) {
		String anchor = headingText.replaceAll("[^a-zA-Z0-9.]", ""); //$NON-NLS-1$ //$NON-NLS-2$
		if (anchor.length() > 0 && Character.isDigit(anchor.charAt(0))) {
			anchor = 'a' + anchor;
		}
		return anchor;
	}

}
