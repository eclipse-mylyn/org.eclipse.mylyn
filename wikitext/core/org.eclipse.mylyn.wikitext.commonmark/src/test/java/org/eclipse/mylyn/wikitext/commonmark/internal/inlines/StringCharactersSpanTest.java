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

import static org.eclipse.mylyn.wikitext.commonmark.internal.inlines.Cursors.createCursor;

import org.junit.Test;

public class StringCharactersSpanTest extends AbstractSourceSpanTest {

	public StringCharactersSpanTest() {
		super(new StringCharactersSpan());
	}

	@Test
	public void createInline() {
		assertNoInline(createCursor("``one"));
		assertNoInline(createCursor("__two"));
		assertNoInline(createCursor("***three"));
		assertNoInline(createCursor("   \nb"));
		assertInline(Characters.class, 0, 3, createCursor("one`"));
		assertInline(Characters.class, 0, 3, createCursor("one\ntwo"));
		assertInline(Characters.class, 0, 4, createCursor(" one *two"));
		assertInline(Characters.class, 0, 8, createCursor(" one two *three"));
		assertInline(Characters.class, 0, 8, createCursor(" one two \\[ab"));
		assertInline(Characters.class, 0, 8, createCursor(" one two !"));
		assertInline(Characters.class, 0, 8, createCursor(" one two <"));
	}
}
