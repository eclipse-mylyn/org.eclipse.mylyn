/*******************************************************************************
 * Copyright (c) 2015, 2021 David Green.
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

import java.util.function.Function;

class TransformLineSequence extends LineSequence {

	private final LineSequence delegate;

	private final Function<Line, Line> transform;

	public TransformLineSequence(LineSequence delegate, Function<Line, Line> transform) {
		this.delegate = requireNonNull(delegate);
		this.transform = requireNonNull(transform);
	}

	@Override
	public Line getCurrentLine() {
		Line line = delegate.getCurrentLine();
		if (line != null) {
			line = transform.apply(line);
		}
		return line;
	}

	@Override
	public Line getNextLine() {
		Line line = delegate.getNextLine();
		if (line != null) {
			line = transform.apply(line);
		}
		return line;
	}

	@Override
	public void advance() {
		delegate.advance();
	}

	@Override
	public LineSequence lookAhead() {
		return new TransformLineSequence(delegate.lookAhead(), transform);
	}

}
