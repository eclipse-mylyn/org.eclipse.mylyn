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

package org.eclipse.mylyn.internal.wikitext.commonmark.inlines;

import org.junit.Test;

public class PotentialBracketSpanTest extends AbstractSourceSpanTest {

	public PotentialBracketSpanTest() {
		super(new PotentialBracketSpan());
	}

	@Test
	public void bracketsNoMarkup() {
		assertParseToHtml("[test] nothing here", "[test] nothing here");
	}

	@Test
	public void link() {
		assertParseToHtml("one <a href=\"/four\" title=\"five six\">two three</a> seven\neight",
				"one [two three](/four \"five six\") seven\neight");
		assertParseToHtml("<a href=\"/four\">two three</a>", "[two three](/four )");
		assertParseToHtml("<a href=\"/four\"></a>", "[](/four )");
		assertParseToHtml("<a href=\"\">test</a>", "[test]()");
		assertParseToHtml("<a href=\"foo(and(bar))\">link</a>", "[link](foo(and\\(bar\\)))");
	}

	@Test
	public void image() {
		assertParseToHtml("<img src=\"/url\" alt=\"foo\" title=\"title\"/>", "![foo](/url \"title\")");
		assertParseToHtml("<img src=\"/url\" alt=\"foo\" title=\"\"/>", "![foo](/url)");
		assertParseToHtml("<img src=\"/url.png\" alt=\"\" title=\"\"/>", "![](/url.png)");
	}
}
