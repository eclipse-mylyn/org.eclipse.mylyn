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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class UrlUtil {

	static char[] ALLOWED_IN_FRAGMENT;
	static {
		ALLOWED_IN_FRAGMENT = """
				/?:@-.~!$&'()*+,;=\
				_\
				abcdefghijklmnopqrstuvwxyz\
				ABCDEFGHIJKLMNOPQRSTUVWXYZ\
				0123456789""".toCharArray(); //$NON-NLS-1$
		Arrays.sort(ALLOWED_IN_FRAGMENT);
	}

	public static String escapeUrlFormParameters(String text) {
		return URLEncoder.encode(text, StandardCharsets.UTF_8).replace("+", "%20"); //$NON-NLS-1$//$NON-NLS-2$
	}

	public static String escapeUrlFragment(String text) {
		int pos = -1;
		for (int i=0, l=text.length(); i<l; i++) {
			if (Arrays.binarySearch(ALLOWED_IN_FRAGMENT, text.charAt(i)) < 0) {
				pos = i;
				break;
			}
		}
		if (pos == -1) {
			return text;
		}
		String head = text.substring(0, pos); // avoid bogus encoding of head containing only legal chars
		String tail = text.substring(pos);
		// TODO: the following call will still escape some chars in ALLOWED_IN_FRAGMENT
		tail = URLEncoder.encode(tail, StandardCharsets.UTF_8).replace("+", "%20"); //$NON-NLS-1$ //$NON-NLS-2$
		return head+tail;
	}

}
