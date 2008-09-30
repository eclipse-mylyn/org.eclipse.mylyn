/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.twiki.core.token;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.internal.wikitext.twiki.core.TWikiLanguage;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElementProcessor;

/**
 * Tokens that represent links, as follows: <code>[[link]]</code>
 *  
 * @author David Green
 */
public class LinkReplacementToken extends PatternBasedElement {

	private static final Pattern replacementPattern = Pattern.compile("\\W");
	private static final Pattern wordBoundaryPattern = Pattern.compile("\\W\\w");
	
	@Override
	protected String getPattern(int groupOffset) {
		return "(!)?(\\[\\[([^\\]]+)(?:(\\]\\[)(.*))?\\]\\])";
	}

	@Override
	protected int getPatternGroupCount() {
		return 5;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new LinkProcessor();
	}

	private static class LinkProcessor extends PatternBasedElementProcessor {
		@Override
		public void emit() {
			String escaped = group(1);
			if (escaped != null) {
				String escapedText = group(2);
				builder.characters(escapedText);
			} else {
				String link = group(3);
				String text = group(5);
				if (text == null || text.trim().length() == 0) {
					text = link;
				}
				boolean looksLikeEmail = link.indexOf('@') != -1;
				if (link.indexOf('/') != -1 || link.indexOf('#') != -1 || looksLikeEmail) {
					if (looksLikeEmail) {
						text = text.replaceFirst("\\s*mailto:","");
					}
					// url link
					builder.link(link, text);
				} else {
					// wiki link
					link = camelCaseWordBoundaries(link);
					String target = replacementPattern.matcher(link).replaceAll("");
					TWikiLanguage twikiLanguage = (TWikiLanguage)markupLanguage;
					boolean exists = twikiLanguage.computeInternalLinkExists(target);
					
					String internalHref = twikiLanguage.toInternalHref(target);
					if (!exists) {
						builder.characters(text);
						builder.link(internalHref, "?");
					} else {
						builder.link(internalHref, text);
					}
				}
			}
		}

		private String camelCaseWordBoundaries(String text) {
			Matcher matcher = wordBoundaryPattern.matcher(text);
			String newText = Character.toString(Character.toUpperCase(text.charAt(0)));
			int start = 1;
			while (matcher.find()) {
				int offset = matcher.start();
				newText += text.substring(start,offset);
				
				newText += Character.toUpperCase(text.charAt(offset+1));
				
				start = offset + 2;
			}
			if (start < text.length()) {
				newText += text.substring(start);
			}
			return newText;
		}
	}


}
