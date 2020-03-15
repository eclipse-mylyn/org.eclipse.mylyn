/*******************************************************************************
 * Copyright (c) 2007, 2011 David Green and others.
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

package org.eclipse.mylyn.wikitext.mediawiki.internal.validation;

import org.eclipse.mylyn.wikitext.validation.ValidationProblem;

import junit.framework.TestCase;

/**
 * @author David Green
 */
public class CommentValidationRuleTest extends TestCase {

	private CommentValidationRule rule;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		rule = new CommentValidationRule();
	}

	public void testOk() {
		String markup = "a <!-- valid comment -->";
		ValidationProblem problem = rule.findProblem(markup, 0, markup.length());
		assertNull(problem);
	}

	public void testFail1() {
		String markup = "a <!--- bogus comment -->";
		ValidationProblem problem = rule.findProblem(markup, 0, markup.length());
		assertNotNull(problem);
		assertEquals(2, problem.getOffset());
		assertEquals(5, problem.getLength());
	}

	public void testFail2() {
		String markup = "a <!--- bogus comment ----->";
		ValidationProblem problem = rule.findProblem(markup, 0, markup.length());
		assertNotNull(problem);
		int offset = problem.getOffset() + problem.getLength();
		problem = rule.findProblem(markup, offset, markup.length() - offset);
		assertNotNull(problem);
		assertEquals(22, problem.getOffset());
		assertEquals(6, problem.getLength());
	}
}
