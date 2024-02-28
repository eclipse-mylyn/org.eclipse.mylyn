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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.validation.ValidationProblem;
import org.eclipse.mylyn.wikitext.validation.ValidationProblem.Severity;
import org.eclipse.mylyn.wikitext.validation.ValidationRule;

/**
 * a test markup validation rule that creates errors for text matching "ERROR" or "WARNING".
 *
 * @author David Green
 */
@SuppressWarnings("nls")
public class TestMarkupValidationRule extends ValidationRule {

	private static final Pattern pattern = Pattern.compile("(ERROR|WARNING)", Pattern.MULTILINE);

	@Override
	public ValidationProblem findProblem(String markup, int offset, int length) {
		Matcher matcher = pattern.matcher(markup);
		if (offset > 0 || length != markup.length()) {
			matcher.region(offset, offset + length);
		}
		if (matcher.find()) {
			String group = matcher.group(1);
			Severity severity;
			if ("ERROR".equals(group)) {
				severity = Severity.ERROR;
			} else {
				severity = Severity.WARNING;
			}
			return new ValidationProblem(severity, "test error", matcher.start(1), matcher.end(1) - matcher.start(1));
		}
		return null;
	}

}
