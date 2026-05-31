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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * An abstraction for a CSS stylesheet.
 *
 * @author David Green
 * @since 3.0
 */
public class Stylesheet {
	private final List<Block> blocks = new ArrayList<>();

	public List<Block> getBlocks() {
		return Collections.unmodifiableList(blocks);
	}

	public interface Receiver {

		void apply(CssRule rule);

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

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Stylesheet other)) {
			return false;
		}
		return Objects.equals(blocks, other.blocks);
	}

	@Override
	public int hashCode() {
		return Objects.hash(blocks);
	}

	@Override
	public String toString() {
		return blocks.stream()
				.map(block -> block.getSelector().toString() + " { " //$NON-NLS-1$
						+ block.getRules().stream()
						.map(r -> r.name + ": " + r.value) //$NON-NLS-1$
						.collect(Collectors.joining("; ")) //$NON-NLS-1$
						+ " }") //$NON-NLS-1$
				.collect(Collectors.joining("\n")); //$NON-NLS-1$
	}
}