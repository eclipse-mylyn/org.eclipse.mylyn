/*******************************************************************************
 * Copyright (c) 2020, 2024 Fraunhofer FOKUS and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Max Bureck (Fraunhofer FOKUS) - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.markdown.tests;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.mylyn.wikitext.markdown.MarkdownLanguage;
import org.eclipse.mylyn.wikitext.toolkit.AbstractMarkupGenerationTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
@SuppressWarnings("nls")
public class MarkdownLanguageExtendedAutomaticLinkReplacementTest
extends AbstractMarkupGenerationTest<MarkdownLanguage> {

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] { { "http://" }, { "https://" }, { "" } });
	}

	private final String urlPrefix;

	private final String hrefPrefix;

	public MarkdownLanguageExtendedAutomaticLinkReplacementTest(String prefix) {
		urlPrefix = prefix;
		if (prefix.equals("")) {
			hrefPrefix = "http://";
		} else {
			hrefPrefix = prefix;
		}
	}

	@Test
	public void testOnlyLink() {
		String markdown = urlPrefix + "www.eclipse.org:80/p2%20update/!+*,';$[foo]/(bar)/~/_emf_/-?bar=baz&oomph#foo";
		// note that in xhmtl attribute values are escaped, therefore
		// ' is escaped as &apos; and & is escaped as &amp;
		String expected = "<p><a href=\"" + hrefPrefix
				+ "www.eclipse.org:80/p2%20update/!+*,&apos;;$[foo]/(bar)/~/_emf_/-?bar=baz&amp;oomph#foo\">"
				+ urlPrefix
				+ "www.eclipse.org:80/p2%20update/!+*,';$[foo]/(bar)/~/_emf_/-?bar=baz&amp;oomph#foo</a></p>";
		assertMarkup(expected, markdown);
	}

	@Test
	public void testLinkWithBalancedParens() {
		String markdown = urlPrefix + "www.eclipse.org/()((fo(o(bar()g)ee)))";
		String expectedOutput = "<p><a href=\"" + hrefPrefix + "www.eclipse.org/()((fo(o(bar()g)ee)))\">" + urlPrefix
				+ "www.eclipse.org/()((fo(o(bar()g)ee)))</a></p>";
		assertMarkup(expectedOutput, markdown);
	}

	@Test
	public void testLinkWithUnbalancedParens() {
		String markdown = urlPrefix + "www.eclipse.org/)((foo(bar()g)ee)))";
		String expectedOutput = "<p><a href=\"" + hrefPrefix + "www.eclipse.org/)((foo(bar()g)ee)\">" + urlPrefix
				+ "www.eclipse.org/)((foo(bar()g)ee)</a>))</p>";
		assertMarkup(expectedOutput, markdown);
	}

	@Test
	public void testLinkWithUnbalancedParensAndPunctuation() {
		String markdown = urlPrefix + "www.eclipse.org/)((foo(bar()g)ee.)!*);)";
		String expectedOutput = "<p><a href=\"" + hrefPrefix + "www.eclipse.org/)((foo(bar()g)ee.)\">" + urlPrefix
				+ "www.eclipse.org/)((foo(bar()g)ee.)</a>!*);)</p>";
		assertMarkup(expectedOutput, markdown);
	}

	@Test
	public void testLinkWithInvalidCharacter() {
		String markdown = urlPrefix + "www.eclipse.örg";
		String expectedOutput = "<p>" + urlPrefix + "www.eclipse.örg</p>";
		assertMarkup(expectedOutput, markdown);
	}

	@Test
	public void testEmptyLinkAfterStrip() {
		String prefix = urlPrefix.isEmpty() ? "www." : urlPrefix;
		String markdown = prefix + "?.~";
		String expectedOutput = "<p>" + markdown + "</p>";
		assertMarkup(expectedOutput, markdown);
	}

	@Test
	public void testLinkAtBeginning() {
		String markdown = urlPrefix + "www.eclipse.org foo bar";
		String expectedOutput = "<p><a href=\"" + hrefPrefix + "www.eclipse.org\">" + urlPrefix
				+ "www.eclipse.org</a> foo bar</p>";
		assertMarkup(expectedOutput, markdown);
	}

	@Test
	public void testInvalidHtmlEntity() {
		String markdown = urlPrefix + "www.eclipse.org&@mp;";
		String expectedOutput = "<p><a href=\"" + hrefPrefix + "www.eclipse.org&amp;@mp\">" + urlPrefix
				+ "www.eclipse.org&amp;@mp</a>;</p>";
		assertMarkup(expectedOutput, markdown);
	}

	@Test
	public void testHtmlEntityDetection() {
		String markdown = urlPrefix + "www.eclipse.org&amp;";
		String expectedOutput = "<p><a href=\"" + hrefPrefix + "www.eclipse.org\">" + urlPrefix
				+ "www.eclipse.org</a>&amp;amp;</p>";
		assertMarkup(expectedOutput, markdown);
	}

	@Test
	public void testLinkEndOfQuote() {
		String markdown = "foo " + urlPrefix + "www.eclipse.org/downloads\" bar";
		String expectedOutput = "<p>foo <a href=\"" + hrefPrefix + "www.eclipse.org/downloads\">" + urlPrefix
				+ "www.eclipse.org/downloads</a>\" bar</p>";
		assertMarkup(expectedOutput, markdown);
	}

	@Test
	public void testLinkEndWithMultiplePunctuationChars() {
		String markdown = "foo " + urlPrefix + "www.eclipse.org/downloads\"~. bar";
		String expectedOutput = "<p>foo <a href=\"" + hrefPrefix + "www.eclipse.org/downloads\">" + urlPrefix
				+ "www.eclipse.org/downloads</a>\"~. bar</p>";
		assertMarkup(expectedOutput, markdown);
	}

	@Test
	public void testLinkInText() {
		String markdown = "foo " + urlPrefix + "www.eclipse.org bar";
		String expectedOutput = "<p>foo <a href=\"" + hrefPrefix + "www.eclipse.org\">" + urlPrefix
				+ "www.eclipse.org</a> bar</p>";
		assertMarkup(expectedOutput, markdown);
	}

	@Test
	public void testLinkInList() {
		String markdown = "\n  - " + urlPrefix + "www.eclipse.org\n";
		String expectedOutput = "<ul><li><a href=\"" + hrefPrefix + "www.eclipse.org\">" + urlPrefix
				+ "www.eclipse.org</a></li></ul>";
		assertMarkup(expectedOutput, markdown);
	}

	@Test
	public void testLinkAtEnd() {
		String markdown = "foo bar " + urlPrefix + "www.eclipse.org";
		String expectedOutput = "<p>foo bar <a href=\"" + hrefPrefix + "www.eclipse.org\">" + urlPrefix
				+ "www.eclipse.org</a></p>";
		assertMarkup(expectedOutput, markdown);
	}

	@Override
	protected MarkdownLanguage createMarkupLanguage() {
		return new MarkdownLanguage(true);
	}

}
