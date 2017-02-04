/*******************************************************************************
 * Copyright (c) 2015 David Green.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.commonmark.internal;

import java.util.regex.Pattern;

import com.google.common.base.Predicate;

public class LinePredicates {

	public static Predicate<Line> empty() {
		return new Predicate<Line>() {

			@Override
			public String toString() {
				return "empty(line)";
			}

			@Override
			public boolean apply(Line input) {
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
			public boolean apply(Line input) {
				return input != null && pattern.matcher(input.getText()).matches();
			}
		};
	}

	private LinePredicates() {
		// prevent instantiation
	}
}
