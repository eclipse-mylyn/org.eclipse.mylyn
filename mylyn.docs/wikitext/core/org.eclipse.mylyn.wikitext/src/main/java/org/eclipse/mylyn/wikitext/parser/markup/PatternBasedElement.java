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
 * An element of markup that is detected using a regular expression pattern. Serves as a means of detecting markup elements and providing a
 * factory for processors that can process the markup element. Implementations of this class must be thread-safe (generally stateless).
 *
 * @author David Green
 * @since 3.0
 */
public abstract class PatternBasedElement implements Cloneable {

	/**
	 * Get the regular expression pattern that matches this element. Generally the pattern may be assembled into a single larger regular
	 * expression.
	 *
	 * @param groupOffset
	 *            the offset of the groups in the pattern, 0 indicating no offset
	 * @return the regular expression pattern
	 */
	protected abstract String getPattern(int groupOffset);

	/**
	 * The number of capturing groups in the {@link #getPattern(int) pattern}. Note that implementations must take care to return the
	 * correct value otherwise the markup language will not work correctly.
	 */
	protected abstract int getPatternGroupCount();

	/**
	 * create a new processor for processing the type of element detected by this class.
	 */
	protected abstract PatternBasedElementProcessor newProcessor();

	@Override
	public PatternBasedElement clone() {
		try {
			return (PatternBasedElement) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException();
		}
	}
}
