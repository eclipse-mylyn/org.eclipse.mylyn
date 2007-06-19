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

package org.eclipse.mylyn.web.core;

/**
 * @author Rob Elves
 */
public class XmlUtil {

	/**
	 * @param text
	 *            string to clean
	 * @return string with all non valid characters removed, if text is null
	 *         return null
	 */
	public static String cleanXmlString(String text) {
		if (text == null)
			return null;
		StringBuilder builder = new StringBuilder(text.length());
		for (int x = 0; x < text.length(); x++) {
			char ch = text.charAt(x);
			if (isValid(ch)) {
				builder.append(ch);
			}
		}
		return builder.toString();
	}

	/**
	 * Return true if character is a valid xml character
	 * 
	 * @see http://www.w3.org/TR/REC-xml/
	 */
	public static boolean isValid(char ch) {
		return (0x0A == ch || 0x0D == ch || 0x09 == ch) || (ch >= 0x20 && ch <= 0xD7FF)
				|| (ch >= 0xE000 && ch <= 0xFFFD) || (ch >= 0x10000 && ch <= 0x10FFFF);
	}

}
