/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.core.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.internal.wikitext.core.validation.StandaloneMarkupValidator;

/**
 * Validate markup given a set of rules
 * 
 * @author David Green
 * 
 * @see StandaloneMarkupValidator
 * @since 1.0
 */
public class MarkupValidator {

	private final List<ValidationRule> rules = new ArrayList<ValidationRule>();

	public List<ValidationProblem> validate(IProgressMonitor monitor, String markup) {
		return validate(monitor, markup, 0, markup.length());
	}

	public List<ValidationProblem> validate(IProgressMonitor monitor, String markup, int offset, int length) {
		final int totalWork = length == 0 || rules.isEmpty() ? 1 : rules.size();
		monitor.beginTask(Messages.getString("MarkupValidator.0"), totalWork); //$NON-NLS-1$
		try {
			if (length == 0 || rules.isEmpty()) {
				return Collections.emptyList();
			}
			int end = offset + length;
			if (end > markup.length()) {
				end = markup.length();
			}
			List<ValidationProblem> problems = new ArrayList<ValidationProblem>();

			for (ValidationRule rule : rules) {
				problems.addAll(rule.findProblems(markup, offset, length));
				monitor.worked(1);
			}
			if (!problems.isEmpty()) {
				Collections.sort(problems);
			}
			return problems;
		} finally {
			monitor.done();
		}
	}

	public List<ValidationRule> getRules() {
		return rules;
	}

}
