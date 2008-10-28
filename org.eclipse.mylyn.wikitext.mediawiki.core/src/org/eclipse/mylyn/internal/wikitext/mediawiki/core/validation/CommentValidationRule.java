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

package org.eclipse.mylyn.internal.wikitext.mediawiki.core.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.validation.ValidationProblem;
import org.eclipse.mylyn.wikitext.core.validation.ValidationRule;
import org.eclipse.mylyn.wikitext.core.validation.ValidationProblem.Severity;

/**
 * a validation rule that tests for malformed comment delimiters.
 * 
 * @author David Green
 */
public class CommentValidationRule extends ValidationRule {

	private static Pattern commentPattern = Pattern.compile("(<!-{3,}|-{3,}>)", Pattern.MULTILINE); //$NON-NLS-1$

	public CommentValidationRule() {
	}

	@Override
	public ValidationProblem findProblem(String markup, int offset, int length) {
		Matcher matcher = commentPattern.matcher(markup);
		if (offset > 0 || length != markup.length()) {
			matcher.region(offset, offset + length);
		}
		if (matcher.find()) {
			int problemOffset = matcher.start();
			int problemLength = Math.max(2, matcher.end() - problemOffset);
			return new ValidationProblem(Severity.WARNING,
					Messages.getString("CommentValidationRule.1"), //$NON-NLS-1$
					problemOffset, problemLength);
		}
		return null;
	}

}
