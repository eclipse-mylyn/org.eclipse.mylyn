/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.core.util;

/**
 * @author Ken Sueda
 */
public class XmlStringConverter {

	public static String convertToXmlString(String s) {
		if (s == null)
			return "";
		StringBuffer res = new StringBuffer(s.length() + 20);
		for (int i = 0; i < s.length(); ++i)
			appendEscapedChar(res, s.charAt(i));
		return res.toString();
	}

	private static void appendEscapedChar(StringBuffer buffer, char c) {
		String replacement = getReplacementForSymbol(c);
		if (replacement != null) {
			buffer.append('&');
			buffer.append(replacement);
			buffer.append(';');
		} else {
			buffer.append(c);
		}
	}

	private static String getReplacementForSymbol(char c) {
		switch (c) {
		case '<':
			return "lt"; //$NON-NLS-1$
		case '>':
			return "gt"; //$NON-NLS-1$
		case '"':
			return "quot"; //$NON-NLS-1$
		case '\'':
			return "apos"; //$NON-NLS-1$
		case '&':
			return "amp"; //$NON-NLS-1$
		case '\r':
			return "#x0D"; //$NON-NLS-1$
		case '\n':
			return "#x0A"; //$NON-NLS-1$
		case '\u0009':
			return "#x09"; //$NON-NLS-1$
		}
		return null;
	}

	public static String convertXmlToString(String string) {
		StringBuffer result = new StringBuffer(string.length() + 10);
		for (int i = 0; i < string.length(); ++i) {
			char xChar = string.charAt(i);
			if (xChar == '&') {
				i++;
				StringBuffer escapeChar = new StringBuffer(10);
				boolean flag = true;
				while (flag) {
					xChar = string.charAt(i++);
					if (xChar == ';') {
						flag = false;
						i--;
					} else {
						escapeChar.append(xChar);
					}
				}
				result.append(getReplacementForXml(escapeChar.toString()));
			} else {
				result.append(xChar);
			}
		}
		return result.toString();
	}

	private static char getReplacementForXml(String s) {
		if (s.equals("lt")) {
			return '<';
		} else if (s.equals("gt")) {
			return '>';
		} else if (s.equals("quot")) {
			return '"';
		} else if (s.equals("apos")) {
			return '\'';
		} else if (s.equals("amp")) {
			return '&';
		} else if (s.equals("#x0D")) {
			return '\r';
		} else if (s.equals("#x0A")) {
			return '\n';
		} else if (s.equals("#x09")) {
			return '\u0009';
		}
		return 0;
	}

}
