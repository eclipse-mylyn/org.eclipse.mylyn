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
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.commonmark.internal.inlines;

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
