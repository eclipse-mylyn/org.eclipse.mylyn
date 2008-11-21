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

/**
 * A validation rule.
 * 
 * Validation rules must not be stateful, as they may be used concurrently on different threads.
 * 
 * @author David Green
 */
public abstract class ValidationRule {

	/**
	 * Starting at the given offset find the next validation problem.
	 * 
	 * @param markup
	 *            the markup content in which a validation problem should be found
	 * @param offset
	 *            the offset at which to start looking for problems
	 * @param length
	 *            the length at which to stop looking for problems
	 * 
	 * @return the validation problem if found, or null if no validation problem was detected
	 */
	public abstract ValidationProblem findProblem(String markup, int offset, int length);

	/**
	 * Find all validation problems that exist starting at the given offset
	 * 
	 * @param markup
	 *            the markup content in which a validation problem should be found
	 * @param offset
	 *            the offset at which to start looking for problems
	 * @param length
	 *            the length at which to stop looking for problems
	 * 
	 * @return the problems, or an empty list if there are none
	 */
	public List<ValidationProblem> findProblems(String markup, int offset, int length) {
		if (length == 0) {
			return Collections.emptyList();
		}
		int end = offset + length;
		if (end > markup.length()) {
			end = markup.length();
		}
		List<ValidationProblem> problems = new ArrayList<ValidationProblem>();

		int o = offset;
		while (o < end) {
			ValidationProblem problem = findProblem(markup, o, length - (o - offset));
			if (problem == null) {
				break;
			}
			problems.add(problem);
			int newO = problem.getOffset() + problem.getLength();
			if (newO <= o) {
				break;
			}
			o = newO;
		}

		return problems;
	}

}
