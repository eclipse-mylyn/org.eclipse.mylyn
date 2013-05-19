/*******************************************************************************
 * Copyright (c) 2013 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.markdown.tests;

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.wikitext.markdown.core.validation.LinkDefinitionValidationRule;
import org.eclipse.mylyn.wikitext.core.validation.MarkupValidator;
import org.eclipse.mylyn.wikitext.core.validation.ValidationProblem;
import org.eclipse.mylyn.wikitext.core.validation.ValidationProblem.Severity;
import org.eclipse.mylyn.wikitext.tests.TestUtil;

public class LinkDefinitionValidationRuleTest extends TestCase {

	private LinkDefinitionValidationRule rule;

	private MarkupValidator validator;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		rule = new LinkDefinitionValidationRule();
		validator = new MarkupValidator();
		validator.getRules().add(rule);
	}

	public void testNoProblemIfLinkDefinitionExistsWithExplicitLinkName() {
		final String markup = "[Google][Google]\n\n[Google]: http://www.google.com/";
		List<ValidationProblem> problems = rule.findProblems(markup, 0, markup.length());
		TestUtil.println(problems);
		assertNotNull(problems);
		assertTrue(problems.isEmpty());
	}

	public void testNoProblemIfLinkDefinitionExistsWithImplicitLinkName() {
		final String markup = "[Bing][]\n\n[Bing]: http://www.bing.com/";
		List<ValidationProblem> problems = rule.findProblems(markup, 0, markup.length());
		TestUtil.println(problems);
		assertNotNull(problems);
		assertTrue(problems.isEmpty());
	}

	public void testErrorIfLinkDefinitionDoesNotExist() {
		final String markup = "[Yahoo][yahoo]";
		List<ValidationProblem> problems = rule.findProblems(markup, 0, markup.length());
		TestUtil.println(problems);
		assertNotNull(problems);
		assertEquals(1, problems.size());
		assertEquals(0, problems.get(0).getOffset());
		assertEquals(14, problems.get(0).getLength());
		assertEquals(Severity.ERROR, problems.get(0).getSeverity());
		assertTrue(problems.get(0).getMessage().contains("yahoo"));
		assertTrue(problems.get(0).getMessage().contains("missing"));
	}

	public void testWarningIfLinkDefinitionIsUnused() {
		final String markup = "[ddg]: https://duckduckgo.com/";
		List<ValidationProblem> problems = rule.findProblems(markup, 0, markup.length());
		TestUtil.println(problems);
		assertNotNull(problems);
		assertEquals(1, problems.size());
		assertEquals(0, problems.get(0).getOffset());
		assertEquals(30, problems.get(0).getLength());
		assertEquals(Severity.WARNING, problems.get(0).getSeverity());
		assertTrue(problems.get(0).getMessage().contains("ddg"));
		assertTrue(problems.get(0).getMessage().contains("never used"));
	}

	public void testWarningIfLinkDefinitionWithLeadingSpacesIsUnused() {
		final String markup = "  [ddg]: https://duckduckgo.com/";
		List<ValidationProblem> problems = rule.findProblems(markup, 0, markup.length());
		TestUtil.println(problems);
		assertNotNull(problems);
		assertEquals(1, problems.size());
		assertEquals(2, problems.get(0).getOffset());
		assertEquals(30, problems.get(0).getLength());
		assertEquals(Severity.WARNING, problems.get(0).getSeverity());
		assertTrue(problems.get(0).getMessage().contains("ddg"));
		assertTrue(problems.get(0).getMessage().contains("never used"));
	}

	public void testErrorIfLinkDefinitionDoesNotExistWithinHeading() {
		final String markup = "# Heading with [Link][link]";
		List<ValidationProblem> problems = rule.findProblems(markup, 0, markup.length());
		TestUtil.println(problems);
		assertNotNull(problems);
		assertEquals(1, problems.size());
		assertEquals(15, problems.get(0).getOffset());
		assertEquals(12, problems.get(0).getLength());
		assertEquals(Severity.ERROR, problems.get(0).getSeverity());
		assertTrue(problems.get(0).getMessage().contains("link"));
		assertTrue(problems.get(0).getMessage().contains("missing"));
	}

	public void testErrorIfLinkDefinitionDoesNotExistWithinUnderlinedHeading() {
		final String markup = "Heading with [Link][link]\n=============";
		List<ValidationProblem> problems = rule.findProblems(markup, 0, markup.length());
		TestUtil.println(problems);
		assertNotNull(problems);
		assertEquals(1, problems.size());
		assertEquals(13, problems.get(0).getOffset());
		assertEquals(12, problems.get(0).getLength());
		assertEquals(Severity.ERROR, problems.get(0).getSeverity());
		assertTrue(problems.get(0).getMessage().contains("link"));
		assertTrue(problems.get(0).getMessage().contains("missing"));
	}

	public void testErrorIfLinkDefinitionDoesNotExistWithinBlockquote() {
		final String markup = "> [Link][link] within blockquote";
		List<ValidationProblem> problems = rule.findProblems(markup, 0, markup.length());
		TestUtil.println(problems);
		assertNotNull(problems);
		assertEquals(1, problems.size());
		assertEquals(2, problems.get(0).getOffset());
		assertEquals(12, problems.get(0).getLength());
		assertEquals(Severity.ERROR, problems.get(0).getSeverity());
		assertTrue(problems.get(0).getMessage().contains("link"));
		assertTrue(problems.get(0).getMessage().contains("missing"));
	}

}
