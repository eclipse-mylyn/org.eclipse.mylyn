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

package org.eclipse.mylyn.wikitext.parser.markup;

/**
 * A strategy for generating IDs from text, follows the Strategy design pattern.
 *
 * @author David Green
 * @since 3.0
 */
public abstract class IdGenerationStrategy {
	/**
	 * produce an id for use in an HTML anchor from heading text.
	 *
	 * @param headingText
	 *            the text of the heading
	 * @return an Id, or null if no Id could be computed from the given text
	 */
	public abstract String generateId(String headingText);
}
