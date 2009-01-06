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

package org.eclipse.mylyn.wikitext.core.parser.markup;

/**
 * A default ID generation strategy which removes all non-alphanumeric characters from the heading text to produce an
 * id.
 * 
 * @author David Green
 */
public class DefaultIdGenerationStrategy extends IdGenerationStrategy {

	@Override
	public String generateId(String headingText) {
		String anchor = headingText.replaceAll("[^a-zA-Z0-9]", ""); //$NON-NLS-1$ //$NON-NLS-2$
		if (anchor.length() > 0 && Character.isDigit(anchor.charAt(0))) {
			anchor = 'a' + anchor;
		}
		return anchor;
	}

}
