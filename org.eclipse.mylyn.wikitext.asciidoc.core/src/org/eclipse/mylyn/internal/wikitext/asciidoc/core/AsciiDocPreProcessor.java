/*******************************************************************************
 * Copyright (c) 2015 Max Rydahl Andersen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Max Rydahl Andersen- initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.asciidoc.core;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.util.LocationTrackingReader;

/**
 * Preprocessor for Asciidoc. Picks up :val: attribute definitions and replace them when {val} occurs.
 *
 * @author Max Rydahl Andersen
 */
public class AsciiDocPreProcessor {

	static final private Pattern ATTRIBUTE_PATTERN = Pattern.compile("^:(.*?):(.*)"); //$NON-NLS-1$

	// TODO: handle escape
	static final private Pattern ATTRIBUTE_TOKEN_PATTERN = Pattern.compile("(?:\\{(.*?)\\})"); //$NON-NLS-1$

	public String process(String markupContent) {
		Map<String, String> attributes = new HashMap<String, String>();

		StringBuilder processedMarkup = new StringBuilder(markupContent.length());

		LocationTrackingReader reader = new LocationTrackingReader(new StringReader(markupContent));

		String line;

		try {
			line = reader.readLine();
			while (line != null) {

				Matcher matcher = ATTRIBUTE_PATTERN.matcher(line);

				if (matcher.matches()) { // attribute definitions matches the
											// whole line.
					String key = matcher.group(1);
					String value = matcher.group(2);

					if (value != null) {
						value = value.trim();
					}
					attributes.put(key, value);
				} else {
					Matcher attributesRef = ATTRIBUTE_TOKEN_PATTERN.matcher(line);
					int offset = 0;
					while (attributesRef.find()) {
						String postfix = line.substring(offset, attributesRef.start());
						if (postfix.endsWith("\\")) { // attribute ref was quoted
							processedMarkup.append(postfix.substring(0, postfix.length() - 1));
							offset = attributesRef.start();
						} else {
							processedMarkup.append(postfix);
							String key = attributesRef.group(1);
							String value = attributes.get(key);

							if (value == null) {
								value = attributesRef.group(0);
							}
							processedMarkup.append(value);
							offset = attributesRef.end();
						}
					}
					if (offset < line.length()) {
						processedMarkup.append(line.substring(offset));
					}
					processedMarkup.append("\n");
				}

				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}

		return processedMarkup.toString();
	}

}
