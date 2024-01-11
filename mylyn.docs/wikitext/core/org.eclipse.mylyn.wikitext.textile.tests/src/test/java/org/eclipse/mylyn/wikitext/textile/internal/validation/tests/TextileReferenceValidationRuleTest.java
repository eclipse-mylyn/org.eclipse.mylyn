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

package org.eclipse.mylyn.wikitext.textile.internal.validation.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.mylyn.wikitext.textile.internal.validation.TextileReferenceValidationRule;
import org.eclipse.mylyn.wikitext.validation.MarkupValidator;
import org.eclipse.mylyn.wikitext.validation.ValidationProblem;
import org.junit.Before;
import org.junit.Test;

public class TextileReferenceValidationRuleTest {

	private TextileReferenceValidationRule rule;

	private MarkupValidator validator;

	@Before
	public void setUp() throws Exception {
		rule = new TextileReferenceValidationRule();

		validator = new MarkupValidator();
		validator.getRules().add(rule);
	}

	@Test
	public void testNoErrors() {
		final String markup = "h1. Title\n\n\"a link\":#Title";
		List<ValidationProblem> problems = rule.findProblems(markup, 0, markup.length());

		assertNotNull(problems);
		assertTrue(problems.isEmpty());
	}

	@Test
	public void testErrors() {
		final String markup = "h1. Title\n\nsome text \"a link\":#BADTitle more text";
		List<ValidationProblem> problems = rule.findProblems(markup, 0, markup.length());

		assertNotNull(problems);
		assertEquals(1, problems.size());
		assertEquals(21, problems.get(0).getOffset());
		assertEquals(18, problems.get(0).getLength());
	}

	@Test
	public void testFootnoteReference() {
		String markup = "some text with a footnote reference[1]\n\nfn1. a footnote";
		List<ValidationProblem> problems = rule.findProblems(markup, 0, markup.length());

		assertNotNull(problems);
		assertEquals(0, problems.size());
	}

	@Test
	public void testFootnoteReferenceWithErrors() {
		String markup = "some text with a footnote reference[1]\n\nfn2. a footnote";
		List<ValidationProblem> problems = rule.findProblems(markup, 0, markup.length());

		assertNotNull(problems);
		assertEquals(1, problems.size());
		assertEquals(35, problems.get(0).getOffset());
		assertEquals(3, problems.get(0).getLength());
	}
}
