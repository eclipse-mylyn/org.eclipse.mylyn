/*******************************************************************************
 * Copyright (c) 2015, 2024 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     Alexander Fedorov (ArSysOp) - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.commonmark.internal;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkArgument;

import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class LineSequence implements Iterable<Line> {

	static abstract class ForwardLineSequence extends LineSequence {

		abstract Line getNextLine(int i);
	}

	public static LineSequence create(String content) {
		return new ContentLineSequence(content);
	}

	public abstract Line getCurrentLine();

	public abstract Line getNextLine();

	public abstract void advance();

	public void advance(int count) {
		checkArgument(count >= 0);
		for (int x = 0; x < count; ++x) {
			advance();
		}
	}

	@Override
	public Iterator<Line> iterator() {
		Predicate<Line> predicate = x -> true;
		return iterator(predicate);
	}

	private Iterator<Line> iterator(Predicate<Line> predicate) {
		return new LinesIterable(this, predicate).iterator();
	}

	public LineSequence with(Predicate<Line> predicate) {
		return new PredicateLineSequence(this, predicate);
	}

	public LineSequence transform(Function<Line, Line> transform) {
		return new TransformLineSequence(this, transform);
	}

	public abstract LineSequence lookAhead();

	@Override
	public String toString() {
		return toStringHelper(LineSequence.class).add("currentLine", getCurrentLine()) //$NON-NLS-1$
				.add("nextLine", getNextLine()) //$NON-NLS-1$
				.toString();
	}
}
