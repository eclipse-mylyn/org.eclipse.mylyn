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

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.mylyn.wikitext.commonmark.internal.LineSequence.ForwardLineSequence;
import org.eclipse.mylyn.wikitext.util.LocationTrackingReader;

class ContentLineSequence extends ForwardLineSequence {

	private final LocationTrackingReader reader;

	private Line currentLine;

	private final List<Line> followingLines = new ArrayList<Line>();

	ContentLineSequence(String content) {
		this.reader = new LocationTrackingReader(new StringReader(Objects.requireNonNull(content)));
		currentLine = readLine();
	}

	private Line readLine() {
		String text;
		try {
			text = reader.readLine();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		if (text == null) {
			return null;
		}
		return new Line(reader.getLineNumber(), reader.getLineOffset(), text);
	}

	@Override
	public Line getCurrentLine() {
		return currentLine;
	}

	@Override
	public Line getNextLine() {
		return getNextLine(0);
	}

	@Override
	Line getNextLine(int index) {
		checkArgument(index >= 0);
		while (followingLines.size() <= index) {
			Line line = readLine();
			if (line == null) {
				break;
			}
			followingLines.add(line);
		}
		if (followingLines.size() <= index) {
			return null;
		}
		return followingLines.get(index);
	}

	@Override
	public void advance() {
		currentLine = getNextLine();
		if (currentLine != null) {
			followingLines.remove(0);
		}
	}

	@Override
	public LineSequence lookAhead() {
		return new LookAheadLineSequence(this);
	}
}
