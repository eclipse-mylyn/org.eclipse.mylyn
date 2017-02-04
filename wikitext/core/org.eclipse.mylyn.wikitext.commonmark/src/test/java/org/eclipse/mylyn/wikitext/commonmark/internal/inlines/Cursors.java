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

package org.eclipse.mylyn.wikitext.commonmark.internal.inlines;

import org.eclipse.mylyn.wikitext.commonmark.internal.Line;
import org.eclipse.mylyn.wikitext.commonmark.internal.LineSequence;
import org.eclipse.mylyn.wikitext.commonmark.internal.TextSegment;

import com.google.common.base.Predicates;

public class Cursors {

	public static Cursor createCursor(String content, int advanceCount) {
		Cursor cursor = createCursor(content);
		cursor.advance(advanceCount);
		return cursor;
	}

	public static Cursor createCursor(String content) {
		return new Cursor(new TextSegment(LineSequence.create(content).with(Predicates.<Line> alwaysTrue())));
	}

	private Cursors() {
		// prevent instantiation
	}
}
