/*******************************************************************************
 * Copyright (c) 2024 GK Software SE, and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *      See git history
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.util;

public class Strings {

	public static boolean isNullOrEmpty(String str) {
		if (str == null) {
			return true;
		}
		return str.isEmpty();
	}

	public static boolean isBlank(String str) {
		return firstNonSpace(str, true) == -1;
	}

	public static int firstNonSpace(String str, boolean anyWhitespace) {
		for (int i = 0, l = str.length(); i < l; i++) {
			char c = str.charAt(i);
			if (anyWhitespace ? !Character.isWhitespace(c) : c != ' ') {
				return i;
			}
		}
		return -1;
	}

	public static String stringOrEmpty(String string) {
		if (string != null) {
			return string;
		}
		return ""; //$NON-NLS-1$
	}
}
