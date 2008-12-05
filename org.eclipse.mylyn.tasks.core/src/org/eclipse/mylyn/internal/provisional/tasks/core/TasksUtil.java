/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.provisional.tasks.core;

/**
 * @author Steffen Pingel
 */
public class TasksUtil {

	public static String decode(String text) {
		boolean escaped = false;
		StringBuffer sb = new StringBuffer(text.length());
		StringBuffer escapedText = new StringBuffer(4);
		char[] chars = text.toCharArray();
		for (char c : chars) {
			if (c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '.') {
				if (escaped) {
					escapedText.append(c);
				} else {
					sb.append(c);
				}
			} else if (c == '%') {
				if (escaped) {
					throw new IllegalArgumentException("Unexpected '%' sign in '" + text + "'");
				}
				escaped = !escaped;
			} else if (c == '_') {
				if (!escaped) {
					throw new IllegalArgumentException("Unexpected '_' sign in '" + text + "'");
				}
				try {
					sb.append((char) Integer.parseInt(escapedText.toString(), 16));
					escapedText.setLength(0);
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException("Invalid escape code in '" + text + "'");
				}
				escaped = !escaped;
			}
		}
		return sb.toString();
	}

	public static String encode(String text) {
		StringBuffer sb = new StringBuffer(text.length());
		char[] chars = text.toCharArray();
		for (char c : chars) {
			if (c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '.') {
				sb.append(c);
			} else {
				sb.append("%" + Integer.toHexString(c).toUpperCase() + "_");
			}
		}
		return sb.toString();
	}

}
