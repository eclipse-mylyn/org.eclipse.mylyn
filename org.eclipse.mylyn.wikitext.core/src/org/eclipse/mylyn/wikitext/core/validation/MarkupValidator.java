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
package org.eclipse.mylyn.wikitext.core.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * validate markup given a set of rules
 * 
 * @author David Green
 */
public class MarkupValidator {

	private final List<ValidationRule> rules = new ArrayList<ValidationRule>();

	public List<ValidationProblem> validate(IProgressMonitor monitor, String markup) {
		return validate(monitor, markup, 0, markup.length());
	}

	public List<ValidationProblem> validate(IProgressMonitor monitor, String markup, int offset, int length) {
		final int totalWork = length == 0 ? 1 : length * rules.size();
		monitor.beginTask("Markup Validation", totalWork);
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
				int o = offset;
				while (o < end) {
					ValidationProblem problem = rule.findProblem(markup, o, length - (o - offset));
					if (problem == null) {
						break;
					}
					problems.add(problem);
					int newO = problem.getOffset() + problem.getLength();
					if (newO <= o) {
						break;
					}
					monitor.worked(newO - o);
					o = newO;
				}
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
