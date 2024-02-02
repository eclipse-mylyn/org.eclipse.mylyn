/*******************************************************************************
 * Copyright (c) 2007, 2015 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.twiki.internal.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.validation.ValidationProblem;
import org.eclipse.mylyn.wikitext.validation.ValidationProblem.Severity;
import org.eclipse.mylyn.wikitext.validation.ValidationRule;

/**
 * TWiki lists markup must have spaces in multiples of 3 preceding the markup (*,i,I,1,a,A)
 * 
 * @author David Green
 */
public class ListWhitespaceValidationRule extends ValidationRule {

	private static final Pattern almostListPattern = Pattern.compile("^((?: |\t)*)(\\*|((i|I|a|A|1)\\.))(\\S)?", //$NON-NLS-1$
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
			if (spaces == null || spaces.length() == 0 || spaces.length() % 3 != 0 || containsNonSpace(spaces)) {
				int problemOffset = matcher.start();
				int problemLength = Math.max(2, matcher.end(2) - problemOffset);
				return new ValidationProblem(Severity.WARNING, Messages.getString("ListWhitespaceValidationRule.1"), //$NON-NLS-1$
						problemOffset, problemLength);
			}
			String after = matcher.group(5);
			if (after != null) {
				int problemOffset = matcher.start();
				int problemLength = Math.max(2, matcher.end(2) - problemOffset);
				return new ValidationProblem(Severity.WARNING, Messages.getString("ListWhitespaceValidationRule.2"), //$NON-NLS-1$
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
