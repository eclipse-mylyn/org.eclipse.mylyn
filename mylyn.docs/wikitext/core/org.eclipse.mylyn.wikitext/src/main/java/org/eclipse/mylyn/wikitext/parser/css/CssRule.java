/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.parser.css;

import java.util.Objects;

/**
 * A rule (name and value) as defined by CSS
 *
 * @author David Green
 * @since 3.0
 */
public class CssRule {
	/**
	 * the name of the rule
	 */
	public final String name;

	/**
	 * the value of the rule
	 */
	public final String value;

	/**
	 * the offset at which the rule was declared
	 */
	public final int offset;

	/**
	 * the length of the rule declaration
	 */
	public final int length;

	/**
	 * the offset of the name of the rule
	 */
	public final int nameOffset;

	/**
	 * the offset of the value of the rule
	 */
	public final int valueOffset;

	public CssRule(String name, String value, int offset, int length, int nameOffset, int valueOffset) {
		this.name = name;
		this.value = value;
		this.offset = offset;
		this.length = length;
		this.nameOffset = nameOffset;
		this.valueOffset = valueOffset;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof CssRule other)) {
			return false;
		}
		return Objects.equals(name, other.name) && Objects.equals(value, other.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, value);
	}
}