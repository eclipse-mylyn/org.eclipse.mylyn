/*******************************************************************************
 * Copyright (c) 2013, 2014 Stefan Seelmann and others.
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

import org.eclipse.mylyn.internal.wikitext.markdown.validation.MarkdownReferenceValidationRule;
import org.eclipse.mylyn.wikitext.validation.MarkupValidator;
import org.eclipse.mylyn.wikitext.validation.ValidationProblem;
import org.eclipse.mylyn.wikitext.validation.ValidationProblem.Severity;

import junit.framework.TestCase;

public class MarkdownReferenceValidationRuleTest extends TestCase {

	private MarkdownReferenceValidationRule rule;

	private MarkupValidator validator;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		rule = new MarkdownReferenceValidationRule();
		validator = new MarkupValidator();
		validator.getRules().add(rule);
	}

	public void testNoErrorInLocalReferenceToExistingAnchor() {
		final String markup = "# Header 1\n\n[Link to title](#header-1)";
		List<ValidationProblem> problems = rule.findProblems(markup, 0, markup.length());
		
		assertNotNull(problems);
		assertTrue(problems.isEmpty());
	}

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
