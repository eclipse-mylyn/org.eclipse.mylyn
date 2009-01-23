/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.ui.util.css;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstraction for a block of CSS rules
 * 
 * @author David Green
 */
public class Block {

	private final Selector selector;

	private final List<CssRule> rules;

	Block(Selector selector) {
		this(selector, new ArrayList<CssRule>());
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
}
