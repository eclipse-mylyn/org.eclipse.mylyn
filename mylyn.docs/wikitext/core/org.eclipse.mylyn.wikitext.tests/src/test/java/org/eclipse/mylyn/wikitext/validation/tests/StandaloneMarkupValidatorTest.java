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

package org.eclipse.mylyn.wikitext.validation.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.eclipse.mylyn.wikitext.validation.StandaloneMarkupValidator;
import org.eclipse.mylyn.wikitext.validation.ValidationProblem;
import org.eclipse.mylyn.wikitext.validation.ValidationProblem.Severity;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("nls")
public class StandaloneMarkupValidatorTest {

	private StandaloneMarkupValidator validator;

	@Before
	public void setUp() throws Exception {
		validator = new StandaloneMarkupValidator();
		validator.setClassLoader(StandaloneMarkupValidatorTest.class.getClassLoader());
		validator.computeRules("TestMarkupLanguage",
				StandaloneMarkupValidatorTest.class.getResource("test-plugin.xml"));
	}

	@Test
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

	@Test
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

	@Test
	public void testMutability() {
		validator.getRules().clear();
		validator.getRules().add(new TestMarkupValidationRule());
		validator.setClassLoader(StandaloneMarkupValidator.class.getClassLoader());
	}
}
