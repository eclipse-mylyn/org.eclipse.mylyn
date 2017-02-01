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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylyn.internal.wikitext.commonmark.LineSequence.ForwardLineSequence;
import org.eclipse.mylyn.wikitext.util.LocationTrackingReader;

import com.google.common.base.Throwables;

class ContentLineSequence extends ForwardLineSequence {

	private final LocationTrackingReader reader;

	private Line currentLine;

	private final List<Line> followingLines = new ArrayList<Line>();

	ContentLineSequence(String content) {
		this.reader = new LocationTrackingReader(new StringReader(checkNotNull(content)));
		currentLine = readLine();
	}

	private Line readLine() {
		String text;
		try {
			text = reader.readLine();
		} catch (IOException e) {
			throw Throwables.propagate(e);
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