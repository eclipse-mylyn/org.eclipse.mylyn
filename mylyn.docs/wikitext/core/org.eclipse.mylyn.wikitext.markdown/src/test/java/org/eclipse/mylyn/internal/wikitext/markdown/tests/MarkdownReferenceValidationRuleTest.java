/*******************************************************************************
 * Copyright (c) 2013, 2021 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.markdown.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.mylyn.wikitext.markdown.internal.validation.MarkdownReferenceValidationRule;
import org.eclipse.mylyn.wikitext.validation.MarkupValidator;
import org.eclipse.mylyn.wikitext.validation.ValidationProblem;
import org.eclipse.mylyn.wikitext.validation.ValidationProblem.Severity;
import org.junit.Before;
import org.junit.Test;

public class MarkdownReferenceValidationRuleTest {

	private MarkdownReferenceValidationRule rule;

	private MarkupValidator validator;

	@Before
	public void setUp() {
		rule = new MarkdownReferenceValidationRule();
		validator = new MarkupValidator();
		validator.getRules().add(rule);
	}

	@Test
	public void testNoErrorInLocalReferenceToExistingAnchor() {
		final String markup = "# Header 1\n\n[Link to title](#header-1)";
		List<ValidationProblem> problems = rule.findProblems(markup, 0, markup.length());

		assertNotNull(problems);
		assertTrue(problems.isEmpty());
	}

	@Test
	public void testErrorInLocalReferenceToNonExistingAnchor() {
		final String markup = "# Header 1\n\n[Link to title](#FooBar)";
		List<ValidationProblem> problems = rule.findProblems(markup, 0, markup.length());

		assertNotNull(problems);
		assertEquals(1, problems.size());
		assertEquals(12, problems.get(0).getOffset());
		assertEquals(24, problems.get(0).getLength());
		assertEquals(Severity.ERROR, problems.get(0).getSeverity());
		assertTrue(problems.get(0).getMessage().contains("FooBar"));
	}

}
