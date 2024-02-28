/*******************************************************************************
 * Copyright (c) 2013, 2024 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.markdown.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.mylyn.wikitext.markdown.internal.validation.LinkDefinitionValidationRule;
import org.eclipse.mylyn.wikitext.validation.MarkupValidator;
import org.eclipse.mylyn.wikitext.validation.ValidationProblem;
import org.eclipse.mylyn.wikitext.validation.ValidationProblem.Severity;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("nls")
public class LinkDefinitionValidationRuleTest {

	private LinkDefinitionValidationRule rule;

	private MarkupValidator validator;

	@Before
	public void setUp() {
		rule = new LinkDefinitionValidationRule();
		validator = new MarkupValidator();
		validator.getRules().add(rule);
	}

	@Test
	public void testNoProblemIfLinkDefinitionExistsWithExplicitLinkName() {
		final String markup = "[Google][Google]\n\n[Google]: http://www.google.com/";
		List<ValidationProblem> problems = rule.findProblems(markup, 0, markup.length());

		assertNotNull(problems);
		assertTrue(problems.isEmpty());
	}

	@Test
	public void testNoProblemIfLinkDefinitionExistsWithImplicitLinkName() {
		final String markup = "[Bing][]\n\n[Bing]: http://www.bing.com/";
		List<ValidationProblem> problems = rule.findProblems(markup, 0, markup.length());

		assertNotNull(problems);
		assertTrue(problems.isEmpty());
	}

	@Test
	public void testErrorIfLinkDefinitionDoesNotExist() {
		final String markup = "[Yahoo][yahoo]";
		List<ValidationProblem> problems = rule.findProblems(markup, 0, markup.length());

		assertNotNull(problems);
		assertEquals(1, problems.size());
		assertEquals(0, problems.get(0).getOffset());
		assertEquals(14, problems.get(0).getLength());
		assertEquals(Severity.ERROR, problems.get(0).getSeverity());
		assertTrue(problems.get(0).getMessage().contains("yahoo"));
		assertTrue(problems.get(0).getMessage().contains("missing"));
	}

	@Test
	public void testWarningIfLinkDefinitionIsUnused() {
		final String markup = "[ddg]: https://duckduckgo.com/";
		List<ValidationProblem> problems = rule.findProblems(markup, 0, markup.length());

		assertNotNull(problems);
		assertEquals(1, problems.size());
		assertEquals(0, problems.get(0).getOffset());
		assertEquals(30, problems.get(0).getLength());
		assertEquals(Severity.WARNING, problems.get(0).getSeverity());
		assertTrue(problems.get(0).getMessage().contains("ddg"));
		assertTrue(problems.get(0).getMessage().contains("never used"));
	}

	@Test
	public void testWarningIfLinkDefinitionWithLeadingSpacesIsUnused() {
		final String markup = "  [ddg]: https://duckduckgo.com/";
		List<ValidationProblem> problems = rule.findProblems(markup, 0, markup.length());

		assertNotNull(problems);
		assertEquals(1, problems.size());
		assertEquals(2, problems.get(0).getOffset());
		assertEquals(30, problems.get(0).getLength());
		assertEquals(Severity.WARNING, problems.get(0).getSeverity());
		assertTrue(problems.get(0).getMessage().contains("ddg"));
		assertTrue(problems.get(0).getMessage().contains("never used"));
	}

	@Test
	public void testErrorIfLinkDefinitionDoesNotExistWithinHeading() {
		final String markup = "# Heading with [Link][link]";
		List<ValidationProblem> problems = rule.findProblems(markup, 0, markup.length());

		assertNotNull(problems);
		assertEquals(1, problems.size());
		assertEquals(15, problems.get(0).getOffset());
		assertEquals(12, problems.get(0).getLength());
		assertEquals(Severity.ERROR, problems.get(0).getSeverity());
		assertTrue(problems.get(0).getMessage().contains("link"));
		assertTrue(problems.get(0).getMessage().contains("missing"));
	}

	@Test
	public void testErrorIfLinkDefinitionDoesNotExistWithinUnderlinedHeading() {
		final String markup = "Heading with [Link][link]\n=============";
		List<ValidationProblem> problems = rule.findProblems(markup, 0, markup.length());

		assertNotNull(problems);
		assertEquals(1, problems.size());
		assertEquals(13, problems.get(0).getOffset());
		assertEquals(12, problems.get(0).getLength());
		assertEquals(Severity.ERROR, problems.get(0).getSeverity());
		assertTrue(problems.get(0).getMessage().contains("link"));
		assertTrue(problems.get(0).getMessage().contains("missing"));
	}

	@Test
	public void testErrorIfLinkDefinitionDoesNotExistWithinBlockquote() {
		final String markup = "> [Link][link] within blockquote";
		List<ValidationProblem> problems = rule.findProblems(markup, 0, markup.length());

		assertNotNull(problems);
		assertEquals(1, problems.size());
		assertEquals(2, problems.get(0).getOffset());
		assertEquals(12, problems.get(0).getLength());
		assertEquals(Severity.ERROR, problems.get(0).getSeverity());
		assertTrue(problems.get(0).getMessage().contains("link"));
		assertTrue(problems.get(0).getMessage().contains("missing"));
	}

}
