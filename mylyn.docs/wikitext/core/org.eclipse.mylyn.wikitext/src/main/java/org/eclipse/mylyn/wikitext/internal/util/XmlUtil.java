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
package org.eclipse.mylyn.wikitext.internal.util;

public class XmlUtil {

	public static String getEscapedAttribute(String s) {
		StringBuilder result = new StringBuilder(s.length() + 10);
		for (int i = 0; i < s.length(); ++i) {
			appendEscapedChar(result, s.charAt(i), true);
		}
		return result.toString();
	}

	public static String getEscapedContent(String s) {
		StringBuilder result = new StringBuilder(s.length() + 10);
		for (int i = 0; i < s.length(); ++i) {
			appendEscapedChar(result, s.charAt(i), false);
		}
		return result.toString();
	}

	private static void appendEscapedChar(StringBuilder buffer, char c, boolean forAttribute) {
		String replacement = getReplacement(c, forAttribute);
		if (replacement != null) {
			buffer.append('&');
			buffer.append(replacement);
			buffer.append(';');
		} else if (c >= 0x00 && c <= 0x1F && c != '\t' && c != '\n' && c != '\r') {
			buffer.append("\uFFFD"); //$NON-NLS-1$
		} else {
			buffer.append(c);
		}
	}

	private static String getReplacement(char c, boolean forAttribute) {
		// Encode special XML characters into the equivalent character references.
		// These five are defined by default for all XML documents.
		switch (c) {
			case '<':
				return "lt"; //$NON-NLS-1$
			case '>':
				return "gt"; //$NON-NLS-1$
			case '&':
				return "amp"; //$NON-NLS-1$
		}
		if (forAttribute) {
			switch (c) {
				case '"':
					return "quot"; //$NON-NLS-1$
				case '\'':
					return "apos"; //$NON-NLS-1$
				case '\t':
					return "#x9"; //$NON-NLS-1$
				case '\n':
					return "#xA"; //$NON-NLS-1$
				case '\r':
					return "#xD"; //$NON-NLS-1$
			}
		}
		return null;
	}
}
