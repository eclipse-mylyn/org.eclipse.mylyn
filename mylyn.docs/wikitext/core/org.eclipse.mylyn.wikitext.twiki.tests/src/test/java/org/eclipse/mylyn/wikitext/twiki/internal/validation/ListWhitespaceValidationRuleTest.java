/*******************************************************************************
 * Copyright (c) 2007, 2024 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.twiki.internal.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.mylyn.wikitext.validation.ValidationProblem;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings({ "nls", "restriction" })
public class ListWhitespaceValidationRuleTest {

	private ListWhitespaceValidationRule rule;

	@Before
	public void setUp() throws Exception {
		rule = new ListWhitespaceValidationRule();
	}

	@Test
	public void testNegativeMatch() {
		String markup = "some text\n\n   * a valid list item\n      * another valid list item\n\nmore text";
		ValidationProblem problem = rule.findProblem(markup, 0, markup.length());
		assertNull(problem);
	}

	@Test
	public void testPositiveMatchSecondItemNotMultipleOf3() {
		String markup = "some text\n\n   * a valid list item\n     * not a list item\n\nmore text";
		ValidationProblem problem = rule.findProblem(markup, 0, markup.length());
		assertNotNull(problem);
		assertEquals(34, problem.getOffset());
	}

	@Test
	public void testPositiveMatchSecondItemTab() {
		String markup = "some text\n\n   * a valid list item\n \t    * not a list item\n\nmore text";
		ValidationProblem problem = rule.findProblem(markup, 0, markup.length());
		assertNotNull(problem);
		assertEquals(34, problem.getOffset());
	}

	@Test
	public void testPositiveMatchFirstItemNotMultipleOf3() {
		String markup = "some text\n\n  * a bad list item\n      * not a list item\n\nmore text";
		ValidationProblem problem = rule.findProblem(markup, 0, markup.length());
		assertNotNull(problem);
		assertEquals(11, problem.getOffset());
	}

	@Test
	public void testPositiveMatchStartOfLine() {
		String markup = "some text\n\n* a bad list item";
		ValidationProblem problem = rule.findProblem(markup, 0, markup.length());
		assertNotNull(problem);
		assertEquals(11, problem.getOffset());
	}

	@Test
	public void testPositiveMatchStartOfInput() {
		String markup = "* a bad list item";
		ValidationProblem problem = rule.findProblem(markup, 0, markup.length());
		assertNotNull(problem);
		assertEquals(0, problem.getOffset());
	}

	@Test
	public void testPositiveMatchNoFollowingWhitespace() {
		String markup = "   *a bad list item";
		ValidationProblem problem = rule.findProblem(markup, 0, markup.length());
		assertNotNull(problem);
		assertEquals(0, problem.getOffset());
	}

	@Test
	public void testNegativeMatchNumeric() {
		String markup = "some text\n\n   1. a valid list item\n      1. another valid list item\n\nmore text";
		ValidationProblem problem = rule.findProblem(markup, 0, markup.length());
		assertNull(problem);
	}

	@Test
	public void testPositiveMatchNumeric() {
		String markup = "some text\n\n  1. a bad list item\n      1. another valid list item\n\nmore text";
		ValidationProblem problem = rule.findProblem(markup, 0, markup.length());
		assertNotNull(problem);
		assertEquals(11, problem.getOffset());
	}

}
