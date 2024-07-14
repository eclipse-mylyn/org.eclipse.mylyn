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

import java.util.Arrays;


public class IDStrategies {

	enum ScanState {
		HEAD, WORD, HYPHEN
	}

	/**
	 * Common strategy:
	 * <ul>
	 * <li>replace non-identifier chars with a dash
	 * <li>collapse multiple dashes into a single dash
	 * <li>remove leading and trailing dash
	 * </ul>
	 *
	 * @param id
	 *            name expected to be in lower case already
	 * @return a dash-separated sequence of words made up of letters, digits and underscores
	 */
	public static String computeID(String id) {
		id = id.replaceAll("[^a-z0-9_-]", "-"); //$NON-NLS-1$//$NON-NLS-2$
		ScanState state = ScanState.HEAD;
		char[] collected = new char[id.length()];
		int j = 0;
		for (int i = 0; i < id.length(); i++) {
			char c = id.charAt(i);
			switch (state) {
				case HEAD: // skip as many hyphens as we can find
					if (c == '-') {
						continue;
					}
					state = ScanState.WORD;
					break;
				case WORD: // commit chars until the next hyphen
					if (c == '-') {
						state = ScanState.HYPHEN;
						continue; // don't yet commit the hyphen in case it is trailing
					}
					break;
				case HYPHEN:
					if (c != '-') {
						collected[j++] = '-'; // deferred commit of the hyphen
						state = ScanState.WORD;
					} else {
						continue; // skip additional hyphen
					}
			}
			collected[j++] = c;
		}
		id = String.valueOf(Arrays.copyOfRange(collected, 0, j));
		return id;
	}
}
