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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.validation.ValidationProblem;
import org.eclipse.mylyn.wikitext.core.validation.ValidationRule;

/**
 * 
 * 
 * @author David Green
 */
public class BlockWhitespaceRule extends ValidationRule {

	private static final Pattern pattern = Pattern.compile("((?:bc|bq|pre|table|p)(?:\\.){1,2})(.)?", Pattern.MULTILINE);

	@Override
	public ValidationProblem findProblem(String markup, int offset, int length) {
		Matcher matcher = pattern.matcher(markup);
		if (offset > 0) {
			matcher.region(offset, offset + length);
		}
		while (matcher.find()) {
			int start = matcher.start();
			boolean startOfLine = false;
			if (start == 0) {
				startOfLine = true;
			} else {
				char c = markup.charAt(start - 1);
				if (c == '\r' || c == '\n') {
					startOfLine = true;
				}
			}
			if (startOfLine) {
				String followingCharacter = matcher.group(2);
				if (followingCharacter == null || !followingCharacter.equals(" ")) {
					int problemLength = matcher.end(1) - start;
					String matched = matcher.group(1);
					return new ValidationProblem(ValidationProblem.Severity.WARNING,
							String.format(
									"'%s' will not start a new block unless it is followed by a space character (' ')",
									matched), start, problemLength);
				}
			}
		}
		return null;
	}

}
