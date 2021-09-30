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

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

class LinesIterable implements Iterable<Line> {

	class LinesIterator implements Iterator<Line> {

		@Override
		public boolean hasNext() {
			Line currentLine = lineSequence.getCurrentLine();
			return currentLine != null && predicate.test(currentLine);
		}

		@Override
		public Line next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			Line line = lineSequence.getCurrentLine();
			lineSequence.advance();
			return line;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	private final LineSequence lineSequence;

	private final Predicate<Line> predicate;

	public LinesIterable(LineSequence lineSequence, Predicate<Line> predicate) {
		this.lineSequence = requireNonNull(lineSequence);
		this.predicate = requireNonNull(predicate);
	}

	@Override
	public Iterator<Line> iterator() {
		return new LinesIterator();
	}

}
