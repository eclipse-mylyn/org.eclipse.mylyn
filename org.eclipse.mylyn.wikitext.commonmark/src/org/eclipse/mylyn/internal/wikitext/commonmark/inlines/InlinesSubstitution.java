/*******************************************************************************
 * Copyright (c) 2015 David Green.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.commonmark.inlines;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.google.common.collect.ImmutableList;

class InlinesSubstitution {

	private final Inline first;

	private final Inline last;

	private final List<Inline> substitution;

	public InlinesSubstitution(Inline first, Inline last, List<Inline> substitution) {
		this.first = checkNotNull(first);
		this.last = checkNotNull(last);
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
