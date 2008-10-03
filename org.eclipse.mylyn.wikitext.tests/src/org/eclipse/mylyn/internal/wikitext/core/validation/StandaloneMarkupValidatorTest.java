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

package org.eclipse.mylyn.internal.wikitext.core.validation;

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.mylyn.wikitext.core.validation.ValidationProblem;
import org.eclipse.mylyn.wikitext.core.validation.ValidationProblem.Severity;

public class StandaloneMarkupValidatorTest extends TestCase {

	private StandaloneMarkupValidator validator;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		validator = new StandaloneMarkupValidator();
		validator.setClassLoader(StandaloneMarkupValidatorTest.class.getClassLoader());
		validator.computeRules("TestMarkupLanguage", StandaloneMarkupValidatorTest.class.getResource("test-plugin.xml"));
	}

	public void testSimple() {
		List<ValidationProblem> problems = validator.validate("some text ERROR more text WARNING and ERROR");

		assertNotNull(problems);
		assertEquals(3, problems.size());

		ValidationProblem first = problems.get(0);
		assertEquals(Severity.ERROR, first.getSeverity());
		assertEquals(10, first.getOffset());
		assertEquals(5, first.getLength());

		ValidationProblem second = problems.get(1);
		assertEquals(Severity.WARNING, second.getSeverity());
		assertEquals(26, second.getOffset());
		assertEquals(7, second.getLength());

		ValidationProblem third = problems.get(2);
		assertEquals(Severity.ERROR, third.getSeverity());
		assertEquals(38, third.getOffset());
		assertEquals(5, third.getLength());
	}

	public void testImmutability() {
		StandaloneMarkupValidator validator = StandaloneMarkupValidator.getValidator("Test");
		try {
			validator.getRules().clear();
			fail("not immutable");
		} catch (Exception e) {
			// expected
		}
		try {
			validator.getRules().add(new TestMarkupValidationRule());
			fail("not immutable");
		} catch (Exception e) {
			// expected
		}
		try {
			validator.setClassLoader(StandaloneMarkupValidator.class.getClassLoader());
			fail("not immutable");
		} catch (Exception e) {
			// expected
		}
	}

	public void testMutability() {
		validator.getRules().clear();
		validator.getRules().add(new TestMarkupValidationRule());
		validator.setClassLoader(StandaloneMarkupValidator.class.getClassLoader());
	}
}
