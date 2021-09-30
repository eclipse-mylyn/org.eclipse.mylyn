/*******************************************************************************
 * Copyright (c) 2007, 2013 David Green and others.
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
package org.eclipse.mylyn.wikitext.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Validates markup given a set of rules
 *
 * @author David Green
 * @see StandaloneMarkupValidator
 * @since 3.0
 */
public class MarkupValidator {

	private final List<ValidationRule> rules = new ArrayList<>();

	/**
	 * @param markup
	 *            the content to validate
	 * @return the list of validation problems, or an empty list if there are none
	 */
	public List<ValidationProblem> validate(String markup) {
		return validate(markup, 0, markup.length());
	}

	/**
	 * @param offset
	 *            the 0-based index at which validation should begin
	 * @param length
	 *            the length of the content to validate
	 * @param markup
	 *            the content to validate
	 * @return the list of validation problems, or an empty list if there are none
	 */
	public List<ValidationProblem> validate(String markup, int offset, int length) {
		if (length == 0 || rules.isEmpty()) {
			return Collections.emptyList();
		}

		List<ValidationProblem> problems = new ArrayList<>();

		for (ValidationRule rule : rules) {
			problems.addAll(rule.findProblems(markup, offset, length));
		}
		if (!problems.isEmpty()) {
			Collections.sort(problems);
		}
		return problems;
	}

	public List<ValidationRule> getRules() {
		return rules;
	}

}
