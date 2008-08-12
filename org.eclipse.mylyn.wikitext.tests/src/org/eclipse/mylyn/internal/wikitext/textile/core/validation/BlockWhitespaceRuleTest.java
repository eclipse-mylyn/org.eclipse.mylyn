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
package org.eclipse.mylyn.internal.wikitext.textile.core.validation;

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.wikitext.core.validation.MarkupValidator;
import org.eclipse.mylyn.wikitext.core.validation.ValidationProblem;

/**
 * 
 * 
 * @author David Green
 */
public class BlockWhitespaceRuleTest extends TestCase {

	private BlockWhitespaceRule rule;

	private MarkupValidator validator;

	@Override
	public void setUp() {
		rule = new BlockWhitespaceRule();

		validator = new MarkupValidator();
		validator.getRules().add(rule);
	}

	public void testNoMatch() {
		final String markup = "bc. \nfoo";
		ValidationProblem problem = rule.findProblem(markup, 0, markup.length());
		System.out.println(problem);
		assertNull(problem);
	}

	public void testNoMatch2() {
		final String markup = "\nabc.\nfoo";
		ValidationProblem problem = rule.findProblem(markup, 0, markup.length());
		System.out.println(problem);
		assertNull(problem);
	}

	public void testMatch() {
		final String markup = "bc.\nfoo";
		ValidationProblem problem = rule.findProblem(markup, 0, markup.length());
		System.out.println(problem);
		assertNotNull(problem);
		assertEquals(0, problem.getOffset());
	}

	public void testMatch2() {
		final String markup = "\nbc.\nfoo";
		ValidationProblem problem = rule.findProblem(markup, 0, markup.length());
		System.out.println(problem);
		assertNotNull(problem);
		assertEquals(1, problem.getOffset());
	}

	public void testMatch3() {
		final String markup = "\n\n\nbc..\nfoo";
		ValidationProblem problem = rule.findProblem(markup, 0, markup.length());
		System.out.println(problem);
		assertNotNull(problem);
		assertEquals(3, problem.getOffset());
	}

	public void testValidator() {
		String markup = "h1. Foo\n\nbc. bar\n\npre.\nsdf\n\nbc.\n\n";
		List<ValidationProblem> result = validator.validate(new NullProgressMonitor(), markup);
		assertEquals(2, result.size());
		System.out.println(result);
		assertEquals(18, result.get(0).getOffset());
		assertEquals(4, result.get(0).getLength());
		assertEquals(28, result.get(1).getOffset());
		assertEquals(3, result.get(1).getLength());
	}
}
