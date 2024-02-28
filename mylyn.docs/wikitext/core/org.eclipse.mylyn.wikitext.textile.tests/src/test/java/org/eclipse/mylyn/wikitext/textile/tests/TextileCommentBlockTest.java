/*******************************************************************************
 * Copyright (c) 2007, 2024 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.textile.tests;

import org.eclipse.mylyn.wikitext.textile.TextileLanguage;
import org.eclipse.mylyn.wikitext.toolkit.AbstractMarkupGenerationTest;
import org.junit.Test;

@SuppressWarnings("nls")
public class TextileCommentBlockTest extends AbstractMarkupGenerationTest<TextileLanguage> {

	@Test
	public void testSimpleComment() {
		assertMarkup("<p>one two</p><p>three</p>", "one two\n\n###. comment\n\nthree");
	}

	@Test
	public void testMultiLineComment() {
		assertMarkup("<p>one two</p><p>three</p>", "one two\n\n###. comment\nline two\nline three\n\nthree");
	}

	@Test
	public void testNotAComment() {
		assertMarkup("<p>one two</p><p>###.nocomment</p><p>three</p>", "one two\n\n###.nocomment\n\nthree");
	}

	@Test
	public void testNotAComment2() {
		assertMarkup("<p>one two<br/>###.nocomment</p><p>three</p>", "one two\n###.nocomment\n\nthree");
	}

	@Test
	public void testExtendedComment() {
		assertMarkup("<p>para</p>", "###.. extended comment\n\n\nwith more\n\np. para");
	}

	@Override
	protected TextileLanguage createMarkupLanguage() {
		return new TextileLanguage();
	}
}
