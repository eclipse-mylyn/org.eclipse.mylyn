/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
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
 * A strategy for generating IDs from text, follows the Strategy design pattern.
 * 
 * @author David Green
 */
public abstract class IdGenerationStrategy {
	/**
	 * produce an id for use in an HTML anchor from heading text.
	 * 
	 * @param headingText
	 *            the text of the heading
	 * 
	 * @return an Id, or null if no Id could be computed from the given text
	 */
	public abstract String generateId(String headingText);
}
