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

package org.eclipse.mylyn.internal.wikitext.core.util.css;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An abstraction for a CSS stylesheet.
 * 
 * @author David Green
 */
public class Stylesheet {
	private final List<Block> blocks = new ArrayList<Block>();

	public List<Block> getBlocks() {
		return Collections.unmodifiableList(blocks);
	}

	public interface Receiver {

		public void apply(CssRule rule);

	}

	public void applyTo(ElementInfo context, Receiver receiver) {
		for (Block block : blocks) {
			if (block.getSelector().select(context)) {
				for (CssRule rule : block.getRules()) {
					receiver.apply(rule);
				}
			}
		}
	}

	void add(Block block) {
		blocks.add(block);
	}
}
