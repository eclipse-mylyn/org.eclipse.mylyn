/*******************************************************************************
 * Copyright (c) 2020 Fraunhofer FOKUS and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Max Bureck (Fraunhofer FOKUS) - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.markdown.internal.phrase;

import java.util.Set;

import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElementProcessor;

/**
 * Heuristic replacement for bare hyperlinks (e.g. http://www.eclipse.org) without &lt; and &gt; delimiters to links in the output. <br>
 * Links are detected when the prefix "http://" or "www." after punctuation, white space or a line beginneing are detected. Links starting
 * with "www." will be prefixed with "http://" in the actual link href. The heuristic is conservative in the way that it only captures URLs
 * containing only characters valid for URLs. However, it does not check the format and if characters with semantic meaning are only used in
 * valid positions. URLs with un-escaped characters are not detected. The reason in mostly complexity, since different parts of a URL have
 * to be escaped differently, and parsing the URL before escaping is complex.<br>
 * URLs end with either white space, line end, quotation mark " or a &lt; character. This avoids most URLs with invalid characters to turn
 * into broken links.<br>
 * <br>
 * The following rules regarding trailing characters are in place:
 * <ul>
 * <li>Trailing punctuation ({@code ?!\"*.:_-~}) is stripped, since it seems more likely these to not part of the URL.</li>
 * <li>Trailing closing parenthesis ')' are stripped, as long as the count of opening and closing parenthesis are not equal/balanced.</li>
 * <li>If the URL trail contains a sequence that resembles an HTML entity (pattern {@code &[a-zA-Z0-9];}) the detected sequence will be
 * removed.</li>
 * <li>If after stripping the URL prefix is simply "http://", "https://" or "www.", the sequence is not recognized as a URL at all.</li>
 * </ul>
 * This behavior is loosely aligned with the <a href="https://github.github.com/gfm/#extended-autolink-path-validation">GitHub flavored
 * Markdown autolink extension and GitHub's actual Markdown implementation.</a>.
 */
public class ExtendedAutomaticLinkReplacementToken extends PatternBasedElement {

	/**
	 * Regex based on characters mentioned in RFC-3986: https://www.ietf.org/rfc/rfc3986.txt Note that this Regex does not check for a
	 * completely valid HTTP link, it only checks for the {@code http(s)://} prefix and if the following characters are valid for URLs.
	 */
	private static final String AUTOMATIC_LINK_REGEX = "(?<=^|\\s|\\p{Punct})((https?://(?!/)|www\\.)[a-zA-Z0-9:/?#\\[\\]@!$&'\\(\\)\\*+,;=\\-\\._~%]+)(?=$|\"|\\s|<)"; //$NON-NLS-1$

	private static final Set<String> EMPTY_LINKS = Set.of("www.", "http://", "https://");

	@Override
	protected String getPattern(int groupOffset) {
		return AUTOMATIC_LINK_REGEX;
	}

	@Override
	protected int getPatternGroupCount() {
		return 2;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new PatternBasedElementProcessor() {
			@Override
			public void emit() {
				String href = group(1);
				int parensBalance = href.codePoints().map(c -> {
					return switch (c) {
						case '(' -> -1;
						case ')' -> +1;
						default -> 0;
					};
				}).sum();
				// omit punctuation
				int endIndex = -1;
				charLoop: for (int i = href.length() - 1; i > 3; i--) {
					switch (href.charAt(i)) {
						case '?':
						case '!':
						case '\'':
						case '"':
						case '*':
						case '.':
						case ':':
						case '_':
						case '~':
							endIndex = i;
							break;
						case ')':
							if (parensBalance > 0) {
								parensBalance--;
							} else {
								break charLoop;
							}
						case ';':
							i = skipHtmlEntity(href, i);
							endIndex = i;
							break;
						default:
							break charLoop;
					}
				}

				String linkText;
				String linkHref;
				if (endIndex > -1) {
					linkText = href.substring(0, endIndex);
				} else {
					linkText = href;
				}
				if (linkText.startsWith("www.")) { //$NON-NLS-1$
					linkHref = "http://" + linkText; //$NON-NLS-1$
				} else {
					linkHref = linkText;
				}

				if (EMPTY_LINKS.contains(linkText)) {
					// do not convert "empty" links
					builder.characters(href);
				} else {
					builder.link(linkHref, linkText);
					// if characters were stripped, add them as regular text after link.
					if (endIndex > -1) {
						builder.characters(href.substring(endIndex));
					}
				}
			}

			private int skipHtmlEntity(String href, int endIndex) {
				for (int i = endIndex - 1; i > 3; i--) {
					char c = href.charAt(i);
					boolean isAlphaNum = inRange(c, 'a', 'z') || inRange(c, 'A', 'Z') || inRange(c, '0', '9');
					// if the character is not in [a-zA-Z0-9], don't skip anything
					if (c == '&') {
						return i;
					}
					if (!isAlphaNum) {
						return endIndex;
					}
				}
				// no & found, don't skip anything
				return endIndex;
			}

			private boolean inRange(char toCheck, char lowerBound, char upperBound) {
				return toCheck >= lowerBound && toCheck <= upperBound;
			}
		};
	}

}
