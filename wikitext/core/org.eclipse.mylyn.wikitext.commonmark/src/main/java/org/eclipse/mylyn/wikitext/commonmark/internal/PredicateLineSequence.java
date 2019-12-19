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

package org.eclipse.mylyn.wikitext.commonmark.internal;

import static java.util.Objects.requireNonNull;

import com.google.common.base.Predicate;

class PredicateLineSequence extends LineSequence {

	private final LineSequence delegate;

	private final Predicate<Line> predicate;

	public PredicateLineSequence(LineSequence delegate, Predicate<Line> predicate) {
		this.delegate = requireNonNull(delegate);
		this.predicate = requireNonNull(predicate);
	}

	@Override
	public Line getCurrentLine() {
		return filter(delegate.getCurrentLine());
	}

	@Override
	public Line getNextLine() {
		return filter(delegate.getNextLine());
	}

	@Override
	public void advance() {
		if (getCurrentLine() != null) {
			delegate.advance();
		}
	}

	@Override
	public LineSequence lookAhead() {
		return new PredicateLineSequence(delegate.lookAhead(), predicate);
	}

	private Line filter(Line line) {
		if (line != null && predicate.apply(line)) {
			return line;
		}
		return null;
	}
}
