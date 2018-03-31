/*******************************************************************************
 * Copyright (c) 2007, 2013 David Green and others.
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

package org.eclipse.mylyn.wikitext.confluence;

import org.eclipse.mylyn.wikitext.toolkit.AbstractMarkupGenerationTest;
import org.junit.Test;

public class ConfluenceLineBreakTest extends AbstractMarkupGenerationTest<ConfluenceLanguage> {

	@Override
	protected ConfluenceLanguage createMarkupLanguage() {
		return new ConfluenceLanguage();
	}

	@Test
	public void testLineBreak() {
		assertMarkup("<p>a paragraph with an arbitrary<br/>line break</p>",
				"a paragraph with an arbitrary\\\\line break");
	}

	@Test
	public void withSpace() {
		assertMarkup("<p>a paragraph with an arbitrary<br/><br/>line break</p>",
				"a paragraph with an arbitrary\\\\ \\\\line break");
	}

	@Test
	public void multipleConsecutive() {
		assertMarkup("<p>a<br/><br/><br/>b</p>", "a\\\\\\\\\\\\b");
	}

	@Test
	public void lineStart() {
		assertMarkup("<p><br/>b</p>", "\\\\b");
	}

	@Test
	public void lineEnd() {
		assertMarkup("<p>b<br/></p>", "b\\\\");
	}

	@Test
	public void onLine() {
		assertMarkup("<p>a<br/>b</p>", "a\nb");
	}

	@Test
	public void listItem() {
		assertMarkup("<ul><li>one<br/>two</li><li>three</li></ul>", "* one\ntwo\n* three");
	}

	@Test
	public void listItemMultipleConsecutiveBreaks() {
		assertMarkup("<ul><li>one<br/><br/><br/>two</li><li>three</li></ul>", "* one\n\\\\\\\\two\n* three");
	}
}
