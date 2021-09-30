/*******************************************************************************
 * Copyright (c) 2015, 2021 David Green.
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

package org.eclipse.mylyn.wikitext.commonmark.internal;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class LinePredicates {

	public static Predicate<Line> empty() {
		return new Predicate<Line>() {

			@Override
			public String toString() {
				return "empty(line)";
			}

			@Override
			public boolean test(Line input) {
				return input != null && input.isEmpty();
			}
		};
	}

	public static Predicate<Line> matches(final Pattern pattern) {
		return new Predicate<Line>() {

			@Override
			public String toString() {
				return "matches(" + pattern.pattern() + ")";
			}

			@Override
			public boolean test(Line input) {
				return input != null && pattern.matcher(input.getText()).matches();
			}
		};
	}

	private LinePredicates() {
		// prevent instantiation
	}
}
