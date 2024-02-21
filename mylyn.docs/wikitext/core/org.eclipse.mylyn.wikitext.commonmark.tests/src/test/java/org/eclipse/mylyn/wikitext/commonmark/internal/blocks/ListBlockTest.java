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

package org.eclipse.mylyn.wikitext.commonmark.internal.blocks;

import static org.eclipse.mylyn.wikitext.commonmark.internal.CommonMarkAsserts.assertContent;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.mylyn.wikitext.commonmark.internal.LineSequence;
import org.junit.Test;

@SuppressWarnings("nls")
public class ListBlockTest {

	@Test
	public void canStartBulleted() {
		assertCanStart(true, "-");
		assertCanStart(true, "- ");
		assertCanStart(true, "- test");
		assertCanStart(true, " - test");
		assertCanStart(true, "  - test");
		assertCanStart(true, "   - test");
		assertCanStart(false, "    - test");
		assertCanStart(true, "* test");
		assertCanStart(true, "+ test");
		assertCanStart(false, "x test");
	}

	@Test
	public void canStartOrdered() {
		assertCanStart(true, "1.");
		assertCanStart(true, "1. ");
		assertCanStart(true, "1. test");
		assertCanStart(true, " 1. test");
		assertCanStart(true, "  1. test");
		assertCanStart(true, "   1. test");
		assertCanStart(false, "    1. test");
		assertCanStart(true, "2. test");
		assertCanStart(true, "3. test");
		assertCanStart(true, "1) test");
		assertCanStart(true, "2) test");
		assertCanStart(true, "23) test");
		assertCanStart(true, "23. test");
		assertCanStart(false, "a. test");
	}

	@Test
	public void simpleList() {
		assertSimpleBulletedList("*");
		assertSimpleBulletedList("-");
		assertSimpleBulletedList("+");
	}

	@Test
	public void listWithNestedBlocks() {
		assertBulletedListWithNestedBlocks("*");
		assertBulletedListWithNestedBlocks("-");
		assertBulletedListWithNestedBlocks("+");
	}

	@Test
	public void tightListWithSublist() {
		List<String> lines = Arrays.asList("* one", "* two", "    * three", "* four");
		assertContent("<ul><li>one</li><li>two<ul><li>three</li></ul></li><li>four</li></ul>",
				lines.stream().collect(Collectors.joining("\n")));
	}

	@Test
	public void simpleOrderedList() {
		assertSimpleOrderedList(".");
		assertSimpleOrderedList(")");
	}

	@Test
	public void simpleOrderedListWithNestedBlocks() {
		assertOrderedListWithNestedBlocks(".");
		assertOrderedListWithNestedBlocks(")");
	}

	@Test
	public void listWithFencedCodeBlock() {
		assertContent("<ul><li><pre><code>a\n\n\nb\n</code></pre></li></ul>", "* ```\n  a\n\n\n  b\n  ```");
	}

	@Test
	public void terminatesWithDoubleBlankLine() {
		List<String> lines = Arrays.asList("* one", "* two", "", "", "* three");
		assertContent("<ul><li>one</li><li>two</li></ul><ul><li>three</li></ul>",
				lines.stream().collect(Collectors.joining("\n")));
	}

	@Test
	public void orderedListWithStart() {
		List<String> lines = Arrays.asList("3. one", "4. two");
		assertContent("<ol start=\"3\"><li>one</li><li>two</li></ol>",
				lines.stream().collect(Collectors.joining("\n")));
	}

	@Test
	public void doubleBlankLineStartingOnListItem() {
		List<String> lines = Arrays.asList("* one", "*", "", "* three");
		assertContent("<ul><li><p>one</p></li><li></li><li><p>three</p></li></ul>",
				lines.stream().collect(Collectors.joining("\n")));
	}

	private void assertSimpleOrderedList(String delimiter) {
		List<String> lines = Arrays.asList("1" + delimiter + " one", "2" + delimiter + " two",
				"3" + delimiter + " three four");
		assertContent("<ol><li>one</li><li>two</li><li>three four</li></ol>",
				lines.stream().collect(Collectors.joining("\n")));
	}

	private void assertBulletedListWithNestedBlocks(String delimiter) {
		List<String> lines = Arrays.asList(delimiter + " one", delimiter + " two\n  two.2\n\n  two.3",
				delimiter + " three four");
		assertContent("<ul><li><p>one</p></li><li><p>two two.2</p><p>two.3</p></li><li><p>three four</p></li></ul>",
				lines.stream().collect(Collectors.joining("\n")));
	}

	private void assertOrderedListWithNestedBlocks(String delimiter) {
		List<String> lines = Arrays.asList("1" + delimiter + " one", "2" + delimiter + " two\n   two.2\n\n   two.3",
				"3" + delimiter + " three four");
		assertContent("<ol><li><p>one</p></li><li><p>two two.2</p><p>two.3</p></li><li><p>three four</p></li></ol>",
				lines.stream().collect(Collectors.joining("\n")));
	}

	private void assertSimpleBulletedList(String delimiter) {
		List<String> lines = Arrays.asList(delimiter + " one", delimiter + " two", delimiter + " three four");
		assertContent("<ul><li>one</li><li>two</li><li>three four</li></ul>",
				lines.stream().collect(Collectors.joining("\n")));
	}

	private void assertCanStart(boolean expected, String string) {
		assertEquals(expected, new ListBlock().canStart(LineSequence.create(string)));
	}
}
