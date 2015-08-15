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

package org.eclipse.mylyn.internal.wikitext.commonmark.inlines;

import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;

class HtmlEntityReferenceToStringConverter {

	private static final Pattern pattern = Pattern.compile("&(#([0-9]+)|#x([a-f0-9]+));", Pattern.CASE_INSENSITIVE);

	static String toString(String entityReference) {
		StringWriter out = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(out);
		builder.setEmitAsDocument(false);
		builder.setFilterEntityReferences(true);
		builder.entityReference(entityReference);
		builder.flush();
		String textWithEntityReferences = out.toString();

		String string = "";
		Matcher matcher = pattern.matcher(textWithEntityReferences);
		while (matcher.find()) {
			String decimalNumber = matcher.group(2);
			String hexadecimalNumber = matcher.group(3);
			int value = 0;
			try {
				if (decimalNumber != null) {
					value = Integer.parseInt(decimalNumber);
				} else if (hexadecimalNumber != null) {
					value = Integer.parseInt(hexadecimalNumber, 16);
				}
			} catch (NumberFormatException e) {
				// fail silently
			}
			if (value > 0) {
				string += ((char) value);
			}
		}
		return string;
	}

}
