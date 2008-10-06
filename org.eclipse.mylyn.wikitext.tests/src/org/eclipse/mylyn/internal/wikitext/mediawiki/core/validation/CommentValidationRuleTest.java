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

package org.eclipse.mylyn.internal.wikitext.mediawiki.core.validation;

import junit.framework.TestCase;

import org.eclipse.mylyn.wikitext.core.validation.ValidationProblem;

/**
 * 
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
