/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.core;

import java.io.IOException;
import java.io.StringReader;

import org.eclipse.mylyn.internal.provisional.commons.core.Html2TextReader;

/**
 * @author Steffen Pingel
 * @since 3.5
 */
public class HtmlUtil {

	/**
	 * Strips HTML tags from a text.
	 * 
	 * @param htmlText
	 *            a string that contains HTML tags
	 * @return htmlText converted to plain text
	 * @throws IOException
	 *             thrown if a parsing error occurs
	 */
	public static String toText(String htmlText) throws IOException {
		Html2TextReader reader = new Html2TextReader(new StringReader(htmlText));
		int c;
		StringBuffer sb = new StringBuffer(htmlText.length());
		while ((c = reader.read()) != -1) {
			sb.append((char) c);
		}
		return sb.toString();
	}

}
