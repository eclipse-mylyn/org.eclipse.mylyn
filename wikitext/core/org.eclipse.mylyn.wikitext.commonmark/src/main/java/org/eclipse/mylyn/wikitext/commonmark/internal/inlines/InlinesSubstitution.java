/*******************************************************************************
 * Copyright (c) 2015 David Green.
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

package org.eclipse.mylyn.wikitext.commonmark.internal.inlines;

import static java.util.Objects.requireNonNull;

import java.util.List;

import com.google.common.collect.ImmutableList;

class InlinesSubstitution {

	private final Inline first;

	private final Inline last;

	private final List<Inline> substitution;

	public InlinesSubstitution(Inline first, Inline last, List<Inline> substitution) {
		this.first = requireNonNull(first);
		this.last = requireNonNull(last);
		this.substitution = ImmutableList.copyOf(substitution);
	}

	public List<Inline> apply(List<Inline> inlines) {
		ImmutableList.Builder<Inline> builder = ImmutableList.builder();

		boolean inReplacementSegment = false;
		for (Inline inline : inlines) {
			if (inline == first) {
				inReplacementSegment = true;
				builder.addAll(substitution);
			}
			if (!inReplacementSegment) {
				builder.add(inline);
			}
			if (inReplacementSegment && inline == last) {
				inReplacementSegment = false;
			}
		}
		return builder.build();
	}
}
