/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.twiki.core.validation;

import junit.framework.TestCase;

import org.eclipse.mylyn.wikitext.core.validation.ValidationProblem;

public class ListWhitespaceValidationRuleTest extends TestCase {

	private ListWhitespaceValidationRule rule;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		rule = new ListWhitespaceValidationRule();
	}

	public void testNegativeMatch() {
		String markup = "some text\n\n   * a valid list item\n      * another valid list item\n\nmore text";
		ValidationProblem problem = rule.findProblem(markup, 0, markup.length());
		assertNull(problem);
	}

	public void testPositiveMatchSecondItemNotMultipleOf3() {
		String markup = "some text\n\n   * a valid list item\n     * not a list item\n\nmore text";
		ValidationProblem problem = rule.findProblem(markup, 0, markup.length());
		assertNotNull(problem);
		assertEquals(34, problem.getOffset());
	}

	public void testPositiveMatchSecondItemTab() {
		String markup = "some text\n\n   * a valid list item\n \t    * not a list item\n\nmore text";
		ValidationProblem problem = rule.findProblem(markup, 0, markup.length());
		assertNotNull(problem);
		assertEquals(34, problem.getOffset());
	}

	public void testPositiveMatchFirstItemNotMultipleOf3() {
		String markup = "some text\n\n  * a bad list item\n      * not a list item\n\nmore text";
		ValidationProblem problem = rule.findProblem(markup, 0, markup.length());
		assertNotNull(problem);
		assertEquals(11, problem.getOffset());
	}

	public void testPositiveMatchStartOfLine() {
		String markup = "some text\n\n* a bad list item";
		ValidationProblem problem = rule.findProblem(markup, 0, markup.length());
		assertNotNull(problem);
		assertEquals(11, problem.getOffset());
	}

	public void testPositiveMatchStartOfInput() {
		String markup = "* a bad list item";
		ValidationProblem problem = rule.findProblem(markup, 0, markup.length());
		assertNotNull(problem);
		assertEquals(0, problem.getOffset());
	}

	public void testPositiveMatchNoFollowingWhitespace() {
		String markup = "   *a bad list item";
		ValidationProblem problem = rule.findProblem(markup, 0, markup.length());
		assertNotNull(problem);
		assertEquals(0, problem.getOffset());
	}

	public void testNegativeMatchNumeric() {
		String markup = "some text\n\n   1. a valid list item\n      1. another valid list item\n\nmore text";
		ValidationProblem problem = rule.findProblem(markup, 0, markup.length());
		assertNull(problem);
	}

	public void testPositiveMatchNumeric() {
		String markup = "some text\n\n  1. a bad list item\n      1. another valid list item\n\nmore text";
		ValidationProblem problem = rule.findProblem(markup, 0, markup.length());
		assertNotNull(problem);
		assertEquals(11, problem.getOffset());
	}

}
