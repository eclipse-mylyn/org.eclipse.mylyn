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

package org.eclipse.mylyn.wikitext.commonmark.internal.inlines;

import org.eclipse.mylyn.wikitext.commonmark.internal.LineSequence;
import org.eclipse.mylyn.wikitext.commonmark.internal.TextSegment;

public class Cursors {

	public static Cursor createCursor(String content, int advanceCount) {
		Cursor cursor = createCursor(content);
		cursor.advance(advanceCount);
		return cursor;
	}

	public static Cursor createCursor(String content) {
		return new Cursor(new TextSegment(LineSequence.create(content).with(x -> true)));
	}

	private Cursors() {
		// prevent instantiation
	}
}
