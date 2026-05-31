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
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.parser.css;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * An abstraction for a block of CSS rules
 *
 * @author David Green
 * @since 3.0
 */
public class Block {

	private final Selector selector;

	private final List<CssRule> rules;

	Block(Selector selector) {
		this(selector, new ArrayList<>());
	}

	Block(Selector selector, List<CssRule> rules) {
		this.selector = selector;
		this.rules = rules;
	}

	public Selector getSelector() {
		return selector;
	}

	public List<CssRule> getRules() {
		return rules;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Block other)) {
			return false;
		}
		return Objects.equals(selector, other.selector) && Objects.equals(rules, other.rules);
	}

	@Override
	public int hashCode() {
		return Objects.hash(selector, rules);
	}
}