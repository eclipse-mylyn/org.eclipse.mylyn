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

package org.eclipse.mylyn.internal.wikitext.commonmark;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.google.common.base.Predicate;

class LinesIterable implements Iterable<Line> {

	class LinesIterator implements Iterator<Line> {

		@Override
		public boolean hasNext() {
			Line currentLine = lineSequence.getCurrentLine();
			return currentLine != null && predicate.apply(currentLine);
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
		this.lineSequence = checkNotNull(lineSequence);
		this.predicate = checkNotNull(predicate);
	}

	@Override
	public Iterator<Line> iterator() {
		return new LinesIterator();
	}

}
