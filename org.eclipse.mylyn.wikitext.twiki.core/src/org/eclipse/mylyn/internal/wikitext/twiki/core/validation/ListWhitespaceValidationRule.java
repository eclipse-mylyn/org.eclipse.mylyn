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
package org.eclipse.mylyn.internal.wikitext.twiki.core.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.validation.ValidationProblem;
import org.eclipse.mylyn.wikitext.core.validation.ValidationRule;
import org.eclipse.mylyn.wikitext.core.validation.ValidationProblem.Severity;

/**
 * TWiki lists markup must have spaces in multiples of 3 preceding the markup (*,i,I,1,a,A)
 * 
 * @author David Green
 */
public class ListWhitespaceValidationRule extends ValidationRule {

	private static final Pattern almostListPattern = Pattern.compile("^((?: |\t)*)(\\*|((i|I|a|A|1)\\.))(\\S)?",
			Pattern.MULTILINE);

	public ListWhitespaceValidationRule() {
	}

	@Override
	public ValidationProblem findProblem(String markup, int offset, int length) {
		Matcher matcher = almostListPattern.matcher(markup);
		if (offset > 0 || length != markup.length()) {
			matcher.region(offset, offset + length);
		}
		while (matcher.find()) {
			String spaces = matcher.group(1);
			if (spaces == null || spaces.length() == 0 || (spaces.length() % 3) != 0 || containsNonSpace(spaces)) {
				int problemOffset = matcher.start();
				int problemLength = Math.max(2, matcher.end(2) - problemOffset);
				return new ValidationProblem(Severity.WARNING,
						"Lists must be indented with spaces in multiples of three", problemOffset, problemLength);
			}
			String after = matcher.group(5);
			if (after != null) {
				int problemOffset = matcher.start();
				int problemLength = Math.max(2, matcher.end(2) - problemOffset);
				return new ValidationProblem(Severity.WARNING, "List item markup must be followed by whitespace",
						problemOffset, problemLength);
			}
		}
		return null;
	}

	private boolean containsNonSpace(String spaces) {
		for (int x = 0; x < spaces.length(); ++x) {
			if (spaces.charAt(x) != ' ') {
				return true;
			}
		}
		return false;
	}
}
